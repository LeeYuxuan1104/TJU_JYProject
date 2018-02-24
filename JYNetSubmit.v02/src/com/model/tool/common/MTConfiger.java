package com.model.tool.common;

import java.io.File;

import com.example.jynetsubmit.R;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;

public class MTConfiger {
//	public static String IP="172.23.1.183";
//	public static String IP="172.23.123.108";
	public static String IP="39.106.70.111";
//	public static String PORT="8888";
	public static String PORT="8080";
	public static String PROGRAM="PLibraryManager.v01";
	public static String FAIL="fail";
	public static int USER_RESIGN=1;
	public static int USER_LOGIN=2;
	public static int QUERY_PAGE_CONDITION=3;
	public static int DEL_ALL=4;
	public static int DEL_ITEM=5;
	public static int ADD_ITEM=6;
	public static int Query_All=8;
	public static int Query_ITEM=9;
	/////
	private String saveDir 	= 	Environment.getExternalStorageDirectory().getPath()+File.separator+"jyFile",
			   saveFolder	=	"photo",
			   fParentPath,
			   fState;
	
	public String getfState() {
		return fState;
	}

	public void setfState(String fState) {
		this.fState = fState;
	}

	public String getfParentPath() {
		return fParentPath;
	}

	public MTConfiger() {
		this.fParentPath=saveDir+File.separator+saveFolder+File.separator;
		this.fState=Environment.getExternalStorageState();
	}
	
	public String docheckEditView(EditText view){
		String text=view.getText().toString().trim();
		if(text.equals("")||"".equals(text)){
			return "null";
		}
		return text;
	}
	//	获取按钮上的文本
	public String docheckButton(Button view){
		String text=view.getText().toString().trim();
		if(text.equals("")||"".equals(text)){
			return "null";
		}
		return text;
	}
	public void doclearEditView(EditText view){
		view.setText("");
	}
	public String getImageName(String path){
		if(path!=null){			
			if(path.contains("/")){
				return path.substring(path.lastIndexOf("/")+1,path.indexOf("."));
			}
		}
		return null;
	}
	
	public void exitSystem(final Activity context){
		Builder builder=new Builder(context);
		builder.setTitle(R.string.exit);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				context.finish();
			}
		});
		builder.setNegativeButton(R.string.no, null);
		builder.create();
		builder.show();
	}
	//	对两位的数字进行处理;
	public String get2bitData(int number){
		if(number<=9)
			return "0"+number;
		else if(number>10&&number<=99){
			return String.valueOf(number);
		}
		return null;	
	}
}
