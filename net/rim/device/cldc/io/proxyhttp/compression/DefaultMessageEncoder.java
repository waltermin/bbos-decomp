package net.rim.device.cldc.io.proxyhttp.compression;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.proxyhttp.compression.coders.Coder;
import net.rim.device.cldc.io.proxyhttp.compression.coders.DateCoder;
import net.rim.device.cldc.io.proxyhttp.compression.coders.IntegerCoder;
import net.rim.device.cldc.io.proxyhttp.compression.coders.MediaEscapedTextCoder;
import net.rim.device.cldc.io.proxyhttp.compression.coders.RimEscapedTextCoder;
import net.rim.device.cldc.io.proxyhttp.compression.coders.TextCoder;
import net.rim.device.cldc.io.proxyhttp.compression.coders.TokenEscapedTextCoder;
import net.rim.device.cldc.io.proxyhttp.compression.coders.UrlEscapedTextCoder;

public final class DefaultMessageEncoder implements MessageEncoder {
   @Override
   public final int getVersion() {
      return 16;
   }

   protected DefaultMessageEncoder() {
   }

   @Override
   public final void encodeHeader(String name, String value, OutputStream outs) {
      int id;
      switch (getHash(StringUtilities.toLowerCase(name, 1701707776))) {
         case 318:
            id = 41;
            break;
         case 606:
            id = 5;
            break;
         case 619:
            id = 47;
            break;
         case 1036:
            id = 20;
            break;
         case 1046:
            id = 19;
            break;
         case 1099:
            id = 23;
            break;
         case 1135:
            id = 24;
            break;
         case 1138:
            id = 46;
            break;
         case 1555:
            id = 36;
            break;
         case 1676:
            id = 6;
            break;
         case 2170:
            id = 33;
            break;
         case 2213:
            id = 18;
            break;
         case 2252:
            id = 0;
            break;
         case 2272:
            id = 21;
            break;
         case 2320:
            id = 39;
            break;
         case 2898:
            id = 44;
            break;
         case 2999:
            id = 42;
            break;
         case 3000:
            id = 37;
            break;
         case 3001:
            id = 48;
            break;
         case 3078:
            id = 22;
            break;
         case 3574:
            id = 28;
            break;
         case 3586:
            id = 25;
            break;
         case 3882:
            id = 31;
            break;
         case 5592:
            id = 45;
            break;
         case 5594:
            id = 40;
            break;
         case 5941:
            id = 9;
            break;
         case 6016:
            id = 15;
            break;
         case 6798:
            id = 38;
            break;
         case 8150:
            id = 17;
            break;
         case 8262:
            id = 32;
            break;
         case 8933:
            id = 27;
            break;
         case 9199:
            id = 30;
            break;
         case 9207:
            id = 16;
            break;
         case 9292:
            id = 4;
            break;
         case 9502:
            id = 8;
            break;
         case 9995:
            id = 7;
            break;
         case 10836:
            id = 1;
            break;
         case 10844:
            id = 13;
            break;
         case 12125:
            id = 56;
            break;
         case 12159:
            id = 3;
            break;
         case 12226:
            id = 2;
            break;
         case 13694:
            id = 55;
            break;
         case 13880:
            id = 12;
            break;
         case 13950:
            id = 11;
            break;
         case 14190:
            id = 14;
            break;
         case 14271:
            id = 49;
            break;
         case 15097:
            id = 26;
            break;
         case 15623:
            id = 43;
            break;
         case 17963:
            id = 34;
            break;
         case 18931:
            id = 29;
            break;
         case 20368:
            id = 10;
            break;
         case 20573:
            id = 35;
            break;
         case 22101:
            id = 51;
            break;
         case 23094:
            id = 50;
            break;
         case 26365:
            id = 52;
            break;
         case 28945:
            id = 53;
            break;
         case 34311:
            id = 57;
            break;
         case 36004:
            id = 54;
            break;
         default:
            id = -1;
      }

      if (id != -1) {
         Coder coder = getCoder(id);
         outs.write(id ^ 128);
         coder.encode(value, outs);
      } else {
         Coder coder = TextCoder.getInstance();
         coder.encode(name, outs);
         coder.encode(value, outs);
      }
   }

   @Override
   public final void decodeHeader(InputStream ins, String[] nameValuePair) {
      int headerToken = ins.read();
      if (headerToken == -1) {
         throw new Object();
      }

      if (headerToken >= 128) {
         headerToken ^= 128;
         Coder coder = getCoder(headerToken);
         nameValuePair[0] = getTokenName(headerToken);
         nameValuePair[1] = coder.decode(ins);
      } else {
         if (headerToken > 0) {
            TextCoder coder = TextCoder.getInstance();
            nameValuePair[0] = coder.decode(ins, headerToken);
            nameValuePair[1] = coder.decode(ins);
         }
      }
   }

   @Override
   public final void encodeRequestLine(String messageLine, OutputStream outs) {
      UrlEscapedTextCoder.getInstance().encode(messageLine, outs);
   }

   @Override
   public final String decodeRequestLine(InputStream ins) {
      return UrlEscapedTextCoder.getInstance().decode(ins);
   }

   @Override
   public final void encodeResponseLine(String messageLine, OutputStream outs) {
      UrlEscapedTextCoder.getInstance().encode(messageLine, outs);
   }

   @Override
   public final String decodeResponseLine(InputStream ins) {
      return UrlEscapedTextCoder.getInstance().decode(ins);
   }

   private static final String getTokenName(int id) {
      switch (id) {
         case -1:
            return "";
         case 0:
         default:
            return "accept";
         case 1:
            return "accept-charset";
         case 2:
            return "accept-encoding";
         case 3:
            return "accept-language";
         case 4:
            return "accept-ranges";
         case 5:
            return "age";
         case 6:
            return "allow";
         case 7:
            return "authorization";
         case 8:
            return "cache-control";
         case 9:
            return "connection";
         case 10:
            return "content-disposition";
         case 11:
            return "content-encoding";
         case 12:
            return "content-language";
         case 13:
            return "content-length";
         case 14:
            return "content-location";
         case 15:
            return "content-md5";
         case 16:
            return "content-range";
         case 17:
            return "content-type";
         case 18:
            return "cookie";
         case 19:
            return "date";
         case 20:
            return "etag";
         case 21:
            return "expect";
         case 22:
            return "expires";
         case 23:
            return "from";
         case 24:
            return "host";
         case 25:
            return "if-match";
         case 26:
            return "if-modified-since";
         case 27:
            return "if-none-match";
         case 28:
            return "if-range";
         case 29:
            return "if-unmodified-since";
         case 30:
            return "last-modified";
         case 31:
            return "location";
         case 32:
            return "max-forwards";
         case 33:
            return "pragma";
         case 34:
            return "proxy-authenticate";
         case 35:
            return "proxy-authorization";
         case 36:
            return "range";
         case 37:
            return "referer";
         case 38:
            return "retry-after";
         case 39:
            return "server";
         case 40:
            return "set-cookie";
         case 41:
            return "te";
         case 42:
            return "trailer";
         case 43:
            return "transfer-encoding";
         case 44:
            return "upgrade";
         case 45:
            return "user-agent";
         case 46:
            return "vary";
         case 47:
            return "via";
         case 48:
            return "warning";
         case 49:
            return "www-authenticate";
         case 50:
            return "x-rim-exclude-headers";
         case 51:
            return "x-rim-push-channel-id";
         case 52:
            return "x-rim-push-description";
         case 53:
            return "x-rim-push-read-icon-url";
         case 54:
            return "x-rim-push-ribbon-position";
         case 55:
            return "x-rim-push-title";
         case 56:
            return "x-rim-push-type";
         case 57:
            return "x-rim-push-unread-icon-url";
      }
   }

   private static final int getHash(String contentType) {
      byte[] bytes = contentType.getBytes();
      int hash = 0;
      int count = bytes.length;

      for (int i = 0; i < count; i++) {
         hash += (bytes[i] & 255) * (i + 1);
      }

      return hash;
   }

   private static final Coder getCoder(int code) {
      switch (code) {
         case -1:
         case 3:
         case 12:
         case 20:
         case 23:
         case 25:
         case 27:
         case 42:
         case 44:
         case 46:
         case 47:
         case 48:
         case 52:
            return TextCoder.getInstance();
         case 0:
         case 17:
         default:
            return MediaEscapedTextCoder.getInstance();
         case 1:
         case 2:
         case 4:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 21:
         case 33:
         case 34:
         case 35:
         case 36:
         case 41:
         case 43:
            return TokenEscapedTextCoder.getInstance();
         case 5:
         case 13:
         case 15:
         case 16:
         case 32:
         case 55:
            return IntegerCoder.getInstance();
         case 6:
         case 14:
         case 18:
         case 24:
         case 31:
         case 37:
         case 39:
         case 40:
         case 45:
         case 49:
            return UrlEscapedTextCoder.getInstance();
         case 19:
         case 22:
         case 26:
         case 28:
         case 29:
         case 30:
         case 38:
            return DateCoder.getInstance();
         case 50:
         case 51:
         case 53:
         case 54:
            return RimEscapedTextCoder.getInstance();
      }
   }
}
