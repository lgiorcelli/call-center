package com.group.callcenter.integration;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.group.callcenter.domain.Call;

public class RealDurationCallMother {

	private static final int MIN_DURATION_IN_SECONDS = 1;
	private static final int MAX_DURATION_IN_SECONDS = 5;
	private Random random = new Random();

	public List<Call> aRandomDurationCallList(int size) {
		List<Call> calls = Lists.newArrayList();
		for (int i = 0; i < size; i++) {
			calls.add(aCall());
		}
		return calls;
	}

	public MockedTimeDurationCall aCall() {
		return new MockedTimeDurationCall(getRandomDuration(MIN_DURATION_IN_SECONDS, MAX_DURATION_IN_SECONDS));
	}

	private int getRandomDuration(int minDurationInSeconds, int maxDurationInSeconds) {
		return random.nextInt((maxDurationInSeconds - minDurationInSeconds) + 1) + minDurationInSeconds;
	}


}
