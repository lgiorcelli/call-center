package com.group.callcenter.domain.priority;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.CallAnswerer;

public class CallAnswererWrapper implements PriorityChainHandler {
	private CallAnswerer callAnswerer;
	private PriorityChainHandler next;

	public CallAnswererWrapper(CallAnswerer callAnswerer, PriorityChainHandler nextHandler) {
		this.callAnswerer = callAnswerer;
		this.next = nextHandler;
	}

	@Override
	public void handle(Call call) {
		if (callAnswerer.canAnswerCall()) {
			callAnswerer.answer(call);
		} else {
			next.handle(call);
		}
	}
}
