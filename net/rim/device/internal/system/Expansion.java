package net.rim.device.internal.system;

public final class Expansion {
   public static final int TYPE_SDCARD = 1;
   public static final int USB_MS_CARD_INSERTED = 1;
   public static final int USB_MS_CARD_LOCKED = 2;
   public static final int EXPANSION_OK = 0;
   public static final int EXPANSION_NOCARD = 1;

   public static final native long getExpansionType(int var0);

   public static final native void setUSBMassStorageProperties(int var0);

   public static final native int getUSBMassStorageProperties();
}
