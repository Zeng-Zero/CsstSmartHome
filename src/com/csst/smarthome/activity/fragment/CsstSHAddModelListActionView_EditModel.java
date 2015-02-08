package com.csst.smarthome.activity.fragment;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.SmartStartActivity;
import com.csst.smarthome.activity.adapter.CsstSHActionAdapter;
import com.csst.smarthome.bean.CsstSHActionBean;
import com.csst.smarthome.bean.CsstSHFloolBean;
import com.csst.smarthome.common.ICsstSHConstant;
import com.csst.smarthome.common.ICsstSHInitialize;
import com.csst.smarthome.dao.CsstSHActionTable;
import com.csst.smarthome.dao.CsstSHDataBase;
import com.csst.smarthome.dao.CsstSHModelTable;

/**
 * 中控配置
 * @author liuyang
 */
public class CsstSHAddModelListActionView_EditModel extends Activity implements ICsstSHInitialize, ICsstSHConstant{

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
	/** 动作配器 */
	private CsstSHActionAdapter actionAdapter = null;
	/** 情景模式名称标签 */
	private Button mBtnaddaction = null;
	private String TAG = "CsstSHAddModelListActionView_EditModel";
	private boolean debug = true;
	
	
	private BackBtnListener mBackBtnListener = null;
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
		setContentView(R.layout.csst_addmodel_editorlistview_editmodel);
		initDataSource();
		initWidget();
		initWidgetState();
		initWidgetListener();
		addWidgetListener();
	}
	
	@Override
	public void initDataSource() {
		Intent intent = getIntent();
		if (null != intent){
			modelName =(String) intent.getSerializableExtra("modelName");
		}
		if(debug){
			System.out.println(TAG+"initDataSource form intent the modelName is "+modelName);
		}
		
	}

	@Override
	public void initWidget() {
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mTVTitle = (TextView) findViewById(R.id.mTVTitle);
		mTVTitle = (TextView) findViewById(R.id.mTVTitle);
		mtvModelName = (TextView) findViewById(R.id.tvmodelname);
		metModelName = (EditText) findViewById(R.id.etmodelname);
		mBtnDone = (Button) findViewById(R.id.mBtnDone);
		mlistviewmodel = (ListView) findViewById(R.id.lv_modellistview);
		mBtnaddaction = (Button) findViewById(R.id.btaddaction);
		// 数据库工具类
		csstSHDataBase = new CsstSHDataBase(this);
		// 数据库对象
		mDb = csstSHDataBase.getWritDatabase();
		//  以modelName 查询该情景模式的实例化
		//然后再用实例化获取modelID，再用modelID查询 查询改模式下的所有的动作
		if(debug){
			System.out.println("query the actionBeans the name is "+modelName);
		}
		//查询数据库中是否有以这个名称存储的action 数据，有的话就显示出来
		if(CsstSHModelTable.getInstance().query(mDb,modelName)!=null){
			actionBeans = CsstSHModelTable.getInstance().getActionByModelId(mDb, CsstSHModelTable.getInstance().query(mDb,modelName).getmodelId());
			actionAdapter = new CsstSHActionAdapter(CsstSHAddModelListActionView_EditModel.this, actionBeans);
			mlistviewmodel.setAdapter(actionAdapter);
		}
	}

	@Override
	public void initWidgetState() {
		//设置标题
//		mTVTitle.setText(R.string.csst_addmodel_title);
		mTVTitle.setText(modelName);
		metModelName.setText(modelName);
		//隐藏完成按钮
//		mBtnDone.setVisibility(View.GONE);
		//链接热点
	}

	@Override
	public void initWidgetListener() {
		mBackBtnListener = new BackBtnListener();
		mBtnDoneListener = new DoneBtnListener();
		mBtnAddActionListener = new AddActionBtnListener();
	}

	@Override
	public void addWidgetListener() {
		mBtnCancel.setOnClickListener(mBackBtnListener);
		mBtnDone.setOnClickListener(mBtnDoneListener);
		mBtnaddaction.setOnClickListener(mBtnAddActionListener);
		// 列表的点击事件
		mlistviewmodel.setOnItemClickListener(new OnItemClickListener() {
						@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long arg3) {
//					mCurFloor = dataBeans.get(position).getFloorId();
				}
			});

		//楼层列表的长按监听事件
		mlistviewmodel.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long arg3) {
					showPopMenu(actionBeans.get(position));
					return true;
			}
		});
	}
	
	
	
	/**
	 * 返回事件
	 */
	private final void backEvent(){
		Intent intent = new Intent();
		intent.setClass(CsstSHAddModelListActionView_EditModel.this, SmartStartActivity.class);
		startActivity(intent);
		CsstSHAddModelListActionView_EditModel.this.finish();
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
			Intent intent = new Intent();
			intent.setClass(CsstSHAddModelListActionView_EditModel.this, SmartStartActivity.class);
			startActivity(intent);
			CsstSHAddModelListActionView_EditModel.this.finish();
		}
	}
	
	private final class AddActionBtnListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(CsstSHAddModelListActionView_EditModel.this, CsstSHAddModelAddAction.class);
			intent.putExtra("modelName", modelName);
			//是否是编辑
			intent.putExtra("modelName", modelName);
			
			startActivity(intent);
			CsstSHAddModelListActionView_EditModel.this.finish();
			
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
		super.onResume();
	}
	/**
	 * 上拉菜单
	 * @param name
	 */
	protected void showPopMenu(final CsstSHActionBean action) {
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
				
				
				Intent intent = new Intent();
//				intent.setClass(mParentActivity, CsstSHAddModelName.class);
//				intent.putExtra("modelName", name);
//				intent.setClass(mParentActivity, CsstSHAddModelListActionView_EditModel.class);
//				intent.putExtra("modelName", name);
				startActivity(intent);
//				mParentActivity.finish();
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
			public void onClick(View v) {}
		});
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CsstSHActionTable.getInstance().delete(mDb, action);
				
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
		pop.dismiss();
		popMenuLayout = null;
		btnEdit=null;
		btnInsert=null;
		btnDelete=null;
		btnCancel = null;
	}
	
	

}
