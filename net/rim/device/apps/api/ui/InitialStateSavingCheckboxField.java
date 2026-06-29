package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.component.CheckboxField;

public class InitialStateSavingCheckboxField extends CheckboxField {
   private boolean _initiallyChecked;

   public InitialStateSavingCheckboxField(String label, boolean checked) {
      super(label, checked);
      this._initiallyChecked = checked;
   }

   public boolean getInitiallyChecked() {
      return this._initiallyChecked;
   }
}
