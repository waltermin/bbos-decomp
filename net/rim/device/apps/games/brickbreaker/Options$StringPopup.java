package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.api.ui.CommonResources;

public final class Options$StringPopup extends Dialog {
   private TextField _editString;
   private ButtonField _buttonOk;
   private ButtonField _buttonCancel;

   public Options$StringPopup(String message, boolean passwordField) {
      super(message, null, null, 0, null);
      this.setEscapeEnabled(false);
      if (passwordField) {
         this._editString = (TextField)(new Object("", "", 24, 4503601774854144L));
      } else {
         this._editString = (TextField)(new Object("", "", 24, 4503601774854144L));
      }

      this._editString.setFilter(new HighScoreTextFilter());
      this._editString.setAllowUnicodeInput(false);
      this.add(this._editString);
      this._buttonOk = (ButtonField)(new Object(CommonResources.getString(117)));
      this._buttonOk.setChangeListener(this);
      this._buttonCancel = (ButtonField)(new Object(CommonResources.getString(9042)));
      this._buttonCancel.setChangeListener(this);
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(12884901888L));
      hfm.add(this._buttonOk);
      hfm.add(this._buttonCancel);
      this.add(hfm);
      this._editString.setFocus();
   }

   public final String getResult() {
      return this._editString.getText().trim();
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
