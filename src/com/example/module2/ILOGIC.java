package com.example.module2;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import android.net.wifi.WifiManager;

import com.example.module2.DeviceListActivity;
import com.example.module2.MyService;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class ILOGIC extends Activity {

	private static final String TAG = "ILOGIC";
    private static final boolean D = true;
    public int DATA_RECEPTION = 0;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";   
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";    
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;    
    private EditText mOutEditText;
    private String mConnectedDeviceName = null;    
    private StringBuffer mOutStringBuffer;  
    private BluetoothAdapter mBluetoothAdapter = null;   
    private WifiManager wManager = null;
    private MyService mChatService = null;	
    private IDatabase iDataBase;
    Notifier notifier;
    EventCreater ec;
    private String LAST_SAVED_STATE = "";
    private static final String DATABASE_NAME = "iDatabase";
	private static final String TABLE_SAVED_FILES = "saved_files";
	private static final String KEY_ID = "_id";
	private static final String KEY_NAME = "name";
	private static final String KEY_DATE = "Date";
	private static final String KEY_ENTRIES = "Entries";
	private static final String KEY_ATTRIBUTES = "Attributes";

    LinearLayout linear;
    public static ArrayList<String> varlist;
    ArrayAdapter<String> varnmAdapter;
    public static ArrayList<Float> varval;
    public static ArrayList<String> varevents;
    //////////EVENT RELATED VARIABLES///////////
    int pinStatusCounter = 0;
    public static ArrayList<String> arduinoPinStatus;    
    int delayCounter = 0;
    public static ArrayList<String> delay;
    int alertCounter = 0;
    public static ArrayList<String> alertList;
    public static ArrayList<String> alertSeverityList;
    int smsCounter = 0;
    public static ArrayList<String> smsNumberList;
    public static ArrayList<String> smsMsgList;
    int callCounter = 0;
    public static ArrayList<String> callList;
    int repeatCounter = 0;
    public static ArrayList<String> repeatList;
    int ifCounter = 0;
    public static ArrayList<String> ifList;
    ArrayList<String> display_list;
    ArrayList<String> value_list;
    ArrayList<String> attrs_list;
    ArrayAdapter<String> loadFileAdapter;
    
    ///////////////////////////////////////////////    
    int tb_status = 0; 		//1=visible, 0=not visible
    int menu_status = 0;	//1=visible, 0=not visible
    int lasteventadded = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ilogic); 
        
        linear = (LinearLayout) findViewById(R.id.linear);
        varlist = new ArrayList<String>();
        varnmAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, varlist);
        varval = new ArrayList<Float>();
        varevents = new ArrayList<String>();
        
        notifier = new Notifier();
        ec = new EventCreater(ILOGIC.this, linear);
        iDataBase = new IDatabase(ILOGIC.this);     
        iDataBase.initResources();
        arduinoPinStatus = new ArrayList<String>();
        delay = new ArrayList<String>();
        alertList = new ArrayList<String>();
        alertSeverityList = new ArrayList<String>();
        smsNumberList = new ArrayList<String>();
        smsMsgList = new ArrayList<String>();
        callList = new ArrayList<String>();
        repeatList = new ArrayList<String>();
        ifList = new ArrayList<String>();
        
        final Button waitbtn = (Button)findViewById(R.id.button3);	
        final Button alertbtn = (Button)findViewById(R.id.button4);	
        final Button ifbtn = (Button)findViewById(R.id.button1);	
        final Button repeatbtn = (Button)findViewById(R.id.button2);
        final Button smsbtn = (Button)findViewById(R.id.button5);	
        final Button callbtn = (Button)findViewById(R.id.button6);	
        final Button arduinobtn = (Button)findViewById(R.id.button7);
        final TextView toolbar = (TextView) findViewById(R.id.textView1);
        final TextView menubar = (TextView) findViewById(R.id.textView2);
        final Button backbtn1 = (Button) findViewById(R.id.button22);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		wManager = (WifiManager)ILOGIC.this.getSystemService(Context.WIFI_SERVICE);
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        setupLayout();
        
        //////////BLUETOOTH BUTTON///////////////
        ((Button)findViewById(R.id.btbtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!mBluetoothAdapter.isEnabled()) {
					Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			        v.setBackgroundResource(R.drawable.bluetoothontool);
			        mChatService = new MyService(ILOGIC.this, mHandler);
			    }
				else{
					mBluetoothAdapter.disable();
					v.setBackgroundResource(R.drawable.bluetoothtool);
				}
			}
		});
        
        ////////////////WI-FI BUTTON////////////////////
        ((Button)findViewById(R.id.wifibtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!wManager.isWifiEnabled()){
					wManager.setWifiEnabled(true);
			        v.setBackgroundResource(R.drawable.wifiontool);
				}else{
					wManager.setWifiEnabled(false);
					v.setBackgroundResource(R.drawable.wifitool);
				}
			}
		});
        
        //////////////////SAVE BUTTON//////////////////
        ((Button)findViewById(R.id.savebtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				saveFileEvent();
			}
		});
        //////////////////LOAD BUTTON/////////////////
        ((Button)findViewById(R.id.loadbtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				display_list = new ArrayList<String>();
				value_list = new ArrayList<String>();
				attrs_list = new ArrayList<String>();
				loadFileAdapter = new ArrayAdapter<String>(ILOGIC.this, 
						android.R.layout.simple_list_item_1, display_list);
				String selectQuery = "SELECT  * FROM " + TABLE_SAVED_FILES;
				SQLiteDatabase db = iDataBase.getWritableDatabase();
				Cursor cursor = db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					do {
						int id = Integer.parseInt(cursor.getString(0));
						String name = cursor.getString(1);
						String date = cursor.getString(2);
        				String entries = cursor.getString(3);
        				String attrs = cursor.getString(4);
						// Adding contact to list
						display_list.add(id +"\r\r"+ name +"\r\n"+ date);
						loadFileAdapter.notifyDataSetChanged();
						value_list.add(entries);
						attrs_list.add(attrs);
					} while (cursor.moveToNext());
				}
				db.close();
				final Dialog d = ec.createDialog("Load File", R.layout.loadfiles);
				((ListView)d.findViewById(R.id.listView1))
				.setAdapter(loadFileAdapter);
				((ListView)d.findViewById(R.id.listView1))
				.setOnItemLongClickListener(loadfiledeleteListener);
				((ListView)d.findViewById(R.id.listView1))
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int pos, long arg3) {
						linear = (LinearLayout) findViewById(R.id.linear);
						ec = new EventCreater(ILOGIC.this, linear);
						linear.removeAllViews();
						String[] tempData = value_list.get(pos).split("\r\n");
						loadLayout(tempData);						
						//getAllAttributes(pos);
						d.dismiss();
					}				
					
				});
				((Button)d.findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						d.dismiss();
					}
				});
			}
		});
        //////////////////CLOSE BUTTON/////////////////
        ((Button)findViewById(R.id.closebtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String saveData = "";
				for(int i=0; i<linear.getChildCount(); i++){
					if(linear.getChildAt(i).getTag().toString().equals("arduinopin")){
						saveData = saveData+linear.getChildAt(i).getTag().toString()
								+((Button) linear.getChildAt(i)).getText()+"\r\n";
					}else{
						saveData = saveData+linear.getChildAt(i).getTag().toString()+"\r\n";
					}
				}
				
				if (!saveData.equals(LAST_SAVED_STATE) && linear.getChildCount()!=0) {
					final Dialog d = ec.createDialog("Contents not saved!", R.layout.closewindow);
					((Button)d.findViewById(R.id.button3)).setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							d.dismiss();
						}
					});
					((Button)d.findViewById(R.id.button2)).setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							finish();
							System.exit(0);
						}
					});
					((Button)d.findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							saveFileEvent();
							finish();
							System.exit(0);
						}
					});
				}else{
					finish();
					System.exit(0);
				}
				
			}
		});
        
        ////////////////////CREATE VARIABLE BUTTON//////////
        ((Button)findViewById(R.id.nvar)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createNewVariable();
			}
		});
        
        /////////////////////CLEAR LAYOUT BUTTON////////////
        ((Button)findViewById(R.id.button23)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clearLayout();
			}
		});
        
        ////////////////////MINIMIZE BUTTON//////////////////
        ((Button)findViewById(R.id.minbtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent m = new Intent();
		        m.setAction(Intent.ACTION_MAIN);
		        m.addCategory(Intent.CATEGORY_HOME);
		        ILOGIC.this.startActivity(m);
			}
		});
        
        /////////////////SMS BUTTON/////////////
        smsbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!ec.createSmsEvent() && D){
					Notifier.notifyUser(getApplicationContext(), "Failed", Notifier.SHORT_TERM);
				}
			}
		});
        
        ////////////IF BUTTON/////////////////
        ifbtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(!ec.createIfEvent() && D){
					Notifier.notifyUser(getApplicationContext(), "Failed", Notifier.SHORT_TERM);
				}			}
		});
        
        //////////Arduino toolbar back button
        backbtn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				((ScrollView)findViewById(R.id.scrollView2)).setVisibility(View.GONE);
			}
		});
        ///////////////////TOOLBAR AND MENUBAR//////////////////////////////
        
        toolbar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				if(tb_status == 1){
					((ScrollView)findViewById(R.id.scrollView1)).setVisibility(View.GONE);
					((ScrollView)findViewById(R.id.scrollView2)).setVisibility(View.GONE);
			        tb_status = 0;
				}
				else if(tb_status ==0) {
			        ((ScrollView)findViewById(R.id.scrollView1)).setVisibility(View.VISIBLE);
			        tb_status = 1;
				}
			}
		});
        
        menubar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(menu_status == 1){
					((HorizontalScrollView)findViewById(R.id.horizontalScrollView1)).setVisibility(View.GONE);
					menu_status = 0;
				}
				else if(menu_status ==0) {
			        ((HorizontalScrollView)findViewById(R.id.horizontalScrollView1)).setVisibility(View.VISIBLE);
			        menu_status = 1;
				}
			}
		});
        
        ///////////////////ARDUINO BUTTONS//////////
        arduinobtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((ScrollView)findViewById(R.id.scrollView2)).setVisibility(View.VISIBLE);
			}
		});
        for (int i=1; i<((LinearLayout)((ScrollView)findViewById(R.id.scrollView2))
        		.getChildAt(0)).getChildCount(); i++) {
        	((LinearLayout)((ScrollView)findViewById(R.id.scrollView2))
            		.getChildAt(0)).getChildAt(i)
            		.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {    				
    				if(!ec.createImoseEvent(((Button)v).getText().toString()) && D){
    					Notifier.notifyUser(getApplicationContext(), "Failed", Notifier.SHORT_TERM);
    				}    				
    			}
    		});;
		}
        ////////////////////WAIT BUTTON////////////
        waitbtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(!ec.createWaitEvent() && D){
					Notifier.notifyUser(getApplicationContext(), "Failed", Notifier.SHORT_TERM);
				}
			}
		});
        
        /////////////////CALL BUTTON/////////////////
        callbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!ec.createCallEvent() && D){
					Notifier.notifyUser(getApplicationContext(), "Failed", Notifier.SHORT_TERM);
				}
			}
		});
        
        //////////////ALERT BUTTON/////////////////
        alertbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!ec.createAlertEvent() && D){
					Notifier.notifyUser(getApplicationContext(), "Failed", Notifier.SHORT_TERM);
				}
			}
		});
        
        ////////////////REPEAT BUTTON//////////////
        repeatbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!ec.createRepeatEvent() && D){
					Notifier.notifyUser(getApplicationContext(), "Failed", Notifier.SHORT_TERM);
				}
			}
		});
    }
    
    private void loadLayout(String[] tempData) {
    	String[] temp = new String[2];
    	for(int i=0; i<tempData.length; i++){
			if(tempData[i].equals("smsbtn")){
				ec.createSmsEvent();
			}else if(tempData[i].equals("waitbtn")){
				ec.createWaitEvent();
			}else if(tempData[i].equals("alertbtn")){
				ec.createAlertEvent();
			}else if(tempData[i].equals("callbtn")){
				ec.createCallEvent();
			}else if(tempData[i].equals("repeatbtn")){
				ec.createRepeatEvent();
			}else if(tempData[i].equals("ifbtn")){
				ec.createIfEvent();
			}else{
				temp = tempData[i].split("\t");
				ec.createImoseEvent(temp[1]);
			}
		}
	}
    
    protected OnItemLongClickListener loadfiledeleteListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
				final int pos, long arg3) {	
			linear = (LinearLayout) findViewById(R.id.linear);
			ec = new EventCreater(ILOGIC.this, linear);
			final Dialog d = ec.createDialog("Delete?", R.layout.deletefile);
			((Button)d.findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SQLiteDatabase db = iDataBase.getWritableDatabase();
					db.delete(TABLE_SAVED_FILES, KEY_ID + " = ?",
							new String[] { String.valueOf(pos+1) });
					display_list.remove(pos);
					loadFileAdapter.notifyDataSetChanged();
					db.close();
					d.dismiss();
				}
			});
			return true;
		}
    	
	};
    
    protected void saveFileEvent(){
    	linear = (LinearLayout) findViewById(R.id.linear);
		ec = new EventCreater(ILOGIC.this, linear);
    	final Dialog d = ec.createDialog("Save As..", R.layout.save_call_repeat_layout);
		((Button)d.findViewById(R.id.button1))
		.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View arg0) {
				String title = ((EditText)d.findViewById(R.id.editText1))
						.getText().toString();
				String saveData = "";
				for(int i=0; i<linear.getChildCount(); i++){
					if(linear.getChildAt(i).getTag().toString().equals("imosebtn")){
						saveData = saveData+linear.getChildAt(i).getTag().toString()+"\t"
								+((Button) linear.getChildAt(i)).getText()+"\r\n";
					}else{
						saveData = saveData+linear.getChildAt(i).getTag().toString()+"\r\n";
					}
				}
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				String date = dateFormat.format(cal.getTime());
				String attributes = setAllAttributes();
				SQLiteDatabase db = iDataBase.getWritableDatabase();
				ContentValues cv = new ContentValues();
				cv.put(KEY_NAME, title);
				cv.put(KEY_DATE, date);
				cv.put(KEY_ENTRIES, saveData);
				cv.put(KEY_ATTRIBUTES, attributes);
				if(db.insert(TABLE_SAVED_FILES, null, cv) < 0){
					notifier.notifyUser(ILOGIC.this, "File save failed!", notifier.SHORT_TERM);
				}else{
					notifier.notifyUser(ILOGIC.this, "File save successfull!", notifier.SHORT_TERM);
					LAST_SAVED_STATE = saveData;
				}
				db.close();
				d.dismiss();
			}
		});
    }

	protected void getAllAttributes(int pos) {
		smsNumberList.clear();
		smsMsgList.clear();
		delay.clear();
		alertList.clear();
		alertSeverityList.clear();
		callList.clear();
		repeatList.clear();
		ifList.clear();
		arduinoPinStatus.clear();
		
		String tempData[] = attrs_list.get(pos).split("\r\n");
		
		if (tempData[0].length() != 0) {
			smsNumberList.addAll(Arrays.asList(tempData[0].split("\t")));
			smsMsgList.addAll(Arrays.asList(tempData[1].split("\t")));
		}
		if (tempData[2].length() != 0) {
			delay.addAll(Arrays.asList(tempData[2].split("\t")));
		}
		if (tempData[3].length() != 0) {
			alertList.addAll(Arrays.asList(tempData[3].split("\t")));
			alertSeverityList.addAll(Arrays.asList(tempData[4].split("\t")));
		}
		if (tempData[5].length() != 0) {
			callList.addAll(Arrays.asList(tempData[5].split("\t")));
		}
		if (tempData[6].length() != 0) {
			repeatList.addAll(Arrays.asList(tempData[6].split("\t")));
		}
		if (tempData[7].length() != 0) {
			ifList.addAll(Arrays.asList(tempData[7].split("\t")));
		}
		if (tempData[8].length() != 0) {
			arduinoPinStatus.addAll(Arrays.asList(tempData[8].split("\t")));
		}
	}

	protected String setAllAttributes() {
		String attributes = "";
		//attributes = attributes + String.valueOf(smsCounter) + "\r\n";				
		for(int i=0; i<smsMsgList.size(); i++){
			attributes = attributes + smsNumberList.get(i) + "\t";		//0
		}
		attributes = attributes + "\r\n";
		for(int i=0; i<smsMsgList.size(); i++){
			attributes = attributes + smsMsgList.get(i) + "\t";			//1
		}
		
		attributes = attributes + "\r\n";
		//attributes = attributes + String.valueOf(delayCounter) + "\r\n";
		for(int i=0; i<delay.size(); i++){
			attributes = attributes + String.valueOf(delay.get(i)) + "\t";	//2
		}
		attributes = attributes + "\r\n";
		//attributes = attributes + String.valueOf(alertCounter) + "\r\n";
		for(int i=0; i<alertList.size(); i++){
			attributes = attributes + alertList.get(i) + "\t";				//3
		}
		attributes = attributes + "\r\n";
		for(int i=0; i<alertSeverityList.size(); i++){
			attributes = attributes + alertSeverityList.get(i) + "\t";		//4
		}
		attributes = attributes + "\r\n";
		//attributes = attributes + String.valueOf(callCounter) + "\r\n";
		for(int i=0; i<callList.size(); i++){
			attributes = attributes + callList.get(i) + "\t";				//5
		}
		attributes = attributes + "\r\n";
		//attributes = attributes + String.valueOf(repeatCounter) + "\r\n";
		for(int i=0; i<repeatList.size(); i++){
			attributes = attributes + repeatList.get(i) + "\t";				//6
		}
		attributes = attributes + "\r\n";
		//attributes = attributes + String.valueOf(ifCounter) + "\r\n";
		for(int i=0; i<ifList.size(); i++){
			attributes = attributes + ifList.get(i) + "\t";					//7
		}
		attributes = attributes + "\r\n";
		for(int i=0; i<arduinoPinStatus.size(); i++){
			attributes = attributes + arduinoPinStatus.get(i) + "\t";		//8				//7
		}
		return attributes;
	}	
    
    private void setupLayout() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled()){
        	((Button)findViewById(R.id.btbtn)).setBackgroundResource(R.drawable.bluetoothontool);
        }else{
        	((Button)findViewById(R.id.btbtn)).setBackgroundResource(R.drawable.bluetoothtool);
        }
		wManager = (WifiManager)ILOGIC.this.getSystemService(Context.WIFI_SERVICE);
		if(wManager.isWifiEnabled()){
			((Button)findViewById(R.id.wifibtn)).setBackgroundResource(R.drawable.wifiontool);
		}else{
			((Button)findViewById(R.id.wifibtn)).setBackgroundResource(R.drawable.wifitool);
		}
		((ScrollView)findViewById(R.id.scrollView1)).setVisibility(View.GONE);
		((ScrollView)findViewById(R.id.scrollView2)).setVisibility(View.GONE);
	}

	private void ensureDiscoverable() {
        
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private void sendMessage(String message) {
        
        if (mChatService.getState() != MyService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

     
        if (message.length() > 0) {
          
            byte[] send = message.getBytes();
            mChatService.write(send);

           
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }
   
    private final Handler mHandler = new Handler() {
    	@Override
        public void handleMessage(Message msg) {
    		switch (msg.what) {              
            case MESSAGE_WRITE:
            	byte[] writeBuf = (byte[]) msg.obj;          
            	String writeMessage = new String(writeBuf);
                break;
                case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);
                ////////////////////////
                ////////YOUR CODE///////
                TextView data = (TextView) findViewById(70707);
                if(DATA_RECEPTION == 1){
                	data.setText(readMessage);
                }
                ////////////////////////
                //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                break;
                case MESSAGE_DEVICE_NAME:
                   
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                                   + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                                   Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        };
        
        
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(D) Log.d(TAG, "onActivityResult " + resultCode);
            switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
              
                if (resultCode == Activity.RESULT_OK) {
                    
                    String address = data.getExtras()
                                         .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                
                if (resultCode == Activity.RESULT_OK) {
                 
                    mChatService = new MyService(this, mHandler);
                } else {
                   
                    
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    
	private void viewvar() {
		linear = (LinearLayout) findViewById(R.id.linear);
		ec = new EventCreater(ILOGIC.this, linear);
		Dialog d = ec.createDialog("Variables", R.layout.viewvars);
		for(int i=0; i<varlist.size(); i++){
			TextView t1 = new TextView(ILOGIC.this);
			t1.setText(varlist.get(i));
			TextView t2 = new TextView(ILOGIC.this);
			t2.setText(String.valueOf(varval.get(i)));
			((LinearLayout)d.findViewById(R.id.l1)).addView(t1, ((LinearLayout)d.findViewById(R.id.l1)).getChildCount());
			((LinearLayout)d.findViewById(R.id.l2)).addView(t2, ((LinearLayout)d.findViewById(R.id.l2)).getChildCount());
		}
    }
   
    @Override
    public void onPause(){
    	super.onPause();
    	LinearLayout lo = (LinearLayout) findViewById(R.id.linear);
        lo.removeAllViews();
        for(int i=0; i<varlist.size(); i++){
        	varlist.remove(i);
        }
        for(int i=0; i<varval.size(); i++){
        	varval.remove(i);
        }
        for(int i=0; i<varevents.size(); i++){
        	varevents.remove(i);
        }
    }
   
    @Override
    public void onResume() {
        super.onResume();
        LinearLayout lo = (LinearLayout) findViewById(R.id.linear);
        lo.removeAllViews();
        for(int i=0; i<varlist.size(); i++){
        	varlist.remove(i);
        }
        for(int i=0; i<varval.size(); i++){
        	varval.remove(i);
        }
        for(int i=0; i<varevents.size(); i++){
        	varevents.remove(i);
        }
    }
    	
    private void createNewVariable(){
    	linear = (LinearLayout) findViewById(R.id.linear);
    	ec = new EventCreater(ILOGIC.this, linear);
    	final Dialog d = ec.createDialog("New Variable", R.layout.createvariable);
    	((Button)d.findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				d.dismiss();
			}
		});
    	((Button)d.findViewById(R.id.button2)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				varlist.add(((EditText)d.findViewById(R.id.editText1)).getText().toString());
				varval.add(Float.valueOf(((EditText)d.findViewById(R.id.editText2)).getText().toString()));
				d.dismiss();				
			}
		});
    }    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case R.id.runcode:
    	  runcode();
      break;
      case R.id.viewvariables:
    	  viewvar();
      break;
      case R.id.scan:
          Intent serverIntent = new Intent(ILOGIC.this, DeviceListActivity.class);
          startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
          return true;
      case R.id.discoverable:
          ensureDiscoverable();
          return true;
      }
      return super.onOptionsItemSelected(item);
    }

    private void clearLayout() {
		linear = (LinearLayout) findViewById(R.id.linear);
		linear.removeAllViews();
		
	}

	private void runcode() {
		
    	LinearLayout lo = (LinearLayout) findViewById(R.id.linear);
		if(lo.getChildCount() == 0){
			Toast.makeText(ILOGIC.this, "Nothing to run!", Toast.LENGTH_SHORT).show();
		}
		else{			
			executeCode();  
			}
    }
  
    private void executeCode() {
    	linear = (LinearLayout) findViewById(R.id.linear);
		for(int i=0; i<linear.getChildCount(); i++)
		{
			if(linear.getChildAt(i).getTag().toString().equals("wait")){
				wait_called();
			}
    		else if(linear.getChildAt(i).getTag().toString().equals("message")){
    			alert_called();
    		}
    		else if(linear.getChildAt(i).getTag().toString().equals("sms")){
    			sms_called();
    		}
    		else if(linear.getChildAt(i).getTag().toString().equals("if")){
    			if(!if_called()){
    				i++;
    			}
    		}
    		else if(linear.getChildAt(i).getTag().toString().equals("arduinopin")){
    			arduinopin_called((Button) linear.getChildAt(i));
    		}
		}
	}
    
    /////////////EVENT DEFINITIONS////////////////

	private void motor_called(int curr_id1) {
		
	}

	private void arduinopin_called(Button v) {
		if(v.getText().equals("00")){
			sendMessage("A");
		}else if(v.getText().equals("01")){
			sendMessage("B");
		}else if(v.getText().equals("02")){
			sendMessage("C");
		}else if(v.getText().equals("03")){
			sendMessage("D");
		}else if(v.getText().equals("04")){
			sendMessage("E");
		}else if(v.getText().equals("05")){
			sendMessage("F");
		}else if(v.getText().equals("06")){
			sendMessage("G");
		}else if(v.getText().equals("07")){
			sendMessage("H");
		}else if(v.getText().equals("08")){
			sendMessage("I");
		}else if(v.getText().equals("09")){
			sendMessage("J");
		}else if(v.getText().equals("10")){
			sendMessage("K");
		}else if(v.getText().equals("11")){
			sendMessage("L");
		}else if(v.getText().equals("12")){
			sendMessage("M");
		}else if(v.getText().equals("13")){
			sendMessage("N");
		}
	}

	private boolean if_called() {
		boolean tempValue /*= ifList.get(ifCounter)*/ = false;
		ifCounter++;
		return tempValue;
	}

	private void wait_called() {
		long time = System.currentTimeMillis();
		while(System.currentTimeMillis() < time + Long.valueOf(delay.get(delayCounter))){
		}
		delayCounter++;
	}

	private void alert_called() {
		Toast.makeText(ILOGIC.this, alertList.get(alertCounter), Toast.LENGTH_SHORT).show();
		alertCounter++;
	}

	private void sms_called() {
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(smsNumberList.get(smsCounter), null, smsMsgList.get(smsCounter), null, null);
		smsCounter++;
	}

	private void call_called() {
			
	}
	
/////////////////////////////////////////////////
  }  