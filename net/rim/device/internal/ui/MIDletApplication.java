package net.rim.device.internal.ui;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;

public class MIDletApplication extends UiApplication {
   public void updateScreen() {
      throw null;
   }

   public void addPushRegistry(String _1, String _2) {
      throw null;
   }

   public void removePushRegistry(String _1, String _2) {
      throw null;
   }

   public void shutdownWorkerThread(String _1) {
      throw null;
   }

   public void exit() {
      throw null;
   }

   public void registerAlarm(Runnable _1) {
      throw null;
   }

   public void bringToForeground() {
      throw null;
   }

   public void setForegroundable(boolean _1) {
      throw null;
   }

   public static final String getAppProperty(String moduleName, String key, boolean cacheResourceObject) {
      if (key == null) {
         throw new NullPointerException();
      }

      Resource resource = Resource$Internal.getResourceClass(moduleName, cacheResourceObject);
      if (resource != null) {
         byte[] data = resource.getProperty(key);
         if (data != null) {
            return new String(data, 2, data.length - 2);
         }
      }

      return null;
   }
}
