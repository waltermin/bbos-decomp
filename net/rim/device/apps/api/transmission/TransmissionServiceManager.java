package net.rim.device.apps.api.transmission;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.io.TrafficLogger;

public final class TransmissionServiceManager {
   public static final long RIM_MESSAGING_SERVICE;
   public static final long RIM_CALENDAR_SERVICE;
   public static final long RIM_GLOBAL_ADDRESS_SERVICE;
   public static final long RIM_OTA_ITADMIN;
   public static final long RIM_OTA_KEYGEN;
   public static final long RIM_OTA_APPLICATION_DELIVERY;
   public static final long RIM_OTASL_UPGRADE;

   public static final TransmissionService get(long factoryIdentifierLong) {
      return (TransmissionService)ApplicationRegistry.getApplicationRegistry().waitFor(factoryIdentifierLong);
   }

   public static final void register(TransmissionService aTransmissionService) {
      ApplicationRegistry.getApplicationRegistry().put(aTransmissionService.getFactoryIdentifier(), aTransmissionService);
   }

   public static final void unregister(TransmissionService aTransmissionService) {
      ApplicationRegistry.getApplicationRegistry().remove(aTransmissionService.getFactoryIdentifier());
   }

   public static final void setTrafficLogger(TrafficLogger tLogger) {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      setTL(appReg, 8399767144006445082L, tLogger);
      setTL(appReg, -8987817959472095554L, tLogger);
      setTL(appReg, -8892319056465090102L, tLogger);
      setTL(appReg, 7017126881385937825L, tLogger);
      setTL(appReg, -7467774798685319400L, tLogger);
      setTL(appReg, -4198074063353182686L, tLogger);
   }

   private static final void setTL(ApplicationRegistry appReg, long guid, TrafficLogger tl) {
      TransmissionService service = (TransmissionService)appReg.get(guid);
      if (service != null) {
         service.setTrafficLogger(tl);
      }
   }
}
