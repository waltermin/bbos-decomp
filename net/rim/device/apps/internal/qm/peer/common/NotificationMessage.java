package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.FieldProvider;

public class NotificationMessage implements FieldProvider {
   protected Contact _contact;
   protected Conversation _conversation;
   private boolean _valid;

   protected NotificationMessage(Conversation conversation, Contact contact) {
      this._conversation = conversation;
      this._contact = contact;
      this._valid = true;
   }

   public void setValid(boolean valid) {
      this._valid = valid;
   }

   public boolean isValid() {
      return this._valid;
   }

   public Conversation getConversation() {
      return this._conversation;
   }

   public Contact getContact() {
      return this._contact;
   }

   @Override
   public Field getField(Object context) {
      return new NotificationMessageField(18014398509482048L, this);
   }

   @Override
   public int getOrder(Object context) {
      return 0;
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof NotificationMessage) {
         Conversation conversation = ((NotificationMessage)obj).getConversation();
         Contact contact = ((NotificationMessage)obj).getContact();
         if (this._conversation != null && this._contact != null && this._conversation.equals(conversation) && this._contact.equals(contact)
            || this._conversation == conversation && this._contact == contact) {
            return true;
         }
      }

      return false;
   }
}
