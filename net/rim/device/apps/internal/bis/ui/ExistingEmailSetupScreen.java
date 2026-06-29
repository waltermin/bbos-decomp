package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.InputHintLabelField;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.EventWrapper;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;

public final class ExistingEmailSetupScreen extends UserSettingsScreen {
   private EmailAddressEditField _emailAddressEdit;
   private PasswordEditField _passwordEdit;
   private CommandEvent _setupExistingMailEvent;
   private LinkEvent _servicesMainLinkEvent;
   private EventWrapper _nextEvent;
   private static final String PARAM_EMAIL = "email";
   private static final String PARAM_PASSWORD = "password";
   private static final String PARAM_REENTERED = "reentered";

   public ExistingEmailSetupScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(7));
      this._setupExistingMailEvent = new CommandEvent(17, 2, new String[]{"email", "password", "reentered"});
      this._servicesMainLinkEvent = new LinkEvent(17, 7);
      this._nextEvent = new EventWrapper(17, this._servicesMainLinkEvent);
      ExistingEmailSetupScreen$EmailInfoFieldListener emailInfoFieldListener = new ExistingEmailSetupScreen$EmailInfoFieldListener(this, null);
      this._emailAddressEdit = new EmailAddressEditField(null, null);
      this._emailAddressEdit.setChangeListener(emailInfoFieldListener);
      this._passwordEdit = new PasswordEditField(null, null);
      this._passwordEdit.setChangeListener(emailInfoFieldListener);
      this.addContentField(new LabelField(ApplicationResources.getString(75)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(136)));
      this.addContentField(this._emailAddressEdit, true);
      this._emailAddressEdit.setFocus();
      this.addContentField(new InputHintLabelField(ApplicationResources.getString(181)));
      this.addContentField(new InputHintLabelField(ApplicationResources.getString(219)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(14)));
      this.addContentField(this._passwordEdit, true);
      Button closeButton = new Button(ApplicationResources.getString(15));
      Button backButton = new Button(ApplicationResources.getString(16));
      Button nextButton = new Button(ApplicationResources.getString(17));
      this.addButtonBarButtons(new Button[]{closeButton, backButton, nextButton}, false, 2);
      this.attachEventToField(nextButton, this._nextEvent);
      this.setDefaultEvent(this._nextEvent);
      this.attachEventToField(closeButton, new CloseEvent());
      this.attachEventToField(backButton, new BackEvent());
      this.setHelp("index.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      String email = this._emailAddressEdit.getText();
      String password = this._passwordEdit.getText();
      if (!InputValidationUtils.isValidEmailAddress(email)) {
         this.setError(ApplicationResources.getString(110));
         return false;
      }

      if (password != null && password.length() != 0) {
         UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
         if (userInfo.getMailboxByEmail(email) != null) {
            this.setError(ApplicationResources.getString(184));
            return false;
         } else {
            inputMap.put("email", email);
            inputMap.put("password", password);
            inputMap.put("reentered", "false");
            return true;
         }
      } else {
         this.setError(ApplicationResources.getString(111));
         return false;
      }
   }
}
