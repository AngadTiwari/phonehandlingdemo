package com.example.phonehandlingdemo;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity 
{
	public TextView incomingNumber;
	public Button attendCall,endCall;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		incomingNumber=(TextView)findViewById(R.id.incomingNumber);
		
		attendCall=(Button)findViewById(R.id.attendCall);
		endCall=(Button)findViewById(R.id.endCall);
		
		endCall.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				disconnectCall();
			}
		});
		
		attendCall.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Log.d("InSecond", "InSecond Method Ans Call");
				// froyo and beyond trigger on buttonUp instead of buttonDown
				Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
				buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
				sendOrderedBroadcast(buttonUp,"android.permission.CALL_PRIVILEGED");

				Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
				headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
				headSetUnPluggedintent.putExtra("state", 0);
				headSetUnPluggedintent.putExtra("name", "Headset");
				try 
				{
					sendOrderedBroadcast(headSetUnPluggedintent, null);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				PhoneHandler.isRinging=false;
				PhoneHandler.isAttended=true;
			}
		});
	}
	
	public void disconnectCall(){
		 try 
		 {
		    String serviceManagerName = "android.os.ServiceManager";
		    String serviceManagerNativeName = "android.os.ServiceManagerNative";
		    String telephonyName = "com.android.internal.telephony.ITelephony";
		    Class<?> telephonyClass;
		    Class<?> telephonyStubClass;
		    Class<?> serviceManagerClass;
		    Class<?> serviceManagerNativeClass;
		    Method telephonyEndCall;
		    Object telephonyObject;
		    Object serviceManagerObject;
		    telephonyClass = Class.forName(telephonyName);
		    telephonyStubClass = telephonyClass.getClasses()[0];
		    serviceManagerClass = Class.forName(serviceManagerName);
		    serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
		    Method getService = // getDefaults[29];
		    serviceManagerClass.getMethod("getService", String.class);
		    Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
		    Binder tmpBinder = new Binder();
		    tmpBinder.attachInterface(null, "fake");
		    serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
		    IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
		    Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
		    telephonyObject = serviceMethod.invoke(null, retbinder);
		    telephonyEndCall = telephonyClass.getMethod("endCall");
		    telephonyEndCall.invoke(telephonyObject);
			PhoneHandler.isCallEnded=true;
		  } 
		  catch (Exception e) 
		  {
		     e.printStackTrace();
		  }
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;																
	}
	//this override method check the windows focus..when focus is lost(on any system popup occur)...again call the same activity
		@SuppressLint("NewApi")
		@Override
		public void onWindowFocusChanged(boolean hasFocus)
		{
			super.onWindowFocusChanged(hasFocus);
			
			Log.i("phoneHandling", "focus changed");

			//if(!hasFocus) is modified from if(!hasFocus && mEnabled)...mEnabled is a boolean flag...if activity is finished and new then it set as true
			if(!hasFocus)
			{
				ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
				am.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME );
				sendBroadcast( new Intent(Intent.ACTION_ANSWER) );
				Log.i("phoneHandling", "focus back to app");
				
				if(PhoneHandler.isRinging)
				{
					incomingNumber.setText(PhoneHandler.phoneNumber);
				}
			}
		}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
