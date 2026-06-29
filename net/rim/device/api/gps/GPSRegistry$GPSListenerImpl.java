package net.rim.device.api.gps;

import net.rim.device.api.util.IntEnumeration;

class GPSRegistry$GPSListenerImpl implements GPSListener {
   int _errorCount;
   private final GPSRegistry this$0;

   GPSRegistry$GPSListenerImpl(GPSRegistry _1) {
      this.this$0 = _1;
   }

   @Override
   public void gpsModeChangeComplete(boolean success, int mode) {
   }

   @Override
   public void gpsPDEChangeComplete(boolean success, int ip, int port) {
      synchronized (GPSRegistry._pdeLock) {
         this.this$0._pdeRequestSuccess = success;
         GPSRegistry._pdeLock.notifyAll();
      }
   }

   @Override
   public void gpsLocationUpdated(int error, int type, int modeAvailable) {
      int code = -1;
      int eCount = this._errorCount;
      synchronized (this.this$0._locationLock) {
         this.this$0._locationLock.notifyAll();
      }

      if (error == 0) {
         if (type == 0) {
            code = this.this$0.gpsGetLocation(this.this$0._standardLocation, modeAvailable);
         } else if (type == 3) {
            code = this.this$0.gpsGetLocation(this.this$0._extendedLocation, modeAvailable);
         }
      }

      if (error == 0 && code == 0) {
         this._errorCount = 0;
         this.this$0.notifyListeners(modeAvailable, 5678354684824604352L);
      } else if (eCount >= 3) {
         this.this$0.notifyListeners(modeAvailable, -7999774137434187609L);
      } else {
         this.this$0.restartLocationUpdate(modeAvailable);
         this._errorCount = eCount + 1;
      }
   }

   @Override
   public void gpsResponseGetLPS(int result) {
   }

   @Override
   public void gpsResponseSetLPS(int result) {
   }

   @Override
   public void gpsResponseEnablePIN(int result) {
   }

   @Override
   public void gpsResponseChangePIN(int result) {
   }

   @Override
   public void gpsEphemerisDataRequired(int format) {
   }

   @Override
   public void gpsCredentialChangeComplete(boolean success, int clientId) {
      IntEnumeration en = this.this$0._pdeTable.keys();

      while (en.hasMoreElements()) {
         int pid = en.nextElement();
         GPSRegistry$PDEInfoStatus pdeInfoStatus = (GPSRegistry$PDEInfoStatus)this.this$0._pdeTable.get(pid);
         if (pdeInfoStatus != null) {
            GPS$GPSPDEInfo pdeInfo = pdeInfoStatus.getPDEInfo();
            if (pdeInfo != null && pdeInfo.getCredential() != null && pdeInfo.getCredential().getAppId() == clientId) {
               pdeInfoStatus.setCredStatus(success);
               if (!success) {
                  this.this$0._criteriaTable.remove(pid);
                  this.this$0._assistFixConsumers.remove(pid);
               }
            }
         }
      }

      if (!success) {
         this.this$0.restartCDMAAssistedLocationUpdate();
      }
   }

   @Override
   public void gpsLocationAidingRequest() {
   }
}
