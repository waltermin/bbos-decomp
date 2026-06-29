package net.rim.device.cldc.impl.ipmodem;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.internal.provisioning.ProvisioningHandler;
import net.rim.device.internal.system.RadioInternal;

final class IPModemProvHandler implements ProvisioningHandler, GlobalEventListener {
   private boolean _isIPModemProvisioned = RadioInfo.getNetworkType() != 4;
   public static final long EVENT_LOGGER_GUID = 3239869412285299861L;
   public static final String EVENT_LOGGER_NAME = "net.rim.ipmodem";
   public static final int EVENT_ERROR_EXCEPTION_PROV_PARSING = 1296322640;
   public static final int ERROR_PROVISION_SERVICE_NOT_AVAILABLE = 1347636801;

   public IPModemProvHandler() {
      this.setupIPModem();
   }

   private final void setupIPModem() {
      switch (RadioInfo.getNetworkType()) {
         case 3:
         case 4:
         case 7:
            boolean isIPModemDisabledByITPolicy = ITPolicy.getBoolean(24, 49, false);
            RadioInternal.enableIPModem(this._isIPModemProvisioned && !isIPModemDisabledByITPolicy);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void updateProvisioningData(IntIntHashtable chunks, DataBuffer db) {
      this._isIPModemProvisioned = RadioInfo.getNetworkType() != 4;
      boolean var5 = false /* VF: Semaphore variable */;

      label44:
      try {
         var5 = true;
         if (chunks.containsKey(3)) {
            db.setPosition(chunks.get(3));
            if (db.readCompressedInt() != 1) {
               throw new Object();
            }

            this._isIPModemProvisioned = db.readByte() == 1;
            var5 = false;
         } else {
            var5 = false;
         }
      } finally {
         if (var5) {
            EventLogger.logEvent(3239869412285299861L, 1296322640, 2);
            break label44;
         }
      }

      this.setupIPModem();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         this.setupIPModem();
      }
   }
}
