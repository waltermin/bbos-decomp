package javax.microedition.io;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.io.PushRegistryHelper;
import net.rim.device.internal.system.MIDletSecurity;
import net.rim.device.internal.ui.MIDletApplication;
import net.rim.vm.Process;

public class PushRegistry {
   private static final String MIDLET_NAME = "MIDlet-Name";
   private static final long MAX_DELAY = 108000000L;
   private static String PUSHREGISTRY_PERMISSION_TOKEN = "pushregistry";
   private static String SMS_PERMISSION_TOKEN = "sms";

   private PushRegistry() {
   }

   public static String getFilter(String connection) {
      if (connection != null && connection.length() != 0) {
         String suiteName = PushRegistryHelper.getMidletProperty("MIDlet-Name");
         PushRegistryHelper prh = PushRegistryHelper.getInstance();
         String[] array = (String[])prh._filterMap.get(connection);
         return array != null && array[0].equals(suiteName) ? array[1] : null;
      } else {
         return null;
      }
   }

   public static String getMIDlet(String connection) {
      if (connection != null && connection.length() != 0) {
         PushRegistryHelper prh = PushRegistryHelper.getInstance();
         String[] array = (String[])prh._connectionMap.get(connection);
         if (array != null) {
            String suiteName = PushRegistryHelper.getMidletProperty("MIDlet-Name");
            if (suiteName != null && suiteName.equals(array[0])) {
               return array[1];
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static String[] listConnections(boolean available) {
      PushRegistryHelper prh = PushRegistryHelper.getInstance();
      String suiteName = PushRegistryHelper.getMidletProperty("MIDlet-Name");
      Vector connections = new Vector(5);
      removeStaleEntries();
      Enumeration e = prh._connectionMap.keys();

      while (e.hasMoreElements()) {
         String connection = (String)e.nextElement();
         String[] array = (String[])prh._connectionMap.get(connection);
         if (array != null && array[0].equals(suiteName)) {
            connections.addElement(connection);
         }
      }

      if (available) {
         for (int i = connections.size() - 1; i >= 0; i--) {
            String connection = (String)connections.elementAt(i);
            if (null == prh.get(connection)) {
               connections.removeElement(connection);
            }
         }
      }

      String[] out = new String[0];
      if (!connections.isEmpty()) {
         out = new String[connections.size()];
         connections.copyInto(out);
      }

      return out;
   }

   public static long registerAlarm(String midlet, long time) throws ClassNotFoundException {
      if (midlet != null && midlet.length() != 0) {
         PushRegistryHelper prh = PushRegistryHelper.getInstance();
         Class.forName(midlet);
         if (!isMidletInSuite(midlet)) {
            throw new ClassNotFoundException("Midlet '" + midlet + "' is not in current suite");
         }

         checkAlarmPermission();
         PushRegistry$MIDletAlarmExpiry mae = (PushRegistry$MIDletAlarmExpiry)prh._alarmMap.get(midlet);
         long oldTimeRegistered = 0;
         if (mae != null) {
            oldTimeRegistered = mae.getExpiry();
         }

         mae = new PushRegistry$MIDletAlarmExpiry(midlet, new Date(time));
         prh._alarmMap.put(midlet, mae);
         long now = new Date().getTime();
         long delay = Math.max(1, Math.min(time - now, 108000000));
         MIDletApplication ma = (MIDletApplication)Application.getApplication();
         ma.registerAlarm(mae);
         ma.invokeLater(mae, delay, false);
         return oldTimeRegistered;
      } else {
         throw new ClassNotFoundException("Midlet '" + midlet + "' is not in current suite");
      }
   }

   private static void checkPermission(int moduleHandle, String permission, String url) {
      MIDletApplication a = (MIDletApplication)Application.getApplication();
      if (!a.hasEventThread()) {
         PushRegistry$PermissionCheckRunnable pcr = new PushRegistry$PermissionCheckRunnable(moduleHandle, permission, url);
         checkPermissionLater(a, pcr);
         if (pcr._failed) {
            throw pcr._re;
         }
      } else {
         checkPermissionPrimitive(moduleHandle, permission, url);
      }
   }

   private static void checkAlarmPermission() {
      MIDletApplication a = (MIDletApplication)Application.getApplication();
      if (!a.hasEventThread()) {
         PushRegistry$AlarmPermissionCheckRunnable apcr = new PushRegistry$AlarmPermissionCheckRunnable(null);
         checkPermissionLater(a, apcr);
         if (apcr._failed) {
            throw apcr._re;
         }
      } else {
         MIDletSecurity.checkPermission(8);
      }
   }

   private static void checkPermissionLater(MIDletApplication app, Runnable permChecker) {
      app.invokeLater(permChecker);
      app.setForegroundable(false);

      try {
         app.enterEventDispatcher();
      } catch (PushRegistry$PushRegistryPermissionCheckExitEvent var3) {
      }
   }

   private static void checkPermissionPrimitive(int moduleHandle, String permission, String url) {
      if (!ControlledAccess.verifyRRISignatures(true) && !Firewall.getInstance().allowConnection(permission, url, false)) {
         throw new SecurityException("Permission denied");
      }
   }

   private static boolean isMidletInSuite(String midlet) {
      StringBuffer sb = new StringBuffer("MIDlet-n");
      int charindex = sb.length() - 1;
      int i = 1;

      while (true) {
         sb.setCharAt(charindex, (char)(i + 48));
         String s = PushRegistryHelper.getMidletProperty(sb.toString());
         if (s == null) {
            return false;
         }

         if (s.indexOf(midlet) != -1) {
            return true;
         }

         i++;
      }
   }

   private static void launchMidlet(String midletClassName, String[] args, boolean grabForeground) {
      MIDletApplication ma = (MIDletApplication)Application.getApplication();
      ma.bringToForeground();
   }

   private static int getModuleHandleForMidletClass(String midletname) {
      return isMidletInSuite(midletname) ? Process.currentProcess().getModuleHandle() : -1;
   }

   public static void registerConnection(String connection, String midlet, String filter) throws IOException, ClassNotFoundException, ConnectionNotFoundException {
      PushRegistryHelper prh = PushRegistryHelper.getInstance();
      if (midlet != null && midlet.length() != 0) {
         Class.forName(midlet);
         if (connection == null || filter == null || connection.length() == 0 || filter.length() == 0 || -1 != filter.indexOf(58)) {
            throw new IllegalArgumentException();
         }

         if (!PushRegistryHelper.isConnectionSupported(connection)) {
            throw new ConnectionNotFoundException(connection + " does not support push");
         }

         if (!isMidletInSuite(midlet)) {
            throw new ClassNotFoundException("Midlet '" + midlet + "' is not in current suite");
         }

         removeStaleConnection(connection);
         removeStaleEntries();
         if (prh._connectionMap.get(connection) != null) {
            throw new IOException(connection + " is already registered");
         }

         checkPermission(getModuleHandleForMidletClass(midlet), PUSHREGISTRY_PERMISSION_TOKEN, connection);
         String suiteName = PushRegistryHelper.getMidletProperty("MIDlet-Name");
         String[] array = new String[]{suiteName, midlet};
         prh._connectionMap.put(connection, array);
         array = new String[]{suiteName, filter};
         prh._filterMap.put(connection, array);
         String callingprocess = PushRegistryHelper.getCallingMidletName();
         String midletprocess = CodeModuleManager.getModuleName(getModuleHandleForMidletClass(midlet));
         if (midletprocess.equals(callingprocess)) {
            MIDletApplication ma = (MIDletApplication)Application.getApplication();
            if (connection.startsWith("sms://")) {
               checkPermission(getModuleHandleForMidletClass(midlet), SMS_PERMISSION_TOKEN, connection);
            }

            ma.addPushRegistry(midlet, connection);
         } else {
            launchMidlet(midlet, new String[]{PushRegistryHelper.APPLICATION_DESCRIPTOR_ARG_DYNAMIC_PUSH_REGISTRY_WORK, midlet, connection}, false);
         }
      } else {
         throw new ClassNotFoundException("Midlet '" + midlet + "' is not a valid class");
      }
   }

   public static boolean unregisterConnection(String connection) {
      if (connection != null && connection.length() != 0) {
         PushRegistryHelper prh = PushRegistryHelper.getInstance();
         String callingprocess = PushRegistryHelper.getCallingMidletName();
         String suite = PushRegistryHelper.getMidletProperty("MIDlet-Name");
         String[] array = (String[])prh._connectionMap.get(connection);
         if (array == null) {
            return false;
         } else if (!array[0].equals(suite)) {
            throw new SecurityException();
         } else if (!removeStaleConnection(connection)) {
            return false;
         } else {
            String midlet = array[1];
            prh._connectionMap.remove(connection);
            prh._filterMap.remove(connection);
            String midletprocess = CodeModuleManager.getModuleName(getModuleHandleForMidletClass(midlet));
            if (midletprocess.equals(callingprocess)) {
               MIDletApplication ma = (MIDletApplication)Application.getApplication();
               ma.removePushRegistry(midlet, connection);
               return true;
            } else {
               launchMidlet(midlet, new String[]{PushRegistryHelper.APPLICATION_DESCRIPTOR_ARG_SHUTDOWN_PUSH_REGISTRY_WORK, midlet, connection}, false);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private static boolean removeStaleConnection(String connection) {
      PushRegistryHelper prh = PushRegistryHelper.getInstance();
      String[] array = (String[])prh._connectionMap.get(connection);
      if (array == null) {
         return true;
      } else {
         String midlet = array[1];
         if (getModuleHandleForMidletClass(midlet) == -1) {
            prh._connectionMap.remove(connection);
            prh._filterMap.remove(connection);
            return false;
         } else {
            return true;
         }
      }
   }

   private static void removeStaleEntries() {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }
}
