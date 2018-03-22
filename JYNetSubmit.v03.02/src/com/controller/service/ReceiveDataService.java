package com.controller.service;
import com.example.jynetsubmit.R;
import com.model.tool.common.MTConfiger;
import com.model.tool.common.MTGetOrPostHelper;
import com.model.tool.common.MTSharedPreference;
//import com.model.tool.decoding.FinishListener;
//import com.view.VAddActivity;

import android.app.Notification;
import android.app.NotificationManager;
//import android.app.PendingIntent;
import android.app.Service;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
//import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;

public class ReceiveDataService extends Service {
	private Context				 mContext;
	private boolean    		 	 threadDisable;
	private int 	   		 	 count;
	private int 				 sleeptime=8000;
	//	进行数据处理的线程;
	//	01.心跳检测线程;
	private CountTread		 	 countTread;
	//	02.发送检测线程;
	private RequestThread		 requestThread;
	//	网络的帮助类;
	private MTGetOrPostHelper	 mtGetOrPostHelper;
	
	//	向服务发送服务的链接;
	private String   		 	 pTargetToService="com.from.activity.to.service";
	//	从服务获得服务的链接;
	private String   		 	 pTargetFromService="com.from.service.to.activity";
	private GetBroadCastReceiver getBroadCastReceiver;
	private IntentFilter 	 	 filter;	
	//	模式识别;
	private MTSharedPreference	mSPrefer; 
	
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
		mContext	 = getApplication();
		//	发送包的内容;
		getBroadCastReceiver=new GetBroadCastReceiver();
		filter 		 = new IntentFilter();
		
		filter.addAction(pTargetToService);
		registerReceiver(getBroadCastReceiver, filter);
		//	模式识别;
		mSPrefer=new MTSharedPreference(mContext, "receive_mode", MODE_PRIVATE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	//	01.计数的线程;
	public class CountTread extends Thread{
		@Override
		public void run() {
			while (threadDisable) {
				try {
					count++;
					Intent  intent=new Intent();
					Bundle	bundle=new Bundle();
					bundle.putInt("count", count);
					intent.putExtras(bundle);//
					intent.setAction(pTargetFromService);//
					sendBroadcast(intent);
					Thread.sleep(sleeptime);
				} catch (InterruptedException e) {

				}
			}
		}
	}
	//	02.问询的线程;
	public class RequestThread extends Thread{
		private MTGetOrPostHelper mtGetOrPostHelper;
		
		public RequestThread(MTGetOrPostHelper mtGetOrPostHelper) {
			this.mtGetOrPostHelper=mtGetOrPostHelper;
		}
		@Override
		public void run() {
			while (threadDisable) {
				try {
					//	问询方法;
					dorequest();
					Thread.sleep(sleeptime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
				
		}
		
		private void dorequest(){
//			Log.i("MyLog", "开启后台服务");
			String 	url 	=	null;
			String 	param	=	null;
			String 	response= 	"-1";
			url	  			= 	"http://"+MTConfiger.IP+":"+MTConfiger.PORT+"/Mine/app/thelast";
			param 			=	" ";
			response  		= 	mtGetOrPostHelper.sendGet(url, param);
			String  result	=	response.trim();
//			Log.i("MyLog", "res="+result);
			if(!result.equals("")){
				int nresult	=	-1;
				try {					
					nresult	=	Integer.parseInt(result);
					int id	=	mSPrefer.getValueInteger("id");
					if(nresult!=id){
						mSPrefer.putValueInteger("id",nresult);
						sendNotfication();
					}
				} catch (Exception e) {
					nresult	=	-1;
				}
				if(nresult>=0){
					
				}
			}
		}
	}
	
	//	生命周期关闭线程;
	public void onDestroy() {
		super.onDestroy();
		if(getBroadCastReceiver!=null){			
			unregisterReceiver(getBroadCastReceiver);
		}
		this.threadDisable = false;
	}

	public class GetBroadCastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context content, Intent intent) {
			Bundle bundle=intent.getExtras();
			boolean flag=bundle.getBoolean("flag");
			if(flag){				
				//	总控开关的开启;
				threadDisable= true;
				
				if(requestThread==null){
					if(mtGetOrPostHelper==null){
						mtGetOrPostHelper=new MTGetOrPostHelper();
					}
					//	问询线程的开启;
					requestThread=new RequestThread(mtGetOrPostHelper);
					requestThread.start();
				}
				
				//	计数的线程进行开启;
				if(countTread==null){
					count=0;
					countTread=new CountTread();
					countTread.start();
				}
			}else{
				threadDisable= false;
				
				if(countTread!=null){
					countTread.interrupt();
					countTread=null;
				}
				
				if(requestThread!=null){
					requestThread.interrupt();
					requestThread=null;
					mtGetOrPostHelper=null;
				}
				count=0;
				Intent i=new Intent();
				Bundle	b=new Bundle();
				b.putInt("count", 0);
				i.putExtras(bundle);//
				i.setAction(pTargetFromService);//
				sendBroadcast(i);
			}
		}
	}
	@SuppressWarnings("deprecation")
	private void sendNotfication(){
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, "Notice!!", System.currentTimeMillis());
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		notification.sound = uri;
		long[] vibrates = { 0, 1000, 1000, 1000 };
		notification.vibrate = vibrates;
		notification.ledARGB = Color.GREEN;
		notification.ledOnMS = 1000;
		notification.ledOffMS = 1000;
		notification.flags = Notification.FLAG_SHOW_LIGHTS;

//		Intent intent = new Intent(this, VAddActivity.class);
//		
//		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_CANCEL_CURRENT);

		notification.setLatestEventInfo(this, "JiaYou International Information", "new Data update finished!!",null);

		manager.notify(1, notification);
	}
}
