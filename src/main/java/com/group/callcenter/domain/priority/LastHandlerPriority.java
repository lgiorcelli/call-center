package com.group.callcenter.domain.priority;

import java.util.function.Consumer;

import com.group.callcenter.domain.Call;

public class LastHandlerPriority implements PriorityChainHandler {
	private Consumer<Call> action;

	public LastHandlerPriority(Consumer<Call> action) {
		this.action = action;
	}

	@Override
	public void handle(Call call) {
		action.accept(call);
	}
}
