package com.group.callcenter;

import java.util.List;

public class RolePriorityDispatcher implements Dispatcher {
	private RolePriorityChain rolePriorityChain;

	public RolePriorityDispatcher(List<CallAnswerer> priorityQueue) {
		rolePriorityChain = new RolePriorityChain(priorityQueue);
	}

	public void dispatchCall(Call call) {
		rolePriorityChain.dispatchCall(call);
	}
}
