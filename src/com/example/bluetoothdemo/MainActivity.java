package com.example.bluetoothdemo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;



import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
public class MainActivity extends Activity {
    private BluetoothAdapter adapter=null;
    private static final int REQ_CODE=100;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    //1、判断是否有蓝牙模块
		adapter=BluetoothAdapter.getDefaultAdapter();
		if(adapter==null){
			Toast.makeText(this,"no bluetooth", Toast.LENGTH_SHORT).show();
		}else{
			//2、启用蓝牙
			if(!adapter.isEnabled()){
				Intent intent=new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(intent,REQ_CODE);
			}else{
				//3、查询已经匹配的设备
				Set<BluetoothDevice> devices=adapter.getBondedDevices();
			    if(devices.size()>0){
			    	for(BluetoothDevice device:devices){
			    		Log.e("---------",device.getName()+":"
			    	     +device.getAddress());
			    	}
			    }else{
			    	Toast.makeText(this,"no paired", Toast.LENGTH_SHORT).show();
			    }
			}
		}
		
	}
   @Override
protected void onStart() {
	   IntentFilter filter=new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
	// TODO Auto-generated method stub
	super.onStart();
	
}
   @Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	unregisterReceiver(mReceiver);
}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId()==R.id.menu_settings){
    		 //startDiscovery非常耗电，故一旦发现需要匹配设备，停止搜索设备
    		   adapter.startDiscovery();
  
			 

    		 Object[] lstDevice = adapter.getBondedDevices().toArray();    
    		 for(int i=0;i<lstDevice.length;i++)
    		 Log.e("Set", lstDevice[i]+""+lstDevice.length);
    		
    	}else if(item.getItemId()==R.id.menu_stop){
    		adapter.cancelDiscovery();
    	}else if(item.getItemId()==R.id.menu_find){
    		//设置本机可发现时间，时间是以秒为单位，值从0-3600之间的值
    		Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    	    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//    	    Intent intent1 = new Intent();
//			intent1.setAction(BluetoothDevice.ACTION_FOUND);
//			 
//		sendBroadcast(intent1);
    	    startActivity(intent);
    	  
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    private  BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("-------------new device","没发现新设备");
            //搜索设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	 Log.e("-------------new device","没发现新设备1");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                 //   mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                   Log.e("--------new deivice", device.getName()+":"+device.getAddress());
                   Boolean returnValue = false;    
                   BluetoothDevice btDev = adapter.getRemoteDevice(device.getAddress());   
              //   BluetoothDevice.createBond(BluetoothDevice remoteDevice);    
                 Method createBondMethod = null;
				try {
					createBondMethod = BluetoothDevice.class.getMethod("createBond");
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
                 Log.d("BlueToothTestActivity", "开始配对");    
                try {
					returnValue = (Boolean) createBondMethod.invoke(btDev);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            
                setTitle("没有新设备");
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    Log.e("-------------new device","没发现新设备");
                   // mNewDevicesArrayAdapter.add("没有新设备");
                }
            }
        }
    };
}
