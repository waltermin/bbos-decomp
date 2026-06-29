package net.rim.blackberry.api.mail;

import java.util.Vector;

final class BccHeaderHandler extends AddressHeaderHandler {
   @Override
   public final void addHeader(String header, String value, Message m) {
      this.addHeader(Header.BCC, 2, header, value, m);
   }

   @Override
   public final void setHeader(String header, String value, Message m) {
      this.setHeader(Header.BCC, 2, header, value, m);
   }

   @Override
   public final void removeHeader(String header, Message m) {
      this.removeHeader(Header.BCC, 2, header, m);
   }

   @Override
   public final String[] getHeader(String header, Message m) {
      return this.getHeader(Header.BCC, 2, header, m);
   }

   @Override
   public final Vector getHeaderObjects(Vector v, Message m) {
      return this.getHeaderObjects(v, Header.BCC, 2, m);
   }
}
