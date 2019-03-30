package com.group.callcenter;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.group.callcenter.domain.Call;

public class MockedTimeDurationCall implements Call {

	private static final Logger logger = LoggerFactory.getLogger(MockedTimeDurationCall.class);
	private final int duration;
	private String id;

	public MockedTimeDurationCall(int duration, String id) {
		this.id = id;
		this.duration = duration;
	}

	public void link() {
		logger.debug("Linked call {}", this);
		sleep();
		logger.info("Call {} finished after {} seconds", this, duration);
	}

	private void sleep() {
		try {
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			throw new RuntimeException("Error simulando espera", e);
		}
	}

	@Override
	public String toString() {
		return "[" + id + "]";
	}
}
