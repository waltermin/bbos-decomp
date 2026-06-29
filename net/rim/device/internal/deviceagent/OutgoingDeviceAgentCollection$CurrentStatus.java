package net.rim.device.internal.deviceagent;

import net.rim.device.internal.proxy.Proxy;

final class OutgoingDeviceAgentCollection$CurrentStatus implements Runnable {
   private int _id;
   private final OutgoingDeviceAgentCollection this$0;

   OutgoingDeviceAgentCollection$CurrentStatus(OutgoingDeviceAgentCollection _1) {
      this.this$0 = _1;
   }

   public final void start(long time) {
      this._id = Proxy.getInstance().invokeLater(this, time, true);
   }

   public final void stop() {
      Proxy.getInstance().cancelInvokeLater(this._id);
   }

   @Override
   public final void run() {
      this.this$0.collectCurrentDeviceInformation();
   }
}
