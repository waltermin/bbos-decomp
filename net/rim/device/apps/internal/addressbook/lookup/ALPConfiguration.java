package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;

public class ALPConfiguration {
   private boolean _active;
   private String _serviceUID;
   private byte[] _encodingData;
   private ALPManager _manager = new ALPManager();

   ALPConfiguration() {
   }

   private static ALPConfiguration getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ALPConfiguration instance = (ALPConfiguration)ar.get(3525746332568432596L);
      if (instance == null) {
         instance = new ALPConfiguration();
         ar.put(3525746332568432596L, instance);
      }

      return instance;
   }

   static void activate(ServiceRecord sr) {
      if (sr != null) {
         getInstance()._active = true;
         getInstance()._encodingData = CMIMEUtilities.getServerEncoding(sr.getApplicationData());
      } else {
         getInstance()._active = false;
         getInstance()._encodingData = null;
      }
   }

   public static boolean isActive() {
      ALPConfiguration configInstance = getInstance();
      ServiceRouting sr = ServiceRouting.getInstance();
      return configInstance != null && configInstance._active && sr.isServiceRoutable(configInstance._serviceUID, -1);
   }

   public static byte[] getEncodingData() {
      return isActive() ? getInstance()._encodingData : null;
   }

   public static ALPManager getManager() {
      return getInstance()._manager;
   }

   public static void setServiceUID(String serviceUID) {
      getInstance()._serviceUID = serviceUID;
   }
}
