package com.group.callcenter;

import java.util.List;

import com.google.common.collect.Lists;

public class RolePriorityChain {
	private List<CallAnswerer> priorityQueue = Lists.newArrayList();

	public RolePriorityChain(List<CallAnswerer> priorityQueue) {
		this.priorityQueue.addAll(priorityQueue);
	}

	public void dispatchCall(Call call) {
		int priority = 0;
		while (!priorityQueue.get(priority).canAnswerCall()) {
			priority++;
		}
		priorityQueue.get(priority).answerCall(call);
	}
}
