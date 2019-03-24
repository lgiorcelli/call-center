package com.group.callcenter.unit;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.priority.PriorityChainHandler;

public class DispatcherTest {
	private Call call = () -> { };

	@Test
	public void call_priority_handler_when_a_call_arrives_and_has_answer_capacity() {
		PriorityChainHandler priorityHandler = Mockito.mock(PriorityChainHandler.class);

		Dispatcher dispatcher = new Dispatcher(priorityHandler);
		//WHEN
		dispatcher.dispatchCall(call);
		//THEN
		Mockito.verify(priorityHandler).handle(call);
	}

	public class Dispatcher {
		private PriorityChainHandler priorityHandler;

		public Dispatcher(PriorityChainHandler priorityHandler) {
			this.priorityHandler = priorityHandler;
		}

		void dispatchCall(Call call) {
			priorityHandler.handle(call);
		}
	}
}
