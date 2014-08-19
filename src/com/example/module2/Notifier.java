package com.example.module2;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Notifier {
	private static String TAG = "Notifier";
	public static int SHORT_TERM = 1;
	public static int LONG_TERM = 2;
	
	public static void notifyUser(Context context, String message, int duration){
		try {
			switch(duration){
			case 1:
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				break;
			}
			
		} catch (Exception e) {
			Log.e(TAG, "Invalid input in third parameter!");
		}
	}

}
