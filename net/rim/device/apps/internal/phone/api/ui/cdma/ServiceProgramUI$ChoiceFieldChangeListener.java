package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.container.MainScreen;

final class ServiceProgramUI$ChoiceFieldChangeListener implements FieldChangeListener {
   private ChoiceField _sourceField;
   private MainScreen _screen;
   private ServiceProgramUI$DependentInfo _info;

   ServiceProgramUI$ChoiceFieldChangeListener(ChoiceField f, ServiceProgramUI$DependentInfo info, MainScreen screen) {
      this._sourceField = f;
      this._screen = screen;
      this._info = info;
   }

   @Override
   public final void fieldChanged(Field f, int context) {
      if (f == this._sourceField) {
         int index = this._sourceField.getSelectedIndex();

         for (int i = 0; i < this._info._dependedBy.length; i++) {
            ChoiceField fieldOnScreen = (ChoiceField)this._screen.getField(this._info._dependedBy[i]);
            if (index == 0) {
               fieldOnScreen.setEditable(true);
            } else {
               fieldOnScreen.setSelectedIndex(0);
               fieldOnScreen.setEditable(false);
            }
         }
      }
   }
}
