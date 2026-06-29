package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.EventWrapper;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;

public final class AdditionalInformationRequiredScreen extends UserSettingsScreen {
   private EmailAddressEditField _emailAddressEdit;
   private PasswordEditField _passwordEdit;
   private RadioButtonGroup _defaultGroup;
   private RadioButtonField _emailAndPasswordField;
   private RadioButtonField _customSettingField;
   private CommandEvent _setupExistingMailEvent;
   private LinkEvent _selectAccountLinkEvent;
   private EventWrapper _nextEvent;
   private static final String PARAM_EMAIL;
   private static final String PARAM_PASSWORD;
   private static final String PARAM_REENTERED;

   public AdditionalInformationRequiredScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(47));
      this._setupExistingMailEvent = new CommandEvent(17, 2, new String[]{"email", "password", "reentered"});
      this._selectAccountLinkEvent = new LinkEvent(17, 16);
      this._nextEvent = new EventWrapper(17, this._setupExistingMailEvent);
      ClientSessionState sessionState = ClientSessionState.getInstance();
      String sessionSimpleEmail = sessionState.getIntegrationEmail();
      String sessionSimplePassword = sessionState.getIntegrationPassword();
      this._emailAddressEdit = (EmailAddressEditField)(new Object(null, sessionSimpleEmail));
      this._passwordEdit = (PasswordEditField)(new Object(null, sessionSimplePassword));
      this._defaultGroup = (RadioButtonGroup)(new Object());
      this._emailAndPasswordField = (RadioButtonField)(new Object(ApplicationResources.getString(66), this._defaultGroup, true));
      this._customSettingField = (RadioButtonField)(new Object(ApplicationResources.getString(67), this._defaultGroup, false));
      this._defaultGroup.setChangeListener(new AdditionalInformationRequiredScreen$EmailTypeChoiceFieldListener(this, null));
      String unableToSetupText = MessageFormat.format(ApplicationResources.getString(68), new Object[]{sessionSimpleEmail});
      this.addContentField((Field)(new Object(unableToSetupText)));
      this.addContentLineBreak();
      this.addContentField(this._emailAndPasswordField);
      this.addContentField(new BoldLabelField(ApplicationResources.getString(136)));
      this.addContentField(this._emailAddressEdit, true);
      this._emailAddressEdit.setFocus();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(14)));
      this.addContentField(this._passwordEdit, true);
      this.addContentLineBreak();
      this.addContentField(this._customSettingField);
      Button closeButton = new Button(ApplicationResources.getString(15));
      Button backButton = new Button(ApplicationResources.getString(16));
      Button nextButton = new Button(ApplicationResources.getString(17));
      this.addButtonBarButtons(new Button[]{closeButton, backButton, nextButton}, false, 2);
      this.attachEventToField(nextButton, this._nextEvent);
      this.setDefaultEvent(this._nextEvent);
      this.attachEventToField(closeButton, new CloseEvent());
      this.attachEventToField(backButton, new BackEvent());
      this.setHelp("221941.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      String email = this._emailAddressEdit.getText();
      String password = this._passwordEdit.getText();
      if (!InputValidationUtils.isValidEmailAddress(email)) {
         this.setError(ApplicationResources.getString(110));
         return false;
      } else if (password != null && password.length() != 0) {
         inputMap.put("email", email);
         inputMap.put("password", password);
         inputMap.put("reentered", "true");
         return true;
      } else {
         this.setError(ApplicationResources.getString(111));
         return false;
      }
   }
}
