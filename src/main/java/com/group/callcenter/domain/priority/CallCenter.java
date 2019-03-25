package com.group.callcenter.domain.priority;

import java.util.List;
import java.util.function.Consumer;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.CallAnswerer;

public class CallCenter {
	private final List<CallAnswerer> answererGroups;
	private Consumer<Call> onCallFinished = call -> {};
	private Consumer<Call> onNoEmployeeAvailable = call -> {
	};

	public CallCenter(List<CallAnswerer> answererGroups) {
		this.answererGroups = answererGroups;
	}


	public void setOnCallFinished(Consumer<Call> onCallFinished) {
		for (CallAnswerer group : this.answererGroups) {
			group.setOnCallFinished(onCallFinished);
		}
	}

	public void setOnNoEmployeeAvailable(Consumer<Call> onNoEmployeeAvailable) {
		this.onNoEmployeeAvailable = onNoEmployeeAvailable;
	}

	public void accept(Call call) {
		CallAnswerer availableGroup = selectAvailableGroup();
		if (availableGroup != null) {
			System.out.println(String.format("Call %s Attended by %s", call, availableGroup.getName()));
			availableGroup.answer(call);
		} else {
			onNoEmployeeAvailable.accept(call);
		}
	}

	private CallAnswerer selectAvailableGroup() {
		synchronized (answererGroups) {
			for (CallAnswerer group : answererGroups) {
				if (group.canAnswerCall()) {
					return group;
				}
			}
		}
		return null;
	}

}
