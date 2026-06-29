package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.EditField;

final class UAProfUrlOptionField extends EditField implements MMSOptionsScreen$Saveable {
   UAProfUrlOptionField() {
      super("UAProf URL: ", MMSClientServiceBook.getUAProfUrl());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setUAProfUrl(this.getText().trim());
      }
   }
}
