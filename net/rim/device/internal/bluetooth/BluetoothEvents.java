package net.rim.device.internal.bluetooth;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.NumberUtilities;

public final class BluetoothEvents {
   private static final boolean LOG_TO_BUGDISP = true;
   private static final long GUID = -7880699269633767460L;
   private static final String EVENT_LOGGER_TITLE = "net.rim.bluetooth";
   public static final int UNKNOWN_DEVICE = 1431194436;
   public static final int PIN_REQUEST_IN_PROGRESS = 1347570000;
   public static final int SERVICE_DISCOVERY_FAILED = 1397900358;
   public static final int BAD_DATA_ELEMENT = 1111770444;
   public static final int SERVICE_NAME_AID_MISSING = 1397637453;
   public static final int INVALID_PROFILE_DESC_LIST = 1229997644;
   public static final int INVALID_PROTOCOL_DESC_LIST = 1230001228;
   public static final int INVALID_SERVICE_CLASS_ID_LIST = 1230193484;
   public static final int BAD_SERVICE_RECORD_DATA = 1112756804;
   public static final int SERVICE_DISCOVERY_START = 542327891;
   public static final int SERVICE_DISCOVERY_PARTIAL_RESPONSE = 1396985938;
   public static final int SERVICE_DISCOVERY_COMPLETE = 542327875;
   public static final int SERVICE_DISCOVERY_START_FAILED = 1396986694;
   public static final int DEVICE_CONNECT = 1145241600;
   public static final int DEVICE_CONNECT_IO_EXCEPTION = 1145260367;
   public static final int DEVICE_DISCONNECTED = 1145307136;
   public static final int FETCH_NAME_FAILED = 541478470;
   public static final int POWER_ON = 538988366;
   public static final int POWER_OFF = 542066246;
   public static final int POWER_ON_FAILED = 542068294;
   public static final int POWER_OFF_FAILED = 1330005574;
   public static final int START_SNIFF_SUCCEEDED = 1398035283;
   public static final int START_SNIFF_FAILED = 1398035270;
   public static final int STOP_SNIFF_SUCCEEDED = 1397773139;
   public static final int STOP_SNIFF_FAILED = 1397948416;
   public static final int SNIFF_MODE_MAX_RETRIES = 1397571928;
   public static final int SNIFF_MODE_DISABLED = 1397638724;
   public static final int BLUETOOTH_NOT_SUPPORTED = 1313817172;
   public static final int RESTORE_FAILED = 1381192774;
   public static final int BACKUP_FAILED = 1111575366;
   public static final int DELETING_UNPAIRED_DEVICE = 1146441796;
   public static final int SNIFF_DISABLED = 1396984147;
   public static final int SET_DISCOVERABLE_FAILED = 1396984134;
   public static final int SECURITY_MODE_2 = 542330162;
   public static final int SECURITY_MODE_3 = 542330163;
   public static final int SECURITY_MODE_3_ENCRYPT = 1397568325;
   public static final int SECURITY_MODE_CHANGE_FAILED = 1397572422;
   public static final int LINK_MODE_CHANGE_FAILED = 1280131910;
   public static final int ENCRYPT_CONNECTION = 1162018816;
   public static final int SWITCH_ROLES = 1397882880;

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
