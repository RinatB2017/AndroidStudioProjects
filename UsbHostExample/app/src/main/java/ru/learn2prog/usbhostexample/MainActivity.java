package ru.learn2prog.usbhostexample;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView lgView;
    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbDeviceConnection mConnection;
    private UsbEndpoint mEndpointIntr;

//    private UsbSerialPort mUsbSerialPort;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		lgView = (TextView)findViewById(R.id.logTextView);

        mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
	}
	
	@Override
    public void onResume() {
        super.onResume();
        
         HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList(); 
         Iterator<UsbDevice> deviceIterator = deviceList.values().iterator(); 

         lgView.setText( "Devices Count:" + deviceList.size() );

         while (deviceIterator.hasNext()) { 
             UsbDevice device = (UsbDevice) deviceIterator.next(); 
             lgView.setText( lgView.getText() + "\n" + "Device ProductID: " + device.getProductId() );
         } 
        
        Intent intent = getIntent();
        lgView.setText( lgView.getText() + "\n" + "intent: " + intent);
        String action = intent.getAction();

        UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            setDevice(device);
            lgView.setText( lgView.getText() + "\n" + "UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action) is TRUE");
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            if (mDevice != null && mDevice.equals(device)) {
                setDevice(null);
                lgView.setText( lgView.getText() + "\n" + "UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action) is TRUE");
            }
        }
        
    }

    private void setDevice(UsbDevice device) {
        lgView.setText( lgView.getText() + "\n" + "setDevice " + device);
        if (device.getInterfaceCount() != 1) {
            
            lgView.setText( lgView.getText() + "\n" + "could not find interface");
            return;
        }
        UsbInterface intf = device.getInterface(0);
       
        if (intf.getEndpointCount() == 0) {
            
            lgView.setText( lgView.getText() + "\n" +  "could not find endpoint");
            return;
        } else {  //13.12.2012
                lgView.setText( lgView.getText() + "\n" + "Endpoints Count: " + intf.getEndpointCount() );
        }
        
    UsbEndpoint epIN = null;
	UsbEndpoint epOUT = null;
	
	for (int i = 0; i < intf.getEndpointCount(); i++) {
		if (intf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_INT) {
			if (intf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
				epIN = intf.getEndpoint(i);
				lgView.setText( lgView.getText() + "\n" + "IN endpoint: " + intf.getEndpoint(i) );
			}
			else {
				epOUT = intf.getEndpoint(i);
				lgView.setText( lgView.getText() + "\n" + "OUT endpoint: " + intf.getEndpoint(i) );
			}	
		} else { lgView.setText( lgView.getText() + "\n" + "no endpoints for INTERRUPT_TRANSFER"); }
	}
        
        mDevice = device;
        mEndpointIntr = epOUT;
       
        if (device != null) {
            UsbDeviceConnection connection = mUsbManager.openDevice(device);
            if (connection != null && connection.claimInterface(intf, true)) {
                
                lgView.setText( lgView.getText() + "\n" + "open device SUCCESS!");
                mConnection = connection;
                

            } else {
                
                lgView.setText( lgView.getText() + "\n" + "open device FAIL!");
                mConnection = null;
            }
         }
    }

    /*
    private void serial_open()
    {
        UsbManager mUsbManager = (UsbManager) DEVICE.getSystemService(Context.USB_SERVICE);
        String type = “FTDI”;

        for (UsbDevice usbDevice: mUsbManager.getDeviceList().values()) {
            UsbSerialDriver usbSerialDriver = UsbSerialProber.probeSingleDevice(usbDevice);
            if (usbSerialDriver == null) continue;
            if (!type.equals(usbSerialDriver.getShortDeviceName())) continue;
            mUsbSerialPort = usbSerialDriver.getPort(0);
            mUsbSerialPort.open(mUsbManager);
            break;
        }
    }

    private void set_serial_param()
    {
        mUsbSerialPort.setParameters(baudRate, dataBits, stopBits, parity);
    }

    public int serial_read(final byte[] data) throws IOException {
        return mUsbSerialPort.read(data, getReadTimeout());
    }

    public int serial_write(final byte[] data, final int length) throws IOException {
        return mUsbSerialPort.write(data, length, getWriteTimeout());
    }

    private void serial_close()
    {
        mUsbSerialPort.close();
    }
    */
}
