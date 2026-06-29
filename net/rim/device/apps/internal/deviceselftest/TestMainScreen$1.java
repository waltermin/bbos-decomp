package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Status;

final class TestMainScreen$1 implements Runnable {
   private final TestMainScreen this$0;

   TestMainScreen$1(TestMainScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      synchronized (Application.getEventLock()) {
         Status.show(DeviceSelfTestResources.getString(126), 3000);
      }
   }
}
