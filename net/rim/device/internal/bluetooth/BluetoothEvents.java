package net.rim.device.internal.bluetooth;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.NumberUtilities;

public final class BluetoothEvents {
   private static final boolean LOG_TO_BUGDISP;
   private static final long GUID;
   private static final String EVENT_LOGGER_TITLE;
   public static final int UNKNOWN_DEVICE;
   public static final int PIN_REQUEST_IN_PROGRESS;
   public static final int SERVICE_DISCOVERY_FAILED;
   public static final int BAD_DATA_ELEMENT;
   public static final int SERVICE_NAME_AID_MISSING;
   public static final int INVALID_PROFILE_DESC_LIST;
   public static final int INVALID_PROTOCOL_DESC_LIST;
   public static final int INVALID_SERVICE_CLASS_ID_LIST;
   public static final int BAD_SERVICE_RECORD_DATA;
   public static final int SERVICE_DISCOVERY_START;
   public static final int SERVICE_DISCOVERY_PARTIAL_RESPONSE;
   public static final int SERVICE_DISCOVERY_COMPLETE;
   public static final int SERVICE_DISCOVERY_START_FAILED;
   public static final int DEVICE_CONNECT;
   public static final int DEVICE_CONNECT_IO_EXCEPTION;
   public static final int DEVICE_DISCONNECTED;
   public static final int FETCH_NAME_FAILED;
   public static final int POWER_ON;
   public static final int POWER_OFF;
   public static final int POWER_ON_FAILED;
   public static final int POWER_OFF_FAILED;
   public static final int START_SNIFF_SUCCEEDED;
   public static final int START_SNIFF_FAILED;
   public static final int STOP_SNIFF_SUCCEEDED;
   public static final int STOP_SNIFF_FAILED;
   public static final int SNIFF_MODE_MAX_RETRIES;
   public static final int SNIFF_MODE_DISABLED;
   public static final int BLUETOOTH_NOT_SUPPORTED;
   public static final int RESTORE_FAILED;
   public static final int BACKUP_FAILED;
   public static final int DELETING_UNPAIRED_DEVICE;
   public static final int SNIFF_DISABLED;
   public static final int SET_DISCOVERABLE_FAILED;
   public static final int SECURITY_MODE_2;
   public static final int SECURITY_MODE_3;
   public static final int SECURITY_MODE_3_ENCRYPT;
   public static final int SECURITY_MODE_CHANGE_FAILED;
   public static final int LINK_MODE_CHANGE_FAILED;
   public static final int ENCRYPT_CONNECTION;
   public static final int SWITCH_ROLES;

   public static final void init() {
      EventLogger.register(-7880699269633767460L, "net.rim.bluetooth", 2);
   }

   public static final void log(int id) {
      EventLogger.logEvent(-7880699269633767460L, id, 0);
      System.out.println("net.rim.bluetooth: " + (char)(id >> 24 & 0xFF) + (char)(id >> 16 & 0xFF) + (char)(id >> 8 & 0xFF) + (char)(id & 0xFF));
   }

   public static final void log(String s) {
      EventLogger.logEvent(-7880699269633767460L, s.getBytes(), 0);
      System.out.println("net.rim.bluetooth: " + s);
   }

   public static final void logResult(int event, int result) {
      char high = NumberUtilities.intToHexDigit(result >> 4);
      char low = NumberUtilities.intToHexDigit(result);
      log(event | high << '\b' | low);
   }
}
