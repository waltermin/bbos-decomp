package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.ClientPersistentState;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.Event;
import net.rim.device.apps.internal.bis.api.ui.FormattedTextField;
import net.rim.device.apps.internal.bis.api.ui.HeadingField;
import net.rim.device.apps.internal.bis.api.ui.LinkField;
import net.rim.device.apps.internal.bis.api.ui.ShutdownListener;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;
import net.rim.device.apps.internal.bis.utils.PasswordTextFilter;

public final class LoginScreen extends BasicScreen implements ShutdownListener {
   private BasicEditField _userNameEdit;
   private PasswordEditField _passwordEdit;
   private CheckboxField _rememberCredentialsCheckbox;
   private Event _createAccountEvent;
   private Event _loginEvent;
   private static final String PARAM_USERNAME = "userName";
   private static final String PARAM_PASSWORD = "password";
   private static final String PARAM_REMEMBER_CREDENTIALS = "rememberCredentials";

   protected final boolean isEmpty(TextField textField) {
      return textField == null || textField.getText() == null || textField.getText().length() <= 0;
   }

   protected final boolean areCredentialsFilledIn() {
      return !this.isEmpty(this._userNameEdit) && !this.isEmpty(this._passwordEdit);
   }

   @Override
   public final void shutdown() {
      if (this._passwordEdit != null) {
         this._passwordEdit.setFilter(null);
      }
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(10));
      String storedUserName = ClientPersistentState.getInstance().getUserName();
      String storedPassword = ClientPersistentState.getInstance().getPassword();
      this._userNameEdit = new BasicEditField(null, storedUserName, 32, 0);
      this._passwordEdit = new PasswordEditField(null, storedPassword, 16, 0);
      this._passwordEdit.setFilter(new PasswordTextFilter());
      this._rememberCredentialsCheckbox = new CheckboxField(ApplicationResources.getString(80), true);
      if (ClientSessionState.getInstance().getBrandingInfo().isSelfCreateEnabled()) {
         this.addContentField(new HeadingField(ApplicationResources.getString(81)));
         this.addContentField(new FormattedTextField(ApplicationResources.getString(84)));
         Button promptAccountSetupButton = new Button(ApplicationResources.getString(82));
         this.addContentField(promptAccountSetupButton);
         this._createAccountEvent = new LinkEvent(82, 8);
         this.attachEventToField(promptAccountSetupButton, this._createAccountEvent);
         this.addContentField(new SeparatorField());
      }

      this.addContentField(new HeadingField(ApplicationResources.getString(83)));
      this.addContentField(new LabelField(ApplicationResources.getString(85)));
      this.addContentField(new BoldLabelField(ApplicationResources.getString(13)));
      this.addContentField(this._userNameEdit, true);
      BoldLabelField passwordLabel = new BoldLabelField(ApplicationResources.getString(14) + " ");
      LinkField forgotPasswordLinkField = new LinkField(ApplicationResources.getString(79));
      this.addContentFieldRow(new Field[]{passwordLabel, forgotPasswordLinkField});
      this.attachEventToField(forgotPasswordLinkField, new LinkEvent(8, 2));
      this.addContentField(this._passwordEdit, true);
      this.addContentField(this._rememberCredentialsCheckbox);
      Button closeButton = new Button(ApplicationResources.getString(15));
      Button loginButton = new Button(ApplicationResources.getString(78));
      this.addButtonBarButtons(new Button[]{closeButton, loginButton}, false, 1);
      this._loginEvent = new CommandEvent(78, 1, new String[]{"userName", "password", "rememberCredentials"});
      this.attachEventToField(loginButton, this._loginEvent);
      this.attachEventToField(closeButton, new CloseEvent());
   }

   @Override
   protected final MenuItem selectDefaultMenuItem(Menu menu) {
      Event defaultEvent;
      if (this.areCredentialsFilledIn()) {
         defaultEvent = this._loginEvent;
      } else {
         defaultEvent = this._createAccountEvent;
      }

      return this.findMenuItemForEvent(menu, defaultEvent);
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      String userName = this._userNameEdit.getText() != null ? this._userNameEdit.getText().trim() : null;
      if (!InputValidationUtils.isValidUsername(userName)) {
         this.setError(ApplicationResources.getString(97));
         return false;
      } else if (!InputValidationUtils.isValidPassword(this._passwordEdit.getText())) {
         this.setError(ApplicationResources.getString(107));
         return false;
      } else {
         inputMap.put("userName", userName);
         inputMap.put("password", this._passwordEdit.getText());
         inputMap.put("rememberCredentials", this._rememberCredentialsCheckbox.getChecked() ? "true" : "false");
         return true;
      }
   }
}
