package com.group.callcenter.unit;

import java.util.function.Consumer;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.priority.CallCenter;

public class Dispatcher {
	private Consumer<Call> onDispatcherCapacityExceeded;
	private int currentOngoingCalls = 0;
	private int maxOngoingCalls;
	private CallCenter callCenter;

	public Dispatcher(Consumer<Call> onDispatcherCapacityExceeded, int maxOngoingCalls, CallCenter callCenter) {
		this.onDispatcherCapacityExceeded = onDispatcherCapacityExceeded;
		this.maxOngoingCalls = maxOngoingCalls;
		this.callCenter = callCenter;
	}

	public synchronized void dispatchCall(Call call) {
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

	private synchronized void handleCall(Call call) {
		int currentCalls = incrementOngoingCalls();
		System.out.println(String.format("Handling call %s. Ongoing calls: %d", call, currentCalls));
		callCenter.accept(call);
	}

	private synchronized int incrementOngoingCalls() {
		return currentOngoingCalls++;
	}

	public synchronized void decreaseOnGoingCalls() {
		currentOngoingCalls--;
	}
}
