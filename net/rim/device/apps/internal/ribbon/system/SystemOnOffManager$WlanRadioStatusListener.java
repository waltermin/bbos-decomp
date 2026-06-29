package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.system.RadioStatusListener;

final class SystemOnOffManager$WlanRadioStatusListener implements RadioStatusListener {
   private final SystemOnOffManager this$0;

   SystemOnOffManager$WlanRadioStatusListener(SystemOnOffManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      if ((this.this$0._radioIsOn & 4) == 0) {
         SystemOnOffManager.access$076(this.this$0, 4);
         this.this$0.saveRadioState();
      }
   }

   @Override
   public final void radioTurnedOff() {
      System.out.println("*** WLAN radioTurnedOff()");
      if (!this.this$0._isPoweringDown) {
         SystemOnOffManager.access$072(this.this$0, -5);
         this.this$0.saveRadioState();
      }
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
   }
}
