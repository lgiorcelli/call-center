package com.group.callcenter.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.group.callcenter.domain.Call;
import com.group.callcenter.unit.Dispatcher;

public class DispatcherIntegrationTest {

	private RealDurationCallMother callMother = new RealDurationCallMother();
	private static final int PARALLEL_EXECUTIONS = 10;
	private static final int CALLS_SIZE = 12;

	private Dispatcher dispatcher;
	private DispatcherFactory factory = new DispatcherFactory();


	@Test
	public void several_calls_must_be_answered_in_parallel() {
		givenAProductionReadyDispatcher();

		whenSeveralCallsArrivesInDifferentThreads();

		thenThereIsNoOnGoingCalls();
	}

	private void givenAProductionReadyDispatcher() {
		this.dispatcher = factory.create();
	}

	private void thenThereIsNoOnGoingCalls() {
		assertThat(dispatcher.getCurrentOngoingCalls()).isEqualTo(0);
	}

	private void whenSeveralCallsArrivesInDifferentThreads() {
		sendCallsInParallelThread();
		waitForCompletion();
	}

	private void sendCallsInParallelThread() {
		for (int i = 0; i < PARALLEL_EXECUTIONS; i++) {
			int callGroup = i;
			CompletableFuture.runAsync(() ->this.sendCallGroup(callGroup));
		}
	}

	private void waitForCompletion() {
		try {
			TimeUnit.SECONDS.sleep(6);
		} catch (InterruptedException e) {
			throw new RuntimeException("Error esperando a que termine el test", e);
		}
	}

	private void sendCallGroup(int groupId) {
		List<Call> calls = callMother.aRandomDurationCallList(CALLS_SIZE, groupId);
		for (Call call : calls) {
			dispatcher.dispatchCall(call);
		}
	}

}

