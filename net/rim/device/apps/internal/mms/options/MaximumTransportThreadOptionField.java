package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.NumericChoiceField;

final class MaximumTransportThreadOptionField extends NumericChoiceField implements MMSOptionsScreen$Saveable {
   MaximumTransportThreadOptionField() {
      super("Max Transport Threads: ", 0, 4, 1, MMSClientServiceBook.getMaxTransportThreads());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setMaxTransportThreads(this.getSelectedIndex());
      }
   }
}
