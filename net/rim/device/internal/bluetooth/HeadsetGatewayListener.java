package net.rim.device.internal.bluetooth;

public interface HeadsetGatewayListener extends BluetoothListener {
   void headsetIncomingConnection(byte[] var1);

   void headsetConnected(int var1);

   void headsetDisconnected();

   void headsetAudioConnected(int var1);

   void headsetAudioDisconnected();

   void headsetButtonPressed();

   void headsetSpeakerVolumeChange(int var1);

   void headsetUnknownATData(String var1);
}
