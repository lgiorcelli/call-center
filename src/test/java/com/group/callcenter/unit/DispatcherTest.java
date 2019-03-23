package com.group.callcenter.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.group.callcenter.Call;
import com.group.callcenter.CallAnswerer;
import com.group.callcenter.RolePriorityDispatcher;

public class DispatcherTest {
	private CallAnswerer supervisors;
	private CallAnswerer operators;
	private CallAnswerer managers;

	private Call ANY_CALL = () -> {
	};

	private RolePriorityDispatcher dispatcher;

	@Before
	public void setUp() {
		operators = mock(CallAnswerer.class);
		supervisors = mock(CallAnswerer.class);
		managers = mock(CallAnswerer.class);
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
		setGroupAvailability(managers, true);
	}

	private void givenAnUnavailableGroupOfSupervisors() {
		setGroupAvailability(supervisors, false);
	}

	private void givenAnAvailableGroupOfOperators() {
		setGroupAvailability(operators, true);
	}

	private void givenAnAvailableGroupOfSupervisors() {
		setGroupAvailability(supervisors, true);
	}

	private void givenAnUnavailableGroupOfOperators() {
		setGroupAvailability(operators, false);
	}

	private void setGroupAvailability(CallAnswerer managerPool, boolean availability) {
		when(managerPool.canAnswerCall()).thenReturn(availability);
	}

	private void givenADispatcherWithRolePriority() {
		List<CallAnswerer> priorityQueue = Lists.newArrayList(operators, supervisors, managers);
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
		verify(operators, times(operatorAttendedCalls)).answerCall(ANY_CALL);
		verify(supervisors, times(supervisorAttendedCalls)).answerCall(ANY_CALL);
		verify(managers, times(managerAttendedCalls)).answerCall(ANY_CALL);
	}

}

