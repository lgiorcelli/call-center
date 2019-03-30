package com.group.callcenter.domain;

import java.util.List;
import java.util.Optional;
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

	public void setOnNoEmployeeAvailable(Consumer<Call> onNoEmployeeAvailable) {
		this.onNoEmployeeAvailable = onNoEmployeeAvailable;
	}

	public void accept(Call call) {
		Optional<CallAnswerer> availableGroup = selectAvailableGroup();
		if (availableGroup.isPresent()) {
			CallAnswerer answerer = availableGroup.get();
			logger.info("Call {} attended by {}", call, answerer.getName());
			answerer.answer(call);
		} else {
			onNoEmployeeAvailable.accept(call);
		}
	}

	private Optional<CallAnswerer> selectAvailableGroup() {
		synchronized (answererGroups) {
			for (CallAnswerer group : answererGroups) {
				if (group.canAnswerCall()) {
					return Optional.of(group);
				}
			}
		}
		return Optional.empty();
	}

}
