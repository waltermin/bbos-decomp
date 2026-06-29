package net.rim.device.api.system;

import net.rim.device.internal.system.USBPortInternal$Internal;

public class USBPort$Internal {
   public static final int USB_MODE_HANDHELD;
   public static final int USB_MODE_MODEM;

   public static void setMode(int mode) {
      USBPortInternal$Internal.setMode(mode);
   }

   public static void redirectedPasswordChallenge(int channel, boolean openChannel) {
      USBPortInternal$Internal.redirectedPasswordChallenge(channel, openChannel);
   }

   public static String getChannelName(int channel) {
      return USBPortInternal$Internal.getChannelName(channel);
   }
}
