package net.rim.blackberry.api.mail;

import java.util.Vector;

final class FromHeaderHandler extends AddressHeaderHandler {
   @Override
   public final void addHeader(String header, String value, Message m) {
      this.addHeader(Header.FROM, 3, header, value, m);
   }

   @Override
   public final void setHeader(String header, String value, Message m) {
      this.setHeader(Header.FROM, 3, header, value, m);
   }

   @Override
   public final void removeHeader(String header, Message m) {
      this.removeHeader(Header.FROM, 3, header, m);
   }

   @Override
   public final String[] getHeader(String header, Message m) {
      return this.getHeader(Header.FROM, 3, header, m);
   }

   @Override
   public final Vector getHeaderObjects(Vector v, Message m) {
      return this.getHeaderObjects(v, Header.FROM, 3, m);
   }
}
