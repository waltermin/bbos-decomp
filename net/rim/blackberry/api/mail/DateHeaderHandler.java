package net.rim.blackberry.api.mail;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import net.rim.device.api.i18n.DateFormat;

final class DateHeaderHandler implements HeaderHandler {
   @Override
   public final void addHeader(String header, String value, Message m) {
      if (header.toLowerCase().equals(Header.DATE.toLowerCase())) {
         long l = Long.parseLong(value);
         Date d = (Date)(new Object(l));
         m.setSentDate(d);
      }
   }

   @Override
   public final void setHeader(String header, String value, Message m) {
      this.removeHeader(header, m);
      this.addHeader(header, value, m);
   }

   @Override
   public final void removeHeader(String header, Message m) {
      if (header.toLowerCase().equals(Header.DATE.toLowerCase())) {
         m.getEmailMessageModel().setTimestamp(0);
      }
   }

   @Override
   public final String[] getHeader(String header, Message m) {
      Date dr = m.getReceivedDate();
      Date ds = m.getSentDate();
      return new Object[]{this.formatDateAsString(dr), this.formatDateAsString(ds)};
   }

   private final String formatDateAsString(Date d) {
      if (d != null && d.getTime() != 0) {
         Calendar c = Calendar.getInstance();
         c.setTime(d);
         StringBuffer sb = (StringBuffer)(new Object());
         DateFormat df = DateFormat.getInstance(36);
         df.format(c, sb, null);
         return sb.toString();
      } else {
         return "";
      }
   }

   @Override
   public final Vector getHeaderObjects(Vector v, Message m) {
      String[] array = this.getHeader(Header.DATE, m);

      for (int i = 0; i < array.length; i++) {
         if (array[i] != null && array[i].length() != 0) {
            v.addElement(new Header(Header.DATE, array[i]));
         }
      }

      return v;
   }
}
