package com.csst.smarthome.camera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csst.smarthome.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchListAdapter extends BaseAdapter {
	
	@SuppressWarnings("unused")
	private static final String LOG_TAG = "SearchListAdapter" ;	
	
	private LayoutInflater listContainer = null;
	@SuppressWarnings("unused")
	private Context context = null;
	private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();;
	
	public final class SearchListItem{    
	    public TextView devName;      
	    public TextView devID;             
	}    
	
	public SearchListAdapter(Context ct){
		context = ct;
		listContainer = LayoutInflater.from(ct);   
	}

	@Override
	public int getCount() {
		return listItems.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		SearchListItem  searchListItem = null; 		
		if(convertView == null){
			searchListItem = new SearchListItem();  
			convertView = listContainer.inflate(R.layout.csst_camera_search_list_item, null);    
			searchListItem.devName = (TextView)convertView.findViewById(R.id.searchDevName) ;
			searchListItem.devID = (TextView)convertView.findViewById(R.id.searchDevID) ;
			convertView.setTag(searchListItem);
		}else{
			searchListItem = (SearchListItem)convertView.getTag();
		}		
		//convertView.setBackgroundResource(R.drawable.listitem_pressed_selector);
		searchListItem.devName.setText((String)listItems.get(position).get(ContentCommon.STR_CAMERA_NAME));
		searchListItem.devID.setText((String)listItems.get(position).get(ContentCommon.STR_CAMERA_ID));
		return convertView;
	}
	
	/**
	 * 向搜索列表中添加摄像机
	 * @param mac
	 * @param ipaddr
	 * @param port
	 */
	public boolean AddCamera(String mac, String name, String did){
		
		//如果摄像机MAC重复，则不添加到列表中
		if(!CheckCameraInfo(mac)){
			return false;
		}
		Map<String, Object> map = new HashMap<String, Object>(); 
		map.put(ContentCommon.STR_CAMERA_MAC, mac) ;
		map.put(ContentCommon.STR_CAMERA_NAME, name);
		map.put(ContentCommon.STR_CAMERA_ID, did);		
		listItems.add(map) ;		
		return true;
	}

	/**
	 * 检查是否重复
	 * @param mac
	 * @return
	 */
	private boolean CheckCameraInfo(String mac) {
		//遍历列表，检查是否有相同mac地址的摄像机
		int size = listItems.size();
		for(int i = 0; i < size; i++){
			String strMac = (String)listItems.get(i).get(ContentCommon.STR_CAMERA_MAC);
			if(mac.equals(strMac)){
				return false;
			}
		}
		return true;
	}

	public Map<String, Object> getItemContent(int pos) {
		if(pos > listItems.size()){
			return null;
		}
		return listItems.get(pos);
	}
	
	public void ClearAll(){
		listItems.clear();
	}
	
}