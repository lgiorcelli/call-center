package com.group.callcenter.domain;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatcher {
	private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);
	private Consumer<Call> onDispatcherCapacityExceeded;
	private int currentOngoingCalls = 0;
	private int maxOngoingCalls;
	private CallCenter callCenter;
	private ExecutorService executorService;

	public Dispatcher(Consumer<Call> onDispatcherCapacityExceeded, int maxOngoingCalls, CallCenter callCenter, ExecutorService executorService,
			Consumer<Call> onNoEmployeeAvailable) {
		this.onDispatcherCapacityExceeded = onDispatcherCapacityExceeded;
		this.maxOngoingCalls = maxOngoingCalls;
		this.callCenter = callCenter;
		callCenter.setOnNoEmployeeAvailable(onNoEmployeeAvailable);
		callCenter.setOnCallFinished(call -> decreaseOnGoingCalls());
		this.executorService = executorService;
	}

	public void dispatchCall(Call call) {
		if (hasRemainingCapacity()) {
			handleCall(call);
		} else {
			onDispatcherCapacityExceeded.accept(call);
		}
	}

	public int getCurrentOngoingCalls() {
		return currentOngoingCalls;
	}

	private boolean hasRemainingCapacity() {
		return currentOngoingCalls < maxOngoingCalls;
	}

	private void handleCall(Call call) {
		incrementOngoingCalls();
		try {
			CompletableFuture //
					.runAsync(() -> {
						logger.info("Accepting call: " + call);
						callCenter.accept(call);}, executorService)
					.thenRun(this::decreaseOnGoingCalls);
		} catch (Exception e) {
			throw new RuntimeException("Error answering call", e);
		}
	}

	private synchronized int incrementOngoingCalls() {
		return currentOngoingCalls++;
	}

	private synchronized void decreaseOnGoingCalls() {
		currentOngoingCalls--;
	}
}
