package net.rim.device.apps.internal.browser.wappush;

import com.fourthpass.wapstack.wsp.WSPHeaderDecoder;
import java.io.DataInputStream;
import java.io.InputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.util.IntStack;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.stack.HeaderParser;
import net.rim.device.apps.internal.browser.stack.WAPInputStream;

final class SLCModelFactory {
   public static final int ACTION_EXECUTE_LOW = 5;
   public static final int ACTION_EXECUTE_HIGH = 6;
   public static final int ACTION_CACHE = 7;
   public static final int HREF = 8;
   public static final int HREF_HTTP = 9;
   public static final int HREF_HTTP_WWW = 10;
   public static final int HREF_HTTPS = 11;
   public static final int HREF_HTTPS_WWW = 12;
   public static final int SL = 5;

   public static final SLCModel createSLCModel(DataInputStream inStream, HttpHeaders headers) {
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

         SLCModel model = new SLCModel(preferredConfigUid, preferredConfigType, preferredTransportCid);
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
      SLCModel model, WAPInputStream in, String encoding, byte[] stringTable, IntStack tagStack, byte tag, int attr, int content, boolean isStartTag
   ) {
      if (isStartTag && content == 1) {
         tagStack.push(tag);
      }

      switch (tag) {
         case 5:
            processElementSL(model, in, encoding, stringTable, attr, content, isStartTag);
         default:
            if (!isStartTag) {
               tagStack.pop();
            }
      }
   }

   private static final void processElementSL(SLCModel model, WAPInputStream in, String encoding, byte[] stringTable, int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         int next = -1;

         while (next != 1) {
            if (attr == 0) {
               return;
            }

            next = in.read();
            switch (next) {
               case 4:
                  break;
               case 5:
               case 6:
               case 7:
                  model.setAction(next);
                  break;
               case 8:
               default:
                  model.setURL(COCModelFactory.readHrefAttribute(in, encoding, stringTable, ""));
                  break;
               case 9:
                  model.setURL(COCModelFactory.readHrefAttribute(in, encoding, stringTable, "http://"));
                  break;
               case 10:
                  model.setURL(COCModelFactory.readHrefAttribute(in, encoding, stringTable, "http://www."));
                  break;
               case 11:
                  model.setURL(COCModelFactory.readHrefAttribute(in, encoding, stringTable, "https://"));
                  break;
               case 12:
                  model.setURL(COCModelFactory.readHrefAttribute(in, encoding, stringTable, "https://www."));
            }
         }
      }
   }
}
