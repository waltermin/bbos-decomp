package net.rim.device.apps.internal.browser.dd;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.plugin.media.MediaRenderingConverter;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.cldc.io.utility.URIEncoder;
import net.rim.device.internal.system.InternalServices;

final class DownloadDescriptorAttributes {
   private String[] _attributes = new Object[11];
   private String[] _types = new Object[0];
   private String[] _errors;
   private int _statusCode = 900;
   private int _size = -1;
   private static final int MAJOR_VERSION_SUPPORTED;
   public static final int TYPE;
   public static final int OBJECT_URI;
   public static final int SIZE;
   public static final int NAME;
   public static final int DD_VERSION;
   public static final int VENDOR;
   public static final int DESCRIPTION;
   public static final int INSTALL_NOTIFY_URI;
   public static final int NEXT_URL;
   public static final int INFO_URL;
   public static final int ICON_URI;
   public static final int INSTALL_PARAM;
   private static final int NUM_ATTRIBUTES;
   private static final int UNKNOWN;
   private static final int MINIMUM_ATTRIBUTE_LENGTH;

   public DownloadDescriptorAttributes() {
   }

   public final String[] getErrors() {
      return this._errors;
   }

   public final String[] getTypes() {
      return this._types;
   }

   public final String getAttribute(int attributeToken) {
      if (attributeToken == 1000) {
         return this._types.length > 0 ? this._types[0] : null;
      }

      try {
         return this._attributes[attributeToken];
      } finally {
         throw new Object();
      }
   }

   final void setAttribute(String attribute, char[] value, int valueStart, int valueLength) {
      int attributeToken = getAttributeToken(attribute);
      if (attributeToken != -1) {
         String valueStr = ((String)(new Object(value, valueStart, valueLength))).trim();
         if (attributeToken == 1000) {
            int semicolon = valueStr.indexOf(59);
            if (semicolon >= 0) {
               valueStr = valueStr.substring(0, semicolon).trim();
            }

            Arrays.add(this._types, valueStr);
            this.validate(attributeToken, attribute, valueStr);
            return;
         }

         if (this._attributes[attributeToken] == null) {
            this._attributes[attributeToken] = valueStr;
            this.validate(attributeToken, attribute, valueStr);
         }
      }
   }

   public final String getURI(int attributeToken) {
      String anyURI = this.getAttribute(attributeToken);
      if (anyURI == null) {
         return null;
      }

      anyURI = URIEncoder.encodeNonUSASCII(anyURI, false);
      int anyURILength = anyURI.length();
      StringBuffer uri = (StringBuffer)(new Object(anyURILength + 8));

      for (int i = 0; i < anyURILength; i++) {
         char ch = anyURI.charAt(i);
         if (ch > 31
            && ch != 127
            && ch != ' '
            && ch != '<'
            && ch != '>'
            && ch != '"'
            && ch != '{'
            && ch != '}'
            && ch != '|'
            && ch != '\\'
            && ch != '^'
            && ch != '`') {
            uri.append(ch);
         } else {
            uri.append('%');
            uri.append(NumberUtilities.intToUpperHexDigit(ch >> 4));
            uri.append(NumberUtilities.intToUpperHexDigit(ch));
         }
      }

      return uri.toString();
   }

   final void finishValidation() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes._types [Ljava/lang/String;
      // 004: arraylength
      // 005: ifne 00f
      // 008: aload 0
      // 009: ldc_w "type"
      // 00c: invokespecial net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes.addErrorForMissingAttribute (Ljava/lang/String;)V
      // 00f: aload 0
      // 010: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes._attributes [Ljava/lang/String;
      // 013: bipush 1
      // 014: aaload
      // 015: ifnonnull 01f
      // 018: aload 0
      // 019: ldc_w "size"
      // 01c: invokespecial net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes.addErrorForMissingAttribute (Ljava/lang/String;)V
      // 01f: aload 0
      // 020: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes._attributes [Ljava/lang/String;
      // 023: bipush 0
      // 024: aaload
      // 025: ifnonnull 02f
      // 028: aload 0
      // 029: ldc_w "objectURI"
      // 02c: invokespecial net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes.addErrorForMissingAttribute (Ljava/lang/String;)V
      // 02f: aload 0
      // 030: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes._statusCode I
      // 033: sipush 900
      // 036: if_icmpeq 03c
      // 039: goto 16f
      // 03c: aload 0
      // 03d: aload 0
      // 03e: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes._types [Ljava/lang/String;
      // 041: invokespecial net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes.validateTypes ([Ljava/lang/String;)Z
      // 044: pop
      // 045: bipush 0
      // 046: istore 1
      // 047: bipush 1
      // 048: istore 2
      // 049: bipush -1
      // 04b: i2l
      // 04c: lstore 3
      // 04d: invokestatic net/rim/device/internal/io/file/FileSystemOptions.getContentStoreMaxFileSize ()J
      // 050: lstore 5
      // 052: invokestatic javax/microedition/io/file/FileSystemRegistry.listRoots ()Ljava/util/Enumeration;
      // 055: astore 7
      // 057: aload 7
      // 059: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 05e: ifne 064
      // 061: goto 122
      // 064: aload 7
      // 066: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 06b: checkcast java/lang/Object
      // 06e: astore 8
      // 070: iload 2
      // 071: ifeq 081
      // 074: ldc_w "store/"
      // 077: aload 8
      // 079: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 07c: ifne 081
      // 07f: bipush 0
      // 080: istore 2
      // 081: aconst_null
      // 082: astore 9
      // 084: new java/lang/Object
      // 087: dup
      // 088: ldc_w "file:///"
      // 08b: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 08e: aload 8
      // 090: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 093: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 096: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 099: checkcast java/lang/Object
      // 09c: astore 9
      // 09e: aload 9
      // 0a0: invokeinterface javax/microedition/io/file/FileConnection.availableSize ()J 1
      // 0a5: lstore 3
      // 0a6: lload 3
      // 0a7: aload 0
      // 0a8: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes._size I
      // 0ab: i2l
      // 0ac: lcmp
      // 0ad: iflt 0dc
      // 0b0: ldc_w "store/"
      // 0b3: aload 8
      // 0b5: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0b8: ifeq 0c6
      // 0bb: aload 0
      // 0bc: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes._size I
      // 0bf: i2l
      // 0c0: lload 5
      // 0c2: lcmp
      // 0c3: ifgt 0dc
      // 0c6: bipush 1
      // 0c7: istore 1
      // 0c8: aload 9
      // 0ca: ifnull 122
      // 0cd: aload 9
      // 0cf: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0d4: goto 122
      // 0d7: astore 10
      // 0d9: goto 122
      // 0dc: aload 9
      // 0de: ifnonnull 0e4
      // 0e1: goto 057
      // 0e4: aload 9
      // 0e6: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0eb: goto 057
      // 0ee: astore 10
      // 0f0: goto 057
      // 0f3: astore 10
      // 0f5: aload 9
      // 0f7: ifnonnull 0fd
      // 0fa: goto 057
      // 0fd: aload 9
      // 0ff: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 104: goto 057
      // 107: astore 10
      // 109: goto 057
      // 10c: astore 11
      // 10e: aload 9
      // 110: ifnull 11f
      // 113: aload 9
      // 115: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 11a: goto 11f
      // 11d: astore 12
      // 11f: aload 11
      // 121: athrow
      // 122: iload 2
      // 123: ifeq 15e
      // 126: aload 0
      // 127: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes._size I
      // 12a: i2l
      // 12b: lload 5
      // 12d: lcmp
      // 12e: ifle 15e
      // 131: aload 0
      // 132: sipush 764
      // 135: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 138: bipush 1
      // 139: anewarray 528
      // 13c: dup
      // 13d: bipush 0
      // 13e: lload 5
      // 140: invokestatic java/lang/Long.toString (J)Ljava/lang/String;
      // 143: aastore
      // 144: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 147: lload 3
      // 148: aload 0
      // 149: getfield net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes._size I
      // 14c: i2l
      // 14d: lcmp
      // 14e: ifge 157
      // 151: sipush 901
      // 154: goto 15a
      // 157: sipush 952
      // 15a: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes.addError (Ljava/lang/String;I)V
      // 15d: return
      // 15e: iload 1
      // 15f: ifne 16f
      // 162: aload 0
      // 163: sipush 765
      // 166: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 169: sipush 901
      // 16c: invokevirtual net/rim/device/apps/internal/browser/dd/DownloadDescriptorAttributes.addError (Ljava/lang/String;I)V
      // 16f: return
      // try (95 -> 97): 98 null
      // try (103 -> 105): 106 null
      // try (62 -> 93): 108 null
      // try (112 -> 114): 115 null
      // try (62 -> 93): 117 null
      // try (108 -> 109): 117 null
      // try (120 -> 122): 123 null
      // try (117 -> 118): 117 null
   }

   private final void addErrorForMissingAttribute(String missingAttribute) {
      this.addError(MessageFormat.format(BrowserResources.getString(766), new Object[]{missingAttribute}), 906);
   }

   final void addError(String errorMessage, int errorCode) {
      if (this._statusCode == 900) {
         this._statusCode = errorCode;
      }

      if (this._errors == null) {
         this._errors = new Object[1];
         this._errors[0] = errorMessage;
      } else {
         Arrays.add(this._errors, errorMessage);
      }
   }

   public final int getStatusCode() {
      return this._statusCode;
   }

   private static final int getAttributeToken(String attribute) {
      if (attribute != null && attribute.length() >= 4) {
         switch (attribute.charAt(0)) {
            case 'D':
               if (attribute.equals("DDVersion")) {
                  return 3;
               }
               break;
            case 'd':
               if (attribute.equals("description")) {
                  return 5;
               }
               break;
            case 'i':
               if (attribute.equals("installNotifyURI")) {
                  return 6;
               }

               if (attribute.equals("infoURL")) {
                  return 8;
               }

               if (attribute.equals("iconURI")) {
                  return 9;
               }

               if (attribute.equals("installParam")) {
                  return 10;
               }
               break;
            case 'n':
               if (attribute.equals("name")) {
                  return 2;
               }

               if (attribute.equals("nextURL")) {
                  return 7;
               }
               break;
            case 'o':
               if (attribute.equals("objectURI")) {
                  return 0;
               }
               break;
            case 's':
               if (attribute.equals("size")) {
                  return 1;
               }
               break;
            case 't':
               if (attribute.equals("type")) {
                  return 1000;
               }
               break;
            case 'v':
               if (attribute.equals("vendor")) {
                  return 4;
               }
         }
      }

      return -1;
   }

   private static final boolean isTypeSupported(String[] supportedTypes, String type) {
      if (supportedTypes != null && type != null) {
         for (int i = 0; i < supportedTypes.length; i++) {
            if (type.equalsIgnoreCase(RendererControl.stripContentTypeParameters(supportedTypes[i]))) {
               return true;
            }
         }
      }

      return false;
   }

   private final void validate(int attributeToken, String attribute, String value) {
      switch (attributeToken) {
         case 0:
         case 6:
         case 7:
         case 8:
         case 9:
            this.validateURI(attribute, value);
            return;
         case 1:
            this._size = this.validatePositiveInteger(attribute, value);
            return;
         case 2:
         case 4:
            this.validateShortString(attribute, value);
            return;
         case 3:
            this.validateDDVersion(attribute, value);
            return;
         case 5:
            this.validateLongString(attribute, value);
            return;
         case 10:
            this.validateVeryLongString(attribute, value);
            return;
         case 1000:
            if (this.validateShortString(attribute, value)) {
               this.validateTypeSyntax(value);
               return;
            }

            return;
         default:
            throw new Object();
      }
   }

   private final boolean validateDDVersion(String attribute, String value) {
      if (!this.validateShortString(attribute, value)) {
         return false;
      }

      int decimal = value.indexOf(46);
      if (decimal > 0) {
         label39:
         try {
            int major = Integer.parseInt(value.substring(0, decimal));
            int minor = Integer.parseInt(value.substring(decimal + 1));
            if (major >= 0 && minor >= 0) {
               if (major > 1) {
                  this.addError(BrowserResources.getString(767), 951);
                  return false;
               }

               return true;
            }
         } finally {
            break label39;
         }
      }

      this.addError(MessageFormat.format(BrowserResources.getString(768), new Object[]{attribute}), 906);
      return false;
   }

   private final boolean validateMaxLength(String attribute, String value, int maxLength) {
      if (value.length() > maxLength) {
         this.addError(MessageFormat.format(BrowserResources.getString(770), new Object[]{attribute, Integer.toString(maxLength)}), 906);
         return false;
      } else {
         return true;
      }
   }

   private final boolean validateShortString(String attribute, String value) {
      return this.validateMaxLength(attribute, value, 40);
   }

   private final boolean validateLongString(String attribute, String value) {
      return this.validateMaxLength(attribute, value, 160);
   }

   private final boolean validateVeryLongString(String attribute, String value) {
      return this.validateMaxLength(attribute, value, 255);
   }

   private final boolean validateURI(String attribute, String value) {
      return this.validateMaxLength(attribute, value, 512);
   }

   private final int validatePositiveInteger(String attribute, String value) {
      if (value.length() > 0 && value.charAt(0) == '+') {
         value = value.substring(1);
      }

      int intValue = -1;

      label33:
      try {
         intValue = Integer.parseInt(value);
      } finally {
         break label33;
      }

      if (intValue <= 0) {
         this.addError(MessageFormat.format(BrowserResources.getString(769), new Object[]{attribute}), 906);
         return -1;
      } else {
         return intValue;
      }
   }

   private final boolean validateTypes(String[] types) {
      boolean valid = true;
      String mainType = null;

      for (int i = 0; i < types.length; i++) {
         String type = MIMETypeAssociations.getNormalizedType(types[i]);
         boolean typeSupported = false;
         if (StringUtilities.startsWithIgnoreCase(type, "image/", 1701707776)) {
            if (EncodedImage.isMIMETypeSupported(StringUtilities.toLowerCase(type, 1701707776))) {
               typeSupported = true;
            }
         } else if (StringUtilities.startsWithIgnoreCase(type, "audio/", 1701707776)) {
            if (isTypeSupported(new MediaRenderingConverter().getSupportedMimeTypes(), type)) {
               typeSupported = true;
            }
         } else if (StringUtilities.startsWithIgnoreCase(type, "video/", 1701707776)
            && isTypeSupported(new MediaRenderingConverter().getSupportedMimeTypes(), type)) {
            typeSupported = true;
         }

         if (typeSupported) {
            if (mainType != null) {
               if (!mainType.equalsIgnoreCase(type)) {
                  this.addError(BrowserResources.getString(isVideoSupported() ? 823 : 771), 952);
                  valid = false;
               }
            } else {
               mainType = type;
            }
         }

         if (!typeSupported && "application/vnd.oma.drm.message".equalsIgnoreCase(type)) {
            typeSupported = true;
         }

         if (!typeSupported) {
            this.addError(MessageFormat.format(BrowserResources.getString(772), new Object[]{types[i]}), 952);
            valid = false;
         }
      }

      if (mainType == null && valid) {
         this.addError(BrowserResources.getString(isVideoSupported() ? 824 : 773), 952);
         valid = false;
      }

      return valid;
   }

   private static final boolean isVideoSupported() {
      return InternalServices.isSoftwareCapable(7);
   }

   private final boolean validateTypeSyntax(String mimeType) {
      boolean valid = true;
      int slashIndex = -1;
      int typeLength = mimeType.length();

      for (int i = 0; valid && i < typeLength; i++) {
         char ch = mimeType.charAt(i);
         switch (ch) {
            case ' ':
            case '"':
            case '(':
            case ')':
            case ',':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case '[':
            case '\\':
            case ']':
               valid = false;
               break;
            case '/':
               if (i == 0) {
                  valid = false;
               }

               if (slashIndex >= 0) {
                  valid = false;
               }

               slashIndex = i;
               break;
            default:
               if (ch <= 31 && ch >= 0 || ch == 127) {
                  valid = false;
               }
         }
      }

      valid = valid && slashIndex > 0 && slashIndex < typeLength - 1;
      if (!valid) {
         this.addError(MessageFormat.format(BrowserResources.getString(774), new Object[]{mimeType}), 906);
      }

      return valid;
   }
}
