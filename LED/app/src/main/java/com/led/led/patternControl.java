package com.led.led;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import com.rarepebble.colorpicker.ColorPickerView;

public class patternControl extends ActionBarActivity {
    BluetoothProtocol proto;
    Spinner patternListSpinner;
    Switch reverseSwitch, oppositeSwitch, halfSwitch;
    Spinner chaseNumberSpinner, strobeModeSpinner;
    Button btnApply;

    BluetoothProtocol.Patterns currentSelectedPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_control);

        proto = (BluetoothProtocol)getIntent().getSerializableExtra("BluetoothProtocol");

        patternListSpinner = (Spinner)findViewById(R.id.spinner2);
        reverseSwitch = (Switch)findViewById(R.id.switchReverse);
        oppositeSwitch = (Switch)findViewById(R.id.switchOpposite);
        halfSwitch = (Switch)findViewById(R.id.switchHalf);
        chaseNumberSpinner = (Spinner)findViewById(R.id.spinnerChaseNumber);
        strobeModeSpinner = (Spinner)findViewById(R.id.spinnerStrobeMode);
        btnApply = (Button)findViewById(R.id.buttonApply);

        patternListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //Strobe
                        currentSelectedPattern = BluetoothProtocol.Patterns.STROBE;
                        DisplayUiStrobe();
                        break;
                    case 1: //Chase
                        currentSelectedPattern = BluetoothProtocol.Patterns.CHASE;
                        DisplayUiChase();
                        break;
                    case 2: //Loading
                        currentSelectedPattern = BluetoothProtocol.Patterns.LOADING;
                        DisplayUiLoading();
                        break;
                    case 3: //Random
                        currentSelectedPattern = BluetoothProtocol.Patterns.RANDOM;
                        DisplayUiRandom();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        btnApply.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                applyPattern();
            }
        });
    }

    private void resetUi() {
        reverseSwitch.setVisibility(View.INVISIBLE);
        oppositeSwitch.setVisibility(View.INVISIBLE);
        halfSwitch.setVisibility(View.INVISIBLE);
        chaseNumberSpinner.setVisibility(View.INVISIBLE);
        strobeModeSpinner.setVisibility(View.INVISIBLE);
    }

    private void DisplayUiStrobe() {
        resetUi();
        strobeModeSpinner.setVisibility(View.VISIBLE);
    }

    private void DisplayUiChase() {
        resetUi();
        reverseSwitch.setVisibility(View.VISIBLE);
        oppositeSwitch.setVisibility(View.VISIBLE);
        chaseNumberSpinner.setVisibility(View.VISIBLE);
    }

    private void DisplayUiLoading() {
        resetUi();
        reverseSwitch.setVisibility(View.VISIBLE);
        oppositeSwitch.setVisibility(View.VISIBLE);
        halfSwitch.setVisibility(View.VISIBLE);
    }

    private void DisplayUiRandom() {
        resetUi();
    }

    private void applyPattern() {
        switch (currentSelectedPattern) {
            case STROBE:
                proto.SetPatternStrobe(strobeModeSpinner.getSelectedItem().toString());
                break;
            case CHASE:
                proto.SetPatternChase(chaseNumberSpinner.getSelectedItem().toString(), reverseSwitch.isChecked(), oppositeSwitch.isChecked());
                break;
            case LOADING:
                proto.SetPatternLoading(reverseSwitch.isChecked(), oppositeSwitch.isChecked(), halfSwitch.isChecked());
                break;
            case RANDOM:
                proto.SetPatternRandom();
                break;
        }
    }

}
