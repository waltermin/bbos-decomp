package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.InputHintLabelField;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;

public final class AccountSetupSuggestionsScreen extends BasicScreen {
   private LabelField _userNameTakenLabel;
   private RadioButtonField _suggestion1Choice;
   private RadioButtonField _suggestion2Choice;
   private RadioButtonField _suggestion3Choice;
   private RadioButtonField _customUserNameChoice;
   private BasicEditField _customUserNameEdit;
   private PasswordEditField _passwordEdit;
   private PasswordEditField _passwordConfirmEdit;
   private static final String PARAM_USERNAME = "userName";
   private static final String PARAM_PASSWORD = "password";

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(2));
      this._userNameTakenLabel = (LabelField)(new Object());
      this.addContentField(this._userNameTakenLabel);
      RadioButtonGroup _suggestionRadioGroup = (RadioButtonGroup)(new Object());
      this._suggestion1Choice = (RadioButtonField)(new Object(null, _suggestionRadioGroup, true));
      this._suggestion2Choice = (RadioButtonField)(new Object(null, _suggestionRadioGroup, false));
      this._suggestion3Choice = (RadioButtonField)(new Object(null, _suggestionRadioGroup, false));
      this._customUserNameChoice = (RadioButtonField)(new Object(ApplicationResources.getString(65), _suggestionRadioGroup, false));
      this._customUserNameEdit = (BasicEditField)(new Object());
      this._customUserNameEdit.setChangeListener(new AccountSetupSuggestionsScreen$CustomUsernameEditFieldListener(this, null));
      this.addContentField(this._suggestion1Choice);
      this.addContentField(this._suggestion2Choice);
      this.addContentField(this._suggestion3Choice);
      this.addContentField(this._customUserNameChoice);
      this.addContentField(this._customUserNameEdit, true);
      this._passwordEdit = (PasswordEditField)(new Object(null, null));
      this._passwordConfirmEdit = (PasswordEditField)(new Object(null, null));
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
      this.attachEventToField(closeButton, new CloseEvent());
      this.attachEventToField(backButton, new BackEvent());
      CommandEvent nextEvent = new CommandEvent(17, 5, new String[]{"userName", "password"});
      this.attachEventToField(nextButton, nextEvent);
      this.setDefaultEvent(nextEvent);
      String userName = ClientSessionState.getInstance().getAccountSetupUserName();
      String userNameTakenText = MessageFormat.format(ApplicationResources.getString(36), new Object[]{userName});
      this._userNameTakenLabel.setText(userNameTakenText);
      String[] suggestions = ClientSessionState.getInstance().getAccountSetupSuggestions();
      this._suggestion1Choice.setLabel(suggestions[0]);
      this._suggestion2Choice.setLabel(suggestions[1]);
      this._suggestion3Choice.setLabel(suggestions[2]);
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      String userName = null;
      if (this._customUserNameChoice.isSelected()) {
         userName = this._customUserNameEdit.getText();
         if (!InputValidationUtils.isValidUsername(userName)) {
            if (InputValidationUtils.hasTooFewUsernameCharacters(userName)) {
               this.setError(ApplicationResources.getString(102));
               return false;
            }

            if (InputValidationUtils.hasTooManyUsernameCharacters(userName)) {
               this.setError(ApplicationResources.getString(101));
               return false;
            }

            this.setError(ApplicationResources.getString(103));
            return false;
         }
      } else if (this._suggestion1Choice.isSelected()) {
         userName = this._suggestion1Choice.getLabel();
      } else if (this._suggestion2Choice.isSelected()) {
         userName = this._suggestion2Choice.getLabel();
      } else if (this._suggestion3Choice.isSelected()) {
         userName = this._suggestion3Choice.getLabel();
      }

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
