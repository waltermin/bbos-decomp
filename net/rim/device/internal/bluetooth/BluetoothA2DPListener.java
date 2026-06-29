package net.rim.device.internal.bluetooth;

public interface BluetoothA2DPListener extends BluetoothListener {
   void a2dpIncomingConnection(int var1, int var2, byte[] var3, int var4, byte[] var5);

   void a2dpConnected(int var1, int var2, byte[] var3);

   void a2dpDisconnected(int var1, int var2);

   void a2dpStartRequested(int var1, int var2);

   void a2dpStarted(int var1, int var2);

   void a2dpIdle(int var1, int var2);

   void a2dpSuspended(int var1, int var2);

   void a2dpAborted(int var1, int var2);

   void a2dpCodecInfo(int var1, int var2, int var3, byte[] var4);

   void a2dpConfigRequired(int var1, int var2);

   void a2dpReconfigRequired(int var1, int var2, int var3, byte[] var4);
}
