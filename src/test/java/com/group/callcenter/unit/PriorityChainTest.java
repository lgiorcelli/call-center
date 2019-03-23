package com.group.callcenter.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.CallAnswerer;
import com.group.callcenter.domain.priority.CallAnswererWrapper;
import com.group.callcenter.domain.priority.LastHandlerPriority;

public class PriorityChainTest {
	private CallAnswerer secondLink;
	private CallAnswerer firstLink;
	private Consumer<Call> noLinksAvailable;

	private Call ANY_CALL = () -> {
	};

	private CallAnswererWrapper callsHandler;

	@Before
	public void setUp() {
		firstLink = mock(CallAnswerer.class);
		secondLink = mock(CallAnswerer.class);

		noLinksAvailable = mock(Consumer.class);
	}

	@Test
	public void only_first_link_is_called_if_can_handle_the_call() {
		//GIVEN
		givenAnAvailableFirstLink();

		givenAConfiguredResponsabilityChain();
		//WHEN
		whenACallIsDispatched();
		//THEN
		thenFirstLinkHandleTheCall();
	}

	@Test
	public void second_link_is_called_if_first_can_not_handle_the_call() {
		//GIVEN
		givenAnUnavailableFirstLink();
		givenAnAvailableSecondLink();

		givenAConfiguredResponsabilityChain();
		//WHEN
		whenACallIsDispatched();
		//THEN
		thenSecondLinkHandleTheCall();
	}

	@Test
	public void execute_final_action_when_no_links_can_answer_the_call() {
		givenAnUnavailableFirstLink();
		givenAnUnavailableSecondLink();

		givenAConfiguredResponsabilityChain();

		whenACallIsDispatched();


		thenNoLinksHandleTheCall();
		thenNoLinkAvailableActionWasCall();
	}

	private void givenAnUnavailableSecondLink() {
		setGroupAvailability(secondLink, false);
	}

	private void givenAnAvailableFirstLink() {
		setGroupAvailability(firstLink, true);
	}

	private void givenAnAvailableSecondLink() {
		setGroupAvailability(secondLink, true);
	}

	private void givenAnUnavailableFirstLink() {
		setGroupAvailability(firstLink, false);
	}

	private void setGroupAvailability(CallAnswerer managerPool, boolean availability) {
		when(managerPool.canAnswerCall()).thenReturn(availability);
	}

	private void givenAConfiguredResponsabilityChain() {
		LastHandlerPriority lastHandler = new LastHandlerPriority(noLinksAvailable);
		CallAnswererWrapper supervisorsHandler = new CallAnswererWrapper(secondLink, lastHandler);
		callsHandler = new CallAnswererWrapper(firstLink, supervisorsHandler);

	}

	private void whenACallIsDispatched() {
		callsHandler.handle(ANY_CALL);
	}

	private void thenNoLinksHandleTheCall() {
		verifyCallsAttended(0, 0);
	}

	private void thenFirstLinkHandleTheCall() {
		verifyCallsAttended(1, 0);
	}

	private void thenSecondLinkHandleTheCall() {
		verifyCallsAttended(0, 1);
	}

	private void thenNoLinkAvailableActionWasCall() {
		verify(noLinksAvailable).accept(ANY_CALL);
	}


	private void verifyCallsAttended(int firstLinkHandledCalls, int secondLinkHandledCalls) {
		verify(firstLink, times(firstLinkHandledCalls)).answer(ANY_CALL);
		verify(secondLink, times(secondLinkHandledCalls)).answer(ANY_CALL);
	}

}

