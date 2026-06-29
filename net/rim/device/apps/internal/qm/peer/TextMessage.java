package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.MessengerContact;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class TextMessage extends MessengerMessageImpl {
   private Object _text;
   private int _state;
   private MessengerContact _recipient;
   static final int TEXT = 4;
   static final int STATE = 5;
   public static final String _buzzString = QmResources.getString(9);

   TextMessage(String text, MessengerContact recipient) {
      super(null);
      this._recipient = recipient;
      this._state = 7;
      super._serialized.put(5, new Object(this._state));
      this._text = PersistentContent.encode(text, true, true);
      super._serialized.put(4, this._text);
      super._serialized.put(0, new Object(0));
      this.commit();
   }

   TextMessage(MessengerContact contact, String text) {
      super(contact);
      this._recipient = null;
      super._isSystem = contact == null;
      super._serialized.put(2, new Object(super._isSystem));
      this._text = PersistentContent.encode(text, true, true);
      super._serialized.put(4, this._text);
      super._serialized.put(0, new Object(0));
      this.commit();
   }

   protected TextMessage(IntHashtable message, PeerContactListCollection contactList) {
      super(message, contactList);
      this._text = message.get(4);
      Object state = message.get(5);
      if (state instanceof Object) {
         this._state = state;
      }
   }

   final void setState(int state) {
      this._state = state;
      super._serialized.put(5, new Object(this._state));
      this.commit();
   }

   final int getState() {
      return this._state;
   }

   final MessengerContact getRecipient() {
      return this._recipient;
   }

   @Override
   public final String getText() {
      String text = null;

      try {
         text = PersistentContent.decodeString(this._text);
      } finally {
         ;
      }

      if (text.equals("<ding>")) {
         return _buzzString;
      }

      if (text.startsWith("<HTML")) {
         text = this.ParseMarkup(text);
      }

      return text;
   }

   @Override
   public final String toString() {
      return this.getText();
   }

   final String ParseMarkup(String text) {
      int length = text.length();
      char[] in = new char[length];
      text.getChars(0, length, in, 0);
      StringBuffer result = (StringBuffer)(new Object(text.length()));
      int i = 0;

      while (i < length) {
         int start = text.indexOf(60, i);
         if (start != i) {
            result.append(in, i, start - i);
         }

         int end = text.indexOf(62, start);
         i = end + 1;
      }

      return result.toString();
   }

   @Override
   public final Field getField(Object context) {
      return new TextMessageField(this);
   }

   @Override
   public final int getOrder(Object context) {
      return 0;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      return false;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return field instanceof TextMessageField;
   }

   final void lock() {
      if (super._serialized != null) {
         this._text = PersistentContent.reEncode(this._text);
         lock(super._serialized);
      }
   }

   static final void lock(IntHashtable message) {
      message.put(4, PersistentContent.reEncode(message.get(4), true, true));
   }

   static final MessengerMessageImpl deserialize(IntHashtable message, PeerContactListCollection contactList) {
      return message instanceof Object ? new TextMessage(message, contactList) : null;
   }
}
