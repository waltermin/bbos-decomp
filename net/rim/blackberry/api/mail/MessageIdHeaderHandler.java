package net.rim.blackberry.api.mail;

import java.util.Vector;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

final class MessageIdHeaderHandler implements HeaderHandler {
   @Override
   public final void addHeader(String header, String value, Message m) {
      throw new IllegalArgumentException("unsupported operation");
   }

   @Override
   public final void setHeader(String header, String value, Message m) {
      this.addHeader(header, value, m);
   }

   @Override
   public final void removeHeader(String header, Message m) {
   }

   @Override
   public final String[] getHeader(String header, Message m) {
      EmailMessageModel emm = m.getEmailMessageModel();
      return new String[]{Integer.toString(emm.getCMIMEReferenceIdentifier())};
   }

   @Override
   public final Vector getHeaderObjects(Vector v, Message m) {
      String[] array = this.getHeader("Message-ID:", m);

      for (int i = 0; i < array.length; i++) {
         if (array[i] != null) {
            v.addElement(new Header("Message-ID:", array[i]));
         }
      }

      return v;
   }
}
