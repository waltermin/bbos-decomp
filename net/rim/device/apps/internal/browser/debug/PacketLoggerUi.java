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
            getAddressText(PacketLogger.getInstance().savePackets()) + PacketLogger.getInstance().getText(2, 1), "Packet Log", null, true, false
         );
         return;
      }
   }

   private static final String getAddressText(int address) {
      return "Packet log saved to filesystem at address " + Integer.toHexString(address) + ". Use cfp savefs to get a file system dump.\n";
   }
}
