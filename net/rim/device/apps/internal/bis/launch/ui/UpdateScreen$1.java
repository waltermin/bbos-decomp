package net.rim.device.apps.internal.bis.launch.ui;

import net.rim.device.api.ui.Field;

final class UpdateScreen$1 implements FieldChangeRunnable {
   private final UpdateScreen this$0;

   UpdateScreen$1(UpdateScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run(Field field, int context) {
      this.this$0.exit();
   }
}
