package net.rim.device.apps.internal.mms.service;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.model.ExtraWSPHeaderAttachment;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;

final class MMSProtocolDataUnitWriter {
   private DataBuffer _output;
   private byte[] _byteBuffer = new byte[8];
   private boolean _forwardLocked;
   private static final byte CONNECTION_HEADER_NAME;
   private static final byte CONTENT_BASE_HEADER_NAME;
   private static final short BIG5_CHARSET_OCTET;
   private static final short ISO_10646_UCS_2_CHARSET_OCTET;
   private static final short ISO_8859_1_CHARSET_OCTET;
   private static final short ISO_8859_2_CHARSET_OCTET;
   private static final short ISO_8859_3_CHARSET_OCTET;
   private static final short ISO_8859_4_CHARSET_OCTET;
   private static final short ISO_8859_5_CHARSET_OCTET;
   private static final short ISO_8859_6_CHARSET_OCTET;
   private static final short ISO_8859_7_CHARSET_OCTET;
   private static final short ISO_8859_8_CHARSET_OCTET;
   private static final short ISO_8859_9_CHARSET_OCTET;
   private static final short SHIFT_JIS_OCTET;
   private static final short US_ASCII_OCTET;
   private static final short UTF_8_OCTET;
   private static final short UTF_16_OCTET;
   private static final short UTF_16BE_OCTET;
   private static final short UTF_16LE_OCTET;
   private static final short WINDOWS_1252_OCTET;
   private static final short GB2312_OCTET;
   private static final String BIG5_CHARSET;
   private static final String ISO_10646_UCS_2_CHARSET;
   private static final String ISO_8859_1_CHARSET;
   private static final String ISO_8859_2_CHARSET;
   private static final String ISO_8859_3_CHARSET;
   private static final String ISO_8859_4_CHARSET;
   private static final String ISO_8859_5_CHARSET;
   private static final String ISO_8859_6_CHARSET;
   private static final String ISO_8859_7_CHARSET;
   private static final String ISO_8859_8_CHARSET;
   private static final String ISO_8859_9_CHARSET;
   private static final String SHIFT_JIS;
   private static final String US_ASCII;
   private static final String UTF_8;
   private static final String UTF_16;
   private static final String UTF_16BE;
   private static final String UTF_16LE;
   private static final String WINDOWS_1252;
   private static final String GB2312;

   public MMSProtocolDataUnitWriter(DataBuffer output, int messageType) {
      this._output = output;
      this.writeMessageType(messageType);
   }

   public final void writeDeliveryReportRequested(int value) {
      this.writeByte(134);
      this.writeByte(value);
   }

   public final void writeFrom(Object context) {
      if (MMSClientServiceBook.getComposeFromScheme() == 1) {
         byte[] mynumber = this.getFromAddress(context);
         if (mynumber != null) {
            this.writeByte(137);
            this.writeByte(1 + mynumber.length + 1);
            this.writeByte(128);
            this.writeString(mynumber);
            return;
         }

         System.out.println("From field cannot be set by device.");
      }

      this.writeByte(137);
      this.writeByte(1);
      this.writeByte(129);
   }

   private final byte[] getFromAddress(Object param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/system/Phone.getInstance ()Lnet/rim/device/api/system/Phone;
      // 03: bipush 0
      // 04: invokevirtual net/rim/device/api/system/Phone.getNumber (I)Ljava/lang/String;
      // 07: astore 2
      // 08: aload 2
      // 09: ifnull 36
      // 0c: aload 2
      // 0d: invokevirtual java/lang/String.length ()I
      // 10: ifle 36
      // 13: invokestatic net/rim/device/apps/internal/mms/options/MMSClientServiceBook.getAddressingOptionsFlags ()I
      // 16: bipush 1
      // 17: iand
      // 18: ifeq 1f
      // 1b: bipush 1
      // 1c: goto 20
      // 1f: bipush 0
      // 20: istore 3
      // 21: aload 2
      // 22: invokevirtual java/lang/String.toCharArray ()[C
      // 25: iload 3
      // 26: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.convertForTransmission ([CZ)Ljava/lang/String;
      // 29: astore 2
      // 2a: aload 2
      // 2b: ldc_w "us-ascii"
      // 2e: invokevirtual java/lang/String.getBytes (Ljava/lang/String;)[B
      // 31: areturn
      // 32: astore 2
      // 33: aconst_null
      // 34: areturn
      // 35: astore 2
      // 36: aconst_null
      // 37: areturn
      // try (0 -> 25): 26 null
      // try (0 -> 25): 29 null
   }

   public final void writeMessageID(String id) {
      if (id != null && id.length() > 0) {
         this.writeByte(139);
         this.writeString(id);
      }
   }

   public final void writeMessageClass(int value) {
      this.writeByte(138);
      this.writeByte(value);
   }

   private final void writeMessageType(int value) {
      this.writeByte(140);
      this.writeByte(value);
   }

   public final void writeMMSVersion() {
      int version = MMSClientServiceBook.getMMSCVersion();
      this.writeByte(141);
      this.writeByte(128 | version);
   }

   public final void writePriority(int value) {
      this.writeByte(143);
      this.writeByte(value);
   }

   public final void writeReadStatus(int value) {
      this.writeByte(155);
      this.writeByte(value);
   }

   public final void writeReadReportRequested(int value) {
      this.writeByte(144);
      this.writeByte(value);
   }

   public final void writeReportAllowed(int value) {
      this.writeByte(145);
      this.writeByte(value);
   }

   public final void writeStatus(int value) {
      this.writeByte(149);
      this.writeByte(value);
   }

   public final void writeSubject(String subject) {
      if (subject != null && subject.length() > 0) {
         this.writeByte(150);
         this.writeSimpleOrEncodedString(subject, "utf-8");
      }
   }

   public final int writeTo(String address) {
      int count = 0;
      if (address != null && address.length() > 0) {
         count += this.writeByte(151);
         count += this.writeSimpleOrEncodedString(address, "utf-8");
      }

      return count;
   }

   public final int writeCc(String address) {
      int count = 0;
      if (address != null && address.length() > 0) {
         count += this.writeByte(130);
         count += this.writeSimpleOrEncodedString(address, "utf-8");
      }

      return count;
   }

   public final int writeBcc(String address) {
      int count = 0;
      if (address != null && address.length() > 0) {
         count += this.writeByte(129);
         count += this.writeSimpleOrEncodedString(address, "utf-8");
      }

      return count;
   }

   public final void writeTransactionID(String id) {
      if (id != null && id.length() > 0) {
         this.writeByte(152);
         this.writeString(id);
      }
   }

   public final void writeContentType(String contentType) {
      if (contentType != null && contentType.length() > 0) {
         this.writeByte(132);
         int length = contentType.length() + 1;
         if (length < 31) {
            this.writeByte(length);
         } else {
            this.writeByte(31);
            this.writeUInt(length);
         }

         this.writeString(contentType);
         this.writeByte(0);
      }
   }

   public final void writeContentLocation(String url) {
      if (url != null && url.length() > 0) {
         this.writeByte(131);
         this.writeString(url);
      }
   }

   public final void copyContent(MMSAttachment pdu) {
      this.writeByte(132);
      byte[] data = pdu.getData();
      DataBuffer dataBuffer = (DataBuffer)(new Object(data, 0, data.length, false));
      MMSProtocolDataUnit.skipToContent(dataBuffer);
      byte[] buf = new byte[8000];

      while (true) {
         int count = dataBuffer.read(buf);
         if (count <= 0) {
            return;
         }

         this._output.write(buf, 0, count);
      }
   }

   public final void setForwardLocked() {
      this._forwardLocked = true;
   }

   public final void beginContent(int attachmentCount, String attachmentName, String attachmentType) {
      this.writeByte(132);
      String content_id = ((StringBuffer)(new Object("<"))).append(attachmentName).append(">").toString();
      int length = 2 + attachmentType.length() + 1 + 1 + content_id.length() + 1;
      if (length < 31) {
         this.writeByte(length);
      } else {
         this.writeByte(31);
         this.writeUInt(length);
      }

      this.writeByte(179);
      this.writeByte(137);
      this.writeString(attachmentType);
      this.writeByte(138);
      this.writeString(content_id);
      this.writeByte(attachmentCount);
   }

   public final void beginContent(int attachmentCount) {
      this.writeByte(132);
      this.writeByte(163);
      this.writeByte(attachmentCount);
   }

   public final void beginContent(MMSAttachment attachment) {
      this.writeByte(132);
      if (this._forwardLocked) {
         attachment = DRMConverter.wrap(attachment);
      }

      int type = attachment.getType();
      if (type > 127) {
         String typeString = MMSUtilities.getMIMETypeString(type);
         this.writeUInt(typeString.length() + 1);
         this.writeString(typeString);
      } else {
         this.writeByte(128 | type);
      }

      this._output.write(attachment.getData());
   }

   public final void endContent() {
   }

   public final void addAttachment(MMSAttachment attachment) {
      if (attachment != null) {
         if (this._forwardLocked && isFwdLockContentType(attachment.getType())) {
            attachment = DRMConverter.wrap(attachment);
         }

         String name = attachment.getName();
         byte[] data = attachment.getData();
         int type = attachment.getType();
         String charset = attachment.getCharset();
         boolean writeExtraWSPHeader = attachment instanceof ExtraWSPHeaderAttachment;
         int extraWSPHeaderLength = 0;
         byte[] extraWSPHeaderData = null;
         String content_id = ((StringBuffer)(new Object("\"<"))).append(name).append(">").toString();
         String typeString = null;
         if (type > 127) {
            typeString = MMSUtilities.getMIMETypeString(type);
         }

         short mibenum = getCharSetAssignedNumber(charset);
         int wspParameterLen = (typeString == null ? 1 : typeString.length() + 1) + (mibenum >= 0 && mibenum < 128 ? 2 : 0) + 1 + name.length() + 1;
         if (writeExtraWSPHeader) {
            extraWSPHeaderData = ((ExtraWSPHeaderAttachment)attachment).getExtraWSPHeaderData();
            if (extraWSPHeaderData != null) {
               extraWSPHeaderLength = extraWSPHeaderData.length;
            }
         }

         int extraParameterLen = 1 + content_id.length() + extraWSPHeaderLength + 1;
         int headerLen = (wspParameterLen < 31 ? 1 : 2) + wspParameterLen + extraParameterLen;
         this.writeUInt(headerLen);
         this.writeUInt(data.length);
         if (wspParameterLen < 31) {
            this.writeByte(wspParameterLen);
         } else {
            this.writeByte(31);
            this.writeByte(wspParameterLen);
         }

         if (typeString != null && typeString.length() > 0) {
            this.writeString(typeString);
         } else {
            this.writeByte(128 | type);
         }

         this.writeByte(133);
         this.writeString(name);
         if (mibenum >= 0 && mibenum < 128) {
            this.writeByte(129);
            this.writeByte(128 | mibenum);
         }

         this.writeByte(192);
         this.writeString(content_id);
         if (writeExtraWSPHeader) {
            this._output.write(extraWSPHeaderData);
         }

         this._output.write(data);
      }
   }

   public final int writeByte(int value) {
      this._output.write((byte)value);
      return 1;
   }

   private final int writeString(String value) {
      byte[] data = value.getBytes();
      return this.writeString(data);
   }

   private final int writeString(byte[] data) {
      this._output.write(data);
      this._output.writeByte(0);
      return data.length + 1;
   }

   private final int writeSimpleOrEncodedString(String value, String charset) {
      try {
         byte[] data = value.getBytes("us-ascii");
         if (data != null) {
            String str2 = (String)(new Object(data, charset));
            if (value.equals(str2)) {
               return this.writeString(data);
            }
         }
      } finally {
         return this.writeEncodedString(value, charset);
      }

      return this.writeEncodedString(value, charset);
   }

   private final int writeEncodedString(String value) {
      return this.writeEncodedString(value, "utf-8");
   }

   private final int writeEncodedString(String value, String charset) {
      int count = 0;
      short mibenum = getCharSetAssignedNumber(charset);
      if (mibenum < 0 || mibenum > 127) {
         charset = "us-ascii";
         mibenum = 3;
      }

      byte[] bytes = value.getBytes(charset);
      int length = 1 + bytes.length + 1;
      boolean useQuoteCharacter = bytes.length > 0 && (bytes[0] & 128) != 0;
      if (useQuoteCharacter) {
         length++;
      }

      if (length < 31) {
         count += this.writeByte(length);
      } else {
         count += this.writeByte(31);
         count += this.writeUInt(length);
      }

      count += this.writeByte(128 | mibenum);
      if (useQuoteCharacter) {
         count += this.writeByte(127);
      }

      this._output.write(bytes);
      this._output.writeByte(0);
      return count + bytes.length + 1;
   }

   private final int writeUInt(long value) {
      int count = 0;

      do {
         this._byteBuffer[count] = (byte)(value & 127);
         count++;
         value >>>= 7;
      } while (value != 0);

      for (int idx = count - 1; idx > 0; idx--) {
         this._output.writeByte(128 | this._byteBuffer[idx]);
      }

      this._output.writeByte(this._byteBuffer[0]);
      return count;
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

   private static final short getCharSetAssignedNumber(String charset) {
      if (charset == null) {
         return 32767;
      }

      int hash = getHash(charset);
      String value = null;
      short id = 32767;
      switch (hash) {
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
         case 4813:
            id = 17;
            value = "shift_JIS";
            break;
         case 5617:
            id = 2252;
            value = "windows-1252";
            break;
         case 5829:
            id = 2026;
            value = "big5";
            break;
         case 8461:
            id = 1000;
            value = "iso-10646-ucs-2";
      }

      return value != null && StringUtilities.compareToIgnoreCase(charset, value, 1701707776) == 0 ? id : 32767;
   }

   private static final boolean isFwdLockContentType(int type) {
      return MMSUtilities.isAudioType(type) || MMSUtilities.isImageType(type) || MMSUtilities.isVideoType(type);
   }
}
