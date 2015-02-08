package com.csst.smarthome.activity.adapter;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.csst.smarthome.R;
import com.csst.smarthome.activity.device.CsstSHUseDeviceActivity;
import com.csst.smarthome.bean.CsstSHActionBean;
import com.csst.smarthome.bean.CsstSHDRCBean;
import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.message.MessageDef;
import com.lishate.message.MessageSeqFactory;
import com.lishate.message.baseMessage;
import com.lishate.message.rfcode.SendRFCodeReqMessage;
import com.lishate.net.UdpProcess;


/**
 * 设备列表设配器
 * @author liuyang
 */
public class CsstSHModelRunActionAdapter extends BaseAdapter {
	private Context context = null;
	private List<CsstSHActionBean> actionBeans = null;
	private byte[] errorPosition=  new byte[20];
	private int  iSelcectposition = 0;
	private boolean debug = true;
	private String TAG = "CsstSHModelRunActionAdapter";
	public CsstSHModelRunActionAdapter(Context context, List<CsstSHActionBean> actionBeans) {
		this.context = context;
		this.actionBeans = actionBeans;
		System.out.println("the size of actionBeans is "+this.actionBeans.size());
		for(int i=0;i<this.actionBeans.size();i++){
			System.out.println("the name of action name is"+actionBeans.get(i).getmActionName());
		}
		
	}
	public CsstSHModelRunActionAdapter(){
		
	}
	public final void setDevices(List<CsstSHActionBean> actionBeans){
		this.actionBeans = actionBeans;
		notifyDataSetInvalidated();
	}

	@Override
	public int getCount() {
		return null == actionBeans ? 0 : actionBeans.size();
	}
	
	@Override
	public Object getItem(int position) {
		return position;
	}
	public void clearErrorPosition(){
		for(int i=0;i<20;i++)
		errorPosition[i] = 0;
	}
	public void setErrorPostion(int i){
		try{
			errorPosition[i] = 1 ;
		}catch(Exception ex){
			System.out.println("the setErrorPostin is error");
		}
		
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	public void setSelectedPosition(int position){
		iSelcectposition = position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		System.out.println("the CsstSHActionAdapter getview ");
		CsstSHActionBean actionBean = actionBeans.get(position);
		convertView = LayoutInflater.from(context).inflate(R.layout.csst_model_run_listview, null);
		//Button btn_open = (Button) convertView.findViewById(R.id.item_open);
		TextView tvActionname =(TextView) convertView.findViewById(R.id.tvclockrepeat_listview);
		
		StringBuffer name = new StringBuffer();
		name.append(position+1);
		name.append("、");
		name.append(context.getResources().getString(R.string.csst_model_actionname));
		name.append(actionBean.getmActionName());
		tvActionname.setText(name.toString());
		convertView.setTag(actionBean);
		
		try{
			if(actionBean.getResultAction()==0){
//				tvActionname.setBackgroundColor(context.getResources().getColor(R.color.poppress));
				tvActionname.setBackgroundColor(context.getResources().getColor(R.color.red));
			}
		}catch(Exception ex){
			System.out.println("the errorPosition[position]==1 is error");
		}
		
		if(iSelcectposition==position){
			if(debug){
				System.out.println(TAG+"the keycode is "+actionBean.getmKeyCode() );
			}
//			new SendCodeTast(actionBean.getmKeyCode()).execute() ;
			tvActionname.setBackgroundColor(context.getResources().getColor(R.color.poppress));
//			tvActionname.setBackgroundColor(context.getResources().getColor(R.color.red));
		}
		return convertView;
	}
	
	
	public final class SendCodeTast extends AsyncTask<Void, Void, Boolean>{
		private String keycode = null;
		
		public SendCodeTast(String key) {
			keycode = key;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			String[] msgBuffer = keycode.split(",");
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
			baseMessage msg = UdpProcess.GetMsgReturn(slrm, sim);
			if(msg == null){
				return false;
			} else {
				//SendRFCodeRspMessage srfrm = (SendRFCodeRspMessage)msg;
				return true;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			int response = result ? R.string.csst_send_cmd_success : R.string.csst_send_cmd_fail;
			if(debug){
				System.out.println(TAG+"the response  is "+response);
			}
		}
	}

	
	
}