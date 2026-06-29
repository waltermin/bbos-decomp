package net.rim.device.internal.system;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;

class Security$SecurityCache implements Persistable {
   private int _maxPasswordAttempts = NvStore.readInt(9, 3);
   private int _currentTimeOut = 120;
   private long _longTermSecurityTimeStamp;
   private boolean _passwordRequiredForAppInstall = ITPolicy.getBoolean(24, 75, false);
   private boolean _lockWhenHolstered = ITPolicy.getBoolean(24, 12, false);
   private boolean _autoFillUserAuthenticatorPasswordField;
   private boolean _allowOutgoingCallWhileLocked;
   private boolean _numericUserAuthenticatorPassword;
   private boolean _numericHandheldPassword;
   private boolean _smartPasswordEntry = !ITPolicy.getBoolean(24, 62, false);
   private int _securityITPolicyServiceColour = -1;
   private int _securityOtherServiceColour = -1;
   private boolean _excludeAddressBookFromContentProtection = false;

   private Security$SecurityCache() {
   }

   private void setMaxPasswordAttempts(int maxAttempts) {
      this._maxPasswordAttempts = maxAttempts;
      this.commit();
   }

   private void markLongTermTimeOutTimeStamp() {
      if (ITPolicy.getBoolean(14, false)) {
         int longTermTimeout = ITPolicy.getInteger(22, 8, 60) * 60 * 1000;
         this._longTermSecurityTimeStamp = System.currentTimeMillis() + longTermTimeout;
         this.commit();
      }
   }

   private int getCurrentTimeOut() {
      return Math.min(this._currentTimeOut, ITPolicy.getInteger(10, 60) * 60);
   }

   private boolean setCurrentTimeOut(int seconds) {
      int maxTimeout = ITPolicy.getInteger(10, 60) * 60;
      if (seconds > 0 && seconds <= maxTimeout) {
         this._currentTimeOut = seconds;
         this.commit();
         return true;
      } else {
         return false;
      }
   }

   private boolean getLockWhenHolstered() {
      return this._lockWhenHolstered || ITPolicy.getBoolean(24, 12, false);
   }

   private void setLockWhenHolstered(boolean lockWhenHolstered) {
      this._lockWhenHolstered = lockWhenHolstered || ITPolicy.getBoolean(24, 12, false);
      this.commit();
   }

   private boolean getPasswordRequiredForAppInstall() {
      return this._passwordRequiredForAppInstall || ITPolicy.getBoolean(24, 75, false);
   }

   private void setPasswordRequiredForAppInstall(boolean passwordRequiredForAppInstall) {
      this._passwordRequiredForAppInstall = passwordRequiredForAppInstall || ITPolicy.getBoolean(24, 75, false);
      this.commit();
   }

   private boolean getAllowOutgoingCallWhileLocked() {
      return this._allowOutgoingCallWhileLocked;
   }

   private void setAllowOutgoingCallWhileLocked(boolean allowOutgoingCall) {
      this._allowOutgoingCallWhileLocked = allowOutgoingCall;
      this.commit();
   }

   private void setAutoFillUserAuthenticatorPasswordField(boolean autoFill) {
      this._autoFillUserAuthenticatorPasswordField = autoFill;
      this.commit();
   }

   private void setSecurityServiceColours(int ITPolicyServiceColour, int otherServiceColour) {
      this._securityITPolicyServiceColour = ITPolicyServiceColour;
      this._securityOtherServiceColour = otherServiceColour;
      this.commit();
   }

   private void setExcludeAddressBookFromContentProtection(boolean excludeAddressBookFromContentProtection) {
      this._excludeAddressBookFromContentProtection = excludeAddressBookFromContentProtection;
      this.commit();
   }

   private void setNumericUserAuthenticatorPassword(boolean numericUserAuthenticatorPassword) {
      this._numericUserAuthenticatorPassword = numericUserAuthenticatorPassword;
      this.commit();
   }

   private void setNumericHandheldPassword(boolean numericHandheldPassword) {
      this._numericHandheldPassword = numericHandheldPassword;
      this.commit();
   }

   private void setSmartPasswordEntry(boolean smartPasswordEntry) {
      this._smartPasswordEntry = smartPasswordEntry;
      if (!this._smartPasswordEntry) {
         this._numericUserAuthenticatorPassword = false;
         this._numericHandheldPassword = false;
      }

      this.commit();
   }

   private void commit() {
      PersistentObject store = RIMPersistentStore.getPersistentObject(5031265368654170436L);
      synchronized (store) {
         store.setContents(this, 51);
         store.commit();
      }
   }

   Security$SecurityCache(Security$1 x0) {
      this();
   }
}
