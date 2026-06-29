package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.InputHintLabelField;
import net.rim.device.apps.internal.bis.api.ui.RefreshableScreen;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;

public final class UsernameSetupScreen extends UserSettingsScreen {
   private LabelField _userNameTakenLabel;
   private RadioButtonField[] _suggestionChoices;
   private RadioButtonField _customUserNameChoice;
   private BasicEditField _userNameEdit;
   private PasswordEditField _passwordEdit;
   private PasswordEditField _passwordConfirmEdit;
   private String _failedIntegrationType;
   public static final String PARAM_SUGGESTIONS;
   public static final String PARAM_USERNAME;
   public static final String PARAM_PASSWORD;
   public static final String PARAM_FAILED_INTEGRATION_TYPE;
   static Class class$net$rim$device$apps$internal$bis$ui$MailConnectorScreen;
   static Class class$net$rim$device$apps$internal$bis$ui$AccountSetupInstructionScreen;

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setMenuOptions(31);
      this._failedIntegrationType = null;
      this.setTitle(ApplicationResources.getString(231));
      this._userNameEdit = (BasicEditField)(new Object());
      if (screenParams != null && screenParams.containsKey("previousScreen")) {
         RefreshableScreen prevScreen = (RefreshableScreen)screenParams.get("previousScreen");
         if (prevScreen.getClass()
            .isAssignableFrom(
               class$net$rim$device$apps$internal$bis$ui$MailConnectorScreen == null
                  ? (class$net$rim$device$apps$internal$bis$ui$MailConnectorScreen = class$("net.rim.device.apps.internal.bis.ui.MailConnectorScreen"))
                  : class$net$rim$device$apps$internal$bis$ui$MailConnectorScreen
            )) {
            this._failedIntegrationType = "mail_connector";
         } else if (prevScreen.getClass()
            .isAssignableFrom(
               class$net$rim$device$apps$internal$bis$ui$AccountSetupInstructionScreen == null
                  ? (
                     class$net$rim$device$apps$internal$bis$ui$AccountSetupInstructionScreen = class$(
                        "net.rim.device.apps.internal.bis.ui.AccountSetupInstructionScreen"
                     )
                  )
                  : class$net$rim$device$apps$internal$bis$ui$AccountSetupInstructionScreen
            )) {
            this._failedIntegrationType = "import_control";
         }
      }

      if (screenParams != null && screenParams.containsKey("suggestions")) {
         this._userNameEdit.setChangeListener(new UsernameSetupScreen$CustomUsernameEditFieldListener(this, null));
         this._userNameTakenLabel = (LabelField)(new Object());
         this.addContentField(this._userNameTakenLabel);
         String userName = (String)screenParams.get("userName");
         String userNameTakenText = MessageFormat.format(ApplicationResources.getString(36), new Object[]{userName});
         this._userNameTakenLabel.setText(userNameTakenText);
         RadioButtonGroup _suggestionRadioGroup = (RadioButtonGroup)(new Object());
         this._suggestionChoices = new Object[3];
         this._suggestionChoices[0] = (RadioButtonField)(new Object(null, _suggestionRadioGroup, true));
         this._suggestionChoices[1] = (RadioButtonField)(new Object(null, _suggestionRadioGroup, false));
         this._suggestionChoices[2] = (RadioButtonField)(new Object(null, _suggestionRadioGroup, false));
         this._customUserNameChoice = (RadioButtonField)(new Object(ApplicationResources.getString(65), _suggestionRadioGroup, false));
         this.addContentField(this._suggestionChoices[0]);
         this.addContentField(this._suggestionChoices[1]);
         this.addContentField(this._suggestionChoices[2]);
         this.addContentField(this._customUserNameChoice);
         this.addContentField(this._userNameEdit, true);
         String[] suggestions = (Object[])screenParams.get("suggestions");
         this._suggestionChoices[0].setLabel(suggestions[0]);
         this._suggestionChoices[1].setLabel(suggestions[1]);
         this._suggestionChoices[2].setLabel(suggestions[2]);
      } else {
         String introText = ApplicationResources.getString(232);
         this.addContentField((Field)(new Object(null, introText, introText.length(), 9007199254740992L)));
         LabelField toCreateLabel = (LabelField)(new Object(ApplicationResources.getString(233)));
         toCreateLabel.setMargin(10, 0, 10, 0);
         this.addContentField(toCreateLabel);
         this.addContentField(new BoldLabelField(ApplicationResources.getString(13)));
         this.addContentField(this._userNameEdit, true);
         this.addContentField(new InputHintLabelField(ApplicationResources.getString(20)));
      }

      this.addContentLineBreak();
      this._passwordEdit = (PasswordEditField)(new Object(null, null));
      this._passwordConfirmEdit = (PasswordEditField)(new Object(null, null));
      this.addContentField(new BoldLabelField(ApplicationResources.getString(14)));
      this.addContentField(this._passwordEdit, true);
      this.addContentField(new InputHintLabelField(ApplicationResources.getString(21)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(19)));
      this.addContentField(this._passwordConfirmEdit, true);
      Button nextButton = new Button(ApplicationResources.getString(17));
      String[] nextParams = null;
      if (this._failedIntegrationType != null) {
         Button closeButton = new Button(ApplicationResources.getString(15));
         Button backButton = new Button(ApplicationResources.getString(16));
         this.addButtonBarButtons(new Button[]{closeButton, backButton, nextButton}, false, 2);
         this.attachEventToField(closeButton, new CloseEvent());
         this.attachEventToField(backButton, new BackEvent());
         nextParams = new String[]{"userName", "password", "failedIntegrationType"};
      } else {
         Button cancelButton = new Button(ApplicationResources.getString(28));
         this.addButtonBarButtons(new Button[]{cancelButton, nextButton}, false, 1);
         this.attachEventToField(cancelButton, new BackEvent(28));
         nextParams = new String[]{"userName", "password"};
      }

      CommandEvent nextEvent = new CommandEvent(17, 32, nextParams);
      this.attachEventToField(nextButton, nextEvent);
      this.setDefaultEvent(nextEvent);
      this.setHelp("232698.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      String userName = null;
      if (this._suggestionChoices == null) {
         userName = this._userNameEdit.getText();
         if (!InputValidationUtils.validateUsername(this, userName)) {
            return false;
         }
      } else if (this._customUserNameChoice.isSelected()) {
         userName = this._userNameEdit.getText();
         if (!InputValidationUtils.validateUsername(this, userName)) {
            return false;
         }
      } else if (this._suggestionChoices[0].isSelected()) {
         userName = this._suggestionChoices[0].getLabel();
      } else if (this._suggestionChoices[1].isSelected()) {
         userName = this._suggestionChoices[1].getLabel();
      } else if (this._suggestionChoices[2].isSelected()) {
         userName = this._suggestionChoices[2].getLabel();
      }

      String password = this._passwordEdit.getText();
      if (!InputValidationUtils.validatePassword(this, password)) {
         return false;
      }

      String passwordConfirm = this._passwordConfirmEdit.getText();
      if (!password.equals(passwordConfirm)) {
         this.setError(ApplicationResources.getString(108));
         return false;
      }

      inputMap.put("userName", userName);
      inputMap.put("password", password);
      if (this._failedIntegrationType != null) {
         inputMap.put("failedIntegrationType", this._failedIntegrationType);
      }

      return true;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
