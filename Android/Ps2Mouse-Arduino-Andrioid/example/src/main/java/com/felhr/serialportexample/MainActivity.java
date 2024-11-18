package com.felhr.serialportexample;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.ref.WeakReference;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    /*
     * Notifications from UsbService will be received here.
     */
    private TextView display;
    private ProgressBar spinner;
    private UsbService usbService;
    //private TextView display;
    private EditText editText;
    private MyHandler mHandler;
    private SeekBar hslider;
    private SeekBar vslider;
    private int hsliderV;
    private int vsliderV;
    private ToggleButton lbutton;
    private ToggleButton rbutton;
    private ToggleButton mbutton;


    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mHandler = new MyHandler(this);

        display = (TextView) findViewById(R.id.textView1);
        spinner = (ProgressBar) findViewById(R.id.progressBar5);
        editText = (EditText) findViewById(R.id.editText1);
        hslider = (SeekBar) findViewById(R.id.seekBar);
        lbutton = (ToggleButton) findViewById(R.id.toggleButtonl);
        mbutton = (ToggleButton) findViewById(R.id.toggleButtonm);
        rbutton = (ToggleButton) findViewById(R.id.toggleButtonr);
        hsliderV = 50;
        vslider = (SeekBar) findViewById(R.id.seekBar3);
        vsliderV = 50;
        //display.append("USB device ready");
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.requestFocus();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().isEmpty()) {
                    String data = editText.getText().toString() +"\n";
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        @Override

        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.VISIBLE);
                    //display.append(UsbService.ACTION_NO_USB.toString());
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.VISIBLE);
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_READY: // Should connect momentarily
                    Toast.makeText(context, "USB device ready", Toast.LENGTH_LONG).show();
                    //display.append(UsbService.ACTION_USB_READY);
                    spinner.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };
    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        //filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_READY);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;
        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    mActivity.get().display.append(data);
                    data = data.trim();
                    //mActivity.get().display.append(data);
                    if (data.equals("right") && mActivity.get().hsliderV <= 99){
                        mActivity.get().hslider.setProgress(mActivity.get().hsliderV++);
                    }
                    if (data.equals("left") && mActivity.get().hsliderV >= 1){
                        mActivity.get().hslider.setProgress(mActivity.get().hsliderV--);
                    }
                    if (data.equals("up") && mActivity.get().vsliderV <= 99){
                        mActivity.get().vslider.setProgress(mActivity.get().vsliderV++);
                    }
                    if (data.equals("down") && mActivity.get().vsliderV >= 1){
                        mActivity.get().vslider.setProgress(mActivity.get().vsliderV--);
                    }
                    if (data.equals("btnLeft")){
                        mActivity.get().lbutton.setChecked(true);
                    }
                    if (data.equals("!btnLeft")){
                        mActivity.get().lbutton.setChecked(false);
                    }
                    if (data.equals("btnRight")){mActivity.get().rbutton.setChecked(true);}
                    if (data.equals("!btnRight")){mActivity.get().rbutton.setChecked(false);}
                    if (data.equals("btnMiddle")){mActivity.get().mbutton.setChecked(true);}
                    if (data.equals("!btnMiddle")){mActivity.get().mbutton.setChecked(false);}
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}