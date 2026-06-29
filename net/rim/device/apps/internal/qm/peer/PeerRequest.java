package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.internal.qm.peer.common.Request;

class PeerRequest implements Request {
   int _hashId;
   Object _name;
   Object _body;
   private boolean _read;
   IntHashtable _persistentData;
   static final int TYPE_KEY;
   static final int TYPE_NEW_CONTACT_REQUEST;
   static final int TYPE_WRONG_PASSCODE_REQUEST;
   static final int TYPE_NAME;
   static final int TYPE_BODY;
   static final int TYPE_INVITE;
   static final int TYPE_ID;
   static final int TYPE_VERSION;
   static final String VERSION;

   PeerRequest(int type, String replyTo, String body, IntHashtable inviteData) {
      this._hashId = replyTo.toUpperCase().hashCode();
      this._name = PersistentContent.encode(Utils.resolveName(replyTo), true, true);
      this._body = PersistentContent.encode(body == null ? "" : body, true, true);
      this._persistentData = (IntHashtable)(new Object(6));
      this._persistentData.put(9, "1.1.0");
      this._persistentData.put(1, new Object(type));
      this._persistentData.put(8, new Object(this._hashId));
      this._persistentData.put(5, this._name);
      this._persistentData.put(6, this._body);
      if (inviteData != null) {
         this._persistentData.put(7, inviteData);
      }
   }

   PeerRequest(IntHashtable initialData) {
      this._persistentData = initialData;
      this._hashId = this._persistentData.get(8);
      this._name = this._persistentData.get(5);
      this._body = this._persistentData.get(6);
   }

   public IntHashtable getPersistentData() {
      return this._persistentData;
   }

   int getRequestId() {
      return this._hashId;
   }

   public int getIconId() {
      return 9;
   }

   public void markRead(boolean read) {
      this._read = read;
   }

   public boolean isRead() {
      return this._read;
   }

   void lock() {
      this._name = PersistentContent.reEncode(this._name, true, true);
      this._body = PersistentContent.reEncode(this._body, true, true);
   }

   public String getName() {
      try {
         return PersistentContent.decodeString(this._name);
      } finally {
         ;
      }
   }

   @Override
   public String toString() {
      return this.getName();
   }

   public void accept() {
      throw null;
   }

   public void decline() {
      throw null;
   }

   public String getBody() {
      throw null;
   }
}
