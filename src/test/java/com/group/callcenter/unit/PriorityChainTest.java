package com.group.callcenter.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.CallAnswerer;
import com.group.callcenter.domain.CallCenter;

public class PriorityChainTest {
	private CallAnswerer secondGroup;
	private CallAnswerer firstGroup;
	private Consumer<Call> noGroupAvailable;

	private Call ANY_CALL = () -> {
	};

	private CallCenter callCenter;
	private Consumer<Call> onCallFinished;

	@Before
	public void setUp() {
		firstGroup = mock(CallAnswerer.class);
		secondGroup = mock(CallAnswerer.class);
		onCallFinished = mock(Consumer.class);

		noGroupAvailable = mock(Consumer.class);

		callCenter = new CallCenter(Lists.newArrayList(firstGroup, secondGroup));
		callCenter.setOnNoEmployeeAvailable(noGroupAvailable);
		callCenter.setOnCallFinished(onCallFinished);
	}

	@Test
	public void only_first_link_is_called_if_can_handle_the_call() {
		//GIVEN
		givenAnAvailableFirstLink();
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

		//WHEN
		whenACallIsDispatched();
		//THEN
		thenSecondLinkHandleTheCall();
	}

	@Test
	public void execute_final_action_when_no_links_can_answer_the_call() {
		givenAnUnavailableFirstLink();
		givenAnUnavailableSecondLink();

		whenACallIsDispatched();

		thenNoLinksHandleTheCall();
		thenNoLinkAvailableActionWasCall();
	}

	private void givenAnUnavailableSecondLink() {
		setGroupAvailability(secondGroup, false);
	}

	private void givenAnAvailableFirstLink() {
		setGroupAvailability(firstGroup, true);
	}

	private void givenAnAvailableSecondLink() {
		setGroupAvailability(secondGroup, true);
	}

	private void givenAnUnavailableFirstLink() {
		setGroupAvailability(firstGroup, false);
	}

	private void setGroupAvailability(CallAnswerer managerPool, boolean availability) {
		when(managerPool.canAnswerCall()).thenReturn(availability);
	}


	private void whenACallIsDispatched() {
		callCenter.accept(ANY_CALL);
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
		verify(noGroupAvailable).accept(ANY_CALL);
	}

	private void verifyCallsAttended(int firstLinkHandledCalls, int secondLinkHandledCalls) {
		verify(firstGroup, times(firstLinkHandledCalls)).answer(ANY_CALL);
		verify(secondGroup, times(secondLinkHandledCalls)).answer(ANY_CALL);
	}

}

