package com.group.callcenter;

public interface CallAnswerer {
	boolean canAnswerCall();

	boolean answerCall(Call call);
}
