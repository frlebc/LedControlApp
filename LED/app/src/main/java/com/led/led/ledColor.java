package com.led.led;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.rarepebble.colorpicker.ColorPickerView;

import java.io.IOException;

public class ledColor extends ActionBarActivity {
    Button btnApply;
    ToggleButton btnRainbow, btnRandom, btnFullRandom;
    ColorPickerView picker;
    BluetoothProtocol proto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_color);

        proto = (BluetoothProtocol)getIntent().getSerializableExtra("BluetoothProtocol");

        btnApply = (Button)findViewById(R.id.buttonApply);
        btnRainbow = (ToggleButton)findViewById(R.id.buttonRainbow);
        btnRandom = (ToggleButton)findViewById(R.id.buttonRandom);
        btnFullRandom = (ToggleButton)findViewById(R.id.buttonFullRandom);

        //https://github.com/martin-stone/hsv-alpha-color-picker-android
        picker = (ColorPickerView)findViewById(R.id.colorPicker);
        picker.setColor(0xff12345);
        picker.showAlpha(false);
        picker.showHex(false);
        picker.showPreview(true);

        //commands to be sent to bluetooth
        btnApply.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                applyColor();
            }
        });

        btnRainbow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnRandom.setChecked(false);
                btnFullRandom.setChecked(false);
            }
        });

        btnRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnRainbow.setChecked(false);
                btnFullRandom.setChecked(false);
            }
        });

        btnFullRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnRainbow.setChecked(false);
                btnRandom.setChecked(false);
            }
        });
    }

    private void applyColor()
    {
        if (btnRainbow.isChecked())
        {
            proto.SetColorRainbow();
        }
        else if (btnRandom.isChecked())
        {
            proto.SetColorRandom();
        }
        else if (btnFullRandom.isChecked())
        {
            proto.SetColorFullRandom();
        }
        else
        {
            Color c = Color.valueOf(picker.getColor());
            int red = Math.round(255 * c.red());
            int green = Math.round(255 * c.green());
            int blue = Math.round(255 * c.blue());
            proto.SetColorRGB(red, green, blue);
        }
    }
}
