package com.group.callcenter.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.CallAnswerer;
import com.group.callcenter.domain.CallCenter;
import com.group.callcenter.domain.Dispatcher;
import com.group.callcenter.infrastructure.FixedCapacityCallAnswerer;

public class DispatcherTest {
	private Call call = mock(Call.class);
	private Consumer<Call> onDispatcherCapacityExceeded;
	private Dispatcher dispatcher;
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	private CallCenter callCenter;

	@Before
	public void setUp() {
		FixedCapacityCallAnswerer defaultGroup = new FixedCapacityCallAnswerer("default", 1F);
		List<CallAnswerer> answererGroup = Lists.newArrayList(defaultGroup);

		callCenter = new CallCenter(answererGroup);
		callCenter.setOnNoEmployeeAvailable(call -> System.out.println("Handler invoked"));

		onDispatcherCapacityExceeded = mock(Consumer.class);
	}

	@Test
	public void a_call_is_dispatched_when_has_answer_capacity() {
		givenADispatcherWithRemainingCapacity();

		whenACallArrives();

		thenCallIsDispatched();
		thenOnCapacityExceedWasNotCalled();
	}

	@Test
	public void handle_a_call_when_exceeds_answer_capacity() {
		givenADispatcherAtItsLimit();

		whenACallArrives();

		thenOnCapacityExceededWasCalled();
		thenCallWasNotDispatched();
	}

	@Test
	public void increase_ongoing_calls_when_a_call_ends() {
		givenACallCenterThatDoesNotEndCalls();
		givenADispatcherWithRemainingCapacity();

		dispatcher.dispatchCall(call);

		thenThereIsAOngoingCall();
	}

	private void givenACallCenterThatDoesNotEndCalls() {
		callCenter = mock(CallCenter.class);
	}

	private void givenADispatcherAtItsLimit() {
		dispatcher = aDispatcherWithCapacity(0);
	}

	private void givenADispatcherWithRemainingCapacity() {
		dispatcher = aDispatcherWithCapacity(10);
	}

	private void whenACallArrives() {
		dispatcher.dispatchCall(call);
		try {
			executorService.awaitTermination(10, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void thenOnCapacityExceededWasCalled() {
		verify(onDispatcherCapacityExceeded).accept(call);
	}

	private void thenCallWasNotDispatched() {
		verify(call, times(0)).link();
	}

	private void thenOnCapacityExceedWasNotCalled() {
		verifyZeroInteractions(onDispatcherCapacityExceeded);
	}

	private void thenThereIsAOngoingCall() {
		Assertions.assertThat(dispatcher.getCurrentOngoingCalls()).isEqualTo(1);
	}

	private void thenCallIsDispatched() {
		verify(call).link();
	}


	private Dispatcher aDispatcherWithCapacity(int capacity) {
		return new Dispatcher(onDispatcherCapacityExceeded, capacity, callCenter, executorService, call ->{});
	}

}
