package net.rim.device.cldc.io.ssl;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.cldc.io.ippp.SocketTransportBase;
import net.rim.device.cldc.io.utility.URLParameters;

public final class SSLOptionsRegistration {
   public static final long ID = 1021895513728250690L;

   private SSLOptionsRegistration() {
   }

   public static final synchronized void register(boolean deviceTLSExists) {
      ApplicationRegistry.getApplicationRegistry().replace(1021895513728250690L, new Object(deviceTLSExists));
   }

   public static final synchronized boolean doesDeviceSideExist() {
      Boolean retval = (Boolean)ApplicationRegistry.getApplicationRegistry().get(1021895513728250690L);
      return retval;
   }

   public static final boolean isDeviceTLSTheOnlySecureConnection(URLParameters urlParameters) throws TLSSecurityException {
      boolean defaultToDeviceTLS = false;
      String uid = SocketTransportBase.findAcceptableConnectionUid(urlParameters);
      ServiceRecord rec;
      if (uid != null && (rec = ServiceBook.getSB().getRecordByUidAndCid(uid, "IPPP")) != null) {
         if (rec.getEncryptionMode() == 2) {
            return defaultToDeviceTLS;
         } else if (doesDeviceSideExist()) {
            return true;
         } else {
            throw new TLSSecurityException("Could not find secure IPPP service");
         }
      } else if (doesDeviceSideExist()) {
         return true;
      } else {
         throw new TLSSecurityException("Could not find a service book entry for IPPP");
      }
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      synchronized (registry) {
         Boolean retval = (Boolean)registry.get(1021895513728250690L);
         if (retval == null) {
            registry.put(1021895513728250690L, Boolean.FALSE);
         }
      }
   }
}
