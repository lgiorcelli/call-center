package com.group.callcenter.integration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import com.group.callcenter.Call;
import com.group.callcenter.CallAnswerer;

class DefaultCallAnswerer implements CallAnswerer {

	private ExecutorService executorService;
	private int ongoingCalls = 0;

	DefaultCallAnswerer(ExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public boolean canAnswerCall() {
		return false;
	}

	@Override
	public boolean answerCall(Call call) {
		incrementOnGoingCalls();
		doAnswerCall(call);
		return false;
	}

	private void doAnswerCall(Call call) {
		try {
			CompletableFuture //
					.runAsync(call::link, executorService) //
					.thenRun(this::decrementOnGoingCalls) //
					.thenRun(() -> System.out.println("counter decremented to = " + ongoingCalls));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private synchronized void incrementOnGoingCalls() {
		ongoingCalls++;
	}

	private synchronized void decrementOnGoingCalls() {
		ongoingCalls--;
	}

	public int getOnGoingCalls() {
		return ongoingCalls;
	}
}