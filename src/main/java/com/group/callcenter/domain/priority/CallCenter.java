package com.group.callcenter.domain.priority;

import java.util.List;
import java.util.function.Consumer;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.CallAnswerer;

public class CallCenter {
	private List<CallAnswerer> answererGroups;
	private Consumer<Call> onNoEmployeeAvailable = call -> {};

	public CallCenter(List<CallAnswerer> answererGroups, Consumer<Call> onCallFinished) {
		this.answererGroups = answererGroups;
		for (CallAnswerer group : this.answererGroups) {
			group.setOnCallFinished(onCallFinished);
		}
	}

	public void setOnNoEmployeeAvailable(Consumer<Call> onNoEmployeeAvailable) {
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
