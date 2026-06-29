package net.rim.device.cldc.io.srp;

import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;

class SrpConnectionManager$1 implements Runnable {
   private final SrpConnectionManager this$0;

   SrpConnectionManager$1(SrpConnectionManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      HostRoutingTable hrt = HRUtils.getDefaultHRT();
      if (hrt != null) {
         HostRoutingInfo[] hris = hrt.getHris();
         int size = hrt.getNumHris();

         for (int i = size - 1; i >= 0; i--) {
            HostRoutingInfo hri = hris[i];
            if ((hri.getArt() & 8) != 0) {
               this.this$0.getRoutingInfoRelay(0, 1, hri, null, (byte)1, 0, false);
            } else if ((hri.getArt() & 55) == 0 && SrpUtils.supportRFLink(1)) {
               this.this$0.getRoutingInfoRelay(1, 1, hri, null, (byte)1, 0, false);
            }
         }
      }
   }
}
