package com.group.callcenter.domain;

import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallCenter {
	private static final Logger logger = LoggerFactory.getLogger(CallCenter.class);
	private final List<CallAnswerer> answererGroups;
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
			logger.info("Call {} attended by {}", call, availableGroup.getName());
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
