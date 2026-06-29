package net.rim.blackberry.api.mail;

import net.rim.blackberry.api.mail.rim.MailConverter;
import net.rim.blackberry.api.mail.rim.MailConverterManager;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModel;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;

class UnsupportedMailConverter implements MailConverter {
   private UnsupportedMailConverter() {
   }

   public static void register() {
      MailConverterManager.getInstance().register(new UnsupportedMailConverter());
   }

   @Override
   public boolean canConvert(Object o) {
      if (o instanceof ProxyModel) {
         ProxyModel pm = (ProxyModel)o;
         o = pm.getObject();
      }

      return o instanceof UnknownMimePartModel;
   }

   @Override
   public Object convert(Object o, Object context) {
      if (o instanceof ProxyModel) {
         ProxyModel pm = (ProxyModel)o;
         o = pm.getObject();
      }

      if (context instanceof Multipart) {
         Multipart parent = (Multipart)context;
         if (o instanceof UnknownMimePartModel) {
            UnknownMimePartModel umpm = (UnknownMimePartModel)o;
            return this.createWrapper(umpm, parent);
         }
      }

      return null;
   }

   private BodyPart createWrapper(UnknownMimePartModel umpm, Multipart parent) {
      UnsupportedAttachmentPart uap = new UnsupportedAttachmentPart(parent);
      uap.setContent(umpm);
      return uap;
   }
}
