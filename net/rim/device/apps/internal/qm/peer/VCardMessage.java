package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.MessengerContact;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;

final class VCardMessage extends FileMessage {
   AddressCardModel _address;

   public VCardMessage(MessengerContact contact, String contentType, byte[] data, String filename, int size, AddressCardModel address) {
      super(contact, contentType, data, filename, size);
      this._address = address;
      super._serialized.put(0, new Object(2));
      this.commit();
   }

   private VCardMessage(IntHashtable message, PeerContactListCollection contactList) {
      super(message, contactList);
      if (super._data != null) {
         ContextObject contextobject = (ContextObject)(new Object());
         ContextObject.put(contextobject, 8849067667159082262L, super._data);
         this._address = (AddressCardModel)FactoryUtil.createInstance(9048770516632928843L, contextobject);
      }
   }

   public final AddressCardModel getAddressCard() {
      return this._address;
   }

   @Override
   public final Field getField(Object context) {
      return new VCardField(this);
   }

   static final MessengerMessageImpl deserialize(IntHashtable message, PeerContactListCollection contactList) {
      VCardMessage result = null;
      if (message instanceof Object) {
         result = new VCardMessage(message, contactList);
      }

      return result;
   }

   static final void lock(IntHashtable message) {
      FileMessage.lock(message);
   }
}
