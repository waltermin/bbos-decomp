package net.rim.device.apps.internal.browser.wappush;

import com.fourthpass.wapstack.wsp.WSPHeaderDecoder;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.util.IntStack;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.stack.HeaderParser;
import net.rim.device.apps.internal.browser.stack.WAPInputStream;

final class SICModelFactory {
   public static final int ACTION_SIGNAL_NONE;
   public static final int ACTION_SIGNAL_LOW;
   public static final int ACTION_SIGNAL_MEDIUM;
   public static final int ACTION_SIGNAL_HIGH;
   public static final int ACTION_SIGNAL_DELETE;
   public static final int CREATED;
   public static final int HREF;
   public static final int HREF_HTTP;
   public static final int HREF_HTTP_WWW;
   public static final int HREF_HTTPS;
   public static final int HREF_HTTPS_WWW;
   public static final int EXPIRES;
   public static final int SI_ID;
   public static final int CLASS;
   public static final int SI;
   public static final int INDICATION;
   public static final int INFO;
   public static final int ITEM;

   public static final SICModel createSICModel(DataInputStream inStream, HttpHeaders headers) {
      try {
         String preferredConfigUid = null;
         int preferredConfigType = 0;
         String preferredTransportCid = "WAP";
         InputStream parentStream = inStream;
         if (inStream instanceof PushTextDataInputStream) {
            parentStream = ((PushTextDataInputStream)inStream).getRawContentStream();
         }

         if (parentStream instanceof Object) {
            PushInputStream pis = (PushInputStream)parentStream;
            if (pis.getConnectionType() == 3) {
               preferredConfigUid = BrowserConfigRecord.mapTransportUIDToConfigUID("IPPP", pis.getSource());
            }
         }

         if (headers.getPropertyValue("x-rim-fetch-bearer") != null) {
            preferredConfigUid = HeaderParser.getPreferredConfigUID(headers, null);
            preferredConfigType = HeaderParser.getPreferredConfigType(headers, 0);
            preferredTransportCid = HeaderParser.getPreferredTransportCID(headers, "WAP");
         }

         SICModel model = new SICModel(preferredConfigUid, preferredConfigType, preferredTransportCid);
         WAPInputStream in = (WAPInputStream)(new Object(inStream));
         IntStack tagStack = (IntStack)(new Object());
         in.read();
         int publicIdentifierId = in.readMBInt();
         if (publicIdentifierId == 0) {
            in.readMBInt();
         }

         int charSet = in.readMBInt();
         if (charSet == 0) {
            charSet = 106;
         } else if (charSet == 3) {
            charSet = 4;
         }

         String encoding = WSPHeaderDecoder.getCharsetName(charSet);
         int stringTableSize = in.readMBInt();
         byte[] stringTable = new byte[stringTableSize];
         in.read(stringTable);
         int id = 0;

         while (true) {
            id = in.read();
            if (id == -1) {
               long expiry = model.getExpiry();
               if (expiry != 0 && expiry < System.currentTimeMillis()) {
                  return null;
               }

               return model;
            }

            switch ((short)id) {
               case 1:
                  if (!tagStack.isEmpty()) {
                     int tag = tagStack.peek();
                     processTag(model, in, encoding, stringTable, tagStack, (byte)tag, 0, 0, false);
                  }
                  break;
               default:
                  int tagId = id & 63;
                  int attr = (id & 128) >> 7;
                  int content = (id & 64) >> 6;
                  processTag(model, in, encoding, stringTable, tagStack, (byte)tagId, attr, content, true);
            }
         }
      } finally {
         throw new Object();
      }
   }

   private static final void processTag(
      SICModel model, WAPInputStream in, String encoding, byte[] stringTable, IntStack tagStack, byte tag, int attr, int content, boolean isStartTag
   ) {
      if (isStartTag && content == 1) {
         tagStack.push(tag);
      }

      switch (tag) {
         case 4:
            break;
         case 5:
         default:
            processElementSI(attr, content, isStartTag);
            break;
         case 6:
            processElementIndication(model, in, encoding, stringTable, attr, content, isStartTag);
            break;
         case 7:
            processElementInfo(attr, content, isStartTag);
            break;
         case 8:
            processElementItem(attr, content, isStartTag);
      }

      if (!isStartTag) {
         tagStack.pop();
      }
   }

   private static final void processElementSI(int attr, int content, boolean isStartTag) {
   }

   private static final void processElementItem(int attr, int content, boolean isStartTag) {
   }

   private static final void processElementInfo(int attr, int content, boolean isStartTag) {
   }

   private static final void processElementIndication(
      SICModel model, WAPInputStream in, String encoding, byte[] stringTable, int attr, int content, boolean isStartTag
   ) {
      if (isStartTag) {
         int next = -1;
         boolean idSet = false;
         String url = null;

         while (next != 1 && attr != 0) {
            next = in.read();
            switch (next) {
               case 4:
                  break;
               case 5:
               case 6:
               case 7:
               case 8:
               case 9:
                  model.setAction(next);
                  break;
               case 10:
                  model.setCreated(readDateAttribute(in));
                  break;
               case 11:
               default:
                  url = COCModelFactory.readHrefAttribute(in, encoding, stringTable, "");
                  model.setURL(url);
                  break;
               case 12:
                  url = COCModelFactory.readHrefAttribute(in, encoding, stringTable, "http://");
                  model.setURL(url);
                  break;
               case 13:
                  url = COCModelFactory.readHrefAttribute(in, encoding, stringTable, "http://www.");
                  model.setURL(url);
                  break;
               case 14:
                  url = COCModelFactory.readHrefAttribute(in, encoding, stringTable, "https://");
                  model.setURL(url);
                  break;
               case 15:
                  url = COCModelFactory.readHrefAttribute(in, encoding, stringTable, "https://www.");
                  model.setURL(url);
                  break;
               case 16:
                  model.setExpiry(readDateAttribute(in));
                  break;
               case 17:
                  model.setID(readSIIDAttribute(in, encoding, stringTable));
                  idSet = true;
            }
         }

         if (!idSet) {
            model.setID(url);
         }

         next = -1;

         while (next != 1) {
            if (content == 0) {
               return;
            }

            next = in.read();
            switch (next) {
               case 3:
                  model.setMessage(in.readInlineString(encoding));
                  break;
               case 131:
                  int offset = in.readMBInt();
                  model.setMessage(COCModelFactory.getStringFromStringTable(stringTable, offset, encoding));
            }
         }
      }
   }

   private static final String readSIIDAttribute(WAPInputStream in, String encoding, byte[] stringTable) {
      int next = -1;
      StringBuffer result = (StringBuffer)(new Object());

      while (next != 1) {
         in.mark(Integer.MAX_VALUE);
         next = in.read();
         switch (next) {
            case 3:
               result = result.append(in.readInlineString(encoding));
               break;
            case 131:
               int offset = in.readMBInt();
               result = result.append(COCModelFactory.getStringFromStringTable(stringTable, offset, encoding));
               break;
            default:
               in.reset();
               return result.toString();
         }
      }

      return result.toString();
   }

   private static final int readDateTimeWORD(byte[] data, int offset) {
      return data.length >= offset + 2
         ? (data[offset] >> 4 & 15) * 1000 + (data[offset] & 15) * 100 + (data[offset + 1] >> 4 & 15) * 10 + (data[offset + 1] & 15)
         : 0;
   }

   private static final int readDateTimeBYTE(byte[] data, int offset) {
      return data.length >= offset + 1 ? (data[offset] >> 4 & 15) * 10 + (data[offset] & 15) : 0;
   }

   private static final long readDateAttribute(WAPInputStream in) {
      int next = -1;
      if (next == 1) {
         return Long.MAX_VALUE;
      }

      in.mark(Integer.MAX_VALUE);
      next = in.read();
      switch (next) {
         case 195:
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            byte[] data = in.readByteArray();
            int year = readDateTimeWORD(data, 0);
            int month = readDateTimeBYTE(data, 2);
            int day = readDateTimeBYTE(data, 3);
            int hour = readDateTimeBYTE(data, 4);
            int minutes = readDateTimeBYTE(data, 5);
            int seconds = readDateTimeBYTE(data, 6);
            calendar.set(1, year);
            calendar.set(2, month - 1);
            calendar.set(5, day);
            calendar.set(11, hour);
            calendar.set(12, minutes);
            calendar.set(13, seconds);
            calendar.set(14, 0);
            return calendar.getTime().getTime();
         default:
            in.reset();
            return Long.MAX_VALUE;
      }
   }
}
