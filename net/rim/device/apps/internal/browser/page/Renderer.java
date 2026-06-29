package net.rim.device.apps.internal.browser.page;

import com.sun.cldc.i18n.Helper;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.core.SecondaryURLNode;
import net.rim.device.apps.internal.browser.util.RendererControl;

public class Renderer {
   protected RenderingApplication _renderingApplication;
   protected RenderingOptions _renderingOptions;
   protected RenderingSession _renderingSession;
   protected InputConnection _inputConnection;
   protected String _contentType;
   protected String _encoding;
   protected String _postEncoding;
   protected int _flags;
   protected String _referrer;
   private static final String UTF_8 = "UTF-8";
   private static final String ISO_8859_1 = "ISO-8859-1";
   private static final String US_ASCII = "US-ASCII";

   public Renderer(InputConnection connection, RenderingSession renderingSession, RenderingApplication renderingApplication, String referrer, int flags) {
      if (connection != null && renderingSession != null) {
         this._inputConnection = connection;
         this._renderingApplication = renderingApplication;
         this._renderingSession = renderingSession;
         this._renderingOptions = this._renderingSession.getRenderingOptions();
         this._referrer = referrer;
         this._contentType = RendererControl.getContentType(this._inputConnection);
         this._encoding = RendererControl.getCharacterEncoding(this._inputConnection);
         this._flags = flags;
      } else {
         throw new Object("Connection and rendering options cannot be null");
      }
   }

   public void cleanup() {
      try {
         if (this._inputConnection != null) {
            this._inputConnection.close();
            return;
         }
      } finally {
         return;
      }
   }

   protected void setPostEncoding(String encoding) {
      if (encoding == null) {
         encoding = RendererControl.getCharacterEncoding(this._inputConnection);
         if (encoding == null) {
            encoding = this._encoding;
         }
      }

      this._postEncoding = this.validateEncoding(encoding);
   }

   protected void setEncoding(String encoding) {
      this.setEncoding(encoding, true);
   }

   protected void setEncoding(String encoding, boolean giveHttpHeaderPrecedence) {
      if (giveHttpHeaderPrecedence) {
         String httpCharacterEncoding = RendererControl.getCharacterEncoding(this._inputConnection);
         this._encoding = this.validateEncoding(httpCharacterEncoding != null ? httpCharacterEncoding : encoding);
      } else {
         this._encoding = this.validateEncoding(encoding);
      }
   }

   private String validateEncoding(String encoding) {
      if (StringUtilities.strEqualIgnoreCase(encoding, "US-ASCII", 1701707776)) {
         return "ISO-8859-1";
      }

      if (encoding == null) {
         return "UTF-8";
      }

      if (!Helper.isSupportedEncoding(encoding)) {
         return "UTF-8";
      }

      String test = null;
      switch (encoding.length()) {
         case 5:
            test = "UTF-8";
            break;
         case 8:
            test = "US-ASCII";
            break;
         case 10:
            test = "ISO-8859-1";
      }

      return StringUtilities.strEqualIgnoreCase(encoding, test, 1701707776) ? test : encoding;
   }

   protected EncodedImage findEncodedImageInCache(String param1, int param2, SecondaryURLNode param3, BrowserContent param4) {
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
      // 001: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 004: ifnonnull 009
      // 007: aconst_null
      // 008: areturn
      // 009: new java/lang/Object
      // 00c: dup
      // 00d: aload 1
      // 00e: aconst_null
      // 00f: iload 2
      // 010: invokespecial net/rim/device/api/browser/field/RequestedResource.<init> (Ljava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;I)V
      // 013: astore 5
      // 015: aload 5
      // 017: bipush 1
      // 018: invokevirtual net/rim/device/api/browser/field/RequestedResource.setCacheOnly (Z)V
      // 01b: aload 0
      // 01c: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 01f: dup
      // 020: instanceof java/lang/Object
      // 023: ifne 02a
      // 026: pop
      // 027: goto 03b
      // 02a: checkcast java/lang/Object
      // 02d: aload 5
      // 02f: aload 4
      // 031: invokeinterface net/rim/device/api/browser/field/ResourceProvider.getInputConnection (Lnet/rim/device/api/browser/field/RequestedResource;Lnet/rim/device/api/browser/field/BrowserContent;)Ljavax/microedition/io/InputConnection; 3
      // 036: astore 6
      // 038: goto 04a
      // 03b: aload 0
      // 03c: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 03f: aload 5
      // 041: aload 4
      // 043: invokeinterface net/rim/device/api/browser/field/RenderingApplication.getResource (Lnet/rim/device/api/browser/field/RequestedResource;Lnet/rim/device/api/browser/field/BrowserContent;)Ljavax/microedition/io/HttpConnection; 3
      // 048: astore 6
      // 04a: aconst_null
      // 04b: astore 7
      // 04d: aload 6
      // 04f: ifnonnull 055
      // 052: goto 1f5
      // 055: aload 6
      // 057: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentType (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 05a: astore 8
      // 05c: bipush 0
      // 05d: i2l
      // 05e: lstore 9
      // 060: aload 6
      // 062: dup
      // 063: instanceof java/lang/Object
      // 066: ifne 06d
      // 069: pop
      // 06a: goto 077
      // 06d: checkcast java/lang/Object
      // 070: invokeinterface javax/microedition/io/ContentConnection.getLength ()J 1
      // 075: lstore 9
      // 077: aload 6
      // 079: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 07e: astore 7
      // 080: aconst_null
      // 081: astore 11
      // 083: bipush 0
      // 084: istore 12
      // 086: lload 9
      // 088: bipush 0
      // 089: i2l
      // 08a: lcmp
      // 08b: ifle 0e7
      // 08e: aload 7
      // 090: instanceof java/lang/Object
      // 093: ifeq 0e7
      // 096: new java/lang/Object
      // 099: dup
      // 09a: invokespecial net/rim/device/internal/browser/util/PipePtr.<init> ()V
      // 09d: astore 13
      // 09f: aload 7
      // 0a1: checkcast java/lang/Object
      // 0a4: aload 13
      // 0a6: lload 9
      // 0a8: l2i
      // 0a9: invokeinterface net/rim/device/internal/browser/util/PipeInput.readByteArray (Lnet/rim/device/internal/browser/util/PipePtr;I)I 3
      // 0ae: i2l
      // 0af: lload 9
      // 0b1: lcmp
      // 0b2: ifeq 0d6
      // 0b5: aconst_null
      // 0b6: astore 14
      // 0b8: aload 7
      // 0ba: ifnull 0c7
      // 0bd: aload 7
      // 0bf: invokevirtual java/io/InputStream.close ()V
      // 0c2: goto 0c7
      // 0c5: astore 15
      // 0c7: aload 6
      // 0c9: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0ce: goto 0d3
      // 0d1: astore 15
      // 0d3: aload 14
      // 0d5: areturn
      // 0d6: aload 13
      // 0d8: invokevirtual net/rim/device/internal/browser/util/PipePtr.getData ()[B
      // 0db: astore 11
      // 0dd: aload 13
      // 0df: invokevirtual net/rim/device/internal/browser/util/PipePtr.getOffset ()I
      // 0e2: istore 12
      // 0e4: goto 11a
      // 0e7: aload 7
      // 0e9: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.readBytesFromInputStream (Ljava/io/InputStream;)[B
      // 0ec: astore 11
      // 0ee: aload 11
      // 0f0: ifnonnull 114
      // 0f3: aconst_null
      // 0f4: astore 13
      // 0f6: aload 7
      // 0f8: ifnull 105
      // 0fb: aload 7
      // 0fd: invokevirtual java/io/InputStream.close ()V
      // 100: goto 105
      // 103: astore 14
      // 105: aload 6
      // 107: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 10c: goto 111
      // 10f: astore 14
      // 111: aload 13
      // 113: areturn
      // 114: aload 11
      // 116: arraylength
      // 117: i2l
      // 118: lstore 9
      // 11a: aload 11
      // 11c: iload 12
      // 11e: lload 9
      // 120: l2i
      // 121: aload 8
      // 123: invokestatic net/rim/device/apps/internal/browser/util/ImageConverter.convert ([BIILjava/lang/String;)Lnet/rim/device/api/system/EncodedImage;
      // 126: astore 13
      // 128: aload 3
      // 129: ifnull 195
      // 12c: aload 3
      // 12d: invokeinterface net/rim/device/apps/internal/browser/core/SecondaryURLNode.getWidth ()I 1
      // 132: bipush -1
      // 134: if_icmpne 195
      // 137: aload 3
      // 138: invokeinterface net/rim/device/apps/internal/browser/core/SecondaryURLNode.getHeight ()I 1
      // 13d: bipush -1
      // 13f: if_icmpne 195
      // 142: aload 6
      // 144: ifnull 195
      // 147: aconst_null
      // 148: astore 14
      // 14a: aload 6
      // 14c: dup
      // 14d: instanceof java/lang/Object
      // 150: ifne 157
      // 153: pop
      // 154: goto 164
      // 157: checkcast java/lang/Object
      // 15a: ldc_w "x-rim-image-original-size"
      // 15d: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (Ljava/lang/String;)Ljava/lang/String; 2
      // 162: astore 14
      // 164: aload 14
      // 166: ifnull 195
      // 169: aload 14
      // 16b: bipush 44
      // 16d: invokevirtual java/lang/String.indexOf (I)I
      // 170: istore 15
      // 172: aload 3
      // 173: aload 14
      // 175: bipush 0
      // 176: iload 15
      // 178: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 17b: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 17e: invokeinterface net/rim/device/apps/internal/browser/core/SecondaryURLNode.setWidth (I)V 2
      // 183: aload 3
      // 184: aload 14
      // 186: iload 15
      // 188: bipush 1
      // 189: iadd
      // 18a: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 18d: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 190: invokeinterface net/rim/device/apps/internal/browser/core/SecondaryURLNode.setHeight (I)V 2
      // 195: aload 13
      // 197: astore 14
      // 199: aload 7
      // 19b: ifnull 1a8
      // 19e: aload 7
      // 1a0: invokevirtual java/io/InputStream.close ()V
      // 1a3: goto 1a8
      // 1a6: astore 15
      // 1a8: aload 6
      // 1aa: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1af: goto 1b4
      // 1b2: astore 15
      // 1b4: aload 14
      // 1b6: areturn
      // 1b7: astore 8
      // 1b9: aload 7
      // 1bb: ifnull 1c8
      // 1be: aload 7
      // 1c0: invokevirtual java/io/InputStream.close ()V
      // 1c3: goto 1c8
      // 1c6: astore 8
      // 1c8: aload 6
      // 1ca: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1cf: aconst_null
      // 1d0: areturn
      // 1d1: astore 8
      // 1d3: aconst_null
      // 1d4: areturn
      // 1d5: astore 16
      // 1d7: aload 7
      // 1d9: ifnull 1e6
      // 1dc: aload 7
      // 1de: invokevirtual java/io/InputStream.close ()V
      // 1e1: goto 1e6
      // 1e4: astore 17
      // 1e6: aload 6
      // 1e8: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1ed: goto 1f2
      // 1f0: astore 17
      // 1f2: aload 16
      // 1f4: athrow
      // 1f5: aconst_null
      // 1f6: areturn
      // try (87 -> 89): 90 null
      // try (91 -> 93): 94 null
      // try (113 -> 115): 116 null
      // try (117 -> 119): 120 null
      // try (183 -> 185): 186 null
      // try (187 -> 189): 190 null
      // try (39 -> 85): 193 null
      // try (97 -> 111): 193 null
      // try (123 -> 181): 193 null
      // try (196 -> 198): 199 null
      // try (200 -> 202): 204 null
      // try (39 -> 85): 207 null
      // try (97 -> 111): 207 null
      // try (123 -> 181): 207 null
      // try (193 -> 194): 207 null
      // try (210 -> 212): 213 null
      // try (214 -> 216): 217 null
      // try (207 -> 208): 207 null
   }

   public BrowserContent processData() {
      throw null;
   }

   public void finishProcessingData() {
      throw null;
   }
}
