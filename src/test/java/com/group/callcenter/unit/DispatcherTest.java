package com.group.callcenter.unit;

import java.util.function.Consumer;

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

	@Before
	public void setUp() {
		priorityHandler = Mockito.mock(PriorityChainHandler.class);
		onDispatcherCapacityExceeded = Mockito.mock(Consumer.class);
	}

	@Test
	public void call_PriorityHandler_when_a_call_arrives_and_has_answer_capacity() {
		Dispatcher dispatcher = new Dispatcher(priorityHandler, onDispatcherCapacityExceeded, 10);
		//WHEN
		dispatcher.dispatchCall(call);
		//THEN
		Mockito.verify(priorityHandler).handle(call);
		Mockito.verifyZeroInteractions(priorityHandler);
	}

	@Test
	public void handle_a_call_when_exceeds_answer_capacity() {
		//GIVEN
		Dispatcher dispatcher = new Dispatcher(priorityHandler, onDispatcherCapacityExceeded, 0);
		//WHEN
		dispatcher.dispatchCall(call);
		//THEN
		Mockito.verify(onDispatcherCapacityExceeded).accept(call);
		Mockito.verifyZeroInteractions(priorityHandler);
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
			if (currentOngoingCalls >= maxOngoingCalls) {
				onDispatcherCapacityExceeded.accept(call);
			} else {
				priorityHandler.handle(call);
			}
		}
	}
}
