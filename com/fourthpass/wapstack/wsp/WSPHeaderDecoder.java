package com.fourthpass.wapstack.wsp;

import com.fourthpass.wapstack.util.VarLengthInt;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.cldc.io.waphttp.HeaderDecoder;

public final class WSPHeaderDecoder {
   private byte[] _varIntBuffer;
   private int _pos;
   private byte[] _buf;
   private HttpHeaders _nameValuePairs;
   private static SimpleDateFormat _dateFormat = (SimpleDateFormat)(new Object("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.get(1701707776)));
   private static final int DEFAULT_STRING_BUFFER_LENGTH = 128;

   public WSPHeaderDecoder(HttpHeaders decodeTo) {
      this._nameValuePairs = decodeTo;
      this._varIntBuffer = new byte[8];
   }

   public final void decode(byte[] buf, boolean reply) {
      this._buf = buf;
      if (reply) {
         this.decodeContentType();
      }

      while (this._pos < this._buf.length) {
         this.parseHeader();
      }
   }

   private final void parseHeader() {
      byte name = this.readOctet();
      byte type = 0;
      if ((name & 128) != 0) {
         name = (byte)(name & 127);
         type = this.getFieldValueInterpretation(this.readOctet());
      } else {
         name = -1;
      }

      switch (name) {
         case -1:
         case 59:
         case 60:
         case 61:
         case 62:
            this.handleCustomFieldValue();
         case 56:
            return;
         case 0:
         default:
            this.handleAccept(type);
            return;
         case 1:
            this.handleAcceptCharset(type);
            return;
         case 2:
            this.handleAcceptEncoding(type);
            return;
         case 3:
            this.handleAcceptLanguage(type);
            return;
         case 4:
            this.handleAcceptRanges(type);
            return;
         case 5:
            this.handleAge(type);
            return;
         case 6:
            this.handleAllow(type);
            return;
         case 7:
            this.handleAuthorization("authorization", type);
            return;
         case 8:
            this.handleCacheControl(type);
            return;
         case 9:
            this.handleConnection(type);
            return;
         case 10:
            this.handleURI("content-base", type);
            return;
         case 11:
            this.handleContentEncoding(type);
            return;
         case 12:
            this.handleContentLanguage(type);
            return;
         case 13:
            this.handleInteger("content-length", type);
            return;
         case 14:
            this.handleURI("content-location", type);
            return;
         case 15:
            this.handleContentMD5(type);
            return;
         case 16:
            this.handleContentRange(type);
            return;
         case 17:
            this.handleContentType(type);
            return;
         case 18:
            this.handleDate("date", type);
            return;
         case 19:
            this.handleTextString("etag", type);
            return;
         case 20:
            this.handleDate("expires", type);
            return;
         case 21:
            this.handleTextString("from", type);
            return;
         case 22:
            this.handleTextString("host", type);
            return;
         case 23:
            this.handleDate("if-modified-since", type);
            return;
         case 24:
            this.handleTextString("if-match", type);
            return;
         case 25:
            this.handleTextString("if-none-match", type);
            return;
         case 26:
            this.handleIfRange(type);
            return;
         case 27:
            this.handleDate("if-unmodified-since", type);
            return;
         case 28:
            this.handleURI("location", type);
            return;
         case 29:
            this.handleDate("last-modified", type);
            return;
         case 30:
            this.handleInteger("max-forwards", type);
            return;
         case 31:
            this.handlePragma(type);
            return;
         case 32:
            this.handleAuthenticate("proxy-authenticate", type);
            return;
         case 33:
            this.handleAuthorization("proxy-authorization", type);
            return;
         case 34:
            this.handlePublic(type);
            return;
         case 35:
            this.handleRange(type);
            return;
         case 36:
            this.handleURI("referer", type);
            return;
         case 37:
            this.handleRetryAfter(type);
            return;
         case 38:
            this.handleTextString("server", type);
            return;
         case 39:
            this.handleTransferEncoding(type);
            return;
         case 40:
            this.handleTextString("upgrade", type);
            return;
         case 41:
            this.handleTextString("user-agent", type);
            return;
         case 42:
            this.handleVary(type);
            return;
         case 43:
            this.handleTextString("via", type);
            return;
         case 44:
            this.handleWarning(type);
            return;
         case 45:
            this.handleAuthenticate("www-authenticate", type);
            return;
         case 46:
            this.handleContentDisposition(type);
            return;
         case 47:
            this.handleXWapApplicationId(type);
            return;
         case 48:
            this.handleURI("x-wap-content-uri", type);
            return;
         case 49:
            this.handleURI("x-wap-initiator-uri", type);
            return;
         case 50:
            this.handleAcceptApplication(type);
            return;
         case 51:
            this.handleInteger("bearer-indication", type);
            return;
         case 52:
            this.handlePushFlag(type);
            return;
         case 53:
            this.handleURI("profile", type);
            return;
         case 54:
            this.handleProfileDiff(type);
            return;
         case 55:
            this.handleProfileWarning(type);
            return;
         case 57:
            this.handleTE(type);
            return;
         case 58:
            this.handleTrailer(type);
            return;
         case 63:
            this.handleDate("x-wap-tod", type);
            return;
         case 64:
            this.handleContentID(type);
            return;
         case 65:
            this.handleSetCookie(type);
            return;
         case 66:
            this.handleCookie(type);
            return;
         case 67:
            this.handleEncodingVersion(type);
      }
   }

   private final String readTextString() {
      int start = this._pos;
      int count = 0;

      while (this._pos < this._buf.length && this._buf[this._pos++] != 0) {
         count++;
      }

      return (String)(new Object(this._buf, start, count));
   }

   private final String readTextValue() {
      switch (this.readOctet()) {
         case -1:
         case 0:
            return null;
         case 34:
            return ((StringBuffer)(new Object())).append('"').append(this.readTextString()).append('"').toString();
         default:
            this._pos--;
            return this.readTextString();
      }
   }

   private final byte readOctet() {
      return this._pos >= this._buf.length ? -1 : this._buf[this._pos++];
   }

   private final byte getFieldValueInterpretation(byte b) {
      short ub = this.convShort(b);
      if (ub >= 0 && ub <= 30) {
         return 0;
      } else if (ub == 31) {
         return 1;
      } else if (ub >= 32 && ub <= 127) {
         return 2;
      } else {
         return (byte)(ub >= 128 && ub <= 255 ? 3 : 127);
      }
   }

   private final short convShort(byte b) {
      short s = (short)(b & 127);
      if (b < 0) {
         s = (short)(s | 128);
      }

      return s;
   }

   private static final boolean isText(char c) {
      return c == '\t' || c == '\n' || c == '\r' || c >= ' ' && c <= '~' || c >= 128 && c <= 255;
   }

   private final long readInteger() {
      if (this._buf[this._pos] >= 0 && this._buf[this._pos] <= 30) {
         this._pos++;
         return this.readLongInteger();
      } else {
         return this.readShortInteger();
      }
   }

   private final byte readShortInteger() {
      return this._pos >= this._buf.length ? -1 : (byte)(this._buf[this._pos++] & 127);
   }

   private static final long bytesToLong(byte[] value, short numBytes) {
      long retval = 0;

      for (int i = 0; i < numBytes; i++) {
         retval = retval << 8 | value[i] & 0xFF;
      }

      return retval;
   }

   private final long readLongInteger() {
      this._pos--;
      byte numBytes = this.readShortInteger();
      System.arraycopy(this._buf, this._pos, this._varIntBuffer, 0, numBytes);
      long retVal = bytesToLong(this._varIntBuffer, numBytes);
      this._pos += numBytes;
      return retVal;
   }

   private final int readValueLength() {
      int lengthByte = this._buf[this._pos - 1] & 255;
      if (lengthByte == 31) {
         lengthByte = (int)VarLengthInt.decode(this._buf, (short)this._pos);
         short numBytes = VarLengthInt.countEncodeBytes(lengthByte);
         this._pos += numBytes;
      }

      return lengthByte;
   }

   private final void parseParameters(StringBuffer sb, int numBytes) {
      int oldPos = this._pos;

      while (this._pos < oldPos + numBytes) {
         String paramName = null;
         String paramValue = null;
         boolean noValue = false;
         if (isText((char)this._buf[this._pos])) {
            paramName = this.readTextString();
            String var14 = null;
            if (isText((char)this._buf[this._pos])) {
               boolean isQuoted = (char)this._buf[this._pos] == '"';
               paramValue = this.readTextString();
               if (isQuoted) {
                  paramValue = ((StringBuffer)(new Object())).append(paramValue).append('"').toString();
               }
            } else {
               paramValue = Long.toString(this.readInteger());
            }
         } else {
            int type = this.readShortInteger();
            paramName = this.getWellKnowParameterString((byte)type);
            switch (type) {
               case 0:
                  int qValue = (int)VarLengthInt.decode(this._buf, (short)this._pos);
                  int varNumBytes = VarLengthInt.countEncodeBytes(qValue);
                  this._pos += varNumBytes;
                  if (qValue <= 100) {
                     qValue--;
                  } else {
                     qValue -= 100;
                  }

                  StringBuffer qBuffer = (StringBuffer)(new Object());

                  while (qValue > 0) {
                     int newQValue = qValue / 10;
                     int remainder = qValue % 10;
                     if (qBuffer.length() != 0 || remainder != 0) {
                        qBuffer.append(remainder);
                     }

                     qValue = newQValue;
                  }

                  qBuffer.append('.');
                  qBuffer.append('0');
                  paramValue = qBuffer.reverse().toString();
                  break;
               case 1:
                  paramValue = getCharsetNameNonNull((short)this.readInteger());
                  break;
               case 3:
                  paramValue = Long.toString(this.readInteger());
                  break;
               case 4:
                  paramValue = this.readTextString();
                  break;
               case 8:
               case 17:
                  paramValue = Integer.toString(this.readShortInteger());
                  break;
               case 18:
               case 23:
               case 24:
               case 25:
               case 26:
               case 27:
               case 28:
               case 29:
                  paramValue = this.readTextValue();
                  if (paramValue == null) {
                     noValue = true;
                  }
            }
         }

         if (paramValue != null || noValue) {
            sb.append(';');
            sb.append(paramName);
            if (!noValue) {
               sb.append('=');
               sb.append(paramValue);
            }
         }
      }

      if (this._pos != oldPos + numBytes) {
         this._pos = oldPos + numBytes;
      }
   }

   private final void addProperty(String name, String value) {
      this._nameValuePairs.addProperty(name, value);
   }

   private final void handleAccept(byte type) {
      String headerValue = null;
      StringBuffer headerValueBuffer = (StringBuffer)(new Object(128));
      switch (type) {
         case -1:
            break;
         case 0:
         case 1:
         default:
            int lengthByte = this.readValueLength();
            if (isText((char)this._buf[this._pos])) {
               int oldPos = this._pos;
               String extMedia = this.readTextString();
               headerValueBuffer.append(extMedia);
               int newPos = this._pos;
               if (newPos - oldPos < lengthByte) {
                  this.parseParameters(headerValueBuffer, lengthByte - (newPos - oldPos));
               }

               headerValue = headerValueBuffer.toString();
            } else {
               int oldPos = this._pos;
               long num = this.readInteger();
               headerValueBuffer.append(getContentTypeName(num));
               int newPos = this._pos;
               if (newPos - oldPos < lengthByte) {
                  this.parseParameters(headerValueBuffer, lengthByte - (newPos - oldPos));
               }

               headerValue = headerValueBuffer.toString();
            }
            break;
         case 2:
            this._pos--;
            headerValue = this.readTextString();
            break;
         case 3:
            this._pos--;
            headerValue = getContentTypeName(this.readShortInteger());
      }

      if (headerValue != null) {
         this.addProperty("accept", headerValue);
      }
   }

   private final void handleAcceptCharset(byte type) {
      String headerValue = null;
      switch (type) {
         case -1:
            headerValue = "?";
            break;
         case 0:
         case 1:
            StringBuffer sb = (StringBuffer)(new Object(128));
            int oldPos = this._pos;
            int lengthByte = this.readValueLength();
            if (isText((char)this._buf[this._pos])) {
               sb.append(this.readTextString());
            } else {
               sb.append(getCharsetNameNonNull((short)this.readInteger()));
            }

            int newPos = this._pos;
            if (newPos - oldPos < lengthByte && newPos - oldPos <= 2 && newPos - oldPos >= 1) {
               System.arraycopy(this._varIntBuffer, 0, this._buf, this._pos, this._pos + newPos - oldPos);
               long num = VarLengthInt.decode(this._varIntBuffer, (short)(newPos - oldPos));
               long d = (num - 1) / (10 * (newPos - oldPos + 1));
               sb.append(';');
               sb.append(' ');
               sb.append(String.valueOf(d));
            }

            headerValue = sb.toString();
            break;
         case 2:
            this._pos--;
            headerValue = this.readTextString();
            break;
         case 3:
         default:
            this._pos--;
            headerValue = getCharsetNameNonNull(this.readShortInteger());
      }

      if (headerValue != null) {
         this.addProperty("accept-charset", headerValue);
      }
   }

   private final void handleAcceptEncoding(byte type) {
      String headerValue = null;
      switch (type) {
         case 1:
            break;
         case 2:
         default:
            this._pos--;
            headerValue = this.readTextString();
            break;
         case 3:
            byte b = this._buf[this._pos - 1];
            if (b == -128) {
               headerValue = "Gzip";
            }

            if (b == -127) {
               headerValue = "Compress";
            }

            if (b == -126) {
               headerValue = "Deflate";
            }
      }

      if (headerValue != null) {
         this.addProperty("accept-encoding", headerValue);
      }
   }

   private final void handleAcceptLanguage(byte type) {
      String headerValue = null;
      switch (type) {
         case -1:
            break;
         case 0:
         case 1:
         default:
            headerValue = this.decodeLanguageGeneralFrom();
            break;
         case 2:
            this._pos--;
            headerValue = this.readTextString();
            break;
         case 3:
            this._pos--;
            headerValue = this.getLanguageName(this.readShortInteger());
      }

      if (headerValue != null) {
         this.addProperty("accept-language", headerValue);
      }
   }

   private final void handleAcceptRanges(byte type) {
      String headerValue = null;
      if (type == 2) {
         this._pos--;
         headerValue = this.readTextString();
      } else if (type == 3) {
         byte val = this._buf[this._pos - 1];
         if (val == -128) {
            headerValue = "none";
         } else if (val == -127) {
            headerValue = "bytes";
         }
      }

      if (headerValue != null) {
         this.addProperty("accept-ranges", headerValue);
      }
   }

   private final void handleAge(byte type) {
      String headerValue = null;
      if (type == 0) {
         headerValue = Long.toString(this.readLongInteger());
      } else if (type == 3) {
         this._pos--;
         headerValue = Integer.toString(this.readShortInteger());
      }

      if (headerValue != null) {
         this.addProperty("age", headerValue);
      }
   }

   private final void handleAllow(byte type) {
      String headerValue = null;
      if (type == 3) {
         this._pos--;
         headerValue = this.getEncodingName(this.readShortInteger());
      }

      if (headerValue != null) {
         this.addProperty("allow", headerValue);
      }
   }

   private final void handleCacheControl(byte type) {
      String headerValue = null;
      switch (type) {
         case -1:
            break;
         case 0:
         case 1:
         default:
            headerValue = this.getCacheControl();
            break;
         case 2:
            this._pos--;
            headerValue = this.readTextString();
            break;
         case 3:
            headerValue = this.getCacheControlName(this._buf[this._pos - 1] & 255);
      }

      if (headerValue != null) {
         this.addProperty("cache-control", headerValue);
      }
   }

   private final void handleConnection(byte type) {
      String headerValue = null;
      if (type == 2) {
         this._pos--;
         headerValue = this.readTextString();
      } else if (type == 3 && this._buf[this._pos - 1] == -128) {
         headerValue = "Close";
      }

      if (headerValue != null) {
         this.addProperty("connection", headerValue);
      }
   }

   private final void handleContentEncoding(byte type) {
      String headerValue = null;
      if (type == 2) {
         this._pos--;
         headerValue = this.readTextString();
      } else {
         byte val = this._buf[this._pos - 1];
         if (val == -128) {
            headerValue = "Gzip";
         } else if (val == -127) {
            headerValue = "Compress";
         } else if (val == -126) {
            headerValue = "Deflate";
         }
      }

      if (headerValue != null) {
         this.addProperty("content-encoding", headerValue);
      }
   }

   private final void handleContentLanguage(byte type) {
      String headerValue = null;
      if (type == 2) {
         this._pos--;
         headerValue = this.readTextString();
      } else if (type == 3) {
         this._pos--;
         headerValue = this.getLanguageName(this.readShortInteger());
      }

      if (headerValue != null) {
         this.addProperty("content-language", headerValue);
      }
   }

   private final void handleInteger(String header, byte type) {
      String headerValue = null;
      if (type == 0) {
         headerValue = Long.toString(this.readLongInteger());
      } else if (type == 3) {
         this._pos--;
         headerValue = Long.toString(this.readShortInteger());
      }

      if (headerValue != null) {
         this.addProperty(header, headerValue);
      }
   }

   private final void handleURI(String header, byte type) {
      String headerValue = null;
      if (type == 2) {
         this._pos--;
         headerValue = this.readURIValue();
      } else if (type == 0) {
         headerValue = Long.toString(this.readLongInteger());
      }

      if (headerValue != null) {
         this.addProperty(header, headerValue);
      }
   }

   private final void handleContentMD5(byte type) {
      String headerValue = null;
      if (type == 0 || type == 1) {
         int len = this.readValueLength();

         label35:
         try {
            ByteArrayOutputStream bytesOut = (ByteArrayOutputStream)(new Object());
            Base64OutputStream b64os = (Base64OutputStream)(new Object(bytesOut));
            b64os.write(this._buf, this._pos, len);
            b64os.close();
            headerValue = (String)(new Object(bytesOut.toByteArray()));
         } finally {
            break label35;
         }

         this._pos += len;
      }

      if (headerValue != null) {
         this.addProperty("content-md5", headerValue);
      }
   }

   private final void handleContentRange(byte type) {
      if (type == 0 || type == 1) {
         int len = this.readValueLength();
         this._pos += len;
      }
   }

   private final void handleContentType(byte type) {
      String headerValue = null;
      switch (type) {
         case -1:
            break;
         case 0:
         case 1:
         default:
            int len = this.readValueLength();
            if (len == 0) {
               return;
            }

            int oldPos = this._pos;
            type = this.getFieldValueInterpretation(this.readOctet());
            StringBuffer param = (StringBuffer)(new Object());
            if (type == 2) {
               this._pos--;
               String name = this.readTextString();
               this.parseParameters(param, len - (this._pos - oldPos));
               headerValue = ((StringBuffer)(new Object())).append(name).append(param.toString()).toString();
            } else if (type == 3 || type == 0) {
               this._pos--;
               String name = getContentTypeName(this.readInteger());
               this.parseParameters(param, len - (this._pos - oldPos));
               headerValue = ((StringBuffer)(new Object())).append(name).append(param.toString()).toString();
            }
            break;
         case 2:
            this._pos--;
            headerValue = this.readTextString();
            break;
         case 3:
            this._pos--;
            byte assNum = this.readShortInteger();
            headerValue = getContentTypeName(assNum);
      }

      if (headerValue != null) {
         this.addProperty("content-type", headerValue);
      }
   }

   private final void handleDate(String header, byte type) {
      String headerValue = null;
      if (type == 0) {
         Date date = (Date)(new Object(this.readLongInteger() * 1000));
         Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
         calendar.setTime(date);
         headerValue = _dateFormat.format(calendar);
      }

      if (headerValue != null) {
         this.addProperty(header, headerValue);
      }
   }

   private final void handleTextString(String header, byte type) {
      String headerValue = null;
      if (type == 2) {
         this._pos--;
         headerValue = this.readTextString();
      } else if (type == 3) {
         headerValue = this.readTextString();
      }

      if (headerValue != null) {
         this.addProperty(header, headerValue);
      }
   }

   private final void handleIfRange(byte type) {
      if (type == 0) {
         this.handleDate("if-range", type);
      } else {
         this.handleTextString("if-range", type);
      }
   }

   private final void handlePragma(byte type) {
      String headerValue = null;
      switch (type) {
         case -1:
         case 2:
            break;
         case 0:
         case 1:
         default:
            int len = this.readValueLength();
            StringBuffer stbuf = (StringBuffer)(new Object());
            this.parseParameters(stbuf, len);
            headerValue = stbuf.toString();
            break;
         case 3:
            if (this._buf[this._pos - 1] == -128) {
               headerValue = "No-cache";
            }
      }

      if (headerValue != null) {
         this.addProperty("pragma", headerValue);
      }
   }

   private final void handleAuthorization(String header, byte type) {
      String headerValue = null;
      if (type == 0 || type == 1) {
         int len = this.readValueLength();
         if (this._buf[this._pos] == -128) {
            this._pos++;
            String tempStr = ((StringBuffer)(new Object())).append(this.readTextString()).append(':').append(this.readTextString()).toString();

            label36:
            try {
               ByteArrayOutputStream bytesOut = (ByteArrayOutputStream)(new Object());
               Base64OutputStream b64os = (Base64OutputStream)(new Object(bytesOut));
               b64os.write(tempStr.getBytes());
               b64os.close();
               headerValue = ((StringBuffer)(new Object("Basic "))).append((String)(new Object(bytesOut.toByteArray()))).toString();
            } finally {
               break label36;
            }
         } else {
            int oldPos = this._pos;
            String auth_scheme = this.readTextString();
            StringBuffer stbuf = (StringBuffer)(new Object());
            this.parseParameters(stbuf, len - (this._pos - oldPos));
            headerValue = ((StringBuffer)(new Object())).append(auth_scheme).append(';').append(stbuf.toString()).toString();
         }
      }

      if (headerValue != null) {
         this.addProperty(header, headerValue);
      }
   }

   private final void handlePublic(byte type) {
      String headerValue = null;
      if (type == 2) {
         this._pos--;
         headerValue = this.readTextString();
      } else if (type == 3) {
         this._pos--;
         headerValue = this.getEncodingName(this.readShortInteger());
      }

      if (headerValue != null) {
         this.addProperty("public", headerValue);
      }
   }

   private final void handleRange(byte type) {
      if (type == 0 || type == 1) {
         int len = this.readValueLength();
         this._pos += len;
      }
   }

   private final void handleRetryAfter(byte type) {
      if (type == 0 || type == 1) {
         int len = this.readValueLength();
         this._pos += len;
      }
   }

   private final void handleTransferEncoding(byte type) {
      String headerName = "transfer-encoding";
      String headerValue = null;
      switch (type) {
         case 1:
            break;
         case 2:
         default:
            this._pos--;
            headerValue = this.readTextString();
            break;
         case 3:
            if (this._buf[this._pos - 1] == -128) {
               headerValue = "Chunked";
            }
      }

      if (headerValue != null) {
         this.addProperty(headerName, headerValue);
      }
   }

   private final void handleVary(byte type) {
      String headerValue = null;
      if (type == 2) {
         this._pos--;
         headerValue = this.readTextString();
      } else if (type == 3) {
         this._pos--;
         headerValue = Long.toString(this.readShortInteger());
      }

      if (headerValue != null) {
         this.addProperty("vary", headerValue);
      }
   }

   private final void handleWarning(byte type) {
      String headerName = "warning";
      String headerValue = null;
      switch (type) {
         case -1:
         case 2:
            break;
         case 0:
         case 1:
         default:
            this.readValueLength();
            StringBuffer buff = (StringBuffer)(new Object());
            int i = this.readShortInteger();
            if ((i < 10 || i > 13) && i != 99) {
               if (i == 14) {
                  buff.append(214);
               } else {
                  buff.append(i);
               }
            } else {
               buff.append(i + 100);
            }

            buff.append(' ');
            buff.append(this.readTextString());
            buff.append(' ');
            buff.append(this.readTextString());
            headerValue = buff.toString();
            break;
         case 3:
            this._pos--;
            headerValue = Integer.toString(this.readShortInteger());
      }

      if (headerValue != null) {
         this.addProperty(headerName, headerValue);
      }
   }

   private final void handleAuthenticate(String header, byte type) {
      String headerValue = null;
      if (type == 0 || type == 1) {
         int len = this.readValueLength();
         int oldPos = this._pos;
         if (this.readOctet() == -128) {
            headerValue = ((StringBuffer)(new Object("Basic realm=\""))).append(this.readTextString()).append("\"").toString();
         } else {
            this._pos--;
            String scheme = this.readTextString();
            String realm = null;
            if (!scheme.startsWith("Passport")) {
               realm = ((StringBuffer)(new Object("realm=\""))).append(this.readTextString()).append("\"").toString();
            }

            StringBuffer buffer = (StringBuffer)(new Object());
            this.parseParameters(buffer, len - (this._pos - oldPos));
            StringBuffer b = (StringBuffer)(new Object());
            b.append(scheme);
            b.append(' ');
            if (realm != null) {
               b.append(realm);
            }

            b.append(buffer);
            headerValue = b.toString();
         }
      }

      if (headerValue != null) {
         this.addProperty(header, headerValue);
      }
   }

   private final void handleContentDisposition(byte type) {
      if (type == 0 || type == 1) {
         int len = this.readValueLength();
         this._pos += len;
      }
   }

   private final void handleXWapApplicationId(byte type) {
      if (type == 0) {
         this.addProperty("x-wap-application-id", HeaderDecoder.getPushAppAssignedName((int)this.readLongInteger()));
      } else if (type == 3) {
         this._pos--;
         this.addProperty("x-wap-application-id", HeaderDecoder.getPushAppAssignedName(this.readShortInteger()));
      } else {
         if (type == 2) {
            this.handleURI("x-wap-application-id", type);
         }
      }
   }

   private final void handleAcceptApplication(byte type) {
      if (type != 0 && type != 3) {
         if (type == 2) {
            this.handleURI("accept-application", type);
         }
      } else {
         this.handleInteger("accept-application", type);
      }
   }

   private final void handlePushFlag(byte type) {
      String headerValue = null;
      if (type == 3) {
         this._pos--;
         headerValue = Integer.toString(this.readShortInteger());
      }

      if (headerValue != null) {
         this.addProperty("push-flag", headerValue);
      }
   }

   private final void handleProfileDiff(byte type) {
      if (type == 0 || type == 1) {
         int len = this.readValueLength();
         this._pos += len;
      }
   }

   private final void handleProfileWarning(byte type) {
      String headerValue = null;
      if (type == 0 || type == 1) {
         int len = this.readValueLength();
         this._pos += len;
      } else if (type == 3) {
         this._pos--;
         int value = this.readShortInteger();
         int newValue = (value >> 4) * 100;
         newValue += value & 15;
         headerValue = Integer.toString(newValue);
      }

      if (headerValue != null) {
         this.addProperty("profile-warning", headerValue);
      }
   }

   private final void handleTE(byte type) {
      if (type != 0 && type != 1) {
         if (type == 3) {
         }
      } else {
         int len = this.readValueLength();
         this._pos += len;
      }
   }

   private final void handleTrailer(byte type) {
      String headerValue = null;
      if (type == 2) {
         this._pos--;
         headerValue = this.readTextString();
      } else if (type == 3) {
         this._pos--;
         this.readShortInteger();
      }

      if (headerValue != null) {
         this.addProperty("trailer", headerValue);
      }
   }

   private final void handleContentID(byte type) {
      String headerValue = null;
      if (type == 2) {
         headerValue = this.readTextString();
      }

      if (headerValue != null) {
         this.addProperty("content-id", headerValue);
      }
   }

   private final void handleSetCookie(byte type) {
      String headerValue = null;
      switch (type) {
         case 0:
         case 1:
         default:
            int len = this.readValueLength();
            int oldPos = this._pos;
            type = this.getFieldValueInterpretation(this.readOctet());
            this._pos--;
            StringBuffer buffer = (StringBuffer)(new Object());
            String version;
            if (type == 2) {
               version = this.readTextString();
            } else {
               version = Integer.toString(this.readShortInteger());
            }

            buffer.append(this.readTextString());
            buffer.append("=");
            buffer.append(this.readTextString());
            buffer.append(";");
            buffer.append("Version");
            buffer.append("=");
            buffer.append(version);
            StringBuffer params = (StringBuffer)(new Object());
            this.parseParameters(params, len - (this._pos - oldPos));
            buffer.append(params.toString());
            headerValue = buffer.toString();
         case -1:
            if (headerValue != null) {
               this.addProperty("set-cookie", headerValue);
            }
      }
   }

   private final void handleCookie(byte type) {
      String headerValue = null;
      switch (type) {
         case 0:
         case 1:
         default:
            int len = this.readValueLength();
            int oldPos = this._pos;
            type = this.getFieldValueInterpretation(this.readOctet());
            this._pos--;
            StringBuffer buffer = (StringBuffer)(new Object());
            buffer.append("Version");
            buffer.append("=");
            if (type == 2) {
               buffer.append(this.readTextString());
            } else {
               buffer.append(this.readShortInteger());
            }

            int last_pos = 0;

            while (len > this._pos - oldPos) {
               buffer.append(",");
               int lengthByte = (int)VarLengthInt.decode(this._buf, (short)this._pos);
               short numBytes = VarLengthInt.countEncodeBytes(lengthByte);
               this._pos += numBytes;
               last_pos = this._pos;
               buffer.append(this.readTextString());
               buffer.append("=");
               buffer.append(this.readTextString());
               if (lengthByte > this._pos - last_pos) {
                  buffer.append(";");
                  buffer.append("Path");
                  buffer.append("=");
                  buffer.append(this.readTextString());
               }

               if (lengthByte > this._pos - last_pos) {
                  buffer.append(";");
                  buffer.append("Domain");
                  buffer.append("=");
                  buffer.append(this.readTextString());
               }
            }

            headerValue = buffer.toString();
         case -1:
            if (headerValue != null) {
               this.addProperty("cookie", headerValue);
            }
      }
   }

   private final void handleEncodingVersion(byte type) {
      String headerValue = null;
      switch (type) {
         case -1:
            break;
         case 0:
         default:
            type = this.getFieldValueInterpretation(this.readOctet());
            break;
         case 1:
            int value = this.readShortInteger();
            headerValue = ((StringBuffer)(new Object())).append(Integer.toString(value >> 4)).append('.').append(Integer.toString(value & 15)).toString();
            break;
         case 2:
            headerValue = this.readTextString();
            break;
         case 3:
            int length = this.readValueLength();
            this._pos += length;
      }

      if (headerValue != null) {
         this.addProperty("encoding-version", headerValue);
      }
   }

   private final void handleCustomFieldValue() {
      this._pos--;
      String headerName = this.readTextString();
      String headerValue = this.readTextString();
      if (headerValue != null) {
         this.addProperty(headerName, headerValue);
      }
   }

   private final String getLanguageName(byte n) {
      short lang = 0;
      switch (n) {
         case -128:
         case -115:
         case -114:
         case -113:
         case -112:
         case -111:
         case -110:
         case -109:
         case -108:
         case -107:
         case -106:
         case -105:
         case -104:
         case -103:
         case -102:
         case -101:
         case -100:
         case -99:
         case -98:
         case -97:
         case -96:
         case -95:
         case -94:
         case -93:
         case -92:
         case -91:
         case -90:
         case -89:
         case -88:
         case -87:
         case -86:
         case -85:
         case -84:
         case -83:
         case -82:
         case -81:
         case -80:
         case -79:
         case -78:
         case -77:
         case -76:
         case -75:
         case -74:
         case -73:
         case -72:
         case -71:
         case -70:
         case -69:
         case -68:
         case -67:
         case -66:
         case -65:
         case -64:
         case -63:
         case -62:
         case -61:
         case -60:
         case -59:
         case -58:
         case -57:
         case -56:
         case -55:
         case -54:
         case -53:
         case -52:
         case -51:
         case -50:
         case -49:
         case -48:
         case -47:
         case -46:
         case -45:
         case -44:
         case -43:
         case -42:
         case -41:
         case -40:
         case -39:
         case -38:
         case -37:
         case -36:
         case -35:
         case -34:
         case -33:
         case -32:
         case -31:
         case -30:
         case -29:
         case -28:
         case -27:
         case -26:
         case -25:
         case -24:
         case -23:
         case -22:
         case -21:
         case -20:
         case -19:
         case -18:
         case -17:
         case -16:
         case -15:
         case -14:
         case -13:
         case -12:
         case -11:
         case -10:
         case -9:
         case -8:
         case -7:
         case -6:
         case -5:
         case -4:
         case -3:
         case -2:
         case -1:
         case 0:
            return "?";
         case -127:
            lang = 28257;
            break;
         case -126:
            lang = 26223;
            break;
         case -125:
            lang = 26233;
            break;
         case -124:
            lang = 26977;
            break;
         case -123:
            lang = 30319;
            break;
         case -122:
            lang = 26981;
            break;
         case -121:
            lang = 26987;
            break;
         case -120:
            lang = 31081;
            break;
         case -119:
            lang = 26997;
            break;
         case -118:
            lang = 27500;
            break;
         case -117:
            lang = 27745;
            break;
         case -116:
            lang = 29293;
            break;
         case 1:
         default:
            lang = 24929;
            break;
         case 2:
            lang = 24930;
            break;
         case 3:
            lang = 24934;
            break;
         case 4:
            lang = 24941;
            break;
         case 5:
            lang = 24946;
            break;
         case 6:
            lang = 24947;
            break;
         case 7:
            lang = 24953;
            break;
         case 8:
            lang = 24954;
            break;
         case 9:
            lang = 25185;
            break;
         case 10:
            lang = 25189;
            break;
         case 11:
            lang = 25191;
            break;
         case 12:
            lang = 25192;
            break;
         case 13:
            lang = 25193;
            break;
         case 14:
            lang = 25198;
            break;
         case 15:
            lang = 25199;
            break;
         case 16:
            lang = 25202;
            break;
         case 17:
            lang = 25441;
            break;
         case 18:
            lang = 25455;
            break;
         case 19:
            lang = 25459;
            break;
         case 20:
            lang = 25465;
            break;
         case 21:
            lang = 25697;
            break;
         case 22:
            lang = 25701;
            break;
         case 23:
            lang = 25722;
            break;
         case 24:
            lang = 25964;
            break;
         case 25:
            lang = 25966;
            break;
         case 26:
            lang = 25967;
            break;
         case 27:
            lang = 25971;
            break;
         case 28:
            lang = 25972;
            break;
         case 29:
            lang = 25973;
            break;
         case 30:
            lang = 26209;
            break;
         case 31:
            lang = 26217;
            break;
         case 32:
            lang = 26218;
            break;
         case 33:
            lang = 30066;
            break;
         case 34:
            lang = 26226;
            break;
         case 35:
            lang = 30074;
            break;
         case 36:
            lang = 26465;
            break;
         case 37:
            lang = 26468;
            break;
         case 38:
            lang = 26476;
            break;
         case 39:
            lang = 26478;
            break;
         case 40:
            lang = 26485;
            break;
         case 41:
            lang = 26721;
            break;
         case 42:
            lang = 26725;
            break;
         case 43:
            lang = 26729;
            break;
         case 44:
            lang = 26738;
            break;
         case 45:
            lang = 26741;
            break;
         case 46:
            lang = 26745;
            break;
         case 47:
            lang = 30313;
            break;
         case 48:
            lang = 26980;
            break;
         case 49:
            lang = 30575;
            break;
         case 50:
            lang = 30824;
            break;
         case 51:
            lang = 26995;
            break;
         case 52:
            lang = 26996;
            break;
         case 53:
            lang = 31087;
            break;
         case 54:
            lang = 27233;
            break;
         case 55:
            lang = 27255;
            break;
         case 56:
            lang = 27489;
            break;
         case 57:
            lang = 27499;
            break;
         case 58:
            lang = 31329;
            break;
         case 59:
            lang = 27501;
            break;
         case 60:
            lang = 27502;
            break;
         case 61:
            lang = 27503;
            break;
         case 62:
            lang = 27507;
            break;
         case 63:
            lang = 27509;
            break;
         case 64:
            lang = 27513;
            break;
         case 65:
            lang = 31336;
            break;
         case 66:
            lang = 27758;
            break;
         case 67:
            lang = 27759;
            break;
         case 68:
            lang = 27764;
            break;
         case 69:
            lang = 27766;
            break;
         case 70:
            lang = 28007;
            break;
         case 71:
            lang = 28009;
            break;
         case 72:
            lang = 28011;
            break;
         case 73:
            lang = 28012;
            break;
         case 74:
            lang = 28014;
            break;
         case 75:
            lang = 28015;
            break;
         case 76:
            lang = 28018;
            break;
         case 77:
            lang = 28019;
            break;
         case 78:
            lang = 28020;
            break;
         case 79:
            lang = 28025;
            break;
         case 80:
            lang = 30059;
            break;
         case 81:
            lang = 28261;
            break;
         case 82:
            lang = 28268;
            break;
         case 83:
            lang = 28271;
            break;
         case 84:
            lang = 28515;
            break;
         case 85:
            lang = 28525;
            break;
         case 86:
            lang = 28530;
            break;
         case 87:
            lang = 28769;
            break;
         case 88:
            lang = 28783;
            break;
         case 89:
            lang = 28787;
            break;
         case 90:
            lang = 28788;
            break;
         case 91:
            lang = 29045;
            break;
         case 92:
            lang = 31349;
            break;
         case 93:
            lang = 29294;
            break;
         case 94:
            lang = 29295;
            break;
         case 95:
            lang = 29301;
            break;
         case 96:
            lang = 29303;
            break;
         case 97:
            lang = 29537;
            break;
         case 98:
            lang = 29540;
            break;
         case 99:
            lang = 29543;
            break;
         case 100:
            lang = 29544;
            break;
         case 101:
            lang = 29545;
            break;
         case 102:
            lang = 29547;
            break;
         case 103:
            lang = 29548;
            break;
         case 104:
            lang = 29549;
            break;
         case 105:
            lang = 29550;
            break;
         case 106:
            lang = 29551;
            break;
         case 107:
            lang = 29553;
            break;
         case 108:
            lang = 29554;
            break;
         case 109:
            lang = 29555;
            break;
         case 110:
            lang = 29556;
            break;
         case 111:
            lang = 29557;
            break;
         case 112:
            lang = 29558;
            break;
         case 113:
            lang = 29559;
            break;
         case 114:
            lang = 29793;
            break;
         case 115:
            lang = 29797;
            break;
         case 116:
            lang = 29799;
            break;
         case 117:
            lang = 29800;
            break;
         case 118:
            lang = 29801;
            break;
         case 119:
            lang = 29803;
            break;
         case 120:
            lang = 29804;
            break;
         case 121:
            lang = 29806;
            break;
         case 122:
            lang = 29807;
            break;
         case 123:
            lang = 29810;
            break;
         case 124:
            lang = 29811;
            break;
         case 125:
            lang = 29812;
            break;
         case 126:
            lang = 29815;
            break;
         case 127:
            lang = 30055;
      }

      return (String)(new Object(new char[]{(char)(lang >> 8), (char)(lang & 0xFF)}));
   }

   static final String getContentTypeName(long n) {
      return HeaderDecoder.getContentTypeAssignedName((int)(n & 65535));
   }

   public static final String getCharsetName(int mibenum) {
      switch (mibenum) {
         case 3:
            return "us-ascii";
         case 4:
            return "iso-8859-1";
         case 5:
            return "iso-8859-2";
         case 6:
            return "iso-8859-3";
         case 7:
            return "iso-8859-4";
         case 8:
            return "iso-8859-5";
         case 9:
            return "iso-8859-6";
         case 10:
            return "iso-8859-7";
         case 11:
            return "iso-8859-8";
         case 12:
            return "iso-8859-9";
         case 13:
            return "iso-8859-10";
         case 17:
            return "shift_JIS";
         case 18:
            return "EUC-JP";
         case 37:
            return "ISO-2022-KR";
         case 38:
            return "EUC-KR";
         case 39:
            return "ISO-2022-JP";
         case 85:
            return "iso-8859-8-i";
         case 104:
            return "ISO-2022-CN";
         case 106:
            return "utf-8";
         case 109:
            return "iso-8859-13";
         case 110:
            return "iso-8859-14";
         case 111:
            return "iso-8859-15";
         case 112:
            return "iso-8859-16";
         case 113:
            return "GBK";
         case 114:
            return "GB18030";
         case 1000:
            return "iso-10646-ucs-2";
         case 1013:
            return "utf-16be";
         case 1014:
            return "utf-16le";
         case 1015:
            return "utf-16";
         case 2025:
            return "gb2312";
         case 2026:
            return "big5";
         case 2082:
            return "VISCII";
         case 2084:
            return "KOI8-R";
         case 2088:
            return "KOI8-U";
         case 2101:
            return "Big5-HKSCS";
         case 2250:
            return "windows-1250";
         case 2251:
            return "windows-1251";
         case 2252:
            return "windows-1252";
         case 2253:
            return "windows-1253";
         case 2254:
            return "windows-1254";
         case 2255:
            return "windows-1255";
         case 2256:
            return "windows-1256";
         case 2257:
            return "windows-1257";
         case 2258:
            return "windows-1258";
         case 2259:
            return "TIS-620";
         default:
            return null;
      }
   }

   private static final String getCharsetNameNonNull(short n) {
      String charsetName = getCharsetName(n);
      return charsetName == null ? "?" : charsetName;
   }

   private final String getEncodingName(byte b) {
      switch (b) {
         case 64:
            return "GET";
         case 65:
            return "OPTIONS";
         case 66:
            return "HEAD";
         case 67:
            return "DELETE";
         case 68:
            return "TRACE";
         case 96:
            return "POST";
         case 97:
            return "PUT";
         default:
            return "?";
      }
   }

   private final String getCacheControlName(int val) {
      switch (val) {
         case 127:
            return "?";
         case 128:
         default:
            return "No-cache";
         case 129:
            return "No-store";
         case 130:
            return "Max-age";
         case 131:
            return "Max-stale";
         case 132:
            return "Min-fresh";
         case 133:
            return "Only-if-cache";
         case 134:
            return "public";
         case 135:
            return "private";
         case 136:
            return "No-transform";
         case 137:
            return "Must-revalidate";
         case 138:
            return "Proxy-revalidate";
      }
   }

   private final String readURIValue() {
      return this.readTextString();
   }

   private final String getCacheControl() {
      long len = this.readValueLength();
      long count = 1;
      long numBytes = 0;
      String field = "";
      int first = this._buf[this._pos++] & 255;
      String cacheName;
      if (first >= 128 && first < 255) {
         cacheName = this.getCacheControlName(first);
         count = 1;
      } else {
         this._pos--;
         numBytes = this._pos;
         cacheName = this.readTextString();
         count += this._pos - numBytes;
      }

      switch (first) {
         case 127:
            if (isText((char)this._buf[this._pos])) {
               numBytes = this._pos;
               field = this.readTextString();
               count += this._pos - numBytes;
            } else {
               field = new Object(this.readShortInteger()).toString();
               count += 1;
            }
            break;
         case 128:
         default:
            field = "\"";

            while (count < len) {
               int var19 = this._buf[this._pos];
               if (var19 > 128 && var19 < 255) {
                  field = ((StringBuffer)(new Object())).append(field).append(',').append(new Object(this.readShortInteger()).toString()).toString();
                  count += 1;
               } else {
                  numBytes = this._pos;
                  field = ((StringBuffer)(new Object())).append(field).append(',').append(this.readTextString()).toString();
                  count += this._pos - numBytes;
               }
            }

            field = ((StringBuffer)(new Object())).append(field).append('"').toString();
         case 129:
         case 133:
         case 134:
         case 136:
         case 137:
         case 138:
            break;
         case 130:
            field = this.readDeltaSeconds();
            break;
         case 131:
            field = this.readDeltaSeconds();
            break;
         case 132:
            field = this.readDeltaSeconds();
            break;
         case 135:
            while (count < len) {
               first = this._buf[this._pos] & 255;
               if (first > 128) {
                  field = ((StringBuffer)(new Object())).append(field).append(',').append(new Object(this.readShortInteger()).toString()).toString();
                  count += 1;
               } else {
                  numBytes = this._pos;
                  field = ((StringBuffer)(new Object())).append(field).append(',').append(this.readTextString()).toString();
                  count += this._pos - numBytes;
               }
            }
      }

      return field == null && field.length() <= 0 ? cacheName : ((StringBuffer)(new Object())).append(cacheName).append('=').append(field).toString();
   }

   private final String decodeLanguageGeneralFrom() {
      this.readValueLength();
      return "";
   }

   private final String readDeltaSeconds() {
      byte i = this._buf[this._pos];
      if (i >= -128 && i <= 0) {
         return new Object(this.readShortInteger()).toString();
      } else if (i >= 0 && i <= 30) {
         this._pos++;
         return Long.toString(this.readLongInteger());
      } else {
         return "";
      }
   }

   private final String getWellKnowParameterString(byte num) {
      for (int i = 0; i < WSPHeaderConstants.WELL_KNOWN_PARAMETER_VALUE.length; i++) {
         if (WSPHeaderConstants.WELL_KNOWN_PARAMETER_VALUE[i] == num) {
            return WSPHeaderConstants.WELL_KNOWN_PARAMETER[i];
         }
      }

      return null;
   }

   private final void decodeContentType() {
      byte type = this.getFieldValueInterpretation(this.readOctet());
      this.handleContentType(type);
   }
}
