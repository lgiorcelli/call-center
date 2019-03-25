package com.group.callcenter.integration;

import java.util.concurrent.TimeUnit;

import com.group.callcenter.domain.Call;

public class MockedTimeDurationCall implements Call {

	private final int duration;
	private String id;

	public MockedTimeDurationCall(int duration, String id) {
		this.id = id;
		this.duration = duration;
	}

	public void link() {
		System.out.println("Linked call " + this);
		sleep();
		System.out.println(String.format("Call %s Finished after %d seconds", this, duration));
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
