package net.rim.device.apps.internal.browser.wappush;

import com.fourthpass.wapstack.wsp.WSPHeaderDecoder;
import java.io.DataInputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.IntStack;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.stack.WAPInputStream;

final class COCModelFactory {
   public static final int URI = 5;
   public static final int URI_HTTP = 6;
   public static final int URI_HTTP_WWW = 7;
   public static final int URI_HTTPS = 8;
   public static final int URI_HTTPS_WWW = 9;
   public static final int CO = 5;
   public static final int INVALIDATE_OBJECT = 6;
   public static final int INVALIDATE_SERVICE = 7;
   private static final int DOT_COM = 133;
   private static final int DOT_EDU = 134;
   private static final int DOT_NET = 135;
   private static final int DOT_ORG = 136;

   public static final Object createCOCModel(DataInputStream inStream, HttpHeaders headers) {
      try {
         COCModel model = new COCModel();
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

         String baseUri = headers.getPropertyValue("X-Wap-Content-URI");
         if (baseUri == null) {
            baseUri = "";
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
                     processTag(model, in, encoding, stringTable, baseUri, tagStack, (byte)tag, 0, 0, false);
                  }
                  break;
               default:
                  int tagId = id & 63;
                  int attr = (id & 128) >> 7;
                  int content = (id & 64) >> 6;
                  processTag(model, in, encoding, stringTable, baseUri, tagStack, (byte)tagId, attr, content, true);
            }
         }
      } finally {
         throw new Object();
      }
   }

   private static final void processTag(
      COCModel model,
      WAPInputStream in,
      String encoding,
      byte[] stringTable,
      String baseUri,
      IntStack tagStack,
      byte tag,
      int attr,
      int content,
      boolean isStartTag
   ) {
      if (isStartTag && content == 1) {
         tagStack.push(tag);
      }

      switch (tag) {
         case 4:
            break;
         case 5:
         default:
            processElementCO(attr, content, isStartTag);
            break;
         case 6:
            processElementInvalidateObject(model, in, encoding, stringTable, baseUri, attr, content, isStartTag);
            break;
         case 7:
            processElementInvalidateService(model, in, encoding, stringTable, baseUri, attr, content, isStartTag);
      }

      if (!isStartTag) {
         tagStack.pop();
      }
   }

   private static final void processElementCO(int attr, int content, boolean isStartTag) {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final void processElementInvalidateObject(
      COCModel model, WAPInputStream in, String encoding, byte[] stringTable, String baseUri, int attr, int content, boolean isStartTag
   ) {
      if (isStartTag) {
         int next = -1;

         while (next != 1 && attr != 0) {
            next = in.read();
            switch (next) {
               case 4:
                  continue;
               case 5:
               default:
                  String value = readHrefAttribute(in, encoding, stringTable, "");
                  boolean var12 = false /* VF: Semaphore variable */;

                  try {
                     var12 = true;
                     URI e = new Object(value, baseUri, true);
                     model.addObjectUri(((URI)e).getAbsoluteURL());
                     var12 = false;
                     continue;
                  } finally {
                     if (var12) {
                        model.addObjectUri(value);
                        continue;
                     }
                  }
               case 6:
                  model.addObjectUri(readHrefAttribute(in, encoding, stringTable, "http://"));
                  continue;
               case 7:
                  model.addObjectUri(readHrefAttribute(in, encoding, stringTable, "http://www."));
                  continue;
               case 8:
                  model.addObjectUri(readHrefAttribute(in, encoding, stringTable, "https://"));
                  continue;
               case 9:
            }

            model.addObjectUri(readHrefAttribute(in, encoding, stringTable, "https://www."));
         }

         for (int var14 = -1; var14 != 1; var14 = in.read()) {
            if (content == 0) {
               return;
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final void processElementInvalidateService(
      COCModel model, WAPInputStream in, String encoding, byte[] stringTable, String baseUri, int attr, int content, boolean isStartTag
   ) {
      if (isStartTag) {
         int next = -1;

         while (next != 1 && attr != 0) {
            next = in.read();
            switch (next) {
               case 4:
                  continue;
               case 5:
               default:
                  String value = readHrefAttribute(in, encoding, stringTable, "");
                  boolean var12 = false /* VF: Semaphore variable */;

                  try {
                     var12 = true;
                     URI e = new Object(value, baseUri, true);
                     model.addServiceUri(((URI)e).getAbsoluteURL());
                     var12 = false;
                     continue;
                  } finally {
                     if (var12) {
                        model.addServiceUri(value);
                        continue;
                     }
                  }
               case 6:
                  model.addServiceUri(readHrefAttribute(in, encoding, stringTable, "http://"));
                  continue;
               case 7:
                  model.addServiceUri(readHrefAttribute(in, encoding, stringTable, "http://www."));
                  continue;
               case 8:
                  model.addServiceUri(readHrefAttribute(in, encoding, stringTable, "https://"));
                  continue;
               case 9:
            }

            model.addServiceUri(readHrefAttribute(in, encoding, stringTable, "https://www."));
         }

         for (int var14 = -1; var14 != 1; var14 = in.read()) {
            if (content == 0) {
               return;
            }
         }
      }
   }

   public static final String readHrefAttribute(WAPInputStream in, String encoding, byte[] stringTable, String inString) {
      StringBuffer result = (StringBuffer)(new Object(inString));
      int next = -1;

      while (next != 1) {
         in.mark(Integer.MAX_VALUE);
         next = in.read();
         switch (next) {
            case 3:
               result = result.append(in.readInlineString(encoding));
               break;
            case 131:
               int offset = in.readMBInt();
               result = result.append(getStringFromStringTable(stringTable, offset, encoding));
               break;
            case 133:
               result = result.append(".com/");
               break;
            case 134:
               result = result.append(".edu/");
               break;
            case 135:
               result = result.append(".net/");
               break;
            case 136:
               result = result.append(".org/");
               break;
            default:
               in.reset();
               return result.toString();
         }
      }

      return result.toString();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final String getStringFromStringTable(byte[] stringTable, int index, String encoding) {
      if (index < 0 | index >= stringTable.length) {
         return "";
      }

      byte next = stringTable[index];
      int start = index;

      int length;
      for (length = 0; next != 0 && index < stringTable.length; length++) {
         next = stringTable[++index];
      }

      String retVal = null;
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         retVal = (String)(new Object(stringTable, start, length, encoding));
         var9 = false;
      } finally {
         if (var9) {
            return (String)(new Object(stringTable, start, length));
         }
      }

      return retVal;
   }
}
