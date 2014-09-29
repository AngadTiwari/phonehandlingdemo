package com.example.phonehandlingdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneHandler extends BroadcastReceiver 
{
	public static String phoneNumber;
	private Context context;
	public static boolean isRinging=false,isAttended=false,isCallEnded=false;
	private TelephonyManager telephony;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		this.context = context ;
	    telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    telephony.listen(phoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	private final PhoneStateListener phoneCallListener = new PhoneStateListener() 
	{
	        @Override
	        public void onCallStateChanged(int state, String incomingNumber) 
	        {
	            switch(state)
	            {
	            case TelephonyManager.CALL_STATE_RINGING:
	                 isRinging = true;
	                 Log.i("phoneHandling", "call is ringing");
	                 phoneNumber=incomingNumber;
	                 Intent callIntent = new Intent(context.getApplicationContext(),MainActivity.class);
		             callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		             context.startActivity(callIntent);
	                 break;
	            case TelephonyManager.CALL_STATE_OFFHOOK:
	                 Log.i("phoneHandling", "call is attended");
	                 break;
	            case TelephonyManager.CALL_STATE_IDLE:
	                 Log.i("phoneHandling", "call state is idle");
	                 break;
	            }
	            super.onCallStateChanged(state, incomingNumber);
	        }
	    };
}
