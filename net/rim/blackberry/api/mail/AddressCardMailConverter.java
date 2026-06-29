package net.rim.blackberry.api.mail;

import net.rim.blackberry.api.mail.rim.MailConverter;
import net.rim.blackberry.api.mail.rim.MailConverterManager;
import net.rim.blackberry.api.pdap.ContactImpl;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.EditableProvider;

public class AddressCardMailConverter implements MailConverter {
   private AddressCardMailConverter() {
   }

   public static void register() {
      MailConverterManager.getInstance().register(new AddressCardMailConverter());
   }

   @Override
   public boolean canConvert(Object o) {
      return o instanceof Object || o instanceof PDAPContactAttachmentPart;
   }

   @Override
   public Object convert(Object o, Object context) {
      if (o instanceof Object && context instanceof Object) {
         AddressCardModel acm = (AddressCardModel)o;
         Multipart parent = (Multipart)context;
         PDAPContactAttachmentPart cap = new PDAPContactAttachmentPart(parent);
         cap.setContent(acm);
         return cap;
      }

      if (!(o instanceof PDAPContactAttachmentPart)) {
         return null;
      }

      PDAPContactAttachmentPart cap = (PDAPContactAttachmentPart)o;
      ContactImpl contact = (ContactImpl)cap.getContent();
      AddressCardModel acm = (AddressCardModel)contact.getInternalModel();
      ((EditableProvider)acm).makeReadOnly();
      return acm;
   }
}
