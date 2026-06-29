package net.rim.device.internal.system;

public final class Expansion {
   public static final int TYPE_SDCARD;
   public static final int USB_MS_CARD_INSERTED;
   public static final int USB_MS_CARD_LOCKED;
   public static final int EXPANSION_OK;
   public static final int EXPANSION_NOCARD;

   public static final native long getExpansionType(int var0);

   public static final native void setUSBMassStorageProperties(int var0);

   public static final native int getUSBMassStorageProperties();
}
