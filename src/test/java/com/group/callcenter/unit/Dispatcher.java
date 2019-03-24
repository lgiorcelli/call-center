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

	public void dispatchCall(Call call) {
		if (hasRemainingCapacity()) {
			handleCall(call);
		} else {
			onDispatcherCapacityExceeded.accept(call);
		}
	}

	private boolean hasRemainingCapacity() {
		return currentOngoingCalls < maxOngoingCalls;
	}

	private void handleCall(Call call) {
		incrementOngoingCalls();
		callCenter.accept(call);
	}

	private void incrementOngoingCalls() {
		currentOngoingCalls++;
	}

	private void decreaseOnGoingCalls() {
		currentOngoingCalls--;
	}
}
