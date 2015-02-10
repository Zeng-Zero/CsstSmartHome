package com.csst.smarthome.activity.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.adapter.CsstSHAddHouseAdapter;
import com.csst.smarthome.bean.CsstSHFloolBean;
import com.csst.smarthome.dao.CsstSHFloorTable;

/**
 * 左边的菜单界�?
 * @author liuyang
 */
public class CsstSHWhichActionFloorFragment extends Fragment {
	
	public static final String TAG = "CsstSHLeftFragment";
	
	/** 添加楼层 */
	private Button mAddfloor = null;
	/** 楼层列表 */
	private ListView mAddFloorListView = null;// 楼层列表
	/** 楼层适配�?*/
	private CsstSHAddHouseAdapter addHouseAdapter = null;
	/** 楼层数据 */
	private List<CsstSHFloolBean> dataBeans = null;
	/** 父Activity对象 */
	private CsstSHAddModelWhichAction mParentActivity = null;
	/** 当前楼层id */
	private int mCurFloor = -1;
	
	public CsstSHWhichActionFloorFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.csst_fragment_left_menu, null);
		initView(view);
		setListener();
		return view;
	}
	
	private void initView(View view) {
		// 父activity对象
		mParentActivity = (CsstSHAddModelWhichAction) getActivity();
		// 初始化控�?
		mAddfloor = (Button) view.findViewById(R.id.btn_add_house);
		mAddFloorListView = (ListView) view.findViewById(R.id.lv_add_house);
		// 查询所有的楼层
		dataBeans = CsstSHFloorTable.getInstance().query(mParentActivity.getDataBase());
		// 当前楼层id
		mCurFloor = mParentActivity.getCurFloorId();
		addHouseAdapter = new CsstSHAddHouseAdapter(getActivity(), dataBeans);
		mAddFloorListView.setAdapter(addHouseAdapter);
		addHouseAdapter.setCurFloor(mCurFloor);
	}
	
	private void setListener() {
		//添加楼层
		mAddfloor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final EditText inputServer = new EditText(getActivity());
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(getString(R.string.csst_please_input_floorname));
				builder.setIcon(R.drawable.csst_floor);
				builder.setView(inputServer);
				builder.setNegativeButton(R.string.csst_cancel, null);
				builder.setPositiveButton(R.string.csst_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String floorName = inputServer.getText().toString().trim();
						//非空判断
						if (TextUtils.isEmpty(floorName)){
							//场景名不能为�?
							Toast.makeText(mParentActivity, R.string.csst_floorname_not_null, Toast.LENGTH_LONG).show();;
							return;
						}
						//判断该场景是否已经存�?
						if (dataBeans != null && !dataBeans.isEmpty()){
							for (int i = 0; i < dataBeans.size(); i++){
								if (floorName.equals(dataBeans.get(i).getFloorName())){
									//%1$s 场景已存�?
									Toast.makeText(mParentActivity, getString(R.string.csst_floorname_repeat, floorName), Toast.LENGTH_SHORT).show();
									return;
								}
							}
						}
						//插入数据�?
						long floorId=CsstSHFloorTable.getInstance().insert(mParentActivity.getDataBase(), new CsstSHFloolBean(floorName));
						if (floorId < 0){
							//数据存储错误，请与厂商联系！
							Toast.makeText(mParentActivity, R.string.csst_save_error, Toast.LENGTH_SHORT).show();
							return;
						}
						//判断当前是否已操作楼层，没操作直接用当前的楼�?
						if (mCurFloor < 0){
							mCurFloor = (int) floorId;
							//存储当前操作楼层
							mParentActivity.setCurFloorId(mCurFloor);
						}
						//更新当前的数据列�?
						dataBeans = CsstSHFloorTable.getInstance().query(mParentActivity.getDataBase());
						//更新楼层显示
						addHouseAdapter.setDatas(dataBeans);
						addHouseAdapter.setCurFloor(mCurFloor);
						switchFragment();
					}
				});
				builder.show();
			}
		});
		// 列表的点击事�?
		mAddFloorListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				mCurFloor = dataBeans.get(position).getFloorId();
				//存储当前操作楼层
				mParentActivity.setCurFloorId(mCurFloor);
				//更新适配�?
				addHouseAdapter.setCurFloor(mCurFloor);
				switchFragment();
			}
		});
		//楼层列表的长按监听事�?
		mAddFloorListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				final CsstSHFloolBean floor=dataBeans.get(position);
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(floor.getFloorName());
				builder.setItems(R.array.csst_floor_modify, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							//重命�?
							case 0:
								modifyFloorName(floor);
								break;
							//删除
							case 1:
								deleteFloor(floor);
								break;
						}
					}
				});
				builder.setNegativeButton(R.string.csst_cancel, null);
				Dialog d = builder.show();
				d.setCanceledOnTouchOutside(true);
				return false;
			}
		});
	}

	/**
	 * 删除楼层
	 * @param floor
	 */
	private final void deleteFloor(final CsstSHFloolBean floor){
		AlertDialog.Builder builder = new AlertDialog.Builder(mParentActivity);
		builder.setTitle(floor.getFloorName());
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(getString(R.string.csst_del_floor_message, floor.getFloorName()));
		builder.setNegativeButton(R.string.csst_cancel, null);
		builder.setPositiveButton(R.string.csst_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//删除场景数据�?
				CsstSHFloorTable.getInstance().delete(mParentActivity.getDataBase(), floor);
				//更新当前的数据列�?
				dataBeans = CsstSHFloorTable.getInstance().query(mParentActivity.getDataBase());
				if (dataBeans != null && !dataBeans.isEmpty()){
					if (mCurFloor == floor.getFloorId()){
						mCurFloor = dataBeans.get(0).getFloorId();
					}
				}else{
					mCurFloor = -1;
				}
				//存储当前楼层id
				mParentActivity.setCurFloorId(mCurFloor);
				//更新楼层显示
				addHouseAdapter.setDatas(dataBeans);
				addHouseAdapter.setCurFloor(mCurFloor);
				//切换到主界面
				switchFragment();
			}
		});
		builder.show();
	}
	
	/**
	 * 修改楼层�?
	 * @param floor
	 */
	private final void modifyFloorName(final CsstSHFloolBean floor){
		final EditText inputServer = new EditText(getActivity());
		inputServer.setText(floor.getFloorName());
		inputServer.setSelection(floor.getFloorName().length());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.csst_please_input_floorname));
		builder.setIcon(R.drawable.csst_floor);
		builder.setView(inputServer);
		builder.setNegativeButton(R.string.csst_cancel, null);
		builder.setPositiveButton(R.string.csst_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String floorName = inputServer.getText().toString().trim();
				//非空判断
				if (TextUtils.isEmpty(floorName)){
					//场景名不能为�?
					Toast.makeText(mParentActivity, R.string.csst_floorname_not_null, Toast.LENGTH_LONG).show();;
					return;
				}
				//判断该场景是否已经存�?
				if (dataBeans != null && !dataBeans.isEmpty()){
					for (int i = 0; i < dataBeans.size(); i++){
						if (floorName.equals(dataBeans.get(i).getFloorName())){
							//%1$s 场景已存�?
							Toast.makeText(mParentActivity, getString(R.string.csst_floorname_repeat, floorName), Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}
				//更新楼层�?
				floor.setFloorName(floorName);
				//插入数据�?
				CsstSHFloorTable.getInstance().update(mParentActivity.getDataBase(), floor);
				//更新当前的数据列�?
				dataBeans = CsstSHFloorTable.getInstance().query(mParentActivity.getDataBase());
				//更新楼层显示
				addHouseAdapter.setDatas(dataBeans);
				addHouseAdapter.setCurFloor(mCurFloor);
			}
		});
		builder.show();
	}
	
   /**
    * 切换到主界面并将floorId带过去，然后刷新界面数据
    * @param floorId
    */
	private final void switchFragment() {
		mParentActivity.refreshView();
	}

}
