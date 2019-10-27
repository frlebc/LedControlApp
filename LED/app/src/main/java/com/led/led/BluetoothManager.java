package com.led.led;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothManager {

    private static BluetoothManager instance = null;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    String address = null;
    private boolean isBtConnected = false;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //standard bluetooth SPP UUID, google it...

    private BluetoothManager()
    {

    }

    public static BluetoothManager getInstance()
    {
        if (instance == null)
            instance = new BluetoothManager();
        return instance;
    }

    public void SetDeviceAddress(String addr)
    {
        address = addr;
    }

    public void Connect()
    {
        new ConnectBT().execute(); //Call the class to connect
    }

    public void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
                btSocket = null;
            }
            catch (IOException e)
            {
                throw new RuntimeException("Error in disconnection");
            }
        }
    }

    public void Write(String msg)
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(msg.getBytes());
            }
            catch (IOException e)
            {
                throw new RuntimeException("Error in write");
            }
        }
    }

    public String Read()
    {
        byte[] buffer = new byte[1024];
        int bytes = 0;
        try {
            InputStream is = btSocket.getInputStream();
            bytes = is.read(buffer, 0, is.available());
        }
        catch (IOException e)
        {
            //throw new RuntimeException("Error in read");
        }
        return new String(buffer, 0, bytes);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                fallbackBtSocket();
            }
            return null;
        }

        private void fallbackBtSocket()
        {
            try
            {
                // ref: https://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3
                Class<?> clazz = myBluetooth.getRemoteDevice(address).getClass();
                Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};

                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};

                btSocket = (BluetoothSocket) m.invoke(myBluetooth.getRemoteDevice(address), params);
                btSocket.connect();
            }
            catch(NoSuchMethodException ex)
            {
                Log.d("BT_CONNECT", ex.getMessage());
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            catch(IOException ex)
            {
                Log.d("BT_CONNECT", ex.getMessage());
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            catch(IllegalAccessException ex)
            {
                Log.d("BT_CONNECT", ex.getMessage());
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            catch(InvocationTargetException ex)
            {
                Log.d("BT_CONNECT", ex.getMessage());
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                throw new RuntimeException("Connection Failed. Is it the right device? Try again.");
            }
            else
            {
                isBtConnected = true;
            }
        }
    }
}
