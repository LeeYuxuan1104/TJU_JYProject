package com.model.entity;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jynetsubmit.R;
import com.model.tool.common.MTConfiger;

public class MMineinfo {
	private String xh;
	private String rq;
	private String zcid;
	private String gcid;
	private String jz;
	private String mz;
	private String dzkh;
	private String sjxm;
	private String hzh;
	private String qfh;
	private String img;
	private String state;
	private MTConfiger mtConfiger;

	public MMineinfo(String xh, String rq, String zcid, String gcid, String jz,
			String mz, String dzkh, String sjxm, String hzh, String qfh,
			String img, String state) {
		this.xh = xh;
		this.rq = rq;
		this.zcid = zcid;
		this.gcid = gcid;
		this.jz = jz;
		this.mz = mz;
		this.dzkh = dzkh;
		this.sjxm = sjxm;
		this.hzh = hzh;
		this.qfh = qfh;
		this.img = img;
		this.state = state;
		if (mtConfiger == null) {
			mtConfiger = new MTConfiger();
		}
	}

	public MMineinfo() {
		if (mtConfiger == null) {
			mtConfiger = new MTConfiger();
		}
	}

	public String getXh() {
		return xh;
	}

	public void setXh(String xh) {
		this.xh = xh;
	}

	public String getRq() {
		return rq;
	}

	public void setRq(String rq) {
		this.rq = rq;
	}

	public String getZcid() {
		return zcid;
	}

	public void setZcid(String zcid) {
		this.zcid = zcid;
	}

	public String getGcid() {
		return gcid;
	}

	public void setGcid(String gcid) {
		this.gcid = gcid;
	}

	public String getJz() {
		return jz;
	}

	public void setJz(String jz) {
		this.jz = jz;
	}

	public String getMz() {
		return mz;
	}

	public void setMz(String mz) {
		this.mz = mz;
	}

	public String getDzkh() {
		return dzkh;
	}

	public void setDzkh(String dzkh) {
		this.dzkh = dzkh;
	}

	public String getSjxm() {
		return sjxm;
	}

	public void setSjxm(String sjxm) {
		this.sjxm = sjxm;
	}

	public String getHzh() {
		return hzh;
	}

	public void setHzh(String hzh) {
		this.hzh = hzh;
	}

	public String getQfh() {
		return qfh;
	}

	public void setQfh(String qfh) {
		this.qfh = qfh;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	// 进行数据的获取;
	// 01.添加有关对象;
	public MMineinfo getMineinfo(Context context, EditText et1, EditText et2,
			EditText et3, EditText et4, EditText et5, EditText et6,
			EditText et7, EditText et8, EditText et9, EditText et10,
			EditText et11) {
		MMineinfo mMineinfo = null;
		String rq = mtConfiger.docheckEditView(et1);
		String zcid = mtConfiger.docheckEditView(et2);
		String gcid = mtConfiger.docheckEditView(et3);
		String jz = mtConfiger.docheckEditView(et4);
		String mz = mtConfiger.docheckEditView(et5);
		String dzkh = mtConfiger.docheckEditView(et6);
		String sjxm = mtConfiger.docheckEditView(et7);
		String hzh = mtConfiger.docheckEditView(et8);
		String qfh = mtConfiger.docheckEditView(et9);
		String img = mtConfiger.docheckEditView(et10);
		String state = mtConfiger.docheckEditView(et11);
		if (!rq.equals("null") && !zcid.equals("null") && !gcid.equals("null")
				&& !jz.equals("null") && !mz.equals("null")
				&& !dzkh.equals("null") && !sjxm.equals("null")
				&& !hzh.equals("null") && !qfh.equals("null")
				&& !img.equals("null") && !state.equals("null")) {
			mMineinfo = new MMineinfo(xh,rq, zcid, gcid, jz, mz, dzkh, sjxm, hzh,qfh, img, state);
		} else Toast.makeText(context, R.string.complete, Toast.LENGTH_SHORT).show();
		return mMineinfo;
	}

	// 02.获得信息的详情;
	public String getMineinfoItem(Map<String, String> obj) {
		String content = "";
		if (obj != null) {
			String id = obj.get("id").toString();
			String xh = obj.get("xh").toString();
			String rq = obj.get("rq").toString();
			String zcid = obj.get("zcid").toString();
			String gcid = obj.get("gcid").toString();
			String jz = obj.get("jz").toString();
			String mz = obj.get("mz").toString();
			String dzkh = obj.get("dzkh").toString();
			String sjxm = obj.get("sjxm").toString();
			String hzh = obj.get("hzh").toString();
			String qfh = obj.get("qfh").toString();
			String img = obj.get("img").toString();
			Log.i("MyLog", img);
			int nstate = Integer.parseInt(obj.get("state").toString());
			String state = "finished";
			if (nstate == 0) {
				state = "unfinished";
			}
			content =
					"[ID]   " + id + "\r\n"+ 
					"[Container barcode]   "+xh+"\r\n"+ 
					"[Truck Loading Time]\r\n"+ rq+ "\r\n" + 
					"[Truck No.]   " + zcid + "\r\n"+ 
					"[Trailer No.]   " + gcid + "\r\n" +
					"[Net Weight]   "+ jz + "kg" + "\r\n" + 
					"[Gross Weight]   " + mz + "kg"+ "\r\n" + 
					"[Truck electronic card]   " + dzkh + "\r\n"+ 
					"[Driver Name]   " + sjxm + "\r\n" + 
					"[Passport]   "+ hzh + "\r\n" + 
					"[Seal No.]   " + qfh + "\r\n"+ 
					"[State]   " + state;
		} else
			return null;
		return content;
	}

	// 03.获取名称信息;
	public MMineinfo getMineinfo(Context context,EditText et12, EditText et1, EditText et2,
			EditText et3, EditText et4, EditText et5, EditText et6,
			EditText et7, EditText et8, EditText et9, String imgname,
			EditText et11) {
		MMineinfo mMineinfo = null;
		String xh =mtConfiger.docheckEditView(et12);
		String rq = mtConfiger.docheckEditView(et1);
		String zcid = mtConfiger.docheckEditView(et2);
		String gcid = mtConfiger.docheckEditView(et3);
		String jz = mtConfiger.docheckEditView(et4);
		String mz = mtConfiger.docheckEditView(et5);
		String dzkh = mtConfiger.docheckEditView(et6);
		String sjxm = mtConfiger.docheckEditView(et7);
		String hzh = mtConfiger.docheckEditView(et8);
		String qfh = mtConfiger.docheckEditView(et9);
		String img = imgname;
		String state = "0";
		if (!xh.equals("null")&&!rq.equals("null") && !zcid.equals("null") && !gcid.equals("null")
				&& !jz.equals("null") && !mz.equals("null")
				&& !dzkh.equals("null") && !sjxm.equals("null")
				&& !hzh.equals("null") && !qfh.equals("null")) {
			mMineinfo = new MMineinfo(xh,rq, zcid, gcid, jz, mz, dzkh, sjxm, hzh,
					qfh, img, state);
		} else
			Toast.makeText(context, R.string.complete, Toast.LENGTH_SHORT)
					.show();
		return mMineinfo;
	}
}
