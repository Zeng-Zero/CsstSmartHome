package com.csst.smarthome.activity.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.csst.smarthome.R;
import com.csst.smarthome.bean.CsstSHDeviceBean;


/**
 * 设备列表设配器
 * @author liuyang
 */
public class CsstSHDeviceAdapter extends BaseAdapter {
	private String TAG = "CsstSHDeviceAdapter";
	private Context context = null;
	private List<CsstSHDeviceBean> deviceBeans = null;
	
	
	private final int REMOTE_TV =1 ;
	/** 空调类型 */
	private final int REMOTE_AC=2 ;
	/** 机顶盒类型 */
	private final int REMOTE_SB=3 ;
	/** DVD类型 */
	private final int REMOTE_DVD=4 ;
	/** 音响 */
	private final int REMOTE_SOUND=5 ;
	//窗帘
	private final int REMOTE_CURTAIN=6 ;
	/** 风扇类型 */
	private final int REMOTE_ELECTFAN=7 ;
	/** 自定义Key类型 */
	private final int CUSTOM_KEY=8 ;

	public CsstSHDeviceAdapter(Context context, List<CsstSHDeviceBean> deviceBeans) {
		this.context = context;
		this.deviceBeans = deviceBeans;
	}
	
	public final void setDevices(List<CsstSHDeviceBean> deviceBeans){
		this.deviceBeans = deviceBeans;
		notifyDataSetInvalidated();
	}

	@Override
	public int getCount() {
		return null == deviceBeans ? 0 : deviceBeans.size();
	}
	
	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		CsstSHDeviceBean deviceBean = deviceBeans.get(position);
		convertView = LayoutInflater.from(context).inflate(R.layout.csst_deviceitem, null);
		//Button btn_open = (Button) convertView.findViewById(R.id.item_open);
		ImageView img = (ImageView) convertView.findViewById(R.id.item_img);
		TextView tvname =(TextView) convertView.findViewById(R.id.devicename);
		Button btnpower =(Button) convertView.findViewById(R.id.devicepower);
		
		tvname.setText(deviceBean.getDeviceName());
		//用PATH表明是插座还是面板开关  RCTtype 的几位面板和插座
		System.out.println("the best is name is device is "+deviceBean.getDeviceName());
		String path = deviceBean.getDeviceIconPath();
		Log.d(TAG,"device is path "+path+"the power is "+deviceBean.isDeviceIconPersert());
		if(path!=null){
			if(path.equals(context.getResources().getString(R.string.csst_adddevice_switch))){
				
				System.out.println("device is path switch"+path+" the getRcType is  "+deviceBean.getRCTypeId());
				if(deviceBean.getRCTypeId()==1){//1位面板
					img.setImageResource(R.drawable.switch1);
				}
				if(deviceBean.getRCTypeId()==2){//2位面板
					img.setImageResource(R.drawable.switch2);		
				}
				if(deviceBean.getRCTypeId()==3){//3位面板
					img.setImageResource(R.drawable.switch3);
				}
				if(deviceBean.getRCTypeId()==4){//4位面板
					img.setImageResource(R.drawable.switch3);
				}
				if(deviceBean.isDeviceIconPersert()){//表示开启电源
					btnpower.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.powron_device));
				}else{//表示关闭电源
					btnpower.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.powroff_device));
				}
			}else if(path.equals(context.getResources().getString(R.string.csst_adddevice_charge))){
				//如果是插座的话；在判断Device 中最后一位是否为真如果是真的，就是绑定的器件；
				System.out.println("the deviceBean.isSearched() is "+deviceBean.isSearched());
				
				if(deviceBean.isSearched()==1){
					
					
					if(deviceBean.isDeviceIconPersert()){//表示开启电源
						btnpower.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.powron_device));
					}else{//表示关闭电源
						btnpower.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.powroff_device));
					}
					int rctype = deviceBean.getRCTypeId();
					switch (rctype) {
					case REMOTE_TV:
						img.setImageResource(R.drawable.tv);
						break;
						/** 空调类型 */
					case REMOTE_AC:
						img.setImageResource(R.drawable.ac);
						break;
						/** 机顶盒类型 */
					case REMOTE_SB:
						img.setImageResource(R.drawable.tv);
						break;
						/** DVD类型 */
					case REMOTE_DVD:
						img.setImageResource(R.drawable.tv);
						break;
						/** 音响 */
					case REMOTE_SOUND:
						img.setImageResource(R.drawable.sound);
						break;
						/** 窗帘类型 */
					case REMOTE_CURTAIN:
						img.setImageResource(R.drawable.curtain);
						break;
						/** 风扇类型 */
					case REMOTE_ELECTFAN:
						img.setImageResource(R.drawable.elect_fan);
						break;
					default:
						break;
					}
				}else{
					System.out.println("device is path socket "+path);
					if(deviceBean.getRCTypeId()==1){//插座
						img.setImageResource(R.drawable.charge);
					}else if(deviceBean.getRCTypeId()==5){//排插
						img.setImageResource(R.drawable.charge);
					}
					if(deviceBean.isDeviceIconPersert()){//表示开启电源
						btnpower.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.powron_device));
					}else{//表示关闭电源
						btnpower.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.powroff_device));
					}
				}
				
				
			}else {
				System.out.println("device is path is not socket and switch "+path);
				if (!TextUtils.isEmpty(path)){
					if (deviceBean.isDeviceIconPersert()){
						img.setImageResource(Integer.valueOf(path));
					}else{
						img.setImageBitmap(BitmapFactory.decodeFile(path));
					}
				}else{
					img.setImageResource(R.drawable.csst_device_default);
				}
			}
		}else{//为了添加设备的时候得设定为空显示默认图片 debug 到时候需要默认图片 根据CRTYPE 类型确定默认图片
			int rctype = deviceBean.getRCTypeId();
			switch (rctype) {
			case REMOTE_TV:
				img.setImageResource(R.drawable.tv);
				break;
				/** 空调类型 */
			case REMOTE_AC:
				img.setImageResource(R.drawable.ac);
				break;
				/** 机顶盒类型 */
			case REMOTE_SB:
				img.setImageResource(R.drawable.tv);
				break;
				/** DVD类型 */
			case REMOTE_DVD:
				img.setImageResource(R.drawable.tv);
				break;
				/** 音响 */
			case REMOTE_SOUND:
				img.setImageResource(R.drawable.sound);
				break;
				/** 窗帘类型 */
			case REMOTE_CURTAIN:
				img.setImageResource(R.drawable.curtain);
				break;
				/** 风扇类型 */
			case REMOTE_ELECTFAN:
				img.setImageResource(R.drawable.elect_fan);
				break;
				/**自定义按键图标
				 */
			case CUSTOM_KEY:
				img.setImageResource(R.drawable.custom_remote_default_icon);
				break;

			default:
				break;
			}
			
			
//			System.out.println("device is path is not socket and switch "+path);
//			if (!TextUtils.isEmpty(path)){
//				if (deviceBean.isDeviceIconPersert()){
//					img.setImageResource(Integer.valueOf(path));
//				}else{
//					img.setImageBitmap(BitmapFactory.decodeFile(path));
//				}
//			}else{
//				img.setImageResource(R.drawable.csst_device_default);
//			}
		
		}
		
		
		convertView.setTag(deviceBean);
		return convertView;
	}
}
