package com.group.callcenter.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import com.group.callcenter.DefaultCallAnswerer;
import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.CallAnswerer;
import com.group.callcenter.domain.priority.CallAnswererWrapper;
import com.group.callcenter.domain.priority.LastHandlerPriority;

public class DispatcherTest {
	private Call call = mock(Call.class);
	private Consumer<Call> onDispatcherCapacityExceeded;
	private List<CallAnswerer> answererGroup;
	private Dispatcher dispatcher;
	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Before
	public void setUp() {
		DefaultCallAnswerer defaultGroup = new DefaultCallAnswerer(executorService, 10, call -> System.out.println("Finished call = " + call));
		answererGroup = Lists.newArrayList(defaultGroup);


		new CallAnswererWrapper(defaultGroup, new LastHandlerPriority(call -> {
			throw new RuntimeException("call must be handled");
		}));

		onDispatcherCapacityExceeded = mock(Consumer.class);
	}

	@Test
	public void call_PriorityHandler_when_a_call_arrives_and_has_answer_capacity() {
		givenADispatcherWithRemainingCapacity();
		//WHEN
		whenACallArrives();
		//THEN
		thenCallIsDispatched();
		thenOnCapacityExceedWasNotCalled();
		Assertions.assertThat(dispatcher.currentOngoingCalls).isEqualTo(1);
	}

	@Test
	public void handle_a_call_when_exceeds_answer_capacity() {
		//GIVEN
		givenADispatcherAtItsLimit();
		//WHEN
		whenACallArrives();
		//THEN
		thenOnCapacityExceededWasCalled();
		thenCallWasNotDispatched();
		Assertions.assertThat(dispatcher.currentOngoingCalls).isEqualTo(0);
	}

	@Test
	public void decrease_ongoing_calls_when_a_call_ends() {
		//GIVEN
		givenADispatcherWithRemainingCapacity();
		dispatcher.handleCall(call);
		//WHEN

		//THEN
		//
	}

	private void givenADispatcherAtItsLimit() {
		dispatcher = aDispatcherWithCapacity(0);
	}

	private void givenADispatcherWithRemainingCapacity() {
		dispatcher = aDispatcherWithCapacity(10);
	}

	private void whenACallArrives() {
		dispatcher.dispatchCall(call);
		try {
			executorService.awaitTermination(10, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void thenOnCapacityExceededWasCalled() {
		verify(onDispatcherCapacityExceeded).accept(call);
	}

	private void thenCallWasNotDispatched() {
		verify(call, times(0)).link();
	}

	private void thenOnCapacityExceedWasNotCalled() {
		verifyZeroInteractions(onDispatcherCapacityExceeded);
	}

	private void thenCallIsDispatched() {
		verify(call).link();
	}

	private Dispatcher aDispatcherWithCapacity(int capacity) {
		return new Dispatcher(answererGroup, onDispatcherCapacityExceeded, capacity);
	}

	public class Dispatcher {
		private Consumer<Call> onDispatcherCapacityExceeded;
		private int currentOngoingCalls = 0;
		private int maxOngoingCalls;
		private CallCenter callCenter;

		public Dispatcher(List<CallAnswerer> answererGroup, Consumer<Call> onDispatcherCapacityExceeded, int maxOngoingCalls) {
			this.onDispatcherCapacityExceeded = onDispatcherCapacityExceeded;
			this.maxOngoingCalls = maxOngoingCalls;
			this.callCenter = new CallCenter(answererGroup, call -> System.out.println("Handler invoked"));
		}

		void dispatchCall(Call call) {
			if (hasRemainingCapacity()) {
				handleCall(call);
			} else {
				onDispatcherCapacityExceeded.accept(call);
			}
		}

		private boolean hasRemainingCapacity() {
			return currentOngoingCalls < maxOngoingCalls;
		}

		private void handleCall(Call call) {
			incrementOngoingCalls();
			callCenter.accept(call);
		}

		private void incrementOngoingCalls() {
			currentOngoingCalls++;
		}

		private void decreaseOnGoingCalls() {
			currentOngoingCalls--;
		}
	}

	public class CallCenter {
		private List<CallAnswerer> answererGroups;
		private Consumer<Call> onNoEmployeeAvailable;

		public CallCenter(List<CallAnswerer> answererGroups, Consumer<Call> onNoEmployeeAvailable) {
			this.answererGroups = answererGroups;
			this.onNoEmployeeAvailable = onNoEmployeeAvailable;
		}

		public void accept(Call call) {
			boolean callAnswered = false;
			for (CallAnswerer group : answererGroups) {
				if (group.canAnswerCall()) {
					callAnswered = true;
					group.answer(call);
				}
			}
			if (!callAnswered) {
				onNoEmployeeAvailable.accept(call);
			}
		}
	}
}
