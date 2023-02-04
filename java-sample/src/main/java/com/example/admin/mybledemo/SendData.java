package com.example.admin.mybledemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.DialogInterface;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleLog;
import cn.com.heaton.blelibrary.ble.callback.BleWriteEntityCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import cn.com.heaton.blelibrary.ble.model.EntityData;
import cn.com.heaton.blelibrary.ble.utils.ByteUtils;

public class SendData {
    private Ble<BleDevice> ble = Ble.getInstance();
    private static final String TAG = "SendData";


    private Activity activity;
    private BleDevice bleDevice;

    private int length;
    private int time;
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGattDescriptor descriptor;
    private String filePath;


    public SendData(Activity activity, BleDevice bleDevice, int length, int time,
                    BluetoothGattCharacteristic characteristic, BluetoothGattDescriptor descriptor,
                    String filePath) {
        this.activity = activity;
        this.bleDevice = bleDevice;
        this.length = length;
        this.time = time;
        this.characteristic = characteristic;
        this.descriptor = descriptor;
        this.filePath = filePath;
    }


    /**
     * 分包发送数据
     *
     * @param autoWriteMode 是否分包自动发送数据(entityData中的delay无效)
     */
    public void sendEntityData(boolean autoWriteMode) {
        EntityData entityData = getEntityData(autoWriteMode);
        if (entityData == null) return;
        showProgress();
        ble.writeEntity(entityData, writeEntityCallback);
    }

    private BleWriteEntityCallback<BleDevice> writeEntityCallback = new BleWriteEntityCallback<BleDevice>() {
        @Override
        public void onWriteSuccess() {
            BleLog.d("writeEntity", "onWriteSuccess");
            hideProgress();
            toToast("发送成功");
        }

        @Override
        public void onWriteFailed() {
            BleLog.d("writeEntity", "onWriteFailed");
            hideProgress();
            toToast("发送失败");
        }

        @Override
        public void onWriteProgress(double progress) {
            Log.d("writeEntity", "当前发送进度: " + progress);
            setDialogProgress((int) (progress * 100));
        }

        @Override
        public void onWriteCancel() {
            Log.d(TAG, "onWriteCancel: ");
            hideProgress();
            toToast("发送取消");
        }
    };

    ProgressDialog dialog;

    private void showProgress() {
        if (dialog == null) {
            dialog = new ProgressDialog(activity);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
            dialog.setTitle("发送大数据文件");
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setMessage("Data is sending, please wait...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.setIndeterminate(false);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ble.cancelWriteEntity();
                }
            });
        }
        dialog.show();
    }

    private void setDialogProgress(int progress) {
        Log.e(TAG, "setDialogProgress: " + progress);
        if (dialog != null) {
            dialog.setProgress(progress);
        }
    }

    private void hideProgress() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
            Log.e(TAG, "hideProgress: ");
        }
    }




    private void toToast(String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.showToast(msg);
            }
        });
    }



    private EntityData getEntityData(boolean autoWriteMode) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(filePath));
//            inputStream = activity.getAssets().open("test.bin");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputStream == null) {
            Utils.showToast("不能发现文件!");
            return null;
        }
        byte[] data = ByteUtils.stream2Bytes(inputStream);
        Log.e(TAG, "data length: " + data.length);
        return new EntityData.Builder()
                .setAutoWriteMode(autoWriteMode)
                .setAddress(bleDevice.getBleAddress())
                .setData(data)
                .setPackLength(20)
                .setCharacteristic(characteristic)
                .setDescriptor(descriptor)
                .setDelay(50L)
                .build();
    }


}
