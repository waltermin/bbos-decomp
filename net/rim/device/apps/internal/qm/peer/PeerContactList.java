package net.rim.device.apps.internal.qm.peer;

import java.util.Vector;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.internal.qm.peer.common.ContactList;

public final class PeerContactList implements ContactList {
   private int _idHash;
   private Object _displayName;
   private IntHashtable _persistentData;
   static final int NAME_ID;
   static final int HASH_ID;
   static final int CONTACTS_ID;

   public PeerContactList(String displayName) {
      this._persistentData = (IntHashtable)(new Object());
      this.setDisplayName(displayName);
      this._persistentData.put(5, new Object());
      this.commit();
   }

   PeerContactList(IntHashtable persistedData) {
      this._persistentData = persistedData;
      this._displayName = this._persistentData.get(1);
      this.initIdHash(this._persistentData.get(3));
   }

   private final void initIdHash(Object obj) {
      if (obj instanceof Object) {
         this._idHash = obj;
      } else {
         if (!PeerEntry.getInstance().isDeviceLocked()) {
            this._idHash = this.getDisplayName().hashCode();
            this._persistentData.put(3, new Object(this._idHash));
            this.commit();
         }
      }
   }

   final void addContact(PeerContact contact) {
      ((Vector)this._persistentData.get(5)).addElement(contact.getPersistentData());
      this.commit();
   }

   final void removeContact(PeerContact contact) {
      int hash = contact.getIdHash();
      Vector contacts = (Vector)this._persistentData.get(5);
      int size = contacts.size();

      for (int index = 0; index < size; index++) {
         IntHashtable current = (IntHashtable)contacts.elementAt(index);
         if (hash == current.get(17)) {
            contacts.removeElementAt(index);
            this.commit();
            return;
         }
      }
   }

   final int getContactsCount() {
      return ((Vector)this._persistentData.get(5)).size();
   }

   final IntHashtable getPersistentData() {
      return this._persistentData;
   }

   public final int getIdHash() {
      return this._idHash;
   }

   final void setDisplayName(String displayName) {
      this._displayName = PersistentContent.reEncode(displayName, true, true);
      this._persistentData.put(1, this._displayName);
      this._idHash = displayName.hashCode();
      this._persistentData.put(3, new Object(this._idHash));
      this.commit();
   }

   public final String getDisplayName() {
      try {
         return PersistentContent.decodeString(this._displayName);
      } finally {
         ;
      }
   }

   @Override
   public final String toString() {
      return this.getDisplayName();
   }

   final void lock() {
      this._displayName = PersistentContent.reEncode(this._displayName, true, true);
      this._persistentData.put(1, this._displayName);
      this.commit();
   }

   private final void commit() {
      PersistentObject.commit(this._persistentData);
   }
}
