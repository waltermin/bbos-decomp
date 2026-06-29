package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.internal.i18n.CommonResource;

public class UsernamePasswordDialog extends PopupDialog implements FieldChangeListener {
   private DialogFieldManager _dfm = (DialogFieldManager)this.getDelegate();
   private EditField _usernameField;
   private EditField _domainField;
   private PasswordEditField _passwordField;
   private ButtonField _okButton;
   private ButtonField _cancelButton;
   private CheckboxField _checkBoxField;
   private boolean _dialogCancelled;
   private boolean _isClosed;
   private String _username;
   private String _domain;
   private String _password;
   private boolean _remember;
   public static final int LDAP_MODE;
   public static final int BROWSER_MODE;
   public static final int JUST_USERNAME_AND_PASSWORD_MODE;

   public UsernamePasswordDialog(int mode) {
      this(null, mode);
   }

   public UsernamePasswordDialog(String prompt, int mode) {
      this(prompt, null, null, null, mode, 0);
   }

   public UsernamePasswordDialog(String prompt, String username, String domain, String password, int mode, long style) {
      super(new DialogFieldManager(), style);
      this._username = username;
      this._domain = domain;
      this._password = password;
      this.populateDialog(prompt, mode);
   }

   private void populateDialog(String prompt, int mode) {
      if (prompt != null && prompt.length() > 0) {
         RichTextField promptField = new RichTextField(prompt, 45035996273704960L);
         this._dfm.addCustomField(promptField);
      }

      if (mode == 1) {
         this._usernameField = new EditField(CommonResource.getString(10054), this._username == null ? "" : this._username);
      } else {
         this._usernameField = new EditField(CommonResource.getString(10026), this._username == null ? "" : this._username);
      }

      this._dfm.addCustomField(this._usernameField);
      if (mode == 2 && this._domain != null) {
         this._domainField = new EditField(CommonResource.getString(10032), this._domain);
         this._dfm.addCustomField(this._domainField);
      }

      this._passwordField = new PasswordEditField(CommonResource.getString(10027), this._password == null ? "" : this._password);
      this._dfm.addCustomField(this._passwordField);
      if (mode == 2) {
         this._checkBoxField = new CheckboxField(CommonResource.getString(10055), false, 1073741824);
         this._dfm.addCustomField(this._checkBoxField);
      }

      this._okButton = new ButtonField(CommonResource.getString(100));
      this._okButton.setChangeListener(this);
      this._cancelButton = new ButtonField(CommonResource.getString(19));
      this._cancelButton.setChangeListener(this);
      HorizontalFieldManager manager = new HorizontalFieldManager(12884901888L);
      manager.add(this._okButton);
      manager.add(this._cancelButton);
      this._dfm.addCustomField(manager);
      this.setCancelAllowed(true);
   }

   public void setAllowUnicodePassword(boolean allow) {
      this._passwordField.setAllowUnicodeInput(allow);
   }

   public boolean isUnicodePasswordAllowed() {
      return this._passwordField.isUnicodeInputAllowed();
   }

   private synchronized boolean accept() {
      if (!this._isClosed) {
         this._isClosed = true;
         this._username = this._usernameField.getText();
         this._domain = this._domainField == null ? null : this._domainField.getText();
         this._password = this._passwordField.getText();
         this._remember = this._checkBoxField == null ? false : this._checkBoxField.getChecked();
         this.close(0);
         return true;
      } else {
         return true;
      }
   }

   private synchronized boolean cancel() {
      if (!this.isCancelAllowed()) {
         return false;
      } else if (!this._isClosed) {
         this._isClosed = true;
         this._dialogCancelled = true;
         this.close(-1);
         return true;
      } else {
         return true;
      }
   }

   public String getUsername() {
      return this._dialogCancelled ? null : this._username;
   }

   public String getDomain() {
      return this._dialogCancelled ? null : this._domain;
   }

   public String getPassword() {
      return this._dialogCancelled ? null : this._password;
   }

   public boolean rememberCredentials() {
      return this._remember;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         this.accept();
      } else {
         if (field == this._cancelButton) {
            this.cancel();
         }
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         Field field = this.getDelegate().getLeafFieldWithFocus();
         if (field == this._cancelButton) {
            this.cancel();
            return true;
         }

         if (field == this._usernameField) {
            if (this._domainField != null) {
               this._domainField.setFocus();
            } else {
               this._passwordField.setFocus();
            }
         } else if (field == this._domainField) {
            this._passwordField.setFocus();
         } else if (field == this._passwordField || field == this._okButton) {
            this.accept();
         }
      } else if (key == 27) {
         this.cancel();
      }

      return super.keyChar(key, status, time);
   }
}
