package com.group.callcenter.integration;

import java.util.Random;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.CallAnswerer;

public class FixedCapacityCallAnswerer implements CallAnswerer {
	private final static Logger LOGGER = Logger.getLogger(FixedCapacityCallAnswerer.class.getName());
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
		System.out.println(String.format("Call %s answer by %s", call, name));
		call.link();
	}

	@Override
	public String getName() {
		return name;
	}
}
