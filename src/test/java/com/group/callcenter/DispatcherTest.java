package com.group.callcenter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
		givenAnAvailableGroupOfOperators();
		givenADispatcherWithRolePriority();
		//WHEN
		whenACallIsDispatched();
		//THEN
		thenOperatorsAttendsTheCall();
	}

	@Test
	public void assign_a_call_to_a_supervisor_when_there_are_not_available_operators() {
		//GIVEN
		givenAnUnavailableGroupOfOperators();
		givenAnAvailableGroupOfSupervisors();
		givenADispatcherWithRolePriority();
		//WHEN
		whenACallIsDispatched();
		//THEN
		thenSupervisorsAttendsTheCall();
	}

	private void givenAnAvailableGroupOfOperators() {
		when(operatorPool.canAttendCall()).thenReturn(true);
	}

	private void givenAnAvailableGroupOfSupervisors() {
		when(supervisorPool.canAttendCall()).thenReturn(true);
	}

	private void givenAnUnavailableGroupOfOperators() {
		when(operatorPool.canAttendCall()).thenReturn(false);
	}

	private void givenADispatcherWithRolePriority() {
		List<Pool> priorityQueue = Lists.newArrayList(operatorPool, supervisorPool, managerPool);
		dispatcher = new RolePriorityDispatcher(priorityQueue);
	}

	private void whenACallIsDispatched() {
		dispatcher.dispatchCall(ANY_CALL);
	}

	private void thenOperatorsAttendsTheCall() {
		verifyCallsAttended(1, 0);
	}

	private void thenSupervisorsAttendsTheCall() {
		verifyCallsAttended(0, 1);
	}

	private void verifyCallsAttended(int operatorAttendedCalls, int supervisorAttendedCalls) {
		verify(operatorPool, times(operatorAttendedCalls)).assignCall(ANY_CALL);
		verify(supervisorPool, times(supervisorAttendedCalls)).assignCall(ANY_CALL);
		verify(managerPool, times(0)).assignCall(ANY_CALL);
	}

}

class RolePriorityDispatcher implements Dispatcher {
	private RolePriorityChain rolePriorityChain;

	public RolePriorityDispatcher(List<Pool> priorityQueue) {
		rolePriorityChain = new RolePriorityChain(priorityQueue);
	}

	public void dispatchCall(Call call) {
		rolePriorityChain.dispatchCall(call);
	}
}

class RolePriorityChain {
	private List<Pool> priorityQueue = Lists.newArrayList();

	public RolePriorityChain(List<Pool> priorityQueue) {
		this.priorityQueue.addAll(priorityQueue);
	}

	public void dispatchCall(Call call) {
		int priority = 0;
		while (!priorityQueue.get(priority).canAttendCall()) {
			priority++;
		}
		priorityQueue.get(priority).assignCall(call);
	}
}

interface Dispatcher {
	void dispatchCall(Call call);
}

interface Pool {
	boolean canAttendCall();

	boolean assignCall(Call call);
}

class Call {

}