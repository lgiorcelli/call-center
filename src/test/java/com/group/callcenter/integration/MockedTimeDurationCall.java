package com.group.callcenter.integration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.group.callcenter.domain.Call;

public class MockedTimeDurationCall implements Call {

	private final int duration;
	private String id;

	public MockedTimeDurationCall(int duration) {
		this.id = UUID.randomUUID().toString();
		this.duration = duration;
	}

	public void link() {
		sleep();
		System.out.println(String.format("Call %s Finished after %d seconds", this, duration));
	}

	public String getId() {
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
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
