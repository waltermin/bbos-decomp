package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

final class SelectAccountTypeScreen$AccountTypeChoiceFieldListener implements FieldChangeListener {
   private final SelectAccountTypeScreen this$0;

   private SelectAccountTypeScreen$AccountTypeChoiceFieldListener(SelectAccountTypeScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.this$0._personalEmailField.isSelected()) {
         this.this$0._accountTypeLinkEvent.setLink(19);
      } else {
         this.this$0._accountTypeLinkEvent.setLink(20);
      }
   }

   SelectAccountTypeScreen$AccountTypeChoiceFieldListener(SelectAccountTypeScreen x0, SelectAccountTypeScreen$1 x1) {
      this(x0);
   }
}
