package com.model.entity;

import com.example.jynetsubmit.R;
import com.model.tool.common.MTConfiger;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

public class MWorkerinfo {
	private String wid;
	private String wname;
	private String wpwd;
	private MTConfiger mtConfiger;
	public MWorkerinfo(String wid, String wname,String wpwd) {
		super();
		this.wid = wid;
		this.wname = wname;
		this.wpwd=wpwd;
		if(mtConfiger==null){			
			mtConfiger=new MTConfiger();
		}
	}
	public MWorkerinfo() {
		super();
		if(mtConfiger==null){			
			mtConfiger=new MTConfiger();
		}
	}
	
	public String getWid() {
		return wid;
	}
	public void setWid(String wid) {
		this.wid = wid;
	}
	public String getWname() {
		return wname;
	}
	public void setWname(String wname) {
		this.wname = wname;
	}
	public String getWpwd() {
		return wpwd;
	}
	public void setWpwd(String wpwd) {
		this.wpwd = wpwd;
	}
	//	进行数据的填写;
	//	01.登录有关对象;
	public MWorkerinfo getWorkerInfo(Context context,EditText et1,EditText et2){
		MWorkerinfo	workerinfo = null;
		String 		wid		 = mtConfiger.docheckEditView(et1);
		String 		wpwd	 = mtConfiger.docheckEditView(et2);
		if(!wid.equals("null")&&!wpwd.equals("null")){
			workerinfo=new MWorkerinfo(wid, null, wpwd);
		}else Toast.makeText(context, R.string.complete, Toast.LENGTH_SHORT).show();
		return workerinfo;
	}
	//	02.填写有关对象;
	public MWorkerinfo getWorkerInfo(Context context,EditText et1,EditText et2,EditText et3){
		MWorkerinfo	workerinfo = null;
		String 		wid		 = mtConfiger.docheckEditView(et1);
		String 		wname	 = mtConfiger.docheckEditView(et2);
		String 		wpwd	 = mtConfiger.docheckEditView(et3);
		if(!wid.equals("null")&&!wpwd.equals("null")){
			workerinfo=new MWorkerinfo(wid, wname, wpwd);
		}else Toast.makeText(context, R.string.complete, Toast.LENGTH_SHORT).show();
		return workerinfo;
	}
}
