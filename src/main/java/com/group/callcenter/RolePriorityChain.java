package com.group.callcenter;

import java.util.List;

import com.google.common.collect.Lists;

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
