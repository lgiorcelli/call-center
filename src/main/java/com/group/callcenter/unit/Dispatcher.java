package com.group.callcenter.unit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.priority.CallCenter;

public class Dispatcher {
	private final static Logger LOGGER = Logger.getLogger(Dispatcher.class.getName());
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
						LOGGER.info("Accepting call: " + call);
						callCenter.accept(call);}, executorService)
					.thenRun(this::decreaseOnGoingCalls);
		} catch (Exception e) {
			throw new RuntimeException("Error answering call", e);
		}
	}

	private synchronized int incrementOngoingCalls() {
		return currentOngoingCalls++;
	}

	public synchronized void decreaseOnGoingCalls() {
		currentOngoingCalls--;
	}
}
