package com.fourthpass.wapstack.wsp;

import com.fourthpass.wapstack.util.Utils;
import com.fourthpass.wapstack.util.VarLengthInt;
import java.io.ByteArrayInputStream;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.browser.util.TimeLogger;

public final class WSPHeaderEncoder {
   private byte[] _encodedHeaders = new byte[32];
   private int _encodedLength;
   private byte[] _VarLenIntBuf = new byte[8];
   private byte[] _tempBuffer;
   private int _tempBufLen;
   private byte[] _nextTempBuffer;
   private int _nextTempBufLen;
   private static final int DEFAULT_BUFFER_INCR = 64;
   private static final int DEFAULT_TEMP_BUF_SIZE = 64;

   public final void encode(String name, String value) {
      if (name != null && value != null) {
         if (TimeLogger._loggingEnabled) {
            TimeLogger.getInstance().startTimer(6, 0);
         }

         byte type = getNameTypeCode(name);
         switch (type) {
            case -1:
            case 59:
            case 60:
            case 61:
            case 62:
               this.handleCustomHeader(name, value);
               break;
            case 0:
            default:
               this.handleHeaderAccept(value);
               break;
            case 1:
               this.handleHeaderAcceptCharset(value);
               break;
            case 2:
               this.handleHeaderAcceptEncoding(value);
               break;
            case 3:
               this.handleHeaderAcceptLanguage(value);
               break;
            case 4:
               this.handleHeaderAcceptRanges(value);
               break;
            case 5:
               this.handleHeaderAge(value);
               break;
            case 6:
               this.handleHeaderAllow(value);
               break;
            case 7:
            case 33:
               this.handleHeaderAuthorization(type, value);
               break;
            case 8:
               this.handleHeaderCacheControl(value);
               break;
            case 9:
               this.handleHeaderConnection(value);
               break;
            case 10:
               this.handleHeaderContentBase(value);
               break;
            case 11:
               this.handleHeaderContentEncoding(value);
               break;
            case 12:
               this.handleHeaderContentLanguage(value);
               break;
            case 13:
               this.handleHeaderContentLength(value);
               break;
            case 14:
               this.handleHeaderContentLocation(value);
               break;
            case 15:
               this.handleHeaderContentMD5(value);
               break;
            case 16:
               this.handleHeaderContentRange(value);
               break;
            case 17:
               this.handleHeaderContentType(value, true);
               break;
            case 18:
            case 20:
            case 23:
            case 27:
            case 29:
            case 63:
               this.handleHeaderDate(type, value);
               break;
            case 19:
               this.handleHeaderEtag(value);
               break;
            case 21:
               this.handleHeaderFrom(value);
               break;
            case 22:
               this.handleHeaderHost(value);
               break;
            case 24:
               this.handleHeaderIfMatch(value);
               break;
            case 25:
               this.handleHeaderIfNoneMatch(value);
               break;
            case 26:
               this.handleHeaderIfRange(value);
               break;
            case 28:
               this.handleHeaderLocation(value);
               break;
            case 30:
               this.handleHeaderMaxForwards(value);
               break;
            case 31:
               this.handleHeaderPragma(value);
               break;
            case 32:
            case 45:
               this.handleHeaderAuthenticate(type, value);
               break;
            case 34:
               this.handleHeaderPublic(value);
               break;
            case 35:
               this.handleHeaderRange(value);
               break;
            case 36:
               this.handleHeaderReferer(value);
               break;
            case 37:
               this.handleHeaderRetryAfter(value);
               break;
            case 38:
               this.handleHeaderServer(value);
               break;
            case 39:
               this.handleHeaderTransferEncoding(value);
               break;
            case 40:
               this.handleHeaderUpgrade(value);
               break;
            case 41:
               this.handleHeaderUserAgent(value);
               break;
            case 42:
               this.handleHeaderVary(value);
               break;
            case 43:
               this.handleHeaderVia(value);
               break;
            case 44:
               this.handleHeaderWarning(value);
               break;
            case 46:
               this.handleHeaderContentDisposition(value);
               break;
            case 47:
               this.handleHeaderXWapApplicationId(value);
               break;
            case 48:
               this.handleHeaderXWapContentURI(value);
               break;
            case 49:
               this.handleHeaderXWapInitiatorURI(value);
               break;
            case 50:
               this.handleHeaderAcceptApplication(value);
               break;
            case 51:
               this.handleHeaderBearerIndication(value);
               break;
            case 52:
               this.handleHeaderPushFlag(value);
               break;
            case 53:
               this.handleHeaderProfile(value);
               break;
            case 54:
               this.handleHeaderProfileDiff(value);
               break;
            case 55:
               this.handleHeaderProfileWarning(value);
               break;
            case 56:
               this.handleHeaderExpect(value);
               break;
            case 57:
               this.handleHeaderTE(value);
               break;
            case 58:
               this.handleHeaderTrailer(value);
               break;
            case 64:
               this.handleHeaderContentID(value);
               break;
            case 65:
               this.handleHeaderSetCookie(value);
               break;
            case 66:
               this.handleHeaderCookie(value);
               break;
            case 67:
               this.handleHeaderEncodingVersion(value);
         }

         if (TimeLogger._loggingEnabled) {
            TimeLogger.getInstance().stopTimer(6, 0);
         }
      }
   }

   private static final byte getNameTypeCode(String name) {
      int hash = getHash(name);
      String value = null;
      byte id = 127;
      switch (hash) {
         case 18:
            id = 57;
            value = "te";
            break;
         case 606:
            id = 5;
            value = "age";
            break;
         case 619:
            id = 43;
            value = "via";
            break;
         case 1036:
            id = 19;
            value = "etag";
            break;
         case 1046:
            id = 18;
            value = "date";
            break;
         case 1099:
            id = 21;
            value = "from";
            break;
         case 1135:
            id = 22;
            value = "host";
            break;
         case 1138:
            id = 42;
            value = "vary";
            break;
         case 1555:
            id = 35;
            value = "range";
            break;
         case 1676:
            id = 6;
            value = "allow";
            break;
         case 2170:
            id = 31;
            value = "pragma";
            break;
         case 2191:
            id = 34;
            value = "public";
            break;
         case 2213:
            id = 66;
            value = "cookie";
            break;
         case 2252:
            id = 0;
            value = "accept";
            break;
         case 2272:
            id = 56;
            value = "expect";
            break;
         case 2320:
            id = 38;
            value = "server";
            break;
         case 2898:
            id = 40;
            value = "upgrade";
            break;
         case 2961:
            id = 53;
            value = "profile";
            break;
         case 2999:
            id = 58;
            value = "trailer";
            break;
         case 3000:
            id = 36;
            value = "referer";
            break;
         case 3001:
            id = 44;
            value = "warning";
            break;
         case 3078:
            id = 20;
            value = "expires";
            break;
         case 3574:
            id = 26;
            value = "if-range";
            break;
         case 3586:
            id = 24;
            value = "if-match";
            break;
         case 3882:
            id = 28;
            value = "location";
            break;
         case 4385:
            id = 63;
            value = "x-wap-tod";
            break;
         case 4403:
            id = 52;
            value = "push-flag";
            break;
         case 5397:
            id = 64;
            value = "content-id";
            break;
         case 5592:
            id = 41;
            value = "user-agent";
            break;
         case 5594:
            id = 65;
            value = "set-cookie";
            break;
         case 5941:
            id = 9;
            value = "connection";
            break;
         case 6016:
            id = 15;
            value = "content-md5";
            break;
         case 6798:
            id = 37;
            value = "retry-after";
            break;
         case 7617:
            id = 54;
            value = "profile-diff";
            break;
         case 7781:
            id = 10;
            value = "content-base";
            break;
         case 8150:
            id = 17;
            value = "content-type";
            break;
         case 8262:
            id = 30;
            value = "max-forwards";
            break;
         case 8933:
            id = 25;
            value = "if-none-match";
            break;
         case 9199:
            id = 29;
            value = "last-modified";
            break;
         case 9207:
            id = 16;
            value = "content-range";
            break;
         case 9292:
            id = 4;
            value = "accept-ranges";
            break;
         case 9502:
            id = 8;
            value = "cache-control";
            break;
         case 9995:
            id = 7;
            value = "authorization";
            break;
         case 10836:
            id = 1;
            value = "accept-charset";
            break;
         case 10844:
            id = 13;
            value = "content-length";
            break;
         case 12159:
            id = 3;
            value = "accept-language";
            break;
         case 12226:
            id = 2;
            value = "accept-encoding";
            break;
         case 12386:
            id = 55;
            value = "profile-warning";
            break;
         case 13880:
            id = 12;
            value = "content-language";
            break;
         case 13950:
            id = 11;
            value = "content-encoding";
            break;
         case 14190:
            id = 14;
            value = "content-location";
            break;
         case 14240:
            id = 67;
            value = "encoding-version";
            break;
         case 14271:
            id = 45;
            value = "www-authenticate";
            break;
         case 15097:
            id = 23;
            value = "if-modified-since";
            break;
         case 15449:
            id = 48;
            value = "x-wap-content-uri";
            break;
         case 15623:
            id = 39;
            value = "transfer-encoding";
            break;
         case 15830:
            id = 51;
            value = "bearer-indication";
            break;
         case 17851:
            id = 50;
            value = "accept-application";
            break;
         case 17963:
            id = 32;
            value = "proxy-authenticate";
            break;
         case 18931:
            id = 27;
            value = "if-unmodified-since";
            break;
         case 19352:
            id = 49;
            value = "x-wap-initiator-uri";
            break;
         case 20368:
            id = 46;
            value = "content-disposition";
            break;
         case 20573:
            id = 33;
            value = "proxy-authorization";
            break;
         case 20702:
            id = 47;
            value = "x-wap-application-id";
      }

      return value != null && StringUtilities.compareToIgnoreCase(name, value, 1701707776) == 0 ? id : 127;
   }

   public final byte[] getEncodedHeader() {
      byte[] encoded = new byte[this._encodedLength];
      System.arraycopy(this._encodedHeaders, 0, encoded, 0, this._encodedLength);
      return encoded;
   }

   @Override
   public final String toString() {
      return new String(this._encodedHeaders, 0, this._encodedLength);
   }

   private final void ensureCapacity(int size) {
      if (this._encodedHeaders.length < size) {
         byte[] buf = new byte[size + 64];
         System.arraycopy(this._encodedHeaders, 0, buf, 0, this._encodedHeaders.length);
         this._encodedHeaders = null;
         this._encodedHeaders = buf;
      }
   }

   private final void ensureTempCapacity(int size) {
      if (this._tempBuffer == null) {
         this._tempBuffer = new byte[64];
      }

      if (this._tempBuffer.length < size) {
         byte[] buf = new byte[size + 64];
         System.arraycopy(this._tempBuffer, 0, buf, 0, this._tempBuffer.length);
         this._tempBuffer = null;
         this._tempBuffer = buf;
      }
   }

   private final void ensureNextTempCapacity(int size) {
      if (this._nextTempBuffer == null) {
         this._nextTempBuffer = new byte[64];
      }

      if (this._nextTempBuffer.length < size) {
         byte[] buf = new byte[size + 64];
         System.arraycopy(this._nextTempBuffer, 0, buf, 0, this._nextTempBufLen);
         this._nextTempBuffer = null;
         this._nextTempBuffer = buf;
      }
   }

   private final void writeOctet(byte octet, byte whichBuffer) {
      if (whichBuffer == 2) {
         this.ensureNextTempCapacity(this._nextTempBufLen + 1);
         this._nextTempBuffer[this._nextTempBufLen++] = octet;
      } else if (whichBuffer == 0) {
         this.ensureTempCapacity(this._tempBufLen + 1);
         this._tempBuffer[this._tempBufLen++] = octet;
      } else {
         this.ensureCapacity(this._encodedLength + 1);
         this._encodedHeaders[this._encodedLength++] = octet;
      }
   }

   private final void writeOctet(byte[] octets, int start, int end, byte whichBuffer) {
      if (end > start) {
         if (whichBuffer == 2) {
            this.ensureNextTempCapacity(this._nextTempBufLen + (end - start));

            for (int i = start; i < end; i++) {
               this._nextTempBuffer[this._nextTempBufLen++] = octets[i];
            }
         } else if (whichBuffer == 0) {
            this.ensureTempCapacity(this._tempBufLen + (end - start));

            for (int i = start; i < end; i++) {
               this._tempBuffer[this._tempBufLen++] = octets[i];
            }
         } else {
            this.ensureCapacity(this._encodedLength + (end - start));

            for (int i = start; i < end; i++) {
               this._encodedHeaders[this._encodedLength++] = octets[i];
            }
         }
      }
   }

   private final boolean isIntegerValue(String val) {
      try {
         int iVal = Integer.parseInt(val);
         return Integer.toString(iVal).length() == val.length();
      } finally {
         ;
      }
   }

   private final boolean isShortInteger(String val) {
      try {
         char firstByte = val.charAt(0);
         if ((firstByte < 'a' || firstByte > 'z') && (firstByte < 'A' || firstByte > 'Z')) {
            Integer.parseInt(val);
            return true;
         } else {
            return false;
         }
      } finally {
         ;
      }
   }

   private final void writeEncodedParameter(String name, String val, byte whichBuffer) {
      byte assignNum = getWellKnownParameterAssignedNumber(name);
      if (assignNum != 127) {
         name = Integer.toString(assignNum & 255);
      }

      int integerName = 0;
      byte type = 1;

      label191:
      try {
         integerName = Integer.parseInt(name);
         type = 0;
      } finally {
         break label191;
      }

      if (type == 0) {
         this.writeIntegerValue(integerName, whichBuffer);
         if (val != null && val.length() > 0) {
            switch (assignNum) {
               case -1:
               case 4:
               case 12:
               case 13:
               case 14:
               case 15:
               case 16:
                  this.writeTextValue(val, whichBuffer);
                  return;
               case 0:
               default:
                  this.writeQValue(val, whichBuffer);
                  return;
               case 1:
                  this.writeIntegerValue(getCharSetAssignedNumber(val), whichBuffer);
                  return;
               case 2:
                  try {
                     byte b = Byte.parseByte(val);
                     this.writeShortIntegerValue(b, whichBuffer);
                     return;
                  } finally {
                     return;
                  }
               case 3:
                  try {
                     int l = Integer.parseInt(val);
                     this.writeIntegerValue(l, whichBuffer);
                     return;
                  } finally {
                     return;
                  }
               case 5:
               case 6:
                  this.writeTextString(val, whichBuffer);
                  return;
               case 7:
                  this.writeTextString(val, whichBuffer);
                  return;
               case 8:
               case 17:
                  try {
                     byte l = (byte)Integer.parseInt(val);
                     this.writeShortIntegerValue(l, whichBuffer);
                     return;
                  } finally {
                     return;
                  }
               case 9:
                  this.writeTextString(val, whichBuffer);
                  return;
               case 10:
               case 11:
                  this.writeTextString(val, whichBuffer);
            }
         } else {
            this.writeOctet((byte)0, whichBuffer);
         }
      } else {
         this.writeTokenText(name, whichBuffer);
         if (this.isIntegerValue(val)) {
            int l = 0;

            label184:
            try {
               l = Integer.parseInt(val);
            } finally {
               break label184;
            }

            this.writeIntegerValue(l, whichBuffer);
         } else {
            this.writeTextValue(val, whichBuffer);
         }
      }
   }

   private final void writeTextValue(String val, byte whichBuffer) {
      if (val == null) {
         this.writeOctet((byte)0, whichBuffer);
      } else if (val.startsWith("\"")) {
         this.writeQuotedString(val, whichBuffer);
      } else {
         this.writeTokenText(val, whichBuffer);
      }
   }

   private final void writeQuotedString(String val, byte whichBuffer) {
      if (val.length() >= 2 && val.charAt(0) == '"' && val.charAt(val.length() - 1) == '"') {
         val = val.substring(1, val.length() - 1);
      }

      this.writeOctet((byte)34, whichBuffer);
      this.writeTokenText(val, whichBuffer);
   }

   private final void writeVersionValue(String value, byte whichBuffer) {
      try {
         int in = Integer.parseInt(value);
         this.writeShortIntegerValue((byte)in, whichBuffer);
      } finally {
         this.writeTextString(value, whichBuffer);
         return;
      }
   }

   private final void writeContentEncodingValue(String encoding, byte whichBuffer) {
      if (StringUtilities.compareToIgnoreCase(encoding, "Gzip", 1701707776) == 0) {
         this.writeOctet((byte)-128, whichBuffer);
      } else if (StringUtilities.compareToIgnoreCase(encoding, "Compress", 1701707776) == 0) {
         this.writeOctet((byte)-127, whichBuffer);
      } else if (StringUtilities.compareToIgnoreCase(encoding, "Deflate", 1701707776) == 0) {
         this.writeOctet((byte)-126, whichBuffer);
      } else {
         this.writeTokenText(encoding, whichBuffer);
      }
   }

   private final void writeTokenText(String s, byte whichBuffer) {
      this.writeOctet(s.getBytes(), 0, s.length(), whichBuffer);
      this.writeOctet((byte)0, whichBuffer);
   }

   private final void writeDeltaSecondsValue(String s, byte whichBuffer) {
      long deltaSeconds = 2147483648L;

      label20:
      try {
         deltaSeconds = Long.parseLong(s, 10);
      } finally {
         break label20;
      }

      this.writeIntegerValue(deltaSeconds, whichBuffer);
   }

   private final void writeValueLength(long value, byte whichBuffer) {
      if (value >= 0 && value <= 30) {
         this.writeShortLength((byte)value, whichBuffer);
      } else {
         if (value > 30) {
            this.writeOctet((byte)31, whichBuffer);
            int numBytes = VarLengthInt.encode(value, this._VarLenIntBuf);
            this.writeOctet(this._VarLenIntBuf, this._VarLenIntBuf.length - numBytes, this._VarLenIntBuf.length, whichBuffer);
         }
      }
   }

   private final void writeIntegerValue(long intVal, byte whichBuffer) {
      if (intVal >= 0 && intVal < 128) {
         this.writeShortIntegerValue((byte)intVal, whichBuffer);
      } else {
         this.writeLongIntegerValue(intVal, whichBuffer);
      }
   }

   private final void writeShortIntegerValue(byte val, byte whichBuffer) {
      this.writeOctet((byte)(val | -128), whichBuffer);
   }

   private final void writeLongIntegerValue(long val, byte whichBuffer) {
      short shortLength = Utils.longToBytes(val, this._VarLenIntBuf);
      this.writeShortLength((byte)shortLength, whichBuffer);
      this.writeOctet(this._VarLenIntBuf, this._VarLenIntBuf.length - shortLength, this._VarLenIntBuf.length, whichBuffer);
   }

   private final void writeShortLength(byte val, byte whichBuffer) {
      if (val >= 0 && val <= 30) {
         this.writeOctet(val, whichBuffer);
      }
   }

   private final void writeURIValue(String uri, byte whichBuffer) {
      this.writeTextString(uri, whichBuffer);
   }

   private final void writeTextString(String s, byte whichBuffer) {
      if (s != null) {
         if (s.length() > 0) {
            char firstChar = s.charAt(0);
            if (firstChar >= 128 && firstChar <= 255) {
               this.writeOctet((byte)127, whichBuffer);
            }
         }

         this.writeTokenText(s, whichBuffer);
      }
   }

   private final void writeQValue(String qValue, byte whichBuffer) {
      int indx = qValue.indexOf(46);
      if (indx < 0) {
      }

      String integerPart = qValue.substring(0, indx);
      String decimalPart = qValue.substring(indx + 1);
      int intPart = Integer.parseInt(integerPart);
      if (intPart != 0) {
      }

      int numDec = decimalPart.length();
      numDec = numDec > 3 ? 3 : numDec;
      int encodedVal = Integer.valueOf(qValue.substring(0, indx) + qValue.substring(indx + 1, indx + 1 + numDec));
      switch (numDec) {
         case 0:
            break;
         case 1:
         default:
            encodedVal *= 10;
         case 2:
            encodedVal++;
            break;
         case 3:
            encodedVal += 100;
      }

      short numOctets = VarLengthInt.encode(encodedVal, this._VarLenIntBuf);
      this.writeOctet(this._VarLenIntBuf, this._VarLenIntBuf.length - numOctets, this._VarLenIntBuf.length, whichBuffer);
   }

   private final void writeMediaRange(String val, byte whichBuffer) {
      StringTokenizer st = new StringTokenizer(val, ";", false);
      String media = st.nextToken();
      byte mediaType = getContentTypeAssignedNumber(media);
      if (mediaType > 52) {
         this.writeExtensionMedia(media, whichBuffer);
      } else {
         this.writeIntegerValue(mediaType, whichBuffer);
      }

      while (st.hasMoreTokens()) {
         String param = st.nextToken().trim();
         int index = param.indexOf("=");
         String name = param.substring(0, index);
         String pval = param.substring(index + 1, param.length());
         this.writeEncodedParameter(name, pval, whichBuffer);
      }
   }

   private final void writeExtensionMedia(String value, byte whichBuffer) {
      this.writeTokenText(value, whichBuffer);
   }

   private final void writeConstrainedMedia(String val, byte whichBuffer) {
      val = val.trim();
      byte assNum = getContentTypeAssignedNumber(val);
      if (assNum <= 52) {
         label42:
         try {
            val = new Byte(assNum).toString();
         } finally {
            break label42;
         }
      }

      if (this.isShortInteger(val)) {
         try {
            this.writeShortIntegerValue((byte)Integer.parseInt(val), whichBuffer);
         } finally {
            return;
         }
      } else {
         this.writeExtensionMedia(val, whichBuffer);
      }
   }

   private final long makeDateValue(String value) {
      try {
         return HttpDateParser.parse(value) / 1000;
      } finally {
         return 0;
      }
   }

   private final boolean ensureEmailAddressIsMachineReadable(String emailAddr) {
      char[] emailAddress = emailAddr.toCharArray();
      int len = emailAddress.length;
      int pos = 0;
      int nameCount = 0;
      int domainNameCount = 0;
      int domainSuffixCount = 0;

      try {
         for (char c = emailAddress[pos++]; c != '@'; c = emailAddress[pos++]) {
            nameCount++;
         }

         for (char var13 = emailAddress[pos]; var13 != '.'; var13 = emailAddress[pos++]) {
            domainNameCount++;
         }

         char var14 = emailAddress[pos++];

         while (pos < len) {
            domainSuffixCount++;
            var14 = emailAddress[pos++];
         }
      } finally {
         ;
      }

      return nameCount > 0 && domainNameCount > 0 && domainSuffixCount > 0;
   }

   private final void handleHeaderAccept(String value) {
      StringTokenizer st = new StringTokenizer(value, ",", false);
      int numTokens = st.countTokens();

      for (int i = 0; i < numTokens; i++) {
         this.writeShortIntegerValue((byte)0, (byte)1);
         String val = st.nextToken();
         int indx = val.indexOf(";");
         if (indx < 0) {
            this.writeConstrainedMedia(val, (byte)1);
         } else {
            this._nextTempBufLen = 0;
            this.writeMediaRange(val, (byte)2);
            this.writeValueLength(this._nextTempBufLen, (byte)1);
            this.ensureCapacity(this._encodedLength + this._nextTempBufLen);
            System.arraycopy(this._nextTempBuffer, 0, this._encodedHeaders, this._encodedLength, this._nextTempBufLen);
            this._encodedLength = this._encodedLength + this._nextTempBufLen;
         }
      }
   }

   private final void handleHeaderAcceptCharset(String value) {
      StringTokenizer st = new StringTokenizer(value, ",", false);
      int numTokens = st.countTokens();
      if (numTokens != 0) {
         for (int i = 0; i < numTokens; i++) {
            this.writeShortIntegerValue((byte)1, (byte)1);
            String charset = null;
            String val = st.nextToken();
            int indx = val.indexOf(";");
            if (indx < 0) {
               charset = val.trim();
               short assNum = getCharSetAssignedNumber(charset);
               if (assNum > 2999) {
                  this.writeTokenText(charset, (byte)1);
                  continue;
               }

               if (assNum >= 0 && assNum < 128) {
                  this.writeShortIntegerValue((byte)assNum, (byte)1);
                  continue;
               }
            }

            this._tempBufLen = 0;
            String qParameter = null;
            int indx2 = -1;
            if (indx < 0) {
               charset = val.trim();
            } else {
               charset = val.substring(0, indx).trim();
               qParameter = val.substring(indx + 1).trim();
               indx2 = qParameter.indexOf("=");
            }

            short assNum = getCharSetAssignedNumber(charset);
            if (assNum > 2999) {
               this.writeTokenText(charset, (byte)0);
            } else {
               this.writeIntegerValue(assNum, (byte)0);
            }

            if (indx2 >= 0) {
               this.writeQValue(qParameter.substring(indx2 + 1).trim(), (byte)0);
            }

            this.writeValueLength(this._tempBufLen, (byte)1);
            this.writeOctet(this._tempBuffer, 0, this._tempBufLen, (byte)1);
            this._tempBufLen = 0;
         }
      }
   }

   private final void handleHeaderAcceptEncoding(String value) {
      StringTokenizer st = new StringTokenizer(value, ",", false);
      int numTokens = st.countTokens();
      if (numTokens == 0) {
         this.writeShortIntegerValue((byte)2, (byte)1);
         this.writeOctet((byte)0, (byte)1);
      } else {
         for (int i = 0; i < numTokens; i++) {
            this.writeShortIntegerValue((byte)2, (byte)1);
            this.writeContentEncodingValue(st.nextToken(), (byte)1);
         }
      }
   }

   private final void handleHeaderAcceptLanguage(String value) {
      int index = -1;
      StringTokenizer st = new StringTokenizer(value, ",", false);
      int numTokens = st.countTokens();
      if (numTokens != 0) {
         for (int i = 0; i < numTokens; i++) {
            this.writeShortIntegerValue((byte)3, (byte)1);
            String lan = st.nextToken().trim();
            index = lan.indexOf(";");
            if (index < 0) {
               long assign = getContentLanguageAssignedNumber(lan);
               if (assign == -1) {
                  if (lan.equals("*")) {
                     this.writeOctet((byte)-128, (byte)1);
                  } else {
                     this.writeExtensionMedia(lan, (byte)1);
                  }
                  continue;
               }

               if (assign >= 0 && assign < 128) {
                  this.writeShortIntegerValue((byte)assign, (byte)1);
                  continue;
               }
            }

            this._tempBufLen = 0;
            String qvalue = null;
            int index2 = -1;
            String language;
            if (index < 0) {
               language = lan.trim();
            } else {
               language = lan.substring(0, index).trim();
               qvalue = lan.substring(index + 1).trim();
               index2 = qvalue.indexOf("=");
            }

            long assign = getContentLanguageAssignedNumber(language);
            if (assign == -1) {
               if (language.equals("*")) {
                  this.writeOctet((byte)-128, (byte)0);
               } else {
                  this.writeTextString(language, (byte)0);
               }
            } else {
               this.writeIntegerValue(assign, (byte)0);
            }

            if (index2 >= 0) {
               this.writeQValue(qvalue.substring(index2 + 1).trim(), (byte)0);
            }

            this.writeValueLength(this._tempBufLen, (byte)1);
            this.writeOctet(this._tempBuffer, 0, this._tempBufLen, (byte)1);
            this._tempBufLen = 0;
         }
      }
   }

   private final void handleHeaderAcceptRanges(String value) {
      StringTokenizer st = new StringTokenizer(value, ",", false);
      int numTokens = st.countTokens();

      for (int i = 0; i < numTokens; i++) {
         this.writeShortIntegerValue((byte)4, (byte)1);
         String val = st.nextToken();
         if (StringUtilities.compareToIgnoreCase(val, "none", 1701707776) == 0) {
            this.writeOctet((byte)-128, (byte)1);
         } else if (StringUtilities.compareToIgnoreCase(val, "bytes", 1701707776) == 0) {
            this.writeOctet((byte)-127, (byte)1);
         } else {
            this.writeTokenText(val, (byte)1);
         }
      }
   }

   private final void handleHeaderAge(String value) {
      this.writeShortIntegerValue((byte)5, (byte)1);
      this.writeDeltaSecondsValue(value, (byte)1);
   }

   private final void handleHeaderAllow(String value) {
      StringTokenizer st = new StringTokenizer(value, ",", false);
      int numTokens = st.countTokens();
      if (numTokens != 0) {
         for (int i = 0; i < numTokens; i++) {
            this.writeShortIntegerValue((byte)6, (byte)1);
            byte wellKnownMethod = getMethodTypeAssignedNumber(st.nextToken());
            this.writeShortIntegerValue(wellKnownMethod, (byte)1);
         }
      }
   }

   private final void handleHeaderAuthorization(byte type, String value) {
      this._tempBufLen = 0;
      this.writeShortIntegerValue(type, (byte)1);
      int index = value.indexOf(32);
      if (StringUtilities.compareToIgnoreCase(value.substring(0, index), "Basic", 1701707776) != 0) {
         index = value.indexOf(32);
         if (index > 0) {
            String val = value.substring(0, index);
            this.writeTokenText(val, (byte)0);
            val = value.substring(index + 1);
            StringTokenizer st = new StringTokenizer(val, ";,", false);

            while (st.hasMoreTokens()) {
               val = st.nextToken();
               index = val.indexOf("=");
               String param = val.substring(index + 1).trim();
               String name = val.substring(0, index).trim();
               this.writeEncodedParameter(name, param, (byte)0);
            }
         }
      } else {
         this.writeOctet((byte)-128, (byte)0);
         int index1 = value.indexOf(58);
         int length = value.length();
         if (index1 == -1) {
            label62:
            try {
               Base64InputStream b64is = new Base64InputStream(new ByteArrayInputStream(value.substring(index + 1).getBytes()));
               byte[] out = new byte[length - index];
               int read = b64is.read(out);
               b64is.close();
               if (read > 0) {
                  value = new String(out, 0, read);
                  index = -1;
               }

               index1 = value.indexOf(58);
            } finally {
               break label62;
            }
         }

         String username;
         String pwd;
         if (index1 > 0 && index1 < length) {
            username = value.substring(index + 1, index1);
            pwd = value.substring(index1 + 1);
         } else {
            username = "";
            pwd = username;
         }

         this.writeTextString(username, (byte)0);
         this.writeTextString(pwd, (byte)0);
      }

      this.writeValueLength(this._tempBufLen, (byte)1);
      this.writeOctet(this._tempBuffer, 0, this._tempBufLen, (byte)1);
   }

   private final void handleHeaderCacheControl(String value) {
      int cur = 0;
      int length = value.length();
      int index = -1;
      int lastc = 0;
      int i = 0;

      while (i < length) {
         lastc = cur;
         if (value.charAt(i) == ',') {
            String cache_St = value.substring(cur, i);
            int cache_num = getCacheControlAssignNumber(cache_St);
            cur = i + 1;
            this.writeShortIntegerValue((byte)8, (byte)1);
            if (cache_num == 255) {
               this.writeTextString(cache_St, (byte)1);
            } else {
               this.writeOctet((byte)cache_num, (byte)1);
            }
         }

         if (value.charAt(i) == '=') {
            this.writeShortIntegerValue((byte)8, (byte)1);
            String cache_St = value.substring(cur, i);
            int cache_num = getCacheControlAssignNumber(cache_St);
            cur = i + 1;
            switch (cache_num) {
               case 127:
                  index = value.indexOf(44, i);
                  String field;
                  if (index == -1) {
                     field = value.substring(cur, length);
                  } else {
                     field = value.substring(cur, index);
                  }

                  long len;
                  if (cache_St.charAt(0) >= 128 && cache_St.charAt(0) < 256) {
                     len = 2 + cache_St.length();
                  } else {
                     len = 1 + cache_St.length();
                  }

                  if (field.charAt(0) >= 128 && field.charAt(0) < 256) {
                     len = len + 2 + field.length();
                  } else {
                     len = len + 1 + field.length();
                  }

                  this.writeValueLength(len, (byte)1);
                  this.writeTextString(cache_St, (byte)1);
                  this.writeTextString(field, (byte)1);
                  break;
               case 128:
               default:
                  cur = this.encodeNo_Cache((byte)cache_num, value, cur);
               case 129:
               case 133:
               case 134:
               case 136:
               case 137:
               case 138:
                  break;
               case 130:
                  cur = this.encodeCacheWithDeltaSeconds(cur, cache_num, length, value);
                  break;
               case 131:
                  cur = this.encodeCacheWithDeltaSeconds(cur, cache_num, length, value);
                  break;
               case 132:
                  cur = this.encodeCacheWithDeltaSeconds(cur, cache_num, length, value);
                  break;
               case 135:
                  cur = this.encodeNo_Cache((byte)cache_num, value, cur);
            }
         }

         if (lastc < cur) {
            i = cur;
         } else {
            i++;
         }
      }

      if (cur < length) {
         this.writeShortIntegerValue((byte)8, (byte)1);
         String cache_St = value.substring(cur, length);
         int cache_num = getCacheControlAssignNumber(cache_St);
         if (cache_num == 255) {
            this.writeTextString(cache_St, (byte)1);
            return;
         }

         this.writeOctet((byte)cache_num, (byte)1);
      }
   }

   private final void handleHeaderConnection(String value) {
      StringTokenizer st = new StringTokenizer(value, ",", false);
      int numTokens = st.countTokens();
      if (numTokens != 0) {
         for (int i = 0; i < numTokens; i++) {
            this.writeShortIntegerValue((byte)9, (byte)1);
            String val = st.nextToken();
            if (StringUtilities.compareToIgnoreCase(val, "Close", 1701707776) == 0) {
               this.writeOctet((byte)-128, (byte)1);
               return;
            }

            this.writeTokenText(val, (byte)1);
         }
      }
   }

   private final void handleHeaderContentBase(String value) {
      this.writeShortIntegerValue((byte)10, (byte)1);
      this.writeURIValue(value, (byte)1);
   }

   private final void handleHeaderContentEncoding(String value) {
      StringTokenizer st = new StringTokenizer(value, ",", false);
      int numTokens = st.countTokens();
      if (numTokens == 0) {
         this.writeShortIntegerValue((byte)11, (byte)1);
         this.writeOctet((byte)0, (byte)1);
      } else {
         for (int i = 0; i < numTokens; i++) {
            this.writeShortIntegerValue((byte)11, (byte)1);
            this.writeContentEncodingValue(st.nextToken(), (byte)1);
         }
      }
   }

   private final void handleHeaderContentLanguage(String value) {
      StringTokenizer st = new StringTokenizer(value, ",", false);
      int numTokens = st.countTokens();
      if (numTokens != 0) {
         for (int i = 0; i < numTokens; i++) {
            this.writeShortIntegerValue((byte)12, (byte)1);
            String val = st.nextToken();
            byte assNum = getContentLanguageAssignedNumber(val);
            if (assNum > 140) {
               this.writeTokenText(val, (byte)1);
            } else {
               this.writeOctet(assNum, (byte)1);
            }
         }
      }
   }

   private final void handleHeaderContentLength(String value) {
      int length = 0;

      try {
         length = Integer.parseInt(value);
         this.writeShortIntegerValue((byte)13, (byte)1);
         this.writeIntegerValue(length, (byte)1);
      } finally {
         return;
      }
   }

   private final void handleHeaderContentLocation(String value) {
      this.writeShortIntegerValue((byte)14, (byte)1);
      this.writeURIValue(value, (byte)1);
   }

   private final void handleHeaderContentMD5(String value) {
   }

   private final void handleHeaderContentRange(String value) {
   }

   private final void handleHeaderContentType(String value, boolean writeField) {
      StringTokenizer st = new StringTokenizer(value, ",", false);

      while (st.hasMoreTokens()) {
         if (writeField) {
            this.writeShortIntegerValue((byte)17, (byte)1);
         }

         String val = st.nextToken().trim();
         StringTokenizer st2 = new StringTokenizer(val, ";", false);
         String contentTypeVal = st2.nextToken().trim();
         byte assNum = getContentTypeAssignedNumber(contentTypeVal);
         if (st2.countTokens() > 0) {
            this._tempBufLen = 0;
            if (assNum <= 52) {
               this.writeShortIntegerValue(assNum, (byte)0);
            } else {
               this.writeExtensionMedia(contentTypeVal, (byte)0);
            }

            while (st2.hasMoreTokens()) {
               String param = st2.nextToken().trim();
               int index = param.indexOf("=");
               String name = param.substring(0, index);
               String pval = param.substring(index + 1, param.length());
               this.writeEncodedParameter(name, pval, (byte)0);
            }

            this.writeValueLength(this._tempBufLen, (byte)1);
            this.ensureCapacity(this._encodedLength + this._tempBufLen);
            System.arraycopy(this._tempBuffer, 0, this._encodedHeaders, this._encodedLength, this._tempBufLen);
            this._encodedLength = this._encodedLength + this._tempBufLen;
         } else if (assNum <= 52) {
            this.writeShortIntegerValue(assNum, (byte)1);
         } else {
            this.writeExtensionMedia(contentTypeVal, (byte)1);
         }
      }
   }

   private final void handleHeaderDate(byte type, String value) {
      long dateValue = this.makeDateValue(value);
      if (dateValue > 0) {
         this.writeShortIntegerValue(type, (byte)1);
         this.writeLongIntegerValue(dateValue, (byte)1);
      }
   }

   private final void handleHeaderEtag(String value) {
      this.writeShortIntegerValue((byte)19, (byte)1);
      this.writeTextString(value, (byte)1);
   }

   private final void handleHeaderFrom(String value) {
      if (this.ensureEmailAddressIsMachineReadable(value)) {
         this.writeShortIntegerValue((byte)21, (byte)1);
         this.writeTextString(value, (byte)1);
      }
   }

   private final void handleHeaderHost(String value) {
      this.writeShortIntegerValue((byte)22, (byte)1);
      this.writeTextString(value, (byte)1);
   }

   private final void handleHeaderIfMatch(String value) {
   }

   private final void handleHeaderIfNoneMatch(String value) {
   }

   private final void handleHeaderIfRange(String value) {
   }

   private final void handleHeaderLocation(String value) {
      this.writeShortIntegerValue((byte)28, (byte)1);
      this.writeURIValue(value, (byte)1);
   }

   private final void handleHeaderMaxForwards(String value) {
      int length = 0;

      try {
         length = Integer.parseInt(value);
         this.writeShortIntegerValue((byte)30, (byte)1);
         this.writeIntegerValue(length, (byte)1);
      } finally {
         return;
      }
   }

   private final void handleHeaderPragma(String value) {
      this.writeShortIntegerValue((byte)31, (byte)1);
      if (value.equals("No-cache")) {
         this.writeOctet((byte)-128, (byte)1);
      }
   }

   private final void handleHeaderAuthenticate(byte type, String value) {
      this.writeShortIntegerValue(type, (byte)1);
      this._tempBufLen = 0;
      int index = value.indexOf(32);
      if (StringUtilities.compareToIgnoreCase(value.substring(0, index), "Basic", 1701707776) == 0) {
         this.writeOctet((byte)-128, (byte)0);
         index = value.indexOf("=");
         this.writeTextString(value.substring(index + 1), (byte)0);
      } else {
         index = value.indexOf(32);
         this.writeTextString(value.substring(0, index), (byte)0);
         StringTokenizer str = new StringTokenizer(value.substring(index + 1).trim(), ",", false);
         String param = str.nextToken();
         index = param.indexOf("=");
         this.writeTextString(param.substring(index + 1), (byte)0);

         while (str.hasMoreTokens()) {
            param = str.nextToken();
            index = param.indexOf("=");
            this.writeEncodedParameter(param.substring(0, index), param.substring(index + 1), (byte)0);
         }
      }

      this.writeValueLength(this._tempBufLen, (byte)1);
      this.writeOctet(this._tempBuffer, 0, this._tempBufLen, (byte)1);
   }

   private final void handleHeaderPublic(String value) {
   }

   private final void handleHeaderRange(String value) {
   }

   private final void handleHeaderReferer(String value) {
      this.writeShortIntegerValue((byte)36, (byte)1);
      this.writeURIValue(value, (byte)1);
   }

   private final void handleHeaderRetryAfter(String value) {
      if (value != null) {
         this.writeShortIntegerValue((byte)37, (byte)1);
         this._tempBufLen = 0;
         if (this.isIntegerValue(value)) {
            this.writeOctet((byte)-127, (byte)0);
            this.writeDeltaSecondsValue(value, (byte)0);
         } else {
            this.writeOctet((byte)-128, (byte)0);
            long l = this.makeDateValue(value);
            this.writeLongIntegerValue(l, (byte)0);
         }

         this.writeValueLength(this._tempBufLen, (byte)1);
         this.writeOctet(this._tempBuffer, 0, this._tempBufLen, (byte)1);
         this._tempBufLen = 0;
      }
   }

   private final void handleHeaderServer(String value) {
      if (value != null) {
         this.writeShortIntegerValue((byte)38, (byte)1);
         this.writeTextString(value, (byte)1);
      }
   }

   private final void handleHeaderTransferEncoding(String value) {
      this.writeShortIntegerValue((byte)39, (byte)1);
      if (StringUtilities.compareToIgnoreCase(value, "Chunked", 1701707776) == 0) {
         this.writeOctet((byte)-128, (byte)1);
      } else {
         this.writeTokenText(value, (byte)1);
      }
   }

   private final void handleHeaderUpgrade(String value) {
   }

   private final void handleHeaderUserAgent(String value) {
      if (value != null) {
         this.writeShortIntegerValue((byte)41, (byte)1);
         this.writeTextString(value, (byte)1);
      }
   }

   private final void handleHeaderVary(String value) {
   }

   private final void handleHeaderVia(String value) {
      this.writeShortIntegerValue((byte)43, (byte)1);
      this.writeTextString(value, (byte)1);
   }

   private final void handleHeaderWarning(String value) {
      this.writeShortIntegerValue((byte)44, (byte)1);
      int index1 = value.indexOf(32);
      if (index1 < 0) {
         this.writeShortIntegerValue((byte)Integer.parseInt(value), (byte)1);
      } else {
         this.writeShortIntegerValue((byte)Integer.parseInt(value.substring(0, index1)), (byte)0);
         int index2 = value.indexOf(32, index1);
         this._tempBufLen = 0;
         this.writeTextString(value.substring(index1 + 1, index2), (byte)0);
         this.writeTextString(value.substring(index2 + 1), (byte)0);
         this.writeValueLength(this._tempBufLen, (byte)1);
         this.ensureCapacity(this._encodedLength + this._tempBufLen);
         System.arraycopy(this._tempBuffer, 0, this._encodedHeaders, this._encodedLength, this._tempBufLen);
         this._encodedLength = this._encodedLength + this._tempBufLen;
      }
   }

   private final void handleHeaderContentDisposition(String value) {
   }

   private final void handleHeaderXWapApplicationId(String value) {
   }

   private final void handleHeaderXWapContentURI(String value) {
      this.writeShortIntegerValue((byte)48, (byte)1);
      this.writeURIValue(value, (byte)1);
   }

   private final void handleHeaderXWapInitiatorURI(String value) {
      this.writeShortIntegerValue((byte)49, (byte)1);
      this.writeURIValue(value, (byte)1);
   }

   private final void handleHeaderAcceptApplication(String value) {
   }

   private final void handleHeaderBearerIndication(String value) {
      try {
         int l = Integer.parseInt(value);
         this.writeShortIntegerValue((byte)51, (byte)1);
         this.writeIntegerValue(l, (byte)1);
      } finally {
         return;
      }
   }

   private final void handleHeaderPushFlag(String value) {
      try {
         byte b = (byte)Integer.parseInt(value);
         this.writeShortIntegerValue((byte)52, (byte)1);
         this.writeShortIntegerValue(b, (byte)1);
      } finally {
         return;
      }
   }

   private final void handleHeaderProfile(String value) {
      this.writeShortIntegerValue((byte)53, (byte)1);
      this.writeURIValue(value, (byte)1);
   }

   private final void handleHeaderProfileDiff(String value) {
      this.writeShortIntegerValue((byte)54, (byte)1);
      this.writeTextString(value, (byte)1);
   }

   private final void handleHeaderProfileWarning(String value) {
   }

   private final void handleHeaderExpect(String value) {
   }

   private final void handleHeaderTE(String value) {
   }

   private final void handleHeaderTrailer(String value) {
   }

   private final void handleHeaderContentID(String value) {
      this.writeShortIntegerValue((byte)64, (byte)1);
      this.writeQuotedString(value, (byte)1);
   }

   private final void handleHeaderSetCookie(String value) {
      StringTokenizer cookies = new StringTokenizer(value, ",", false);

      while (cookies.hasMoreTokens()) {
         this.writeShortIntegerValue((byte)65, (byte)1);
         String cookie = cookies.nextToken();
         this._tempBufLen = 0;
         StringTokenizer str = new StringTokenizer(cookie, ";", false);
         if (str == null) {
            return;
         }

         int index = cookie.indexOf("Version");
         if (index > 0) {
            int index1 = cookie.indexOf("=", index);
            index = cookie.indexOf(";", index1);
            if (index < 0) {
               index = cookie.length();
            }

            this.writeVersionValue(cookie.substring(index1 + 1, index), (byte)0);
         } else {
            this.writeShortIntegerValue((byte)0, (byte)0);
         }

         String pair = str.nextToken();
         index = pair.indexOf("=");
         this.writeTextString(pair.substring(0, index), (byte)0);
         this.writeTextString(pair.substring(index + 1), (byte)0);

         while (str.hasMoreTokens()) {
            pair = str.nextToken();
            index = pair.indexOf("=");
            String name = pair.substring(0, index);
            if (!name.equals("Version")) {
               this.writeEncodedParameter(name, pair.substring(index + 1), (byte)0);
            }
         }

         this.writeValueLength(this._tempBufLen, (byte)1);
         this.ensureCapacity(this._encodedLength + this._tempBufLen);
         System.arraycopy(this._tempBuffer, 0, this._encodedHeaders, this._encodedLength, this._tempBufLen);
         this._encodedLength = this._encodedLength + this._tempBufLen;
      }
   }

   private final void handleHeaderCookie(String value) {
      this.writeTextString("Cookie", (byte)1);
      this.writeTextString(value, (byte)1);
   }

   private final void handleHeaderEncodingVersion(String value) {
   }

   private final void handleCustomHeader(String name, String value) {
      this.writeTokenText(name, (byte)1);
      this.writeTokenText(value, (byte)1);
   }

   private static final int getHash(String contentType) {
      byte[] bytes = StringUtilities.toLowerCase(contentType, 1701707776).getBytes();
      int hash = 0;
      int count = bytes.length;

      for (int i = 0; i < count; i++) {
         hash += (bytes[i] & 255) * (i + 1);
      }

      return hash;
   }

   private static final byte getContentTypeAssignedNumber(String contentType) {
      int hash = getHash(contentType);
      String value = null;
      byte id = 127;
      switch (hash) {
         case 262:
            id = 0;
            value = "*/*";
            break;
         case 1629:
            id = 1;
            value = "text/*";
            break;
         case 2107:
            id = 28;
            value = "image/*";
            break;
         case 3724:
            id = 40;
            value = "text/xml";
            break;
         case 4292:
            id = 29;
            value = "image/gif";
            break;
         case 4404:
            id = 32;
            value = "image/png";
            break;
         case 4657:
            id = 2;
            value = "text/html";
            break;
         case 5390:
            id = 30;
            value = "image/jpeg";
            break;
         case 5403:
            id = 31;
            value = "image/tiff";
            break;
         case 5626:
            id = 3;
            value = "text/plain";
            break;
         case 5895:
            id = 11;
            value = "multipart/*";
            break;
         case 6422:
            id = 4;
            value = "text/x-hdml";
            break;
         case 6662:
            id = 5;
            value = "text/x-ttml";
            break;
         case 7671:
            id = 7;
            value = "text/x-vcard";
            break;
         case 8190:
            id = 16;
            value = "application/*";
            break;
         case 11319:
            id = 49;
            value = "text/vnd.wap.co";
            break;
         case 11453:
            id = 45;
            value = "text/vnd.wap.si";
            break;
         case 11498:
            id = 47;
            value = "text/vnd.wap.sl";
         case 26839:
            id = 48;
            value = "application/vnd.wap.slc";
            break;
         case 12350:
            id = 39;
            value = "application/xml";
            break;
         case 12366:
            id = 12;
            value = "multipart/mixed";
            break;
         case 13297:
            id = 8;
            value = "text/vnd.wap.wml";
            break;
         case 13726:
            id = 6;
            value = "text/x-vcalendar";
            break;
         case 16674:
            id = 33;
            value = "image/vnd.wap.wbmp";
            break;
         case 18662:
            id = 17;
            value = "application/java-vm";
            break;
         case 18672:
            id = 19;
            value = "application/x-hdmlc";
            break;
         case 18750:
            id = 13;
            value = "multipart/form-data";
            break;
         case 20695:
            id = 10;
            value = "text/vnd.wap.channel";
            break;
         case 22090:
            id = 14;
            value = "multipart/byteranges";
            break;
         case 24401:
            id = 15;
            value = "multipart/alternative";
            break;
         case 26204:
            id = 9;
            value = "text/vnd.wap.wmlscript";
            break;
         case 26569:
            id = 50;
            value = "application/vnd.wap.coc";
            break;
         case 26727:
            id = 52;
            value = "application/vnd.wap.sia";
            break;
         case 26773:
            id = 46;
            value = "application/vnd.wap.sic";
            break;
         case 29528:
            id = 20;
            value = "application/vnd.wap.wmlc";
            break;
         case 30753:
            id = 26;
            value = "application/x-x509-ca-cert";
            break;
         case 30901:
            id = 43;
            value = "application/x-x968-ca-cert";
            break;
         case 32502:
            id = 41;
            value = "application/vnd.wap.wbxml";
            break;
         case 35101:
            id = 23;
            value = "application/vnd.wap.uaprof";
            break;
         case 37285:
            id = 27;
            value = "application/x-x509-user-cert";
            break;
         case 37433:
            id = 44;
            value = "application/x-x968-user-cert";
            break;
         case 40073:
            id = 22;
            value = "application/vnd.wap.channelc";
            break;
         case 40530:
            id = 42;
            value = "application/x-x968-cross-cert";
            break;
         case 47296:
            id = 34;
            value = "application/vnd.wap.multipart.*";
            break;
         case 47656:
            id = 21;
            value = "application/vnd.wap.wmlscriptc";
            break;
         case 56447:
            id = 18;
            value = "application/x-www-form-urlencoded";
            break;
         case 63627:
            id = 35;
            value = "application/vnd.wap.multipart.mixed";
            break;
         case 71018:
            id = 51;
            value = "application/vnd.wap.multipart.related";
            break;
         case 76899:
            id = 24;
            value = "application/vnd.wap.wtls-ca-certificate";
            break;
         case 77131:
            id = 36;
            value = "application/vnd.wap.multipart.form-data";
            break;
         case 84171:
            id = 37;
            value = "application/vnd.wap.multipart.byteranges";
            break;
         case 86387:
            id = 25;
            value = "application/vnd.wap.wtls-user-certificate";
            break;
         case 88622:
            id = 38;
            value = "application/vnd.wap.multipart.alternative";
      }

      return value != null && StringUtilities.compareToIgnoreCase(contentType, value, 1701707776) == 0 ? id : 127;
   }

   private static final byte getMethodTypeAssignedNumber(String methodName) {
      if (StringUtilities.compareToIgnoreCase(methodName, "GET", 1701707776) == 0) {
         return 64;
      } else if (StringUtilities.compareToIgnoreCase(methodName, "OPTIONS", 1701707776) == 0) {
         return 65;
      } else if (StringUtilities.compareToIgnoreCase(methodName, "HEAD", 1701707776) == 0) {
         return 66;
      } else if (StringUtilities.compareToIgnoreCase(methodName, "DELETE", 1701707776) == 0) {
         return 67;
      } else if (StringUtilities.compareToIgnoreCase(methodName, "TRACE", 1701707776) == 0) {
         return 68;
      } else if (StringUtilities.compareToIgnoreCase(methodName, "POST", 1701707776) == 0) {
         return 96;
      } else {
         return (byte)(StringUtilities.compareToIgnoreCase(methodName, "PUT", 1701707776) == 0 ? 97 : 127);
      }
   }

   private static final byte getContentLanguageAssignedNumber(String lang) {
      if (lang != null && lang.length() == 2) {
         switch (lang.charAt(0) << '\b' | lang.charAt(1)) {
            case 24929:
               return 1;
            case 24930:
               return 2;
            case 24934:
               return 3;
            case 24941:
               return 4;
            case 24946:
               return 5;
            case 24947:
               return 6;
            case 24953:
               return 7;
            case 24954:
               return 8;
            case 25185:
               return 9;
            case 25189:
               return 10;
            case 25191:
               return 11;
            case 25192:
               return 12;
            case 25193:
               return 13;
            case 25198:
               return 14;
            case 25199:
               return 15;
            case 25202:
               return 16;
            case 25441:
               return 17;
            case 25455:
               return 18;
            case 25459:
               return 19;
            case 25465:
               return 20;
            case 25697:
               return 21;
            case 25701:
               return 22;
            case 25722:
               return 23;
            case 25964:
               return 24;
            case 25966:
               return 25;
            case 25967:
               return 26;
            case 25971:
               return 27;
            case 25972:
               return 28;
            case 25973:
               return 29;
            case 26209:
               return 30;
            case 26217:
               return 31;
            case 26218:
               return 32;
            case 26223:
               return -126;
            case 26226:
               return 34;
            case 26233:
               return -125;
            case 26465:
               return 36;
            case 26468:
               return 37;
            case 26476:
               return 38;
            case 26478:
               return 39;
            case 26485:
               return 40;
            case 26721:
               return 41;
            case 26725:
               return 42;
            case 26729:
               return 43;
            case 26738:
               return 44;
            case 26741:
               return 45;
            case 26745:
               return 46;
            case 26977:
               return -124;
            case 26980:
               return 48;
            case 26981:
               return -122;
            case 26987:
               return -121;
            case 26995:
               return 51;
            case 26996:
               return 52;
            case 26997:
               return -119;
            case 27233:
               return 54;
            case 27255:
               return 55;
            case 27489:
               return 56;
            case 27499:
               return 57;
            case 27500:
               return -118;
            case 27501:
               return 59;
            case 27502:
               return 60;
            case 27503:
               return 61;
            case 27507:
               return 62;
            case 27509:
               return 63;
            case 27513:
               return 64;
            case 27745:
               return -117;
            case 27758:
               return 66;
            case 27759:
               return 67;
            case 27764:
               return 68;
            case 27766:
               return 69;
            case 28007:
               return 70;
            case 28009:
               return 71;
            case 28011:
               return 72;
            case 28012:
               return 73;
            case 28014:
               return 74;
            case 28015:
               return 75;
            case 28018:
               return 76;
            case 28019:
               return 77;
            case 28020:
               return 78;
            case 28025:
               return 79;
            case 28257:
               return -127;
            case 28261:
               return 81;
            case 28268:
               return 82;
            case 28271:
               return 83;
            case 28515:
               return 84;
            case 28525:
               return 85;
            case 28530:
               return 86;
            case 28769:
               return 87;
            case 28783:
               return 88;
            case 28787:
               return 89;
            case 28788:
               return 90;
            case 29045:
               return 91;
            case 29293:
               return -116;
            case 29294:
               return 93;
            case 29295:
               return 94;
            case 29301:
               return 95;
            case 29303:
               return 96;
            case 29537:
               return 97;
            case 29540:
               return 98;
            case 29543:
               return 99;
            case 29544:
               return 100;
            case 29545:
               return 101;
            case 29547:
               return 102;
            case 29548:
               return 103;
            case 29549:
               return 104;
            case 29550:
               return 105;
            case 29551:
               return 106;
            case 29553:
               return 107;
            case 29554:
               return 108;
            case 29555:
               return 109;
            case 29556:
               return 110;
            case 29557:
               return 111;
            case 29558:
               return 112;
            case 29559:
               return 113;
            case 29793:
               return 114;
            case 29797:
               return 115;
            case 29799:
               return 116;
            case 29800:
               return 117;
            case 29801:
               return 118;
            case 29803:
               return 119;
            case 29804:
               return 120;
            case 29806:
               return 121;
            case 29807:
               return 122;
            case 29810:
               return 123;
            case 29811:
               return 124;
            case 29812:
               return 125;
            case 29815:
               return 126;
            case 30055:
               return 127;
            case 30059:
               return 80;
            case 30066:
               return 33;
            case 30074:
               return 35;
            case 30313:
               return 47;
            case 30319:
               return -123;
            case 30575:
               return 49;
            case 30824:
               return 50;
            case 31081:
               return -120;
            case 31087:
               return 53;
            case 31329:
               return 58;
            case 31336:
               return 65;
            case 31349:
               return 92;
         }
      }

      return -1;
   }

   private static final int getCacheControlAssignNumber(String type) {
      if (StringUtilities.compareToIgnoreCase(type, "No-cache", 1701707776) == 0) {
         return 128;
      } else if (StringUtilities.compareToIgnoreCase(type, "No-store", 1701707776) == 0) {
         return 129;
      } else if (StringUtilities.compareToIgnoreCase(type, "Max-age", 1701707776) == 0) {
         return 130;
      } else if (StringUtilities.compareToIgnoreCase(type, "Max-stale", 1701707776) == 0) {
         return 131;
      } else if (StringUtilities.compareToIgnoreCase(type, "Min-fresh", 1701707776) == 0) {
         return 132;
      } else if (StringUtilities.compareToIgnoreCase(type, "Only-if-cache", 1701707776) == 0) {
         return 133;
      } else if (StringUtilities.compareToIgnoreCase(type, "public", 1701707776) == 0) {
         return 134;
      } else if (StringUtilities.compareToIgnoreCase(type, "private", 1701707776) == 0) {
         return 135;
      } else if (StringUtilities.compareToIgnoreCase(type, "No-transform", 1701707776) == 0) {
         return 136;
      } else if (StringUtilities.compareToIgnoreCase(type, "Must-revalidate", 1701707776) == 0) {
         return 137;
      } else {
         return StringUtilities.compareToIgnoreCase(type, "Proxy-revalidate", 1701707776) == 0 ? 138 : 255;
      }
   }

   private static final short getCharSetAssignedNumber(String charset) {
      int hash = getHash(charset);
      String value = null;
      short id = 32767;
      switch (hash) {
         case 620:
            id = 113;
            value = "GBK";
            break;
         case 829:
            id = 2026;
            value = "big5";
            break;
         case 1115:
            id = 106;
            value = "utf-8";
            break;
         case 1198:
            id = 2025;
            value = "gb2312";
            break;
         case 1404:
            id = 1015;
            value = "utf-16";
            break;
         case 1552:
            id = 114;
            value = "GB18030";
            break;
         case 1757:
            id = 2259;
            value = "TIS-620";
            break;
         case 1777:
            id = 2084;
            value = "KOI8-R";
            break;
         case 1795:
            id = 2088;
            value = "KOI8-U";
            break;
         case 2014:
            id = 18;
            value = "EUC-JP";
            break;
         case 2031:
            id = 38;
            value = "EUC-KR";
            break;
         case 2224:
            id = 2082;
            value = "VISCII";
            break;
         case 2898:
            id = 1013;
            value = "utf-16be";
            break;
         case 2968:
            id = 1014;
            value = "utf-16le";
            break;
         case 3186:
            id = 4;
            value = "iso-8859-1";
            break;
         case 3196:
            id = 5;
            value = "iso-8859-2";
            break;
         case 3206:
            id = 6;
            value = "iso-8859-3";
            break;
         case 3216:
            id = 7;
            value = "iso-8859-4";
            break;
         case 3226:
            id = 8;
            value = "iso-8859-5";
            break;
         case 3236:
            id = 9;
            value = "iso-8859-6";
            break;
         case 3246:
            id = 10;
            value = "iso-8859-7";
            break;
         case 3256:
            id = 11;
            value = "iso-8859-8";
            break;
         case 3266:
            id = 12;
            value = "iso-8859-9";
            break;
         case 3614:
            id = 3;
            value = "us-ascii";
            break;
         case 3714:
            id = 13;
            value = "iso-8859-10";
            break;
         case 3747:
            id = 109;
            value = "iso-8859-13";
            break;
         case 3758:
            id = 110;
            value = "iso-8859-14";
            break;
         case 3769:
            id = 111;
            value = "iso-8859-15";
            break;
         case 3780:
            id = 112;
            value = "iso-8859-16";
            break;
         case 4741:
            id = 104;
            value = "ISO-2022-CN";
            break;
         case 4813:
            id = 17;
            value = "shift_JIS";
            break;
         case 4833:
            id = 39;
            value = "ISO-2022-JP";
            break;
         case 4865:
            id = 37;
            value = "ISO-2022-KR";
            break;
         case 5011:
            id = 85;
            value = "iso-8859-8-i";
            break;
         case 5388:
            id = 2101;
            value = "Big5-HKSCS";
            break;
         case 5593:
            id = 2250;
            value = "windows-1250";
            break;
         case 5605:
            id = 2251;
            value = "windows-1251";
            break;
         case 5617:
            id = 2252;
            value = "windows-1252";
            break;
         case 5629:
            id = 2253;
            value = "windows-1253";
            break;
         case 5641:
            id = 2254;
            value = "windows-1254";
            break;
         case 5653:
            id = 2255;
            value = "windows-1255";
            break;
         case 5665:
            id = 2256;
            value = "windows-1256";
            break;
         case 5677:
            id = 2257;
            value = "windows-1257";
            break;
         case 5689:
            id = 2258;
            value = "windows-1258";
            break;
         case 8461:
            id = 1000;
            value = "iso-10646-ucs-2";
      }

      return value != null && StringUtilities.compareToIgnoreCase(charset, value, 1701707776) == 0 ? id : 32767;
   }

   private static final byte getWellKnownParameterAssignedNumber(String paramName) {
      for (int i = 0; i < WSPHeaderConstants.WELL_KNOWN_PARAMETER.length; i++) {
         if (StringUtilities.compareToIgnoreCase(paramName, WSPHeaderConstants.WELL_KNOWN_PARAMETER[i], 1701707776) == 0) {
            return WSPHeaderConstants.WELL_KNOWN_PARAMETER_VALUE[i];
         }
      }

      return 127;
   }

   private final int encodeNo_Cache(byte cache, String value, int cur) {
      int index = -1;
      int qindex = value.indexOf(34, ++cur);
      this._tempBufLen = 0;

      while (cur < qindex) {
         index = value.indexOf(44, cur);
         if (index == -1 || index > qindex) {
            index = qindex;
         }

         this.writeTextString(value.substring(cur, index), (byte)0);
         cur = index + 1;
      }

      cur++;
      this.writeValueLength(this._tempBufLen, (byte)1);
      this.writeOctet(cache, (byte)1);
      this.writeOctet(this._tempBuffer, 0, this._tempBufLen, (byte)1);
      this._tempBufLen = 0;
      return cur;
   }

   private final long getDeltaSecondsLength(String value) {
      long v = 2147483648L;

      try {
         v = Long.parseLong(value);
      } finally {
         return v >= 0 && v < 128 ? 1 : Utils.longToBytes(v, this._VarLenIntBuf) + 1;
      }

      return v >= 0 && v < 128 ? 1 : Utils.longToBytes(v, this._VarLenIntBuf) + 1;
   }

   private final int encodeCacheWithDeltaSeconds(int cur, int cache_num, int length, String value) {
      int index = value.indexOf(44, cur);
      if (index == -1) {
         index = length;
      }

      String field = value.substring(cur, index);
      long len = this.getDeltaSecondsLength(field);
      this.writeValueLength(len + 1, (byte)1);
      this.writeOctet((byte)cache_num, (byte)1);
      this.writeDeltaSecondsValue(field, (byte)1);
      return index + 1;
   }
}
