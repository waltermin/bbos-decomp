package net.rim.device.internal.provisioning;

import net.rim.device.api.system.Branding;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.system.RadioInternal;

class IntelligentPDPHandler implements ProvisioningHandler, SystemListener2 {
   private boolean _enabled;
   private boolean _initialized = false;
   private static final long GUID;
   private static final int BRANDING_ON;
   private static final int BRANDING_OFF;
   private static final int PROVISIONING_ON;
   private static final int PROVISIONING_OFF;
   private static final int RADIO_MULTIPLE_PDP_ENABLE;

   public IntelligentPDPHandler() {
      EventLogger.register(-7994417364447547294L, "net.rim.bbprotectmode", 2);
   }

   @Override
   public void updateProvisioningData(IntIntHashtable chunks, DataBuffer db) {
      boolean isSet = false;
      byte[] data = Branding.getData(24);
      boolean brandingEnabled = data == null || data.length == 0 || data[0] == 0;

      label61:
      try {
         if (chunks.containsKey(34)) {
            db.setPosition(chunks.get(34));
            this.setValue(TLEUtilities.readIntegerField(db) == 0);
            if (this._enabled) {
               EventLogger.logEvent(-7994417364447547294L, 1349668718, 0);
            } else {
               EventLogger.logEvent(-7994417364447547294L, 1349668710, 0);
            }

            isSet = true;
         }
      } finally {
         break label61;
      }

      if (!isSet) {
         this.setValue(brandingEnabled);
         if (this._enabled) {
            EventLogger.logEvent(-7994417364447547294L, 1114787694, 0);
            return;
         }

         EventLogger.logEvent(-7994417364447547294L, 1114787686, 0);
      }
   }

   private void setValue(boolean value) {
      if (this._enabled != value || !this._initialized) {
         this._enabled = value;
         this._initialized = true;
         RadioInternal.setup(113, value ? 1 : 0);
      }
   }

   @Override
   public void batteryGood() {
   }

   @Override
   public void batteryLow() {
   }

   @Override
   public void batteryStatusChange(int status) {
   }

   @Override
   public void powerOff() {
   }

   @Override
   public void powerUp() {
   }

   @Override
   public void backlightStateChange(boolean on) {
   }

   @Override
   public void cradleMismatch(boolean mismatch) {
   }

   @Override
   public void powerOffRequested(int reason) {
   }

   @Override
   public void usbConnectionStateChange(int state) {
   }

   @Override
   public void fastReset() {
      try {
         ProvisioningService.getInstance().updateHandler(this);
      } finally {
         return;
      }
   }
}
