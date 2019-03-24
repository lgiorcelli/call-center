package com.group.callcenter.domain;

import java.util.function.Consumer;

public interface CallAnswerer {

	void setOnCallFinished(Consumer<Call> onCallFinished);

	boolean canAnswerCall();

	void answer(Call call);

	String getName();
}
