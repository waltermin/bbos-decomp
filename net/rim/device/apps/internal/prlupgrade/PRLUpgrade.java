package net.rim.device.apps.internal.prlupgrade;

import net.rim.device.api.system.Branding;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.EngineeringDataListener;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.resources.Resource;

final class PRLUpgrade implements EngineeringDataListener {
   private boolean _fastResetNeeded;
   private Object _syncObject = new Object();
   private static final String SPRINT_PRL_FILE = "Sprint_Saturn_ONLY_evdo_prl.bin";
   private static final String SPRINT_LEGACY_PRL_FILE = "Sprint_evdo_prl.bin";

   private PRLUpgrade(byte[] prl) {
      if (prl != null) {
         Proxy.getInstance().addRadioListener(this);
         synchronized (this._syncObject) {
            if (RadioInternal.processOTASPMessage(1, prl)) {
               this._fastResetNeeded = true;
            } else {
               Proxy.getInstance().removeRadioListener(this);
            }
         }
      }
   }

   public static final void main(String[] args) {
      if (RadioInfo.areWAFsSupported(2)) {
         String prlFile = null;
         switch (Branding.getVendorId()) {
            case 104:
            case 213:
            case 225:
               switch (InternalServices.getHardwareID()) {
                  case 67111172:
                  case 67111684:
                  case 469763332:
                     prlFile = "Sprint_evdo_prl.bin";
                     break;
                  default:
                     prlFile = "Sprint_Saturn_ONLY_evdo_prl.bin";
               }
            default:
               if (prlFile != null) {
                  new PRLUpgrade(Resource.getResourceClass().getResource(prlFile));
               }
         }
      }
   }

   @Override
   public final void engResponseMasterReset(int type) {
   }

   @Override
   public final void engServiceProgramEvent(int type) {
   }

   @Override
   public final void engOTASPResponse(byte[] response) {
      synchronized (this._syncObject) {
         if (this._fastResetNeeded) {
            this._fastResetNeeded = false;
            InternalServices.initiateReset("PRL upgrade");
         }
      }
   }

   @Override
   public final void engDataInitialized() {
   }

   @Override
   public final void engDataChanged() {
   }

   @Override
   public final void engDataLogworthy(int type) {
   }
}
