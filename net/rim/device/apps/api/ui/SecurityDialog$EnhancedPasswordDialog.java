package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.internal.ui.component.PasswordDialog;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

class SecurityDialog$EnhancedPasswordDialog extends PasswordDialog {
   private int _choice;
   private Object _signal = new Object();
   private ButtonField[] _extendedButtons;

   public SecurityDialog$EnhancedPasswordDialog(String passwordLabel, boolean revealPassword, Object[] extendedChoices) {
      super(passwordLabel, revealPassword, 32, 33554432);
      VerticalIndentFieldManager vifm = (VerticalIndentFieldManager)this.getDelegate();
      this.setStatusPriority(-1073741823);
      if (extendedChoices == null) {
         this._extendedButtons = new ButtonField[0];
      } else {
         HorizontalFieldManager buttonManager = new HorizontalFieldManager(12884901888L);
         int numChoices = extendedChoices.length;
         this._extendedButtons = new ButtonField[numChoices];

         for (int i = 0; i < numChoices; i++) {
            this._extendedButtons[i] = new ButtonField(extendedChoices[i].toString());
            this._extendedButtons[i].setChangeListener(this);
            buttonManager.add(this._extendedButtons[i]);
         }

         vifm.add(buttonManager);
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      this.updateFieldSelected(field);
      this.releaseSignal();
   }

   private boolean updateFieldSelected(Field field) {
      if (field == super._ok) {
         this.accept();
         this._choice = 0;
         return true;
      }

      if (field == super._cancel) {
         this.cancel();
         this._choice = 1;
         return true;
      }

      if (field instanceof ButtonField) {
         this.close(0);

         for (int i = 0; i < this._extendedButtons.length; i++) {
            if (field == this._extendedButtons[i]) {
               this._choice = i + 2;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void releaseSignal() {
      if (this._choice >= 0) {
         synchronized (this._signal) {
            this._signal.notifyAll();
         }
      }
   }

   public int getChoice() {
      synchronized (this._signal) {
         try {
            this._signal.wait();
         } finally {
            return this._choice;
         }
      }

      return this._choice;
   }

   @Override
   public void show() {
      this._choice = -1;
      super.show();
   }

   @Override
   public void setCancelAllowed(boolean isAllowed) {
      super.setCancelAllowed(isAllowed);
      if (!isAllowed) {
         super._cancel.getManager().delete(super._cancel);
         super._cancel = null;
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.cancel();
         this._choice = 1;
      } else {
         if (key != '\n') {
            return super.keyChar(key, status, time);
         }

         Field field = this.getLeafFieldWithFocus();
         if (!this.updateFieldSelected(field) && field == super._passwordField) {
            super._ok.setFocus();
         }
      }

      this.releaseSignal();
      return true;
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      this.updateFieldSelected(this.getLeafFieldWithFocus());
      this.releaseSignal();
      return true;
   }
}
