package com.group.callcenter.integration;

import java.util.List;

import com.google.common.collect.Lists;
import com.group.callcenter.domain.Call;

public class RealDurationCallMother {

	private static final int MIN_DURATION_IN_SECONDS = 5;
	private static final int MAX_DURATION_IN_SECONDS = 10;

	public List<Call> aRandomDurationCallList(int size) {
		List<Call> calls = Lists.newArrayList();
		for (int i = 0; i < size; i++) {
			calls.add(aCall());
		}
		return calls;
	}

	public MockedTimeDurationCall aCall() {
		return new MockedTimeDurationCall(MIN_DURATION_IN_SECONDS, MAX_DURATION_IN_SECONDS);
	}

}
