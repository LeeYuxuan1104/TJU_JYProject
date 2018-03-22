package com.model.tool.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.jynetsubmit.R;
import com.model.tool.common.MTConfiger;
import com.model.tool.view.MVCalendar.OnCalendarClickListener;
import com.model.tool.view.MVCalendar.OnCalendarDateChangedListener;

/**
 * edited by LeeYuxuan
 * **/
@SuppressLint("ViewConstructor")
public class MVPopupWindows extends PopupWindow {
	private String date;
	private int hour;
	private int minute;
	
	@SuppressWarnings("deprecation")
	public MVPopupWindows(Context mContext,final MTConfiger configer, View parent,final EditText et) {

		View view = View.inflate(mContext, R.layout.popupwindow_calendar,null);
		view.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
		
		//	外围框子;
		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.push_bottom_in_1));
		
		//	设置宽度;
		setWidth(LayoutParams.FILL_PARENT);
		//	设置高度;
		setHeight(LayoutParams.FILL_PARENT);
		//	设置背景;
		setBackgroundDrawable(new BitmapDrawable());
		//	获得焦点;
		setFocusable(true);
		//	获得出去;
		setOutsideTouchable(true);
		setContentView(view);
		//	绘图定位置;
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		//	更新绘图点；
		update();
		//////////////////////////////
		Calendar c = Calendar.getInstance();//可以对每个时间域单独修改

		 
		hour = c.get(Calendar.HOUR_OF_DAY); 
		minute = c.get(Calendar.MINUTE); 
		 
		/*控件的自定义的内容*/
		//	月份的显示;
		final TextView popupwindow_calendar_month = (TextView) view.findViewById(R.id.popupwindow_calendar_month);
		//	日历定义类;
		final MVCalendar calendar = (MVCalendar) view.findViewById(R.id.popupwindow_calendar);
		//	定义按钮类;
		TextView popupwindow_calendar_bt_enter =  (TextView) view.findViewById(R.id.popupwindow_calendar_bt_enter);
		//	相应的时间内容的控件;
		//	下一分钟;
		RelativeLayout vmnext	=(RelativeLayout) view.findViewById(R.id.minute_next);
		//	上一分钟;
		RelativeLayout vmlast	=(RelativeLayout) view.findViewById(R.id.minute_last);
		//	分钟显示;
		final TextView 	   vmnumber =(TextView) view.findViewById(R.id.minute_num);
		//	下一小时;
		RelativeLayout vhnext	=(RelativeLayout) view.findViewById(R.id.hour_next);
		//	上一小时;
		RelativeLayout vhlast	=(RelativeLayout) view.findViewById(R.id.hour_last);
		//	小时显示;
		final TextView	   vhnumber =(TextView) view.findViewById(R.id.hour_num);
		vhnumber.setText(configer.get2bitData(hour));
		vmnumber.setText(configer.get2bitData(minute));
		
		//	下一分钟点击;
		vmnext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(minute==59) minute=0;
				else minute++;
				vmnumber.setText(configer.get2bitData(minute));
			}
		});
		//	上一分钟点击;
		vmlast.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(minute==0) minute=59;
				else minute--;
				vmnumber.setText(configer.get2bitData(minute));				
			}
		});		
		//	下一小时点击;
		vhnext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(hour==24) hour=1;
				else hour++;
				vhnumber.setText(configer.get2bitData(hour));
				
			}
		});
		//	上一小时点击;
		vhlast.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(hour==1) hour=24;
				else hour--;
				vhnumber.setText(configer.get2bitData(hour));
			}
		});
		///
		popupwindow_calendar_month.setText(calendar.getCalendarYear() + "-"+ calendar.getCalendarMonth());

		if (null != date) {

			int years = Integer.parseInt(date.substring(0,
					date.indexOf("-")));
			int month = Integer.parseInt(date.substring(
					date.indexOf("-") + 1, date.lastIndexOf("-")));
			popupwindow_calendar_month.setText(years + "-" + month);

			calendar.showCalendar(years, month);
			calendar.setCalendarDayBgColor(date,
					R.drawable.calendar_date_focused);
		}

		List<String> list = new ArrayList<String>(); // 设置标记列表
		list.add("2014-04-01");
		list.add("2014-04-02");
		calendar.addMarks(list, 0);

		// 监听所选中的日期
		calendar.setOnCalendarClickListener(new OnCalendarClickListener() {

			public void onCalendarClick(int row, int col, String dateFormat) {
				int month = Integer.parseInt(dateFormat.substring(
						dateFormat.indexOf("-") + 1,
						dateFormat.lastIndexOf("-")));

				if (calendar.getCalendarMonth() - month == 1// 跨年跳转
						|| calendar.getCalendarMonth() - month == -11) {
					calendar.lastMonth();

				} else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
						|| month - calendar.getCalendarMonth() == -11) {
					calendar.nextMonth();

				} else {
					calendar.removeAllBgColor();
					calendar.setCalendarDayBgColor(dateFormat,
							R.drawable.calendar_date_focused);
					date = dateFormat+" "+vhnumber.getText().toString()+":"+vmnumber.getText().toString();// 最后返回给全局 date
				}
			}
		});

		// 监听当前月份
		calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
			public void onCalendarDateChanged(int year, int month) {
				popupwindow_calendar_month.setText(year + "-" + month);
			}
		});

		// 上月监听按钮
		RelativeLayout popupwindow_calendar_last_month = (RelativeLayout) view
				.findViewById(R.id.popupwindow_calendar_last_month);
		popupwindow_calendar_last_month.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				calendar.lastMonth();
			}

		});

		// 下月监听按钮
		RelativeLayout popupwindow_calendar_next_month = (RelativeLayout) view
				.findViewById(R.id.popupwindow_calendar_next_month);
		popupwindow_calendar_next_month.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				calendar.nextMonth();
			}
		});

		// 关闭窗口
		popupwindow_calendar_bt_enter.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				et.setText(date);
				dismiss();
			}
		});
	}
}
