package net.rim.device.internal.system;

public class USBPortInternal$Internal {
   public static final int USB_MODE_HANDHELD;
   public static final int USB_MODE_MODEM;

   public static native void setMode(int var0);

   public static native void redirectedPasswordChallenge(int var0, boolean var1);

   public static native String getChannelName(int var0);
}
