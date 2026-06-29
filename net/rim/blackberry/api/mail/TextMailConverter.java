package net.rim.blackberry.api.mail;

import net.rim.blackberry.api.mail.rim.MailConverter;
import net.rim.blackberry.api.mail.rim.MailConverterManager;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailModifier;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;

class TextMailConverter implements MailConverter {
   private TextMailConverter() {
   }

   public static void register() {
      MailConverterManager.getInstance().register(new TextMailConverter());
   }

   @Override
   public boolean canConvert(Object o) {
      return o instanceof BodyModel || o instanceof TextBodyPart;
   }

   @Override
   public Object convert(Object o, Object context) {
      if (o instanceof BodyModel && context instanceof Multipart) {
         BodyModel body = (BodyModel)o;
         Multipart parent = (Multipart)context;
         TextBodyPart tbp = new TextBodyPart(parent, body.getText());
         if (body instanceof MorePartModel) {
            MorePartModel mpm = (MorePartModel)body;
            Message m = (Message)parent.getParent();
            EmailMessageModel emm = m.getEmailMessageModel();
            tbp.setInternalModel(emm, mpm.getMorePartID());
         }

         return tbp;
      } else if (o instanceof TextBodyPart && context instanceof EmailMessageModel) {
         TextBodyPart tbp = (TextBodyPart)o;
         EmailMessageModel m = (EmailMessageModel)context;
         EmailPayloadModel oldpayload = EmailModifier.beginChanges(m, null);
         String s = (String)tbp.getContent();
         BodyModel body = Message.createBodyModel(s);
         ReadableList l = m;
         int size = l.size();

         for (int i = 0; i < size; i++) {
            Object element = l.getAt(i);
            if (element instanceof BodyModel) {
               body = (BodyModel)element;
               body.setText(s);
            }
         }

         EmailModifier.endChanges(m, oldpayload, null);
         return body;
      } else {
         return null;
      }
   }
}
