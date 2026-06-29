package net.rim.device.internal.system;

public final class TTY {
   public static final int MODE_FULL = 0;
   public static final int MODE_TALK = 1;
   public static final int MODE_HEAR = 2;
   public static final int MODE_OFF = 3;

   private TTY() {
   }

   public static final native boolean setMode(int var0, int var1, int var2);

   public static final native int getMode();

   public static final native boolean requestModeChange(int var0);

   public static final native int getSource();

   public static final native int getBaud();

   public static final native byte read();

   public static final native byte[] read(int var0);

   public static final native int readAvail();

   public static final native void write(byte var0);

   public static final native int write(byte[] var0);

   public static final native int writeAvail();
}
