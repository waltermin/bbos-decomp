package net.rim.wica.runtime.persistence.internal;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.persistence.CredentialStore;

public class CredentialStoreImpl implements CredentialStore {
   private LongSubstore _credentialsSubstore = new LongSubstore(2027130143323582880L);
   private static final long KEY_CREDENTIALS;

   CredentialStoreImpl() {
   }

   @Override
   public void storeCredentials(long id, Persistable[] credentials) {
      ObjectGroup.createGroupIgnoreTooBig(credentials);
      this._credentialsSubstore.put(id, credentials);
   }

   @Override
   public Persistable[] loadCredentials(long id) {
      return (Object[])this._credentialsSubstore.get(id);
   }

   @Override
   public Persistable[] loadCredentialsForWrite(long id) {
      Object o = this._credentialsSubstore.get(id);
      Persistable[] credentials = o != null && ObjectGroup.isInGroup(o) ? (Object[])ObjectGroup.expandGroup(o) : (Object[])o;
      return credentials;
   }

   @Override
   public void deleteCredentials(long id) {
      this._credentialsSubstore.remove(id);
   }

   @Override
   public void deleteAllCredentials() {
      this._credentialsSubstore.wipe();
   }

   static void wipe() {
      PersistentStore.destroyPersistentObject(2027130143323582880L);
   }
}
