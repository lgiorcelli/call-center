package com.group.callcenter.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.group.callcenter.Call;

public class CallAnswererTest {
	private RealDurationCallMother callMother = new RealDurationCallMother();
	private static final int PARALLEL_EXECUTIONS = 10;
	private static final int CALLS_SIZE = 10;

	private ExecutorService executorService;
	private DefaultCallAnswerer callAnswerer;

	@Test
	public void several_calls_must_be_answered_in_parallel() {
		givenAnExecutorWithCapacityForAllCalls();
		givenADefaultCallAnswer();

		whenSeveralCallsArrivesInDifferentThreads();

		thenThereIsNoOnGoingCalls();
	}

	private void thenThereIsNoOnGoingCalls() {
		assertThat(callAnswerer.getOnGoingCalls()).isEqualTo(0);
	}

	private void whenSeveralCallsArrivesInDifferentThreads() {
		sendCallsInParallelThread();
		waitForCompletion();
	}

	private void sendCallsInParallelThread() {
		for (int i = 0; i < PARALLEL_EXECUTIONS; i++) {
			CompletableFuture.runAsync(this::sendCallGroup);
		}
	}

	private void waitForCompletion() {
		try {
			TimeUnit.SECONDS.sleep(11);
		} catch (InterruptedException e) {
			throw new RuntimeException("Error esperando a que termine el test",e);
		}
	}

	private void sendCallGroup() {
		List<Call> calls = callMother.aRandomDurationCallList(CALLS_SIZE);
		for (Call call : calls) {
			callAnswerer.answerCall(call);
		}
	}

	private void givenADefaultCallAnswer() {
		callAnswerer = new DefaultCallAnswerer(executorService);
	}

	private void givenAnExecutorWithCapacityForAllCalls() {
		executorService = Executors.newFixedThreadPool(PARALLEL_EXECUTIONS * CALLS_SIZE);
	}
}

class MockedTimeDurationCall implements Call {
	private Random random = new Random();
	private final int duration;

	public MockedTimeDurationCall(int minDurationInSeconds, int maxDurationInSeconds) {
		this.duration = getRandomDuration(minDurationInSeconds, maxDurationInSeconds);
	}

	public void link() {
		sleep();
		System.out.println("Call Finished after " + duration + " seconds");
	}

	private void sleep() {
		try {
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			throw new RuntimeException("Error simulando espera", e);
		}
	}

	private int getRandomDuration(int minDurationInSeconds, int maxDurationInSeconds) {
		return random.nextInt((maxDurationInSeconds - minDurationInSeconds) + 1) + minDurationInSeconds;
	}
}
