package com.group.callcenter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class DispatcherTest {
	private Pool supervisorPool;
	private Pool operatorPool;
	private Pool managerPool;

	private Call ANY_CALL = new Call();
	private RolePriorityDispatcher dispatcher;

	@Before
	public void setUp() {
		operatorPool = mock(Pool.class);

		supervisorPool = mock(Pool.class);
		managerPool = mock(Pool.class);
	}

	@Test
	public void assign_a_call_to_a_operator_as_a_first_option() {
		//GIVEN
		when(operatorPool.assignCall(ANY_CALL)).thenReturn(true);
		givenADispatcherWithRolePriority();
		//WHEN
		dispatcher.dispatchCall(ANY_CALL);
		//THEN
		thenOnlyOperatorsAttendsTheCall();
	}

	@Test
	public void assign_a_call_to_a_operator_when_there_are_not_available_operators() {
		//GIVEN
		when(operatorPool.assignCall(ANY_CALL)).thenReturn(false);
		when(supervisorPool.assignCall(ANY_CALL)).thenReturn(true);
		givenADispatcherWithRolePriority();
		//WHEN
		dispatcher.dispatchCall(ANY_CALL);
		//THEN
		verify(operatorPool).assignCall(ANY_CALL);
		verify(supervisorPool).assignCall(ANY_CALL);
		verifyZeroInteractions(managerPool);
	}

	private void givenADispatcherWithRolePriority() {
		List<Pool> priorityQueue = Lists.newArrayList(operatorPool, supervisorPool, managerPool);
		dispatcher = new RolePriorityDispatcher(priorityQueue);
	}

	private void thenOnlyOperatorsAttendsTheCall() {
		verify(operatorPool).assignCall(ANY_CALL);
		verifyZeroInteractions(supervisorPool, managerPool);
	}

}

class RolePriorityDispatcher implements Dispatcher {
	private List<Pool> priorityQueue = Lists.newArrayList();

	public RolePriorityDispatcher(List<Pool> priorityQueue) {
		this.priorityQueue.addAll(priorityQueue);
	}

	public void dispatchCall(Call call) {
		int priority = 0;
		while (!priorityQueue.get(priority).assignCall(call)) {
			priority++;
		}
	}
}

interface Dispatcher {
	void dispatchCall(Call call);
}

interface Pool {

	boolean assignCall(Call call);
}

class Call {

}