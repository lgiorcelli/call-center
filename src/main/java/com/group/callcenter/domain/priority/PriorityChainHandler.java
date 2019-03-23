package com.group.callcenter.domain.priority;

import com.group.callcenter.domain.Call;

public interface PriorityChainHandler {
	void handle(Call call);
}
