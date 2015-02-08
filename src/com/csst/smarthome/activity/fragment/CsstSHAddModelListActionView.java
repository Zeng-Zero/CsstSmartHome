package com.csst.smarthome.activity.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.SmartStartActivity;
import com.csst.smarthome.activity.adapter.CsstSHActionAdapter;
import com.csst.smarthome.bean.CsstSHActionBean;
import com.csst.smarthome.bean.CsstSHModelBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHActionTable;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHModelTable;

/**
 * 中控配置
 * @author liuyang
 */
public class CsstSHAddModelListActionView extends Activity implements ICsstSHInitialize, ICsstSHConstant{

	/** 返回按钮 */
	private Button mBtnCancel = null;
	/** 完成按钮 */
	private Button mBtnDone = null;
	/** 标题 */
	private TextView mTVTitle = null;
	/** 动作列表 */
	private ListView mlistviewmodel = null;
	/** 动作数据 */
	private List<CsstSHActionBean> actionBeans = null;
	
	/** 插入数据所用数据库 */
	private List<CsstSHActionBean> insertActionBeans = null;
	
	/**
	 * 插入标志位，等程序回来重新加载之前删除的数据
	 */
	private boolean insertActionFlag = false;
	/** 动作配器 */
	private CsstSHActionAdapter actionAdapter = null;
	/** 情景模式名称标签 */
	private Button mBtnaddaction = null;
	private String TAG = "CsstSHAddModelListActionView";
	private boolean debug = true;
	
	
	private DoneBtnListener mBtnDoneListener = null;
	private AddActionBtnListener mBtnAddActionListener = null;
	/** 数据库实例化 */
	private CsstSHDataBase csstSHDataBase = null;
	/** 数据库对象 */
	private SQLiteDatabase mDb = null;
	
	/**
	 * 上层传过来的modelname
	 */
	private String modelName = null;
	/**
	 * 上层传过来是否是编辑界面的
	 * 如果是编辑界面显示不同
	 */
	private String EditModel = null;
	private boolean EditModelflag = false;
	
	/**
	 * 上层传过来的modelname
	 */
	private TextView mtvModelName ;
	private EditText metModelName;
	/**
	 * 上拉菜单
	 */
	private View popMenuLayout;	
	private PopupWindow pop;
	private LinearLayout menuLayout;
	private Button btnEdit,btnInsert,btnCancel,btnDelete;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(debug){
			System.out.println(TAG+"the onCreate");
		}
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (null != intent){
			modelName =(String) intent.getSerializableExtra("modelName");
			EditModel = (String) intent.getSerializableExtra("EditModel");
		}
		if(debug){
			System.out.println(TAG+"initDataSource form intent the modelName is "+modelName);
		}
		if(EditModel.equals("EditModel")){
			setContentView(R.layout.csst_addmodel_editorlistview_editmodel);
			EditModelflag=true;
		}else if(EditModel.equals("ADDModel")){
			EditModelflag=false;
			setContentView(R.layout.csst_modeladd_editorlistview);
		}
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
	}
	
	@Override
	public void initDataSource() {
	
		
	}

	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mBtnCancel.setVisibility(View.GONE);
		if(EditModelflag){
			mtvModelName = (TextView) findViewById(R.id.tvmodelname);
			metModelName = (EditText) findViewById(R.id.etmodelname);
			metModelName.setText(modelName);
		}
		mTVTitle = (TextView) findViewById(R.id.mTVTitle);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mlistviewmodel = (ListView) findViewById(R.id.lv_modellistview);
		mBtnaddaction = (Button) findViewById(R.id.btaddaction);
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		//初始化插入数据
		insertActionBeans = new ArrayList<CsstSHActionBean>();
		//  以modelName 查询该情景模式的实例化
		//然后再用实例化获取modelID，再用modelID查询 查询改模式下的所有的动作
		if(debug){
			System.out.println("query the actionBeans the name is "+modelName);
		}
		//查询数据库中是否有以这个名称存储的action 数据，有的话就显示出来
		if(CsstSHModelTable.getInstance().query(mDb,modelName)!=null){
			actionBeans = CsstSHModelTable.getInstance().getActionByModelId(mDb, CsstSHModelTable.getInstance().query(mDb,modelName).getmodelId());
			if(actionBeans!=null){
				actionAdapter = new CsstSHActionAdapter(CsstSHAddModelListActionView.this, actionBeans);
				mlistviewmodel.setAdapter(actionAdapter);
			}
			
		}
	}

	@Override
	public void initWidgetState() {
		//设置标题
//		mTVTitle.setText(R.string.csst_addmodel_title);
		mTVTitle.setText(modelName);
		//隐藏完成按钮
//		mBtnDone.setVisibility(View.GONE);
		//链接热点
	}

	@Override
	public void initWidgetListener() {
//		mBackBtnListener = new BackBtnListener();
		mBtnDoneListener = new DoneBtnListener();
		mBtnAddActionListener = new AddActionBtnListener();
	}

	@Override
	public void addWidgetListener() {
//		mBtnCancel.setOnClickListener(mBackBtnListener);
		mBtnDone.setOnClickListener(mBtnDoneListener);
		mBtnaddaction.setOnClickListener(mBtnAddActionListener);
	// 列表的点击事件
			mlistviewmodel.setOnItemClickListener(new OnItemClickListener() {
							@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long arg3) {
//							mCurFloor = dataBeans.get(position).getFloorId();
							Intent intent = new Intent();
							intent.setClass(CsstSHAddModelListActionView.this, CsstSHAddModelAddAction.class);
							intent.putExtra("modelName", modelName);
							intent.putExtra("actionBean", actionBeans.get(position));
							startActivity(intent);
					}
				});

				//楼层列表的长按监听事件
				mlistviewmodel.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view,
							int position, long arg3) {
							showPopMenu(actionBeans.get(position),position);
							return true;
					}
				});
	}
	
	/**
	 * 返回事件
	 */
	private final void backEvent(){
		Intent intent = new Intent();
		intent.setClass(CsstSHAddModelListActionView.this, SmartStartActivity.class);
		startActivity(intent);
		CsstSHAddModelListActionView.this.finish();
	}
	
	/**
	 * 返回按钮监听器
	 * @author liuyang
	 */
	private final class BackBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			backEvent();
		}
	}
	
	/**
	 * 完成按钮监听器
	 * @author liuyang
	 */
	private final class DoneBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if(EditModel!=null){//表明是进入了修改界面如果是进入了修改界面再去修改动作是还存在bug 不能修改名称的
			if(EditModel.equals("EditModel")){
				if(metModelName.getText().toString().equals("")){
					Toast.makeText(CsstSHAddModelListActionView.this, R.string.csst_model_name_null_tip, Toast.LENGTH_LONG).show();;
				}else if(!metModelName.getText().toString().equals(modelName)){//如果是编辑界面就更新
					CsstSHModelBean modelbean = CsstSHModelTable.getInstance().query(mDb,modelName);
					modelbean.setmodelName(metModelName.getText().toString());
					CsstSHModelTable.getInstance().update(mDb,modelbean);
					}
				}
			}
			Intent intent = new Intent();
			intent.setClass(CsstSHAddModelListActionView.this, SmartStartActivity.class);
			startActivity(intent);
			CsstSHAddModelListActionView.this.finish();
		}
	}
	
	private final class AddActionBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(CsstSHAddModelListActionView.this, CsstSHAddModelAddAction.class);
			intent.putExtra("modelName", modelName);
			startActivity(intent);
//			CsstSHAddModelListActionView.this.finish();
			
		}
	}
	@Override
	protected void onDestroy() {
		if(debug){
			System.out.println(TAG+"the onDestroy");
		}
		mDb.close();
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		if(debug){
			System.out.println(TAG+"the onResume");
		}
		//刷新列表
		
		
		
		
//查询数据库中是否有以这个名称存储的action 数据，有的话就显示出来
		if(CsstSHModelTable.getInstance().query(mDb,modelName)!=null){
			if(insertActionFlag){
				//如果进入了插入insertacitionBeans 就不为空 将之前存储的添加到数据库中
				if(insertActionBeans!=null){
					for(int i=0;i<insertActionBeans.size();i++){
						// 实例化ActionBean 插入数据库
						insertActionBeans.get(i).setmModelId(CsstSHModelTable.getInstance().query(mDb,modelName).getmodelId());
						long insert = CsstSHActionTable.getInstance().insert(mDb,insertActionBeans.get(i));
					}
					//然后清空数据 和标志位一面下次重复添加
					insertActionBeans.clear();
					insertActionFlag = false;
				}
			}
			actionBeans = CsstSHModelTable.getInstance().getActionByModelId(mDb, CsstSHModelTable.getInstance().query(mDb,modelName).getmodelId());
		
			if(actionBeans!=null){
				actionAdapter = new CsstSHActionAdapter(CsstSHAddModelListActionView.this, actionBeans);
				mlistviewmodel.setAdapter(actionAdapter);
			}
		}
		super.onResume();
	}
	
	
	/**
	 * 上拉菜单
	 * @param name
	 */
	protected void showPopMenu(final CsstSHActionBean action,final int position) {
		 DisplayMetrics displayMetrics = new DisplayMetrics();       
		 getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);         
	 
		popMenuLayout = getLayoutInflater().inflate(R.layout.csst_model_upmenu_layout, null);
		pop = new PopupWindow(popMenuLayout, getWindowManager()
				.getDefaultDisplay().getWidth(), getWindowManager()
				.getDefaultDisplay().getHeight());
		pop.showAtLocation(popMenuLayout, Gravity.BOTTOM, 0, 0);
		menuLayout = (LinearLayout) popMenuLayout
				.findViewById(R.id.menu_layout);
		btnEdit = (Button) popMenuLayout.findViewById(R.id.btnedit);
		btnInsert = (Button) popMenuLayout.findViewById(R.id.btnclockopen);
		btnInsert.setText(R.string.csst_model_insert);
		btnDelete = (Button) popMenuLayout.findViewById(R.id.btndelete);
		btnCancel = (Button) popMenuLayout.findViewById(R.id.btn_cancel);

		btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exitPopWindow();
				Intent intent = new Intent();
				intent.setClass(CsstSHAddModelListActionView.this, CsstSHAddModelAddAction.class);
				intent.putExtra("modelName", modelName);
				intent.putExtra("actionBean", action);
				startActivity(intent);
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exitPopWindow();
			}
		});
		btnInsert.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//添加之前先清空以免上次死机时保存的东西
				insertActionFlag = false;
				if(insertActionBeans!=null){
					if(insertActionBeans.size()>0){
						insertActionBeans.clear();
					}
				}
				if(debug){
					System.out.println(TAG+"the position is "+position+" the actionBeans size is "+actionBeans.size());
				}
				for(int i =position;i<actionBeans.size();i++){
					insertActionBeans.add(actionBeans.get(i));
					CsstSHActionTable.getInstance().delete(mDb, actionBeans.get(i));
				}
				insertActionFlag = true;
				Intent intent = new Intent();
				intent.setClass(CsstSHAddModelListActionView.this, CsstSHAddModelAddAction.class);
				intent.putExtra("modelName", modelName);
				startActivity(intent);
				exitPopWindow();
				
			}
		});
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CsstSHActionTable.getInstance().delete(mDb, action);
				
				
		//查询数据库中是否有以这个名称存储的action 数据，有的话就显示出来
				if(CsstSHModelTable.getInstance().query(mDb,modelName)!=null){
					actionBeans = CsstSHModelTable.getInstance().getActionByModelId(mDb, CsstSHModelTable.getInstance().query(mDb,modelName).getmodelId());
					//在删除最后一条的action 之后还需要跟新列表actionBeans就没有了。
					if(actionBeans!=null){
						actionAdapter = new CsstSHActionAdapter(CsstSHAddModelListActionView.this, actionBeans);
						mlistviewmodel.setAdapter(actionAdapter);
					}else{
						actionAdapter = new CsstSHActionAdapter();
						mlistviewmodel.setAdapter(actionAdapter);
					}
				}
				exitPopWindow();
				
			}
		});
		menuLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				exitPopWindow();
			}
		});
		
	}
	private void exitPopWindow() {
		if(pop!=null)
		pop.dismiss();
		popMenuLayout = null;
		btnEdit=null;
		btnInsert=null;
		btnDelete=null;
		btnCancel = null;
	}
	
	public boolean onKeyDown(int keyCode,KeyEvent event) {   
	       // 是否触发按键为back键   
	       if (keyCode == KeyEvent.KEYCODE_BACK) {   
	           // 弹出 退出确认框
	    	   exitPopWindow();
	    	   backEvent();
	            return true;   
	        } 
	       return true; 
	    }
	
	
	

}
