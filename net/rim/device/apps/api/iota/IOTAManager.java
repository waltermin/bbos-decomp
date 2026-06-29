package net.rim.device.apps.api.iota;

import net.rim.device.api.system.ApplicationRegistry;

public class IOTAManager {
   private static final long KEY;
   public static final int IOTA_IDLE;
   public static final int IOTA_BUSY;
   public static final int IOTA_CIP_IDLE;
   public static final int IOTA_CIP_BUSY;

   public static void register(IOTAManager manager) {
      ApplicationRegistry.getApplicationRegistry().put(-2072185981212834145L, manager);
   }

   public static IOTAManager getInstance() {
      return (IOTAManager)ApplicationRegistry.getApplicationRegistry().get(-2072185981212834145L);
   }

   public boolean initiateIOTA(int _1) {
      throw null;
   }

   public boolean cancelIOTA() {
      throw null;
   }

   public int currentStatus() {
      throw null;
   }
}
