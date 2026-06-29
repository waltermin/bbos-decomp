package net.rim.device.internal.system;

import net.rim.device.api.system.RadioStatusListener;

final class CoverageInfoInternalImpl$MyRadioStatusListener implements RadioStatusListener {
   private int _waf;
   private final CoverageInfoInternalImpl this$0;

   public final void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      synchronized (this.this$0) {
         this.this$0.updateRadioCoverageAndNotifyListeners(this._waf);
      }
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      synchronized (this.this$0) {
         this.this$0.updateRadioCoverageAndNotifyListeners(this._waf);
      }
   }

   @Override
   public final void radioTurnedOff() {
      synchronized (this.this$0) {
         this.this$0.updateRadioCoverageAndNotifyListeners(this._waf);
      }
   }

   @Override
   public final void signalLevel(int level) {
      synchronized (this.this$0) {
         int radioCoverage = 0;
         switch (this._waf) {
            case 1:
               radioCoverage = this.this$0._3gppRadioCoverage;
               break;
            case 2:
               radioCoverage = this.this$0._cdmaRadioCoverage;
               break;
            case 4:
               radioCoverage = this.this$0._wlanRadioCoverage;
               break;
            case 8:
               radioCoverage = this.this$0._idenRadioCoverage;
         }

         if (level == -256 ^ radioCoverage == 0) {
            this.this$0.updateRadioCoverageAndNotifyListeners(this._waf);
         }
      }
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      synchronized (this.this$0) {
         this.this$0.updateRadioCoverageAndNotifyListeners(this._waf);
      }
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   public CoverageInfoInternalImpl$MyRadioStatusListener(CoverageInfoInternalImpl _1, int waf) {
      this.this$0 = _1;
      this._waf = waf;
   }
}
