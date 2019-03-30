package com.group.callcenter.infrastructure;

import java.util.Random;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.CallAnswerer;

public class FixedCapacityCallAnswerer implements CallAnswerer {
	private static final Logger logger = LoggerFactory.getLogger(FixedCapacityCallAnswerer.class);
	private String name;
	private float probability;
	Random random = new Random();


	public FixedCapacityCallAnswerer(String name, float probability) {
		this.name = name;
		this.probability = probability;
	}

	@Override
	public void setOnCallFinished(Consumer<Call> onCallFinished) {

	}

	@Override
	public boolean canAnswerCall() {
		float value = random.nextFloat();
		return value < probability;
	}

	@Override
	public void answer(Call call) {
		logger.info("Call {} answer by {}", call, name);
		call.link();
	}

	@Override
	public String getName() {
		return name;
	}
}
