package net.rim.device.apps.internal.ldap;

import java.util.Hashtable;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;

public final class LDAPBrowserOptionStore implements Persistable {
   private Hashtable _optionData = (Hashtable)(new Object());
   private static final long KEY;
   private static final long SYNC_ID;
   private static PersistentObject _persist = RIMPersistentStore.getPersistentObject(-6129006259646707626L);
   public static final int YES;
   public static final int NO;
   public static final int PROMPT;

   private LDAPBrowserOptionStore() {
      LDAPBrowserOptionStore$LDAPOptionsSyncItem ldapOptionsSyncItem = this.getLDAPOptionsSyncItem();
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(ldapOptionsSyncItem);
      }
   }

   private final LDAPBrowserOptionStore$LDAPOptionsSyncItem getLDAPOptionsSyncItem() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      LDAPBrowserOptionStore$LDAPOptionsSyncItem ldapOptionsSyncItem = (LDAPBrowserOptionStore$LDAPOptionsSyncItem)registry.getOrWaitFor(7515256977697666692L);
      if (ldapOptionsSyncItem == null) {
         ldapOptionsSyncItem = new LDAPBrowserOptionStore$LDAPOptionsSyncItem(this);
         registry.put(7515256977697666692L, ldapOptionsSyncItem);
      }

      return ldapOptionsSyncItem;
   }

   public static final LDAPBrowserOptionStore getInstance() {
      return (LDAPBrowserOptionStore)_persist.getContents();
   }

   public final int getFetchCertStatus(String context) {
      return this.getOptionData(context)._fetchCertStatus;
   }

   public final boolean getPromptForCertLabel(String context) {
      return this.getOptionData(context)._promptForCertLabel;
   }

   private final void commit() {
      _persist.commit();
      this.getLDAPOptionsSyncItem().fireSyncItemUpdated();
   }

   public final void setFetchCertStatus(String context, int fetchCertStatus) {
      this.getOptionData(context)._fetchCertStatus = fetchCertStatus;
      this.commit();
   }

   public final void setPromptForCertLabel(String context, boolean promptForCertLabel) {
      this.getOptionData(context)._promptForCertLabel = promptForCertLabel;
      this.commit();
   }

   private final LDAPBrowserOptionStore$LDAPOptionData getOptionData(String context) {
      if (context == null) {
         throw new Object();
      }

      LDAPBrowserOptionStore$LDAPOptionData optionData = (LDAPBrowserOptionStore$LDAPOptionData)this._optionData.get(context);
      if (optionData == null) {
         optionData = new LDAPBrowserOptionStore$LDAPOptionData(this);
         this._optionData.put(context, optionData);
      }

      return optionData;
   }

   static {
      synchronized (_persist) {
         if (_persist.getContents() == null) {
            _persist.setContents(new LDAPBrowserOptionStore(), 51);
            _persist.commit();
         }
      }
   }
}
