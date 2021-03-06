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
import com.harry.bluetodevices.base.app.BaseActivity;
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
        progressDialog = new CustomDialog(this, "?????????");
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animation.setInterpolator(new LinearInterpolator());

        mDeviceAdapter = new DeviceAdapter(BluetoothActivity.this);
        mDeviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {//??????
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().cancelScan();
                    connect(bleDevice);
                }
            }

            @Override
            public void onDisConnect(final BleDevice bleDevice) {//????????????
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().disconnect(bleDevice);
                }
            }

            @Override
            public void onDetail(BleDevice bleDevice) {//????????????
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    String bleDeviceName = bleDevice.getName();
                    Log.e("???????????? ???????????? >>>>", "onDetail: " + "??????????????????" + bleDeviceName);
                    Intent intent = new Intent();
                    if(bleDeviceName.equals("BJYC_CGM_01")){
                        intent.setClass(BluetoothActivity.this, BloodSugarActivity.class);
                        intent.putExtra(BloodSugarActivity.KEY_DATA, bleDevice);
                        startActivity(intent);
                    }else{
                        intent.setClass(BluetoothActivity.this, DevicesActivity.class);
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
            case R.id.btn_scan://????????????
                if (search.getText().equals("????????????")) {
                    LogUtils.e("search---", "????????????");
                    checkBluetoothAndLocationPermission();//????????????
                } else if (search.getText().equals("????????????")) {
                    BleManager.getInstance().cancelScan();
                    LogUtils.e("search---", "????????????");
                }
                break;
        }
    }

    /**
     * ??????????????????
     */
    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {//??????????????????????????????????????????????????????????????????
                mDeviceAdapter.clearScanDevice();
                mDeviceAdapter.notifyDataSetChanged();
                img_loading.startAnimation(animation);
                img_loading.setVisibility(View.VISIBLE);
                search.setText(R.string.stop);
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {//????????????????????????????????????????????????
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {//????????????????????????????????????????????????
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {//???????????????????????????????????????????????????????????????
                img_loading.clearAnimation();
                img_loading.setVisibility(View.INVISIBLE);
                search.setText(R.string.start);
            }
        });
    }

    /**
     * ????????????????????????????????????
     *
     * @param bleDevice
     */
    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                progressDialog.show();
                LogUtils.e("search---", "????????????");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                img_loading.clearAnimation();
                img_loading.setVisibility(View.INVISIBLE);
                search.setText(R.string.start);
                progressDialog.dismiss();
                Toast.makeText(BluetoothActivity.this, R.string.conntFaile, Toast.LENGTH_LONG).show();
                LogUtils.e("search---", "????????????");
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {//???????????????????????????
                LogUtils.e("search---?????????", status + "");
                if (status == 0) {//?????????
                    if (BleManager.getInstance().isConnected(bleDevice)) {
                        String bleDeviceName = bleDevice.getName();
                        Log.e("???????????? ???????????? >>>>", "onConnectSuccess: "+bleDeviceName );
                        Intent intent = new Intent();
                        if(bleDeviceName.equals("BJYC_CGM_01")){
                            intent.setClass(BluetoothActivity.this, BloodSugarActivity.class);
                            intent.putExtra(BloodSugarActivity.KEY_DATA, bleDevice);
                            startActivity(intent);
                        }else{
                            intent.setClass(BluetoothActivity.this, DevicesActivity.class);
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
                LogUtils.e("search---", "????????????");

                int reConnectCount = BleManager.getInstance().getReConnectCount();
                int splitWriteNum = BleManager.getInstance().getSplitWriteNum();
                int maxConnectCount = BleManager.getInstance().getMaxConnectCount();
                LogUtils.e("mtu-----????????????", "MaxConnect???---" + maxConnectCount + "  ReConnect???---" + reConnectCount + "  SplitWrite???---" + splitWriteNum);

                //????????????????????????MTU????????????????????????MTU(????????????????????????20??????)
                BleManager.getInstance().setMtu(bleDevice, 512, new BleMtuChangedCallback() {
                    @Override
                    public void onSetMTUFailure(BleException exception) {
                        LogUtils.e("mtu-----????????????", exception.toString());
                    }

                    @Override
                    public void onMtuChanged(int mtu) {
                        LogUtils.e("mtu-----??????????????????", mtu + "");
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
                    LogUtils.e("search---", "???????????????");
                } else {
                    Toast.makeText(BluetoothActivity.this, R.string.conntIsFail, Toast.LENGTH_LONG).show();
                    LogUtils.e("search---", "?????????????????????????????????");
//                    getDeviceAndConnect();
                    ObserverManager.getInstance().notifyObserver(bleDevice);
                }
            }
        });
    }

    private void saveDeviceAddress(BleDevice bleDevice) {
        SharedPreferences.Editor editor = getSharedPreferences("device", MODE_PRIVATE).edit();
        String address = bleDevice.getDevice().getAddress();
        LogUtils.e("????????????---preferences", address + "(???????????????)");
        editor.putString("address", address);
        editor.commit();
    }

    private void getDeviceAndConnect() {
        Intent intent = this.getIntent();
        device = intent.getParcelableExtra("device");
        LogUtils.e("????????????---intent", device + "(???????????????)");

        if (device == null) {
            LogUtils.e("????????????---????????????", device + "(????????????)");
            autoConnect();
        } else {
            LogUtils.e("????????????---???????????????", device + "(????????????)");
            connect((BleDevice) device);
        }
    }

    private void autoConnect() {
        SharedPreferences preferences = getSharedPreferences("device", MODE_PRIVATE);
        String address = preferences.getString("address", "");
        LogUtils.e("????????????---preferences", address);
        if (address != null) {
//            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
//            Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
            List<BleDevice> connectedDevice = BleManager.getInstance().getAllConnectedDevice();
            int size = connectedDevice.size();
            LogUtils.e("????????????---????????????????????????", connectedDevice.toString() + "---" + size);
//            if (connectedDevice.size() > 0) {
            for (BleDevice device1 : connectedDevice) {
                String device1Address = device1.getDevice().getAddress();
                LogUtils.e("????????????---device1", device1Address);
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

        //??????????????????????????????????????????????????????????????????????????????
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
