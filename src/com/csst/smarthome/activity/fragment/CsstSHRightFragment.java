package com.csst.smarthome.activity.fragment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.SmartStartActivity;
import com.csst.smarthome.activity.adapter.CsstSHAddModelAdapter;
import com.csst.smarthome.activity.adapter.CsstSHModelRunActionAdapter;
import com.csst.smarthome.bean.CsstSHActionBean;
import com.csst.smarthome.bean.CsstSHModelBean;
import com.csst.smarthome.dao.CsstSHModelTable;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.BaseCodeMessage;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.message.baseMessage;
import com.lishate.message.rfcode.SendRFCodeReqMessage;
import com.lishate.net.UdpJavaEncodingProcess;
import com.lishate.net.UdpProcess;


/**
 * 右边 的菜单界??
 * @author liuyang
 */
public class CsstSHRightFragment extends Fragment {

	
public static final String TAG = "CsstSHRightFragment";
	
	/** 添加情景模式按钮 */
	private Button mAddmodel = null;
	/** 情景模式列表 */
	private ListView mAddmodelListView = null;// 楼层列表
	/** 情景模式适配??*/
	private CsstSHAddModelAdapter addModelAdapter = null;
	/** 情景模式数据 */
	private List<CsstSHModelBean> dataBeans = null;
	/** 父Activity对象 */
	private SmartStartActivity mParentActivity = null;
	/** 当前楼层id */
	private int mCurmodel = -1;
	/**
	 * 上拉菜单
	 */
	private View popMenuLayout;	
	public static PopupWindow pop;
	private LinearLayout menuLayout;
	private Button btnEdit,btnClockOpen,btnCancel,btnDelete;
	/**
	 * model动作执行
	 */
	private ListView listviewrun =null;
	
	/** 动作数据 */
	private List<CsstSHActionBean> actionBeans = null;
	
	/** 动作配器 */
	private CsstSHModelRunActionAdapter actionAdapter = null;
	//用来设定只能等待执行完情景模式才能让客户点击情景模式 
	private boolean modelruning =false;
	/**
	 * 定时
	 */
	private Timer timerconfigwifi;
	private TimerTask tastconfigwifi;
	private Handler handlerconfigwifi;
	private int i = 0 ;
	private int iActionsize = 0;
	
	public boolean  modelrunlistflag = true;
	public  int j=0 ;
	/**
	 * 调试
	 */
	private boolean debug = true;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.csst_fragment_right_menu, null);
		initView(view);
		System.out.println(TAG+" onCreateView ");
		setListener();
		return view;
	}

	
	private void initView(View view) {
		// 父activity对象
		mParentActivity = (SmartStartActivity) getActivity();
		// 初始化添加情景控??
		mAddmodel = (Button) view.findViewById(R.id.btn_add_model);
		mAddmodelListView = (ListView) view.findViewById(R.id.lv_add_model);
		// 查询所有的情景模式
		dataBeans = CsstSHModelTable.getInstance().query(mParentActivity.getDataBase());
		// 当前楼层id
//		mCurmodel = mParentActivity.getCurFloorId();
		if(dataBeans!=null){
			System.out.println("the dataBeans count is "+dataBeans.size());	
		}
		addModelAdapter = new CsstSHAddModelAdapter(getActivity(), dataBeans);
		mAddmodelListView.setAdapter(addModelAdapter);
//		addModelAdapter.setCurFloor(mCurmodel);
	}
	
	private void setListener() {
		//添加情景模式
		mAddmodel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(mParentActivity, CsstSHAddModelName.class);
//				intent.putExtra("IP", modemIp);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				mParentActivity.finish();
			}
		});
		// 列表的点击事??
		mAddmodelListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				//先把原来的定时器给关掉要不然会出现bug
				//直到执行完了才能点击第二次
				//如果有动作才能执行
				if(!modelruning){
					modelruning = true;
				mParentActivity.getTempflag_Thread = false;
				i=0;
				try{
					Thread.sleep(500);
				}catch(Exception ex){
					System.out.println(ex.toString());
				}
				dispearpop();
				
				modelRun(dataBeans.get(position).getmodelName());
				
				}
				
			}
		});
		//楼层列表的长按监听事??
		mAddmodelListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				//获取当前名称
				//先把原来的定时器给关掉要不然会出现bug
				dispearpop();
				showPopMenu(dataBeans.get(position).getmodelName());
				return true;
			}
		});
	}

	/**
	 * 删除情景模式
	 * @param floor
	 */
	private final void deleteModel(final CsstSHModelBean model){
		AlertDialog.Builder builder = new AlertDialog.Builder(mParentActivity);
		builder.setTitle(model.getmodelName());
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(getString(R.string.csst_del_model_message, model.getmodelName()));
		builder.setNegativeButton(R.string.csst_cancel, null);
		builder.setPositiveButton(R.string.csst_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//删除场景数据??
				CsstSHModelTable.getInstance().delete(mParentActivity.getDataBase(), model);
				//更新当前的数据列??
				dataBeans = CsstSHModelTable.getInstance().query(mParentActivity.getDataBase());
				if (dataBeans != null && !dataBeans.isEmpty()){
					if (mCurmodel == model.getmodelId()){
						mCurmodel = dataBeans.get(0).getmodelId();
					}
				}else{
					mCurmodel = -1;
				}
				//存储当前楼层id
//				mParentActivity.setCurFloorId(mCurmodel);
				//更新楼层显示
				addModelAdapter.setDatas(dataBeans);
				addModelAdapter.setCurFloor(mCurmodel);
				//切换到主界面
				switchFragment();
			}
		});
		builder.show();
	}
	/**
	 * 修改楼层??
	 * @param floor
	 */
	private final void modifyFloorName(final CsstSHModelBean floor){
		final EditText inputServer = new EditText(getActivity());
		inputServer.setText(floor.getmodelName());
		inputServer.setSelection(floor.getmodelName().length());
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
					//场景名不能为??
					Toast.makeText(mParentActivity, R.string.csst_floorname_not_null, Toast.LENGTH_LONG).show();;
					return;
				}
				//判断该场景是否已经存??
				if (dataBeans != null && !dataBeans.isEmpty()){
					for (int i = 0; i < dataBeans.size(); i++){
						if (floorName.equals(dataBeans.get(i).getmodelName())){
							//%1$s 场景已存??
							Toast.makeText(mParentActivity, getString(R.string.csst_floorname_repeat, floorName), Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}
				//更新楼层??
				floor.setmodelName(floorName);
				//插入数据??
				CsstSHModelTable.getInstance().update(mParentActivity.getDataBase(), floor);
				//更新当前的数据列??
				dataBeans = CsstSHModelTable.getInstance().query(mParentActivity.getDataBase());
				//更新楼层显示
				addModelAdapter.setDatas(dataBeans);
				addModelAdapter.setCurFloor(mCurmodel);
			}
		});
		builder.show();
	}
	
   /**
    * 切换到主界面并将floorId带过去，然后刷新界面数据
    * @param floorId
    */
	private final void switchFragment() {
		System.out.println(TAG+" switchFragment ");
		mParentActivity.refreshView();
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * 上拉菜单
	 * @param name
	 */
	protected void showPopMenu(final String name) {
		 DisplayMetrics displayMetrics = new DisplayMetrics();       
		 mParentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);         
	 
		popMenuLayout = mParentActivity.getLayoutInflater().inflate(R.layout.csst_model_upmenu_layout, null);
		pop = new PopupWindow(popMenuLayout, mParentActivity.getWindowManager()
				.getDefaultDisplay().getWidth(), mParentActivity.getWindowManager()
				.getDefaultDisplay().getHeight());
		pop.showAtLocation(popMenuLayout, Gravity.BOTTOM, 0, 0);
		menuLayout = (LinearLayout) popMenuLayout
				.findViewById(R.id.menu_layout);
		btnEdit = (Button) popMenuLayout.findViewById(R.id.btnedit);
		btnClockOpen = (Button) popMenuLayout.findViewById(R.id.btnclockopen);
		btnDelete = (Button) popMenuLayout.findViewById(R.id.btndelete);
		btnCancel = (Button) popMenuLayout.findViewById(R.id.btn_cancel);

		btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				exitPopWindow();
				Intent intent = new Intent();
//				intent.setClass(mParentActivity, CsstSHAddModelName.class);
//				intent.putExtra("modelName", name);
				intent.setClass(mParentActivity, CsstSHAddModelListActionView.class);
				intent.putExtra("modelName", name);
				intent.putExtra("EditModel", "EditModel");
				//传入编辑标志 让List 界面选着不同布局文件
				startActivity(intent);
				mParentActivity.finish();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exitPopWindow();
			}
		});
		btnClockOpen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exitPopWindow();
				Intent intent = new Intent();
//				intent.setClass(mParentActivity, CsstSHAddModelName.class);
//				intent.putExtra("modelName", name);
				
//				intent.setClass(mParentActivity, CsstSHAddModelClockOpenAddClock.class);
				
				intent.setClass(mParentActivity, CsstSHAddModelClockOpenMain.class);
				intent.putExtra("modelName", name);
				//传入编辑标志 让List 界面选着不同布局文件
				startActivity(intent);
				mParentActivity.finish();
			}
		});
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exitPopWindow();
				deleteModel(CsstSHModelTable.getInstance().query(mParentActivity.getDataBase(),name));
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
		if(pop!=null){
			if(debug){
	  			   System.out.println(TAG+" exitPopWindow is here \n");
	  		   }
			pop.dismiss();
			popMenuLayout = null;
			btnEdit=null;
			btnClockOpen=null;
			btnDelete=null;
			btnCancel = null;
		}
	}
	
	  public void configwifitimer(){
			handlerconfigwifi=new Handler(){
				  @Override
					  public void handleMessage(Message msg) {
					   super.handleMessage(msg);
					   System.out.println("handleMessage  is here ");
						   mParentActivity.getTempflag_Thread = false;
						   actionAdapter.setSelectedPosition(i);
						   actionAdapter.notifyDataSetInvalidated();
						   if(i<iActionsize){
							   new modelrunTast(actionBeans.get(i).getmKeyCode()).execute() ;  
							   
						   }else{
							   //可以执行其他情景模式和本次情景模式
							   modelruning = false;
							   //重启查询线程
							   mParentActivity.getTempflag_Thread = true;
							   exitPopWindow();
							   Toast.makeText(mParentActivity, mParentActivity.getResources().getString(R.string.csst_model_runmodel_suc), Toast.LENGTH_LONG).show();
							   try{
									Thread.sleep(500);
								}catch(Exception ex){
									System.out.println(ex.toString());
								}
							   switchFragment();
						   }
					  }
				    };
		}
	/**
	 * 上拉菜单
	 * @param name
	 */
	protected void modelRun(final String name) {
		 DisplayMetrics displayMetrics = new DisplayMetrics();       
		 mParentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);         
	 
		popMenuLayout = mParentActivity.getLayoutInflater().inflate(R.layout.csst_model_runmain_listview, null);
		pop = new PopupWindow(popMenuLayout, mParentActivity.getWindowManager()
				.getDefaultDisplay().getWidth(), mParentActivity.getWindowManager()
				.getDefaultDisplay().getHeight()/2);
		pop.showAtLocation(popMenuLayout, Gravity.BOTTOM, 0, 0);
		menuLayout = (LinearLayout) popMenuLayout
				.findViewById(R.id.modelrunmain_layout);
		
		listviewrun = (ListView) popMenuLayout.findViewById(R.id.lv_modelrunmainlistview);
		
		
//查询数据库中是否有以这个名称存储的action 数据，有的话就显示出??
		if(CsstSHModelTable.getInstance().query(mParentActivity.getDataBase(),name)!=null){
			actionBeans = CsstSHModelTable.getInstance().getActionByModelId(mParentActivity.getDataBase(), CsstSHModelTable.getInstance().query(mParentActivity.getDataBase(),name).getmodelId());
			iActionsize = actionBeans.size();
			if(actionBeans!=null){
				actionAdapter = new CsstSHModelRunActionAdapter(mParentActivity, actionBeans);
				listviewrun.setAdapter(actionAdapter);
			}
			
			//开启定时器有数据的时候开开始定时
			configwifitimer();
			
			Message msg= handlerconfigwifi.obtainMessage(1);
	        handlerconfigwifi.sendMessage(msg);
	        actionAdapter.clearErrorPosition();
			
		}
	
        
		listviewrun.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
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
	
	 public boolean onKeyDown(int keyCode,KeyEvent event) {   
	       // 是否触发按键为back??  
	       if (keyCode == KeyEvent.KEYCODE_BACK) {   
	           // 弹出 退出确认框   
	    	   //将运行取??定时器取??
	    	   dispearpop();
	            return true;   
	        } 
	       return true;  
	    }
	  
	 //消除pop 和定时器
	 public void dispearpop(){
		   //将运行取??定时器取??
  	   if(timerconfigwifi!=null){
  		   if(debug){
  			   System.out.println(TAG+" dispearpop is here \n");
  		   }
  		   i=0;
  		   timerconfigwifi.cancel();
  	   }
  	   //上拉对话框取??
  	   exitPopWindow();
	 }
	 
	 
	 public final class modelrunTast extends AsyncTask<Void, Void, Boolean>{
			private String keycode = null;
			private boolean result = false;
			
			public modelrunTast(String key) {
				modelrunlistflag = true;
				keycode = key;
			}
			@Override
			protected Boolean doInBackground(Void... params) {
				System.out.println("modelrunTast  IN  ");
				String[] msgBuffer = keycode.split(",");
				if(msgBuffer[0].endsWith(getResources().getString(R.string.csst_adddevice_charge))){
					ServerItemModel sim = new ServerItemModel();
					sim.setIpaddress("218.244.129.177");
					sim.setPort(GobalDef.SERVER_PORT);
					BaseCodeMessage bcm = new BaseCodeMessage();
					bcm.Direct = MessageDef.BASE_MSG_FT_REQ;
					bcm.setFromId(GobalDef.MOBILEID);
					bcm.Seq = MessageSeqFactory.GetNextSeq();
					bcm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
					bcm.ToType = MessageDef.BASE_MSG_FT_HUB;
					
					
					bcm.setToId(Long.valueOf(mParentActivity.mMacAdress));
					
		  			bcm.setMCMD((byte) 4);
		  			bcm.setSCMD((byte) 1);
		  			System.out.println(" the cmd  is "+Integer.valueOf(msgBuffer[1]));
		  			//
		  			byte[] contentBuf = new byte[6];
		  			contentBuf[0]=(byte)0x07;
		  			contentBuf[1]=(byte)0x01;
		  			contentBuf[2]=(byte)Byte.valueOf(msgBuffer[2]);
		  			contentBuf[3]=(byte)Byte.valueOf(msgBuffer[3]);
		  			contentBuf[4]=(byte)0x03;
		  			contentBuf[5]=(byte)Byte.valueOf(msgBuffer[1]);
		  			for(int n=0;n<6;n++){
		  				System.out.println(TAG+ "contentbuf setting send" + n + " value: " + contentBuf[n]);
		  			}
		  			
		  			bcm.setContentBuf(contentBuf);
		  			System.out.println("sendChargCmd start to send MsgReturn ");
		  			BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
		  			System.out.println("sendChargCmd ready send get MsgReturn ");
		  			if(bcmrcv != null){
		  				//return true;
		  				if(bcmrcv.getContentBuf() != null){
		  					for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
		  						System.out.println(TAG+ "content reciver " + i + " value: " + bcmrcv.getContentBuf()[i]);
		  					}
		  				}
		  				return true;
		  			}
		  			else{
		  				result = false;
		  				
		  			}
				}else if(msgBuffer[0].endsWith(getResources().getString(R.string.csst_adddevice_switch))){

					ServerItemModel sim = new ServerItemModel();
					sim.setIpaddress("218.244.129.177");
					sim.setPort(GobalDef.SERVER_PORT);
					BaseCodeMessage bcm = new BaseCodeMessage();
					bcm.Direct = MessageDef.BASE_MSG_FT_REQ;
					bcm.setFromId(GobalDef.MOBILEID);
					bcm.Seq = MessageSeqFactory.GetNextSeq();
					bcm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
					bcm.ToType = MessageDef.BASE_MSG_FT_HUB;
					
					
					bcm.setToId(Long.valueOf(mParentActivity.mMacAdress));
					
		  			bcm.setMCMD((byte) 4);
		  			bcm.setSCMD((byte) 1);
		  			System.out.println(" the cmd  is "+Integer.valueOf(msgBuffer[1]));
		  			//
		  			byte[] contentBuf = new byte[6];
		  			contentBuf[0]=(byte)0x07;
		  			contentBuf[1]=(byte)0x01;
		  			contentBuf[2]=(byte)Byte.valueOf(msgBuffer[2]);
		  			contentBuf[3]=(byte)Byte.valueOf(msgBuffer[3]);
		  			contentBuf[4]=(byte)0x02;
		  			contentBuf[5]=(byte)Byte.valueOf(msgBuffer[1]);
		  			for(int n=0;n<6;n++){
		  				System.out.println(TAG+ "contentbuf setting send" + n + " value: " + contentBuf[n]);
		  			}
		  			
		  			bcm.setContentBuf(contentBuf);
		  			System.out.println("sendChargCmd start to send MsgReturn ");
		  			BaseCodeMessage bcmrcv = UdpJavaEncodingProcess.GetMsgReturn(bcm, sim);
		  			System.out.println("sendChargCmd ready send get MsgReturn ");
		  			if(bcmrcv != null){
		  				//return true;
		  				if(bcmrcv.getContentBuf() != null){
		  					for(int i = 0; i<bcmrcv.getContentBuf().length; i++){
		  						System.out.println(TAG+ "content reciver " + i + " value: " + bcmrcv.getContentBuf()[i]);
		  					}
		  				}
		  				return true;
		  			}
		  			else{
		  				result = false;
		  				
		  			}
					
					
					
					
				}else{
					ServerItemModel sim = new ServerItemModel();
					byte[] buffer = new byte[msgBuffer.length - 1];
					int temp = 0;
					for (int i = 1; i < msgBuffer.length; i++){
						temp = Integer.parseInt(msgBuffer[i].trim().substring(2), 16);
						buffer[i - 1] = (byte)(0xFF & temp);
					}
					sim.setIpaddress("218.244.129.177");
					sim.setPort(12188);
					SendRFCodeReqMessage slrm = new SendRFCodeReqMessage();
					slrm.Direct = MessageDef.BASE_MSG_FT_REQ;
					slrm.setFromId(GobalDef.MOBILEID);
					slrm.MsgType = MessageDef.MESSAGE_TYPE_RFCODE_SEND_REQ;
					slrm.Seq = MessageSeqFactory.GetNextSeq();
					slrm.FromType = MessageDef.BASE_MSG_FT_MOBILE;
					slrm.ToType = MessageDef.BASE_MSG_FT_HUB;
					slrm.setToId(Long.valueOf(msgBuffer[0]));
					slrm.setCodeBuf(buffer);
					if(debug){
						System.out.println(TAG+"start to send the  the msg ");
					}
					baseMessage msg = UdpProcess.GetMsgReturn(slrm, sim);
					if(debug){
						System.out.println(TAG+"end the send msg  is return the msg ");
					}
					modelrunlistflag = false;
					if(msg == null){
						actionAdapter.setErrorPostion(i);
						if(debug){
							System.out.println(TAG+"end the send msg  return is false ");
						}
						result = false;
					} else {
						//SendRFCodeRspMessage srfrm = (SendRFCodeRspMessage)msg;
						if(debug){
							System.out.println(TAG+"end the send msg  is return the msg  return is ture ");
						}
						result = true;
					}
				}
				
				System.out.println("modelrunTast  OUT  ");
				return result;
				
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				int response = result ? R.string.csst_send_cmd_success : R.string.csst_send_cmd_fail;
				Toast.makeText(mParentActivity, response, Toast.LENGTH_SHORT).show();
				
				if(debug){
					System.out.println(TAG+"the response  is "+response);
				}
				if(!result){
					actionBeans.get(i).setResultAction(0);
					actionAdapter = new CsstSHModelRunActionAdapter(mParentActivity, actionBeans);
					listviewrun.setAdapter(actionAdapter);
				}
				i++;
				
				if(i<iActionsize){//最后一个就不执行相应的延时啦
					try{
						   Thread.sleep(actionBeans.get(i-1).getmDelayTime()*1000);
					   }catch(Exception ex){
						   System.out.println(ex.toString());
					   }
				}
					
				 Message msg= handlerconfigwifi.obtainMessage(1);
		         handlerconfigwifi.sendMessage(msg);
				
			}
		}
	 
	 	
	 
	
	
}
