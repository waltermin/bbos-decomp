package net.rim.device.apps.internal.browser.wml;

import net.rim.device.apps.internal.browser.stack.WAPInputStream;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.cldc.io.utility.URIEncoder;

public final class WMLAttributeReader {
   private byte[] _stringTable;
   private String _encoding;
   private String _postEncoding;
   private static final int MAX_ENCODED_SIZE = 32768;

   public WMLAttributeReader(byte[] stringTable, String encoding, String postEncoding) {
      this._stringTable = stringTable;
      this._encoding = encoding;
      this._postEncoding = postEncoding;
   }

   public final String getEncoding() {
      return this._encoding;
   }

   public final byte[] getEncodedVariableName(WAPInputStream in) {
      int id = 0;
      int count = 0;
      in.mark(Integer.MAX_VALUE);

      while (true) {
         try {
            id = in.read();
         } finally {
            ;
         }

         count++;
         switch ((short)id) {
            case 2:
               count += this.readMBIntCount(in);
               break;
            case 3:
               try {
                  count += this.getInlineOffset(in);
                  break;
               } finally {
                  break;
               }
            case 64:
               try {
                  count += this.getInlineOffset(in);
                  break;
               } finally {
                  break;
               }
            case 65:
               try {
                  count += this.getInlineOffset(in);
                  break;
               } finally {
                  break;
               }
            case 66:
               try {
                  count += this.getInlineOffset(in);
                  break;
               } finally {
                  break;
               }
            case 128:
               count += this.readMBIntCount(in);
               break;
            case 129:
               count += this.readMBIntCount(in);
               break;
            case 130:
               count += this.readMBIntCount(in);
               break;
            case 131:
               count += this.readMBIntCount(in);
               break;
            case 195:
               return null;
            default:
               String token = WMLConstants.getStringByToken((byte)id);
               if (token == null) {
                  count--;
                  byte[] result = null;

                  try {
                     in.reset();
                     int numToRead = count;
                     if (numToRead > 32768) {
                        numToRead = 32768;
                     }

                     result = new byte[numToRead];
                     in.read(result, 0, numToRead);
                     if (count != numToRead) {
                        in.skip(count - numToRead);
                     }
                  } finally {
                     return result;
                  }

                  return result;
               }
         }
      }
   }

   public final String read(WAPInputStream in, WMLContextManager wmlContextManager) {
      int id = 0;
      StringBuffer str = (StringBuffer)(new Object());

      while (true) {
         in.mark(2);
         id = in.read();
         switch ((short)id) {
            case 2:
               str.append((char)in.readMBInt());
               break;
            case 3:
               str.append(in.readInlineString(this._encoding));
               break;
            case 64: {
               String attr = in.readInlineString(this._encoding);
               str.append(URIEncoder.encode(null, wmlContextManager.get(attr), this._postEncoding, true));
               break;
            }
            case 65:
               String var9 = in.readInlineString(this._encoding);
               str.append(URIDecoder.decode(wmlContextManager.get(var9), this._postEncoding));
               break;
            case 66: {
               String attr = in.readInlineString(this._encoding);
               str.append(wmlContextManager.get(attr));
               break;
            }
            case 128:
               int var13 = in.readMBInt();
               str.append(URIEncoder.encode(null, wmlContextManager.get(this.getStringFromStringTable(var13)), this._postEncoding, true));
               break;
            case 129:
               int var12 = in.readMBInt();
               str.append(URIDecoder.decode(wmlContextManager.get(this.getStringFromStringTable(var12)), this._postEncoding));
               break;
            case 130:
               int var11 = in.readMBInt();
               str.append(wmlContextManager.get(this.getStringFromStringTable(var11)));
               break;
            case 131:
               int offset = in.readMBInt();
               str.append(this.getStringFromStringTable(offset));
               break;
            case 195:
               return null;
            default:
               String token = WMLConstants.getStringByToken((byte)id);
               if (token == null) {
                  in.reset();
                  return str.toString();
               }

               str.append(token);
         }
      }
   }

   public final void skip(WAPInputStream in) {
      int id = 0;

      while (true) {
         in.mark(2);
         id = in.read();
         switch ((short)id) {
            case -123:
            case -122:
            case -121:
            case -120:
            case -119:
            case -118:
            case -117:
            case -116:
            case -115:
            case -114:
            case -113:
            case -112:
            case -111:
            case -109:
            case -108:
            case -107:
            case -106:
            case -105:
            case -104:
            case -103:
            case -102:
            case -101:
            case -99:
            case -98:
            case -97:
            case -96:
            case -95:
               break;
            case 2:
            case 128:
            case 129:
            case 130:
            case 131:
               in.readMBInt();
               break;
            case 3:
            case 64:
            case 65:
            case 66:
               in.skipInlineString();
               break;
            case 195:
               return;
            default:
               in.reset();
               return;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final String getStringFromStringTable(int index) {
      if (index < 0 | index >= this._stringTable.length) {
         return "";
      }

      byte next = this._stringTable[index];
      int start = index;

      int length;
      for (length = 0; next != 0 && index < this._stringTable.length; length++) {
         next = this._stringTable[++index];
      }

      String retVal = null;
      boolean var8 = false /* VF: Semaphore variable */;

      try {
         var8 = true;
         retVal = (String)(new Object(this._stringTable, start, length, this._encoding));
         var8 = false;
      } finally {
         if (var8) {
            return (String)(new Object(this._stringTable, start, length));
         }
      }

      return retVal;
   }

   private final int getInlineOffset(WAPInputStream in) {
      int count = 0;

      int i;
      do {
         i = in.read();
         count++;
         if (i == -1) {
            throw new Object();
         }
      } while (i != 0);

      return count;
   }

   private final int readMBIntCount(WAPInputStream in) {
      int result = 0;
      int cnt = 0;

      int i;
      do {
         try {
            i = in.read();
         } finally {
            ;
         }

         if (i == -1) {
            return -1;
         }

         result = result << 7 | i & 127;
         cnt++;
      } while ((i & 128) != 0);

      return cnt;
   }
}
