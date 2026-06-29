package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.MessengerContact;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.internal.io.file.FileUtilities;

class FileMessage extends MessengerMessageImpl implements FieldProvider, ConversionProvider, CloneProvider {
   private Object _contentType;
   private Object _filename;
   protected byte[] _data;
   private int _size;
   int _state;
   static final int CONTENT_TYPE;
   static final int FILE_NAME;
   static final int FILE_DATA;
   static final int FILE_SIZE;
   static final int FILE_STATE;
   static final int PENDING;
   static final int ADDED;
   static final int REMOVED;
   static final int ERROR;

   FileMessage(MessengerContact contact, String contentType, byte[] data, String filename, int size) {
      super(contact);
      this._contentType = PersistentContent.encode(contentType, true, true);
      this._filename = PersistentContent.encode(filename, true, true);
      this._data = data;
      this._size = size;
      super._serialized.put(0, new Object(1));
      super._serialized.put(4, this._contentType);
      super._serialized.put(5, this._filename);
      super._serialized.put(6, this._data);
      super._serialized.put(7, new Object(this._size));
      this.commit();
   }

   protected FileMessage(IntHashtable message, PeerContactListCollection contactList) {
      super(message, contactList);
      this._contentType = message.get(4);
      this._filename = message.get(5);
      Object obj = message.get(6);
      if (obj instanceof byte[]) {
         this._data = (byte[])obj;
      }

      obj = message.get(7);
      if (obj instanceof Object) {
         this._size = obj;
      }

      obj = message.get(8);
      if (obj instanceof Object) {
         this._state = obj;
      }
   }

   static void lock(IntHashtable message) {
      message.put(4, PersistentContent.reEncode(message.get(4), true, true));
      message.put(5, PersistentContent.reEncode(message.get(5), true, true));
   }

   void setState(int state) {
      this._state = state;
      super._serialized.put(8, new Object(this._state));
      this.commit();
   }

   int getState() {
      return this._state;
   }

   public String getContentType() {
      try {
         return PersistentContent.decodeString(this._contentType);
      } finally {
         ;
      }
   }

   public byte[] getData() {
      return this._data;
   }

   public String getFilename() {
      try {
         return PersistentContent.decodeString(this._filename);
      } finally {
         ;
      }
   }

   public int getSize() {
      return this._size;
   }

   @Override
   public String getText() {
      return PeerResources.format(2031, FileUtilities.getDisplayName(this.getFilename()), String.valueOf(this._size >> 10));
   }

   @Override
   public Field getField(Object context) {
      return new FileMessageField(this);
   }

   @Override
   public int getOrder(Object context) {
      return 0;
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return false;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public Object clone(Object context) {
      return this;
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (!(target instanceof Object)) {
         return false;
      }

      RIMMessagingOutgoingMessage outgoingMessage = (RIMMessagingOutgoingMessage)target;
      Parameters parameters = CMIMEUtilities.createContentDispositionParameters(outgoingMessage, this.getFilename());
      outgoingMessage.addAttachment(this._data, parameters, this.getContentType());
      return true;
   }

   static MessengerMessageImpl deserialize(IntHashtable message, PeerContactListCollection contactList) {
      FileMessage result = null;
      if (message instanceof Object) {
         result = new FileMessage(message, contactList);
      }

      return result;
   }
}
