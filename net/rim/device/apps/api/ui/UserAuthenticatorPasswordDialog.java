package net.rim.device.apps.api.ui;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.system.USBPasswordRedirectManager;
import net.rim.device.internal.system.USBPortInternal$Internal;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.container.FrameLayout;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.vm.Message;

class UserAuthenticatorPasswordDialog extends PopupDialog implements FieldChangeListener {
   private BasicEditField _devicePasswordField;
   private HorizontalFieldManager _deviceHFM;
   private boolean _numericDevicePassword;
   private boolean _revealDevicePassword;
   private BasicEditField _authenticatorPasswordField;
   private HorizontalFieldManager _authenticatorHFM;
   private boolean _numericAuthenticatorPassword;
   private boolean _revealAuthenticatorPassword;
   private VerticalFieldManager _statusFieldVFM;
   private int _statusFieldHeight = 0;
   private boolean _passwordEntryState;
   private boolean _deviceIsUnlocked;
   private char _initialCharacter;
   private VerticalFieldManager _nonScrollingRegion;
   private UserAuthenticatorPasswordDialog$HeightRestrictedVerticalFieldManager _scrollingRegion;
   private boolean _showStatusField;
   private boolean _showAlphaNumericIndicator;
   private boolean _numericEntryMode;
   private boolean _isPendingUSBConnection;
   private Vector _uSBChoices;
   private Vector _uSBChannels;
   private USBPasswordRedirectManager _redirectManager;
   private UserAuthenticatorPasswordDialog$ClearPasswordOnIdleThread _clearPasswordOnIdleThread;
   private static ResourceBundle _rb = ResourceBundle.getBundle(-1488627819050031640L, "net.rim.device.apps.internal.resource.Security");

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public int[] getSelectedChannels() {
      if (this._uSBChannels == null) {
         return new int[0];
      }

      Vector selectedChannels = (Vector)(new Object());

      for (int i = 0; i < this._uSBChannels.size(); i++) {
         if (((CheckboxField)this._uSBChoices.elementAt(i)).getChecked()) {
            selectedChannels.addElement(this._uSBChannels.elementAt(i));
         } else {
            this._redirectManager.allowChannel(this._uSBChannels.elementAt(i), false);
            boolean var5 = false /* VF: Semaphore variable */;

            try {
               var5 = true;
               this._redirectManager.addToDisallowedChannels(USBPortInternal$Internal.getChannelName(this._uSBChannels.elementAt(i)));
               var5 = false;
            } finally {
               if (var5) {
                  USBPasswordRedirectManager.logEvent(1195593285);
                  continue;
               }
            }
         }
      }

      int[] selectedChannelsInt = new int[selectedChannels.size()];

      for (int i = 0; i < selectedChannels.size(); i++) {
         selectedChannelsInt[i] = selectedChannels.elementAt(i);
      }

      return selectedChannelsInt;
   }

   public BasicEditField getTextPictureField(String as, Bitmap bitmap) {
      if (as == null) {
         return null;
      }

      BasicEditField editField = (BasicEditField)(new Object(45035996273704960L));
      int length = as.length();

      for (int i = 0; i < length; i++) {
         char ch = as.charAt(i);
         if (ch == '￼') {
            editField.insert(new UserAuthenticatorPasswordDialog$BitmapChar(bitmap));
         } else {
            editField.insert(String.valueOf(ch));
         }
      }

      return editField;
   }

   public void outOfHolster() {
   }

   public String getDevicePassword() {
      return this._devicePasswordField.getText();
   }

   public String getAuthenticatorPassword() {
      return this._authenticatorPasswordField.getText();
   }

   public void setInitialPasswordString(String initialPasswordString) {
      if (initialPasswordString != null) {
         if (InputContext.getInstance(false).isSureType()) {
            this._initialCharacter = initialPasswordString.charAt(0);
         } else {
            label25:
            try {
               this._devicePasswordField.setText(initialPasswordString);
               this._devicePasswordField.setCursorPosition(initialPasswordString.length());
            } finally {
               break label25;
            }
         }

         this.passwordEntryStateChanged();
         this.startClearPasswordThreadIfNecessary();
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._devicePasswordField) {
         if (!this._authenticatorPasswordField.isDirty()) {
            try {
               this._authenticatorPasswordField.setText(this._devicePasswordField.getText());
               return;
            } finally {
               return;
            }
         }

         this._devicePasswordField.setChangeListener(null);
      }
   }

   private void updateStatusField() {
      if (this._numericEntryMode) {
         if (this._isPendingUSBConnection && this.getLeafFieldWithFocus() instanceof Object) {
            this._showStatusField = false;
            this._showAlphaNumericIndicator = false;
            this._statusFieldVFM.deleteAll();
            this._statusFieldVFM.add((Field)(new Object(this._statusFieldHeight)));
         } else {
            BasicEditField fieldWithFocus = (BasicEditField)this.getLeafFieldWithFocus();
            boolean showStatusField = false;
            boolean showAlphaNumericIndicator = false;
            if (fieldWithFocus == this._authenticatorPasswordField) {
               showAlphaNumericIndicator = this._numericAuthenticatorPassword;
               showStatusField = this._authenticatorPasswordField.getText().length() == 0;
            } else if (fieldWithFocus == this._devicePasswordField) {
               showAlphaNumericIndicator = this._numericDevicePassword;
               showStatusField = this._devicePasswordField.getText().length() == 0;
            }

            if (showStatusField != this._showStatusField || showAlphaNumericIndicator != this._showAlphaNumericIndicator) {
               this._showStatusField = showStatusField;
               this._showAlphaNumericIndicator = showAlphaNumericIndicator;
               this._statusFieldVFM.deleteAll();
               if (showStatusField) {
                  Bitmap bitmap;
                  if (showAlphaNumericIndicator) {
                     bitmap = Bitmap.getBitmapResource("alphanumericinput.gif");
                  } else {
                     bitmap = Bitmap.getBitmapResource("numericinput.gif");
                  }

                  BasicEditField editField = this.getTextPictureField(_rb.getString(753), bitmap);
                  this._statusFieldVFM.add(editField);
                  this._statusFieldHeight = this._statusFieldVFM.getHeight();
               } else {
                  this._statusFieldVFM.add((Field)(new Object(this._statusFieldHeight)));
               }
            }
         }
      }
   }

   private void resetPasswordField(HorizontalFieldManager hfm, FrameLayout passwordFrame, boolean numericPassword) {
      hfm.deleteAll();
      Bitmap bitmap;
      if (numericPassword) {
         bitmap = Bitmap.getBitmapResource("numericinput.gif");
      } else {
         bitmap = Bitmap.getBitmapResource("alphanumericinput.gif");
      }

      BitmapField bitmapField = (BitmapField)(new Object(bitmap, 51539607552L));
      UserAuthenticatorPasswordDialog$WidthRestrictedHorizontalFieldManager wrHFM = new UserAuthenticatorPasswordDialog$WidthRestrictedHorizontalFieldManager(
         bitmapField.getPreferredWidth()
      );
      wrHFM.add(passwordFrame);
      hfm.add(wrHFM);
      hfm.add(bitmapField);
   }

   @Override
   protected boolean trackwheelRoll(int amount, int status, int time) {
      return !this.navigationMovement(amount, status) ? super.trackwheelRoll(amount, status, time) : true;
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      return !this.navigationMovement(dy, status) ? super.navigationMovement(dx, dy, status, time) : true;
   }

   private boolean navigationMovement(int dy, int status) {
      if ((status & 257) == 0) {
         if (this._isPendingUSBConnection && this.getLeafFieldWithFocus() instanceof Object) {
            CheckboxField checkboxField = (CheckboxField)this.getLeafFieldWithFocus();
            if (dy > 0 && this._uSBChoices.indexOf(checkboxField) == this._uSBChoices.size() - 1) {
               this.setFocus(this._devicePasswordField);
               return true;
            }

            this.updateStatusField();
            return false;
         }

         BasicEditField fieldWithFocus = (BasicEditField)this.getLeafFieldWithFocus();
         if (dy < 0 && fieldWithFocus == this._authenticatorPasswordField) {
            this._authenticatorPasswordField.select(false);
            this.setFocus(this._devicePasswordField);
            return true;
         }

         if (dy > 0 && fieldWithFocus == this._devicePasswordField) {
            this._devicePasswordField.select(false);
            this.setFocus(this._authenticatorPasswordField);
            return true;
         }

         if (dy < 0 && fieldWithFocus == this._devicePasswordField && this._uSBChoices != null) {
            this._devicePasswordField.select(false);
            this._showStatusField = false;
            this._showAlphaNumericIndicator = false;
            this._statusFieldVFM.deleteAll();
            this._statusFieldVFM.add((Field)(new Object(this._statusFieldHeight)));
         }
      }

      return false;
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this.updateStatusField();
      } else {
         if (this._clearPasswordOnIdleThread != null) {
            this._clearPasswordOnIdleThread.stopThread();
         }
      }
   }

   private void startClearPasswordThreadIfNecessary() {
      if ((this._devicePasswordField.getText().length() > 0 || this._authenticatorPasswordField.getText().length() > 0)
         && this._clearPasswordOnIdleThread == null) {
         this._clearPasswordOnIdleThread = new UserAuthenticatorPasswordDialog$ClearPasswordOnIdleThread(this, Application.getApplication());
         this._clearPasswordOnIdleThread.start();
      }
   }

   private FrameLayout createAuthenticatorPasswordField() {
      if (this._revealAuthenticatorPassword) {
         this._authenticatorPasswordField = (BasicEditField)(new Object(
            null, null, 1000000, (this._numericAuthenticatorPassword ? 16777216 : 1073741824) | 2147483648L
         ));
      } else {
         this._authenticatorPasswordField = (BasicEditField)(new Object(null, null, 1000000, this._numericAuthenticatorPassword ? 16777216 : 0));
      }

      FrameLayout layout = (FrameLayout)(new Object(1));
      layout.add(this._authenticatorPasswordField);
      return layout;
   }

   private void setFocus(BasicEditField field) {
      field.setFocus();
      int textLength = field.getTextLength();
      if (textLength > 0) {
         field.setCursorPosition(0);
         field.select(true);
         field.setCursorPosition(textLength);
         this.invalidate();
      }

      this.updateStatusField();
   }

   private FrameLayout createDevicePasswordField() {
      if (this._revealDevicePassword) {
         this._devicePasswordField = (BasicEditField)(new Object(null, null, 32, (this._numericDevicePassword ? 16777216 : 1073741824) | 2147483648L));
      } else {
         this._devicePasswordField = (BasicEditField)(new Object(null, null, 32, this._numericDevicePassword ? 16777216 : 0));
      }

      FrameLayout layout = (FrameLayout)(new Object(1));
      layout.add(this._devicePasswordField);
      return layout;
   }

   private void resetAuthenticatorPasswordField(boolean focusOnAuthenticatorPasswordField) {
      this.resetPasswordField(this._authenticatorHFM, this.createAuthenticatorPasswordField(), this._numericAuthenticatorPassword);
      if (focusOnAuthenticatorPasswordField) {
         this._authenticatorPasswordField.setFocus();
      }
   }

   private void resetDevicePasswordField(boolean focusOnDevicePasswordField) {
      this.resetPasswordField(this._deviceHFM, this.createDevicePasswordField(), this._numericDevicePassword);
      if (focusOnDevicePasswordField) {
         this._devicePasswordField.setFocus();
      }
   }

   @Override
   protected void onFocusNotify(boolean focus) {
      super.onFocusNotify(focus);
      if (focus && this._initialCharacter != 0) {
         int initialModifier = 0;
         if (CharacterUtilities.isUpperCase(this._initialCharacter)) {
            initialModifier = 1;
         } else if (Keypad.getLayout().getUnaltedChar(this._initialCharacter) != 0) {
            initialModifier = 8;
         }

         UiApplication.getUiApplication()
            .publicProcessMessage(
               (Message)(new Object(
                  2,
                  513,
                  this._initialCharacter,
                  Keypad.keycode(
                     (char)Keypad.getLayout().getOriginalKeyCode(this._initialCharacter, initialModifier),
                     Keypad.status(SLKeyLayout.convertModifiersToStatus(initialModifier))
                  ),
                  (int)System.currentTimeMillis()
               ))
            );
         this._initialCharacter = 0;
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      if (super.trackwheelClick(status, time)) {
         return true;
      }

      if (this._isPendingUSBConnection && this.getLeafFieldWithFocus() instanceof Object) {
         if (((CheckboxField)this.getLeafFieldWithFocus()).getChecked()) {
            ((CheckboxField)this.getLeafFieldWithFocus()).setChecked(false);
            return true;
         }

         ((CheckboxField)this.getLeafFieldWithFocus()).setChecked(true);
      } else if (this._devicePasswordField.getTextLength() > 0 && this._authenticatorPasswordField.getTextLength() > 0) {
         this.close(0);
         return true;
      }

      return true;
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      return super.stylusTap(x, y, status, time) ? true : this.trackwheelClick(status, time);
   }

   private void passwordEntryStateChanged() {
      if (!this._deviceIsUnlocked) {
         boolean passwordEntryState;
         if (this._devicePasswordField.getText().length() <= 0 && this._authenticatorPasswordField.getText().length() <= 0) {
            passwordEntryState = false;
         } else {
            passwordEntryState = true;
         }

         if (passwordEntryState != this._passwordEntryState) {
            this._passwordEntryState = passwordEntryState;
            RIMGlobalMessagePoster.postGlobalEvent(306123729322610706L, passwordEntryState ? 1 : 0, 0);
         }
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      boolean result = super.keyDown(keycode, time);
      if (InternalServices.isReducedFormFactor()) {
         this.passwordEntryStateChanged();
         this.startClearPasswordThreadIfNecessary();
      }

      return result;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean result = super.keyChar(key, status, time);
      if (!InternalServices.isReducedFormFactor()) {
         this.passwordEntryStateChanged();
         this.startClearPasswordThreadIfNecessary();
      }

      this.updateStatusField();
      if (result) {
         return true;
      }

      if (key == 27) {
         this._devicePasswordField.setText("");
         this._authenticatorPasswordField.setText("");
         this.passwordEntryStateChanged();
         this.close(-1);
         return true;
      }

      if (key != '\n') {
         return false;
      }

      if (this._devicePasswordField.getTextLength() > 0 && this._authenticatorPasswordField.getTextLength() > 0) {
         this.close(0);
         return true;
      }

      if (this._numericEntryMode && this.getLeafFieldWithFocus() == this._devicePasswordField && this._devicePasswordField.getTextLength() == 0) {
         this._numericDevicePassword = !this._numericDevicePassword;
         this.resetDevicePasswordField(true);
         this.updateStatusField();
         return true;
      }

      if (this.getLeafFieldWithFocus() == this._devicePasswordField) {
         this._authenticatorPasswordField.setFocus();
         this.updateStatusField();
         return true;
      }

      if (this._numericEntryMode && this.getLeafFieldWithFocus() == this._authenticatorPasswordField && this._authenticatorPasswordField.getTextLength() == 0) {
         this._numericAuthenticatorPassword = !this._numericAuthenticatorPassword;
         this.resetAuthenticatorPasswordField(true);
         this.updateStatusField();
      }

      return true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public UserAuthenticatorPasswordDialog(
      String devicePasswordLabel,
      boolean revealDevicePassword,
      String authenticatorPasswordLabel,
      boolean revealAuthenticatorPassword,
      boolean autoFillAuthenticator,
      boolean isPendingUSBConnection
   ) {
      super((Manager)(new Object(1152921504606846976L)));
      this._revealDevicePassword = revealDevicePassword;
      this._revealAuthenticatorPassword = revealAuthenticatorPassword;
      Security security = Security.getInstance();
      boolean numericHandheldPasswordEntryMode = security.isSmartPasswordEntryEnabledOnHandheldPassword();
      boolean numericAuthenticatorPasswordEntryMode = security.isSmartPasswordEntryEnabledOnUserAuthenticatorPassword();
      this._numericEntryMode = numericHandheldPasswordEntryMode || numericAuthenticatorPasswordEntryMode;
      if (this._numericEntryMode) {
         VerticalFieldManager tempVfm = (VerticalFieldManager)(new Object(562949953421312L));
         tempVfm.add((Field)(new Object()));
         tempVfm.add(this.getTextPictureField(_rb.getString(753), Bitmap.getBitmapResource("alphanumericinput.gif")));
         this._statusFieldHeight = tempVfm.getPreferredHeight();
         this._nonScrollingRegion = (VerticalFieldManager)(new Object(1152921504606846976L));
      }

      this._scrollingRegion = new UserAuthenticatorPasswordDialog$HeightRestrictedVerticalFieldManager(1153220571769602048L, this._statusFieldHeight);
      Manager manager = this.getDelegate();
      manager.add(this._scrollingRegion);
      if (this._numericEntryMode) {
         manager.add(this._nonScrollingRegion);
      }

      this._isPendingUSBConnection = isPendingUSBConnection;
      if (this._isPendingUSBConnection) {
         this._scrollingRegion.add((Field)(new Object(CommonResource.getString(10180), 36028797018963968L)));
         this._redirectManager = USBPasswordRedirectManager.getInstance();
         this._uSBChannels = this._redirectManager.getAllChannels();
         this._uSBChoices = (Vector)(new Object());
         boolean var15 = false /* VF: Semaphore variable */;

         label91:
         try {
            var15 = true;

            for (int layout = 0; layout < this._uSBChannels.size(); layout++) {
               String usbPeripheralName = USBPortInternal$Internal.getChannelName(this._uSBChannels.elementAt(layout));
               if (usbPeripheralName.equals("RIM Bypass")) {
                  usbPeripheralName = CommonResource.getString(10183);
               } else if (usbPeripheralName.equals("RIM Desktop")) {
                  usbPeripheralName = CommonResource.getString(10181);
               }

               CheckboxField checkboxField = (CheckboxField)(new Object(usbPeripheralName, true));
               this._scrollingRegion.add(checkboxField);
               this._uSBChoices.addElement(checkboxField);
            }

            var15 = false;
         } finally {
            if (var15) {
               USBPasswordRedirectManager.logEvent(1195593285);
               break label91;
            }
         }
      }

      this._scrollingRegion.add((Field)(new Object(devicePasswordLabel, 36028797018963968L)));
      if (this._numericEntryMode) {
         this._deviceHFM = (HorizontalFieldManager)(new Object());
         this._scrollingRegion.add(this._deviceHFM);
         this._numericDevicePassword = numericHandheldPasswordEntryMode;
         this.resetDevicePasswordField(false);
      } else {
         FrameLayout layout = this.createDevicePasswordField();
         this._scrollingRegion.add(layout);
      }

      this._scrollingRegion.add((Field)(new Object(authenticatorPasswordLabel, 36028797018963968L)));
      if (autoFillAuthenticator) {
         this._devicePasswordField.setChangeListener(this);
      }

      if (this._numericEntryMode) {
         this._authenticatorHFM = (HorizontalFieldManager)(new Object());
         this._scrollingRegion.add(this._authenticatorHFM);
         this._numericAuthenticatorPassword = numericAuthenticatorPasswordEntryMode;
         this.resetAuthenticatorPasswordField(false);
         this._nonScrollingRegion.add((Field)(new Object()));
         this._statusFieldVFM = (VerticalFieldManager)(new Object(562949953421312L));
         this._nonScrollingRegion.add(this._statusFieldVFM);
      } else {
         FrameLayout layout = this.createAuthenticatorPasswordField();
         this._scrollingRegion.add(layout);
      }

      this._deviceIsUnlocked = !ApplicationManager.getApplicationManager().isSystemLocked();
      this._devicePasswordField.setFocus();
      this.setModal(true);
   }
}
