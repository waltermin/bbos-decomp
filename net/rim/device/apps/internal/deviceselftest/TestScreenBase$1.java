package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;

final class TestScreenBase$1 extends MenuItem {
   private final TestScreenBase this$0;

   TestScreenBase$1(TestScreenBase _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int response = Dialog.ask(3, DeviceSelfTestResources.getString(122), 4);
      if (4 == response) {
         this.this$0.doNext();
      } else {
         this.this$0.isMenuOn = false;
      }
   }
}
