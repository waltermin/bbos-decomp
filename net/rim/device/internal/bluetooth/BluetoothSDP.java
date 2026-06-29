package net.rim.device.internal.bluetooth;

public final class BluetoothSDP {
   private BluetoothSDP() {
   }

   public static final native int addRecord(int var0, boolean var1, int[] var2, byte[][][] var3, int var4, int var5, int var6, int var7);

   public static final native int removeRecord(int var0);

   public static final native int updateRecord(int var0, int[] var1, byte[][][] var2, int var3);

   public static final native int updateServiceAvailability(int var0, int var1);
}
