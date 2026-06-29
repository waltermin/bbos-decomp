package net.rim.device.cldc.io.proxyhttp.compression;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.proxyhttp.compression.coders.Coder;
import net.rim.device.cldc.io.proxyhttp.compression.coders.DateCoder;
import net.rim.device.cldc.io.proxyhttp.compression.coders.EscapedTextCoder;
import net.rim.device.cldc.io.proxyhttp.compression.coders.IntegerCoder;
import net.rim.device.cldc.io.proxyhttp.compression.coders.TextCoder;
import net.rim.vm.Array;

public final class GenericMessageEncoder implements MessageEncoder, CodebookConstants {
   private Coder _messageLineCoder;
   private String[] _tokenToHeaderMap;
   private Coder[] _tokenToCoderMap;
   private int[] _tokenToHashMap;
   private int _version;
   private char[] _strBuffer;
   private int _strBufferSectionSize;
   private int _strBufferLen;

   protected final void initialize(InputStream in) {
      Hashtable props = (Hashtable)(new Object(15));
      String key = null;
      Vector values = null;
      this.readLine(in);

      while (this._strBufferLen > 0) {
         int startIndex = 0;
         int endIndex = 0;
         key = null;

         while (endIndex < this._strBufferLen) {
            if (this._strBuffer[endIndex] == '=') {
               key = (String)(new Object(this._strBuffer, startIndex, endIndex - startIndex));
               startIndex = endIndex + 1;
               endIndex++;
               break;
            }

            endIndex++;
         }

         if (key == null) {
            throw new Object("Invalid codebook format");
         }

         if (StringUtilities.strEqual(key, "headers") || StringUtilities.strEqual(key, "valueTypes")) {
            values = (Vector)(new Object(128));
         } else if (key.endsWith("values")) {
            values = (Vector)(new Object(32));
         } else {
            values = (Vector)(new Object(1));
         }

         for (; endIndex < this._strBufferLen; endIndex++) {
            if (this._strBuffer[endIndex] == ',') {
               values.addElement(new Object(this._strBuffer, startIndex, endIndex - startIndex));
               startIndex = endIndex + 1;
            }
         }

         values.addElement(new Object(this._strBuffer, startIndex, endIndex - startIndex));
         props.put(key, values);
         this.readLine(in);
      }

      Hashtable coders = (Hashtable)(new Object(15));
      coders.put("Integer", IntegerCoder.getInstance());
      coders.put("Date", DateCoder.getInstance());
      coders.put("Text-value", TextCoder.getInstance());
      String[] intToHeader = getStringArrayProperty(props, "headers");
      int headerCount = intToHeader.length;
      Vector headerValues = (Vector)props.get("valueTypes");
      if (headerValues != null && headerValues.size() == headerCount) {
         Coder[] intToCoder = new Coder[headerCount];

         for (int i = 0; i < headerCount; i++) {
            String coderName = (String)headerValues.elementAt(i);
            Coder coder = (Coder)coders.get(coderName);
            if (coder == null) {
               coder = this.initializeCoder(props, coderName);
               coders.put(coderName, coder);
            }

            intToCoder[i] = coder;
         }

         this._tokenToHeaderMap = intToHeader;
         this._tokenToCoderMap = intToCoder;
         this._tokenToHashMap = new int[headerCount];

         for (int i = 0; i < headerCount; i++) {
            this._tokenToHashMap[i] = getHash(this._tokenToHeaderMap[i]);
         }

         this._strBufferLen = 0;
         this.append_STRING("messageLine");
         this.append_CHAR('.');
         this.append_STRING("valueType");
         String messageLineCoderName = getStringProperty(props, (String)(new Object(this._strBuffer, 0, this._strBufferLen)));
         this._messageLineCoder = (Coder)coders.get(messageLineCoderName);
         if (this._messageLineCoder == null) {
            this._messageLineCoder = this.initializeCoder(props, messageLineCoderName);
         }

         this._strBufferLen = 0;
         props.clear();
         props = null;
         coders.clear();
         coders = null;
         if (values != null) {
            values.removeAllElements();
            values = null;
         }

         headerValues.removeAllElements();
         headerValues = null;
      } else {
         throw new Object("Invalid codebook format");
      }
   }

   @Override
   public final void encodeHeader(String name, String value, OutputStream outs) {
      int headerToken = -1;
      int hash = getHash(name);
      int tokenCount = this._tokenToHeaderMap.length;

      for (int i = 0; i < tokenCount; i++) {
         if (hash == this._tokenToHashMap[i] && StringUtilities.strEqualIgnoreCase(this._tokenToHeaderMap[i], name, 1701707776)) {
            headerToken = i;
            break;
         }
      }

      if (headerToken != -1) {
         Coder coder = this._tokenToCoderMap[headerToken];
         outs.write(headerToken ^ 128);
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
         Coder coder = this._tokenToCoderMap[headerToken];
         nameValuePair[0] = this._tokenToHeaderMap[headerToken];
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
      this._messageLineCoder.encode(messageLine, outs);
   }

   @Override
   public final String decodeRequestLine(InputStream ins) {
      return this._messageLineCoder.decode(ins);
   }

   @Override
   public final void encodeResponseLine(String messageLine, OutputStream outs) {
      this._messageLineCoder.encode(messageLine, outs);
   }

   @Override
   public final String decodeResponseLine(InputStream ins) {
      return this._messageLineCoder.decode(ins);
   }

   @Override
   public final int getVersion() {
      return this._version;
   }

   protected GenericMessageEncoder(int version) {
      this._version = version;
      this._strBuffer = new char[0];
      this._strBufferSectionSize = Array.getSectionSize(this._strBuffer);
      Array.resize(this._strBuffer, this._strBufferSectionSize);
      this._strBufferLen = 0;
   }

   private static final int getHash(String contentType) {
      int hash = 0;
      int count = contentType.length();

      for (int i = 0; i < count; i++) {
         char nextChar = CharacterUtilities.toLowerCase(contentType.charAt(i), 1701707776);
         hash += (nextChar & 255) * (i + 1);
      }

      return hash;
   }

   private static final boolean getBooleanProperty(Hashtable props, String name) {
      boolean propValue = false;
      Vector values = (Vector)props.get(name);
      if (values != null) {
         propValue = StringUtilities.strEqualIgnoreCase("true", (String)values.firstElement(), 1701707776);
      }

      return propValue;
   }

   private static final String[] getStringArrayProperty(Hashtable props, String name) {
      Vector values = (Vector)props.get(name);
      if (values == null) {
         throw new Object("Invalid codebook format");
      }

      String[] propValue = new Object[values.size()];
      values.copyInto(propValue);
      return propValue;
   }

   private static final String getStringProperty(Hashtable props, String name) {
      Vector values = (Vector)props.get(name);
      if (values == null) {
         throw new Object("Invalid codebook format");
      }

      String propValue = (String)values.firstElement();
      return propValue;
   }

   private final Coder initializeCoder(Hashtable props, String coderName) {
      this._strBufferLen = 0;
      this.append_STRING("valueType");
      this.append_CHAR('.');
      this.append_STRING(coderName);
      this.append_CHAR('.');
      this.append_STRING("values");
      String[] escapeTokens = getStringArrayProperty(props, (String)(new Object(this._strBuffer, 0, this._strBufferLen)));
      this._strBufferLen -= 6;
      this.append_STRING("caseSensitive");
      boolean caseSensitive = getBooleanProperty(props, (String)(new Object(this._strBuffer, 0, this._strBufferLen)));
      return new EscapedTextCoder(escapeTokens, caseSensitive);
   }

   private final void readLine(InputStream in) {
      this._strBufferLen = 0;

      while (true) {
         int lastReadChar = in.read();
         switch (lastReadChar) {
            case -1:
               return;
            case 9:
            case 32:
               break;
            case 10:
            case 13:
               if (this._strBufferLen > 0) {
                  return;
               }
               break;
            default:
               this.append_CHAR((char)lastReadChar);
         }
      }
   }

   private final void append_CHAR(char c) {
      if (this._strBufferLen >= this._strBuffer.length) {
         Array.resize(this._strBuffer, this._strBuffer.length + this._strBufferSectionSize);
      }

      this._strBuffer[this._strBufferLen] = c;
      this._strBufferLen++;
   }

   private final void append_STRING(String str) {
      int strLen = str.length();
      if (this._strBufferLen + strLen > this._strBuffer.length) {
         int newLen = strLen + this._strBuffer.length + (this._strBufferSectionSize - 1) & ~(this._strBufferSectionSize - 1);
         Array.resize(this._strBuffer, newLen + this._strBufferSectionSize);
      }

      str.getChars(0, strLen, this._strBuffer, this._strBufferLen);
      this._strBufferLen += strLen;
   }
}
