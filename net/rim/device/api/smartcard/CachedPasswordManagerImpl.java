package net.rim.device.api.smartcard;

import java.util.Hashtable;
import net.rim.device.api.crypto.keystore.KeyStoreOptions;
import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.internal.applicationcontrol.ApplicationControl;

final class CachedPasswordManagerImpl extends CachedPasswordManager implements MemoryCleanerListener {
   private Hashtable _cachedPasswordsHashtable;
   private boolean _memoryCleanerListenerAdded = false;

   public CachedPasswordManagerImpl() {
      this._cachedPasswordsHashtable = (Hashtable)(new Object());
   }

   @Override
   public final synchronized void put(SmartCardID smartCardID, String password) {
      if (this.verifyAccess()) {
         if (!this._memoryCleanerListenerAdded) {
            this._memoryCleanerListenerAdded = true;
            MemoryCleanerDaemon.addListener(this);
         }

         this._cachedPasswordsHashtable.put(smartCardID, new CachedPasswordManagerImpl$CachedObject(password));
      }
   }

   @Override
   public final synchronized String get(SmartCardID smartCardID) {
      Object ticket = PersistentContent.getTicket();
      if (ticket == null) {
         this.cleanNow(0);
         return null;
      }

      if (!this.verifyAccess()) {
         return null;
      }

      CachedPasswordManagerImpl$CachedObject cachedObject = (CachedPasswordManagerImpl$CachedObject)this._cachedPasswordsHashtable.get(smartCardID);
      if (cachedObject != null) {
         long passphraseTimeout = KeyStoreOptions.getPassphraseTimeout();
         if (System.currentTimeMillis() - cachedObject.getTimeLastAccessed() >= passphraseTimeout) {
            this._cachedPasswordsHashtable.remove(smartCardID);
            return null;
         } else {
            cachedObject.setTimeLastAccessed();
            return cachedObject.getPassword();
         }
      } else {
         return null;
      }
   }

   private final boolean verifyAccess() {
      if (!SmartCardOptions.getInstance().getAllowPINCaching()) {
         this.cleanNow(0);
         return false;
      } else {
         return ApplicationControl.isKeyStoreMediumSecurityAllowed(true);
      }
   }

   @Override
   public final synchronized boolean cleanNow(int event) {
      if (this._cachedPasswordsHashtable.size() == 0) {
         return false;
      }

      this._cachedPasswordsHashtable.clear();
      return true;
   }

   @Override
   public final String getDescription() {
      return null;
   }
}
