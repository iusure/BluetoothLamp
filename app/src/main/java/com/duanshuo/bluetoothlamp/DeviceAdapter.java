package com.duanshuo.bluetoothlamp;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ds on 2017/12/17.
 */

public class DeviceAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> mDevices;
    private Context mContext;

    public DeviceAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        mDevices = devices;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.item, null);
            holder = new ViewHolder();
            holder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mTvAddress = (TextView) convertView.findViewById(R.id.tv_address);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        BluetoothDevice device = mDevices.get(position);
        holder.mTvName.setText(device.getName());
        holder.mTvAddress.setText(device.getAddress());
        return convertView;
    }

    class ViewHolder {
        TextView mTvName;
        TextView mTvAddress;
    }

}