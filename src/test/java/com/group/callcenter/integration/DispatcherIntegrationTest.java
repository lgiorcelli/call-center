package com.group.callcenter.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.CallAnswerer;
import com.group.callcenter.domain.priority.CallCenter;
import com.group.callcenter.unit.Dispatcher;

public class DispatcherIntegrationTest {

	private RealDurationCallMother callMother = new RealDurationCallMother();
	private static final int PARALLEL_EXECUTIONS = 10;
	private static final int CALLS_SIZE = 12;

	private ExecutorService executorService;

	private Dispatcher dispatcher;

	@Before
	public void setUp() {
		executorService = Executors.newFixedThreadPool(10);

		Consumer<Call> onDispatcherCapacityExceeded = call -> {
			System.out.println("Dispatcher rejects call: " + call);
		};
		CallAnswerer operators = new FixedCapacityCallAnswerer("operators", 0.3F);
		CallAnswerer supervisors = new FixedCapacityCallAnswerer("supervisors", 0.5F);
		CallAnswerer managers = new FixedCapacityCallAnswerer("managers", 0.9F);
		List<CallAnswerer> answerPriority = Lists.newArrayList(operators, supervisors, managers);

		CallCenter callCenter = new CallCenter(answerPriority, call -> dispatcher.decreaseOnGoingCalls());
		dispatcher = new Dispatcher(onDispatcherCapacityExceeded, 10, callCenter, executorService, call -> System.out.println("No employees availables for call " + call));
	}

	@Test
	public void several_calls_must_be_answered_in_parallel() {
		givenAnExecutorWithCapacityForAllCalls();

		whenSeveralCallsArrivesInDifferentThreads();

		thenThereIsNoOnGoingCalls();
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

	private void givenAnExecutorWithCapacityForAllCalls() {
		executorService = Executors.newFixedThreadPool(PARALLEL_EXECUTIONS * CALLS_SIZE);
	}

}
