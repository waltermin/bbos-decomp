package net.rim.blackberry.api.mail;

import java.util.Vector;

final class ReplyToHeaderHandler extends AddressHeaderHandler {
   @Override
   public final void addHeader(String header, String value, Message m) {
      this.addHeader(Header.REPLY_TO, 5, header, value, m);
   }

   @Override
   public final void setHeader(String header, String value, Message m) {
      this.setHeader(Header.REPLY_TO, 5, header, value, m);
   }

   @Override
   public final void removeHeader(String header, Message m) {
      this.removeHeader(Header.REPLY_TO, 5, header, m);
   }

   @Override
   public final String[] getHeader(String header, Message m) {
      return this.getHeader(Header.REPLY_TO, 5, header, m);
   }

   @Override
   public final Vector getHeaderObjects(Vector v, Message m) {
      return this.getHeaderObjects(v, Header.REPLY_TO, 5, m);
   }
}
