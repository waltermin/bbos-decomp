package net.rim.blackberry.api.mail;

import java.util.Vector;

final class SenderHeaderHandler extends AddressHeaderHandler {
   @Override
   public final void addHeader(String header, String value, Message m) {
      this.addHeader(Header.SENDER, 4, header, value, m);
   }

   @Override
   public final void setHeader(String header, String value, Message m) {
      this.setHeader(Header.SENDER, 4, header, value, m);
   }

   @Override
   public final void removeHeader(String header, Message m) {
      this.removeHeader(Header.SENDER, 4, header, m);
   }

   @Override
   public final String[] getHeader(String header, Message m) {
      return this.getHeader(Header.SENDER, 4, header, m);
   }

   @Override
   public final Vector getHeaderObjects(Vector v, Message m) {
      return this.getHeaderObjects(v, Header.SENDER, 4, m);
   }
}
