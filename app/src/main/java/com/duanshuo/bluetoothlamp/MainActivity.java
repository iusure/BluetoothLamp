package com.duanshuo.bluetoothlamp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    protected static final String tag = "MainActivity";
    private ListView mLv;
    private BluetoothAdapter mBluetoothAdapter;
    private OutputStream mOutputStream;
    private ArrayList<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();
    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 扫描到蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDevices.add(device);
                mAdapter.notifyDataSetChanged();
                Log.i(tag,"设备名称: " + device.getName());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(getApplicationContext(), "开始扫描", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(getApplicationContext(), "扫描结束", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private DeviceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLv = (ListView) findViewById(R.id.lv);
        mAdapter = new DeviceAdapter(getApplicationContext(), mDevices);
        mLv.setAdapter(mAdapter);
        mLv.setOnItemClickListener(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 注册广播接收者, 当扫描到蓝牙设备的时候, 系统会发送广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBluetoothReceiver, filter);

        if (Build.VERSION.SDK_INT>=23)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);   }
        }
    }


    public void clickBtn(View v) {

        switch (v.getId()) {
            case R.id.bt_open_bluetooth:
                // 蓝牙是否可用
                if (!mBluetoothAdapter.isEnabled()) {
                    // 打开蓝牙
                    mBluetoothAdapter.enable();
                }
                break;
            case R.id.bt_close_bluetooth:
                // 关闭蓝牙
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                }
                break;
            case R.id.bt_scan_start:
                // 开始扫描

                mDevices.clear();
                mAdapter.notifyDataSetChanged();
                mBluetoothAdapter.startDiscovery();
                break;
            case R.id.bt_stop_scan:
                // 停止扫描
                if (!mBluetoothAdapter.isEnabled()){
                    Toast.makeText(getApplicationContext(), "请先打开蓝牙", Toast.LENGTH_SHORT);
                }else {
                mBluetoothAdapter.cancelDiscovery();
                break;
                }
            case R.id.bt_open_lamp:
                String s ="lopened8910111213145";
                sendCtrl(s);
                setText("灯已经打开");
                setImage("open");

                break;
            case R.id.bt_lamp_close:
                String s1 = "loclosed891011121314";
                sendCtrl(s1);
                Log.i(tag,"你输入的信息是："+s1);
                setText("灯已经关闭");
                setImage("close");
                break;
            case R.id.bt_start_clock:
                sendCtrl("ccopen12345678901234");
                setText1("闹钟已开启");
                break;
            case R.id.bt_stop_clock:
                sendCtrl("ccstop12345678901234");
                setText1("闹钟已停止");
                break;
            case R.id.bt_close_clock:
                sendCtrl("cclose12345678901234");
                setText1("闹钟已关闭");
                break;
            case R.id.bt_send:
                EditText et_command = (EditText) findViewById(R.id.et_command);
                String edit = et_command.getText().toString();
                Log.i(tag,"你输入的信息是："+et_command);
                sendCtrl(edit);
                Toast.makeText(this, "已发送", Toast.LENGTH_SHORT);

            default:
                break;
        }
    }

    /**
     * 设置闹钟开启关闭显示
     * @param str
     */
    private void setText1(String str) {
        TextView tv_clock = (TextView) findViewById(R.id.tv_clock);
        tv_clock.setText(str);
    }


    /**
     * 根据按钮显示开灯状态
     * @param state 灯的开关状态
     */
    private void setImage(String state) {
        ImageView iv_lamp = (ImageView) findViewById(R.id.iv_lamp);
        if (state == "open"){
            iv_lamp.setImageResource(R.drawable.lamp_on);
        }else if (state == "close"){
            iv_lamp.setImageResource(R.drawable.lamp_off);
        }

    }

    /**
     * 显示开关灯
     * @param str
     */
    private void setText(String str) {
        TextView tv_lamp = (TextView) findViewById(R.id.tv_lamp);
        tv_lamp.setText(str);
    }






    //发送字符串
    private void sendCtrl(String message){
        byte[] toSend = message.getBytes();
        try {
            mOutputStream.write(toSend);
            // Your Data is sent to  BT connected paired device ENJOY.
        } catch (IOException e) {
            Log.e(tag, "Exception during write", e);
        }
    }




/*
    private void sendCtrl(int i) {
        try {
            byte[] bs = new byte[5];
            bs[0] = (byte)0x01;
            bs[1] = (byte)0x99;
            if(i== 0) {
                bs[2] = (byte)0x10;
                bs[3] = (byte)0x10;
            }else if(i==1) {
                bs[2] = (byte)0x11;
                bs[3] = (byte)0x11;
            }else if(i==2) {
                bs[2] = (byte)0x17;
                bs[3] = (byte)0x17;
            }
            bs[4] = (byte)0x99;
            mOutputStream.write(bs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothReceiver);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = mDevices.get(position);
        conn(device);

    }

    private void connsetText(String str, String name) {
        TextView tv_conn = (TextView) findViewById(R.id.tv_conn);
        tv_conn.setText(str+name);
    }

    private void conn(final BluetoothDevice device) {
        // 建立蓝牙连接是耗时操作, 类似TCP Socket, 需要放在子线程里
        new Thread() {
            public void run() {
                try {
                    // 获取 BluetoothSocket, UUID需要和蓝牙服务端保持一致

                    BluetoothSocket mBluetoothSocket = device.createRfcommSocketToServiceRecord(UUID
                            .fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    // 和蓝牙服务端建立连接
                    mBluetoothSocket.connect();
                    // 获取输出流, 往蓝牙服务端写指令信息
                    mOutputStream = mBluetoothSocket.getOutputStream();
                    // 提示用户
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Log.i(tag,"连接成功----");
                            Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT)
                                    .show();
                            connsetText("设备已连接",device.getName());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

}