package com.group.callcenter.integration;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.group.callcenter.domain.Call;

public class MockedTimeDurationCall implements Call {

	private final int duration;
	private int id;

	public MockedTimeDurationCall(int duration, int id) {
		this.id = id;
		this.duration = duration;
	}

	public void link() {
		System.out.println("Linked call " + this);
		sleep();
		System.out.println(String.format("Call %s Finished after %d seconds", this, duration));
	}

	public int getId() {
		return id;
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
