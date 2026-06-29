package net.rim.device.cldc.io.proxyhttp.compression.coders;

import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.system.ApplicationRegistry;

public final class DateCoder implements Coder {
   private static SimpleDateFormat _dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.get(1701707776));
   private static final long ID = -325877882800875996L;

   public static final DateCoder getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      DateCoder coder = (DateCoder)registry.getOrWaitFor(-325877882800875996L);
      if (coder == null) {
         coder = new DateCoder();
         registry.put(-325877882800875996L, coder);
      }

      return coder;
   }

   @Override
   public final void encode(String decoded, OutputStream outs) {
      int dateValue = parseDate(decoded);
      if (dateValue != -1) {
         IntegerCoder.getInstance().encode(dateValue, outs);
      } else {
         TextCoder.getInstance().encode(decoded, outs);
      }
   }

   @Override
   public final String decode(InputStream ins) throws EOFException {
      int firstByte = ins.read();
      if (firstByte == -1) {
         throw new EOFException();
      }

      long value = 0;
      int nextByte = 0;
      if (firstByte >= 128) {
         value = firstByte ^ 128;
         return decodeDate(value);
      }

      if (firstByte <= 4) {
         while (firstByte > 0) {
            value <<= 8;
            nextByte = ins.read();
            if (nextByte == -1) {
               throw new EOFException();
            }

            value += nextByte;
            firstByte--;
         }

         return decodeDate(value * 1000);
      } else {
         return TextCoder.getInstance().decode(ins, firstByte);
      }
   }

   private static final int parseDate(String date) {
      return (int)(HttpDateParser.parse(date) / 1000);
   }

   private static final String decodeDate(long dateValue) {
      synchronized (_dateFormat) {
         Date date = new Date(dateValue);
         Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
         calendar.setTime(date);
         return _dateFormat.format(calendar);
      }
   }
}
