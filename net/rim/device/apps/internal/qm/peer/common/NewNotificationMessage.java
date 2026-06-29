package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.internal.qm.resource.QmResources;

public class NewNotificationMessage extends NotificationMessage {
   private Object _message;
   public static final String _buzzString = QmResources.getString(9);

   public NewNotificationMessage(Conversation conversation, Contact contact, String message) {
      super(conversation, contact);
      this._message = PersistentContent.encode(message, true, true);
   }

   public void lock() {
      this._message = PersistentContent.reEncode(this._message, true, true);
   }

   String getMessage() {
      try {
         String message = PersistentContent.decodeString(this._message);
         if (message.equals("<ding>")) {
            message = _buzzString;
         }

         return message;
      } finally {
         ;
      }
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof NewNotificationMessage ? super.equals(obj) : false;
   }

   @Override
   public String toString() {
      String displayName = this.getContact().getDisplayName();
      return QmResources.format(46, displayName == null ? this.getContact().getId() : displayName, this.getMessage());
   }
}
