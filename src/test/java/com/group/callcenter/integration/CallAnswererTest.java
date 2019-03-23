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

	@Test
	public void several_calls_must_be_answered_in_parallel() throws Exception {
		//GIVEN
		int parallelExecutions = 10;
		int callsSize = 10;
		ExecutorService executorService = Executors.newFixedThreadPool(parallelExecutions * callsSize);
		DefaultCallAnswerer callAnswerer = new DefaultCallAnswerer(executorService);

		//WHEN
		for (int i = 0; i < parallelExecutions; i++) {
			CompletableFuture.runAsync(() -> {
				List<MockedTimeDurationCall> calls = callMother.aRandomDurationCallList(callsSize);
				for (MockedTimeDurationCall call : calls) {
					callAnswerer.answerCall(call);
				}
			});
		}
		//THEN
		TimeUnit.SECONDS.sleep(11);
		assertThat(callAnswerer.getOnGoingCalls()).isEqualTo(0);
	}
}

class MockedTimeDurationCall implements Call {
	private Random random = new Random();
	private final int duration;

	public MockedTimeDurationCall(int minDurationInSeconds, int maxDurationInSeconds) {
		this.duration = getDuration(minDurationInSeconds, maxDurationInSeconds);
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

	private int getDuration(int minDurationInSeconds, int maxDurationInSeconds) {
		return random.nextInt((maxDurationInSeconds - minDurationInSeconds) + 1) + minDurationInSeconds;
	}
}
