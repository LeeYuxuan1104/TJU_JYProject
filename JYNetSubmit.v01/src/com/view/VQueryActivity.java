package com.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.controller.service.ReceiveDataService;
import com.example.jynetsubmit.R;
import com.model.entity.MMineinfo;
import com.model.tool.common.MTConfiger;
import com.model.tool.common.MTGetOrPostHelper;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class VQueryActivity extends Activity implements OnClickListener{
	//	上下文;
	private Context mContext;
	//	控件;
	//	上方按钮控件;
	private TextView vTopic;
	private Button   vBack,vAdd,vListener;
	//	下方按钮控件;
	private Spinner  vPageCount;
	private Button   vUpPage,vDownPage;
	private EditText vPage;
	//	中间的按钮控件;
	private Spinner  vKind;
	private Button   vSearch;
	private EditTextWithDel vValue;
	//	样式形式内容;
	private TextView vNum,vContent;
	private ListView vlistView;

	//	进行线程控件;
	private ProgressDialog 	vDialog; // 对话方框;
	//	自定义类;
	private Builder	   vBuilder;
	private MyThread   myThread; 
//	private MTConfiger mtConfiger;
	private int 	   nUpOrDown=1; 
	private ArrayList<Map<String, String>> listdata;
	//	适配器内容;
	//	参数值信息;
	private String[] names={"Trucker No.","Trailer No.","Passport","Seal No."},
					 kinds={"zcid","gcid","hzh","qfh"},
					 pages={"1","2","3","4","5","6","7","8","9","10"},
					 pages2={"01 /page","02 /page","03 /page","04 /page","05 /page","06 /page","07 /page","08 /page","09 /page","10 /page"};
	private ArrayAdapter<String> adapter,pAdapter;
	private SimpleAdapter	mAdapter;

	//	参数的初始化;
	//	当前页码;
	private int nCurrentPage=1,nCountLimit=4;
	//	参数码值;
	private String value=" ",pkind="";
	//	矿山信息;
	private MMineinfo mMineinfo;
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
	
	//	控制线程;
	@SuppressLint("HandlerLeak") 
	Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle=msg.getData();
			int nFlag = bundle.getInt("flag");
			ArrayList<Map<String, String>> lresult=(ArrayList<Map<String, String>>) bundle.getSerializable("list");
			
			vDialog.dismiss();
			switch (nFlag) {
			// 01.成功;
			case 1:
				Toast.makeText(mContext, R.string.update,Toast.LENGTH_SHORT).show();
				break;
			// 02.失败;
			case 2:
				Toast.makeText(mContext, R.string.nodata, Toast.LENGTH_LONG).show();
				switch (nUpOrDown) {
				//	上一页;				
				case 1:
					nCurrentPage++;
					break;
				//	下一页;
				case 2:					
					nCurrentPage--;
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
			loadData(lresult);
			vPage.setText("Page "+nCurrentPage);
			if(myThread!=null){
				myThread.interrupt();
				myThread=null;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_query);
		initView();
		initEvent();
	}
	// 初始化控件;
	private void initView(){
		//	控件;
		//	上方按钮控件;
		vTopic	 =(TextView) findViewById(R.id.tvTopic);
		vBack	 =(Button) findViewById(R.id.btnBack);
		vAdd	 =(Button) findViewById(R.id.btnFunction);
		//	自动刷新;
		vListener=(Button) findViewById(R.id.btnOther);
		//	下方按钮控件;
		vPageCount=(Spinner) findViewById(R.id.spPageCount);
		vUpPage	  =(Button) findViewById(R.id.btnUpPage);
		vDownPage =(Button) findViewById(R.id.btnDownPage);
		vPage	  =(EditText) findViewById(R.id.etPage);
		//	中间的按钮控件;
		vKind	  =(Spinner) findViewById(R.id.spKind);
		vSearch	  =(Button) findViewById(R.id.btnSearch);
		vValue	  =(EditTextWithDel) findViewById(R.id.etValue);
		//	样式形式内容;
		vNum	  =(TextView) findViewById(R.id.num);
		vContent  =(TextView) findViewById(R.id.content);
		vlistView =(ListView) findViewById(R.id.listView);
	}
	// 初始化事件;
	private void initEvent(){
		//	上下文的联系;
		mContext	= VQueryActivity.this;
		//	服务机制;
		/*	01.注册广播—发送信息*/
		setIntentInfo=new Intent(mContext, ReceiveDataService.class);
		startService(setIntentInfo);
		/*	02.注册广播-接收信号*/
		getInfoReceiver = new GetInfoReceiver();
		getInfoFilter 	= new IntentFilter();
		getInfoFilter.addAction(pTargetFromService);
		registerReceiver(getInfoReceiver, getInfoFilter);
		
		//	配置类的声明;
//		mtConfiger	=new MTConfiger();
		mMineinfo	=new MMineinfo();
		vTopic.setText("嘉友国际");
		vBack.setText("Back");
		//	同步按钮;
		vAdd.setText("Synchronize");
		vAdd.setVisibility(View.VISIBLE);
		//	控件显示;
		vListener.setVisibility(View.GONE);
		vListener.setText("Synchronize");
		//	事件监听;
		vListener.setOnClickListener(this);
		//	退出按钮;
		vBack.setOnClickListener(this);
		//	新增按钮;
		vAdd.setOnClickListener(this);
		//	搜索按钮;
		vSearch.setOnClickListener(this);
		//	上一页按钮;
		vUpPage.setOnClickListener(this);
		//	下一页按钮;
		vDownPage.setOnClickListener(this);
		vNum.setText("Order");
		vContent.setText("Truck NO. · Trailer No. · Passport · Seal No.");
		initLoad();
		
		adapter=new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, names);
		vKind.setAdapter(adapter);
		//增加事件监听;
		vKind.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				
				pkind=kinds[position];
				vValue.setText("");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				pkind="";
				
			}
		});

		//	列表单点
		vlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				vBuilder				=new Builder(mContext);
				Map<String, String> obj	=listdata.get(position);
				vBuilder.setTitle("Detail");
				String 				info=mMineinfo.getMineinfoItem(obj);
				vBuilder.setMessage(info);
				vBuilder.setNegativeButton(R.string.no, null);
				vBuilder.create();
				vBuilder.show();
			}
		});
		//	每页显示;
		pAdapter=new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, pages2);
		vPageCount.setAdapter(pAdapter);
		vPageCount.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				nCountLimit=Integer.parseInt(pages[position]);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				nCountLimit=4;
			}
		});
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		initLoad();
	}
	// 定义的线程——自定义的线程内容;
	public class MyThread extends Thread {
		private MTGetOrPostHelper mGetOrPostHelper;
		private int 	currentpage,countlimit;
		private String  pkind,value,order;
		private int 	id;
		public MyThread(int currentpage,int countlimit,String pkind,String value,String order,int id) {
			this.mGetOrPostHelper=new MTGetOrPostHelper();
			this.currentpage=currentpage;
			this.countlimit=countlimit;
			this.pkind=pkind;
			this.value=value;
			this.order=order;
			this.id=id;
		}
		@Override
		public void run() {
			int nFlag = 1;
			// 进行相应的登录操作的界面显示;
			// 01.Http 协议中的Get和Post方法;
			String 		url 	;
			String 		param	;
			String 		response= 	"fail";
			Message 	msg		=	new Message();
			Bundle	    bundle	=	new Bundle();
			try {
				url	  			= 	"http://"+MTConfiger.IP+":"+MTConfiger.PORT+"/Mine/app/getmineinfo";
				if(order.equals("delall")){
					param = 
					"opertype="+MTConfiger.DEL_ALL;
					response  = mGetOrPostHelper.sendGet(url, param);
					listdata.clear();
				}
				
				if(order.equals("delitem")){
					param = 
					"opertype="+MTConfiger.DEL_ITEM+"" +
					"&id="+id;
					response  = mGetOrPostHelper.sendGet(url, param);
					listdata.clear();
				}
				String tmp="";
				if(pkind.equals("zcid")){
					tmp="zcid="+URLEncoder.encode(value,"utf-8")+"&gcid=&hzh=&qfh=";
				}else if(pkind.equals("gcid")){
					tmp="zcid=&gcid="+URLEncoder.encode(value,"utf-8")+"&hzh=&qfh=";
				}else if(pkind.equals("hzh")){
					tmp="zcid=&gcid=&hzh="+URLEncoder.encode(value,"utf-8")+"&qfh=";
				}else if(pkind.equals("qfh")){
					tmp="zcid=&gcid=&hzh=&qfh="+URLEncoder.encode(value,"utf-8");
				}else{
					tmp="zcid=&gcid=&hzh=&qfh=";
				}
				
				param = 
				"currentpage="+currentpage+"&" +
				"countlimit="+countlimit+"&" +tmp;

				response  = mGetOrPostHelper.sendGet(url, param);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (response.trim().equals("fail")||response.trim().equals("")) {
				nFlag = 2;
			}else{
				JSONArray 	  array = null;
				try {
					array 			= new JSONArray(response);
					int		nsize	= array.length();
					if(nsize!=0){
						listdata		= new ArrayList<Map<String,String>>();
						int i = 0;
						JSONObject obj = null;
						do {
							try {
								// JsonObject的解析;
								obj 			 = array.getJSONObject(i);
								
								int    number	 = (nCurrentPage-1)*nCountLimit+(i+1);
								String id		 = obj.getString("id");
								String xh		 = obj.getString("xh");
								String rq		 = obj.getString("rq");
								String zcid		 = obj.getString("zcid");
								String gcid		 = obj.getString("gcid");
								String jz		 = obj.getString("jz");
								String mz		 = obj.getString("mz");
								String dzkh		 = obj.getString("dzkh");
								String sjxm		 = obj.getString("sjxm");
								String hzh		 = obj.getString("hzh");
								String qfh		 = obj.getString("qfh");
								String img		 = obj.getString("img");
								String state	 = obj.getString("state");
								
								Map<String, String> map=new HashMap<String, String>();
								map.put("number", number+"");
								map.put("id", id);
								map.put("xh", xh);
								map.put("rq", rq);
								map.put("zcid", zcid);
								map.put("gcid", gcid);
								map.put("jz", jz);
								map.put("mz", mz);
								map.put("dzkh", dzkh);
								map.put("sjxm", sjxm);
								map.put("hzh", hzh);
								map.put("qfh", qfh);
								map.put("img", img);
								map.put("state", state);
								map.put("content",zcid+"  ·  "+gcid+"  ·  "+hzh+"  ·  "+qfh);
								listdata.add(map);
								i++;
							} catch (Exception e) {
								obj = null;
							}
						} while (obj != null);
					}else nFlag = 2;
				} catch (JSONException e) {
					nFlag = 2;
				}
			}
			
			bundle.putSerializable("list", listdata);
			bundle.putInt("flag", nFlag);
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}
	}
	//	进行数据的加载;
	private void loadData(ArrayList<Map<String, String>> list){
		if(list!=null){
			mAdapter=new SimpleAdapter(mContext, list, R.layout.act_item, new String[]{"number","content","id"}, new int []{R.id.number,R.id.content,R.id.id});
			vlistView.setAdapter(mAdapter);
		}else nCurrentPage=0;
	}
	//	初始化加载;
	private void initLoad(){
		if(myThread==null){
			nUpOrDown=0;
			nCurrentPage=1;
			final CharSequence strDialogTitle = getString(R.string.wait);
			final CharSequence strDialogBody = getString(R.string.doing);
			vDialog = ProgressDialog.show(mContext, strDialogTitle,strDialogBody, true);
			myThread=new MyThread(nCurrentPage, nCountLimit, " ", "","null",0);
			myThread.start();
		}		
	}
	@Override
	public void onClick(View view) {
		int nVid= view.getId();
		//	获得值;
		value	= vValue.getText().toString().trim();
		switch (nVid) {
		case R.id.btnBack:
//			mtConfiger.exitSystem(VQueryActivity.this);
			Builder builder=new Builder(mContext);
			builder.setTitle(R.string.exit);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
//					接收传递的数据包;
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
			break;
		//	搜索键;
		case R.id.btnSearch:
			if(myThread==null){
				nUpOrDown=0;
				nCurrentPage=1;
				final CharSequence strDialogTitle = getString(R.string.wait);
				final CharSequence strDialogBody = getString(R.string.doing);
				vDialog = ProgressDialog.show(mContext, strDialogTitle,strDialogBody, true);
				myThread=new MyThread(nCurrentPage, nCountLimit, pkind, value,"null",0);
				myThread.start();
			}
			break;
			
		//	上一页;
		case R.id.btnUpPage:
			if(listdata!=null){				
				if(myThread==null){
					nUpOrDown=1;
					nCurrentPage--;
					final CharSequence strDialogTitle = getString(R.string.wait);
					final CharSequence strDialogBody = getString(R.string.doing);
					vDialog = ProgressDialog.show(mContext, strDialogTitle,strDialogBody, true);
					myThread= new MyThread(nCurrentPage, nCountLimit, pkind, value,"null",0);
					myThread.start();
				}	
			}
			break;
			
		//	下一页;
		case R.id.btnDownPage:
			if(listdata!=null){				
				if(myThread==null){
					nUpOrDown=2;
					nCurrentPage++;
					final CharSequence strDialogTitle = getString(R.string.wait);
					final CharSequence strDialogBody = getString(R.string.doing);
					vDialog = ProgressDialog.show(mContext, strDialogTitle,strDialogBody, true);
					myThread=new MyThread(nCurrentPage, nCountLimit, pkind, value,"null",0);
					myThread.start();
				}
			}
			break;	
		case R.id.btnOther:
			Intent intent=new Intent(mContext, VAddActivity.class);
			startActivity(intent);
			break;
		//	接受的按钮;
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
		default:
			break;
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
//					接收传递的数据包;
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
	
}
