package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

final class AbstractFilterScreen$AddButtonFieldListener implements FieldChangeListener {
   private final AbstractFilterScreen this$0;

   private AbstractFilterScreen$AddButtonFieldListener(AbstractFilterScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      this.this$0.addContainsContentToContainsArea();
   }

   AbstractFilterScreen$AddButtonFieldListener(AbstractFilterScreen x0, AbstractFilterScreen$1 x1) {
      this(x0);
   }
}
