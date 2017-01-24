package com.example.dinghao.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mLvConn;
    private ListView mLvNearby;
    private Button mBtScan;
    private LinearLayout mActivityMain;
    private BluetoothAdapter BA;
    private ProgressBar mPbScan;
    private ArrayAdapter<String> nearByAdapter;
    private ArrayList<String> nearByList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        // 注册BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // 不要忘了之后解除绑定
        BA = BluetoothAdapter.getDefaultAdapter();
        On();
        connListView();
        nearByListView();
    }

    private void nearByListView() {
        nearByAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, nearByList);
        mLvNearby.setAdapter(nearByAdapter);
    }

    private void connListView() {
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();
        for (BluetoothDevice bt : pairedDevices)
            list.add(bt.getName()+"  "+bt.getAddress());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        mLvConn.setAdapter(adapter);
    }

    private void initView() {
        mLvConn = (ListView) findViewById(R.id.lv_conn);
        mLvNearby = (ListView) findViewById(R.id.lv_nearby);
        mBtScan = (Button) findViewById(R.id.bt_scan);
        mActivityMain = (LinearLayout) findViewById(R.id.activity_main);

        mBtScan.setOnClickListener(this);
        mPbScan = (ProgressBar) findViewById(R.id.pb_scan);
    }

    public void On() {
        if (!BA.isEnabled()) {
            BA.enable();
            Toast.makeText(getApplicationContext(), "Turned on"
                    , Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_scan:
                BA.startDiscovery();
                mPbScan.setVisibility(View.VISIBLE);
                break;
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("ttt", "onReceive: ");
            // 发现设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                nearByList.add(device.getName()+"  "+device.getAddress());
                Log.i("ttt", "onReceive: "+device.getName());
                nearByAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mPbScan.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
