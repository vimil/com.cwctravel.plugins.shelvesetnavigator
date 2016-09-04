package com.cwctravel.plugins.shelvesetreview.asynch;

public class RepeatingJob {
	private int counter;

	public void schedule(Runnable command) {
		counter++;
		AsyncService.execute(new Runnable() {
			int requestId = counter;

			@Override
			public void run() {
				if (requestId == counter) {
					command.run();
				}
			}
		});
	}

}
