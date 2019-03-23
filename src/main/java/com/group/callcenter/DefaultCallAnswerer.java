package com.group.callcenter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.CallAnswerer;

public class DefaultCallAnswerer implements CallAnswerer {

	private ExecutorService executorService;
	private int ongoingCalls = 0;

	public DefaultCallAnswerer(ExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public boolean canAnswerCall() {
		return false;
	}

	@Override
	public void answer(Call call) {
		incrementOnGoingCalls();
		doAnswerCall(call);
	}

	private void doAnswerCall(Call call) {
		try {
			CompletableFuture //
					.runAsync(call::link, executorService) //
					.thenRun(this::decrementOnGoingCalls) //
					.thenRun(() -> System.out.println("counter decremented to = " + ongoingCalls));
		} catch (Exception e) {
			throw new RuntimeException("Error answering call", e);
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
