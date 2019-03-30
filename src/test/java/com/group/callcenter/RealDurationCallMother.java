package com.group.callcenter;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.group.callcenter.domain.Call;

public class RealDurationCallMother {

	private static final int MIN_DURATION_IN_SECONDS = 1;
	private static final int MAX_DURATION_IN_SECONDS = 5;
	private Random random = new Random();
	private int counter = 0;

	public List<Call> aRandomDurationCallList(int size, int groupId) {
		List<Call> calls = Lists.newArrayList();
		for (int i = 0; i < size; i++) {
			calls.add(aCall(groupId));
		}
		return calls;
	}

	public MockedTimeDurationCall aCall(int groupId) {
		String id = String.format("%d-%d", groupId, ++counter);
		return new MockedTimeDurationCall(getRandomDuration(), id);
	}

	private int getRandomDuration() {
		return random.nextInt((MAX_DURATION_IN_SECONDS - MIN_DURATION_IN_SECONDS) + 1) + MIN_DURATION_IN_SECONDS;
	}

}
