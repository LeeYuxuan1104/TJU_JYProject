package cn.com.jy.view.need;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.jy.activity.R;
import cn.com.jy.model.helper.MTConfigHelper;
import cn.com.jy.model.helper.MTGetOrPostHelper;
import cn.com.jy.model.helper.MTSharedpreferenceHelper;



import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WelActivity extends Activity implements OnFocusChangeListener{
		// 01.控件类型;
		// 01.1输入框;
		private EditText 		vWid,vWpwd;
		// 01.2输入框的背景;
		private RelativeLayout	l1,l2;			
		// 01.4注册字样显示文本格式;
		private TextView		resign;		// 注册信息;
		private ProgressDialog  mDialog;	// 对话框;
		// 03.与程序有关的机制内容;
		private Context			mContext;	 // 上下文内容;
		private MTSharedpreferenceHelper mSpHelper;	 
		private MTGetOrPostHelper mGetOrPostHelper; // 与网络有关的帮助类;
		private MTConfigHelper	mConfigHelper;
		private Intent 	 		mIntent;	 // 跳转信息;
		private MyThread		mThread=null;// 自定义的上传线程;
		// 02.数据类型;
		private String			wid,wpwd;
		// 03.与交互相关的Hanlder内容;
		@SuppressLint("HandlerLeak")
		public Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//	控制符的标签;
				Bundle bundle= msg.getData();
				int    nFlag = bundle.getInt("flag");
				//	关闭对话方框;
				mDialog.dismiss();
				//	判断标记内容; 
				switch (nFlag) {
				//	结果正确信号;
				case MTConfigHelper.NTAG_SUCCESS:		
					//	提示符;
					Toast.makeText(mContext, R.string.tip_success,Toast.LENGTH_LONG).show();
					//	跳转符;
					mIntent = 	new Intent(mContext, MenuActivity.class);
					//	新跳转;
					startActivity(mIntent);
					//	结束本;
					finish();
					break;
				//	结果错误信号;
				case MTConfigHelper.NTAG_FAIL:
					//	提示框;
					Toast.makeText(mContext, R.string.tip_fail,Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}
				//	关闭线程的操作;
				closeThread();
			}
		};

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// 01.检查首选项的相应内容;
			doCheckSharedPreference();
			// 02.布局文件加载;
			setContentView(R.layout.wel);
			// 03.初始化控件;
			initView();
			// 04.初始化事件;
			initEvent();
		}

		/**01.检查首选项操作;
		 * @author liyuxuan;
		 * @param null;
		 * @return null;
		 * */
		@SuppressWarnings("static-access")
		public void doCheckSharedPreference() {
			// 程序自身;
			mContext	=	WelActivity.this;
			// 首选项内容_其中文件为姓名;
			mSpHelper   = 	new MTSharedpreferenceHelper(mContext, MTConfigHelper.CONFIG_SELF,mContext.MODE_APPEND);
			mConfigHelper=new MTConfigHelper();
			// 首选项中的用户名称+密码;
			wid			= 	mSpHelper.getValue(MTConfigHelper.CONFIG_SELF_WID);
			wpwd 		= 	mSpHelper.getValue(MTConfigHelper.CONFIG_SELF_WPWD);
			// 检测数据中是否有数值;
			if (wid!= null && wpwd!= null) {
				mIntent =  new Intent(mContext, MenuActivity.class);
				startActivity(mIntent);
				finish();
			}
		}
		/**03.初始化控件;
		 * @author liYuxuan;
		 * @param null
		 * @return null
		 * */
		public void initView() {
			//	输入框控件定义;
			vWid  = (EditText) findViewById(R.id.etWid);
			vWpwd = (EditText) findViewById(R.id.etWpwd);
			//	背景控件定义;
			l1	   = (RelativeLayout) findViewById(R.id.l1);
			l2	   = (RelativeLayout) findViewById(R.id.l2);
			//	注册控件定义;
			resign = (TextView) findViewById(R.id.resign);
		}
		/**
		 * 04.事件监听初始化;
		 * @author liYuxuan
		 * @param  null;
		 * @return null;
		 * */
		public void initEvent(){
			//	生成一个实例的与网络进行交互的方法;
			mGetOrPostHelper=new MTGetOrPostHelper();
			//	进行底框颜色内容的设置;
			l1.setBackgroundColor(Color.parseColor("#d0d0d0"));
			l2.setBackgroundColor(Color.parseColor("#d0d0d0"));
			//	注册按钮的标签;
			//	用户编号的变化按钮;
			vWid.setOnFocusChangeListener(this);
			vWpwd.setOnFocusChangeListener(this);
			//	进行注册跳转;
			resign.setText(Html.fromHtml("<a href=\"\">注  册</a>"));				
			resign.setVisibility(View.GONE);
		}

		/**05.登录操作
		 * @author liyuxuan;
		 * @param view 控件
		 * @return null
		 * */
		public void onClickOk(View view) {
			// 获得相应的;
			wid 	 = mConfigHelper.setStringFormat(vWid);
			wpwd 	 = mConfigHelper.setStringFormat(vWpwd);
			if(!wid.equals("未填")&&!wpwd.equals("未填")){				
				if(mThread==null){
					// 进度条的内容;
					final CharSequence strDialogTitle = getString(R.string.tip_dialog_wait);
					final CharSequence strDialogBody  = getString(R.string.tip_dialog_done);
					mDialog = ProgressDialog.show(mContext, strDialogTitle, strDialogBody,true);
					mThread	= new MyThread();
					mThread.start();
				}
			}else Toast.makeText(mContext, "请填写完整内容", Toast.LENGTH_SHORT).show();
		}
		//进行登录的线程;
		class MyThread extends Thread{
			@Override
			public void run() {
				// 进行相应的登录操作的界面显示;
				String  url		 =	"http://"+MTConfigHelper.TAG_IP_ADDRESS+":"+MTConfigHelper.TAG_PORT+"/"+MTConfigHelper.TAG_PROGRAM+"/login";
				String  param	 =	"operType=1&wid="+ wid + "&wpwd=" + wpwd;
				String  response = 	mGetOrPostHelper.sendGet(url,param);
				int     nFlag	 = 	MTConfigHelper.NTAG_FAIL;
				Message	msg		 =  new Message();
				Bundle	bundle	 =	new Bundle();

				// 判断种类;
				if(!response.trim().equalsIgnoreCase("fail")){
					
					try {
						JSONArray array = 	new JSONArray(response);
						int 	  i		=	0;
						JSONObject obj  = 	null;
						do {
							try {								
								obj		=	array.getJSONObject(i);
								//	插入到首选项的配置文件当中;
								mSpHelper.putValue(MTConfigHelper.CONFIG_SELF_WID,obj.getString("wid").toString());
								mSpHelper.putValue(MTConfigHelper.CONFIG_SELF_WNAME,obj.getString("wname").toString());
								mSpHelper.putValue(MTConfigHelper.CONFIG_SELF_WCALL,obj.getString("wcall").toString());					
								mSpHelper.putValue(MTConfigHelper.CONFIG_SELF_WPWD,obj.getString("wpwd").toString());
								mSpHelper.putValue(MTConfigHelper.CONFIG_SELF_WNOTE,obj.getString("wnote").toString());
								nFlag= MTConfigHelper.NTAG_SUCCESS;
								i++;
							} catch (Exception e) {
								obj		=	null;
							}
						} while (obj!=null);
					} catch (JSONException e) {
						nFlag			=	MTConfigHelper.NTAG_FAIL;
					}
				}
				bundle.putInt("flag", nFlag);
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			}
		}
		private void closeThread(){
			if(mThread!=null){
				mThread.interrupt();
				mThread=null;
			}
		}
		@Override
		protected void onDestroy() {
			super.onDestroy();
			closeThread();
		}
		//	颜色变化
		@Override
		public void onFocusChange(View view, boolean flag) {
			int nVid=view.getId();
			switch (nVid) {
			case R.id.etWid:
				mConfigHelper.setViewColor(flag, l1, "#d1eeee","#d0d0d0");
				break;
			case R.id.etWpwd:
				mConfigHelper.setViewColor(flag, l2, "#d1eeee","#d0d0d0");
				break;
			default:
				break;
			}
		}
}
