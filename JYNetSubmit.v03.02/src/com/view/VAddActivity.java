package com.view;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.controller.service.ReceiveDataService;
import com.example.jynetsubmit.R;
import com.model.entity.MMineinfo;
import com.model.entity.MPhoto;
import com.model.tool.common.MTConfiger;
import com.model.tool.common.MTGetOrPostHelper;
import com.model.tool.common.MTImgHelper;
import com.model.tool.view.MVEditTextWithDel;
import com.model.tool.view.MVPopupWindows;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VAddActivity extends Activity implements OnClickListener,OnFocusChangeListener{
	//	上下文的信息;
	private Context	mContext;
	private Intent	mIntent;
	private Resources mResources;
	//	控件的内容;
	private TextView vTopic,vBack,vList,vxhcode,vibimage,vSet;
	// 增加界面的主要内容;
	private Button  vOk;
	private MVEditTextWithDel vxh,vrq,vzcid,vgcid,vjz,vmz,vdzkh,vsjxm,vhzh,vqfh;
	private LinearLayout	linear;
	private RelativeLayout  vlayxh,vlaygcid,vlayqfh;
	private LinearLayout 	vlayrq,vlayzcid,vlaydzkh,vlayjz,vlaymz,vlaysjxm,vlayhzh;
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
	/**列表控件*/
	private ListView 		listview;
	
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
		linear=(LinearLayout) findViewById(R.id.linear);
		//	标题内容;
		vTopic=(TextView) findViewById(R.id.tvTopic);
		//	返回按钮;
		vBack =(TextView) findViewById(R.id.btnBack);
		//	上传按钮;
		vOk=(Button) findViewById(R.id.btnOk);
		//	集装箱号;
		vxh	   =(MVEditTextWithDel) findViewById(R.id.etxh);
		vxhcode=(TextView) findViewById(R.id.xhcode);
		
		//	图片头像的内容;
		vibimage= (TextView) findViewById(R.id.ibimage);
		//	日期内容;
		vrq=(MVEditTextWithDel) findViewById(R.id.etrq);
		//	主车编号
		vzcid=(MVEditTextWithDel) findViewById(R.id.etzcid);
		//	挂车编号
		vgcid=(MVEditTextWithDel) findViewById(R.id.etgcid);
		//	净重信息;
		vjz=(MVEditTextWithDel) findViewById(R.id.etjz);
		//	毛重信息;
		vmz=(MVEditTextWithDel) findViewById(R.id.etmz);
		//	电子卡号
		vdzkh=(MVEditTextWithDel) findViewById(R.id.etdzkh);
		//	司机姓名
		vsjxm=(MVEditTextWithDel) findViewById(R.id.etsjxm);
		vhzh =(MVEditTextWithDel) findViewById(R.id.ethzh);
		//	铅封编号
		vqfh=(MVEditTextWithDel) findViewById(R.id.etqfh);
		//	列表按钮;
		vList= (TextView) findViewById(R.id.btnFunction);
		//	图标按钮;
		vSet = (TextView) findViewById(R.id.ibibook);
		//	显示框架的相应内容;
		vlayxh	=(RelativeLayout) findViewById(R.id.layxh);
		vlaygcid=(RelativeLayout) findViewById(R.id.laygcid);
		vlayqfh	=(RelativeLayout) findViewById(R.id.layqfh);
		vlayrq=(LinearLayout) findViewById(R.id.layrq);
		vlayzcid=(LinearLayout) findViewById(R.id.layzcid);
		vlaydzkh=(LinearLayout) findViewById(R.id.laydzkh);
		vlayjz=(LinearLayout) findViewById(R.id.layjz);
		vlaymz=(LinearLayout) findViewById(R.id.laymz);
		vlaysjxm=(LinearLayout) findViewById(R.id.laysjxm);
		vlayhzh=(LinearLayout) findViewById(R.id.layhzh);
		//  进行列表的初始化;
		listPhoto=new ArrayList<MPhoto>();
	}
	//	初始化操作;
	private void initEvent(){
		mContext=VAddActivity.this;
		mResources=getResources();
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
		vTopic.setText("JIAYOU InterNational");
		vBack.setText("reset");
		//	返回按钮添加事件监听;
		vBack.setOnClickListener(this);
		//	上传按钮添加事件监听;
		vOk.setOnClickListener(this);
		vxhcode.setOnClickListener(this);
		//	照相添加事件监听;
		vibimage.setOnClickListener(this);
		//	设置时间添加监听;
		vrq.setOnClickListener(this);
		//	列表信息;
		vList.setText("Synchronize");
		vList.setOnClickListener(this);
		//	配置属性文件;
		mtConfiger =new MTConfiger();
		mtImgHelper=new MTImgHelper();
		showCount(listPhoto, vSet);
		vSet.setOnClickListener(this);
		//	添加事件监听;
		//	集装箱号;
		vxh.setOnFocusChangeListener(this);
		//	日期内容;
		vrq.setOnFocusChangeListener(this);
		//	主车编号
		vzcid.setOnFocusChangeListener(this);
		//	挂车编号
		vgcid.setOnFocusChangeListener(this);
		//	净重信息;
		vjz.setOnFocusChangeListener(this);
		//	毛重信息;
		vmz.setOnFocusChangeListener(this);
		//	电子卡号
		vdzkh.setOnFocusChangeListener(this);
		//	司机姓名
		vsjxm.setOnFocusChangeListener(this);
		vhzh.setOnFocusChangeListener(this);
		//	铅封编号
		vqfh.setOnFocusChangeListener(this);
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
					vBuilder.setTitle("Confirm Information");
					String message=
							"[Container barcode]   "+mineinfo.getXh()+"\r\n" +
							"[Truck loading time]   "+mineinfo.getRq()+"\r\n" +
							"[Truck No.]   "+mineinfo.getZcid()+"\r\n" +
							"[Trailer No.]   "+mineinfo.getGcid()+"\r\n" +
							"[Net weight]   "+mineinfo.getJz()+" kg\r\n" +
							"[Gross weight]   "+mineinfo.getMz()+" kg\r\n" +
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
			//	日期的设置框;
			new MVPopupWindows(mContext,mtConfiger ,linear,vrq);
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
				listPhoto=showImageSet(mContext,listPhoto);
			}else Toast.makeText(mContext, "No pictures", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	
	// 返回键
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//	铅封编号
		if(requestCode == 3
				&& resultCode == 1){
			String qfh = intent.getStringExtra("bid");
			mtConfiger.setViewText(vqfh, qfh);
		}
		//	集装箱号
		else if(requestCode == 5
				&& resultCode == 1){
			String xh = intent.getStringExtra("bid");
			mtConfiger.setViewText(vxh, xh);
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
	private void showCount(List<MPhoto> list,TextView view){
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
					//	关闭当前的互动对象;
					mIntent=new Intent(mContext, VWelActivity.class);
					startActivity(mIntent);
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
	private List<MPhoto> showImageSet(Context context,List<MPhoto> listPhoto){
		List<MPhoto> list=null;
		Builder 	builder= new Builder(context);
		
		builder.setTitle("Picture collection");
		View  		view   = getLayoutInflater().inflate(R.layout.act_img, null);
		float screenWidth  = getWindowManager().getDefaultDisplay().getWidth();     // 屏幕宽（像素，如：480px）  
		float screenHeight = getWindowManager().getDefaultDisplay().getHeight(); 
		listview 		   = (ListView) view.findViewById(R.id.listview);
		ListAdapter listAdapter= new ListAdapter(context,listPhoto,screenWidth,screenHeight);
		listview.setAdapter(listAdapter);
		list			   = listAdapter.getListPhoto();
		builder.setNegativeButton(R.string.ok, null);
		builder.setView(view);
		//	对话框进行显示;
		builder.create();
		builder.show();
		return list;
	}

	//	初始化参数;
	private void finishparam(){
		//	集装箱号;
		mtConfiger.setViewText(vxh,"");
		//	日期内容;
		mtConfiger.setViewText(vrq,"");
		//	主车编号
		mtConfiger.setViewText(vzcid,"");
		//	挂车编号
		mtConfiger.setViewText(vgcid,"");
		//	净重信息;
		mtConfiger.setViewText(vjz,"");
		//	毛重信息;
		mtConfiger.setViewText(vmz,"");
		//	电子卡号
		mtConfiger.setViewText(vdzkh,"");
		//	司机姓名
		mtConfiger.setViewText(vsjxm,"");
		mtConfiger.setViewText(vhzh,"");
		//	铅封编号
		mtConfiger.setViewText(vqfh,"");
		listPhoto=new ArrayList<MPhoto>();
		showCount(listPhoto, vSet);
	}
	
	//	listview适配器
	public class ListAdapter extends BaseAdapter {
		private Context context;
		private List<MPhoto> 	listPhoto;
		private float screenWidth,screenHeight;
		public ListAdapter(Context context,List<MPhoto> listPhoto,float screenWidth,float screenHeight) {
			this.context=context;
			this.listPhoto=listPhoto;
			this.screenWidth=screenWidth;
			this.screenHeight=screenHeight;
		}
		@Override
		public int getCount() {
			return listPhoto.size();
		}

		@Override
		public Object getItem(int position) {
			return listPhoto.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder 	 = new ViewHolder();
				convertView  = LayoutInflater.from(context).inflate(R.layout.item_image, null);
				
				viewHolder.txt_content = (TextView) convertView.findViewById(R.id.text);
				viewHolder.txt_delete = (TextView) convertView.findViewById(R.id.text_delete);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			//设置内容
			viewHolder.txt_content.setText(listPhoto.get(position).getFilename()+".jpg");

			viewHolder.txt_content.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					String path=listPhoto.get(position).getFilepath();
					showImage(context, path,screenWidth,screenHeight);
				}
			});
			 //删除按钮
	        viewHolder.txt_delete.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	String path=listPhoto.get(position).getFilepath();
	            	File file=new File(path);
	            	boolean flag=file.exists();
	            	if(flag){
	            		file.delete();
	            		listPhoto.remove(position);
	            	}
	            	showCount(listPhoto, vSet);
	            	notifyDataSetChanged();
	            }
	        });
	        
			return convertView;
		}

		public List<MPhoto> getListPhoto() {
			return listPhoto;
		}

		private class ViewHolder {
			private TextView txt_content;
			private TextView txt_delete;
		}
	}
	@SuppressWarnings("deprecation")
	private void showImage(Context context,String imagePath,float screenWidth,float screenHeight){
		Builder 	builder= new Builder(context);
		builder.setTitle("Picture show");
		View  		view   = LayoutInflater.from(context).inflate(R.layout.img_show, null);
		ImageView 	iv	   = (ImageView) view.findViewById(R.id.imgShow);
        Bitmap 		bm 	   = BitmapFactory.decodeFile(imagePath);
        int 		width  = bm.getWidth();  
        int 		height = bm.getHeight();  
  
        //放大為屏幕的1/2大小  
        float scaleWidth   = screenWidth/2/width;  
        float scaleHeight  = screenHeight/2/height;  
  
        // 取得想要缩放的matrix參數  
        Matrix matrix = new Matrix();  
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,true);

        iv.setBackgroundDrawable(new BitmapDrawable(context.getResources(),newbm));
        builder.setNegativeButton(R.string.ok, null);
        builder.setView(view);
        builder.create();
        builder.show();
	}
	@Override
	public void onFocusChange(View view, boolean flag) {
		int nVid=view.getId();
		switch (nVid) {
		case R.id.etxh:
			mtConfiger.setViewDrawable(flag, mResources, vlayxh, R.drawable.shape_table0, R.drawable.shape_table1);
			break;
		case R.id.etrq:
			mtConfiger.setViewDrawable(flag, mResources, vlayrq, R.drawable.shape_table0, R.drawable.shape_table1);
			break;
		case R.id.etzcid:
			mtConfiger.setViewDrawable(flag, mResources, vlayzcid, R.drawable.shape_table0, R.drawable.shape_table1);
			break;
		case R.id.etgcid:
			mtConfiger.setViewDrawable(flag, mResources, vlaygcid, R.drawable.shape_table0, R.drawable.shape_table1);
			break;
		case R.id.etjz:
			mtConfiger.setViewDrawable(flag, mResources, vlayjz, R.drawable.shape_table0, R.drawable.shape_table1);
			break;
		case R.id.etmz:
			mtConfiger.setViewDrawable(flag, mResources, vlaymz, R.drawable.shape_table0, R.drawable.shape_table1);
			break;
		case R.id.etdzkh:
			mtConfiger.setViewDrawable(flag, mResources, vlaydzkh, R.drawable.shape_table0, R.drawable.shape_table1);
			break;
		case R.id.etsjxm:
			mtConfiger.setViewDrawable(flag, mResources, vlaysjxm, R.drawable.shape_table0, R.drawable.shape_table1);
			break;
		case R.id.ethzh:
			mtConfiger.setViewDrawable(flag, mResources, vlayhzh, R.drawable.shape_table0, R.drawable.shape_table1);
			break;
		case R.id.etqfh:
			mtConfiger.setViewDrawable(flag, mResources, vlayqfh, R.drawable.shape_table0, R.drawable.shape_table1);
			break;
		default:
			break;
		}
	}
}
