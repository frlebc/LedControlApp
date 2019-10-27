package com.led.led;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;


public class ledControl extends ActionBarActivity {

    Button btnOn, btnOff, btnDis, btnColor, btnPattern, btnPoweroff, btnReboot;
    SeekBar speed;
    TextView lumn, temp;
    private ProgressDialog progress;
    BluetoothProtocol proto;

    TimerTask timer= new TimerTask(){

        @Override
        public void run() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //Show.setText(hours + ":" + minutes + ":" + seconds);
                    String msg = BluetoothManager.getInstance().Read();
                    if (!msg.isEmpty()) {
                        temp.setText("Temperature: " + proto.decodeTemperature(msg) + " °C");
                        Log.d("Received: ", msg);
                    }
                }
            });
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        proto = new BluetoothProtocol();

        Intent newint = getIntent();
        BluetoothManager.getInstance().SetDeviceAddress(newint.getStringExtra(DeviceList.EXTRA_ADDRESS)); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_led_control);

        //call the widgtes
        btnOn = (Button)findViewById(R.id.button2);
        btnOff = (Button)findViewById(R.id.button3);
        btnDis = (Button)findViewById(R.id.button4);
        speed = (SeekBar)findViewById(R.id.seekBar);
        lumn = (TextView)findViewById(R.id.lumn);
        temp = (TextView)findViewById(R.id.textViewTemperature);
        btnColor = (Button)findViewById(R.id.button6);
        btnPattern = (Button)findViewById(R.id.buttonPattern);
        btnPoweroff = (Button)findViewById(R.id.buttonPoweroff);
        btnReboot = (Button)findViewById(R.id.buttonReboot);
        progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog

        Timer t = new Timer();
        t.scheduleAtFixedRate(timer , 0 , 100);

        try {
            BluetoothManager.getInstance().Connect();
            msg("Connected.");
        }
        catch (RuntimeException e)
        {
            msg(e.getMessage());
            finish();
        }
        finally {
            progress.dismiss();
        }

        //commands to be sent to bluetooth
        btnOn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                turnOnLed();      //method to turn on
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                turnOffLed();   //method to turn off
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    BluetoothManager.getInstance().Disconnect();
                    finish();
                }
                catch (RuntimeException e)
                {
                    msg(e.getMessage());
                }
            }
        });

        btnColor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View  v)
            {
                // Make an intent to start next activity.
                Intent i = new Intent(ledControl.this, ledColor.class);
                i.putExtra("BluetoothProtocol", proto);
                startActivity(i);
            }
        });

        btnPattern.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View  v)
            {
                // Make an intent to start next activity.
                Intent i = new Intent(ledControl.this, patternControl.class);
                i.putExtra("BluetoothProtocol", proto);
                startActivity(i);
            }
        });

        btnPoweroff.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View  v)
            {
                new AlertDialog.Builder(ledControl.this)
                        .setMessage("Do you really want to POWEROFF?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                proto.PowerOff();
                                BluetoothManager.getInstance().Disconnect();
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        btnReboot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View  v)
            {
                new AlertDialog.Builder(ledControl.this)
                        .setMessage("Do you really want to REBOOT?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                proto.Reboot();
                                BluetoothManager.getInstance().Disconnect();
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private float convertProgressValue(int progress) {
                if (progress == 255) {
                    return 10.0f;
                }
                if (progress > 220) {
                    return progress * 7.0f / 255;
                }
                return progress * 4.0f / 255;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser==true)
                {
                    lumn.setText(String.format("%.02f", convertProgressValue(progress)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try
                {
                    float value = convertProgressValue(speed.getProgress());
                    proto.SetSpeed(value);
                }
                catch (RuntimeException e)
                {
                    msg("Failed to set speed");
                }
            }
        });
    }

    private void turnOffLed()
    {
        try
        {
            proto.TurnOffLeds();
        }
        catch (RuntimeException e)
        {
            msg(e.getMessage());
        }
    }

    private void turnOnLed()
    {
        try
        {
            proto.TurnOnLeds();
        }
        catch (RuntimeException e)
        {
            msg(e.getMessage());
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
