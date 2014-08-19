package com.example.module2;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class EventCreater {
	
	private Context context;
	private LinearLayout linear;
	
	public EventCreater(Context ctx, LinearLayout lo) {
		context = ctx;
		linear = lo;
	}
	
	public boolean createSmsEvent(){
		try {
			ImageButton n = createEventIcon(context, linear, "smsbtn");
			n.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final Dialog d = createDialog("Send SMS", R.layout.smslayout);
					((Button)d.findViewById(R.id.button1))
					.setOnClickListener(new OnClickListener() {						
						@Override
						public void onClick(View v) {
							try {
								ILOGIC.smsNumberList.add(((EditText)d.findViewById(R.id.editText1))
										.getText().toString());
								ILOGIC.smsMsgList.add(((EditText)d.findViewById(R.id.editText2))
										.getText().toString());
								d.dismiss();
							} catch (Exception e) {
								Notifier.notifyUser(context, "Enter Valid Data!", Notifier.SHORT_TERM);
							}
						}
					});
				}
			});
			linear.addView(n, linear.getChildCount());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean createWaitEvent(){
		try {
			ImageButton n = createEventIcon(context, linear, "waitbtn");
			n.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					final Dialog d = createDialog("Add Delay", R.layout.waitlayout);
					((Button)d.findViewById(R.id.button2))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							d.dismiss();
						}
					});
					((Button)d.findViewById(R.id.button1))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							ILOGIC.delay.add(((EditText)d.findViewById(R.id.editText1)).getText().toString());
							d.dismiss();
						}
					});
				}
			});				
			linear.addView(n, linear.getChildCount());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean createAlertEvent() {
		try {
			ImageButton n = createEventIcon(context, linear, "alertbtn");
			n.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final Dialog d = createDialog("Add Alert Message", R.layout.alertlayout);
					((Button)d.findViewById(R.id.button2))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							d.dismiss();
						}
					});
					((Button)d.findViewById(R.id.button1))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							ILOGIC.alertList.add(((EditText)d.findViewById(R.id.editText1)).getText().toString());
							ILOGIC.alertSeverityList.add(String.valueOf(((Spinner)d.findViewById(R.id.spinner1)).getSelectedItemPosition()));
							d.dismiss();
						}
					});
				}
			});
			linear.addView(n, linear.getChildCount());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean createCallEvent() {
		try {
			ImageButton n = createEventIcon(context, linear, "callbtn");
			n.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final Dialog d = createDialog("Call a Number", R.layout.save_call_repeat_layout);
					((Button)d.findViewById(R.id.button1))
					.setText("Call");
					((Button)d.findViewById(R.id.button1))
					.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							ILOGIC.callList.add(((EditText)d.findViewById(R.id.editText1)).getText().toString());
							d.dismiss();
						}
					});
				}
			});
			linear.addView(n, linear.getChildCount());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean createRepeatEvent() {
		try {
			ImageButton n = createEventIcon(context, linear, "repeatbtn");
			n.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final Dialog d = createDialog("Enter repeatation number", R.layout.save_call_repeat_layout);
					((Button)d.findViewById(R.id.button1))
					.setText("Submit");
					((Button)d.findViewById(R.id.button1))
					.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							ILOGIC.repeatList.add(((EditText)d.findViewById(R.id.editText1)).getText().toString());
							d.dismiss();
						}
					});
				}
			});
			linear.addView(n, linear.getChildCount());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean createIfEvent() {
		try {
			ImageButton n = createEventIcon(context, linear, "ifbtn");
			n.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final Dialog d = createDialog("Enter Condition", R.layout.iflayout);				
					((Button)d.findViewById(R.id.button1))
					.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							checkIfCondition(((Spinner)d.findViewById(R.id.spinner1))
											.getSelectedItemPosition(), 
											((Spinner)d.findViewById(R.id.spinner1))
											.getSelectedItemPosition(), 
											((EditText)d.findViewById(R.id.editText1))
											.getText().toString());
							d.dismiss();
						}
					});
				}
			});
			linear.addView(n, linear.getChildCount());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean createImoseEvent(String pinNumber) {
		try {
			Button n = createEventButton(context, linear, "imosebtn", pinNumber);
			n.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					final Dialog d = createDialog("Arduino pin config", 
							R.layout.arduino_layout);
					((Button)d.findViewById(R.id.button2))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							d.dismiss();
						}
					});
					((Button)d.findViewById(R.id.button1))
					.setOnClickListener(new OnClickListener() {						
						@Override
						public void onClick(View v) {
							ILOGIC.arduinoPinStatus.add(String.valueOf(((Spinner)d.findViewById(R.id.spinner2))
									.getSelectedItemPosition()));
							d.dismiss();
						}
					});
				}
			});
			linear.addView(n, linear.getChildCount());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public  Dialog createDialog(String title, int layout){
		Dialog dialog = new Dialog(context);
    	dialog.setTitle(title);
    	dialog.setCancelable(true);
    	dialog.setContentView(layout);
    	WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    	lp.copyFrom(dialog.getWindow().getAttributes());
    	dialog.show();
    	dialog.getWindow().setAttributes(lp);
    	return dialog;
	}
	
	public ImageButton createEventIcon(Context c, LinearLayout lo, String tag) {
		IDatabase iDatabase = new IDatabase(c);
    	ImageButton n = new ImageButton(c);
		n.setTag(tag);
		n.setPadding(5, 5, 5, 5);
		n.setLayoutParams(new LinearLayout.LayoutParams(85, 85));
		n.setId(777+lo.getChildCount() + 1);
		n.setLongClickable(true);
		n.setBackgroundResource(iDatabase.getResourceImage(tag));
		n.setOnLongClickListener(longClick);
		return n;
	}
	
	public Button createEventButton(Context c, LinearLayout lo, String tag, String arg0) {
		IDatabase iDatabase = new IDatabase(c);
    	Button n = new Button(c);
    	n.setTag(tag);
    	n.setText(arg0);
		n.setPadding(5, 5, 5, 5);
		n.setLayoutParams(new LinearLayout.LayoutParams(85, 85));
		n.setId(777+lo.getChildCount() + 1);
		n.setLongClickable(true);
		n.setBackgroundResource(iDatabase.getResourceImage(tag));
		n.setOnLongClickListener(longClick);
		return n;
	}
	
	public  OnLongClickListener longClick=new OnLongClickListener() {
		
    	public boolean onLongClick(View v) {
    		show_alert(v);
    		return false;
    	}
    };
    
    public void show_alert(final View v){
    	AlertDialog.Builder builder=new AlertDialog.Builder(context);
    	builder.setTitle("DELETE");
    	builder.setMessage("Are you sure you want to delete?");
    	builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
    		
    		public void onClick(DialogInterface dialog, int which) {
    			linear.removeView(v);
			}
    	});
    	builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
    		
    		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
    		}
    	});
    	AlertDialog ad=builder.show();
    }
    
    private void checkIfCondition(int var, int sym,
			String val) {
		switch (sym) {
		case 0:
			if(ILOGIC.varval.get(var) == Float.valueOf(val)){
				ILOGIC.ifList.add("1");
			}else{
				ILOGIC.ifList.add("0");
			}
			break;
		case 1:
			if(ILOGIC.varval.get(var) < Float.valueOf(val)){
				ILOGIC.ifList.add("1");
			}else{
				ILOGIC.ifList.add("1");
			}
			break;
		case 2:
			if(ILOGIC.varval.get(var) > Float.valueOf(val)){
				ILOGIC.ifList.add("1");
			}else{
				ILOGIC.ifList.add("1");
			}
			break;
		case 3:
			if(ILOGIC.varval.get(var) != Float.valueOf(val)){
				ILOGIC.ifList.add("1");
			}else{
				ILOGIC.ifList.add("1");
			}
			break;
		case 4:
			if(ILOGIC.varval.get(var) <= Float.valueOf(val)){
				ILOGIC.ifList.add("1");
			}else{
				ILOGIC.ifList.add("1");
			}
			break;
		case 5:
			if(ILOGIC.varval.get(var) >= Float.valueOf(val)){
				ILOGIC.ifList.add("1");
			}else{
				ILOGIC.ifList.add("1");
			}
			break;			
		default:
			break;
		}
	}
}
