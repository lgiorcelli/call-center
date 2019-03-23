package com.group.callcenter;

import java.util.List;

class RolePriorityDispatcher implements Dispatcher {
	private RolePriorityChain rolePriorityChain;

	public RolePriorityDispatcher(List<Pool> priorityQueue) {
		rolePriorityChain = new RolePriorityChain(priorityQueue);
	}

	public void dispatchCall(Call call) {
		rolePriorityChain.dispatchCall(call);
	}
}
