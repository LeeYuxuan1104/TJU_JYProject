package com.view;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.controller.service.ReceiveDataService;
import com.example.jynetsubmit.R;
import com.model.entity.MMineinfo;
import com.model.entity.MPhoto;
import com.model.tool.common.MTConfiger;
import com.model.tool.common.MTGetOrPostHelper;
import com.model.tool.common.MTImgHelper;
import com.model.tool.view.ImageAdpter;
import com.model.tool.view.EditTextWithDel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;

public class VAddActivity extends Activity implements OnClickListener{
	//	上下文的信息;
	private Context	mContext;
	private Intent	mIntent;
	//	控件的内容;
	private TextView vTopic;
	private Button  vBack;
	// 增加界面的主要内容;
	private Button  vOk,
					vxhcode,vzcidcode,vgcidcode,vqfhcode,vibimage,vList,vSet;
	private EditTextWithDel vxh,vrq,vzcid,vgcid,vjz,vmz,vdzkh,vsjxm,vhzh,vqfh;
	//	提示信息框;
	private Builder	    vBuilder;
	private MMineinfo   mineinfo;
	private MTConfiger  mtConfiger;
	//	图片管理类;
	private MTImgHelper mtImgHelper;
	//	文件的相关内容;
	private String folderpath,filepath,imgname,tmppath;
	//	使用的线程;
	private	ProgressDialog	vDialog;	// 	对话框;
	private MyThread		myThread;	// 线程;
	/***/
	//	进行检测监听;
	//	01.计数项;
	private int 	  pCount;
	//	02.标签发送;
	private String    pTargetToService  ="com.from.activity.to.service";
	//	03.标签接收;
	private String    pTargetFromService="com.from.service.to.activity";
	private GetInfoReceiver	getInfoReceiver;
	private Intent			setIntentInfo;
	private IntentFilter 	getInfoFilter;
	/***/
	//	图片的提交;
	private List<MPhoto> 	listPhoto;
	//	相册集合控件;
	private ImageAdpter 	imageAdpter;
	private GridView 		gridView;
	//	handler控件;
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int nFlag=msg.what;
			vDialog.dismiss();
			switch (nFlag) {
			case 1:				
				Toast.makeText(mContext, R.string.success,Toast.LENGTH_SHORT).show();
				finishparam();
				break;
			case 2:
				Toast.makeText(mContext, R.string.fail,Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
			closeThread();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_add);
		initView();
		initEvent();
	}
	//	初始化控件;
	private void initView(){
		//	标题内容;
		vTopic=(TextView) findViewById(R.id.tvTopic);
		//	返回按钮;
		vBack=(Button) findViewById(R.id.btnBack);
		//	上传按钮;
		vOk=(Button) findViewById(R.id.btnOk);
		//	主车id的扫描二维码值;
		vzcidcode=(Button) findViewById(R.id.zcidcode);
		//	挂车id的扫描二维码值;
		vgcidcode=(Button) findViewById(R.id.gcidcode);
		//	集装箱号;
		vxh	   =(EditTextWithDel) findViewById(R.id.etxh);
		vxhcode=(Button) findViewById(R.id.xhcode);
		
		//	图片头像的内容;
		vibimage=(Button) findViewById(R.id.ibimage);
		//	日期内容;
		vrq=(EditTextWithDel) findViewById(R.id.etrq);
		//	主车编号
		vzcid=(EditTextWithDel) findViewById(R.id.etzcid);
		//	挂车编号
		vgcid=(EditTextWithDel) findViewById(R.id.etgcid);
		//	净重信息;
		vjz=(EditTextWithDel) findViewById(R.id.etjz);
		//	毛重信息;
		vmz=(EditTextWithDel) findViewById(R.id.etmz);
		//	电子卡号
		vdzkh=(EditTextWithDel) findViewById(R.id.etdzkh);
		//	司机姓名
		vsjxm=(EditTextWithDel) findViewById(R.id.etsjxm);
		vhzh =(EditTextWithDel) findViewById(R.id.ethzh);
		//	铅封编号
		vqfh=(EditTextWithDel) findViewById(R.id.etqfh);
		//	铅封号扫码;
		vqfhcode=(Button) findViewById(R.id.qfhcode);
		//	列表按钮;
		vList=(Button) findViewById(R.id.btnFunction);
		//	图标按钮;
		vSet=(Button) findViewById(R.id.ibibook);
		//  进行列表的初始化;
		listPhoto=new ArrayList<MPhoto>();
	}
	//	初始化操作;
	private void initEvent(){
		mContext=VAddActivity.this;
		//		服务机制;
		/*	01.注册广播—发送信息*/
		setIntentInfo=new Intent(mContext, ReceiveDataService.class);
		startService(setIntentInfo);
		/*	02.注册广播-接收信号*/
		getInfoReceiver = new GetInfoReceiver();
		getInfoFilter 	= new IntentFilter();
		getInfoFilter.addAction(pTargetFromService);
		registerReceiver(getInfoReceiver, getInfoFilter);
		/////////////////
		vTopic.setText("嘉友国际");
		vBack.setText("reset");
		//	返回按钮添加事件监听;
		vBack.setOnClickListener(this);
		//	上传按钮添加事件监听;
		vOk.setOnClickListener(this);
		
		vxhcode.setOnClickListener(this);
		//	主车扫码添加事件监听;
		vzcidcode.setOnClickListener(this);
		vzcidcode.setVisibility(View.GONE);
		//	挂车扫码添加事件监听;
		vgcidcode.setVisibility(View.GONE);
		vgcidcode.setOnClickListener(this);
		//	铅封号扫码添加事件监听;
		vqfhcode.setVisibility(View.GONE);
		vqfhcode.setOnClickListener(this);
		//	照相添加事件监听;
		vibimage.setOnClickListener(this);
		//	设置时间添加监听;
		vrq.setOnClickListener(this);
		//	列表信息;
		vList.setVisibility(View.VISIBLE);
		vList.setText("Synchronize");
		vList.setOnClickListener(this);
		//	配置属性文件;
		mtConfiger =new MTConfiger();
		mtImgHelper=new MTImgHelper();
		showCount(listPhoto, vSet);
		vSet.setOnClickListener(this);
	}
	@Override
	public void onClick(View view) {
		int nVid=view.getId();
		switch (nVid) {
		//	返回按钮;
		case R.id.btnBack:
			finishparam();
			break;
		//	上传按钮;
		case R.id.btnOk:
				if(mineinfo==null){
					mineinfo   =new MMineinfo();
				}
				
				String imgname2="";
				for(MPhoto photo:listPhoto){
					String name=photo.getFilename();
					imgname2+=name+"_";
				}

				mineinfo=mineinfo.getMineinfo(mContext,vxh, vrq, vzcid, vgcid, vjz, vmz, vdzkh, vsjxm, vhzh, vqfh, imgname2, null);
				if(mineinfo!=null){	
					vBuilder=new Builder(mContext);
					vBuilder.setTitle("Ensure Information");
					String message=
							"[Container barcode]   "+mineinfo.getRq()+"\r\n" +
							"[Truck loading time]   "+mineinfo.getRq()+"\r\n" +
							"[Truck No.]   "+mineinfo.getZcid()+"\r\n" +
							"[Trailer No.]   "+mineinfo.getGcid()+"\r\n" +
							"[Net Weight]   "+mineinfo.getJz()+"kg\r\n" +
							"[Gross Weight]   "+mineinfo.getMz()+"kg\r\n" +
							"[Truck electronic card]   "+mineinfo.getDzkh()+"\r\n" +
							"[Driver name]   "+mineinfo.getSjxm()+"\r\n" +
							"[Passport]   "+mineinfo.getHzh()+"\r\n" +
							"[Seal No.]   "+mineinfo.getQfh()
							;
					vBuilder.setMessage(message);
					vBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							if(myThread==null){								
								final CharSequence strDialogTitle = getString(R.string.wait);
								final CharSequence strDialogBody = getString(R.string.doing);
								vDialog = ProgressDialog.show(mContext, strDialogTitle,strDialogBody, true);
								myThread=new MyThread(mineinfo,listPhoto);
								myThread.start();	
							}
						}
					});
					vBuilder.setNegativeButton(R.string.no, null);
					vBuilder.create();
					vBuilder.show();
				}
		
			break;
		//	主车扫码;
		case R.id.zcidcode:
			// 跳转至专门的intent控件;
			mIntent = new Intent(mContext, VFlushActivity.class);
			// 有返回值的跳转;
			startActivityForResult(mIntent,1);
			break;
		//	挂车扫码;
		case R.id.gcidcode:
			mIntent = new Intent(mContext, VFlushActivity.class);
			// 有返回值的跳转;
			startActivityForResult(mIntent,2);
			break;
		//	铅封号扫码添加事件监听;
		case R.id.qfhcode:
			mIntent = new Intent(mContext, VFlushActivity.class);
			// 有返回值的跳转;
			startActivityForResult(mIntent,3);
			break;
		//	集装箱号扫码添加事件监听;
		case R.id.xhcode:
			mIntent = new Intent(mContext, VFlushActivity.class);
			// 有返回值的跳转;
			startActivityForResult(mIntent,5);
			break;
		//	图片加载;
		case R.id.ibimage:
			//	主车编号;
			String zcid=mtConfiger.docheckEditView(vzcid);
			//	挂车编号;
			String gcid=mtConfiger.docheckEditView(vgcid);
			if(!zcid.equals("null")&&!gcid.equals("null")){	
				int nSize=listPhoto.size();
				if(nSize<3){					
					getPhotoInfo(mtConfiger,zcid, gcid);
				}else Toast.makeText(mContext, "The maximum is 3", Toast.LENGTH_SHORT).show();
			}else Toast.makeText(mContext, "Please input information first!", Toast.LENGTH_SHORT).show();
			break;
		//	时间加载;
		case R.id.etrq:
			setViewDate(mContext, vrq);
			break;
		//	列表显示;
		case R.id.btnFunction:
			Intent i=new Intent();
			Bundle bundle=new Bundle();
			if(pCount==0){
				Toast.makeText(mContext, "Start Synchronizing", Toast.LENGTH_SHORT).show();
				bundle.putBoolean("flag", true);				
			}else{	
				bundle.putBoolean("flag", false);
			}
			i.putExtras(bundle);
			i.setAction(pTargetToService);
			sendBroadcast(i);
			break;
		//	图标的列表显示;
		case R.id.ibibook:
			int nSize=listPhoto.size();
			if(nSize>0){
				showImageSet(mContext);
			}else Toast.makeText(mContext, "No Pictures", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	
	// 返回键
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//	主车编号;
		if (requestCode == 1
				&& resultCode == 1) {
			String zcid = intent.getStringExtra("bid");
			vzcid.setText(zcid);
		}
		//	挂车编号;
		else if(requestCode == 2
				&& resultCode == 1){
			String gcid = intent.getStringExtra("bid");
			vgcid.setText(gcid);
		}
		//	铅封编号
		else if(requestCode == 3
				&& resultCode == 1){
			String qfh = intent.getStringExtra("bid");
			vqfh.setText(qfh);
		}
		//	集装箱号
		else if(requestCode == 5
				&& resultCode == 1){
			String xh = intent.getStringExtra("bid");
			vxh.setText(xh);
		}
		else if (requestCode == 4&& resultCode == -1) {
			Toast.makeText(mContext, "finished", Toast.LENGTH_SHORT).show();
			//	清空照片列表;
			//	适当裁剪照片;
			mtImgHelper.compressPicture(tmppath, filepath);
			//	清除老照片;
			mtImgHelper.clearPicture(tmppath, null);
			//	对于老照片进行显示;
			MPhoto mPhoto=new MPhoto(imgname,filepath);
			listPhoto.add(mPhoto);
			showCount(listPhoto, vSet);
		} 
	}
	//	显示图片的个数;
	private void showCount(List<MPhoto> list,Button view){
		int nSize=0;
		if(list!=null){
			nSize=list.size();
		}
		view.setText(nSize+"");
	}
	
	// 拍照功能;
	public void getPhotoInfo(MTConfiger configer, String zcid,String gcid) {
		File   file;
		String environmentstate=mtConfiger.getfState();
		//	判定硬件的状态;
		if (environmentstate.equals(Environment.MEDIA_MOUNTED)) {
			//	文件夹的生成;
			folderpath = mtConfiger.getfParentPath()+zcid+File.separator+gcid;
			//	获取文件名称;
			imgname    = java.lang.System.currentTimeMillis()+"";
			//	文件状态内容;
			file 	   = new File(folderpath);
			//	文件存在状态;
			boolean flag=file.exists();
			// 	生成文件夹的方式;
			if (!flag) {
				//	文件生成;
				file.mkdirs();
			}
			// 生成2中文件路径:01.临时的 02.永久的
			tmppath    = folderpath + File.separator + imgname + "_tmp.jpg";
			filepath   = folderpath + File.separator + imgname + ".jpg";
			// 文件夹的内容;
			file 	   = new File(tmppath);
			// 文件维护;
			if (file.exists()) {
				file.delete();
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(mContext, "Create picture failed!", Toast.LENGTH_LONG).show();
				}
			}
			//	利用系统的文件内容进行跳转;
			Intent mIntent = new Intent("android.media.action.IMAGE_CAPTURE");
			//	额外的文件内哦让那个
			mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(mIntent,4);
			
		} else Toast.makeText(mContext, "sdcard enable!", Toast.LENGTH_SHORT).show();
	}

	private String date,time;
	private void setViewDate(Context mContext,final EditText btn){
		Builder    vBuilder   = new Builder(mContext);
		/*布局控件*/
		View 	   view 	  = getLayoutInflater().inflate(R.layout.act_datatimepicker2, null);
		vBuilder.setTitle("Seting Time");
		vBuilder.setView(view);
		/*时间日期有关控件*/
		DatePicker datePicker = (DatePicker) view.findViewById(R.id.dpPicker);
		TimePicker timePicker = (TimePicker) view.findViewById(R.id.tpPicker);
		Calendar   calendar   = Calendar.getInstance();

		int 	   nYear 	  = calendar.get(Calendar.YEAR);
		int 	   nMonth 	  = calendar.get(Calendar.MONTH);
		int 	   nDay 	  = calendar.get(Calendar.DAY_OF_MONTH);
		int 	   nHour 	  = calendar.get(Calendar.HOUR_OF_DAY);
		int 	   nMinute 	  = calendar.get(Calendar.MINUTE);

		date = nYear + "-" + (nMonth + 1) + "-" + nDay+" ";
		time = nHour + ":" + nMinute;
		datePicker.init(nYear, nMonth, nDay, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				// 日历控件;
				date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " ";
			}
		});

		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view,
					int hourOfDay, int minute) {
				time = hourOfDay + ":" + minute;
			}
		});
		vBuilder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						String stime = date + time;
						btn.setText(stime);
					}
				});
		vBuilder.setNegativeButton(R.string.no, null);
		vBuilder.create();
		vBuilder.show();
	}
	//	进行数据上传的线程;
	//	线程的自定义形式;
	class MyThread extends Thread{
		
		private MTGetOrPostHelper mGetOrPostHelper;
		private MMineinfo		  mMineinfo;
		private List<MPhoto> 	  listPhoto;
		public MyThread(MMineinfo mMineinfo,List<MPhoto> listPhoto) {
			this.mGetOrPostHelper=  new MTGetOrPostHelper();
			this.mMineinfo		 =	mMineinfo;
			this.listPhoto		 =	listPhoto;
		}
		
		@Override
		public void run() {
			int nFlag = 1;
			// 传值;
			String url 	  	 = "http://"+MTConfiger.IP+":"+MTConfiger.PORT+"/Mine/app/addmineinfos";
			String param;
			String response	 = "fail";
			try {
				param 	  = 
					"rq="+URLEncoder.encode(mMineinfo.getRq(),"utf-8")+"&" +
					"zcid="+URLEncoder.encode(mMineinfo.getZcid(),"utf-8")+"&" +
					"gcid="+URLEncoder.encode(mMineinfo.getGcid(),"utf-8")+"&" +
					"jz="+mMineinfo.getJz()+"&" +
					"mz="+mMineinfo.getMz()+"&" +
					"dzkh="+URLEncoder.encode(mMineinfo.getDzkh(),"utf-8")+"&" +
					"sjxm="+URLEncoder.encode(mMineinfo.getSjxm(),"utf-8")+"&" +
					"hzh="+URLEncoder.encode(mMineinfo.getHzh(),"utf-8")+"&" +
					"qfh="+URLEncoder.encode(mMineinfo.getQfh(),"utf-8")+"&" +
					"img="+mMineinfo.getImg()+"&" +
					"state=0&"+
					"xh="+URLEncoder.encode(mMineinfo.getXh(),"utf-8")
					;

				response  = mGetOrPostHelper.sendGet(url, param);
				int nSize = listPhoto.size();
				if(nSize!=0){	
					for(MPhoto photo:listPhoto){
						String path=photo.getFilepath();
						String name=photo.getFilename();
						url			=	"http://"+MTConfiger.IP+":"+MTConfiger.PORT+"/Mine/upload";
						response	=	mGetOrPostHelper.uploadFile(url,path,name);
					}

				}else Log.i("MyLog", "无");
			} catch (UnsupportedEncodingException e) {
				
			}

			if (response.trim().equalsIgnoreCase("fail")) {
				nFlag = 2;
			}
			
			mHandler.sendEmptyMessage(nFlag);
		}
	}
	//	关闭线程;
	private void closeThread(){
		if(myThread!=null){
			myThread.interrupt();
			myThread=null;
		}
	 }
	
	//	广播进行接收的;
	public class GetInfoReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent){
			Bundle bundle=intent.getExtras();			
			pCount=bundle.getInt("count");
			//	接收信息的内容;
			if(pCount==0){
				Toast.makeText(mContext, "Stop Synchronizing", Toast.LENGTH_SHORT).show();
			}
		}
	}
	//	按下事件;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Builder builder=new Builder(mContext);
			builder.setTitle(R.string.exit);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					//	接收传递的数据包;
					if(getInfoReceiver!=null){
						unregisterReceiver(getInfoReceiver);
					}
					//	发送传递的数据包;
					if(setIntentInfo!=null){
						stopService(setIntentInfo);
					}	
					finish();
				}
			});
			builder.setNegativeButton(R.string.no, null);
			builder.create();
			builder.show();  
	    }
		return super.onKeyDown(keyCode, event);
	}
	
	@SuppressWarnings("deprecation")
	private void showImageSet(Context context){
		Builder 	builder= new Builder(context);
		
		builder.setTitle("Picture collection");
		View  		view   = getLayoutInflater().inflate(R.layout.img_main, null);
		float screenWidth  = getWindowManager().getDefaultDisplay().getWidth();     // 屏幕宽（像素，如：480px）  
		float screenHeight = getWindowManager().getDefaultDisplay().getHeight(); 
		imageAdpter		   = new ImageAdpter(listPhoto, mContext, screenWidth, screenHeight);
		gridView		   = (GridView) view.findViewById(R.id.grid);
		gridView.setAdapter(imageAdpter);
		//	图片模式加载;
		listPhoto		   = imageAdpter.getListPhoto();

		builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
				ArrayList<MPhoto> tmp=new ArrayList<MPhoto>();
				for(MPhoto mPhoto:listPhoto){
					boolean flag=mPhoto.isFlag();
					String  path=mPhoto.getFilepath();
					if(!flag){
						MPhoto mPhoto2=new MPhoto(path,false);
						tmp.add(mPhoto2);
					}else{
						File file=new File(path);
						file.delete();						
					}
				}
				listPhoto=new ArrayList<MPhoto>();
				listPhoto=tmp;
				showCount(listPhoto, vSet);
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		
		builder.setView(view);
		//	对话框进行显示;
		builder.create();
		builder.show();
	}

	//	初始化参数;
	private void finishparam(){
		//	集装箱号;
		vxh.setText("");
		//	日期内容;
		vrq.setText("");
		//	主车编号
		vzcid.setText("");
		//	挂车编号
		vgcid.setText("");
		//	净重信息;
		vjz.setText("");
		//	毛重信息;
		vmz.setText("");
		//	电子卡号
		vdzkh.setText("");
		//	司机姓名
		vsjxm.setText("");
		vhzh.setText("");
		//	铅封编号
		vqfh.setText("");
		listPhoto=new ArrayList<MPhoto>();
		showCount(listPhoto, vSet);
	}
}
