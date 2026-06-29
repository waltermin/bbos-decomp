package net.rim.device.apps.internal.activation;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.internal.provisioning.ProvisioningHandler;

final class ActivationServiceHandler implements ProvisioningHandler {
   private byte[] _state = new byte[1];
   ActivationServiceImpl _activationService;
   private static final int DEFAULT_VALUE = 1;

   public final boolean isOTAEnterpriseActivationProvisioned() {
      return this._state[0] != 0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void updateProvisioningData(IntIntHashtable chunks, DataBuffer db) {
      byte newValue = 1;
      EventLogger.logEvent(1200380696048604626L, 1095784016, 0);
      EventLogger.logEvent(1200380696048604626L, this._state, 0);
      boolean var6 = false /* VF: Semaphore variable */;

      label35:
      try {
         var6 = true;
         if (chunks.containsKey(1)) {
            db.setPosition(chunks.get(1));
            if (db.readCompressedInt() != 1) {
               throw new Object();
            }

            newValue = db.readByte();
            var6 = false;
         } else {
            var6 = false;
         }
      } finally {
         if (var6) {
            EventLogger.logEvent(1200380696048604626L, 1095783978, 2);
            break label35;
         }
      }

      if (this._state[0] != newValue) {
         this._state[0] = newValue;
         EventLogger.logEvent(1200380696048604626L, 1095783979, 0);
         EventLogger.logEvent(1200380696048604626L, this._state, 0);
         this._activationService.iconRefresh();
      } else {
         EventLogger.logEvent(1200380696048604626L, 1095783981, 0);
         EventLogger.logEvent(1200380696048604626L, this._state, 0);
      }
   }

   public ActivationServiceHandler(ActivationServiceImpl activationService) {
      this._activationService = activationService;
      this._state[0] = 1;
   }
}
