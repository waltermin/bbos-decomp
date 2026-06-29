package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.api.ui.CommonResources;

public final class DialogEnterString extends Dialog {
   private EditField _editString;
   private ButtonField _buttonOk;
   private ButtonField _buttonCancel;

   public DialogEnterString(String message, String defaultValue, String buttonLabel) {
      this(message, defaultValue, buttonLabel, "");
   }

   public DialogEnterString(String message, String defaultValue, String buttonLabel, String editFieldLabel) {
      super(message, null, null, 0, null);
      this.setEscapeEnabled(false);
      this._editString = new EditField(editFieldLabel, defaultValue, 1000000, 4503601774854144L);
      this.add(this._editString);
      this._buttonOk = new ButtonField(buttonLabel);
      this._buttonOk.setChangeListener(this);
      this._buttonCancel = new ButtonField(CommonResources.getString(9042));
      this._buttonCancel.setChangeListener(this);
      HorizontalFieldManager hfm = new HorizontalFieldManager(12884901888L);
      hfm.add(this._buttonOk);
      hfm.add(this._buttonCancel);
      this.add(hfm);
      this._editString.setFocus();
   }

   public final String getResult() {
      return this._editString.getText();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._buttonCancel) {
         this.cancel();
      } else {
         if (field == this._buttonOk) {
            this.close();
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (!handled) {
         if (key == 27) {
            this.cancel();
         }

         if (key == '\n') {
            this.close();
         }

         return true;
      } else {
         return handled;
      }
   }
}
