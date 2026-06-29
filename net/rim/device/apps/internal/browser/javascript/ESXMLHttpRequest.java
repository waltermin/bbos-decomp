package net.rim.device.apps.internal.browser.javascript;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.Document;

final class ESXMLHttpRequest extends RedirectedObject implements JavaScriptResourceCallback {
   private String _method;
   private String _uriToRequest;
   private HttpHeaders _requestHeaders;
   private HttpHeaders _responseHeaders;
   private String _responseMessage;
   private int _responseCode;
   private RequestedResource _requestedResource;
   private int _state;
   private boolean _aSync;
   private JavaScriptEngine _engine = JavaScriptEngine.getInstance();
   private byte[] _responseData;
   private boolean _valueAsTextCreated;
   private String _valueAsText;
   private Document _xmlDoc;
   private boolean _xmlDocCreated;
   private static final int STATE_UNINITIALIZED = 0;
   private static final int STATE_OPEN = 1;
   private static final int STATE_SENT = 2;
   private static final int STATE_RECEIVING = 3;
   private static final int STATE_LOADED = 4;

   final synchronized void open(String method, String url, boolean aSync, String username, String pwd) {
      this.reset();
      this._method = method;
      this._uriToRequest = url;
      this._aSync = aSync;
      this._state = 1;
      this._responseCode = 0;
      this._responseMessage = null;
      this._responseHeaders = null;
   }

   final synchronized void send(String data) throws ThrownValue {
      if (this._state != 1) {
         throw ESDOMException.createThrownValue(11);
      }

      this._state = 2;
      byte[] requestData = null;

      label44:
      try {
         if (data != null) {
            requestData = data.getBytes("UTF-8");
         }
      } finally {
         break label44;
      }

      JavaScriptEngine engine = JavaScriptEngine.getInstance();
      Object doc = engine.getCurrentWindow()._esDocument._domDoc;
      BrowserContent content = engine._browserContent;
      String url = content.resolveUrl(this._uriToRequest);
      this._requestHeaders.setProperty("x-rim-transcode-content", "none");
      this._requestedResource = (RequestedResource)(new Object(url, this._requestHeaders, 0, this._method, requestData));
      if (doc instanceof JavaScriptResourceProvider) {
         JavaScriptResourceProvider rp = (JavaScriptResourceProvider)doc;
         rp.requestResourceFromJavascript(this._requestedResource, this._aSync ? this : null);
         if (!this._aSync) {
            this.resourceReady(this._requestedResource);
         }
      }
   }

   final synchronized String getAllResponseHeaders() throws ThrownValue {
      if (this._state >= 3 && this._responseHeaders != null) {
         try {
            ByteArrayOutputStream bytesOut = (ByteArrayOutputStream)(new Object());
            this._responseHeaders.writeToStream((DataOutputStream)(new Object(bytesOut)));
            bytesOut.close();
            return (String)(new Object(bytesOut.toByteArray()));
         } finally {
            throw ESDOMException.createThrownValue(11);
         }
      } else {
         return null;
      }
   }

   final synchronized void setRequestHeader(String header, String value) throws ThrownValue {
      if (this._state != 1) {
         throw ESDOMException.createThrownValue(11);
      }

      for (int i = header.length() - 1; i >= 0; i--) {
         switch (header.charAt(i)) {
            case '\n':
            case '\r':
            case ' ':
            case ':':
               return;
         }
      }

      for (int i = value.length() - 1; i >= 0; i--) {
         switch (value.charAt(i)) {
            case '\n':
            case '\r':
               return;
         }
      }

      switch (CharacterUtilities.toLowerCase(header.charAt(0))) {
         case 'a':
            if (StringUtilities.strEqualIgnoreCase("Accept-Charset", header, 1701707776)
               || StringUtilities.strEqualIgnoreCase("Accept-Encoding", header, 1701707776)) {
               return;
            }
            break;
         case 'd':
            if (StringUtilities.strEqualIgnoreCase("Date", header, 1701707776)) {
               return;
            }
            break;
         case 'e':
            if (StringUtilities.strEqualIgnoreCase("Expect", header, 1701707776)) {
               return;
            }
            break;
         case 'h':
            if (StringUtilities.strEqualIgnoreCase("Host", header, 1701707776)) {
               return;
            }
            break;
         case 'k':
            if (StringUtilities.strEqualIgnoreCase("Keep-Alive", header, 1701707776)) {
               return;
            }
            break;
         case 'r':
            if (StringUtilities.strEqualIgnoreCase("Referer", header, 1701707776)) {
               return;
            }
            break;
         case 't':
            if (StringUtilities.strEqualIgnoreCase("TE", header, 1701707776)) {
               return;
            }

            if (StringUtilities.strEqualIgnoreCase("Trailer", header, 1701707776)) {
               return;
            }

            if (StringUtilities.strEqualIgnoreCase("Transfer-Encoding", header, 1701707776)) {
               return;
            }
            break;
         case 'u':
            if (StringUtilities.strEqualIgnoreCase("Upgrade", header, 1701707776)) {
               return;
            }
      }

      this._requestHeaders.setProperty(header, value);
   }

   final synchronized void abort() {
      if (this._requestedResource != null) {
         Object doc = JavaScriptEngine.getInstance().getCurrentWindow()._esDocument._domDoc;
         if (doc instanceof JavaScriptResourceProvider) {
            JavaScriptResourceProvider rp = (JavaScriptResourceProvider)doc;
            rp.cancelResourceFromJavascript(this._requestedResource);
         }
      }

      this.reset();
   }

   final synchronized String getResponseHeader(String header) {
      return this._state >= 3 && this._responseHeaders != null ? this._responseHeaders.getPropertyValue(header) : "";
   }

   @Override
   public final synchronized void resourceReady(RequestedResource param1) {
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
      // 000: aload 1
      // 001: aload 0
      // 002: getfield net/rim/device/apps/internal/browser/javascript/ESXMLHttpRequest._requestedResource Lnet/rim/device/api/browser/field/RequestedResource;
      // 005: if_acmpeq 00b
      // 008: goto 11e
      // 00b: aload 0
      // 00c: aconst_null
      // 00d: putfield net/rim/device/apps/internal/browser/javascript/ESXMLHttpRequest._requestedResource Lnet/rim/device/api/browser/field/RequestedResource;
      // 010: aload 1
      // 011: invokevirtual net/rim/device/api/browser/field/RequestedResource.getHttpConnection ()Ljavax/microedition/io/HttpConnection;
      // 014: astore 2
      // 015: aconst_null
      // 016: astore 3
      // 017: aload 2
      // 018: ifnonnull 01e
      // 01b: goto 11e
      // 01e: aload 2
      // 01f: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 024: astore 3
      // 025: aload 0
      // 026: aload 2
      // 027: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 02c: putfield net/rim/device/apps/internal/browser/javascript/ESXMLHttpRequest._responseCode I
      // 02f: aload 0
      // 030: aload 2
      // 031: invokeinterface javax/microedition/io/HttpConnection.getResponseMessage ()Ljava/lang/String; 1
      // 036: putfield net/rim/device/apps/internal/browser/javascript/ESXMLHttpRequest._responseMessage Ljava/lang/String;
      // 039: aload 0
      // 03a: new java/lang/Object
      // 03d: dup
      // 03e: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 041: putfield net/rim/device/apps/internal/browser/javascript/ESXMLHttpRequest._responseHeaders Lnet/rim/device/api/io/http/HttpHeaders;
      // 044: new java/lang/Object
      // 047: dup
      // 048: invokespecial java/lang/StringBuffer.<init> ()V
      // 04b: astore 4
      // 04d: bipush 0
      // 04e: istore 5
      // 050: aload 2
      // 051: iload 5
      // 053: invokeinterface javax/microedition/io/HttpConnection.getHeaderFieldKey (I)Ljava/lang/String; 2
      // 058: astore 6
      // 05a: aload 6
      // 05c: ifnonnull 062
      // 05f: goto 079
      // 062: aload 0
      // 063: getfield net/rim/device/apps/internal/browser/javascript/ESXMLHttpRequest._responseHeaders Lnet/rim/device/api/io/http/HttpHeaders;
      // 066: aload 6
      // 068: aload 2
      // 069: iload 5
      // 06b: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (I)Ljava/lang/String; 2
      // 070: invokevirtual net/rim/device/api/io/http/HttpHeaders.addProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 073: iinc 5 1
      // 076: goto 050
      // 079: getstatic net/rim/ecmascript/runtime/Value.UNDEFINED J
      // 07c: lstore 5
      // 07e: aload 0
      // 07f: getstatic net/rim/device/apps/internal/browser/javascript/Names.onreadystatechange Ljava/lang/String;
      // 082: invokevirtual net/rim/ecmascript/runtime/RedirectedObject.getField (Ljava/lang/String;)J
      // 085: lstore 5
      // 087: goto 08c
      // 08a: astore 7
      // 08c: lload 5
      // 08e: invokestatic net/rim/ecmascript/runtime/Value.checkIfFunctionValue (J)Lnet/rim/ecmascript/runtime/ESFunction;
      // 091: astore 7
      // 093: aload 7
      // 095: ifnull 0ae
      // 098: aload 0
      // 099: bipush 3
      // 09b: putfield net/rim/device/apps/internal/browser/javascript/ESXMLHttpRequest._state I
      // 09e: aload 0
      // 09f: getfield net/rim/device/apps/internal/browser/javascript/ESXMLHttpRequest._engine Lnet/rim/device/apps/internal/browser/javascript/JavaScriptEngine;
      // 0a2: aload 0
      // 0a3: aload 7
      // 0a5: aconst_null
      // 0a6: bipush 0
      // 0a7: bipush -1
      // 0a9: i2l
      // 0aa: invokevirtual net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.executeMethodInternal (Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;ZJ)Z
      // 0ad: pop
      // 0ae: aload 0
      // 0af: bipush 4
      // 0b1: putfield net/rim/device/apps/internal/browser/javascript/ESXMLHttpRequest._state I
      // 0b4: aload 0
      // 0b5: aload 3
      // 0b6: invokestatic net/rim/device/api/io/IOUtilities.streamToBytes (Ljava/io/InputStream;)[B
      // 0b9: putfield net/rim/device/apps/internal/browser/javascript/ESXMLHttpRequest._responseData [B
      // 0bc: aload 7
      // 0be: ifnull 0d1
      // 0c1: aload 0
      // 0c2: getfield net/rim/device/apps/internal/browser/javascript/ESXMLHttpRequest._engine Lnet/rim/device/apps/internal/browser/javascript/JavaScriptEngine;
      // 0c5: aload 0
      // 0c6: aload 7
      // 0c8: aconst_null
      // 0c9: bipush 0
      // 0ca: bipush -1
      // 0cc: i2l
      // 0cd: invokevirtual net/rim/device/apps/internal/browser/javascript/JavaScriptEngine.executeMethodInternal (Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;ZJ)Z
      // 0d0: pop
      // 0d1: aload 3
      // 0d2: ifnull 0de
      // 0d5: aload 3
      // 0d6: invokevirtual java/io/InputStream.close ()V
      // 0d9: goto 0de
      // 0dc: astore 4
      // 0de: aload 2
      // 0df: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0e4: return
      // 0e5: astore 4
      // 0e7: return
      // 0e8: astore 4
      // 0ea: aload 3
      // 0eb: ifnull 0f7
      // 0ee: aload 3
      // 0ef: invokevirtual java/io/InputStream.close ()V
      // 0f2: goto 0f7
      // 0f5: astore 4
      // 0f7: aload 2
      // 0f8: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0fd: return
      // 0fe: astore 4
      // 100: return
      // 101: astore 8
      // 103: aload 3
      // 104: ifnull 110
      // 107: aload 3
      // 108: invokevirtual java/io/InputStream.close ()V
      // 10b: goto 110
      // 10e: astore 9
      // 110: aload 2
      // 111: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 116: goto 11b
      // 119: astore 9
      // 11b: aload 8
      // 11d: athrow
      // 11e: return
      // try (56 -> 60): 61 null
      // try (101 -> 103): 104 null
      // try (105 -> 107): 108 null
      // try (16 -> 99): 110 null
      // try (113 -> 115): 116 null
      // try (117 -> 119): 120 null
      // try (16 -> 99): 122 null
      // try (110 -> 111): 122 null
      // try (125 -> 127): 128 null
      // try (129 -> 131): 132 null
      // try (122 -> 123): 122 null
   }

   @Override
   public final synchronized long requestFieldValue(String name) throws ThrownValue {
      if (name == Names.readyState) {
         return Value.makeIntegerValue(this._state);
      }

      if (name == Names.responseText) {
         if (!this._valueAsTextCreated && this._responseData != null) {
            this._valueAsTextCreated = true;

            try {
               this._valueAsText = (String)(new Object(this._responseData, "UTF-8"));
            } finally {
               return JavaScriptEngine.makeStringValue(this._valueAsText);
            }
         }

         return JavaScriptEngine.makeStringValue(this._valueAsText);
      } else if (name != Names.responseXML) {
         if (name == Names.status) {
            if (this._state >= 3) {
               return Value.makeIntegerValue(this._responseCode);
            } else {
               throw ESDOMException.createThrownValue(11);
            }
         } else if (name == Names.statusText) {
            if (this._state >= 3) {
               return JavaScriptEngine.makeStringValue(this._responseMessage);
            } else {
               throw ESDOMException.createThrownValue(11);
            }
         } else {
            return Value.DEFAULT;
         }
      } else {
         if (!this._xmlDocCreated && this._responseData != null) {
            this._xmlDocCreated = true;
            String contentType = this._responseHeaders.getPropertyValue("Content-Type");
            if (StringUtilities.strEqualIgnoreCase("text/xml", contentType, 1701707776)
               || StringUtilities.strEqualIgnoreCase("application/xml", contentType, 1701707776)
               || StringUtilities.endsWithIgnoreCase("+xml", contentType, 1701707776)) {
               try {
                  this._xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse((InputStream)(new Object(this._responseData)));
               } finally {
                  return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this._xmlDoc);
               }
            }
         }

         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this._xmlDoc);
      }
   }

   private final synchronized void reset() {
      this._method = null;
      this._uriToRequest = null;
      this._aSync = true;
      this._requestHeaders = (HttpHeaders)(new Object());
      this._requestedResource = null;
      this._state = 0;
      this._valueAsTextCreated = false;
      this._valueAsText = null;
      this._responseData = null;
      this._xmlDoc = null;
      this._xmlDocCreated = false;
   }

   public ESXMLHttpRequest() {
      super(Names.XMLHttpRequest, JavaScriptEngine.getInstance()._xmlHttpRequestPrototype);
      this.reset();
   }
}
