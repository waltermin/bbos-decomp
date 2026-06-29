package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.i18n.CommonResource;

public final class PasswordKeeperOptionsScreen extends AppsMainScreen {
   private NumericChoiceField _passwordLength;
   private ObjectChoiceField _passwordAlpha;
   private ObjectChoiceField _passwordNumeric;
   private ObjectChoiceField _passwordSymbol;
   private ObjectChoiceField _confirmDelete;
   private NumericChoiceField _passwordAttempts;
   private ObjectChoiceField _allowCopy;
   private ObjectChoiceField _showPassword;
   private ObjectChoiceField _otaSync;
   private PasswordKeeperOptionsScreen$PasswordVerb _saveVerb;
   private PasswordKeeperOptionsScreen$PasswordVerb _closeVerb;
   private static final int MENU_ORDERING_SAVE = 131072;
   private static final int MENU_ORDERING_CLOSE = 200704;

   public PasswordKeeperOptionsScreen() {
      super(0);
      this.setDefaultClose(false);
      this.addFields();
   }

   private final void addFields() {
      this.setTitle(PasswordKeeper.getBundle(), 3011);
      PasswordKeeperOptions options = PasswordKeeperOptions.getOptions();
      int alphaIndex = options.getAlpha() ? 0 : 1;
      int numericIndex = options.getNumeric() ? 0 : 1;
      int symbolIndex = options.getSymbol() ? 0 : 1;
      int confirmIndex = options.getConfirmDelete() ? 0 : 1;
      int allowIndex = options.getAllowCopy() ? 0 : 1;
      int showIndex = options.getShowPassword() ? 0 : 1;
      int otaSyncIndex = options.getOTASync() ? 0 : 1;
      this._passwordLength = (NumericChoiceField)(new Object(PasswordKeeper.getString(3012), 4, 16, 1, options.getRandomPasswordLength() - 4));
      this._passwordAlpha = (ObjectChoiceField)(new Object(PasswordKeeper.getString(3025), PasswordKeeper.getStringArray(3013), alphaIndex));
      this._passwordNumeric = (ObjectChoiceField)(new Object(PasswordKeeper.getString(3014), PasswordKeeper.getStringArray(3013), numericIndex));
      this._passwordSymbol = (ObjectChoiceField)(new Object(PasswordKeeper.getString(3015), PasswordKeeper.getStringArray(3013), symbolIndex));
      this._confirmDelete = (ObjectChoiceField)(new Object(CommonResources.getString(2008), PasswordKeeper.getStringArray(3013), confirmIndex));
      this._passwordAttempts = (NumericChoiceField)(new Object(PasswordKeeper.getString(3017), 1, 20, 1, options.getPasswordThreshold() - 1));
      this._allowCopy = (ObjectChoiceField)(new Object(PasswordKeeper.getString(3020), PasswordKeeper.getStringArray(3013), allowIndex));
      this._showPassword = (ObjectChoiceField)(new Object(PasswordKeeper.getString(3021), PasswordKeeper.getStringArray(3013), showIndex));
      this._otaSync = (ObjectChoiceField)(new Object(PasswordKeeper.getString(3038), CommonResources.getYesNoArray(0), otaSyncIndex));
      this.add(this._passwordLength);
      this.add(this._passwordAlpha);
      this.add(this._passwordNumeric);
      this.add(this._passwordSymbol);
      this.add(this._confirmDelete);
      this.add(this._passwordAttempts);
      this.add(this._allowCopy);
      this.add(this._showPassword);
      PasswordKeeperSync passwordKeeperSync = PasswordKeeper.getInstance().getCollection();
      if (SyncManager.getInstance().isOTASyncAvailable(passwordKeeperSync, false)) {
         this.add(this._otaSync);
      }

      this.add((Field)(new Object()));
      this.add((Field)(new Object(CommonResources.getString(9133), Integer.toString(passwordKeeperSync.getSyncObjectCount()))));
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (this._saveVerb == null) {
         this._saveVerb = new PasswordKeeperOptionsScreen$PasswordVerb(this, 1, 131072, PasswordKeeper.getBundle().getFamily(), 8);
      }

      menu.add(this._saveVerb);
      menu.setDefault(this._saveVerb);
      if (this._closeVerb == null) {
         this._closeVerb = new PasswordKeeperOptionsScreen$PasswordVerb(this, 2, 200704, CommonResource.getBundle(), 9);
      }

      menu.add(this._closeVerb);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      }

      switch (key) {
         case '\u001b':
            if (this.hasChanged()) {
               this.promptForSave();
               return true;
            }

            this.close();
            return true;
         default:
            return false;
      }
   }

   @Override
   public final boolean isDataValid() {
      boolean passwordAlpha = this._passwordAlpha.getSelectedIndex() == 0;
      boolean passwordNumeric = this._passwordNumeric.getSelectedIndex() == 0;
      boolean passwordSymbol = this._passwordSymbol.getSelectedIndex() == 0;
      if (!passwordAlpha && !passwordNumeric && !passwordSymbol) {
         Dialog.inform(PasswordKeeper.getString(3027));
         return false;
      } else {
         return true;
      }
   }

   private final boolean hasChanged() {
      PasswordKeeperOptions options = PasswordKeeperOptions.getOptions();
      if (options.getRandomPasswordLength() != this._passwordLength.getSelectedValue()) {
         return true;
      } else if ((options.getAlpha() ? 0 : 1) != this._passwordAlpha.getSelectedIndex()) {
         return true;
      } else if ((options.getNumeric() ? 0 : 1) != this._passwordNumeric.getSelectedIndex()) {
         return true;
      } else if ((options.getSymbol() ? 0 : 1) != this._passwordSymbol.getSelectedIndex()) {
         return true;
      } else if ((options.getConfirmDelete() ? 0 : 1) != this._confirmDelete.getSelectedIndex()) {
         return true;
      } else if (options.getPasswordThreshold() != this._passwordAttempts.getSelectedValue()) {
         return true;
      } else if ((options.getAllowCopy() ? 0 : 1) != this._allowCopy.getSelectedIndex()) {
         return true;
      } else {
         return (options.getShowPassword() ? 0 : 1) != this._showPassword.getSelectedIndex()
            ? true
            : (options.getOTASync() ? 0 : 1) != this._otaSync.getSelectedIndex();
      }
   }

   private final void promptForSave() {
      int choice = Dialog.ask(1);
      if (choice == 1) {
         label28:
         try {
            this.save();
         } finally {
            break label28;
         }

         this.close();
      } else {
         if (choice == 2) {
            this.close();
         }
      }
   }

   @Override
   public final void save() {
      int passwordLength = this._passwordLength.getSelectedValue();
      boolean passwordAlpha = this._passwordAlpha.getSelectedIndex() == 0;
      boolean passwordNumeric = this._passwordNumeric.getSelectedIndex() == 0;
      boolean passwordSymbol = this._passwordSymbol.getSelectedIndex() == 0;
      boolean confirmDelete = this._confirmDelete.getSelectedIndex() == 0;
      int passwordAttempts = this._passwordAttempts.getSelectedValue();
      boolean allowCopy = this._allowCopy.getSelectedIndex() == 0;
      boolean showPassword = this._showPassword.getSelectedIndex() == 0;
      boolean otaSync = this._otaSync.getSelectedIndex() == 0;
      PasswordKeeperOptions options = PasswordKeeperOptions.getOptions();
      options.setRandomPasswordLength(passwordLength);
      options.setAlpha(passwordAlpha);
      options.setNumeric(passwordNumeric);
      options.setSymbol(passwordSymbol);
      options.setConfirmDelete(confirmDelete);
      options.setPasswordThreshold(passwordAttempts);
      options.setAllowCopy(allowCopy);
      options.setShowPassword(showPassword);
      boolean existingOTASync = options.getOTASync();
      options.setOTASync(otaSync);
      if (existingOTASync != otaSync) {
         PasswordKeeper.getInstance().getCollection().updateOTASync(otaSync);
      }
   }
}
