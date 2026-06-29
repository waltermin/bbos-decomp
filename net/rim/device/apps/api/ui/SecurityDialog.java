package net.rim.device.apps.api.ui;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.UserAuthenticationException;
import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.verb.WipeHandheldVerb;
import net.rim.device.internal.system.FIPSPolicy;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.system.Security$NoAccessUserAuthenticator;
import net.rim.device.internal.system.USBPasswordRedirectManager;
import net.rim.device.internal.ui.UiInternal;
import net.rim.device.internal.ui.UiSettings;
import net.rim.device.internal.ui.component.PleaseWaitDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.device.internal.ui.component.SimplePasswordDialog;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.util.Utils;
import net.rim.vm.Array;

public final class SecurityDialog {
   private static final int PASSWORD_INVALID = -1;
   private static Security _security = Security.getInstance();
   private static SimplePasswordDialog _passwordDialog = new SimplePasswordDialog(null, 1, 32, false, 0);
   private static UserAuthenticatorPasswordDialog _userAuthenticatorPasswordDialog;
   private static SecurityDialog$USBPasswordRedirectDialog _uSBPasswordRedirectDialog;
   private static String _knownPassword = "blackberry";
   private static String _newPassword;
   private static String _currentPassword;
   private static String _userAuthenticatorPassword;
   private static int _validationStatus;

   public static final synchronized boolean changePassword(String prompt, boolean blankScreen, boolean allowClose, boolean disablePassword, char key) {
      return displayPasswordDialog(prompt, blankScreen, key, allowClose, true, disablePassword);
   }

   public static final synchronized boolean challengeUser(String prompt, boolean blankScreen, boolean allowClose, char key, boolean pasteable) {
      if (!_security.isPasswordEnabled()) {
         return true;
      }

      if (!pasteable) {
         _passwordDialog.setPasteable(pasteable);
      }

      return displayPasswordDialog(prompt, blankScreen, key, allowClose, false, false);
   }

   public static final synchronized boolean initializeAuthenticator(boolean itAdminEnforced) {
      UserAuthenticator userAuthenticator = _security.getUserAuthenticator();
      if (userAuthenticator != null && userAuthenticator.isInitialized()) {
         return true;
      }

      SecurityDialog$GetAuthenticatorThread getAuthenticatorThread = new SecurityDialog$GetAuthenticatorThread(itAdminEnforced);
      PleaseWaitDialog pleaseWait = new PleaseWaitDialog(getAuthenticatorThread);
      pleaseWait.display();
      userAuthenticator = getAuthenticatorThread.getUserAuthenticator();
      if (userAuthenticator == null) {
         Dialog.alert(CommonResources.getString(9166));
         return false;
      }

      while (true) {
         StringBuffer passwordPromptMessage = new StringBuffer();
         if (itAdminEnforced) {
            passwordPromptMessage.append(CommonResources.getString(9130));
         } else {
            passwordPromptMessage.append(CommonResources.getString(9088));
         }

         int authenticatorPasswordAttempt = 0;
         int authenticatorPasswordMaxAttempts = 0;
         boolean authenticationPasswordAttemptsSupported = true;

         try {
            authenticatorPasswordMaxAttempts = userAuthenticator.getMaxAuthenticationAttempts();
            int remainingAuthenticationAttempts = userAuthenticator.getRemainingAuthenticationAttempts();
            authenticatorPasswordAttempt = authenticatorPasswordMaxAttempts - remainingAuthenticationAttempts + 1;
            authenticationPasswordAttemptsSupported = authenticatorPasswordMaxAttempts >= 0 && remainingAuthenticationAttempts >= 0;
         } finally {
            ;
         }

         if (authenticationPasswordAttemptsSupported && authenticatorPasswordAttempt > 1) {
            createAttemptsBuffer(passwordPromptMessage, authenticatorPasswordAttempt, authenticatorPasswordMaxAttempts);
         }

         passwordPromptMessage.append(": ");
         _passwordDialog.show(passwordPromptMessage.toString());
         _userAuthenticatorPassword = _passwordDialog.getText();
         if (_passwordDialog.getCloseReason() == -1) {
            if (!itAdminEnforced) {
               return false;
            }
         } else {
            SecurityDialog$ChallengeUser challenge = new SecurityDialog$ChallengeUser(_userAuthenticatorPassword, userAuthenticator);
            pleaseWait = new PleaseWaitDialog(CommonResources.getString(9031), challenge);
            pleaseWait.display();
            _passwordDialog.setText("");
            _userAuthenticatorPassword = null;
            Throwable throwable = pleaseWait.getThrowable();
            if (throwable instanceof UserAuthenticationException) {
               if (!itAdminEnforced) {
                  return false;
               }
            } else {
               if (challenge.getResult()) {
                  return true;
               }

               Dialog.alert(CommonResources.getString(6006));
               if (!itAdminEnforced) {
                  return false;
               }
            }
         }
      }
   }

   private static final boolean displayPasswordDialog(
      String prompt, boolean blankScreen, char key, boolean allowClose, boolean changePassword, boolean disablePassword
   ) {
      _validationStatus = -1;
      if (blankScreen) {
         UiApplication.getUiApplication().pushScreen(new MainScreen());
      }

      if (prompt == null) {
         prompt = CommonResources.getString(2012);
      }

      while (!checkCurrentPassword(key, prompt)) {
         if (allowClose) {
            return cleanup(blankScreen, false);
         }

         _passwordDialog.setText(null);
      }

      if (disablePassword) {
         String pleaseWaitMessage = null;
         if (_userAuthenticatorPassword != null) {
            pleaseWaitMessage = CommonResources.getString(9030);
         }

         SecurityDialog$ChallengeUser challenge = new SecurityDialog$ChallengeUser(_currentPassword, null, _userAuthenticatorPassword);
         PleaseWaitDialog pleaseWait = new PleaseWaitDialog(pleaseWaitMessage, challenge);
         pleaseWait.display();
         return cleanup(blankScreen, challenge.getResult());
      } else if (changePassword || _security.verifyPasswordPattern(_currentPassword) != 0 || _security.isPasswordValid(_currentPassword) != 0) {
         return challengeForNewPassword(blankScreen, allowClose);
      } else if (_security.activatePasswordAging() && _security.isPasswordEnabled()) {
         Dialog.alert(CommonResources.getString(9017));
         return challengeForNewPassword(blankScreen, allowClose);
      } else {
         return cleanup(blankScreen, true);
      }
   }

   private static final boolean challengeForNewPassword(boolean blankScreen, boolean allowClose) {
      boolean initialPasswordStatus = _security.isPasswordEnabled();

      while (true) {
         _passwordDialog.setText("");

         while (getNewPassword() == null && !allowClose) {
            if (!initialPasswordStatus && _security.isPasswordEnabled()) {
               allowClose = true;
            }
         }

         if ((_newPassword != null || _currentPassword != null) && _validationStatus == 0) {
            String pleaseWaitMessage = null;
            if (_userAuthenticatorPassword != null) {
               pleaseWaitMessage = CommonResources.getString(9031);
            }

            SecurityDialog$ChallengeUser challenge = new SecurityDialog$ChallengeUser(_currentPassword, _newPassword, _userAuthenticatorPassword);
            PleaseWaitDialog pleaseWait = new PleaseWaitDialog(pleaseWaitMessage, challenge);
            pleaseWait.display();
            Throwable throwable = pleaseWait.getThrowable();
            if (throwable instanceof UserAuthenticationException) {
               if (allowClose) {
                  return cleanup(blankScreen, false);
               }
               continue;
            }

            boolean result = challenge.getResult();
            if (!result) {
               Dialog.alert(CommonResources.getString(6006));
               if (!allowClose) {
                  continue;
               }
            }

            return cleanup(blankScreen, result);
         }

         return false;
      }
   }

   private static final boolean cleanup(boolean blankScreen, boolean result) {
      _userAuthenticatorPassword = null;
      _newPassword = null;
      _currentPassword = null;
      if (blankScreen) {
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      }

      return result;
   }

   private static final boolean checkCurrentPassword(char firstPasswordChar, String prompt) {
      if (!_security.isPasswordEnabled()) {
         _currentPassword = null;
         _userAuthenticatorPassword = null;
         return true;
      }

      USBPasswordRedirectManager redirectManager = USBPasswordRedirectManager.getInstance();
      boolean isPendingUSBConnection = redirectManager.getAllChannels().size() > 0;
      UserAuthenticator authenticator = _security.getUserAuthenticator();
      if (authenticator != null && !authenticator.isInitialized()) {
         authenticator = null;
      }

      if (authenticator != null && authenticator instanceof Security$NoAccessUserAuthenticator) {
         SimpleChoiceDialog dialog = new SimpleChoiceDialog(
            CommonResources.getString(9146), new String[]{CommonResources.getString(9042), WipeHandheldVerb.getDisplayString()}, 0, null
         );
         dialog.show();
         if (dialog.getCloseReason() != -1 && dialog.getSelectedIndex() != 0) {
            WipeHandheldVerb wipeVerb = new WipeHandheldVerb(0);
            wipeVerb.invoke(null);
            wipeVerb.waitForCompletion();
            return false;
         } else {
            return false;
         }
      } else {
         String initialPasswordString = null;
         if (firstPasswordChar != 0 && firstPasswordChar != '\n') {
            if ((Locale.getDefaultInputForSystem().getCode() & -65536) != 1701707776) {
               int modifier = 0;
               if (CharacterUtilities.isUpperCase(firstPasswordChar)) {
                  modifier = 1;
               } else if (Keypad.getLayout().getUnaltedChar(firstPasswordChar) != 0) {
                  modifier = 8;
               }

               int keycode = Keypad.getLayout().getOriginalKeyCode(firstPasswordChar, modifier);
               firstPasswordChar = UiInternal.mapFromFallbackLayout(keycode, modifier);
            }

            initialPasswordString = String.valueOf(firstPasswordChar);
         }

         int devicePasswordAttempt = _security.getPasswordFailureCount() + 1;
         int devicePasswordMaxAttempts = _security.getMaxPasswordAttempts();
         if (ITPolicy.getString(22, 7) != null) {
            devicePasswordAttempt = (devicePasswordAttempt + 1) / 2;
            devicePasswordMaxAttempts = (devicePasswordMaxAttempts + 1) / 2;
         }

         int authenticatorPasswordAttempt = 0;
         int authenticatorPasswordMaxAttempts = 0;
         boolean authenticationPasswordAttemptsSupported = true;
         if (authenticator != null) {
            try {
               authenticatorPasswordMaxAttempts = authenticator.getMaxAuthenticationAttempts();
               int remainingAuthenticationAttempts = authenticator.getRemainingAuthenticationAttempts();
               authenticatorPasswordAttempt = authenticatorPasswordMaxAttempts - remainingAuthenticationAttempts + 1;
               authenticationPasswordAttemptsSupported = authenticatorPasswordMaxAttempts >= 0 && remainingAuthenticationAttempts >= 0;
            } finally {
               ;
            }
         }

         if (devicePasswordAttempt == devicePasswordMaxAttempts / 2 + 1
            || devicePasswordAttempt >= devicePasswordMaxAttempts - 1
            || authenticator != null
               && authenticationPasswordAttemptsSupported
               && (
                  authenticatorPasswordAttempt == authenticatorPasswordMaxAttempts / 2 + 1
                     || authenticatorPasswordAttempt == authenticatorPasswordMaxAttempts - 1
               )) {
            initialPasswordString = null;
            if (!askForKnownPassword()) {
               return false;
            }
         }

         if (devicePasswordAttempt >= devicePasswordMaxAttempts
            || authenticator != null && authenticationPasswordAttemptsSupported && authenticatorPasswordAttempt == authenticatorPasswordMaxAttempts) {
            Dialog.alert(CommonResources.getString(9043));
         }

         boolean revealDevicePassword = devicePasswordAttempt > _security.getRevealPasswordAttempts(devicePasswordMaxAttempts);
         StringBuffer deviceAttemptsBuffer = new StringBuffer();
         if (devicePasswordAttempt > 1 || authenticatorPasswordAttempt > 1) {
            createAttemptsBuffer(deviceAttemptsBuffer, devicePasswordAttempt, devicePasswordMaxAttempts);
         }

         deviceAttemptsBuffer.append(": ");
         if (authenticator == null && !isPendingUSBConnection) {
            if (InputContext.getInstance(false).isSureType() && initialPasswordString != null && initialPasswordString.length() == 1) {
               _passwordDialog.setInitial(initialPasswordString.charAt(0));
            } else {
               _passwordDialog.setText(initialPasswordString);
            }

            _passwordDialog.setRevealPassword(revealDevicePassword);
            _passwordDialog.show(prompt + deviceAttemptsBuffer);
            if (_passwordDialog.getCloseReason() == -1) {
               return false;
            }

            _currentPassword = _passwordDialog.getText();
         } else if (authenticator == null && isPendingUSBConnection) {
            _uSBPasswordRedirectDialog = new SecurityDialog$USBPasswordRedirectDialog();
            if (InputContext.getInstance(false).isSureType() && initialPasswordString != null && initialPasswordString.length() == 1) {
               _uSBPasswordRedirectDialog.setInitial(initialPasswordString.charAt(0));
            } else {
               _uSBPasswordRedirectDialog.setText(initialPasswordString);
            }

            _uSBPasswordRedirectDialog.setMessage(deviceAttemptsBuffer.toString());
            _uSBPasswordRedirectDialog.setRevealPassword(revealDevicePassword);
            _uSBPasswordRedirectDialog.getEditField().setFocus();
            _uSBPasswordRedirectDialog.show();
            if (_uSBPasswordRedirectDialog.getCloseReason() == -1) {
               return false;
            }

            _currentPassword = _uSBPasswordRedirectDialog.getText();
         } else {
            StringBuffer authenticatorAttemptsBuffer = new StringBuffer();
            if (authenticatorPasswordMaxAttempts != Integer.MAX_VALUE
               && authenticationPasswordAttemptsSupported
               && (devicePasswordAttempt > 1 || authenticatorPasswordAttempt > 1)) {
               createAttemptsBuffer(authenticatorAttemptsBuffer, authenticatorPasswordAttempt, authenticatorPasswordMaxAttempts);
            }

            authenticatorAttemptsBuffer.append(": ");
            boolean revealAuthenticatorPassword = authenticationPasswordAttemptsSupported
               && authenticatorPasswordAttempt > _security.getRevealPasswordAttempts(authenticatorPasswordMaxAttempts);
            _userAuthenticatorPasswordDialog = new UserAuthenticatorPasswordDialog(
               CommonResources.getString(9089) + deviceAttemptsBuffer,
               revealDevicePassword,
               CommonResources.getString(9090) + authenticatorAttemptsBuffer,
               revealAuthenticatorPassword,
               _security.getAutoFillUserAuthenticatorPasswordField(),
               isPendingUSBConnection
            );
            _userAuthenticatorPasswordDialog.setInitialPasswordString(initialPasswordString);
            _userAuthenticatorPasswordDialog.show();
            if (_userAuthenticatorPasswordDialog.getCloseReason() == -1) {
               return false;
            }

            _currentPassword = _userAuthenticatorPasswordDialog.getDevicePassword();
            _userAuthenticatorPassword = _userAuthenticatorPasswordDialog.getAuthenticatorPassword();
         }

         if (_currentPassword.length() < 4) {
            Dialog.alert(CommonResources.getString(6006));
            return false;
         }

         String pleaseWaitMessage = null;
         if (_userAuthenticatorPassword != null) {
            pleaseWaitMessage = CommonResources.getString(9030);
         }

         Backlight.setTimeout(UiSettings.getBacklightTimeout());
         int[] selectedChannels = new int[0];
         if (_uSBPasswordRedirectDialog != null) {
            selectedChannels = _uSBPasswordRedirectDialog.getSelectedChannels();
         } else if (_userAuthenticatorPasswordDialog != null) {
            selectedChannels = _userAuthenticatorPasswordDialog.getSelectedChannels();
         }

         SecurityDialog$ChallengeUser challenge;
         if (selectedChannels.length > 0) {
            challenge = new SecurityDialog$ChallengeUser(_currentPassword, _userAuthenticatorPassword, selectedChannels);
         } else {
            challenge = new SecurityDialog$ChallengeUser(_currentPassword, _userAuthenticatorPassword);
         }

         PleaseWaitDialog pleaseWait = new PleaseWaitDialog(pleaseWaitMessage, challenge);
         pleaseWait.display();
         Throwable throwable = pleaseWait.getThrowable();
         if (throwable instanceof UserAuthenticationException) {
            return false;
         }

         if (challenge.getResult()) {
            return true;
         }

         String notification = CommonResources.getString(6006);
         Field f = _passwordDialog.getEditField();
         if (f instanceof PasswordEditField) {
            PasswordEditField pef = (PasswordEditField)f;
            if (pef.isUnicodeInputAllowed()) {
               Locale[] locales = Locale.getAvailableInputLocales();
               if (checkInputLocales(locales)) {
                  locales = getFilteredInputLocales(locales);
                  int index = Dialog.ask(notification + "\n" + CommonResources.getString(9161), Utils.getInputLocalesDisplayNames(locales), 0);
                  if (index != -1) {
                     Locale.setDefaultInputForSystem(locales[index]);
                  }

                  return false;
               }
            }
         }

         Dialog.alert(notification);
         return false;
      }
   }

   private static final boolean checkInputLocales(Locale[] locales) {
      if (locales.length > 1) {
         for (int i = locales.length - 1; i >= 0; i--) {
            if ((locales[i].getCode() & -65536) != 2053636096 && !Locale.isLatinOneCharacterSetLocale(locales[i])) {
               return true;
            }
         }
      }

      return false;
   }

   private static final Locale[] getFilteredInputLocales(Locale[] src) {
      InputContext ic = InputContext.getInstance();
      boolean isMultitapMode = ic.getActiveInputMethodID() == 4096;
      Locale[] res = new Locale[src.length];
      int count = 0;

      for (int i = 0; i < src.length; i++) {
         if (src[i].getVariant().equals("Multitap")) {
            if (isMultitapMode) {
               res[count++] = src[i];
            }
         } else if (!isMultitapMode && (src[i].getCode() & -65536) != 2053636096) {
            res[count++] = src[i];
         }
      }

      Array.resize(res, count);
      Utils.filterRootInputLocales(res);
      Utils.moveToIndex(res, ic.getLocale(), 0);
      return res;
   }

   private static final void createAttemptsBuffer(StringBuffer buffer, int attempt, int maxAttempts) {
      if (attempt >= maxAttempts) {
         buffer.append(' ');
         buffer.append('(');
         buffer.append(CommonResources.getString(9164));
         buffer.append(')');
      } else {
         if (attempt > 1) {
            buffer.append(' ');
            buffer.append('(');
            buffer.append(attempt);
            buffer.append('/');
            buffer.append(maxAttempts);
            buffer.append(')');
         }
      }
   }

   private static final String getNewPassword() {
      _passwordDialog.setRevealPassword(!FIPSPolicy.getBoolean(22, 3, true, true));
      String label = CommonResources.getString(2010);
      String mask = ITPolicy.getString(24, 73);
      if (mask != null) {
         StringBuffer sb = new StringBuffer(label.length() + 2 + mask.length() + 1);
         sb.append(label);
         sb.append(' ');
         sb.append('(');
         sb.append(mask);
         sb.append(')');
         label = sb.toString();
      }

      _passwordDialog.show(label);
      String newPassword = _passwordDialog.getText();
      if (!checkPasswordHistory(newPassword)) {
         Dialog.alert(CommonResources.getString(9050));
         return null;
      }

      if (_passwordDialog.getCloseReason() != -1 && isValidPassword(newPassword)) {
         _validationStatus = _security.verifyPasswordPattern(newPassword);
         switch (_validationStatus) {
            case 3:
            case 7:
               _passwordDialog.show(CommonResources.getString(2011));
               String verifiedPassword = _passwordDialog.getText();
               if (_passwordDialog.getCloseReason() != -1 && newPassword.compareTo(verifiedPassword) == 0) {
                  _newPassword = newPassword;
                  return _newPassword;
               }

               _validationStatus = -1;
               Dialog.alert(CommonResources.getString(6001));
               break;
            case 4:
            default:
               Dialog.alert(CommonResources.getString(9027));
               return null;
            case 5:
               Dialog.alert(CommonResources.getString(9028));
               return null;
            case 6:
               Dialog.alert(CommonResources.getString(9029));
               return null;
            case 8:
               Dialog.alert(CommonResources.getString(9168));
               return null;
         }
      }

      return null;
   }

   private static final boolean isValidPassword(String password) {
      _validationStatus = _security.isPasswordValid(password);
      switch (_validationStatus) {
         case -1:
         case 4:
         case 5:
         case 6:
            Dialog.alert(CommonResources.getString(6004));
            return false;
         case 0:
         default:
            if (_currentPassword != null && _currentPassword.equals(password)) {
               Dialog.alert(CommonResources.getString(6005));
               _validationStatus = -1;
               return false;
            }

            return true;
         case 1:
            Dialog.alert(CommonResources.getString(6002));
            return false;
         case 2:
            Dialog.alert(CommonResources.getString(6003));
            return false;
         case 3:
            Dialog.alert(CommonResources.getString(9018));
            return false;
         case 7:
            Dialog.alert(CommonResources.getString(9160));
            return false;
      }
   }

   private static final boolean askForKnownPassword() {
      _passwordDialog.setText("");
      String prompt = MessageFormat.format(CommonResources.getString(9019), new Object[]{_knownPassword});
      _passwordDialog.setRevealPassword(true);
      _passwordDialog.show(prompt);
      return _passwordDialog.getCloseReason() == -1 ? false : StringUtilities.compareToIgnoreCase(_knownPassword, _passwordDialog.getText(), 1701707776) == 0;
   }

   private static final boolean statusAskForKnownPassword() {
      String prompt = MessageFormat.format(CommonResources.getString(9019), new Object[]{_knownPassword});
      SecurityDialog$EnhancedPasswordDialog dialog = new SecurityDialog$EnhancedPasswordDialog(prompt, true, null);
      dialog.show();
      int result = dialog.getChoice();
      return result == 1 ? false : StringUtilities.compareToIgnoreCase(_knownPassword, new String(dialog.getPassword()), 1701707776) == 0;
   }

   public static final synchronized boolean otaChangePassword(String otaPwd) {
      while (true) {
         int devicePasswordAttempt = _security.getPasswordFailureCount() + 1;
         int devicePasswordMaxAttempts = _security.getMaxPasswordAttempts();
         if ((devicePasswordAttempt == devicePasswordMaxAttempts / 2 + 1 || devicePasswordAttempt >= devicePasswordMaxAttempts - 1)
            && !statusAskForKnownPassword()) {
            return false;
         }

         if (devicePasswordAttempt >= devicePasswordMaxAttempts) {
            statusAlert(CommonResources.getString(9043));
         }

         boolean revealDevicePassword = devicePasswordAttempt > _security.getRevealPasswordAttempts(devicePasswordMaxAttempts);
         StringBuffer deviceAttemptsBuffer = new StringBuffer();
         createAttemptsBuffer(deviceAttemptsBuffer, devicePasswordAttempt, devicePasswordMaxAttempts);
         deviceAttemptsBuffer.append(": ");
         String prompt = CommonResources.getString(9114) + deviceAttemptsBuffer.toString();
         SecurityDialog$EnhancedPasswordDialog dialog = new SecurityDialog$EnhancedPasswordDialog(
            prompt, revealDevicePassword, new Object[]{WipeHandheldVerb.getDisplayString()}
         );
         dialog.setCancelAllowed(false);
         dialog.show();
         int choice = dialog.getChoice();
         switch (choice) {
            case -1:
               return false;
            case 0:
            default:
               label79:
               try {
                  byte[] oldPassword = dialog.getPassword();
                  String password;
                  if (oldPassword != null && oldPassword.length != 0) {
                     password = new String(oldPassword);
                  } else {
                     password = null;
                  }

                  if (_security.setPassword(password, otaPwd, null)) {
                     return true;
                  }

                  statusAlert(CommonResources.getString(6004));
               } finally {
                  break label79;
               }

               if (_security.getPasswordFailureCount() >= _security.getMaxPasswordAttempts()) {
                  _security.deviceUnderAttack();
               }
               break;
            case 1:
               return false;
            case 2:
               WipeHandheldVerb wipeVerb = new WipeHandheldVerb(33554432);
               wipeVerb.invoke(null);
               wipeVerb.waitForCompletion();
               return false;
         }
      }
   }

   private static final void statusAlert(String message) {
      Dialog dialog = new Dialog(0, message, 0, Bitmap.getPredefinedBitmap(0), 33554432);
      UiApplication.getUiApplication().pushGlobalScreen(dialog, -1073741823, 2);
   }

   private static final byte[] calculateNewHash(String pswd) {
      Digest digest = new SHA1Digest();
      digest.update(pswd.getBytes());
      return Arrays.copy(digest.getDigest(), 0, 4);
   }

   private static final byte[] calculateOldHash(String pswd) {
      int crc = CRC32.update(-1, pswd.getBytes());
      return new byte[]{(byte)(crc >> 24), (byte)(crc >> 16), (byte)(crc >> 8), (byte)crc};
   }

   public static final void addPasswordHistory(String pswd) {
      if (enabledPasswordHistory()) {
         byte[] hash = calculateNewHash(pswd);
         int max = ITPolicy.getInteger(22, 4, 0);
         byte[] data = NvStore.readData(11);
         if (data == null) {
            data = new byte[4];
         } else if (data.length < max * 4) {
            Array.resize(data, data.length + 4);
         } else {
            Array.resize(data, max * 4);
         }

         System.arraycopy(data, 0, data, 4, data.length - 4);
         System.arraycopy(hash, 0, data, 0, 4);
         NvStore.writeData(11, data);
      }
   }

   public static final boolean checkPasswordHistory(String pswd) {
      if (!enabledPasswordHistory()) {
         return true;
      }

      byte[] data = NvStore.readData(11);
      if (data == null) {
         return true;
      }

      byte[] newHash = calculateNewHash(pswd);
      byte[] oldHash = calculateOldHash(pswd);

      for (int i = data.length - 4; i >= 0; i -= 4) {
         if (Arrays.equals(data, i, newHash, 0, 4) || Arrays.equals(data, i, oldHash, 0, 4)) {
            return false;
         }
      }

      return true;
   }

   private static final boolean enabledPasswordHistory() {
      if (ITPolicy.getInteger(22, 4, 0) == 0) {
         NvStore.deleteData(11);
         return false;
      } else {
         return true;
      }
   }
}
