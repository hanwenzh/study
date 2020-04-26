package com.bscan.XTraderGFZQ;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;

//import com.bscan.notificationlistener.NotificationMonitor.ScreenOffReceiver;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;//����


public class MainActivity extends Activity  implements CompoundButton.OnCheckedChangeListener {
	public static Context gMainContext = null;
	public static WifiManager.MulticastLock wifiLock  =  null;
	
	public static float fAction = 0.0f;
	public static float fAvaMoney = 0.0f;
	//public static float iAction000 = 0;
	public static MyHandler myHandler;
	
	public static TextView tvLog;
	public static CheckBox cbAction;
	
	private static final String ACTION = "action";
	private static final String ACTION_START_ACCESSIBILITY_SETTING = "action_start_accessibility_setting";
	public static final String TAG = "XXOO";
	public static final String TAG_SVR = "SVR: ";

    @SuppressWarnings("deprecation")
    private static KeyguardManager  km;
    static KeyguardLock kl;
    static PowerManager pm;
    static PowerManager.WakeLock wl;
	private ScreenBroadcastReceiver screenEventReceiver;
    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
             
    	gMainContext = this.getApplicationContext();
    	setWifiDormancy(gMainContext);
        this.wakeAndUnlock(true);
		// �������һ��ϵͳ�㲥���ж������ĻϨ��Ͱ�ϵͳ������ԭ
// 	   IntentFilter intentFilter = new IntentFilter();
// 	   intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
// 	   intentFilter.addAction(Intent.ACTION_SCREEN_ON);
// 	   intentFilter.addAction(Intent.ACTION_USER_PRESENT);
// 	   screenEventReceiver = new ScreenBroadcastReceiver();
// 	   registerReceiver(screenEventReceiver, intentFilter);

        
        ((TextView) findViewById(R.id.tvLocalIP)).setText(Html.fromHtml("����IP: <font color='#0000FF'>" + getLocalHostIp() + "</font>"));
        
        cbAction = (CheckBox) findViewById(R.id.cbAct);

        cbAction.setOnCheckedChangeListener(this);
        
        tvLog = (TextView)findViewById(R.id.tvLog);     
        tvLog.setMovementMethod(ScrollingMovementMethod.getInstance());  
        tvLog.setText("");

		MainActivity.setAction(0.0f);
        //showConnected("");

        myHandler = new MyHandler();
        //newSecTimer();
        
        // ������һ���µ�Handlerʵ��ʱ, ����󶨵���ǰ�̺߳���Ϣ�Ķ�����,��ʼ�ַ�����
        // Handler����������, (1) : ��ʱִ��Message��Runnalbe ����
        // (2): ��һ������,�ڲ�ͬ���߳���ִ��.
 
        // ��������Ϣ,�����·���
        // post(Runnable)
        // postAtTime(Runnable,long)
        // postDelayed(Runnable,long)
        // sendEmptyMessage(int)
        // sendMessage(Message);
        // sendMessageAtTime(Message,long)
        // sendMessageDelayed(Message,long)
     
        // ���Ϸ����� post��ͷ�������㴦��Runnable����
        //sendMessage()�����㴦��Message����(Message����԰�������,)

        jumpToSettingPage(gMainContext);

        
        //infomation.append("Ŀ��IP�� "+SERVER_IP+"\n"+"Ŀ�Ķ˿ڣ� "+SERVER_PORT+"\n");
        //infomation.append("���ض˿ڣ� " +LOCAL_PORT);
    }

    protected void newSecTimer()
    {
    	// run in a second
    	// ÿһ����ִ��һ��
    	final long timeInterval = 1000;
    	Runnable runnable = new Runnable() {
    		public void run() {
    			while (true) {
    				// ------- code for task to run
    				// ------- Ҫ���е��������
    				System.out.println("Hello, Timer...");

    				// Date d = new Date();
    				// if((d.getHours() == 9) && (d.getMinutes() == 10)){
    				// return d;
    				// }else{
    				// return null;
    				// }
    				// ------- ends here
    				try {
    					// sleep()��ͬ���ӳ����ݣ����һ������߳�
    					Thread.sleep(timeInterval);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    	};
    	// ������ʱ��
    	Thread thread = new Thread(runnable);
    	// ��ʼִ��
    	thread.start();
    }
 
    public static String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // �������õ�����ӿ�
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// �õ�ÿһ������ӿڰ󶨵�����ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // ����ÿһ���ӿڰ󶨵�����ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                            .getHostAddress())) {
                        return ip.getHostAddress();
                    }
                }
            }
        }
        catch(SocketException e)
        {
                Log.e("feige", "��ȡ����ip��ַʧ��");
                e.printStackTrace();
        }
        return ipaddress;
    }

    /**
     * �ж��Ƿ�Ϊ���֣������������
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Boolean flag = false;
        String tmp;
        if((str.length() > 0)){
            if(str.startsWith("-")){
                tmp = str.substring(1);
            }else{
                tmp = str;
            }
            
          //�����������ʽ�ķ�ʽ���ж�һ���ַ����Ƿ�Ϊ���֣����ַ�ʽ�ж���Ƚ�ȫ  
            //�����ж�����������С��  
            //?:0��1��, *:0����, +:1����  
            //Boolean strResult = str.matches("-?[0-9]+.*[0-9]*");  
            flag = tmp.matches("-?[0-9]+.*[0-9]*");
        }
        return flag;
    }

    public static void updateData2MainUI(String rcvstr) {
    	Message msg = new Message();
    	Bundle bundle = new Bundle();

    	// �����ݷŵ�buddle��
    	bundle.putString("receive", rcvstr);
    	// ��buddle���ݵ�message
    	msg.setData(bundle);
    	MainActivity.myHandler.sendMessage(msg); // ��Handler������Ϣ,����UI
    	
    }


    /**
    * ������Ϣ,������Ϣ ,��Handler���뵱ǰ���߳�һ������
    * */

    class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }


        // ���������д�˷���,��������
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
			super.handleMessage(msg);

			String rcvstr = msg.getData().getString("receive");
			// Log.d(TAG, "handleMessage......" + rcvstr);
			
			if (MainActivity.isNumeric(rcvstr)) {
				Log.w(TAG, "handleMessage...ָ��=" + rcvstr);
				//Log.d("XXOO", "rcvedTCP......" + data + " ��ǰָ��=" + MainActivity.fAction);

		        showLog("handleMessage...�յ�ָ��=" + rcvstr);
		        
		        soundAlarm(RingtoneManager.TYPE_NOTIFICATION); 
                
				if (((CheckBox) findViewById(R.id.cbAct)).isChecked()) {
					MainActivity.this.ActionByStrCMD(rcvstr);
				}
				else
				{
					MainActivity.setAction(0.0f);
			        Toast.makeText(MainActivity.this,"�յ�BSָ���������ѡ���ж���ѡ�򣡣���",Toast.LENGTH_LONG).show();

				}

			}
			else if(rcvstr.equalsIgnoreCase("Connected"))
			{
				//public static void showConnected(String sIP) {
					((RadioButton) findViewById(R.id.svrIP)).setChecked(true);
			        //((RadioButton) findViewById(R.id.svrIP)).setText(rcvstr);
				//}
			        //showLog(rcvstr);
				
			}
			else
			{
				if(rcvstr.contains(TAG_SVR)) {
					((RadioButton) findViewById(R.id.svrIP)).setChecked(false);
			        ((RadioButton) findViewById(R.id.svrIP)).setText(Html.fromHtml(rcvstr));
				}
			    
		        //Toast.makeText(MainActivity.this, rcvstr, Toast.LENGTH_LONG).show();
//		        Toast toast=Toast.makeText(MainActivity.this,rcvstr, Toast.LENGTH_SHORT);
//		        toast.setGravity(Gravity.CENTER, 0, 0);
//		        toast.show();


				showLog(rcvstr);
			}
				


            // �˴����Ը���UI
            //Bundle b = msg.getData();
//            String color = b.getString("color");
//            MainActivity.this.button.append(color);

        }

    }

		private static void showLog(String rcvstr) {
			SimpleDateFormat   formatter   =   new   SimpleDateFormat   (/*"yyyy-MM-dd "+ */"HH:mm:ss.SSS");
	        Date curDate =  new Date(System.currentTimeMillis());
	        //��ȡ��ǰʱ��
	        String   str   =   formatter.format(curDate);
	        
	        //((TextView) findViewById(R.id.tvLocalIP)).setText(Html.fromHtml("����IP: <font color='#0000FF'>" + getLocalHostIp() + "</font>"));

	        //tvLog.append(str + "   " + Html.fromHtml(rcvstr).toString() + "\n");//ȥ��<***/>
	        //Html.fromHtml("<font color='#0000FF'>" + str + "</font>")
	        tvLog.append(Html.fromHtml("<font color='#0000FF'>" + str + "</font>" + "   " + Html.fromHtml(rcvstr).toString() + "<br>"));//ȥ��<***/>
	        
	        tvLog.post(new Runnable() {
	        	 
	            @Override
	            public void run() {
	                //int lineCount = tvLog.getLineCount();
	                //bug: getLineCount() wrong value; need be fixed later...
	    	        Log.w(TAG, " getLineCount=" + tvLog.getLineCount()
	    	        		+ " getLineHeight=" + tvLog.getLineHeight()
	    	        		+ " getHeight=" + tvLog.getHeight());
	    	        
	    	        int offset=tvLog.getLineCount()*tvLog.getLineHeight();
	    	        if(offset>tvLog.getHeight()){
	    	        	tvLog.scrollTo(0,offset-tvLog.getHeight());
	    	        }

	            }
	        });
		}
		
    public static void setAction(float fff) {
			// TODO Auto-generated method stub
    	MainActivity.fAction = fff;
    	cbAction.setText("" + MainActivity.fAction);
    	showLog("[����ָ��Ϊ] --> " + MainActivity.fAction);
    	showLog(Thread.currentThread().getName() + " setAction---fAct=" + MainActivity.fAction);
		
		}

	/**
	 * ��⸨�������Ƿ���<br>
	 * �� �� ����isAccessibilitySettingsOn <br>
	 * �� �� �� <br>
	 * ����ʱ�䣺2016-6-22 ����2:29:24 <br>
	 * �� �� �ˣ� <br>
	 * �޸����ڣ� <br>
	 * @param mContext
	 * @return boolean
	 */
	private static boolean isAccessibilitySettingsOn(Context mContext) {
		int accessibilityEnabled = 0;
		// TestServiceΪ��Ӧ�ķ���
		final String service = mContext.getPackageName() + "/" + ".MyAccessibilityService"/*MyAccessibilityService.class.getCanonicalName()*/;
		Log.i(TAG, "service:" + service);
		// com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
		try {
			accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
					android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
			Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
		} catch (Settings.SettingNotFoundException e) {
			Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
		}
		TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
 
		if (accessibilityEnabled == 1) {
			Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
			String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
					Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
			// com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
			if (settingValue != null) {
				mStringColonSplitter.setString(settingValue);
				while (mStringColonSplitter.hasNext()) {
					String accessibilityService = mStringColonSplitter.next();
 
					Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
					if (accessibilityService.equalsIgnoreCase(service)) {
						Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
						return true;
					}
				}
			}
		} else {
			Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
		}
		return false;
	}
	
	/**
     * �ж�AccessibilityService�����Ƿ��Ѿ�����
     * @param context
     * @param name
     * @return
     */
    public static boolean isStartAccessibilityService(Context context, String name){
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : serviceInfos) {
            String id = info.getId();
            Log.v(TAG, "all -->" + id);
            if (id.contains(name)) {
                return true;
            }
        }
        return false;
    }
    /**
     * �ж����ϰ������Ƿ���
     *
     * @param context
     * @return
     */
    private static boolean isStartAccessibilityServiceEnable(Context context) {
        AccessibilityManager accessibilityManager =
                (AccessibilityManager)         
                 context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        assert accessibilityManager != null;
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(
                        AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().contains(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

	public static boolean isAccessibilityEnabled(Context context, String id) {

		AccessibilityManager am = (AccessibilityManager) context
				.getSystemService(Context.ACCESSIBILITY_SERVICE);

		List<AccessibilityServiceInfo> runningServices = am
				.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
		for (AccessibilityServiceInfo service : runningServices) {
			if (id.equals(service.getId())) {
				return true;
			}
		}

		return false;
	}
	
    public static void jumpToSettingPage(Context context) {
            try {
//        		WifiManager manager = (WifiManager) gMainContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        		wifiLock  = manager.createMulticastLock("localWifi");
        		
//                Intent intent = new Intent(context,  MainActivity.class);
//                intent.putExtra(ACTION, ACTION_START_ACCESSIBILITY_SETTING);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);

//            	isAccessibilityEnabled(gMainContext, "mmm");
            	if (!isAccessibilitySettingsOn(gMainContext)) {
//        			Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//        			startActivity(intent);
        			
	            	Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
	            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	            	intent.putExtra(ACTION, ACTION_START_ACCESSIBILITY_SETTING);
	            	context.startActivity(intent);
        		}
            	
            	} catch (Exception ignore) {
            		int xxx=999;
            	}
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
    
    public void click(View v){
        Toast.makeText(MainActivity.this,((Button)findViewById(v.getId())).getText().toString(),Toast.LENGTH_LONG).show();
        String sAction = ((Button)v).getText().toString();
        
        ActionByStrCMD(sAction);
        
    }

	public void ActionByStrCMD(String sAction) {
		MainActivity.setAction(Float.parseFloat(sAction));
        Log.i("XXOO", fAction + " -> 		click..." );
        
		// ������Ļ���⿪������������ԭ���ǰ������رյ�
		this.wakeAndUnlock(true);
        //runHAZQapp();
//		try {
//			openApp("com.gfjgj.dzh");
//		} catch (NameNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		PowerManager powerManager = (PowerManager) MainActivity.gMainContext.getSystemService(Context.POWER_SERVICE);
		
		boolean ifOpen = false;
		
		while(!ifOpen)
		{
			try {
		    	showLog(Thread.currentThread().getName() + "==[sleep(1000)]");
		        Log.i("XXOO", "Thread.sleep(1000)..[" );
				Thread.sleep(1000);
		        Log.i("XXOO", "Thread.sleep(1000)..]" );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ifOpen = powerManager.isScreenOn(); 
		}

        runTradeApp();
        
        ActivityManager mAm = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        String activity_name = mAm.getRunningTasks(1).get(0).topActivity.getClassName();
        Log.i("XXOO", "->"+activity_name );

		//playAudio(this, 10);

	}

	public void setWifiDormancy(Context context){
		int value = Settings.System.getInt(context.getContentResolver(), Settings.System.WIFI_SLEEP_POLICY,  Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
		Log.d(TAG, "setWifiDormancy() returned: " + value);
		final SharedPreferences prefs = context.getSharedPreferences("wifi_sleep_policy", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(Settings.System.WIFI_SLEEP_POLICY, value);
		editor.commit();
 
		if(Settings.System.WIFI_SLEEP_POLICY_NEVER != value){
			Settings.System.putInt(context.getContentResolver(), Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_NEVER);
		}
	}
	
	class ScreenBroadcastReceiver extends BroadcastReceiver {
		 private String action=null;

		  @Override
		  public void onReceive(Context context, Intent intent) {
//			   if (kl != null) {
//				    // ��ԭ����
//		               //����
//		               //kl.reenableKeyguard();
//		          }
//			   if (wl != null && wl.isHeld()) {
//				    // ��ԭ����
//		               //�ͷ�wakeLock���ص�
//		               wl.release();
//				   }
			   
		        action=intent.getAction();
		        if(Intent.ACTION_SCREEN_ON.equals(action)){
					Log.e(TAG, "***��Ļ*** -----------����------");
		            //Toast.makeText(context,"��Ļ����",Toast.LENGTH_SHORT).show();
		            //runHAZQapp();
		 
		        }else if(Intent.ACTION_SCREEN_OFF.equals(action)){
					Log.e(TAG, "***��Ļ*** -----------��------");
		            //Toast.makeText(context,"��Ļ����",Toast.LENGTH_SHORT).show();
		        }else if(Intent.ACTION_USER_PRESENT.equals(action)){
					Log.e(TAG, "****** ---------����--------");
		            //Toast.makeText(context,"��Ļ����",Toast.LENGTH_SHORT).show();
		            
		            //runHAZQapp();
		        }

		  }
		 }
	 
		void openApp(String packageName) throws NameNotFoundException {
			PackageInfo pi = getPackageManager().getPackageInfo(packageName, 0);

			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);

			List<ResolveInfo> apps = getPackageManager().queryIntentActivities(resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String pkgName = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);

				ComponentName cn = new ComponentName(pkgName, className);

				intent.setComponent(cn);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
//				if(pkgName.contains("com.tmall.wireless"))
//		        {
//					bAutoOpen = true;
//		        }
			}
		}

	
	  //�������������
		private void wakeAndUnlock(boolean b)
	    {
	           if(b)
	           {
	                  //��ȡ��Դ����������
	                  pm=(PowerManager) getSystemService(Context.POWER_SERVICE);
	     
	                  //��ȡPowerManager.WakeLock���󣬺���Ĳ���|��ʾͬʱ��������ֵ�������ǵ����õ�Tag
	                  wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
	          		  wl.setReferenceCounted(false); 
	     
	                  //������Ļ
	                  wl.acquire();
	                 
	                  //�õ�����������������
	                  km= (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
	                  kl = km.newKeyguardLock("unLock");
	     
	                  //����
	                  kl.disableKeyguard();
	           }
	           else
	           {
	                  //����
	                  //kl.reenableKeyguard();
	                 
	                  //�ͷ�wakeLock���ص�
	                  wl.release();
	           }
	          
	    }

	public void runTradeApp() {
//		PackageManager packageManager = this.getPackageManager();   
//        Intent intent = packageManager.getLaunchIntentForPackage("com.gfjgj.dzh");
//        startActivity(intent);
   
    	showLog("[����] --> " + "com.gfjgj.dzh");

		Intent intent = getPackageManager().getLaunchIntentForPackage("com.gfjgj.dzh");
		startActivity(intent);
		
//        Intent i = new Intent("com.gfjgj.dzh");
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(i);
		
//		Intent i = new Intent(); 
//        ComponentName cn = new ComponentName("com.gfjgj.dzh", 
//                  "com.android.dazhihui.ui.screen.stock.MainScreen"); 
//        i.setComponent(cn); 
//        i.setAction("android.intent.action.MAIN"); 
//        startActivity(i);
		
//        startActivityForResult(i, RESULT_OK);
	}
	
    @Override
    protected void onDestroy() {  
    super.onDestroy();
    }
    
    /**

     * ��ȡ��Դ�������ָ÷�������ĻϨ��ʱ��Ȼ��ȡCPUʱ����������

     */

    static public WakeLock wakeLock;
//    static public void acquireWakeLock(){
//
////    	if (null != wifiLock) wifiLock.acquire();	
//    	if (null == wakeLock) {
//
//    		PowerManager pm = (PowerManager) gMainContext.getSystemService(Context.POWER_SERVICE);
//
//    		wakeLock = pm.newWakeLock(
//    				PowerManager.PARTIAL_WAKE_LOCK 
//    				| PowerManager.ON_AFTER_RELEASE
//    				//| PowerManager.FULL_WAKE_LOCK
//    				, gMainContext.getClass()
//
//    				.getCanonicalName());
//			Log.i("XXOO", "call acquireWakeLock = " + wakeLock);
//
//    		if (null != wakeLock) {
//
//
//    			wakeLock.acquire();
//
//    		}
//
//    	}
//
//    }
//
//    // �ͷ��豸��Դ��
//
//    static public void releaseWakeLock() {
//
////    	if (null != wifiLock) wifiLock.release();//�������
//    	if (null != wakeLock && wakeLock.isHeld()) {
//
//    		Log.i(TAG, "call releaseWakeLock");
//
//    		wakeLock.release();
//
//    		wakeLock = null;
//
//    	}
//
//    }

    //	WakeLock �����Լ�˵����
    //
    //	PARTIAL_WAKE_LOCK:����CPU ��ת����Ļ�ͼ��̵��п����ǹرյġ�
    //
    //	SCREEN_DIM_WAKE_LOCK������CPU ��ת������������Ļ��ʾ���п����ǻҵģ������رռ��̵�
    //
    //	SCREEN_BRIGHT_WAKE_LOCK������CPU ��ת������������Ļ������ʾ�������رռ��̵�
    //
    //	FULL_WAKE_LOCK������CPU ��ת��������Ļ������ʾ�����̵�Ҳ��������
    //
    //	ACQUIRE_CAUSES_WAKEUP��ǿ��ʹ��Ļ������������Ҫ���һЩ����֪ͨ�û��Ĳ���.
    //
    //	ON_AFTER_RELEASE���������ͷ�ʱ��������Ļ����һ��ʱ��
    //
    //	��� AndroidManifest.xml ����Ȩ�ޣ�
    //
    //
    //
    
    /**
     * ����ѡ���״̬�����仯ʱ�Զ����õķ���
     * @param buttonView �¼�Դ
     * @param isChecked ��ǰ��ѡ���Ƿ�ѡ��, true:ѡ��,false ��ѡ��
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            //�û�����ĸ�ѡ�������
            String text=buttonView.getText().toString();
            System.out.println("text="+text+",isChecked="+isChecked);
            System.out.println("��ǰ�ؼ�����ѡ��״̬");
        }else{
            System.out.println("��ǰ�ؼ�ȡ����ѡ��״̬");
    		MainActivity.setAction(0.0f);
        }
 
    }

	public static void soundAlarm(int iSound) {
		Uri notification = RingtoneManager.getDefaultUri(iSound);  
		if((null != notification) && (null != gMainContext ))
		{
			Ringtone r = RingtoneManager.getRingtone(gMainContext, notification);  
			if(null != r)
				r.play();
		}
	}
}
