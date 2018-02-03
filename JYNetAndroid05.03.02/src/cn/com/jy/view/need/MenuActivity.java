package cn.com.jy.view.need;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.jy.activity.R;
import cn.com.jy.model.helper.MTConfigHelper;
import cn.com.jy.model.helper.MTSharedpreferenceHelper;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity implements OnClickListener{
	private TextView tvTopic;    // 标题窗口;
	private TextView btnBack,    // 返回按钮;
					 btnFunction;// 功能按钮;
	private MTSharedpreferenceHelper mSpHelper;  // 首选项存储;
	private Context  mContext;
	private Intent	 mIntent;
	//	列表按钮;
	private int[] 	 nImages={R.drawable.tihuo2,R.drawable.gangkou2,R.drawable.xiangguan2,R.drawable.kouan2,R.drawable.qianshou2};
	private String[] sNames ={"提  货","港  口","箱  管","口  岸","签  收"};
	private ListView mListView;
	private List<Map<String, Object>> list;
	private MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		initView();
		initEvent();
	}
	//	控件声明;
	private void initView(){
		//	列表按钮;
		mListView=(ListView) findViewById(R.id.listView);
		//	按钮控件;
		tvTopic  =(TextView) findViewById(R.id.tvTopic);
		btnBack  =(TextView) findViewById(R.id.btnBack);
		btnFunction= (TextView) findViewById(R.id.btnFunction);
	}
	//	事件声明;
	private void initEvent(){
		
		mContext   = MenuActivity.this;
		//	返回键按钮;
		btnBack.setOnClickListener(this);
		//	功能键按钮隐藏;
		btnFunction.setVisibility(View.GONE);
		tvTopic.setText("");
		//	内容信息初始化操作;
		mSpHelper  = new MTSharedpreferenceHelper(this, MTConfigHelper.CONFIG_SELF,MODE_APPEND);
		//	数据信息的加载;
		list	   = doLoad();
		mAdapter=new MyAdapter(mContext, list);
		//	将适配器与列表进行绑定
		mListView.setAdapter(mAdapter);		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				
					switch (position) {
					case 0:
						mIntent=new Intent(mContext, GetgoodsInformationActivity.class);
						break;
					case 1:
						mIntent=new Intent(mContext, PortActivity.class);
						break;
					case 2:
						mIntent=new Intent(mContext, BoxActivity.class);
						break;
					case 3:
						mIntent=new Intent(mContext, HarborInformationActivity.class);
						break;
					case 4:
						mIntent=new Intent(mContext, SignInformationActivity.class);
						break;
					default:
						break;
					}
					startActivity(mIntent);
			}
		});
	}
	//	列表信息进行加载的列表信息;
	private List<Map<String, Object>> doLoad(){
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		for(int i=0;i<5;i++){
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("name", sNames[i]);
			map.put("img", nImages[i]);
			list.add(map);
		}
		return list;
	}
	@Override
	public void onClick(View view) {
		int nVid=view.getId();
		switch (nVid) {
		//	退出信息操作;
		case R.id.btnBack:
			Builder builder=new Builder(this);
			builder.setTitle("提示");
			builder.setMessage("退出登录?");
			builder.create();
			builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					mSpHelper.putValue(MTConfigHelper.CONFIG_SELF_WID,null);
					mSpHelper.putValue(MTConfigHelper.CONFIG_SELF_WNAME,null);
					mSpHelper.putValue(MTConfigHelper.CONFIG_SELF_WCALL,null);
					mSpHelper.putValue(MTConfigHelper.CONFIG_SELF_WPWD,null);
					mSpHelper.putValue(MTConfigHelper.CONFIG_SELF_WNOTE,null);
					Toast.makeText(mContext, R.string.result_exit, Toast.LENGTH_SHORT).show();				
					finish();
				}
			});
			
			builder.setNegativeButton(R.string.action_no, null);
			builder.show();
			break;
		default:
			break;
		}
	}
	/*自定义适配器*/
	class MyAdapter extends BaseAdapter{
		private List<Map<String, Object>> list;
		private LayoutInflater inflater;
		private int size;
		
		public MyAdapter(Context context,List<Map<String, Object>> list) {
			this.list=list;
			inflater=LayoutInflater.from(context);
			this.size=this.list.size();
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return size;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if(convertView==null){
				viewHolder=new ViewHolder();
				convertView=inflater.inflate(R.layout.item_menu,null);
				//将控件保存到viewHolder中
				viewHolder.imageView=(ImageView)convertView.findViewById(R.id.iv);
				viewHolder.textView=(TextView) convertView.findViewById(R.id.name);
				//通过setTag将ViewHoler与convertView绑定
				convertView.setTag(viewHolder);			
			}else{
				viewHolder=(ViewHolder) convertView.getTag();					
			}
				
			viewHolder.imageView.setImageResource(Integer.parseInt(list.get(position).get("img").toString()));
			viewHolder.textView.setText(list.get(position).get("name").toString());
			return convertView;
			
		}
		//	建立标签内容;
		class ViewHolder{
			public ImageView imageView;
			public TextView	 textView;
		}
	}
}
