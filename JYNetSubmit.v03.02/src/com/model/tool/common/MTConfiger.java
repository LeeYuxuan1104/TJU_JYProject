package com.model.tool.common;

import java.io.File;
import com.model.tool.view.MVEditTextWithDel;

import android.content.res.Resources;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;

public class MTConfiger {
	public static String IP = "210.12.45.200";
	public static String PORT = "8080";

	private String saveDir = Environment.getExternalStorageDirectory()
			.getPath() + File.separator + "jyFile", saveFolder = "photo",
			fParentPath, fState;
	public MTConfiger() {
		this.fParentPath = saveDir + File.separator + saveFolder+ File.separator;
		this.fState = Environment.getExternalStorageState();
	}

	public String getfState() {
		return fState;
	}

	public void setfState(String fState) {
		this.fState = fState;
	}

	public String getfParentPath() {
		return fParentPath;
	}


	public String docheckEditView(EditText view) {
		String text = view.getText().toString().trim();
		if (text.equals("") || "".equals(text)) {
			return "null";
		}
		return text;
	}
	public void setViewText(View view,String str) {
		String viewKind=view.getClass().getName();
		if(viewKind.equals("com.model.tool.view.MVEditTextWithDel")){
			MVEditTextWithDel etview=(MVEditTextWithDel) view;
			etview.setText(str);
		}
	}

	public String getImageName(String path) {
		if (path != null) {
			if (path.contains("/")) {
				return path.substring(path.lastIndexOf("/") + 1,
						path.indexOf("."));
			}
		}
		return null;
	}
	// 对两位的数字进行处理;
	public String get2bitData(int number) {
		if (number <= 9)
			return "0" + number;
		else if (number > 10 && number <= 99) {
			return String.valueOf(number);
		}
		return null;
	}
	//	颜色的变化;
	@SuppressWarnings("deprecation")
	public void setViewDrawable(boolean flag,Resources resources,View view,int drawable1,int drawable2){
		if(!flag) view.setBackgroundDrawable(resources.getDrawable(drawable1));
		else view.setBackgroundDrawable(resources.getDrawable(drawable2));
	}
}
