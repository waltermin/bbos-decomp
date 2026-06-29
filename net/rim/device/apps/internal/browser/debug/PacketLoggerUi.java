package net.rim.device.apps.internal.browser.debug;

import net.rim.device.cldc.io.utility.PacketLogger;

public final class PacketLoggerUi {
   public static final void showSavedMessage(int address) {
      new DebugViewerScreen(getAddressText(address), "Packet Log", null, true, true);
   }

   public static final void showPacketLog() {
      try {
         new DebugViewerScreen(PacketLogger.getInstance().getText(2, 3), "Packet Log", null, true, true);
      } finally {
         new DebugViewerScreen(
            ((StringBuffer)(new Object()))
               .append(getAddressText(PacketLogger.getInstance().savePackets()))
               .append(PacketLogger.getInstance().getText(2, 1))
               .toString(),
            "Packet Log",
            null,
            true,
            false
         );
         return;
      }
   }

   private static final String getAddressText(int address) {
      return ((StringBuffer)(new Object("Packet log saved to filesystem at address ")))
         .append(Integer.toHexString(address))
         .append(". Use cfp savefs to get a file system dump.\n")
         .toString();
   }
}
