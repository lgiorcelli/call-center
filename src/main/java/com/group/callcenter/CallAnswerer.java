package com.group.callcenter;

public interface CallAnswerer {
	boolean canAnswerCall();

	void answerCall(Call call);
}
