package net.rim.device.apps.internal.api.serialformats;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.util.StringUtilities;

class TokenWriter {
   private OutputStream _out;
   private int _length;
   private String _encoding;
   private boolean _isUTF8;
   private static final int TAB;
   private static final int SPACE;
   private static final int CR;
   private static final int LF;
   private static final byte[] CRLF = new byte[]{13, 10};
   private static int LINE_FOLDING_LIMIT = 75;

   protected TokenWriter(OutputStream out, String encoding) {
      this._out = out;
      this._encoding = encoding.toLowerCase();
      if (this._encoding.equals("utf8") || this._encoding.equals("utf-8")) {
         this._isUTF8 = true;
      }
   }

   protected void resetLength() {
      this._length = 0;
   }

   protected void addOneByte(int c) {
      if (this._length == LINE_FOLDING_LIMIT) {
         this._out.write(CRLF);
         this._out.write(32);
         this._out.write(c);
         this._length = 2;
      } else {
         this._out.write(c);
         this._length++;
      }
   }

   protected void addLineBreak() {
      this._out.write(CRLF);
      this._length = 0;
   }

   protected void addProperty(String property, String value) {
      this.addPropertyTag(property);
      this.addString(value);
      this.addLineBreak();
   }

   protected void addPropertyTag(String property) {
      byte[] data;
      if (this._isUTF8 && StringUtilities.isASCII(property)) {
         data = property.getBytes();
      } else {
         data = property.getBytes(this._encoding);
      }

      this._out.write(data);
      this._length += data.length;
   }

   protected void addByteArray(byte[] value) {
      for (int c : value) {
         if (this._length == LINE_FOLDING_LIMIT) {
            this._out.write(CRLF);
            this._out.write(32);
            this._length = 1;
         } else if (this._length == LINE_FOLDING_LIMIT - 1 && (c == 92 || c == 59 || c == 44 || c == 10)) {
            this._out.write(CRLF);
            this._out.write(32);
            this._length = 1;
         }

         switch (c) {
            case 10:
               this._out.write(92);
               this._out.write(110);
               this._length += 2;
               break;
            case 44:
            case 59:
            case 92:
               this._out.write(92);
               this._out.write(c);
               this._length += 2;
               break;
            default:
               this._out.write(c);
               this._length++;
         }
      }
   }

   protected void addDataInQuotedPrintableFormat(byte[] data) {
      int MAXIMUM_LENGTH = 76;
      int i = 0;
      int lineLength = 0;
      int dataLength = data.length;

      while (i < dataLength) {
         if (lineLength == MAXIMUM_LENGTH) {
            this.addString("=");
            this.addLineBreak();
            lineLength = 0;
         } else if (i < dataLength && data[i] == 10) {
            if (lineLength + 6 <= MAXIMUM_LENGTH) {
               this.addString("=0D=0A");
               lineLength += 6;
            } else {
               this.addString("=");
               this.addLineBreak();
               this.addString("=0D=0A");
               lineLength = 6;
            }

            i++;
         } else {
            if (data[i] != 9 && data[i] != 32) {
               this._length = 0;
               this.addOneByte(data[i]);
               lineLength++;
            } else if (lineLength == 73 && i + 1 < dataLength && data[i + 1] == 10) {
               if (data[i] == 9) {
                  this.addString("=09");
               } else {
                  this.addString("=20");
               }

               lineLength += 3;
            } else {
               this._length = 0;
               this.addOneByte(data[i]);
               lineLength++;
            }

            i++;
         }
      }

      this.addLineBreak();
   }

   protected void addDataInBase64Format(byte[] data) {
      ByteArrayOutputStream byteOut = (ByteArrayOutputStream)(new Object());
      Base64OutputStream base64Out = (Base64OutputStream)(new Object(byteOut));
      base64Out.write(data);
      base64Out.flush();
      this.addByteArray(byteOut.toByteArray());
      this.addLineBreak();
   }

   protected void addString(String string) {
      byte[] data;
      if (this._isUTF8 && StringUtilities.isASCII(string)) {
         data = string.getBytes();
      } else {
         data = string.getBytes(this._encoding);
      }

      this.addByteArray(data);
   }
}
