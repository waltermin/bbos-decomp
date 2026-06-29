package net.rim.device.apps.internal.mms.service;

import com.fourthpass.wapstack.wsp.WSPHeaderDecoder;
import java.io.DataInput;
import java.io.EOFException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.model.MMSAttachmentImpl;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.vm.Array;

public final class MMSProtocolDataUnit implements AttachmentDataProvider {
   private StringBuffer _buf = (StringBuffer)(new Object());
   private Hashtable _headers = (Hashtable)(new Object());
   private Vector _recipients;
   private Vector _ccRecipients;
   private Vector _bccRecipients;
   private byte[][] _content;
   private Hashtable[] _contentHeader;
   private boolean _forwardLocked;
   private boolean _truncated;
   private static final String CONTENT_LOCATION_HEADER_NAME_STRING = "content-location";
   private static final String CONTENT_ID_HEADER_NAME_STRING = "content-id";
   private static final String X_WAP_CONTENT_URI_HEADER_NAME_STRING = "x-wap-content-uri";
   private static String[] WSP_PARAMETER_NAMES = new Object[]{
      "wsp-q",
      "wsp-charset",
      "wsp-level",
      "wsp-type",
      null,
      "wsp-name",
      "wsp-filename",
      "wsp-differences",
      "wsp-padding",
      "wsp-type",
      "wsp-start",
      "wsp-start-info",
      "wsp-comment",
      "wsp-domain",
      "wsp-max-age",
      "wsp-path",
      "wsp-secure",
      "wsp-sec",
      "wsp-mac",
      "wsp-creation-date",
      "wsp-modification-date",
      "wsp-read-date",
      "wsp-size",
      "wsp-name",
      "wsp-filename",
      "wsp-start",
      "wsp-start-info",
      "wsp-comment",
      "wsp-domain",
      "wsp-path"
   };

   final void dumpFields() {
      this.dumpAttributes("=====PDU attributes", this._headers);
      System.out.println(((StringBuffer)(new Object("encrypted="))).append(this._forwardLocked).toString());
      this.dumpRecipients("To:", this._recipients);
      this.dumpRecipients("Cc:", this._ccRecipients);
      this.dumpRecipients("Bcc:", this._bccRecipients);
      if (this._contentHeader != null) {
         int attachmentCount = this._contentHeader.length;

         for (int idx = 0; idx < attachmentCount; idx++) {
            this.dumpAttributes(((StringBuffer)(new Object("===Attachment#"))).append(idx).toString(), this._contentHeader[idx]);
         }
      }
   }

   public final int getType() {
      return MMSUtilities.parseInt((String)this._headers.get("x-mms-message-type"), 0);
   }

   public final boolean isForwardLocked() {
      return this._forwardLocked;
   }

   public final boolean isTruncated() {
      return this._truncated;
   }

   public final Vector getRecipients() {
      return this._recipients;
   }

   public final Vector getCcRecipients() {
      return this._ccRecipients;
   }

   public final Vector getBccRecipients() {
      return this._bccRecipients;
   }

   public final Enumeration attributeNames() {
      return this._headers.keys();
   }

   public final String getAttribute(String attributeName) {
      return (String)this._headers.get(attributeName);
   }

   public final MMSAttachment getAttachment(int idx) {
      return new MMSAttachmentImpl(this.getAttachmentName(idx, 0), this.getAttachmentType(idx), this.getAttachmentData(idx), this.getAttachmentCharset(idx));
   }

   @Override
   public final boolean hasAttachment(String attachmentName) {
      if (attachmentName != null) {
         int count = this.getAttachmentCount();

         for (int idx = 0; idx < count; idx++) {
            if (this.equalsAttachmentName(idx, attachmentName)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public final int getAttachmentType(String attachmentName) {
      if (attachmentName != null) {
         int count = this.getAttachmentCount();

         for (int idx = 0; idx < count; idx++) {
            if (this.equalsAttachmentName(idx, attachmentName)) {
               return this.getAttachmentType(idx);
            }
         }
      }

      return -1;
   }

   @Override
   public final int getTotalAttachmentDataSize() {
      int dataSize = 0;
      int count = this.getAttachmentCount();

      for (int idx = 0; idx < count; idx++) {
         dataSize += this._content[idx].length;
      }

      return dataSize;
   }

   @Override
   public final MMSAttachment getAttachment(String attachmentName) {
      int idx = this.findAttachment(attachmentName);
      return idx >= 0 ? new MMSAttachmentImpl(attachmentName, this.getAttachmentType(idx), this.getAttachmentData(idx), this.getAttachmentCharset(idx)) : null;
   }

   @Override
   public final boolean hasAttachments() {
      return this.getAttachmentCount() > 0;
   }

   @Override
   public final Enumeration attachmentNames() {
      return new MMSProtocolDataUnit$1(this);
   }

   private final void internalSkipToContent(DataInput input) {
      while (true) {
         byte id = input.readByte();
         if (id == -124) {
            return;
         }

         this.readField(input, id);
      }
   }

   private final void dumpRecipients(String label, Vector recipients) {
      if (recipients != null) {
         int count = recipients.size();

         for (int idx = 0; idx < count; idx++) {
            String name = (String)recipients.elementAt(idx);
            System.out.println(((StringBuffer)(new Object())).append(label).append(name).toString());
         }
      }
   }

   public MMSProtocolDataUnit(byte[] buffer, int offset, int length) {
      byte[] data = MMSUtilities.decrypt(buffer);
      if (data != null) {
         if (data != buffer) {
            this._forwardLocked = true;
         }

         this.readAllFields((DataInput)(new Object(data, offset, length, false)));
      }
   }

   public MMSProtocolDataUnit(DataInput aDataInput) {
      this.readAllFields(aDataInput);
   }

   private MMSProtocolDataUnit() {
   }

   private final void dumpAttributes(String title, Hashtable attributes) {
      System.out.println(title);
      Enumeration names = attributes.keys();

      while (names.hasMoreElements()) {
         String name = (String)names.nextElement();
         System.out.println(((StringBuffer)(new Object())).append(name).append('=').append(this.getAttribute(name)).toString());
      }
   }

   private final int findAttachment(String attachmentName) {
      if (attachmentName != null) {
         int count = this.getAttachmentCount();

         for (int idx = 0; idx < count; idx++) {
            if (this.equalsAttachmentName(idx, attachmentName)) {
               return idx;
            }
         }
      }

      return -1;
   }

   public MMSProtocolDataUnit(byte[] buffer) {
      this(buffer, 0, buffer.length);
   }

   static final void skipToContent(DataInput input) {
      new MMSProtocolDataUnit().internalSkipToContent(input);
   }

   private final int getAttachmentCount() {
      return this._content != null ? this._content.length : 0;
   }

   private final byte[] getAttachmentData(int idx) {
      return this._content[idx];
   }

   private final String getAttachmentName(int attachmentNumber, int index) {
      Hashtable contentHeader = this._contentHeader[attachmentNumber];
      if (contentHeader == null) {
         return null;
      }

      String name = (String)contentHeader.get("wsp-name");
      if (name != null) {
         if (index == 0) {
            return name;
         }

         index--;
      }

      name = (String)contentHeader.get("content-location");
      if (name != null) {
         if (index == 0) {
            return name;
         }

         index--;
      }

      name = (String)contentHeader.get("content-id");
      if (name != null) {
         if (index == 0) {
            return name;
         }

         index--;
      }

      name = (String)contentHeader.get("x-wap-content-uri");
      if (name != null) {
         if (index == 0) {
            return name;
         }

         index--;
      }

      return null;
   }

   private static final String trimAttachmentName(String name) {
      if (name != null) {
         int nameLength = name.length();
         if (nameLength > 4 && name.charAt(0) == '"' && name.charAt(1) == '<' && name.charAt(nameLength - 2) == '>' && name.charAt(nameLength - 1) == '"') {
            return name.substring(2, nameLength - 2);
         }

         if (nameLength > 2 && name.charAt(0) == '<' && name.charAt(nameLength - 1) == '>') {
            name = name.substring(1, nameLength - 1);
         }
      }

      return name;
   }

   private final boolean equalsAttachmentName(int attachmentNumber, String attachmentName) {
      int attachmentNameLength = attachmentName.length();
      int nameIndex = 0;

      while (true) {
         String name = this.getAttachmentName(attachmentNumber, nameIndex);
         if (name == null) {
            return false;
         }

         int nameLength = name.length();
         if (nameLength > 4 && name.charAt(0) == '"' && name.charAt(1) == '<' && name.charAt(nameLength - 2) == '>' && name.charAt(nameLength - 1) == '"') {
            if (attachmentNameLength == nameLength - 4 && StringUtilities.regionMatches(name, true, 2, attachmentName, 0, nameLength - 4, 1701707776)) {
               return true;
            }
         } else if (nameLength > 2 && name.charAt(0) == '<' && name.charAt(nameLength - 1) == '>') {
            if (attachmentNameLength == nameLength - 2 && StringUtilities.regionMatches(name, true, 1, attachmentName, 0, nameLength - 2, 1701707776)) {
               return true;
            }
         } else if (attachmentNameLength == nameLength && StringUtilities.regionMatches(name, true, 0, attachmentName, 0, nameLength, 1701707776)) {
            return true;
         }

         nameIndex++;
      }
   }

   private final boolean hasAttachmentName(int idx) {
      return this.getAttachmentName(idx, 0) != null;
   }

   private final int getAttachmentType(int index) {
      String type = (String)this._contentHeader[index].get("content-type");
      return parseAttachmentType(type);
   }

   private static final int parseAttachmentType(String type) {
      int v = MMSUtilities.parseInt(type, -1);
      if (v == -1 && type != null) {
         v = MMSUtilities.getMIMEType(type);
      }

      return v;
   }

   private final String getAttachmentCharset(int index) {
      return (String)this._contentHeader[index].get("wsp-charset");
   }

   private final void readAllFields(DataInput input) {
      try {
         while (true) {
            this.readField(input, input.readByte());
         }
      } finally {
         return;
      }
   }

   private final void readField(DataInput input, byte id) {
      if ((id & 128) == 0) {
         String name = ((StringBuffer)(new Object())).append((char)id).append(this.readTextString(input)).toString();
         this._headers.put(name, this.readTextString(input));
      } else {
         switch (id & 127) {
            case 0:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
               break;
            case 1:
            default:
               if (this._bccRecipients == null) {
                  this._bccRecipients = (Vector)(new Object());
               }

               this._bccRecipients.addElement(this.readEncodedString(input));
               return;
            case 2:
               if (this._ccRecipients == null) {
                  this._ccRecipients = (Vector)(new Object());
               }

               this._ccRecipients.addElement(this.readEncodedString(input));
               return;
            case 3:
               this._headers.put("x-mms-content-location", this.readTextString(input));
               return;
            case 4:
               this._truncated = false;
               copyParameters(this._headers, this.readContentType(input));
               int contentType = MMSUtilities.getMIMEType((String)this._headers.get("content-type"));
               if (MMSUtilities.isMultipartType(contentType)) {
                  this.readMultipartContent(input);
                  return;
               }

               String name = (String)this._headers.get("wsp-name");
               this.readSimpleContent(name, contentType, input);
               return;
            case 5:
               this._headers.put("date", this.readInteger(input));
               return;
            case 6:
               this._headers.put("x-mms-delivery-report", this.readShortInteger(input));
               return;
            case 7:
               this.readValueLength(input);
               int adToken = input.readUnsignedByte();
               if (adToken == 128) {
                  this._headers.put("x-mms-delivery-time-absolute", Integer.toString(adToken));
               }

               this._headers.put("x-mms-delivery-time", this.readInteger(input));
               return;
            case 8:
               this.readValueLength(input);
               int aeToken = input.readUnsignedByte();
               if (aeToken == 128) {
                  this._headers.put("x-mms-expiry-absolute", Integer.toString(aeToken));
               }

               this._headers.put("x-mms-expiry", this.readInteger(input));
               return;
            case 9:
               this.readValueLength(input);
               switch (input.readByte() & 0xFF) {
                  case 128:
                     this._headers.put("from", this.readEncodedString(input));
                     return;
                  default:
                     return;
               }
            case 10:
               this._headers.put("x-mms-message-class", this.readMessageClass(input));
               return;
            case 11:
               this._headers.put("message-id", this.readTextString(input));
               return;
            case 12:
               int messageType = input.readUnsignedByte();
               if (messageType == 132) {
                  this._truncated = true;
               }

               this._headers.put("x-mms-message-type", Integer.toString(messageType));
               return;
            case 13:
               this._headers.put("x-mms-mms-version", readVersionNumber(input));
               return;
            case 14:
               this._headers.put("x-mms-message-size", this.readInteger(input));
               return;
            case 15:
               this._headers.put("x-mms-priority", this.readShortInteger(input));
               return;
            case 16:
               this._headers.put("x-mms-read-report", this.readShortInteger(input));
               return;
            case 17:
               this._headers.put("x-mms-report-allowed", this.readShortInteger(input));
               return;
            case 18:
               this._headers.put("x-mms-response-status", this.readShortInteger(input));
               return;
            case 19:
               this._headers.put("x-mms-response-text", this.readEncodedString(input));
               return;
            case 20:
               this._headers.put("x-mms-sender-visibility", this.readInteger(input));
               return;
            case 21:
               this._headers.put("x-mms-status", this.readShortInteger(input));
               return;
            case 22:
               this._headers.put("subject", this.readEncodedString(input));
               return;
            case 23:
               if (this._recipients == null) {
                  this._recipients = (Vector)(new Object());
               }

               this._recipients.addElement(this.readEncodedString(input));
               return;
            case 24:
               this._headers.put("x-mms-transaction-id", this.readTextString(input));
               return;
            case 25:
               this._headers.put("x-mms-retrieve-status", this.readShortInteger(input));
               return;
            case 26:
               this._headers.put("x-mms-retrieve-text", this.readEncodedString(input));
               return;
            case 27:
               this._headers.put("x-mms-read-status", this.readShortInteger(input));
               return;
            case 28:
               this._headers.put("x-mms-reply-charging", this.readShortInteger(input));
               return;
            case 29:
               this._headers.put("x-mms-reply-charging-deadline", this.readShortInteger(input));
               return;
            case 30:
               this._headers.put("x-mms-reply-charging-id", this.readTextString(input));
               return;
            case 31:
               this._headers.put("x-mms-reply-charging-size", this.readInteger(input));
               return;
            case 32:
               readShortInt(input);
               this._headers.put("x-mms-previously-sent-count", this.readInteger(input));
               this._headers.put("x-mms-previously-sent-by", this.readEncodedString(input));
               return;
            case 33:
               readShortInt(input);
               this._headers.put("x-mms-previously-sent-count", this.readInteger(input));
               this._headers.put("x-mms-previously-sent-date", this.readInteger(input));
               return;
            case 34:
               this.readShortInteger(input);
               return;
            case 35:
               this.readShortInteger(input);
               return;
            case 36:
               readShortInt(input);
               this.readShortInteger(input);
               this.readEncodedString(input);
               return;
            case 37:
               this.readShortInteger(input);
               return;
            case 38:
               this.readEncodedString(input);
               return;
            case 49:
               this.readShortInteger(input);
               return;
            case 50:
               input.skipBytes(readShortInt(input));
               return;
            case 51:
               this.readInteger(input);
         }
      }
   }

   private final String readTextString(DataInput input) {
      return this.readTextString(input, 0);
   }

   private final String readTextString(DataInput input, int offset) {
      this._buf.setLength(offset);

      try {
         for (char ch = (char)input.readByte(); ch != 0; ch = (char)input.readByte()) {
            this._buf.append(ch);
         }
      } finally {
         return this._buf.toString();
      }

      return this._buf.toString();
   }

   private final int readValueLength(DataInput input) {
      try {
         int val = input.readUnsignedByte();
         if (val < 31) {
            return val;
         }

         if (val == 31) {
            return readUInt(input);
         }
      } finally {
         return -1;
      }

      return -1;
   }

   private final String readEncodedString(DataInput input) {
      try {
         int val = input.readUnsignedByte();
         if (val > 31) {
            this._buf.setLength(0);
            this._buf.append((char)val);
            return this.readTextString(input, 1);
         }

         int length = val;
         if (length == 31) {
            length = readUInt(input);
         }

         if (length <= 0) {
            return "";
         }

         int mibenum = readShortInt(input);
         length--;
         String encoding = WSPHeaderDecoder.getCharsetName(mibenum);
         if (length <= 0) {
            return "";
         }

         byte[] bytes = new byte[length - 1];
         input.readFully(bytes);
         if (bytes.length > 0 && mibenum == 106 && bytes[0] == 127) {
            int newLength = bytes.length - 1;
            System.arraycopy(bytes, 1, bytes, 0, newLength);
            Array.resize(bytes, newLength);
         }

         char ch = (char)input.readUnsignedByte();
         if (ch == 0) {
            if (encoding == null) {
               System.out
                  .println(
                     ((StringBuffer)(new Object("MMS Parse Error in readEncodedString - charset was null (mibenum was ")))
                        .append(mibenum)
                        .append(")")
                        .toString()
                  );
               return (String)(new Object(bytes));
            }

            try {
               return (String)(new Object(bytes, encoding));
            } finally {
               System.out
                  .println(
                     ((StringBuffer)(new Object("MMS - Unsupported Encoding: ")))
                        .append(encoding)
                        .append(" (mibenum was ")
                        .append(mibenum)
                        .append(")")
                        .toString()
                  );
               return (String)(new Object(bytes));
            }
         } else {
            System.out.println("MMS Parse Error in readEncodedString - invalid state");
            return (String)(encoding == null ? new Object(bytes) : new Object(bytes, encoding));
         }
      } finally {
         ;
      }
   }

   private static final int readUInt(DataInput input) {
      int val = 0;

      byte b;
      do {
         b = input.readByte();
         val = (val << 7) + (b & 127);
      } while ((b & 128) != 0);

      return val;
   }

   private static final int readShortInt(DataInput input) {
      return input.readUnsignedByte() & 127;
   }

   private static final int readLongInt(DataInput input, int count) {
      int val = 0;

      while (count > 0) {
         val = (val << 8) + input.readUnsignedByte();
         count--;
      }

      return val;
   }

   private final String readShortInteger(DataInput input) {
      int val = input.readUnsignedByte();
      return Integer.toString(val);
   }

   private final String readInteger(DataInput input) {
      int val = input.readUnsignedByte();
      return (val & 128) == 0 ? Integer.toString(readLongInt(input, val)) : Integer.toString(val & 127);
   }

   private final String readMessageClass(DataInput input) {
      int val = input.readUnsignedByte();
      return (val & 128) == 0 ? ((StringBuffer)(new Object())).append((char)val).append(this.readTextString(input)).toString() : Integer.toString(val);
   }

   private final Hashtable readContentType(DataInput input) {
      Hashtable headers = null;
      byte b = input.readByte();
      String contentType;
      if ((b & 128) != 0) {
         contentType = Integer.toString(b & 127);
      } else {
         int length = b;
         if (length == 31) {
            length = readUInt(input);
         }

         b = input.readByte();
         if ((b & 128) != 0) {
            contentType = Integer.toString(b & 127);
            length--;
         } else {
            contentType = ((StringBuffer)(new Object())).append((char)b).append(this.readTextString(input)).toString();
            length -= contentType.length() + 1;
         }

         headers = decodeWSPParameters(input, length);
      }

      if (headers == null) {
         headers = (Hashtable)(new Object());
      }

      headers.put("content-type", contentType);
      return headers;
   }

   private final void readSimpleContent(String name, int type, DataInput input) {
      if (name == null) {
         name = MMSUtilities.getDefaultAttachmentName(type);
      }

      if (type == 65549) {
         type = MMSUtilities.getImpliedAttachmentType(name);
      }

      byte[] data = readRemaining(input);
      if (type != 72) {
         Hashtable header = (Hashtable)(new Object());
         header.put("wsp-name", name);
         header.put("content-type", Integer.toString(type));
         this.addAttachment(header, data);
      } else {
         this._forwardLocked = true;
         MMSAttachment attachment = DRMConverter.unwrap(new MMSAttachmentImpl(name, type, data, null));
         if (attachment == null) {
            System.out.println("PDU unwrap error.");
         } else {
            byte[] innerData = attachment.getData();
            DataBuffer innerBuffer = (DataBuffer)(new Object(innerData, 0, innerData.length, false));
            copyParameters(this._headers, this.readContentType(innerBuffer));
            int contentType = MMSUtilities.getMIMEType((String)this._headers.get("content-type"));
            if (MMSUtilities.isMultipartType(contentType)) {
               this.readMultipartContent(innerBuffer);
            } else {
               name = (String)this._headers.get("wsp-name");
               this.readSimpleContent(name, contentType, input);
            }
         }
      }
   }

   private final void readMultipartContent(DataInput param1) throws EOFException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: invokestatic net/rim/device/apps/internal/mms/service/MMSProtocolDataUnit.readShortInt (Ljava/io/DataInput;)I
      // 04: istore 2
      // 05: bipush 0
      // 06: istore 3
      // 07: iload 3
      // 08: iload 2
      // 09: if_icmplt 0f
      // 0c: goto 91
      // 0f: aload 1
      // 10: invokestatic net/rim/device/apps/internal/mms/service/MMSProtocolDataUnit.readUInt (Ljava/io/DataInput;)I
      // 13: istore 4
      // 15: aload 1
      // 16: invokestatic net/rim/device/apps/internal/mms/service/MMSProtocolDataUnit.readUInt (Ljava/io/DataInput;)I
      // 19: istore 5
      // 1b: aload 0
      // 1c: aload 1
      // 1d: iload 4
      // 1f: invokespecial net/rim/device/apps/internal/mms/service/MMSProtocolDataUnit.readContentHeader (Ljava/io/DataInput;I)Ljava/util/Hashtable;
      // 22: astore 6
      // 24: aload 6
      // 26: ldc_w "content-type"
      // 29: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 2c: checkcast java/lang/Object
      // 2f: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.getMIMEType (Ljava/lang/String;)I
      // 32: istore 7
      // 34: iload 7
      // 36: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.isMultipartType (I)Z
      // 39: ifeq 44
      // 3c: aload 0
      // 3d: aload 1
      // 3e: invokespecial net/rim/device/apps/internal/mms/service/MMSProtocolDataUnit.readMultipartContent (Ljava/io/DataInput;)V
      // 41: goto 8b
      // 44: iload 5
      // 46: newarray 8
      // 48: astore 8
      // 4a: aload 1
      // 4b: aload 8
      // 4d: invokeinterface java/io/DataInput.readFully ([B)V 2
      // 52: aload 0
      // 53: aload 6
      // 55: aload 8
      // 57: invokespecial net/rim/device/apps/internal/mms/service/MMSProtocolDataUnit.addAttachment (Ljava/util/Hashtable;[B)V
      // 5a: goto 8b
      // 5d: astore 9
      // 5f: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 62: ldc_w "MMS - incomplete content"
      // 65: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 68: aload 0
      // 69: bipush 1
      // 6a: putfield net/rim/device/apps/internal/mms/service/MMSProtocolDataUnit._truncated Z
      // 6d: aload 9
      // 6f: athrow
      // 70: astore 9
      // 72: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 75: ldc_w "MMS - parsing error"
      // 78: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 7b: aload 0
      // 7c: bipush 1
      // 7d: putfield net/rim/device/apps/internal/mms/service/MMSProtocolDataUnit._truncated Z
      // 80: new java/lang/Object
      // 83: dup
      // 84: ldc_w "MMSParseException"
      // 87: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 8a: athrow
      // 8b: iinc 3 1
      // 8e: goto 07
      // 91: return
      // try (36 -> 43): 44 null
      // try (36 -> 43): 53 null
   }

   private final void addAttachment(Hashtable contentHeader, byte[] content) {
      int idx;
      if (this._contentHeader == null) {
         idx = 0;
         this._contentHeader = new Object[1];
         this._content = new byte[1][];
      } else {
         idx = this._contentHeader.length;
         Array.resize(this._contentHeader, idx + 1);
         Array.resize(this._content, idx + 1);
      }

      this._contentHeader[idx] = contentHeader;
      this._content[idx] = content;
      String name = this.getAttachmentName(idx, 0);
      if (name == null || this.findAttachment(name) != idx) {
         name = this.makeUniqueName(name);
         contentHeader.put("content-location", name);
      }

      if (this.getAttachmentType(idx) == 72) {
         this._forwardLocked = true;
         MMSAttachment attachment = DRMConverter.unwrap(this.getAttachment(idx));
         if (attachment != null) {
            this._content[idx] = attachment.getData();
            contentHeader.put("content-location", attachment.getName());
            contentHeader.put("content-type", Integer.toString(attachment.getType()));
         }
      }
   }

   private final String makeUniqueName(String name) {
      String prefix;
      String suffix;
      if (name == null) {
         prefix = "CID";
         suffix = "";
      } else {
         int idx = name.lastIndexOf(46);
         if (idx < 0) {
            prefix = name;
            suffix = "";
         } else {
            prefix = name.substring(0, idx);
            suffix = name.substring(idx);
         }
      }

      int idx = 0;

      while (true) {
         name = ((StringBuffer)(new Object())).append(prefix).append(idx).append(suffix).toString();
         if (this.findAttachment(name) < 0) {
            return name;
         }

         idx++;
      }
   }

   private final Hashtable readContentHeader(DataInput input, int headerLength) {
      Hashtable header = (Hashtable)(new Object());
      byte b = input.readByte();
      headerLength--;
      String contentType;
      if ((b & 128) != 0) {
         contentType = Integer.toString(b & 127);
         header.put("content-type", contentType);
      } else {
         int length = b;
         if (length != 31) {
            if (length > 31) {
               length = 0;
            } else {
               b = input.readByte();
               headerLength--;
               length--;
            }
         } else {
            length = 0;

            do {
               b = input.readByte();
               headerLength--;
               length = (length << 7) + (b & 127);
            } while ((b & 128) != 0);

            b = input.readByte();
            headerLength--;
            length--;
         }

         if ((b & 128) != 0) {
            contentType = Integer.toString(b & 127);
            header.put("content-type", contentType);
         } else {
            contentType = ((StringBuffer)(new Object())).append((char)b).append(this.readTextString(input)).toString();
            header.put("content-type", contentType);
            int byteCount = contentType.length();
            headerLength -= byteCount;
            length -= byteCount;
         }

         if (length > 0) {
            copyParameters(header, decodeWSPParameters(input, length));
            headerLength -= length;
         }
      }

      if (this.isForwardLocked() || parseAttachmentType(contentType) == 72) {
         this._headers.put("drm-status", Integer.toString(1));
      }

      copyParameters(header, decodeWSPHeader(input, headerLength));
      return header;
   }

   private static final Hashtable decodeWSPHeader(DataInput input, int len) {
      if (len <= 0) {
         return null;
      }

      byte[] header = new byte[len];
      input.readFully(header);
      HttpHeaders hdrs = (HttpHeaders)(new Object());
      WSPHeaderDecoder decoder = (WSPHeaderDecoder)(new Object(hdrs));
      decoder.decode(header, false);
      return hdrs.toHashtable();
   }

   private static final Hashtable decodeWSPParameters(DataInput input, int len) {
      Hashtable params = (Hashtable)(new Object());
      byte[] buf = new byte[len];
      input.readFully(buf);
      StringBuffer sb = (StringBuffer)(new Object());
      int index = 0;

      label64:
      while (index < len) {
         int id = buf[index++] & 127;
         if (id > 32) {
            sb.setLength(0);
            sb.append((char)id);

            while (index < len) {
               byte b = buf[index++];
               if (b == 0) {
                  break;
               }

               sb.append((char)b);
            }

            String paramName = sb.toString();
            sb.setLength(0);

            while (index < len) {
               byte b = buf[index++];
               if (b == 0) {
                  break;
               }

               sb.append((char)b);
            }

            params.put(paramName, sb.toString());
         } else {
            switch (id) {
               case 0:
               case 2:
               case 3:
               case 4:
               case 7:
               case 8:
               case 9:
               case 14:
               case 15:
               case 19:
               case 20:
               case 21:
               case 22:
                  index = len;
                  break;
               case 1:
                  Object charset = WSPHeaderDecoder.getCharsetName(buf[index++] & 127);
                  if (charset != null) {
                     params.put(WSP_PARAMETER_NAMES[id], charset);
                  }
                  break;
               case 5:
               case 6:
               case 10:
               case 11:
               case 12:
               case 13:
               case 18:
               case 23:
               case 24:
               case 25:
               case 26:
               case 27:
               case 28:
               case 29:
               default:
                  sb.setLength(0);

                  while (true) {
                     if (index < len) {
                        byte b = buf[index++];
                        if (b != 0) {
                           sb.append((char)b);
                           continue;
                        }
                     }

                     params.put(WSP_PARAMETER_NAMES[id], sb.toString());
                     continue label64;
                  }
               case 16:
                  params.put(WSP_PARAMETER_NAMES[id], "");
                  break;
               case 17:
                  params.put(WSP_PARAMETER_NAMES[id], Integer.toString(buf[index++] & 255));
            }
         }
      }

      return params;
   }

   private static final String readVersionNumber(DataInput input) {
      int ver = input.readByte() & 127;
      return Integer.toString(ver);
   }

   private static final void copyParameters(Hashtable target, Hashtable source) {
      if (source != null) {
         Enumeration keys = source.keys();

         while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)source.get(key);
            String var5 = URIDecoder.decode(value, "utf-8");
            target.put(key, var5);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final byte[] readRemaining(DataInput input) {
      int bufsize = 2048;
      byte[] buf = new byte[bufsize];
      byte[] data = new byte[0];

      while (bufsize > 0) {
         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            input.readFully(buf, 0, bufsize);
            int ioe = data.length;
            Array.resize(data, data.length + bufsize);
            System.arraycopy(buf, 0, data, ioe, bufsize);
            var6 = false;
         } finally {
            if (var6) {
               bufsize /= 2;
               continue;
            }
         }
      }

      return data;
   }
}
