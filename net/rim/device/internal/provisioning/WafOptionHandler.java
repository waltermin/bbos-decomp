package net.rim.device.internal.provisioning;

import net.rim.device.api.system.Branding;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.RadioInternal;

final class WafOptionHandler implements ProvisioningHandler, SystemListener2 {
   private static final int DTM_VERSION = 3;
   private static final int RRLP_VERSION = 1;
   private static final int SUPL_VERSION = 1;
   private static final int DTM_ENABLED = 1146375525;
   private static final int DTM_DISABLED = 1146375524;
   private static final int RRLP_ENABLED = 1381125221;
   private static final int RRLP_DISABLED = 1381125220;
   private static final int SUPL_ENABLED = 1398100069;
   private static final int SUPL_DISABLED = 1398100068;
   private static final int OPTION_ENABLED = 1;
   private static final int OPTION_NOT_PROVISIONED = 0;
   private static final int OPTION_DISABLED = -1;

   WafOptionHandler() {
      Proxy.getInstance().addSystemListener(this);
   }

   public static final boolean isSupported() {
      return RadioInfo.areWAFsSupported(1) && RadioInternal.isDTMCapable();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void updateProvisioningData(IntIntHashtable chunks, DataBuffer db) {
      int dtmEnabled = isEnabled(chunks, db, 24, 3);
      if (dtmEnabled != 0) {
         EventLogger.logEvent(2028567949942654338L, dtmEnabled == 1 ? 1146375525 : 1146375524);

         label111:
         try {
            RadioInternal.enableDTM(dtmEnabled == 1);
         } catch (Throwable var13) {
            ProvisioningService.log(((StringBuffer)(new Object("DTM "))).append(e.toString()).toString(), 2);
            break label111;
         }
      }

      boolean rimBranded = Branding.getVendorId() == 1;
      boolean rrlpEnabled = isEnabled(chunks, db, 25, 1, rimBranded);
      boolean suplEnabled = rrlpEnabled && isEnabled(chunks, db, 26, 1, rimBranded);
      EventLogger.logEvent(2028567949942654338L, rrlpEnabled ? 1381125221 : 1381125220);
      if (rrlpEnabled) {
         EventLogger.logEvent(2028567949942654338L, suplEnabled ? 1398100069 : 1398100068);
      }

      try {
         RadioInternal.setAGPSOptions(rrlpEnabled, suplEnabled);
      } catch (Throwable var12) {
         ProvisioningService.log(((StringBuffer)(new Object("A-GPS "))).append(e.toString()).toString(), 2);
         return;
      }
   }

   private static final boolean isEnabled(IntIntHashtable chunks, DataBuffer db, int field, int version, boolean defaultValue) {
      int optionEnabled = isEnabled(chunks, db, field, version);
      return optionEnabled == 0 ? defaultValue : optionEnabled == 1;
   }

   private static final int isEnabled(IntIntHashtable chunks, DataBuffer db, int field, int version) {
      try {
         if (chunks != null && db != null && chunks.containsKey(field)) {
            db.setPosition(chunks.get(field));
            int versionRequired = TLEUtilities.readIntegerField(db);
            return versionRequired > 0 && version >= versionRequired ? 1 : -1;
         }
      } finally {
         return 0;
      }

      return 0;
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerOffRequested(int reason) {
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void fastReset() {
      try {
         ProvisioningService.getInstance().updateHandler(this);
      } finally {
         return;
      }
   }

   @Override
   public final void backlightStateChange(boolean on) {
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }
}
