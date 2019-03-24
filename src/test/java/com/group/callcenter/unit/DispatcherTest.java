package com.group.callcenter.unit;

import java.util.function.Consumer;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.priority.PriorityChainHandler;

public class DispatcherTest {
	private Call call = () -> {
	};
	private Consumer<Call> onDispatcherCapacityExceeded;
	private PriorityChainHandler priorityHandler;
	private Dispatcher dispatcher;

	@Before
	public void setUp() {
		priorityHandler = Mockito.mock(PriorityChainHandler.class);
		onDispatcherCapacityExceeded = Mockito.mock(Consumer.class);
	}

	@Test
	public void call_PriorityHandler_when_a_call_arrives_and_has_answer_capacity() {
		givenADispatcherWithRemainingCapacity();
		//WHEN
		whenACallArrives();
		//THEN
		thenCallIsDispatched();
		thenOnCapacityExceedWasNotCalled();
		Assertions.assertThat(dispatcher.currentOngoingCalls).isEqualTo(1);
	}

	@Test
	public void handle_a_call_when_exceeds_answer_capacity() {
		//GIVEN
		givenADispatcherAtItsLimit();
		//WHEN
		whenACallArrives();
		//THEN
		thenOnCapacityExceededWasCalled();
		thenCallWasNotDispatched();
	}


	private void givenADispatcherAtItsLimit() {
		dispatcher = aDispatcherWithCapacity(0);
	}

	private void givenADispatcherWithRemainingCapacity() {
		dispatcher = aDispatcherWithCapacity(10);
	}

	private void whenACallArrives() {
		dispatcher.dispatchCall(call);
	}

	private void thenOnCapacityExceededWasCalled() {
		Mockito.verify(onDispatcherCapacityExceeded).accept(call);
	}

	private void thenCallWasNotDispatched() {
		Mockito.verifyZeroInteractions(priorityHandler);
	}

	private void thenOnCapacityExceedWasNotCalled() {
		Mockito.verifyZeroInteractions(onDispatcherCapacityExceeded);
	}

	private void thenCallIsDispatched() {
		Mockito.verify(priorityHandler).handle(call);
	}


	private Dispatcher aDispatcherWithCapacity(int capacity) {
		return new Dispatcher(priorityHandler, onDispatcherCapacityExceeded, capacity);
	}

	public class Dispatcher {
		private PriorityChainHandler priorityHandler;
		private Consumer<Call> onDispatcherCapacityExceeded;
		private int currentOngoingCalls = 0;
		private int maxOngoingCalls;

		public Dispatcher(PriorityChainHandler priorityHandler, Consumer<Call> onDispatcherCapacityExceeded, int maxOngoingCalls) {
			this.priorityHandler = priorityHandler;
			this.onDispatcherCapacityExceeded = onDispatcherCapacityExceeded;
			this.maxOngoingCalls = maxOngoingCalls;
		}

		void dispatchCall(Call call) {
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
			priorityHandler.handle(call);
		}

		private void incrementOngoingCalls() {
			currentOngoingCalls++;
		}
	}
}
