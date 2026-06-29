package net.rim.device.internal.io;

import java.io.IOException;
import java.util.Hashtable;
import javax.microedition.io.Connection;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.cldc.io.utility.MalformedURLException;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.internal.midlet.MIDPSupport;
import net.rim.device.internal.system.MIDletSecurityConstants;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.vm.Process;

public class PushRegistryHelper {
   private Hashtable _pushRegsitryConnections = new Hashtable();
   public Hashtable _weakreferencemap = new Hashtable();
   public Hashtable _filterMap = new Hashtable();
   public Hashtable _connectionMap = new Hashtable();
   public Hashtable _alarmMap = new Hashtable();
   private static final long ID = 8840121830482700777L;
   public static String APPLICATION_DESCRIPTOR_NAME_PUSH_REGISTRY_WORK = "ApplicationDescriptor-Pushregistrystartup";
   public static String APPLICATION_DESCRIPTOR_ARG_PUSH_REGISTRY_WORK = "dostaticuppushregistrywork";
   public static String APPLICATION_DESCRIPTOR_ARG_DYNAMIC_PUSH_REGISTRY_WORK = "dodynamicuppushregistrywork";
   public static String APPLICATION_DESCRIPTOR_ARG_SHUTDOWN_PUSH_REGISTRY_WORK = "doshutdownpushregistrywork";
   public static final String MIDLET_NAME = "MIDlet-Name";
   public static String MIDLET_PUSH_PROPERTY_NAME_PREFIX = "MIDlet-Push-";
   public static String MIDLET_PROPERTY_NAME_PREFIX = "MIDlet-";
   private static final String[] PUSH_TRANSPORTS = new String[]{"http", "socket", "sms", "datagram"};
   private static final String[] PUSH_TRANSPORT_PERMISSIONS = new String[]{
      MIDletSecurityConstants.MIDletPermissions[0],
      MIDletSecurityConstants.MIDletPermissions[5],
      MIDletSecurityConstants.MIDletPermissions[13],
      MIDletSecurityConstants.MIDletPermissions[3]
   };

   public static PushRegistryHelper getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         PushRegistryHelper prh = (PushRegistryHelper)ar.get(8840121830482700777L);
         if (prh == null) {
            prh = new PushRegistryHelper();
            ar.put(8840121830482700777L, prh);
         }

         return prh;
      }
   }

   public static String getCallingMidletName() {
      String name = null;
      if (CodeModuleManager.isMidlet()) {
         name = Process.currentProcess().getModuleName();
      }

      return name;
   }

   public static String getMidletProperty(String key) {
      Resource resource = Resource$Internal.getResourceClass(Process.currentProcess().getModuleName());
      if (resource != null) {
         byte[] data = resource.getProperty(key);
         if (data != null) {
            return new String(data, 2, data.length - 2);
         }
      }

      return null;
   }

   private PushRegistryHelper() {
   }

   public void put(String connectionString, Connection connection) {
      this._pushRegsitryConnections.put(connectionString, connection);
   }

   public Connection get(String connectionString) {
      return (Connection)this._pushRegsitryConnections.get(connectionString);
   }

   public Connection remove(String connectionString) {
      return (Connection)this._pushRegsitryConnections.remove(connectionString);
   }

   public static String[] getPushPropertyValues(String rawProperty) {
      String s = rawProperty;
      int comma = s.indexOf(44, 0);
      String connectionUrl = s.substring(0, comma);
      connectionUrl = connectionUrl.trim();
      int index = comma + 1;
      comma = s.indexOf(44, index);
      String midletClassName = s.substring(index, comma);
      midletClassName = midletClassName.trim();
      index = comma + 1;
      String filter = s.substring(index);
      filter = filter.trim();
      return new String[]{connectionUrl, midletClassName, filter};
   }

   public static boolean isConnectionSupported(String connection) {
      try {
         boolean connectionSchemeAcceptableForPush = false;

         for (int i = 0; i < PUSH_TRANSPORTS.length; i++) {
            if (connection.startsWith(PUSH_TRANSPORTS[i])) {
               connectionSchemeAcceptableForPush = true;
               break;
            }
         }

         return !MIDPSupport.connectionNotSupported(connection) && connectionSchemeAcceptableForPush;
      } catch (MalformedURLException e) {
         return false;
      }
   }

   public static boolean isPushTransportPermissionRequested(String pushURL, String permissions) {
      if (permissions != null) {
         URL url;
         try {
            url = new URL(pushURL);
         } catch (MalformedURLException e) {
            return false;
         }

         String scheme = url.getScheme();
         int index = Arrays.getIndex(PUSH_TRANSPORTS, scheme);
         if (index > 0) {
            String permission = PUSH_TRANSPORT_PERMISSIONS[index];
            StringTokenizer stringTokenizer = new StringTokenizer(permissions, ',');

            while (stringTokenizer.hasMoreTokens()) {
               String token = stringTokenizer.nextToken().trim();
               if (token.equals(permission)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static Connection checkConnection(String connection) throws IOException {
      PushRegistryHelper prh = getInstance();
      String[] array = (String[])prh._connectionMap.get(connection);
      if (array != null && array.length > 0) {
         String suite = array[0];
         String suiteName = getMidletProperty("MIDlet-Name");
         if (!suiteName.equals(suite)) {
            throw new IOException("Connection " + connection + " registered by another midlet!");
         }
      }

      return prh.get(connection);
   }
}
