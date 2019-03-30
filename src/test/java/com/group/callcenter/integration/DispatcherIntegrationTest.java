package com.group.callcenter.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.group.callcenter.RealDurationCallMother;
import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.Dispatcher;
import com.group.callcenter.factory.DispatcherFactory;

public class DispatcherIntegrationTest {

	private static final int TEST_TIMEOUT = 6;
	private static final int PARALLEL_EXECUTIONS = 10;
	private static final int CALLS_LIST_SIZE = 12;

	private RealDurationCallMother callMother = new RealDurationCallMother();
	private Dispatcher dispatcher;
	private DispatcherFactory factory = new DispatcherFactory();


	@Test
	public void several_calls_must_be_answered_in_parallel() {
		givenAProductionReadyDispatcher();

		whenSeveralCallsArrivesInDifferentThreads();
		afterWaitForCompletion();

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
	}

	private void sendCallsInParallelThread() {
		for (int i = 0; i < PARALLEL_EXECUTIONS; i++) {
			int callGroup = i;
			CompletableFuture.runAsync(() ->this.sendCallGroup(callGroup));
		}
	}

	private void afterWaitForCompletion() {
		try {
			TimeUnit.SECONDS.sleep(TEST_TIMEOUT);
		} catch (InterruptedException e) {
			throw new RuntimeException("Error esperando a que termine el test", e);
		}
	}

	private void sendCallGroup(int groupId) {
		List<Call> calls = callMother.aRandomDurationCallList(CALLS_LIST_SIZE, groupId);
		for (Call call : calls) {
			dispatcher.dispatchCall(call);
		}
	}

}

