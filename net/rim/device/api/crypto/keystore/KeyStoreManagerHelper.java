package net.rim.device.api.crypto.keystore;

import java.util.Hashtable;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.system.FIPSPolicy;
import net.rim.vm.Array;

final class KeyStoreManagerHelper implements Persistable, SyncObject {
   private byte[] _hash;
   private int _uid = 1;
   private long _passphraseTimeout;
   private long _lastLoginTime;
   private int _passwordAttempts = 1;
   private int _unused;
   private boolean _promptForStatus;
   private Hashtable _keyStoreHashtable;
   private Hashtable _statusHashtable;
   private LongHashtable _codeSigningHashtable;
   private int _timerID;
   private int _passwordVersion;
   private long _staleTime;
   private Hashtable _syncedWithBESHashtable;
   private boolean _allowBackupRestore;
   private boolean _allowUnverifiedCRLs;
   private boolean _keyStoreAddressInjectorEnabled;
   private String _certificateServiceUID;
   private byte[][] _passwordHistory;
   private static final long KEY_STORE_MANAGER_HELPER = 4177297936493411938L;
   private static PersistentObject _persist = RIMPersistentStore.getPersistentObject(4177297936493411938L);
   private static boolean _access;
   private static boolean _backupFlag;
   private static KeyStoreManagerHelper _helper;
   private static final long SINGLETON_ID = -7560559415799202491L;

   final void resetOptions() {
      this._passphraseTimeout = 60000;
      this._allowBackupRestore = true;
      this._staleTime = 14400000;
      this._allowUnverifiedCRLs = true;
      this._keyStoreAddressInjectorEnabled = false;
      _persist.commit();
   }

   public final void setHash(byte[] hash) {
      this._hash = hash;
      _persist.commit();
   }

   public final void setPassphraseTimeout(long timeout) {
      this._passphraseTimeout = timeout;
      _persist.commit();
      KeyStoreUtilitiesInternal.setTimeoutReminder();
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   public final void setKeyStoreHashtable(Hashtable keyStoreHashtable) {
      this._keyStoreHashtable = keyStoreHashtable;
      _persist.commit();
   }

   public final void setSyncedWithBESHashtable(Hashtable syncedWithBESHashtable) {
      this._syncedWithBESHashtable = syncedWithBESHashtable;
      _persist.commit();
   }

   public final byte[] getHash() {
      return this._hash;
   }

   public final Hashtable getKeyStoreHashtable() {
      if (this._keyStoreHashtable == null) {
         this._keyStoreHashtable = new Hashtable();
         _persist.commit();
      }

      return this._keyStoreHashtable;
   }

   public final Hashtable getSyncedWithBESHashtable() {
      if (this._syncedWithBESHashtable == null) {
         this._syncedWithBESHashtable = new Hashtable();
         _persist.commit();
      }

      return this._syncedWithBESHashtable;
   }

   public final long getPassphraseTimeout() {
      int keyStorePasswordMaxTimeout = ITPolicy.getInteger(24, 8, 60) * 60 * 1000;
      return Math.min(this._passphraseTimeout, keyStorePasswordMaxTimeout);
   }

   public final long getStaleTime() {
      if (ITPolicy.getBoolean(24, 57, false)) {
         return Long.MAX_VALUE;
      }

      int staleMaxTimeout = ITPolicy.getInteger(24, 33, -1);
      long convertedStaleMaxTimeout = Long.MAX_VALUE;
      if (staleMaxTimeout >= 0) {
         convertedStaleMaxTimeout = (long)staleMaxTimeout * 60 * 60 * 1000;
      }

      return Math.min(this._staleTime, convertedStaleMaxTimeout);
   }

   public final void setStaleTime(long staleTime) {
      this._staleTime = staleTime;
      _persist.commit();
   }

   public final boolean isPassphraseSet() {
      return this._hash != null;
   }

   public final void setLastTime(long lastTime) {
      this._lastLoginTime = lastTime;
      _persist.commit();
   }

   public final long getLastTime() {
      return this._lastLoginTime;
   }

   public final int getPasswordAttempts() {
      return this._passwordAttempts;
   }

   public final void setPasswordAttempts(int passwordAttempts) {
      this._passwordAttempts = passwordAttempts;
      _persist.commit();
   }

   public final int getPasswordAttemptsThreshold() {
      return ITPolicy.getInteger(22, 2, 10);
   }

   public final void incrementPasswordAttempts() {
      this._passwordAttempts++;
      _persist.commit();
   }

   public final void setAccess(boolean access) {
      _access = access;
   }

   public final boolean getAccess() {
      return _access;
   }

   public final Hashtable getStatusHashtable() {
      return this._statusHashtable;
   }

   public final void setStatusHashtable(Hashtable statusHashtable) {
      this._statusHashtable = statusHashtable;
      _persist.commit();
   }

   public final boolean getBackupFlag() {
      return _backupFlag;
   }

   public final void setBackupFlag(boolean flag) {
      _backupFlag = flag;
   }

   public final boolean getPromptForStatus() {
      return this._promptForStatus;
   }

   public final void setPromptForStatus(boolean promptForStatus) {
   }

   public final boolean getAllowBackupRestore() {
      boolean disallowBackup = ITPolicy.getBoolean(24, 32, false);
      return !disallowBackup && this._allowBackupRestore;
   }

   public final void setAllowBackupRestore(boolean allow) {
      this._allowBackupRestore = allow;
      _persist.commit();
   }

   public final boolean getAllowUnverifiedCRLs() {
      boolean disableUnverifiedCRL = ITPolicy.getBoolean(24, 41, false);
      return !disableUnverifiedCRL && this._allowUnverifiedCRLs;
   }

   public final void setAllowUnverifiedCRLs(boolean allow) {
      this._allowUnverifiedCRLs = allow;
      _persist.commit();
   }

   public final boolean getKeyStoreAddressInjectorEnabled() {
      return this._keyStoreAddressInjectorEnabled;
   }

   public final void setKeyStoreAddressInjectorEnabled(boolean enabled) {
      this._keyStoreAddressInjectorEnabled = enabled;
      _persist.commit();
   }

   public final String getCertificateServiceUID() {
      return this._certificateServiceUID;
   }

   public final void setCertificateServiceUID(String certificateServiceUID) {
      this._certificateServiceUID = certificateServiceUID;
      _persist.commit();
   }

   public final int getRevealPasswordAttempts() {
      return FIPSPolicy.getBoolean(22, 3, false, true) ? this.getPasswordAttemptsThreshold() + 1 : this.getPasswordAttemptsThreshold() >> 1;
   }

   public final int getTimerID() {
      return this._timerID;
   }

   public final void setTimerID(int timerID) {
      this._timerID = timerID;
   }

   public final LongHashtable getCodeSigningHashtable() {
      if (this._codeSigningHashtable == null) {
         this._codeSigningHashtable = new LongHashtable();
         _persist.commit();
      }

      return this._codeSigningHashtable;
   }

   public final void setCodeSigningHashtable(LongHashtable table) {
      this._codeSigningHashtable = table;
      _persist.commit();
   }

   public final void setPasswordVersion(int version) {
      this._passwordVersion = version;
      _persist.commit();
   }

   public final int getPasswordVersion() {
      return this._passwordVersion;
   }

   public final boolean checkPasswordHistory(byte[] password) {
      if (password == null) {
         throw new IllegalArgumentException();
      }

      if (this._passwordHistory == null) {
         return false;
      }

      byte[] passwordHash = KeyStoreUtilitiesInternal.computeHash(password);
      int historyLength = this._passwordHistory.length;

      for (int i = 0; i < historyLength; i++) {
         if (Arrays.equals(this._passwordHistory[i], passwordHash)) {
            return true;
         }
      }

      return false;
   }

   public final void addPasswordToHistory(byte[] password) {
      byte[] passwordHash = KeyStoreUtilitiesInternal.computeHash(password);
      int numPasswords = ITPolicy.getInteger(22, 4, 0);
      if (numPasswords > 0) {
         if (this._passwordHistory == null) {
            this._passwordHistory = new byte[1][];
            this._passwordHistory[0] = passwordHash;
            return;
         }

         int historyLength = this._passwordHistory.length;
         if (historyLength < numPasswords) {
            Array.resize(this._passwordHistory, historyLength + 1);
            this._passwordHistory[historyLength] = passwordHash;
         } else if (historyLength == numPasswords) {
            for (int i = 0; i < historyLength - 1; i++) {
               this._passwordHistory[i] = this._passwordHistory[i + 1];
            }

            this._passwordHistory[historyLength - 1] = passwordHash;
         } else {
            if (historyLength <= numPasswords) {
               throw new RuntimeException();
            }

            byte[][] newHistory = new byte[numPasswords][];
            int i = 0;

            for (int j = historyLength - numPasswords + 1; i < numPasswords - 1; j++) {
               newHistory[i] = this._passwordHistory[j];
               i++;
            }

            newHistory[numPasswords - 1] = passwordHash;
            this._passwordHistory = newHistory;
         }

         _persist.commit();
      }
   }

   final synchronized boolean checkPasswordEquals(byte[] hash) {
      if (Arrays.equals(hash, this._hash)) {
         this._lastLoginTime = System.currentTimeMillis();
         this._passwordAttempts = 1;
         _persist.commit();
         return true;
      }

      this._passwordAttempts++;
      _persist.commit();

      try {
         Thread.sleep(1000);
         return false;
      } finally {
         ;
      }
   }

   public static final KeyStoreManagerHelper getInstance() {
      return _helper;
   }

   private KeyStoreManagerHelper() {
   }

   static {
      synchronized (_persist) {
         if (_persist.getContents() == null) {
            KeyStoreManagerHelper helper = new KeyStoreManagerHelper();
            _persist.setContents(helper, 4801362);
            helper.resetOptions();
         }
      }

      _helper = (KeyStoreManagerHelper)_persist.getContents();
   }
}
