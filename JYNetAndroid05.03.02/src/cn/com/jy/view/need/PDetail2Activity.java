package cn.com.jy.view.need;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.jy.model.helper.MTConfigHelper;
import cn.com.jy.model.helper.MTFileHelper;
import cn.com.jy.model.helper.MTGetOrPostHelper;
import cn.com.jy.model.helper.MTImgHelper;
import cn.com.jy.model.helper.MTSQLiteHelper;
import cn.com.jy.model.helper.MTScreenHelper;

import cn.com.jy.activity.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

@SuppressWarnings("deprecation")
public class PDetail2Activity extends Activity implements OnClickListener{
    //	全程序内容;
    private Context	 		mContext;	//	上下文信息;
    //	主要的控件;
    private TextView 		tvTopic,btnBack,btnFunction;

    private Gallery	 		mGallery;	//	画廊按钮;
    private	ProgressDialog	mDialog;	// 对话框;
    private String 			_id, sql, bid, gid, folderPath, imgs;

    //	数据库管理;
    private MTSQLiteHelper	mSqLiteHelper;
    private SQLiteDatabase 	mDB;		  //  数据库件;
    private Cursor 		   	mCursor;      //  数据库遍历签;
    private MTConfigHelper	mConfigHelper;// 配置项;
    private MTGetOrPostHelper mGetOrPostHelper;

    private MTFileHelper	mtFileHelper;
    private MTImgHelper		mImgHelper;   // 图片辅助类;
    //	图片的集合列表;
    private List<BitmapDrawable> listBD = null;
    private ArrayList<String>    listMapName,listInfo;
    private MyThread		mThread;	  // 线程;
    /*设置内容*/
    private Display 		  mDisplay; // 为获取屏幕宽、高
    private Window 			  mWindow;
    private MTScreenHelper 	  mtScreenHelper;
    private ListView		vlist;
    private InfoAdapter		cInfoAdapter;

    /*中间显示的层次*/
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int nFlag=msg.what;
            mDialog.dismiss();
            switch (nFlag) {
                case MTConfigHelper.NTAG_SUCCESS:
                    Toast.makeText(mContext, R.string.tip_success,Toast.LENGTH_SHORT).show();
                    break;
                case MTConfigHelper.NTAG_FAIL:
                    Toast.makeText(mContext, R.string.tip_fail,Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.detail3);
        //	添加控件;
        initView();
        //	添加事件监听;
        initEvent();
    }
    //	控件初始化;
    private void initView(){
        tvTopic			=	(TextView) findViewById(R.id.tvTopic);
        vlist			=	(ListView) findViewById(R.id.listView);

        btnBack			=	(TextView) findViewById(R.id.btnBack);
        btnFunction		=	(TextView) findViewById(R.id.btnFunction);
        mGallery		=	(Gallery) findViewById(R.id.gallery);;
    }
    //	事件监听初始化;
    private void initEvent(){
        mContext		=	PDetail2Activity.this;
        mConfigHelper	=	new MTConfigHelper();
        mGetOrPostHelper=	new MTGetOrPostHelper();
        mtFileHelper	=	new MTFileHelper();
        mImgHelper		=	new MTImgHelper();

        //	设置内容;
        mDisplay 		= 	getWindowManager().getDefaultDisplay();
        mWindow			= 	getWindow();
        mtScreenHelper  =	new MTScreenHelper(mDisplay, mWindow);
        int screenHeight=   mtScreenHelper.getScreenHeight();
        int nImgHeight	=	(int) ((screenHeight)*0.5);

        //	控件的初始化;
        btnFunction.setVisibility(View.GONE);
        tvTopic.setText("港口信息详情");

        //	获取id;
        Intent	mIntent =	getIntent();
        Bundle	mBundle =	mIntent.getExtras();
        _id				=	mBundle.getString("_id");
        imgs			=	mBundle.getString("imgs");

        //	数据库加载;
        mSqLiteHelper	=	new MTSQLiteHelper(mContext);
        mDB 			= 	mSqLiteHelper.getmDB();
        //	添加事件监听;
        btnBack.setOnClickListener(this);
        listMapName		=	mtFileHelper.getFileNamesByList(imgs,"_");
        int 	size	=	listMapName.size();

        //	数据信息加载;
        doLoadData(size);
        //	提货信息路径;
        folderPath	=	mConfigHelper.getfParentPath()+bid+File.separator+"port"+File.separator+gid;
        if(size>0){
            //	承装图片的容器;
            listBD		=	mImgHelper.getBitmap01_2(folderPath, imgs);
            cInfoAdapter=	new InfoAdapter(mContext, listInfo);
            vlist.setAdapter(cInfoAdapter);
            //	设置图片适配器;
            mGallery.setAdapter(new ImageAdaper(mContext, listBD,nImgHeight));
            //	图片长按的上传;
            mGallery.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view,
                                               final int position, long id) {
                    Builder builder=new Builder(mContext);
                    builder.setTitle("提示信息:");
                    builder.setPositiveButton("上传", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if(mThread==null){
                                // 进度条的内容;
                                final CharSequence strDialogTitle = getString(R.string.tip_dialog_wait);
                                final CharSequence strDialogBody  = getString(R.string.tip_dialog_done);
                                mDialog = ProgressDialog.show(mContext, strDialogTitle, strDialogBody,true);
                                mThread	= new MyThread(position);
                                mThread.start();
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.action_no, null);
                    builder.create();
                    builder.show();
                    return false;
                }
            });
        }
    }
    //	信息加载;
    private void doLoadData(int size){
        sql		=	"select * from portinfo where _id="+_id;
        mCursor	= 	mDB.rawQuery(sql, null);
        listInfo=	new ArrayList<String>();
        while (mCursor.moveToNext()) {
            //	信息加载;
            gid=mCursor.getString(mCursor.getColumnIndex("barcode")).toString(); //
            String ptoportdate=mCursor.getString(mCursor.getColumnIndex("ptoportdate")).toString(); //
            String preinvoicedate_port=mCursor.getString(mCursor.getColumnIndex("preinvoicedate_port")).toString(); //
            String pjinchangdate=mCursor.getString(mCursor.getColumnIndex("pjinchangdate")).toString(); //
            String ppackingtime=mCursor.getString(mCursor.getColumnIndex("ppackingtime")).toString(); //
            String sfpxpz=mCursor.getString(mCursor.getColumnIndex("sfpxpz")).toString(); //
            String bssj=mCursor.getString(mCursor.getColumnIndex("bssj")).toString(); //
            String fcchgk=mCursor.getString(mCursor.getColumnIndex("fcchgk")).toString(); // 拖车(取)发车时间(国内时间)
            String dcjsgkdz=mCursor.getString(mCursor.getColumnIndex("dcjsgkdz")).toString(); //
            String dcdsgkdz=mCursor.getString(mCursor.getColumnIndex("dcdsgkdz")).toString(); // 铁路车型(国内信息)
            String dsgkdz=mCursor.getString(mCursor.getColumnIndex("dsgkdz")).toString();
            String startdate=mCursor.getString(mCursor.getColumnIndex("startdate")).toString();
            String blhtl=mCursor.getString(mCursor.getColumnIndex("blhtl")).toString();
            String dgtrainwagonno=mCursor.getString(mCursor.getColumnIndex("dgtrainwagonno")).toString();
            String dgtraintype=mCursor.getString(mCursor.getColumnIndex("dgtraintype")).toString();
            String dgtrainwaybillno=mCursor.getString(mCursor.getColumnIndex("dgtrainwaybillno")).toString();
            String dgtrainsinglenum=mCursor.getString(mCursor.getColumnIndex("dgtrainsinglenum")).toString();
            String dgtrainsingleton=mCursor.getString(mCursor.getColumnIndex("dgtrainsingleton")).toString();
            String cargostatusport=mCursor.getString(mCursor.getColumnIndex("cargostatusport")).toString();
            String dgtrainwagonkg=mCursor.getString(mCursor.getColumnIndex("dgtrainwagonkg")).toString();
            String dgtrainstartdate=mCursor.getString(mCursor.getColumnIndex("dgtrainstartdate")).toString();
            // + // 图片
            bid=mCursor.getString(mCursor.getColumnIndex("busiinvcode")).toString();
            // + // 图片
            bid=mCursor.getString(mCursor.getColumnIndex("busiinvcode")).toString();
            listInfo.add("二维码值\t"+gid);
            listInfo.add("到港时间\t"+ptoportdate);
            listInfo.add("换单时间\t"+preinvoicedate_port);
            listInfo.add("进场时间\t"+pjinchangdate);
            listInfo.add("装箱时间\t"+ppackingtime);
            listInfo.add("是否偏心/偏重\t"+sfpxpz);
            listInfo.add("报数时间\t"+bssj);
            listInfo.add("发车车号\t"+fcchgk);
            listInfo.add("单车件数\t"+dcjsgkdz);
            listInfo.add("单车吨数\t"+dcdsgkdz);
            listInfo.add("车数\t"+dsgkdz);
            listInfo.add("发车时间\t"+startdate);
            listInfo.add("铁路班列号\t"+blhtl);
            listInfo.add("铁路车皮号 \t"+dgtrainwagonno);
            listInfo.add("铁路车型\t"+dgtraintype);
            listInfo.add("铁路运单号\t"+dgtrainwaybillno);
            listInfo.add("单车件数\t"+dgtrainsinglenum);
            listInfo.add("单车吨数\t"+dgtrainsingleton);
            listInfo.add("货物状态\t"+cargostatusport);
            listInfo.add("铁路车皮标重\t"+dgtrainwagonkg);
            listInfo.add("发车时间\t"+dgtrainstartdate);
            listInfo.add("图片个数\t"+size+"张");
        }
        if(mCursor!=null){
            mCursor.close();
        }
    }
    //	适配器的类;
    public class ImageAdaper extends BaseAdapter{
        private Context mContext;
        private int 	mGalBackgroundItem;
        private int 	nSize;
        private List<BitmapDrawable> listBD;
        private int 	nImgWidth;
        private int 	nImgHeight;

        public ImageAdaper(Context mContext,List<BitmapDrawable> list,int nImgHeight){
            this.mContext = mContext;
            this.listBD	  = list;
            this.nSize	  = list.size();
            this.nImgHeight=nImgHeight;
            this.nImgWidth=this.nImgHeight;
            TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);
            mGalBackgroundItem 	  = typedArray.getResourceId( R.styleable.Gallery_android_galleryItemBackground, 0);
            typedArray.recycle();
        }

        public int getCount() {
            return this.nSize;
        }

        public Object getItem(int position) {
            return this.listBD.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageview = new ImageView(this.mContext);
            imageview.setImageDrawable(this.listBD.get(position));

            imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            imageview.setLayoutParams(new Gallery.LayoutParams(nImgWidth,nImgHeight));
            imageview.setBackgroundResource(mGalBackgroundItem);
            notifyDataSetChanged();
            return imageview;
        }
    }

    public class InfoAdapter extends BaseAdapter{
        private Context context;
        private LayoutInflater inflater;
        private ArrayList<String> list;
        private int nSize;

        public InfoAdapter(Context context,ArrayList<String> list) {
            this.context	=	context;
            this.inflater	=	LayoutInflater.from(this.context);
            this.list		=	list;
            this.nSize		=	this.list.size();
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return nSize;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder	=	null;
            if(view==null){
                holder	=	new ViewHolder();
                view	=	inflater.inflate(R.layout.item04, null);
                holder.tvName=(TextView) view.findViewById(R.id.tvTopic);
                view.setTag(holder);
            }else{
                holder	=	(ViewHolder) view.getTag();
            }
            holder.tvName.setText(getItem(position).toString());
            holder.tvName.setBackgroundColor(Color.parseColor("#AEEEEE"));
            if(position%2==0){
                holder.tvName.setBackgroundColor(Color.parseColor("#A2CD5A"));
            }


            return view;
        }
        private class ViewHolder{
            private TextView tvName;
        }
    }

    //	线程的自定义形式;
    class MyThread extends Thread{
        private String url,
                response;
        private int position;
        public MyThread(int position) {
            this.position=position;
        }
        @Override
        public void run() {
            String path		=	folderPath+File.separator+listMapName.get(this.position)+".jpg";
            url				=	"http://"+MTConfigHelper.TAG_IP_ADDRESS+":"+MTConfigHelper.TAG_PORT+"/"+MTConfigHelper.TAG_PROGRAM+"/upPhoto";
            response		=	mGetOrPostHelper.uploadFile(url,path,listMapName.get(position));
            int nFlag= MTConfigHelper.NTAG_FAIL;
            if(!response.endsWith("fail")){
                nFlag= MTConfigHelper.NTAG_SUCCESS;
            }
            mHandler.sendEmptyMessage(nFlag);
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
        mSqLiteHelper.doCloseDataBase();
    }

    @Override
    public void onClick(View view) {
        int nVid=view.getId();
        switch (nVid) {
            case R.id.btnBack:
                finish();
                break;

            default:
                break;
        }
    }
}
