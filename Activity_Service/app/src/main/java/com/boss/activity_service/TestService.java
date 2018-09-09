package com.boss.activity_service;

import android.app.Service;
import android.content.*;
import android.os.*;
import android.os.Process;
import android.util.Log;

public class TestService extends Service {
    public static final int COUNT_PLUS = 1;
    public static final int COUNT_MINUS = 2;
	public static final int SET_COUNT = 0;
	public static final int GET_COUNT = 3;
	
	int count = 0;
	
	IncomingHandler inHandler;
	
	Messenger messanger;
	Messenger toActivityMessenger;
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();

	    inHandler = new IncomingHandler(thread.getLooper());
	    messanger = new Messenger(inHandler);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return messanger.getBinder();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
	
	//обработчик сообщений активити
	private class IncomingHandler extends Handler {
		public IncomingHandler(Looper looper){
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg){
			//super.handleMessage(msg);
			
			toActivityMessenger = msg.replyTo;
			
			switch (msg.what) {
			case SET_COUNT:
				count = msg.arg1; 
				
				Log.i(MainActivity.TAG, "(service)...set count");
				break;

			case COUNT_PLUS:
				count++;
				
				Log.i(MainActivity.TAG, "(service)...count plus");
				break;
				
			case COUNT_MINUS:
                count--;

				Log.i(MainActivity.TAG, "(service)...count minus");
				break;
			}
			
			//отправляем значение счетчика в активити
			Message outMsg = Message.obtain(inHandler, GET_COUNT);
			outMsg.arg1 = count;
			outMsg.replyTo = messanger;
			
			try {
				if( toActivityMessenger != null )
				    toActivityMessenger.send(outMsg);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
