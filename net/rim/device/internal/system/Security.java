package net.rim.device.internal.system;

import java.io.EOFException;
import java.util.Vector;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentInternal;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.applicationcontrol.ApplicationControl;

public final class Security {
   private Security$SecurityCache _securityCache;
   private UserAuthenticator _userAuthenticator;
   private Vector _registeredUserAuthenticators;
   private boolean _pendingContentProtectionChange;
   private boolean _pendingContentProtectionEncryptionSetting;
   private int _pendingContentProtectionEncryptionStrength;
   private boolean _lockOnIdle = true;
   private DevicePasswordListener _keyStoreListener;
   private DevicePasswordListener _fileSystemEncryptionListener;
   private SecurityCallHandler _callHandler = null;
   private boolean _isAutoOnRequired = false;
   private int _unlockCounter = 0;
   private int _lockCounter = 0;
   private static final long REGISTRY_NAME = 9159244075162769423L;
   private static final long PASSWORD_KEY = 5031265368654170436L;
   private static final int DAY_IN_MILLISECONDS = 86400000;
   private static final int PASSWORD_ENABLED_TIMESTAMP = 1;
   private static Security _instance;
   public static final int MAX_PASSWORD_LENGTH = 32;
   public static final int PASSWORD_OK = 0;
   public static final int PASSWORD_TOO_SHORT = 1;
   public static final int PASSWORD_TOO_LONG = 2;
   public static final int PASSWORD_IS_SEQUENCE = 3;
   public static final int PASSWORD_REQ_ALPHA_NUMERIC = 4;
   public static final int PASSWORD_REQ_ALPHA_NUMERIC_SPECIAL = 5;
   public static final int PASSWORD_REQ_ALPHA_CASE_NUMERIC_SPECIAL = 6;
   public static final int PASSWORD_FORBIDDEN = 7;
   public static final int PASSWORD_PATTERN_MASK_ERROR = 8;
   public static final long GUID_PASSWORD_STATE_CHANGED = -1789715216180579536L;
   public static final long GUID_SECURITY_OPTIONS_CHANGED = 9206737719270818227L;
   public static final long SECURITY_SETTING_PASSWORD_GUID = 5862813030521710644L;
   public static final long SECURITY_SETTING_CONTENT_PROTECTION_GUID = -746144713976755387L;
   public static final long SECURITY_SETTING_FILE_SYSTEM_ENCRYPTION_UID = -2975703265270751603L;
   public static final long GUID_PASSWORD_ENTRY_CHANGED = 306123729322610706L;

   public static final Security getInstance() {
      if (_instance == null) {
         ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (Security)appRegistry.getOrWaitFor(9159244075162769423L);
         if (_instance == null) {
            _instance = new Security();
            appRegistry.put(9159244075162769423L, _instance);
         }
      }

      return _instance;
   }

   private static final native boolean lockOnIdleTimeout();

   private Security() {
      this._registeredUserAuthenticators = new Vector();
      PersistentObject store = RIMPersistentStore.getPersistentObject(5031265368654170436L);
      this._securityCache = (Security$SecurityCache)store.getContents();
      if (this._securityCache == null) {
         this._securityCache = new Security$SecurityCache(null);
      }

      byte[] userAuthenticatorClassNameBytes = NvStore.readData(15);
      if (userAuthenticatorClassNameBytes != null) {
         String userAuthenticatorClassName = new String(userAuthenticatorClassNameBytes);

         try {
            if (userAuthenticatorClassName.equals("net.rim.device.apps.internal.smartcard.gsacac.GSACACSmartCardUserAuthenticator")) {
               userAuthenticatorClassName = "net.rim.device.api.smartcard.GenericSmartCardUserAuthenticator";
            } else if (userAuthenticatorClassName.equals("net.rim.device.apps.internal.smartcard.datakey.DatakeySmartCardUserAuthenticator")) {
               userAuthenticatorClassName = "net.rim.device.api.smartcard.GenericSmartCardUserAuthenticator";
            }

            this._userAuthenticator = (UserAuthenticator)Class.forName(userAuthenticatorClassName).newInstance();
         } catch (ClassNotFoundException var5) {
         } catch (InstantiationException var6) {
         } catch (IllegalAccessException var7) {
         } catch (ClassCastException var8) {
         }

         if (this._userAuthenticator == null) {
            if (!this.isPasswordEnabled() && !FIPSPolicy.isDevicePasswordRequired()) {
               this.uninitializeUserAuthenticator();
            } else {
               this._userAuthenticator = new Security$NoAccessUserAuthenticator();
            }
         }

         if (this._userAuthenticator != null) {
            byte[] data = NvStore.readData(16);
            if (this._userAuthenticator.setStateData(data) && this._userAuthenticator.isInitialized()) {
               if (!this.isPasswordEnabled()) {
                  this.uninitializeUserAuthenticator();
               }
            } else if (!this.isPasswordEnabled() && !FIPSPolicy.isDevicePasswordRequired()) {
               this.uninitializeUserAuthenticator();
            } else {
               this._userAuthenticator = new Security$NoAccessUserAuthenticator();
            }
         }
      }

      if (this.isPasswordEnabled() && ITPolicy.getInteger(24, 18, -1) != -1) {
         this._pendingContentProtectionChange = true;
         this._pendingContentProtectionEncryptionSetting = true;
      }

      this._lockOnIdle = lockOnIdleTimeout();
   }

   public final boolean setUserAuthenticatorPassword(UserAuthenticator userAuthenticator, String userAuthenticatorPassword) {
      if (userAuthenticator == null
         || this._userAuthenticator != null
         || userAuthenticatorPassword == null
         || !this._registeredUserAuthenticators.contains(userAuthenticator)) {
         throw new IllegalArgumentException();
      }

      if (userAuthenticator.initialize(userAuthenticatorPassword)) {
         this._userAuthenticator = userAuthenticator;
         this.setNumericPasswords(null, userAuthenticatorPassword);
         Class authenticatorClass = userAuthenticator.getClass();
         NvStore.writeData(15, authenticatorClass.getName().getBytes());
         byte[] state = userAuthenticator.getStateData();
         if (state != null) {
            NvStore.writeData(16, state);
         }

         return true;
      } else {
         return false;
      }
   }

   public final void reinitializeUserAuthenticatorStateData() {
      if (this._userAuthenticator == null) {
         throw new IllegalArgumentException();
      }

      byte[] state = this._userAuthenticator.getStateData();
      if (state != null) {
         NvStore.writeData(16, state);
      }
   }

   public final void uninitializeUserAuthenticator() {
      NvStore.deleteData(15);
      NvStore.deleteData(16);
      this._securityCache.setAutoFillUserAuthenticatorPasswordField(false);
      if (this._userAuthenticator != null) {
         this._userAuthenticator.uninitialize();
         this._userAuthenticator = null;
      }
   }

   public final boolean setPassword(String oldPassword, String newPassword, String userAuthenticatorPassword) {
      if (this.isPasswordValid(newPassword) != 0) {
         return false;
      }

      boolean success = this.setPassword(oldPassword, newPassword);
      boolean enabledDuress = ITPolicy.getString(22, 7) != null;
      if (!success && enabledDuress) {
         String duressPswd = oldPassword.charAt(oldPassword.length() - 1) + oldPassword.substring(0, oldPassword.length() - 1);
         success = this.setPassword(duressPswd, newPassword);
         if (success) {
            oldPassword = duressPswd;
            RIMGlobalMessagePoster.postGlobalEvent(4681343386835470834L);
         }
      }

      if (success) {
         if (oldPassword == null && newPassword != null) {
            RIMGlobalMessagePoster.postGlobalEvent(-1789715216180579536L, 1, 0);
         } else if (oldPassword != null && newPassword == null) {
            RIMGlobalMessagePoster.postGlobalEvent(-1789715216180579536L, 0, 0);
            this._securityCache.setNumericHandheldPassword(false);
         } else if (oldPassword != null && newPassword != null && userAuthenticatorPassword != null) {
            this.setNumericPasswords(newPassword, userAuthenticatorPassword);
         }

         RIMGlobalMessagePoster.postGlobalEvent(9206737719270818227L, 0, 0);
         this.setPasswordEnableTimeStamp();
         this._securityCache.markLongTermTimeOutTimeStamp();
         this._securityCache.setAutoFillUserAuthenticatorPasswordField(StringUtilities.strEqual(newPassword, userAuthenticatorPassword));
         if (this._pendingContentProtectionChange) {
            this._pendingContentProtectionChange = false;
            PersistentContentInternal.setContentProtection(
               newPassword, this._pendingContentProtectionEncryptionSetting, this._pendingContentProtectionEncryptionStrength
            );
         } else {
            PersistentContentInternal.changePassword(oldPassword, newPassword);
         }

         if (this._keyStoreListener != null) {
            this._keyStoreListener.changePassword(oldPassword, newPassword);
         }

         if (this._fileSystemEncryptionListener != null) {
            this._fileSystemEncryptionListener.changePassword(oldPassword, newPassword);
         }
      }

      return success;
   }

   public final void setTimeoutIfRequired() {
      int setTimeout = 60 * ITPolicy.getInteger(22, 1, -1);
      boolean allowUserToChangeTimeout = ITPolicy.getBoolean(12, true);
      if (setTimeout >= 0) {
         if (allowUserToChangeTimeout) {
            if (!this.isPasswordEnabled()) {
               this.setTimeout(setTimeout);
               return;
            }
         } else {
            this.setTimeout(setTimeout);
         }
      }
   }

   public final void setContentProtection(boolean newEncryptionSetting, int newEncryptionStrength) {
      this._pendingContentProtectionChange = true;
      this._pendingContentProtectionEncryptionSetting = newEncryptionSetting;
      this._pendingContentProtectionEncryptionStrength = newEncryptionStrength;
   }

   public final int getEncryptionStrength() {
      return this._pendingContentProtectionEncryptionStrength;
   }

   public final void clearContentProtection() {
      this._pendingContentProtectionChange = false;
   }

   public final boolean isContentProtectionPending() {
      return this._pendingContentProtectionChange;
   }

   public final int verifyPasswordPattern(String password) {
      int result = 0;
      int pattern = ITPolicy.getInteger(13, 0);
      if (pattern != 0) {
         boolean alpha = false;
         boolean numeric = false;
         boolean specialChar = false;
         boolean upperCase = false;
         boolean lowerCase = false;
         int length = password.length();

         for (int i = 0; i < length; i++) {
            char current = password.charAt(i);
            if (Character.isDigit(current)) {
               numeric = true;
            } else if (Character.isLowerCase(current)) {
               lowerCase = true;
            } else if (Character.isUpperCase(current)) {
               upperCase = true;
            }

            if (CharacterUtilities.isLetter(current)) {
               alpha = true;
            } else if (CharacterUtilities.isPunctuation(current) || CharacterUtilities.isSymbol(current)) {
               specialChar = true;
            }
         }

         switch (pattern) {
            case 0:
               break;
            case 1:
            default:
               if (!alpha || !numeric) {
                  result = 4;
               }
               break;
            case 2:
               if (!alpha || !numeric || !specialChar) {
                  result = 5;
               }
               break;
            case 3:
               if (!alpha || !numeric || !specialChar || !upperCase || !lowerCase) {
                  result = 6;
               }
         }
      }

      String mask = ITPolicy.getString(24, 73);
      if (mask != null) {
         int length = mask.length();
         if (password.length() < length) {
            return 8;
         }

         for (int i = 0; i < length; i++) {
            char ch = password.charAt(i);
            switch (mask.charAt(i)) {
               case '#':
               case 'N':
               case 'n':
                  if (!isDigit(ch)) {
                     return 8;
                  }
                  break;
               case '@':
               case 'S':
               case 's':
                  if (isLetter(ch) || isDigit(ch)) {
                     return 8;
                  }
                  break;
               case 'A':
                  if (!isUppercase(ch)) {
                     return 8;
                  }
               case 'a':
                  if (!isLetter(ch)) {
                     return 8;
                  }
                  break;
               case 'C':
                  if (!isUppercase(ch)) {
                     return 8;
                  }
               case 'c':
                  if (!isLetter(ch) || isVowel(ch)) {
                     return 8;
                  }
                  break;
               case 'V':
                  if (!isUppercase(ch)) {
                     return 8;
                  }
               case 'v':
                  if (!isLetter(ch) || !isVowel(ch)) {
                     return 8;
                  }
            }
         }
      }

      return result;
   }

   public final native boolean verifyPasswordChallenge(byte[] var1, byte[] var2);

   private static final boolean isLetter(char ch) {
      return 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z';
   }

   private static final boolean isUppercase(char ch) {
      return 'A' <= ch && ch <= 'Z';
   }

   private static final boolean isVowel(char ch) {
      return ch == 'a'
         || ch == 'e'
         || ch == 'i'
         || ch == 'o'
         || ch == 'u'
         || ch == 'y'
         || ch == 'A'
         || ch == 'E'
         || ch == 'I'
         || ch == 'O'
         || ch == 'U'
         || ch == 'Y';
   }

   private static final boolean isDigit(char ch) {
      return '0' <= ch && ch <= '9';
   }

   public final boolean verifyPassword(String password, String userAuthenticatorPassword, int[] uSBChannels) {
      boolean result = false;
      if (this.verifyPassword(password, userAuthenticatorPassword)) {
         result = true;
      }

      for (int i = 0; i < uSBChannels.length; i++) {
         USBPasswordRedirectManager.getInstance().allowChannel(uSBChannels[i], result);
      }

      return result;
   }

   public final boolean verifyPassword(String password, String userAuthenticatorPassword) {
      boolean enabledDuress = ITPolicy.getString(22, 7) != null;
      if (this.verifyPassword(password)) {
         return this.processVerifyPassword(password, userAuthenticatorPassword);
      }

      if (enabledDuress) {
         String duressPswd = password.charAt(password.length() - 1) + password.substring(0, password.length() - 1);
         if (this.verifyPassword(duressPswd)) {
            if (!this.processVerifyPassword(duressPswd, userAuthenticatorPassword)) {
               return false;
            }

            RIMGlobalMessagePoster.postGlobalEvent(4681343386835470834L);
            return true;
         }
      }

      int failureCount = this.getPasswordFailureCount();
      int devicePasswordMaxAttempts = this.getMaxPasswordAttempts();
      if (enabledDuress) {
         failureCount = (failureCount + 1) / 2;
         devicePasswordMaxAttempts = devicePasswordMaxAttempts / 2 + 1;
      }

      if (failureCount >= devicePasswordMaxAttempts) {
         this.deviceUnderAttack();
      }

      return false;
   }

   private final boolean processVerifyPassword(String password, String userAuthenticatorPassword) {
      try {
         if (this._userAuthenticator != null && this._userAuthenticator.isInitialized()) {
            if (!this._userAuthenticator.authenticate(userAuthenticatorPassword)) {
               return false;
            }

            this.setNumericPasswords(password, userAuthenticatorPassword);
         }
      } finally {
         ;
      }

      this._securityCache.markLongTermTimeOutTimeStamp();
      PersistentContentInternal.unlock(password);
      if (this._pendingContentProtectionChange) {
         this._pendingContentProtectionChange = false;
         PersistentContentInternal.setContentProtection(
            password, this._pendingContentProtectionEncryptionSetting, this._pendingContentProtectionEncryptionStrength
         );
      }

      if (this._keyStoreListener != null) {
         this._keyStoreListener.unlock(password);
      }

      if (this._fileSystemEncryptionListener != null) {
         this._fileSystemEncryptionListener.unlock(password);
      }

      this._securityCache.setAutoFillUserAuthenticatorPasswordField(StringUtilities.strEqual(password, userAuthenticatorPassword));
      return true;
   }

   public final boolean setKeyStoreListener(DevicePasswordListener listener) {
      if (this._keyStoreListener != null) {
         return false;
      }

      this._keyStoreListener = listener;
      return true;
   }

   public final boolean setFileSystemEncryptionListener(DevicePasswordListener listener) {
      if (this._fileSystemEncryptionListener != null) {
         return false;
      }

      this._fileSystemEncryptionListener = listener;
      return true;
   }

   public final boolean verifyStoredPasswordOnly(String password) {
      if (this.verifyPassword(password)) {
         USBPasswordRedirectManager.getInstance().clearChannels(true);
         this._securityCache.markLongTermTimeOutTimeStamp();
         PersistentContentInternal.unlock(password);
         if (this._keyStoreListener != null) {
            this._keyStoreListener.unlock(password);
         }

         if (this._fileSystemEncryptionListener != null) {
            this._fileSystemEncryptionListener.unlock(password);
         }

         return true;
      } else {
         if (this.getPasswordFailureCount() >= this.getMaxPasswordAttempts()) {
            this.deviceUnderAttack();
         }

         return false;
      }
   }

   public final int isPasswordValid(String password) {
      if (password == null) {
         return 0;
      }

      int length = password.length();
      if (length < FIPSPolicy.getMaxInteger(8, 4, 5)) {
         return 1;
      }

      if (length > 32) {
         return 2;
      }

      if (this.checkCyclic(password, false) < length && this.checkCyclic(password, true) < length) {
         char previous = password.charAt(0);
         int count = 1;

         for (int i = 1; i < length; i++) {
            char current = password.charAt(i);
            if (current != previous) {
               previous = current;
               count = 1;
            } else if (++count >= 3) {
               return 3;
            }
         }

         return containsForbiddenPassword(password, ITPolicy.getString(22, 9)) ? 7 : 0;
      } else {
         return 3;
      }
   }

   private final int checkCyclic(String password, boolean altedChar) {
      int length = password.length();
      int difference = altedChar
         ? password.charAt(1) - password.charAt(0)
         : Keypad.getUnaltedChar(password.charAt(1)) - Keypad.getUnaltedChar(password.charAt(0));

      int i;
      for (i = 1; i < length; i++) {
         char current = altedChar ? password.charAt(i) : Keypad.getUnaltedChar(password.charAt(i));
         char previous = altedChar ? password.charAt(i - 1) : Keypad.getUnaltedChar(password.charAt(i - 1));
         if (current - previous != difference || !Character.isDigit(current) && !Character.isLowerCase(current) && !Character.isUpperCase(current)) {
            break;
         }

         if (!Character.isDigit(previous) && !Character.isLowerCase(previous) && !Character.isUpperCase(previous)) {
            return i;
         }
      }

      return i;
   }

   static final boolean containsForbiddenPassword(String password, String forbiddenPasswords) {
      if (forbiddenPasswords == null) {
         return false;
      }

      forbiddenPasswords = StringUtilities.toLowerCase(forbiddenPasswords.trim(), 1701707776);
      int forbiddenPasswordsLength = forbiddenPasswords.length();
      if (forbiddenPasswordsLength == 0) {
         return false;
      }

      password = StringUtilities.toLowerCase(password, 1701707776);
      int passwordLength = password.length();
      String[] matches = new String[]{
         "@",
         "4",
         "^",
         "/\\",
         "|3",
         "8",
         "c",
         "(",
         "<",
         "|)",
         "3",
         "f",
         "|[",
         "|=",
         "6",
         "9",
         "|-|",
         "(-)",
         "|{",
         "1",
         "!",
         "|",
         "_|",
         "k",
         "|<",
         "|_",
         "rn",
         "|v|",
         "/v\\",
         "/\\/\\",
         "|\\/|",
         "^^",
         "/\\/",
         "|\\|",
         "0",
         "()",
         "|o",
         "|0",
         "0,",
         "o,",
         "|)\\",
         "|p",
         "s",
         "$",
         "5",
         "7",
         "+",
         "|_|",
         "\\/",
         "vv",
         "\\/\\/",
         "|/\\|",
         "\\\\'",
         "'//",
         "`//",
         "x",
         "><",
         "'/",
         "`/",
         "\\'",
         "z",
         "2",
         "0r",
         "ph",
         "cks"
      };
      String[] replaces = new String[]{
         "a",
         "a",
         "a",
         "a",
         "b",
         "b",
         "ks",
         "cks",
         "cks",
         "d",
         "e",
         "ph",
         "fph",
         "fph",
         "g",
         "g",
         "h",
         "h",
         "hk",
         "il",
         "il",
         "il",
         "j",
         "c",
         "kc",
         "l",
         "m",
         "m",
         "m",
         "m",
         "m",
         "m",
         "n",
         "n",
         "o",
         "ou",
         "p",
         "p",
         "q",
         "q",
         "r",
         "r",
         "cz",
         "scz",
         "scz",
         "t",
         "t",
         "u",
         "v",
         "w",
         "w",
         "w",
         "w",
         "w",
         "w",
         "cks",
         "xcks",
         "y",
         "y",
         "y",
         "s",
         "zs",
         "oer",
         "f",
         "x"
      };
      StringBuffer buffer = new StringBuffer(128);

      for (int offset = 0; offset < passwordLength; offset++) {
         char c = password.charAt(offset);
         buffer.append(c);

         for (int i = matches.length - 1; i >= 0; i--) {
            String match = matches[i];
            if (password.regionMatches(false, offset, match, 0, match.length())) {
               buffer.append(replaces[i]);
            }
         }
      }

      int bufferLength = buffer.length();
      int j = 0;

      while (j < forbiddenPasswordsLength) {
         char m = forbiddenPasswords.charAt(j);

         for (int i = 0; i < bufferLength; i++) {
            if (buffer.charAt(i) == m) {
               if (++j >= forbiddenPasswordsLength) {
                  return true;
               }

               m = forbiddenPasswords.charAt(j);
               if (m == ',' || m == ' ') {
                  return true;
               }
            }
         }

         while (j < forbiddenPasswordsLength) {
            m = forbiddenPasswords.charAt(j);
            if (m == ',' || m == ' ') {
               break;
            }

            j++;
         }

         while (j < forbiddenPasswordsLength) {
            m = forbiddenPasswords.charAt(j);
            if (m != ',' && m != ' ') {
               break;
            }

            j++;
         }
      }

      return false;
   }

   public final void setCallHandler(SecurityCallHandler handler) {
      this._callHandler = handler;
   }

   public final SecurityCallHandler getCallHandler() {
      return this._callHandler;
   }

   public final native int getMaxPasswordAttempts();

   public final void setMaxPasswordAttempts(int maxAttempts) {
      int itPolicyMaxPasswordAttempts = ITPolicy.getInteger(22, 2, 10);
      int clampedValue = MathUtilities.clamp(3, maxAttempts, itPolicyMaxPasswordAttempts);
      NvStore.writeInt(9, clampedValue);
      this._securityCache.setMaxPasswordAttempts(clampedValue);
      this.setMaxPasswordAttemptsInternal(clampedValue);
   }

   private final native boolean setMaxPasswordAttemptsInternal(int var1);

   public final boolean isLastAttempt() {
      return this.getPasswordFailureCount() + 1 >= this.getMaxPasswordAttempts();
   }

   public final int getRevealPasswordAttempts(int maxAttempts) {
      return FIPSPolicy.getBoolean(22, 3, false, true) ? maxAttempts + 1 : maxAttempts / 2;
   }

   public final boolean isPasswordEnabled() {
      return !this.verifyPassword(null);
   }

   public final boolean setTimeout(int seconds) {
      boolean timeoutSet = this._securityCache.setCurrentTimeOut(seconds);
      if (timeoutSet) {
         RIMGlobalMessagePoster.postGlobalEvent(9206737719270818227L, 22, 1, null, null);
      }

      return timeoutSet;
   }

   public final int getTimeout() {
      return this._securityCache.getCurrentTimeOut();
   }

   public final void setLockWhenHolstered(boolean lockWhenHolstered) {
      this._securityCache.setLockWhenHolstered(lockWhenHolstered);
      RIMGlobalMessagePoster.postGlobalEvent(9206737719270818227L, 24, 12, null, null);
   }

   public final boolean getLockWhenHolstered() {
      return this._securityCache.getLockWhenHolstered();
   }

   public final void setPasswordRequiredForAppInstall(boolean passwordRequiredForInstall) {
      this._securityCache.setPasswordRequiredForAppInstall(passwordRequiredForInstall);
   }

   public final boolean getPasswordRequiredForAppInstall() {
      return this._securityCache.getPasswordRequiredForAppInstall();
   }

   public final void setAllowOutgoingCallWhileLocked(boolean allowOutgoingCallWhileLocked) {
      this._securityCache.setAllowOutgoingCallWhileLocked(allowOutgoingCallWhileLocked);
      RIMGlobalMessagePoster.postGlobalEvent(9206737719270818227L, 24, 40, null, null);
   }

   public final boolean getAllowOutgoingCallWhileLocked() {
      return ITPolicy.getBoolean(24, 40, true) && this._securityCache.getAllowOutgoingCallWhileLocked();
   }

   public final boolean getAutoFillUserAuthenticatorPasswordField() {
      return this._securityCache._autoFillUserAuthenticatorPasswordField;
   }

   public final void setSecurityServiceColours(int ITPolicyServiceColour, int otherServiceColour) {
      this._securityCache.setSecurityServiceColours(ITPolicyServiceColour, otherServiceColour);
      RIMGlobalMessagePoster.postGlobalEvent(9206737719270818227L, 24, 42, null, null);
   }

   private final int parseSecurityServiceColour(boolean ITPolicyServiceColour, int userColour) {
      String itPolicyColours = ITPolicy.getString(24, 42);
      if (itPolicyColours != null) {
         try {
            if (!ITPolicyServiceColour) {
               itPolicyColours = itPolicyColours.trim();
               return Integer.parseInt(itPolicyColours.substring(itPolicyColours.length() - 6, itPolicyColours.length()), 16);
            }

            int separator = itPolicyColours.indexOf(59);
            if (separator == -1) {
               separator = itPolicyColours.indexOf(44);
            }

            if (separator >= 6) {
               return Integer.parseInt(itPolicyColours.substring(separator - 6, separator), 16);
            }
         } catch (IndexOutOfBoundsException var5) {
            return userColour;
         } catch (NumberFormatException var6) {
         }
      }

      return userColour;
   }

   public final int getSecurityITPolicyServiceColour() {
      return this.parseSecurityServiceColour(true, this._securityCache._securityITPolicyServiceColour);
   }

   public final int getSecurityOtherServiceColour() {
      return this.parseSecurityServiceColour(false, this._securityCache._securityOtherServiceColour);
   }

   public final boolean isAddressBookExcludedFromContentProtection() {
      if (!PersistentContent.isEncryptionEnabled()) {
         return true;
      } else {
         return ITPolicy.getBoolean(24, 55, false) ? false : this._securityCache._excludeAddressBookFromContentProtection;
      }
   }

   public final boolean getExcludeAddressBookFromContentProtection() {
      return this._securityCache._excludeAddressBookFromContentProtection;
   }

   public final void setExcludeAddressBookFromContentProtection(boolean excludeAddressBookFromContentProtection) {
      this._securityCache.setExcludeAddressBookFromContentProtection(excludeAddressBookFromContentProtection);
   }

   public final boolean isSmartPasswordEntryEnabledOnUserAuthenticatorPassword() {
      return this.getSmartPasswordEntry() && this._securityCache._numericUserAuthenticatorPassword;
   }

   public final boolean isSmartPasswordEntryEnabledOnHandheldPassword() {
      return this.getSmartPasswordEntry() && this._securityCache._numericHandheldPassword;
   }

   public final void setNumericPasswords(String handheldPassword, String userAuthenticatorPassword) {
      if (this.getSmartPasswordEntry()) {
         if (handheldPassword != null) {
            this._securityCache.setNumericHandheldPassword(this.isNumeric(handheldPassword));
         }

         if (userAuthenticatorPassword != null) {
            this._securityCache.setNumericUserAuthenticatorPassword(this.isNumeric(userAuthenticatorPassword));
         }
      }
   }

   private final boolean isNumeric(String password) {
      for (int i = 0; i < password.length(); i++) {
         int digit = Character.digit(password.charAt(i), 10);
         if (digit < 0) {
            return false;
         }
      }

      return true;
   }

   public final boolean getSmartPasswordEntry() {
      return ITPolicy.getBoolean(24, 62, false) ? false : this._securityCache._smartPasswordEntry;
   }

   public final void setSmartPasswordEntry(boolean smartPasswordEntry) {
      this._securityCache.setSmartPasswordEntry(smartPasswordEntry);
   }

   public final boolean cleanNow(int event) {
      ApplicationManager manager = ApplicationManager.getApplicationManager();
      if (event != 6 && (event != 3 || !manager.isSystemLocked())) {
         return false;
      }

      PersistentContentInternal.lock();
      return PersistentContentInternal.doesEncryptionKeyExist();
   }

   public final boolean activateLongTermTimeOut() {
      return !ITPolicy.getBoolean(14, false) ? false : System.currentTimeMillis() > this._securityCache._longTermSecurityTimeStamp;
   }

   public final boolean activatePasswordAging() {
      int maxPasswordAge = ITPolicy.getInteger(11, 0);
      long passwordEnableTimeStamp = this.getPasswordEnableTimeStamp();
      if (maxPasswordAge != 0 && passwordEnableTimeStamp != 0) {
         long days = (System.currentTimeMillis() - passwordEnableTimeStamp) / 86400000;
         return days >= maxPasswordAge;
      } else {
         return false;
      }
   }

   public final synchronized boolean registerUserAuthenticator(UserAuthenticator authenticator) {
      if (authenticator == null) {
         throw new IllegalArgumentException();
      }

      ApplicationControl.assertAuthenticatorApiAllowed(true);
      Class newAuthenticatorClass = authenticator.getClass();

      try {
         newAuthenticatorClass.newInstance();
         this._registeredUserAuthenticators.addElement(authenticator);
         return true;
      } catch (InstantiationException var4) {
      } catch (IllegalAccessException var5) {
      }

      throw new IllegalArgumentException();
   }

   public final UserAuthenticator getUserAuthenticator() {
      return this._userAuthenticator;
   }

   public final UserAuthenticator[] getRegisteredUserAuthenticators() {
      int size = this._registeredUserAuthenticators.size();
      UserAuthenticator[] result = new UserAuthenticator[size];
      this._registeredUserAuthenticators.copyInto(result);
      return result;
   }

   private final boolean isPhoneOff() {
      return !Phone.isSupported() || !Phone.getInstance().isActive();
   }

   public final boolean isLockRequired() {
      boolean passwordEnabled = this.isPasswordEnabled();
      if (passwordEnabled && this._lockOnIdle && DeviceInfo.getIdleTime() >= this.getTimeout() && this.isPhoneOff()) {
         return true;
      } else if (this._securityCache.getLockWhenHolstered() && DeviceInfo.isInHolster() && this.isPhoneOff()) {
         return true;
      } else {
         return passwordEnabled ? this.activateLongTermTimeOut() : false;
      }
   }

   public final boolean isAutoOnRequired() {
      return this._isAutoOnRequired;
   }

   public final void setAutoOnRequired(boolean required) {
      this._isAutoOnRequired = required;
   }

   public final int getUnlockCounter() {
      return this._unlockCounter;
   }

   public final void incrementUnlockCounter() {
      this._unlockCounter++;
   }

   public final int getLockCounter() {
      return this._lockCounter;
   }

   public final void incrementLockCounter() {
      this._lockCounter++;
   }

   private final void setPasswordEnableTimeStamp() {
      DataBuffer buff = new DataBuffer(true);
      DataBuffer timeStampBuffer = new DataBuffer(true);
      buff.writeLong(System.currentTimeMillis());
      TLEUtilities.writeDataField(timeStampBuffer, 1, buff.toArray());
      NvStore.writeData(39, timeStampBuffer.getArray());
   }

   private final long getPasswordEnableTimeStamp() {
      long result = 0;
      DataBuffer timestampBuffer = null;
      byte[] buffer = NvStore.readData(39);
      if (buffer != null) {
         timestampBuffer = new DataBuffer(true);
         timestampBuffer.setData(buffer, 0, buffer.length, true);
      }

      if (timestampBuffer != null) {
         try {
            while (!timestampBuffer.eof()) {
               int tag = TLEUtilities.getType(timestampBuffer);
               switch (tag) {
                  case 1:
                     timestampBuffer.readCompressedInt();
                     timestampBuffer.readCompressedInt();
                     result = timestampBuffer.readLong();
                     break;
                  default:
                     TLEUtilities.skipField(timestampBuffer);
               }
            }
         } catch (EOFException var6) {
         }
      }

      return result;
   }

   public final boolean verifyPassword(String password) {
      return this.verifyPasswordInternal(password == null ? null : password.getBytes());
   }

   private final native boolean verifyPasswordInternal(byte[] var1);

   public final boolean setPassword(String oldPassword, String newPassword) {
      return this.setPasswordInternal(oldPassword == null ? null : oldPassword.getBytes(), newPassword == null ? null : newPassword.getBytes());
   }

   private final native boolean setPasswordInternal(byte[] var1, byte[] var2);

   public final native int getPasswordFailureCount();

   public final native void deviceUnderAttack();

   public final native boolean resetPassword();
}
