package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.MessengerContact;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.model.FieldProvider;

class MessengerMessageImpl implements MessengerMessage, FieldProvider {
   private MessengerContact _contact;
   private long _time;
   protected boolean _isSystem;
   IntHashtable _serialized;
   static final int MESSAGE_TYPE = 0;
   static final int CONTACT_ID_HASH = 1;
   static final int SYSTEM = 2;
   static final int TIME = 3;
   static final int TEXT_MESSAGE = 0;
   static final int FILE_MESSAGE = 1;
   static final int VCARD_MESSAGE = 2;

   protected void commit() {
      PersistentObject.commit(this._serialized);
   }

   public IntHashtable getPersistentData() {
      return this._serialized;
   }

   @Override
   public String getSender() {
      if (this._contact != null) {
         return this._contact.getDisplayName();
      } else {
         return !this._isSystem ? PeerApplication.getSession().getDisplayName() : null;
      }
   }

   @Override
   public long getTime() {
      return this._time;
   }

   @Override
   public boolean isSystem() {
      return this._isSystem;
   }

   @Override
   public boolean isIncoming() {
      return this._contact != null && !this.isSystem();
   }

   @Override
   public Field getField(Object _1) {
      throw null;
   }

   @Override
   public String getText() {
      throw null;
   }

   @Override
   public boolean validate(Field _1, Object _2) {
      throw null;
   }

   @Override
   public boolean grabDataFromField(Field _1, Object _2) {
      throw null;
   }

   @Override
   public int getOrder(Object _1) {
      throw null;
   }

   MessengerMessageImpl(MessengerContact contact) {
      this._serialized = new IntHashtable();
      if (contact != null) {
         this._contact = contact;
         this._serialized.put(1, new Integer(this._contact.getContactId()));
      }

      this._time = System.currentTimeMillis();
      this._serialized.put(3, new Long(this._time));
   }

   static MessengerMessageImpl deserialize(IntHashtable data, PeerContactListCollection contactList) {
      MessengerMessageImpl message = null;
      Object obj = data.get(0);
      if (obj instanceof Integer) {
         int type = (Integer)obj;
         switch (type) {
            case -1:
               break;
            case 0:
            default:
               return TextMessage.deserialize(data, contactList);
            case 1:
               return FileMessage.deserialize(data, contactList);
            case 2:
               message = VCardMessage.deserialize(data, contactList);
         }
      }

      return message;
   }

   static void lock(IntHashtable message) {
      switch (message.get(0)) {
         case 0:
         default:
            TextMessage.lock(message);
            return;
         case 1:
            FileMessage.lock(message);
            return;
         case 2:
            VCardMessage.lock(message);
         case -1:
      }
   }

   MessengerMessageImpl(IntHashtable message, PeerContactListCollection contactList) {
      this._serialized = message;
      Object idHash = message.get(1);
      if (idHash instanceof Integer) {
         this._contact = contactList.findContactByHashId((Integer)idHash);
      }

      this._time = (Long)message.get(3);
      Object system = message.get(2);
      if (system instanceof Boolean) {
         this._isSystem = (Boolean)system;
      }
   }
}
