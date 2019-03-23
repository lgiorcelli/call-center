package com.group.callcenter.domain;

public interface CallAnswerer {
	boolean canAnswerCall();

	void answer(Call call);
}
