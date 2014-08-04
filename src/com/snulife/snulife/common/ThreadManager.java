package com.snulife.snulife.common;

public class ThreadManager {

	private static Thread mMainThread;
	
	public static void setMainThread(Thread thread) {
		mMainThread = thread;
	}
	
	public static boolean isMainThread() {
		return mMainThread == Thread.currentThread();
	}
}
