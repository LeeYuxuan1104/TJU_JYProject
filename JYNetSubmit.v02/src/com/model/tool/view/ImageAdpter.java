package com.model.tool.view;

import java.util.List;

import com.example.jynetsubmit.R;
import com.model.entity.MPhoto;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

public class ImageAdpter extends BaseAdapter {
	//	初始值的变换符内容;
	private boolean isChice[];
	//	上下文的内容;
	private Context context;
	//	屏幕的宽度;
	private float screenWidth;
	//	屏幕的高度;
	private float screenHeight;
	//	承装照片的列表;
	private List<MPhoto> listPhoto;
	//	长度值;
	private int   nSize;
	
	public List<MPhoto> getListPhoto() {
		return listPhoto;
	}

	public ImageAdpter(List<MPhoto> listPhoto, Context context,float screenWidth,float screenHeight) {
		//	装载图片的容器集合;
		this.listPhoto=listPhoto;
		//	图片的容器大小;
		this.nSize	  =listPhoto.size();
		//	进行状态的变换;
		isChice = new boolean[nSize];
		//	状态的初始化;
		for (int i = 0; i < nSize; i++) {
			isChice[i] = false;
		}
		//	上下文内容的赋值;
		this.context = context;
		//放大為屏幕的1/2大小  
		this.screenWidth  = screenWidth;  
		this.screenHeight = screenHeight; 
	}

	@Override
	public int getCount() {
		return nSize;
	}

	@Override
	public Object getItem(int position) {
		return listPhoto.get(position);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(final int position, View arg1, ViewGroup arg2) {
		View view = arg1;
		GetView getView = null;
		if (view == null) {
			view = LayoutInflater.from(context)
					.inflate(R.layout.img_item, null);
			getView = new GetView();
			getView.imageView = (ImageView) view.findViewById(R.id.image_item);
			getView.vDetail   = (Button) view.findViewById(R.id.detail_item);
			getView.vCheck 	  = (CheckBox) view.findViewById(R.id.state);
			view.setTag(getView);
		} else {
			getView = (GetView) view.getTag();
		}
		getView.imageView.setImageDrawable(getView(position));
		//	单点按钮的事件响应;
		getView.vDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String path=listPhoto.get(position).getFilepath();
				showImage(context, path);
				
			}
		});
		//	对状态信息进行相应的编辑;
		getView.vCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean flag) {
				listPhoto.get(position).setFlag(flag);
			}
		});	
		return view;
	}

	static class GetView {
		ImageView imageView;
		CheckBox  vCheck;
		Button	  vDetail;
	}

	@SuppressWarnings("deprecation")
	private LayerDrawable getView(int post) {
		//////////
		String 	imagePath=listPhoto.get(post).getFilepath();
		Bitmap 	bm 		 = BitmapFactory.decodeFile(imagePath);
		int 	width 	 = bm.getWidth();  
		int 	height 	 = bm.getHeight();  
 
		//放大為屏幕的1/2大小  
		float scaleWidth = screenWidth/2/width;  
		float scaleHeight = screenHeight/2/height;  
 
		// 取得想要缩放的matrix參數  
		Matrix matrix = new Matrix();  
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm  = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,true);

		//
		Bitmap bitmap2 	 = null;
		LayerDrawable la = null;
		if (isChice[post] == true) {
			bitmap2 = BitmapFactory.decodeResource(context.getResources(),R.drawable.editable_mode_checked_tag);
		}
		
		if (bitmap2 != null) {
			Drawable[] array = new Drawable[2];
			array[0] = new BitmapDrawable(newbm);
			array[1] = new BitmapDrawable(bitmap2);
			la = new LayerDrawable(array);
			la.setLayerInset(0, 0, 0, 0, 0);
			la.setLayerInset(1, 0, 65, 65, 0);
		} else {
			Drawable[] array = new Drawable[1];
			array[0] = new BitmapDrawable(newbm);
			la = new LayerDrawable(array);
			la.setLayerInset(0, 0, 0, 0, 0);
		}
		return la;
	}
	@SuppressWarnings("deprecation")
	private void showImage(Context mContext,String imagePath){
		Builder 	builder= new Builder(mContext);
		builder.setTitle("Picture show");
		View  		view   = LayoutInflater.from(context).inflate(R.layout.img_show, null);
		ImageView 	iv	   = (ImageView) view.findViewById(R.id.imgShow);
        Bitmap 		bm 	   = BitmapFactory.decodeFile(imagePath);
        int 		width  = bm.getWidth();  
        int 		height = bm.getHeight();  
  
        //放大為屏幕的1/2大小  
        float scaleWidth = screenWidth/2/width;  
        float scaleHeight = screenHeight/2/height;  
  
        // 取得想要缩放的matrix參數  
        Matrix matrix = new Matrix();  
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,true);

        iv.setBackgroundDrawable(new BitmapDrawable(this.context.getResources(),newbm));
        builder.setNegativeButton(R.string.ok, null);
        builder.setView(view);
        builder.create();
        builder.show();
	}
	public boolean chiceState(int post) {
		isChice[post] = isChice[post] == true ? false : true;
		this.notifyDataSetChanged();
		return isChice[post];
	}
}
