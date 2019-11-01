package com.led.led;

import java.io.Serializable;

public class BluetoothProtocol implements Serializable {

    enum Patterns {
        STROBE,
        CHASE,
        LOADING,
        RANDOM,
    }

    public void TurnOffLeds()
    {
        BluetoothManager.getInstance().Write("led.off");
    }

    public void TurnOnLeds()
    {
        BluetoothManager.getInstance().Write("led.on");
    }

    public void SetSpeed(float speedFactor)
    {
        String cmd = new StringBuilder("speed.").append(speedFactor).toString();
        BluetoothManager.getInstance().Write(cmd);
    }

    public void SetIntensity(float intensityFactor)
    {
        String cmd = new StringBuilder("int.").append(intensityFactor).toString();
        BluetoothManager.getInstance().Write(cmd);
    }

    public void SetColorRGB(int red, int green, int blue)
    {
        String cmd = new StringBuilder("rgb.")
                .append(String.valueOf(Math.min(red, 255)))
                .append(" ")
                .append(String.valueOf(Math.min(green, 255)))
                .append(" ")
                .append(String.valueOf(Math.min(blue, 255)))
                .toString();
        BluetoothManager.getInstance().Write(cmd);
    }

    public void SetColorRandom()
    {
        BluetoothManager.getInstance().Write("col.random");
    }

    public void SetColorRainbow()
    {
        BluetoothManager.getInstance().Write("col.rainbow");
    }

    public void SetColorFullRandom()
    {
        BluetoothManager.getInstance().Write("col.fullrandom");
    }

    public void SetPatternStrobe(String mode)
    {
        String cmd = "pat.strobe." + mode.toLowerCase();
        BluetoothManager.getInstance().Write(cmd);
    }

    public void SetPatternChase(String nbLeds, boolean reverse, boolean opposite)
    {
        String cmd = "pat.chase." + nbLeds;
        if (opposite)
            cmd += ".opposite";
        if (reverse)
            cmd += ".reverse";
        BluetoothManager.getInstance().Write(cmd);
    }

    public void SetPatternLoading(boolean reverse, boolean opposite, boolean half)
    {
        String cmd = "pat.load";
        cmd += (half ? ".half" : ".full");
        if (opposite)
            cmd += ".opposite";
        if (reverse)
            cmd += ".reverse";
        BluetoothManager.getInstance().Write(cmd);
    }

    public void SetPatternRandom()
    {
        BluetoothManager.getInstance().Write("pat.fullrandom");
    }

    public void PowerOff()
    {
        BluetoothManager.getInstance().Write("rpi.poweroff");
    }

    public void Reboot()
    {
        BluetoothManager.getInstance().Write("rpi.reboot");
    }

    public String decodeTemperature(String msg)
    {
        if (msg.startsWith("temp."))
        {
            return msg.substring(msg.indexOf(".") + 1);
        }
        return "Error";
    }
}
