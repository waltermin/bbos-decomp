package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.wica.runtime.resources.RuntimeResources;

public final class AuthenticationDialog extends PopupDialog implements FieldChangeListener {
   private DialogFieldManager _dfm = (DialogFieldManager)this.getDelegate();
   private EditField _usernameField;
   private EditField _domainField;
   private PasswordEditField _passwordField;
   private BitmapButton _showDetailsButton;
   private ButtonField _okButton;
   private ButtonField _cancelButton;
   private CheckboxField _checkBoxField;
   private RichTextField _serviceField;
   private boolean _dialogCancelled;
   private boolean _isClosed;
   private String _username;
   private String _domain;
   private String _password;
   private boolean _remember;
   private boolean _isShowingDetails;
   private String _serviceMessage;
   private String _detailedServiceMessage;
   private int[] _offsets;
   private byte[] _attributes;
   private Font[] _fonts;
   public static final int BASIC_MODE = 1;
   public static final int NTLM_MODE = 2;

   public AuthenticationDialog(
      String username, String domain, String password, String serviceName, String serviceDetails, boolean isProxy, int mode, long style
   ) {
      super((Manager)(new Object()), style);
      this._username = username;
      this._domain = domain;
      this._password = password;
      this.populateDialog(serviceName, serviceDetails, isProxy, mode);
   }

   private final void populateDialog(String serviceName, String serviceDetails, boolean isProxy, int mode) {
      RichTextField promptField = (RichTextField)(new Object(RuntimeResources.getString(86), 45035996273704960L));
      this._dfm.setMessage(promptField);
      this._serviceMessage = !isProxy ? serviceName : RuntimeResources.getString(88, serviceName);
      if (serviceDetails != null) {
         this._showDetailsButton = new BitmapButton(RuntimeResources.getBitmapResource("i.png"), RuntimeResources.getBitmapResource("focusi.png"));
         this._showDetailsButton.setChangeListener(this);
         this._dfm.setIcon(this._showDetailsButton);
      } else {
         EncodedImage image = ThemeManager.getActiveTheme().getImage("lock_screen_icon");
         if (image != null) {
            BitmapField imageField = (BitmapField)(new Object());
            imageField.setImage(image);
            this._dfm.setIcon(imageField);
         }
      }

      this._serviceField = (RichTextField)(new Object(this._serviceMessage, 45036047880421376L));
      this._dfm.addCustomField(this._serviceField);
      if (serviceDetails != null) {
         this._detailedServiceMessage = ((StringBuffer)(new Object())).append(this._serviceMessage).append('\n').append(serviceDetails).toString();
         this._offsets = new int[]{0, this._serviceMessage.length(), this._detailedServiceMessage.length()};
         Font currentFont = this._serviceField.getFont();
         this._fonts = new Object[]{currentFont, currentFont.derive(currentFont.getStyle(), (int)(currentFont.getHeight() * 4604480259023595110L))};
         this._attributes = new byte[]{0, 1};
      }

      int verticalIndent = this.getFont().getHeight() / 3;
      this._usernameField = (EditField)(new Object(CommonResource.getString(10026), this._username == null ? "" : this._username));
      this._usernameField.setMargin(verticalIndent, 0, 0, 0);
      this._dfm.addCustomField(this._usernameField);
      if (mode == 2 || this._domain != null) {
         this._domainField = (EditField)(new Object(CommonResource.getString(10032), this._domain));
         this._dfm.addCustomField(this._domainField);
      }

      this._passwordField = (PasswordEditField)(new Object(CommonResource.getString(10027), this._password == null ? "" : this._password));
      this._dfm.addCustomField(this._passwordField);
      this._checkBoxField = (CheckboxField)(new Object(RuntimeResources.getString(87), false, 1073741824));
      this._checkBoxField.setMargin(verticalIndent, 0, 0, 0);
      this._dfm.addCustomField(this._checkBoxField);
      this._okButton = (ButtonField)(new Object(CommonResource.getString(100)));
      this._okButton.setChangeListener(this);
      this._cancelButton = (ButtonField)(new Object(CommonResource.getString(19)));
      this._cancelButton.setChangeListener(this);
      HorizontalFieldManager manager = (HorizontalFieldManager)(new Object(12884901888L));
      manager.add(this._okButton);
      manager.add(this._cancelButton);
      this._dfm.addCustomField(manager);
      this.setCancelAllowed(true);
      this._usernameField.setFocus();
   }

   private final void showhideDetails() {
      this._isShowingDetails = !this._isShowingDetails;
      if (this._isShowingDetails) {
         this._serviceField.setText(this._detailedServiceMessage, this._offsets, this._attributes, this._fonts);
      } else {
         this._serviceField.setText(this._serviceMessage);
      }
   }

   private final synchronized boolean accept() {
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

   private final synchronized boolean cancel() {
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

   public final String getUsername() {
      return this._dialogCancelled ? null : this._username;
   }

   public final String getDomain() {
      return this._dialogCancelled ? null : this._domain;
   }

   public final String getPassword() {
      return this._dialogCancelled ? null : this._password;
   }

   public final boolean rememberCredentials() {
      return this._remember;
   }

   public final boolean cancelled() {
      return this._dialogCancelled;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         this.accept();
      } else if (field == this._cancelButton) {
         this.cancel();
      } else {
         if (field == this._showDetailsButton && context == 0) {
            this.showhideDetails();
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         Field field = this.getDelegate().getLeafFieldWithFocus();
         if (field == this._cancelButton) {
            this.cancel();
            return true;
         }

         if (field == this._usernameField) {
            if (this._domainField != null) {
               this._domainField.setFocus();
               return true;
            }

            this._passwordField.setFocus();
            return true;
         }

         if (field == this._domainField) {
            this._passwordField.setFocus();
            return true;
         }

         if (field == this._passwordField || field == this._okButton) {
            this.accept();
            return true;
         }
      } else if (key == 27) {
         this.cancel();
         return true;
      }

      return super.keyChar(key, status, time);
   }
}
