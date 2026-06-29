package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.MainScreen;

final class ServiceProgramUI$EditFieldChangeListener implements FieldChangeListener {
   private EditField _sourceField;
   private ServiceProgramUI$EditFieldDependentInfo _info;
   private MainScreen _screen;

   ServiceProgramUI$EditFieldChangeListener(EditField f, ServiceProgramUI$EditFieldDependentInfo info, MainScreen screen) {
      this._sourceField = f;
      this._info = info;
      this._screen = screen;
   }

   @Override
   public final void fieldChanged(Field f, int context) {
      if (f == this._sourceField && this._sourceField.isDirty()) {
         String value = this._sourceField.getText();

         for (int i = 0; i < this._info._dependedBy.length; i++) {
            EditField fieldOnScreen = (EditField)this._screen.getField(this._info._dependedBy[i]);
            String text = fieldOnScreen.getText();
            int delimPos = text.indexOf((char)this._info._delimitter[i]);
            if (delimPos >= 0) {
               String rest = text.substring(delimPos, text.length());
               fieldOnScreen.setText(value + rest);
            } else {
               fieldOnScreen.setText(value);
            }
         }
      }
   }
}
