package com.group.callcenter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class DispatcherTest {
	private Pool supervisorPool;
	private Pool operatorPool;
	private Pool managerPool;

	private Call aCall;
	private RolePriorityDispatcher dispatcher;

	@Before
	public void setUp() {
		operatorPool = mock(Pool.class);
		supervisorPool = mock(Pool.class);
		managerPool = mock(Pool.class);
		givenACall();
	}

	@Test
	public void assign_a_call_to_a_operator_as_a_first_option() {
		//GIVEN
		givenADispatcherWithRolePriority();
		//WHEN
		dispatcher.dispatchCall(aCall);
		//THEN
		thenOnlyOperatorsAttendsTheCall();
	}

	private void givenADispatcherWithRolePriority() {
		List<Pool> priorityQueue = Lists.newArrayList(operatorPool, supervisorPool, managerPool);
		dispatcher = new RolePriorityDispatcher(priorityQueue);
	}

	private void thenOnlyOperatorsAttendsTheCall() {
		verify(operatorPool).assignCall(aCall);
		verifyZeroInteractions(supervisorPool, managerPool);
	}

	private void givenACall() {
		aCall = new Call();
	}
}

class RolePriorityDispatcher implements Dispatcher {
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