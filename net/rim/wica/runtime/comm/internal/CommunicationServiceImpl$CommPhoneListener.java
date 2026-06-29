package net.rim.wica.runtime.comm.internal;

import net.rim.blackberry.api.phone.AbstractPhoneListener;
import net.rim.device.api.system.RadioInfo;

class CommunicationServiceImpl$CommPhoneListener extends AbstractPhoneListener {
   private final CommunicationServiceImpl this$0;

   private CommunicationServiceImpl$CommPhoneListener(CommunicationServiceImpl this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void callConnected(int arg0) {
      synchronized (this) {
         this.this$0._inDataCoverage = false;
         this.notifyAll();
      }

      this.this$0.fireCoverageEvent(this.this$0.isInCoverage());
   }

   @Override
   public void callDisconnected(int arg0) {
      synchronized (this) {
         this.this$0._inDataCoverage = (RadioInfo.getNetworkService() & 4) != 0;
         this.notifyAll();
      }

      this.this$0.fireCoverageEvent(this.this$0.isInCoverage());
   }

   CommunicationServiceImpl$CommPhoneListener(CommunicationServiceImpl x0, CommunicationServiceImpl$1 x1) {
      this(x0);
   }
}
