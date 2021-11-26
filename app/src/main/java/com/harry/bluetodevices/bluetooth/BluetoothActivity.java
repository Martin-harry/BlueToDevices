package com.harry.bluetodevices.bluetooth;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.harry.bluetodevices.R;
import com.harry.bluetodevices.adapter.DeviceAdapter;
import com.harry.bluetodevices.base.BaseActivity;
import com.harry.bluetodevices.comm.ObserverManager;
import com.harry.bluetodevices.util.CustomDialog;
import com.harry.bluetodevices.util.LogUtils;

import java.util.List;

/**
 * @author Martin-harry
 * @date 2021/7/13
 * @address
 * @Desc MainActivity
 */
public class BluetoothActivity extends BaseActivity implements View.OnClickListener {
    private Button search;
    private ListView list_device;
    private ImageView img_loading;
    private DeviceAdapter mDeviceAdapter;
    private Animation animation;
    private Parcelable device;
    private static final int LOCATION_PERMISSION_CODE = 1;
    private CustomDialog progressDialog;

    @Override
    protected void initView() {
        setStatusBg(4);
        search = findViewById(R.id.btn_scan);
        search.setText(R.string.start);
        img_loading = findViewById(R.id.img_loading);
        list_device = findViewById(R.id.list_device);

        search.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        progressDialog = new CustomDialog(this, "连接中");
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animation.setInterpolator(new LinearInterpolator());

        mDeviceAdapter = new DeviceAdapter(BluetoothActivity.this);
        mDeviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {//连接
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().cancelScan();
                    connect(bleDevice);
                }
            }

            @Override
            public void onDisConnect(final BleDevice bleDevice) {//断开连接
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().disconnect(bleDevice);
                }
            }

            @Override
            public void onDetail(BleDevice bleDevice) {//进入操作
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    String bleDeviceName = bleDevice.getName();
                    Log.e("连接成功 设备仪器 >>>>", "onDetail: " + "当前设备名：" + bleDeviceName);
                    Intent intent = new Intent();
                    if(bleDeviceName.equals("BJYC-CGM-01")){
                        intent.setClass(BluetoothActivity.this, BloodSugarActivity.class);
                        intent.putExtra(DevicesActivity.KEY_DATA, bleDevice);
                        startActivity(intent);
                    }else{
                        intent.setClass(BluetoothActivity.this, BloodSugarActivity.class);
                        intent.putExtra(DevicesActivity.KEY_DATA, bleDevice);
                        startActivity(intent);
                    }
                }
            }
        });
        list_device.setAdapter(mDeviceAdapter);
    }

    @Override
    protected int createViews() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan://开始扫描
                if (search.getText().equals("开始扫描")) {
                    LogUtils.e("search---", "开始扫描");
                    checkBluetoothAndLocationPermission();//设置权限
                } else if (search.getText().equals("停止扫描")) {
                    BleManager.getInstance().cancelScan();
                    LogUtils.e("search---", "停止扫描");
                }
                break;
        }
    }

    /**
     * 扫描外围设备
     */
    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {//回到主线程，参数表示本次扫描动作是否开启成功
                mDeviceAdapter.clearScanDevice();
                mDeviceAdapter.notifyDataSetChanged();
                img_loading.startAnimation(animation);
                img_loading.setVisibility(View.VISIBLE);
                search.setText(R.string.stop);
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {//扫描过程中所有被扫描到的结果回调
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {//扫描过程中的所有过滤后的结果回调
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {//本次扫描时段内所有被扫描且过滤后的设备集合
                img_loading.clearAnimation();
                img_loading.setVisibility(View.INVISIBLE);
                search.setText(R.string.start);
            }
        });
    }

    /**
     * 连接、断连、监控连接状态
     *
     * @param bleDevice
     */
    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                progressDialog.show();
                LogUtils.e("search---", "开始连接");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                img_loading.clearAnimation();
                img_loading.setVisibility(View.INVISIBLE);
                search.setText(R.string.start);
                progressDialog.dismiss();
                Toast.makeText(BluetoothActivity.this, R.string.conntFaile, Toast.LENGTH_LONG).show();
                LogUtils.e("search---", "连接失败");
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {//连接成功并发现服务
                LogUtils.e("search---状态值", status + "");
                if (status == 0) {//状态值
                    if (BleManager.getInstance().isConnected(bleDevice)) {
                        String bleDeviceName = bleDevice.getName();
                        Log.e("连接成功 设备仪器 >>>>", "onConnectSuccess: "+bleDeviceName );
                        Intent intent = new Intent();
                        if(bleDeviceName.equals("BJYC-CGM-01")){
                            intent.setClass(BluetoothActivity.this, BloodSugarActivity.class);
                            intent.putExtra(DevicesActivity.KEY_DATA, bleDevice);
                            startActivity(intent);
                        }else{
                            intent.setClass(BluetoothActivity.this, BloodSugarActivity.class);
                            intent.putExtra(DevicesActivity.KEY_DATA, bleDevice);
                            startActivity(intent);
                        }
                    }
                }

                progressDialog.dismiss();
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();

//                saveDeviceAddress(bleDevice);

                Toast.makeText(BluetoothActivity.this, R.string.conntSuccess, Toast.LENGTH_LONG).show();
                LogUtils.e("search---", "连接成功");

                int reConnectCount = BleManager.getInstance().getReConnectCount();
                int splitWriteNum = BleManager.getInstance().getSplitWriteNum();
                int maxConnectCount = BleManager.getInstance().getMaxConnectCount();
                LogUtils.e("mtu-----传输单元", "MaxConnect：---" + maxConnectCount + ";ReConnect：---" + reConnectCount + ";SplitWrite：---" + splitWriteNum);

                //设置最大传输单元MTU设置最大传输单元MTU(蓝牙传输数据限制20字节)
                BleManager.getInstance().setMtu(bleDevice, 512, new BleMtuChangedCallback() {
                    @Override
                    public void onSetMTUFailure(BleException exception) {
                        LogUtils.e("mtu-----传输单元", exception.toString());
                    }

                    @Override
                    public void onMtuChanged(int mtu) {
                        LogUtils.e("mtu-----最大传输单元", mtu + "");
                    }
                });
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();
                mDeviceAdapter.removeDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();

                if (isActiveDisConnected) {
                    Toast.makeText(BluetoothActivity.this, R.string.conntIstop, Toast.LENGTH_LONG).show();
                    LogUtils.e("search---", "连接断开！");
                } else {
                    Toast.makeText(BluetoothActivity.this, R.string.conntIsFail, Toast.LENGTH_LONG).show();
                    LogUtils.e("search---", "设备异常，请重新操作！");
//                    getDeviceAndConnect();
                    ObserverManager.getInstance().notifyObserver(bleDevice);
                }
            }
        });
    }

    private void saveDeviceAddress(BleDevice bleDevice) {
        SharedPreferences.Editor editor = getSharedPreferences("device", MODE_PRIVATE).edit();
        String address = bleDevice.getDevice().getAddress();
        LogUtils.e("自动连接---preferences", address + "(保存的地址)");
        editor.putString("address", address);
        editor.commit();
    }

    private void getDeviceAndConnect() {
        Intent intent = this.getIntent();
        device = intent.getParcelableExtra("device");
        LogUtils.e("自动连接---intent", device + "(得到的数据)");

        if (device == null) {
            LogUtils.e("自动连接---设备为空", device + "(自动连接)");
            autoConnect();
        } else {
            LogUtils.e("自动连接---设备有数据", device + "(开始连接)");
            connect((BleDevice) device);
        }
    }

    private void autoConnect() {
        SharedPreferences preferences = getSharedPreferences("device", MODE_PRIVATE);
        String address = preferences.getString("address", "");
        LogUtils.e("自动连接---preferences", address);
        if (address != null) {
//            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
//            Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
            List<BleDevice> connectedDevice = BleManager.getInstance().getAllConnectedDevice();
            int size = connectedDevice.size();
            LogUtils.e("自动连接---本机已配对的设备", connectedDevice.toString() + "---" + size);
//            if (connectedDevice.size() > 0) {
            for (BleDevice device1 : connectedDevice) {
                String device1Address = device1.getDevice().getAddress();
                LogUtils.e("自动连接---device1", device1Address);
                if (device1Address.equals(address)) {
                    device = device1;
                    break;
                }
            }
//            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkBluetoothAndLocationPermission() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!defaultAdapter.isEnabled()) {
            Toast.makeText(this, R.string.openBlue, Toast.LENGTH_LONG).show();
            return;
        }

        //判断是否有访问位置的权限，没有权限，直接申请位置权限
        if ((checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);

        } else {
            startScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean grantedLocation = true;
        if (requestCode == LOCATION_PERMISSION_CODE) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    grantedLocation = false;
                }
            }
        }

        if (!grantedLocation) {
            Toast.makeText(this, R.string.appLocal, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("activity", "onDestroy");

        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}
