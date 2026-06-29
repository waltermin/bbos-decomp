package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

final class SetupWorkEmailScreen$EmailTypeChoiceFieldListener implements FieldChangeListener {
   private final SetupWorkEmailScreen this$0;

   private SetupWorkEmailScreen$EmailTypeChoiceFieldListener(SetupWorkEmailScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.this$0._ispOutlook.isSelected()) {
         this.this$0._emailSetupLinkEvent.setLink(24);
      } else if (this.this$0._ispProvideSettings.isSelected()) {
         this.this$0._emailSetupLinkEvent.setLink(23);
      } else if (this.this$0._owa.isSelected()) {
         this.this$0._emailSetupLinkEvent.setLink(22);
      } else {
         this.this$0._emailSetupLinkEvent.setLink(21);
      }
   }

   SetupWorkEmailScreen$EmailTypeChoiceFieldListener(SetupWorkEmailScreen x0, SetupWorkEmailScreen$1 x1) {
      this(x0);
   }
}
