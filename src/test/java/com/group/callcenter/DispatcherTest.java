package com.group.callcenter;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

public class DispatcherTest {

	@Test
	public void assign_a_call_to_a_operator_as_a_first_option() throws Exception {
		//GIVEN
		// operator availables
		Pool operatorPool = Mockito.mock(Pool.class);
		// supervisor available
		Pool supervisorPool = Mockito.mock(Pool.class);
		// manager available
		Pool managerPool = Mockito.mock(Pool.class);
		//a dispatcher with
		List<Pool> priorityQueue = Lists.newArrayList(operatorPool, supervisorPool, managerPool);

		RolePriorityDispatcher dispatcher = new RolePriorityDispatcher(priorityQueue);
		//a call
		Call call = new Call();
		//WHEN
		// dispatches a call
		dispatcher.dispatchCall(call);
		//THEN
		Mockito.verify(operatorPool).assignCall(call);
		// an operator receives the call
		// supervisor did not receive de call
		Mockito.verifyZeroInteractions(supervisorPool, managerPool);
		// manager did not receive de call
	}
}

class RolePriorityDispatcher implements Dispatcher{
	private List<Pool> priorityQueue = Lists.newArrayList();

	public RolePriorityDispatcher(List<Pool> priorityQueue) {
		this.priorityQueue.addAll(priorityQueue);
	}

	public void dispatchCall(Call call) {
		priorityQueue.get(0).assignCall(call);
	}
}

interface Dispatcher {
	void dispatchCall(Call call);
}

interface Pool {

	void assignCall(Call call);
}

class Call {

}