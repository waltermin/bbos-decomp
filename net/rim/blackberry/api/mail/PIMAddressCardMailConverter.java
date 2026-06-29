package net.rim.blackberry.api.mail;

import net.rim.blackberry.api.mail.rim.MailConverter;
import net.rim.blackberry.api.mail.rim.MailConverterManager;
import net.rim.blackberry.api.pim.ContactImpl;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.EditableProvider;

public class PIMAddressCardMailConverter implements MailConverter {
   private PIMAddressCardMailConverter() {
   }

   public static void register() {
      MailConverterManager.getInstance().register(new PIMAddressCardMailConverter());
   }

   @Override
   public boolean canConvert(Object o) {
      return o instanceof AddressCardModel || o instanceof ContactAttachmentPart;
   }

   @Override
   public Object convert(Object o, Object context) {
      if (o instanceof AddressCardModel && context instanceof Multipart) {
         AddressCardModel acm = (AddressCardModel)o;
         Multipart parent = (Multipart)context;
         ContactAttachmentPart cap = new ContactAttachmentPart(parent);
         cap.setContent(acm);
         return cap;
      }

      if (!(o instanceof ContactAttachmentPart)) {
         return null;
      }

      ContactAttachmentPart cap = (ContactAttachmentPart)o;
      ContactImpl contact = (ContactImpl)cap.getContent();
      AddressCardModel acm = (AddressCardModel)contact.getInternalModel();
      ((EditableProvider)acm).makeReadOnly();
      return acm;
   }
}
