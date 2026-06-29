package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.InputHintLabelField;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;

public final class AddExchangeEmail extends UserSettingsScreen {
   private BasicEditField _owaURLEdit;
   private BasicEditField _userNameEdit;
   private PasswordEditField _passwordEdit;
   private EmailAddressEditField _emailAddressEdit;
   private BasicEditField _mailboxNameEdit;
   private static final String PARAM_EMAIL;
   private static final String PARAM_SERVER;
   private static final String PARAM_DESCRIPTION;
   private static final String PARAM_OWA_USERNAME;
   private static final String PARAM_OWA_PASSWORD;

   public AddExchangeEmail() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(50));
      this.addContentField(new BoldLabelField(ApplicationResources.getString(41)));
      String sessionSimpleEmail = ClientSessionState.getInstance().getIntegrationEmail();
      String acctConfigDescriptionText = MessageFormat.format(ApplicationResources.getString(42), new Object[]{sessionSimpleEmail});
      this.addContentField((Field)(new Object(acctConfigDescriptionText)));
      this.addContentField(new BoldLabelField(ApplicationResources.getString(51)));
      this._owaURLEdit = (BasicEditField)(new Object());
      this.addContentField(this._owaURLEdit, true);
      this.addContentField(new InputHintLabelField(ApplicationResources.getString(53)));
      this.addContentField(new BoldLabelField(ApplicationResources.getString(13)));
      this._userNameEdit = (BasicEditField)(new Object());
      this.addContentField(this._userNameEdit, true);
      this.addContentField(new InputHintLabelField(ApplicationResources.getString(54)));
      this.addContentField(new BoldLabelField(ApplicationResources.getString(14)));
      this._passwordEdit = (PasswordEditField)(new Object());
      this.addContentField(this._passwordEdit, true);
      this.addContentField(new InputHintLabelField(ApplicationResources.getString(55)));
      this.addContentField(new BoldLabelField(ApplicationResources.getString(43)));
      this._emailAddressEdit = (EmailAddressEditField)(new Object(null, null));
      this.addContentField(this._emailAddressEdit, true);
      this.addContentField(new BoldLabelField(ApplicationResources.getString(52)));
      this._mailboxNameEdit = (BasicEditField)(new Object());
      this.addContentField(this._mailboxNameEdit, true);
      Button closeButton = new Button(ApplicationResources.getString(15));
      Button backButton = new Button(ApplicationResources.getString(16));
      Button nextButton = new Button(ApplicationResources.getString(17));
      this.addButtonBarButtons(new Button[]{closeButton, backButton, nextButton}, false, 2);
      CommandEvent nextEvent = new CommandEvent(17, 4, new String[]{"email", "server", "description", "userName", "password"});
      this.attachEventToField(nextButton, nextEvent);
      this.setDefaultEvent(nextEvent);
      this.attachEventToField(closeButton, new CloseEvent());
      this.attachEventToField(backButton, new BackEvent());
      this.setHelp("98535.wml");
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      String email = this._emailAddressEdit.getText();
      String server = this._owaURLEdit.getText();
      String userName = this._userNameEdit.getText();
      String password = this._passwordEdit.getText();
      if (!InputValidationUtils.isValidEmailAddress(email)) {
         this.setError(ApplicationResources.getString(110));
         return false;
      }

      if (password == null || password.length() == 0) {
         this.setError(ApplicationResources.getString(111));
         return false;
      }

      if (userName != null && userName.length() != 0) {
         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            new Object(server);
            var8 = false;
         } finally {
            if (var8) {
               this.setError(ApplicationResources.getString(187));
               return false;
            }
         }

         UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
         if (userInfo.getMailboxByEmail(email) != null) {
            this.setError(ApplicationResources.getString(184));
            return false;
         } else {
            inputMap.put("email", email);
            inputMap.put("server", server);
            inputMap.put("description", this._mailboxNameEdit.getText());
            inputMap.put("userName", userName);
            inputMap.put("password", password);
            return true;
         }
      } else {
         this.setError(ApplicationResources.getString(97));
         return false;
      }
   }
}
