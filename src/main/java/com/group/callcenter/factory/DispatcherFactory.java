package com.group.callcenter.factory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.group.callcenter.domain.Call;
import com.group.callcenter.domain.CallAnswerer;
import com.group.callcenter.domain.CallCenter;
import com.group.callcenter.domain.Dispatcher;
import com.group.callcenter.infrastructure.FixedCapacityCallAnswerer;

public class DispatcherFactory {
	private static final Logger logger = LoggerFactory.getLogger(DispatcherFactory.class);

	public Dispatcher create() {
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		Consumer<Call> onDispatcherCapacityExceeded = call -> logger.info("Dispatcher rejects call: {}", call);
		CallAnswerer operators = new FixedCapacityCallAnswerer("operators", 0.3F);
		CallAnswerer supervisors = new FixedCapacityCallAnswerer("supervisors", 0.5F);
		CallAnswerer managers = new FixedCapacityCallAnswerer("managers", 0.9F);
		List<CallAnswerer> answerPriority = Lists.newArrayList(operators, supervisors, managers);

		CallCenter callCenter = new CallCenter(answerPriority);

		return new Dispatcher(onDispatcherCapacityExceeded, 10, callCenter, executorService,
				call -> logger.info("No employees availables for call {}", call));
	}
}
