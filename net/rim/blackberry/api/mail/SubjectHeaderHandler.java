package net.rim.blackberry.api.mail;

import java.util.Vector;

final class SubjectHeaderHandler implements HeaderHandler {
   @Override
   public final void addHeader(String header, String value, Message m) {
      if (header.toLowerCase().equals(Header.SUBJECT.toLowerCase())) {
         m.setSubject(value);
      }
   }

   @Override
   public final void setHeader(String header, String value, Message m) {
      this.removeHeader(header, m);
      this.addHeader(header, value, m);
   }

   @Override
   public final void removeHeader(String header, Message m) {
      if (header.toLowerCase().equals(Header.SUBJECT.toLowerCase())) {
         m.setSubject(null);
      }
   }

   @Override
   public final String[] getHeader(String header, Message m) {
      return new Object[]{m.getSubject()};
   }

   @Override
   public final Vector getHeaderObjects(Vector v, Message m) {
      String[] array = this.getHeader(Header.SUBJECT, m);

      for (int i = 0; i < array.length; i++) {
         if (array[i] != null) {
            v.addElement(new Header(Header.SUBJECT, array[i]));
         }
      }

      return v;
   }
}
