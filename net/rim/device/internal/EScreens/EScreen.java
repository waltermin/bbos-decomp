package net.rim.device.internal.EScreens;

public final class EScreen {
   public static final native void setActive(boolean var0);

   public static final native void getScreenInfo(EScreenInfo var0, boolean var1);

   public static final native void getItemInfo(EScreenItem var0, EScreenItemInfo var1);

   public static final native int getItemData(EScreenItem var0, byte[] var1);

   public static final native void getMenuInfo(EScreenItem var0, EScreenMenuInfo var1);

   public static final native void doAction(int var0, int var1, byte[] var2);

   public static final native void keyPressed(EScreenItem var0, int var1);

   public static final native byte[] getBugDispLog(int var0);

   public static final native byte[] getBugDispLog(int var0, boolean var1);

   public static final native boolean bugDispLogAvailable(int var0);
}
