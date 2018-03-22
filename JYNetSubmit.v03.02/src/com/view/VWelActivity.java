package com.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.example.jynetsubmit.R;
import com.model.entity.MWorkerinfo;
import com.model.tool.common.MTConfiger;
import com.model.tool.common.MTGetOrPostHelper;
import com.model.tool.view.MVEditTextWithDel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class VWelActivity extends Activity implements OnClickListener,OnFocusChangeListener{
	/*系统本身的内容*/
	//	01.上下文内容;
	private Context   mContext;
	//	02.提货的标签;
	private Intent	  mIntent;
	//	03.资源的内容;
	private Resources mResources;
	/*控件*/
	private MVEditTextWithDel vid,vpwd;
	private Button   vlogin,vsignup;
	private LinearLayout vlayid,vlaypwd;
	
	private ProgressDialog 	vDialog; // 对话方框;
	private MyThread   myThread; 
	/*对象参数*/
	private MWorkerinfo	mWorkerinfo;
	private MTConfiger	mtConfiger;
	
	//	控制线程;
	@SuppressLint("HandlerLeak") 
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int nFlag = msg.what;
			vDialog.dismiss();
			switch (nFlag) {
			// 01.成功;
			case 1:
				//	成功提醒;
				Toast.makeText(mContext, R.string.success,Toast.LENGTH_SHORT).show();
				//	页面跳转;
				mIntent=new Intent(mContext, VAddActivity.class);
				//	跳转页面;
				startActivity(mIntent);
				//	关闭本页面;
				finish();
				break;
			// 02.失败;
			case 2:
				//	失败显示;
				Toast.makeText(mContext, R.string.fail, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
			if(myThread!=null){
				myThread.interrupt();
				myThread=null;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//	加载显示页面;
		setContentView(R.layout.act_wel);
		//	控件初始化;
		initView();
		//	事件初始化;
		initEvent();
	}
	//	初始化控件;
	private void initView(){
		 // 用户编号;	
		 vid	=(MVEditTextWithDel) findViewById(R.id.etid);
		 //	用户密码;
		 vpwd	=(MVEditTextWithDel) findViewById(R.id.etpwd);
		 //	登录按钮;
		 vlogin	=(Button) findViewById(R.id.btnlogin);
		 //	注册按钮;
		 vsignup=(Button) findViewById(R.id.btnsignup);
		 // 账户布局;
		 vlayid=(LinearLayout) findViewById(R.id.layid);
		 //	密码布局;
		 vlaypwd=(LinearLayout) findViewById(R.id.laypwd);
	}
	
	//	初始化事件;
	private void initEvent(){
		//	本地的上下文;
		mContext	=	VWelActivity.this;
		mResources	=	getResources();
		mtConfiger	=	new MTConfiger();
		vsignup.setVisibility(View.GONE);
		//	输入框的事件监听;
		//	用户的账户内容事件监听;
		vid.setOnFocusChangeListener(this);
		//	用户的账户密码事件监听;
		vpwd.setOnFocusChangeListener(this);
		//	添加事件监听;
		vlogin.setOnClickListener(this);
	}
	// 定义的线程——自定义的线程内容;
	public class MyThread extends Thread {
		private MTGetOrPostHelper mGetOrPostHelper;
		private MWorkerinfo		  mWorkerinfo;
		public MyThread(MWorkerinfo mWorkerinfo) {
			this.mGetOrPostHelper=new MTGetOrPostHelper();
			this.mWorkerinfo	 =mWorkerinfo;
		}
		@Override
		public void run() {
			int nFlag = 1;
			// 进行相应的登录操作的界面显示;
			// 01.Http 协议中的Get和Post方法;
			String url 	  	 = "http://"+MTConfiger.IP+":"+MTConfiger.PORT+"/Mine/app/login";
			String param;
			String response	 = "fail";
			try {
				param = "wid="+URLEncoder.encode(mWorkerinfo.getWid(),"utf-8")+"&wpwd="+URLEncoder.encode(mWorkerinfo.getWpwd(),"utf-8");
				response  = mGetOrPostHelper.sendGet(url, param);
			} catch (UnsupportedEncodingException e) {
				
			}
			if (response.trim().equals("fail")||response.trim().equals("")) {
				nFlag = 2;
			}
			mHandler.sendEmptyMessage(nFlag);
		}
	}
	@Override
	public void onClick(View view) {
		int nVid=view.getId();
		switch (nVid) {
		//	登录按钮;
		case R.id.btnlogin:
			if(myThread==null){
				if(mWorkerinfo==null){						
					mWorkerinfo=new MWorkerinfo();
				}
				mWorkerinfo=mWorkerinfo.getWorkerInfo(mContext, vid, vpwd);

				if(mWorkerinfo!=null){					
					final CharSequence strDialogTitle = getString(R.string.wait);
					final CharSequence strDialogBody = getString(R.string.doing);
					vDialog = ProgressDialog.show(mContext, strDialogTitle,strDialogBody, true);
					myThread=new MyThread(mWorkerinfo);
					myThread.start();
				}
			}
			break;

		default:
			break;
		}
	}
	//	获取焦点的事件监听;
	@Override
	public void onFocusChange(View view, boolean flag) {
		int nVid=view.getId();
		switch (nVid) {
		case R.id.etid:
			mtConfiger.setViewDrawable(flag, mResources, vlayid, R.drawable.shape_edit0, R.drawable.shape_edit1);
			break;
		case R.id.etpwd:
			mtConfiger.setViewDrawable(flag, mResources, vlaypwd, R.drawable.shape_edit0, R.drawable.shape_edit1);
			break;
		default:
			break;
		}
		
	}
}
