package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;

final class VCardHandler implements MessageHandler {
   private static final String[] TYPES = new String[]{"text/x-vcard"};

   static final void register() {
      ContentHandlerManager.getInstance().registerSystemHandler(new VCardHandler(), TYPES);
   }

   @Override
   public final MessengerMessage handle(PeerContact contact, String contentType, String filename, byte[] data, int integer, Object context) {
      ContextObject contextobject = (ContextObject)(new Object());
      ContextObject.put(contextobject, 8849067667159082262L, data);
      AddressCardModel card = (AddressCardModel)FactoryUtil.createInstance(9048770516632928843L, contextobject);
      return card != null ? new VCardMessage(contact, contentType, data, filename, data.length, card) : null;
   }
}
