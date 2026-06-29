package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

public final class OptionsScreen extends MainScreen {
   Diag _manager;
   LabelField _title;
   EditField _email;
   EditField _pin;
   private MenuItem _saveMenuItem;
   private static final int MENU_SAVE = 0;

   OptionsScreen(Diag manager) {
      this.setupMenuItems();
      this._manager = manager;
      this._title = (LabelField)(new Object(DiagnosticResources.getString(14), 1152921504606846976L));
      this.setTitle(this._title);
      this._email = (EditField)(new Object(
         ((StringBuffer)(new Object())).append(DiagnosticResources.getString(12)).append(":  ").toString(),
         this._manager.isEmailRecptSetByITPolicy() ? ITPolicy.getString(46, 2) : DiagOptions.getOptions().getEmailRecpt()
      ));
      this._email.setMaxSize(255);
      this.add(this._email);
      if (this._manager.isEmailRecptSetByITPolicy()) {
         this._email.setEditable(false);
      }

      this._pin = (EditField)(new Object(
         ((StringBuffer)(new Object())).append(DiagnosticResources.getString(13)).append(":  ").toString(),
         this._manager.isPinRecptSetByITPolicy() ? this.formatPinRecpt(ITPolicy.getString(46, 3)) : DiagOptions.getOptions().getPinRecpt()
      ));
      this._pin.setMaxSize(8);
      this.add(this._pin);
      if (this._manager.isPinRecptSetByITPolicy()) {
         this._pin.setEditable(false);
      }
   }

   private final String formatPinRecpt(String original) {
      StringBuffer formatted = (StringBuffer)(new Object(original.length()));

      for (int i = 0; i < original.length(); i++) {
         char c = original.charAt(i);
         if (c >= 'a' && c <= 'f') {
            formatted.append((char)(c - ' '));
         } else {
            formatted.append(c);
         }
      }

      return formatted.toString();
   }

   private final void setupMenuItems() {
      this._saveMenuItem = new OptionsScreen$1(this, DiagnosticResources.getString(15), 0, 0);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if ((!this._manager.isPinRecptSetByITPolicy() || !this._manager.isEmailRecptSetByITPolicy()) && (this._email.isDirty() || this._pin.isDirty())) {
         menu.add(this._saveMenuItem);
      }
   }

   @Override
   public final boolean onSave() {
      if (!this._manager.isEmailRecptSetByITPolicy()) {
         this._email.setDirty(false);
         this._manager.saveEmailRecpt(this._email.getText().trim());
      }

      if (!this._manager.isPinRecptSetByITPolicy()) {
         this._pin.setDirty(false);
         this._manager.savePinRecpt(this._pin.getText().trim());
      }

      this._manager.popScreen(this);
      return true;
   }

   private final boolean changesMade() {
      return this._email.isDirty() || this._pin.isDirty();
   }

   @Override
   public final boolean onClose() {
      if (this.changesMade()) {
         int response = Dialog.ask(1);
         if (response == 1) {
            this.onSave();
            return true;
         }

         if (response == -1) {
            return false;
         }
      }

      this._manager.popScreen(this);
      return true;
   }
}
