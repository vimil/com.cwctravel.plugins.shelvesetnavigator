package com.cwctravel.plugins.shelvesetreview.asynch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class AsyncService {
	private static final ExecutorService SCHEDULER = Executors.newSingleThreadExecutor(new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		}
	});

	public static void execute(Runnable command) {
		SCHEDULER.execute(command);
	}
}
