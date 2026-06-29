package net.rim.device.api.crypto.keystore;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;

public class PersistableRIMKeyStore extends RIMKeyStore {
   protected PersistentObject _persist;

   public PersistableRIMKeyStore(String name, long id, CodeSigningKey key, PersistableRIMKeyStoreFactory factory) {
      this(name, id, key, factory, null);
   }

   public PersistableRIMKeyStore(String name, long id, CodeSigningKey key, PersistableRIMKeyStoreFactory factory, KeyStore keyStore) {
      super(name, factory.getClass().getName(), id, key, keyStore, (Vector)getPersistentObject(id, key).getContents());
      this._persist = getPersistentObject(id, key);
   }

   private static PersistentObject getPersistentObject(long id, CodeSigningKey key) {
      PersistentObject persist = RIMPersistentStore.getPersistentObject(id);
      synchronized (persist) {
         if (persist.getContents() == null) {
            persist.setContents(new ControlledAccess(new Vector(), key));
            persist.commit();
         }

         return persist;
      }
   }

   @Override
   public void removeKey(KeyStoreData data, KeyStoreTicket ticket) {
      super.removeKey(data, ticket);
      this._persist.commit();
   }

   @Override
   public boolean addIndex(KeyStoreIndex index) {
      if (super.addIndex(index)) {
         this._persist.commit();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void removeIndex(long index) {
      super.removeIndex(index);
      this._persist.commit();
   }

   @Override
   public void changePassword() {
      PrivateKeysKeyStoreIndex index = new PrivateKeysKeyStoreIndex();
      this.addIndex(index);
      Enumeration enumeration = this.elements(index.getID());

      while (enumeration.hasMoreElements()) {
         RIMKeyStoreData data = (RIMKeyStoreData)enumeration.nextElement();
         this.ungroupData(data);
         data.changePassword();
         this.groupData(data);
      }
   }

   @Override
   void groupData(RIMKeyStoreData data) {
      super.groupData(data);
      this._persist.commit();
   }
}
