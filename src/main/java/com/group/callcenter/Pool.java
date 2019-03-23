package com.group.callcenter;

interface Pool {
	boolean canAttendCall();

	boolean assignCall(Call call);
}
