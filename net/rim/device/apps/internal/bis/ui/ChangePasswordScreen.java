package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.InputHintLabelField;
import net.rim.device.apps.internal.bis.api.ui.LinkField;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;

public class ChangePasswordScreen extends UserSettingsScreen {
   protected int _saveCommandId = 18;
   protected String _helpFile = "231237.wml";
   protected int _forgotCommandId = -1;
   private PasswordEditField _oldPasswordEdit;
   private PasswordEditField _passwordEdit;
   private PasswordEditField _passwordConfirmEdit;
   protected Button _saveButton;
   public static final String PARAM_USERNAME = "userName";
   public static final String PARAM_PASSWORD = "password";
   public static final String PARAM_OLD_PASSWORD = "oldPassword";

   public ChangePasswordScreen() {
      super(23);
   }

   protected String getTitle() {
      return ApplicationResources.getString(178);
   }

   @Override
   public void refresh(Hashtable screenParams) {
      this.setTitle(this.getTitle());
      this._passwordEdit = (PasswordEditField)(new Object(null, null));
      this._passwordConfirmEdit = (PasswordEditField)(new Object(null, null));
      this._oldPasswordEdit = (PasswordEditField)(new Object(null, null));
      Field oldPasswordLabel = new BoldLabelField(ApplicationResources.getString(179));
      if (this._forgotCommandId == -1) {
         this.addContentField(oldPasswordLabel);
      } else {
         LinkField forgotPasswordLinkField = new LinkField(ApplicationResources.getString(79));
         forgotPasswordLinkField.setMargin(0, 0, 0, 10);
         this.addContentFieldRow(new Object[]{oldPasswordLabel, forgotPasswordLinkField});
         this.attachEventToField(forgotPasswordLinkField, new CommandEvent(247, this._forgotCommandId, new Object[0]));
      }

      this.addContentField(this._oldPasswordEdit, true);
      this.addContentField(new InputHintLabelField(ApplicationResources.getString(21)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(180)));
      this.addContentField(this._passwordEdit, true);
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(270)));
      this.addContentField(this._passwordConfirmEdit, true);
      Button cancelButton = new Button(ApplicationResources.getString(28));
      this._saveButton = new Button(ApplicationResources.getString(29));
      this.addButtonBarButtons(new Button[]{cancelButton, this._saveButton}, false, 1);
      this.attachEventToField(cancelButton, new BackEvent(28));
      CommandEvent saveEvent = new CommandEvent(29, this._saveCommandId, new String[]{"oldPassword", "password"});
      this.attachEventToField(this._saveButton, saveEvent);
      this.setDefaultEvent(saveEvent);
      this.setHelp(this._helpFile);
   }

   @Override
   public boolean importFormDataFromUI(Hashtable inputMap) {
      if (this._saveButton.isFocus()) {
         String oldPassword = this._oldPasswordEdit.getText();
         String password = this._passwordEdit.getText();
         if (!InputValidationUtils.validatePassword(this, password)) {
            return false;
         }

         String passwordConfirm = this._passwordConfirmEdit.getText();
         if (!password.equals(passwordConfirm)) {
            this.setError(ApplicationResources.getString(108));
            return false;
         }

         inputMap.put("oldPassword", oldPassword);
         inputMap.put("password", password);
      }

      return true;
   }
}
