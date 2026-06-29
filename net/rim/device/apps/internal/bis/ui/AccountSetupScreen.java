package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.InputHintLabelField;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;

public final class AccountSetupScreen extends BasicScreen {
   private BasicEditField _userNameEdit;
   private PasswordEditField _passwordEdit;
   private PasswordEditField _passwordConfirmEdit;
   private static final String PARAM_USERNAME = "userName";
   private static final String PARAM_PASSWORD = "password";

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(2));
      this._userNameEdit = (BasicEditField)(new Object(null, null));
      this._passwordEdit = (PasswordEditField)(new Object(null, null));
      this._passwordConfirmEdit = (PasswordEditField)(new Object(null, null));
      this.addContentField(new BoldLabelField(ApplicationResources.getString(13)));
      this.addContentField(this._userNameEdit, true);
      this._userNameEdit.setFocus();
      this.addContentField(new InputHintLabelField(ApplicationResources.getString(20)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(14)));
      this.addContentField(this._passwordEdit, true);
      this.addContentField(new InputHintLabelField(ApplicationResources.getString(21)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(19)));
      this.addContentField(this._passwordConfirmEdit, true);
      Button closeButton = new Button(ApplicationResources.getString(15));
      Button backButton = new Button(ApplicationResources.getString(16));
      Button nextButton = new Button(ApplicationResources.getString(17));
      this.addButtonBarButtons(new Button[]{closeButton, backButton, nextButton}, false, 2);
      this.attachEventToField(backButton, new BackEvent());
      CommandEvent nextEvent = new CommandEvent(17, 5, new String[]{"userName", "password"});
      this.attachEventToField(nextButton, nextEvent);
      this.setDefaultEvent(nextEvent);
      this.attachEventToField(closeButton, new CloseEvent());
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      String userName = this._userNameEdit.getText();
      if (!InputValidationUtils.isValidUsername(userName)) {
         if (InputValidationUtils.hasTooFewUsernameCharacters(userName)) {
            this.setError(ApplicationResources.getString(102));
            return false;
         } else if (InputValidationUtils.hasTooManyUsernameCharacters(userName)) {
            this.setError(ApplicationResources.getString(101));
            return false;
         } else {
            this.setError(ApplicationResources.getString(103));
            return false;
         }
      } else {
         String password = this._passwordEdit.getText();
         if (!InputValidationUtils.isValidPassword(password)) {
            if (InputValidationUtils.hasTooFewPasswordCharacters(password)) {
               this.setError(ApplicationResources.getString(105));
               return false;
            } else if (InputValidationUtils.hasTooManyPasswordCharacters(password)) {
               this.setError(ApplicationResources.getString(104));
               return false;
            } else {
               this.setError(ApplicationResources.getString(106));
               return false;
            }
         } else {
            String passwordConfirm = this._passwordConfirmEdit.getText();
            if (!password.equals(passwordConfirm)) {
               this.setError(ApplicationResources.getString(108));
               return false;
            } else {
               inputMap.put("userName", userName);
               inputMap.put("password", password);
               return true;
            }
         }
      }
   }
}
