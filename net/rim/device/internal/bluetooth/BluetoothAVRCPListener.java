package net.rim.device.internal.bluetooth;

public interface BluetoothAVRCPListener extends BluetoothListener {
   void avrcpIncomingConnection(int var1, byte[] var2);

   void avrcpConnected(int var1, byte[] var2);

   void avrcpDisconnected(int var1);

   void avrcpPanelPress(int var1, int var2);

   void avrcpPanelHold(int var1, int var2);

   void avrcpPanelRelease(int var1, int var2);

   void avrcpPanelResponse(int var1, int var2, boolean var3, int var4);
}
