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

	@Test
	public void assign_a_call_to_a_manager_when_there_are_not_available_operators_nor_managers() {
		givenAnUnavailableGroupOfOperators();
		givenAnUnavailableGroupOfSupervisors();
		givenAnAvailableGroupOfManagers();

		givenADispatcherWithRolePriority();
		//WHEN
		whenACallIsDispatched();
		//THEN
		verifyCallsAttended(0,0,1);
	}

	private void givenAnAvailableGroupOfManagers() {
		setGroupAvailability(managerPool, true);
	}

	private void givenAnUnavailableGroupOfSupervisors() {
		setGroupAvailability(supervisorPool, false);
	}

	private void givenAnAvailableGroupOfOperators() {
		setGroupAvailability(operatorPool, true);
	}

	private void givenAnAvailableGroupOfSupervisors() {
		setGroupAvailability(supervisorPool, true);
	}

	private void givenAnUnavailableGroupOfOperators() {
		setGroupAvailability(operatorPool, false);
	}

	private void setGroupAvailability(Pool managerPool, boolean availability) {
		when(managerPool.canAttendCall()).thenReturn(availability);
	}

	private void givenADispatcherWithRolePriority() {
		List<Pool> priorityQueue = Lists.newArrayList(operatorPool, supervisorPool, managerPool);
		dispatcher = new RolePriorityDispatcher(priorityQueue);
	}

	private void whenACallIsDispatched() {
		dispatcher.dispatchCall(ANY_CALL);
	}

	private void thenOperatorsAttendsTheCall() {
		verifyCallsAttended(1, 0, 0);
	}

	private void thenSupervisorsAttendsTheCall() {
		verifyCallsAttended(0, 1, 0);
	}

	private void verifyCallsAttended(int operatorAttendedCalls, int supervisorAttendedCalls, int managerAttendedCalls) {
		verify(operatorPool, times(operatorAttendedCalls)).assignCall(ANY_CALL);
		verify(supervisorPool, times(supervisorAttendedCalls)).assignCall(ANY_CALL);
		verify(managerPool, times(managerAttendedCalls)).assignCall(ANY_CALL);
	}

}

