package net.rim.device.internal.io.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.devicehttp.LengthControlledInputStream;
import net.rim.device.cldc.io.http.Utilities;

final class ReceiveThread extends Thread {
   private boolean _running;
   private Connection _conn;
   private DataInputStream _in;
   private USBOutputStream _usbOut;
   private DataOutputStream _out;
   private Object _writeLock;
   private Vector _updateRequests;
   private boolean _waitingForResponse;
   private StringMatch _stringMatch = new StringMatch(new String[]{"<", ">", "&"}, false, false);
   private static final int ERROR_OK = 200;
   private static final int ERROR_BAD_REQUEST = 400;
   private static final int ERROR_FILE_NOT_FOUND = 404;
   private static final int ERROR_GENERAL_ERROR = 500;
   private static final int ERROR_METHOD_NOT_IMPLEMENTED = 501;
   private static final int ERROR_SERVICE_UNAVAILABLE = 503;
   private static String DIRECTORY_START = "<directory>";
   private static String DIRECTORY_END = "</directory>";
   private static String FILE_START = "<file>";
   private static String FILE_END = "</file>";
   private static String NAME_START = "<name>";
   private static String NAME_END = "</name>";
   private static String SIZE_START = "<size>";
   private static String SIZE_END = "</size>";
   private static String LASTMOD_START = "<lastmod>";
   private static String LASTMOD_END = "</lastmod>";
   private static String TOTAL_SIZE_START = "<totalsize>";
   private static String TOTAL_SIZE_END = "</totalsize>";
   private static String USED_SIZE_START = "<usedsize>";
   private static String USED_SIZE_END = "</usedsize>";
   private static String MAX_FILE_SIZE_START = "<maxfilesize>";
   private static String MAX_FILE_SIZE_END = "</maxfilesize>";
   private static String ATTRIBUTES_START = "<attrib>";
   private static String ATTRIBUTES_END = "</attrib>";
   private static String CRLF = "\r\n";
   private static String DFTP_VERSION_10 = "DFTP/1.0 ";
   private static String DFTP = "DFTP";
   private static final int UPDATE_REQUEST_QUEUE_SIZE = 20;

   public ReceiveThread(Connection conn) {
      this._conn = conn;
      this._in = new DataInputStream(new USBInputStream(this._conn));
      this._usbOut = new USBOutputStream(this._conn);
      this._out = new DataOutputStream(this._usbOut);
      this._running = true;
      this._updateRequests = new Vector();
      this._writeLock = new Object();
   }

   public final void shutdown() {
      this._running = false;
      this._waitingForResponse = false;
      this._updateRequests.removeAllElements();

      label34:
      try {
         this._in.close();
      } finally {
         break label34;
      }

      try {
         this._out.close();
      } finally {
         return;
      }
   }

   public final void sendRequest(UpdateRequest request) {
      synchronized (this._updateRequests) {
         if (this._updateRequests.size() >= 20) {
            this._updateRequests.removeElementAt(0);
         }

         this._updateRequests.addElement(request);
         if (!this._waitingForResponse) {
            this.writeRequest((UpdateRequest)this._updateRequests.firstElement());
            this._updateRequests.removeElementAt(0);
            this._waitingForResponse = true;
         }
      }
   }

   final String xmlEncode(String name) {
      synchronized (this._stringMatch) {
         int patternIndex = this._stringMatch.indexOf(name);
         if (patternIndex == -1) {
            return name;
         }

         int oldPos = 0;
         int length = name.length();
         StringBuffer newStr = new StringBuffer(length);

         while (patternIndex >= 0) {
            StringUtilities.append(newStr, name, oldPos, patternIndex - oldPos);
            switch (this._stringMatch.getLastMatchedPattern()) {
               case -1:
                  break;
               case 0:
               default:
                  newStr.append("&lt;");
                  break;
               case 1:
                  newStr.append("&gt;");
                  break;
               case 2:
                  newStr.append("&amp;");
            }

            oldPos = ++patternIndex;
            if (patternIndex >= length) {
               break;
            }

            patternIndex = this._stringMatch.indexOf(name, patternIndex);
         }

         StringUtilities.append(newStr, name, oldPos, length - oldPos);
         return newStr.toString();
      }
   }

   private final void writeRequest(UpdateRequest request) {
      synchronized (this._writeLock) {
         this._out.write(("UPDATE " + request._uri + " DFTP/1.0\r\n").getBytes());
         request._headers.writeToStream(this._out);
         this._out.flush();
      }
   }

   private final void writeError(int responseCode, int additionalErrorCode) {
      HttpHeaders headers = new HttpHeaders();
      if (additionalErrorCode >= 0) {
         headers.setProperty("error-details", Integer.toString(additionalErrorCode));
      }

      synchronized (this._writeLock) {
         this._out.write((DFTP_VERSION_10 + Integer.toString(responseCode) + CRLF).getBytes());
         headers.writeToStream(this._out);
         this._out.flush();
      }
   }

   private final void writeResponse(int responseCode, HttpHeaders headers, String contentType, byte[] output) {
      headers.setProperty("content-length", Long.toString(output == null ? 0 : output.length));
      if (contentType != null) {
         headers.setProperty("content-type", contentType);
      }

      synchronized (this._writeLock) {
         this._out.write((DFTP_VERSION_10 + Integer.toString(responseCode) + CRLF).getBytes());
         headers.writeToStream(this._out);
         if (output != null) {
            this._out.write(output);
         }

         this._out.flush();
      }
   }

   private final void writeResponse(int responseCode, String contentType, byte[] output) {
      this.writeResponse(responseCode, new HttpHeaders(), contentType, output);
   }

   private final void writeResponse(int responseCode, long length, InputStream bytesIn) {
      this.writeResponse(responseCode, new HttpHeaders(), length, bytesIn);
   }

   private final void writeResponse(int responseCode, HttpHeaders headers, long length, InputStream bytesIn) {
      headers.setProperty("content-length", Long.toString(length));
      headers.setProperty("content-type", "application/octet-stream");
      synchronized (this._writeLock) {
         this._out.write((DFTP_VERSION_10 + Integer.toString(responseCode) + CRLF).getBytes());
         headers.writeToStream(this._out);
         if (bytesIn != null) {
            this._usbOut.copy(bytesIn, length);
         }

         this._out.flush();
      }
   }

   private final void handleList(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 2
      // 002: aload 1
      // 003: invokevirtual java/lang/String.length ()I
      // 006: ifeq 016
      // 009: aload 1
      // 00a: ldc_w "/"
      // 00d: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 010: ifne 016
      // 013: goto 14f
      // 016: new java/io/ByteArrayOutputStream
      // 019: dup
      // 01a: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 01d: astore 3
      // 01e: new java/io/OutputStreamWriter
      // 021: dup
      // 022: aload 3
      // 023: ldc_w "UTF-8"
      // 026: invokespecial java/io/OutputStreamWriter.<init> (Ljava/io/OutputStream;Ljava/lang/String;)V
      // 029: astore 4
      // 02b: aload 4
      // 02d: ldc_w "<list>\r\n"
      // 030: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 033: invokestatic javax/microedition/io/file/FileSystemRegistry.listRoots ()Ljava/util/Enumeration;
      // 036: astore 5
      // 038: aload 5
      // 03a: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 03f: ifne 045
      // 042: goto 131
      // 045: aload 5
      // 047: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 04c: checkcast java/lang/String
      // 04f: astore 6
      // 051: aload 4
      // 053: ldc_w "<root>"
      // 056: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 059: aload 4
      // 05b: getstatic net/rim/device/internal/io/file/ReceiveThread.NAME_START Ljava/lang/String;
      // 05e: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 061: aload 4
      // 063: aload 0
      // 064: aload 6
      // 066: invokevirtual net/rim/device/internal/io/file/ReceiveThread.xmlEncode (Ljava/lang/String;)Ljava/lang/String;
      // 069: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 06c: aload 4
      // 06e: getstatic net/rim/device/internal/io/file/ReceiveThread.NAME_END Ljava/lang/String;
      // 071: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 074: new java/lang/StringBuffer
      // 077: dup
      // 078: ldc_w "file:///"
      // 07b: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 07e: aload 6
      // 080: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 083: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 086: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 089: checkcast javax/microedition/io/file/FileConnection
      // 08c: astore 2
      // 08d: aload 2
      // 08e: ifnull 0d6
      // 091: aload 2
      // 092: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 097: ifeq 0d6
      // 09a: aload 4
      // 09c: getstatic net/rim/device/internal/io/file/ReceiveThread.TOTAL_SIZE_START Ljava/lang/String;
      // 09f: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 0a2: aload 4
      // 0a4: aload 2
      // 0a5: invokeinterface javax/microedition/io/file/FileConnection.totalSize ()J 1
      // 0aa: invokestatic java/lang/Long.toString (J)Ljava/lang/String;
      // 0ad: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 0b0: aload 4
      // 0b2: getstatic net/rim/device/internal/io/file/ReceiveThread.TOTAL_SIZE_END Ljava/lang/String;
      // 0b5: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 0b8: aload 4
      // 0ba: getstatic net/rim/device/internal/io/file/ReceiveThread.USED_SIZE_START Ljava/lang/String;
      // 0bd: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 0c0: aload 4
      // 0c2: aload 2
      // 0c3: invokeinterface javax/microedition/io/file/FileConnection.usedSize ()J 1
      // 0c8: invokestatic java/lang/Long.toString (J)Ljava/lang/String;
      // 0cb: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 0ce: aload 4
      // 0d0: getstatic net/rim/device/internal/io/file/ReceiveThread.USED_SIZE_END Ljava/lang/String;
      // 0d3: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 0d6: aload 2
      // 0d7: ifnull 100
      // 0da: aload 2
      // 0db: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0e0: goto 0e5
      // 0e3: astore 7
      // 0e5: aconst_null
      // 0e6: astore 2
      // 0e7: goto 100
      // 0ea: astore 8
      // 0ec: aload 2
      // 0ed: ifnull 0fd
      // 0f0: aload 2
      // 0f1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0f6: goto 0fb
      // 0f9: astore 9
      // 0fb: aconst_null
      // 0fc: astore 2
      // 0fd: aload 8
      // 0ff: athrow
      // 100: aload 6
      // 102: ldc_w "store/"
      // 105: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 108: ifeq 126
      // 10b: aload 4
      // 10d: getstatic net/rim/device/internal/io/file/ReceiveThread.MAX_FILE_SIZE_START Ljava/lang/String;
      // 110: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 113: aload 4
      // 115: invokestatic net/rim/device/internal/io/file/FileSystemOptions.getContentStoreMaxFileSize ()J
      // 118: invokestatic java/lang/Long.toString (J)Ljava/lang/String;
      // 11b: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 11e: aload 4
      // 120: getstatic net/rim/device/internal/io/file/ReceiveThread.MAX_FILE_SIZE_END Ljava/lang/String;
      // 123: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 126: aload 4
      // 128: ldc_w "</root>\r\n"
      // 12b: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 12e: goto 038
      // 131: aload 4
      // 133: ldc_w "</list>"
      // 136: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 139: aload 4
      // 13b: invokevirtual java/io/OutputStreamWriter.close ()V
      // 13e: aload 0
      // 13f: sipush 200
      // 142: ldc_w "text/xml"
      // 145: aload 3
      // 146: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 149: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeResponse (ILjava/lang/String;[B)V
      // 14c: goto 2e4
      // 14f: new java/lang/StringBuffer
      // 152: dup
      // 153: ldc_w "file://"
      // 156: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 159: aload 1
      // 15a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 15d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 160: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 163: checkcast javax/microedition/io/file/FileConnection
      // 166: astore 2
      // 167: aload 2
      // 168: ifnonnull 16e
      // 16b: goto 2db
      // 16e: aload 2
      // 16f: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 174: ifne 17a
      // 177: goto 2cf
      // 17a: aload 2
      // 17b: invokeinterface javax/microedition/io/file/FileConnection.isDirectory ()Z 1
      // 180: ifne 186
      // 183: goto 2c3
      // 186: new java/io/ByteArrayOutputStream
      // 189: dup
      // 18a: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 18d: astore 3
      // 18e: new java/io/OutputStreamWriter
      // 191: dup
      // 192: aload 3
      // 193: ldc_w "UTF-8"
      // 196: invokespecial java/io/OutputStreamWriter.<init> (Ljava/io/OutputStream;Ljava/lang/String;)V
      // 199: astore 4
      // 19b: aload 4
      // 19d: ldc_w "<list>\r\n"
      // 1a0: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 1a3: aload 4
      // 1a5: getstatic net/rim/device/internal/io/file/ReceiveThread.TOTAL_SIZE_START Ljava/lang/String;
      // 1a8: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 1ab: aload 4
      // 1ad: aload 2
      // 1ae: invokeinterface javax/microedition/io/file/FileConnection.totalSize ()J 1
      // 1b3: invokestatic java/lang/Long.toString (J)Ljava/lang/String;
      // 1b6: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 1b9: aload 4
      // 1bb: getstatic net/rim/device/internal/io/file/ReceiveThread.TOTAL_SIZE_END Ljava/lang/String;
      // 1be: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 1c1: aload 4
      // 1c3: getstatic net/rim/device/internal/io/file/ReceiveThread.USED_SIZE_START Ljava/lang/String;
      // 1c6: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 1c9: aload 4
      // 1cb: aload 2
      // 1cc: invokeinterface javax/microedition/io/file/FileConnection.usedSize ()J 1
      // 1d1: invokestatic java/lang/Long.toString (J)Ljava/lang/String;
      // 1d4: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 1d7: aload 4
      // 1d9: getstatic net/rim/device/internal/io/file/ReceiveThread.USED_SIZE_END Ljava/lang/String;
      // 1dc: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 1df: bipush 0
      // 1e0: istore 5
      // 1e2: aload 2
      // 1e3: dup
      // 1e4: instanceof net/rim/device/api/io/file/ExtendedFileConnection
      // 1e7: ifne 1ee
      // 1ea: pop
      // 1eb: goto 202
      // 1ee: checkcast net/rim/device/api/io/file/ExtendedFileConnection
      // 1f1: ldc_w "*"
      // 1f4: bipush 1
      // 1f5: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.listWithDetails (Ljava/lang/String;Z)Ljava/util/Enumeration; 3
      // 1fa: astore 6
      // 1fc: bipush 1
      // 1fd: istore 5
      // 1ff: goto 20a
      // 202: aload 2
      // 203: invokeinterface javax/microedition/io/file/FileConnection.list ()Ljava/util/Enumeration; 1
      // 208: astore 6
      // 20a: aload 6
      // 20c: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 211: ifne 217
      // 214: goto 2a5
      // 217: bipush -1
      // 219: istore 13
      // 21b: iload 5
      // 21d: ifeq 267
      // 220: aload 6
      // 222: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 227: checkcast net/rim/device/api/io/FileInfo
      // 22a: astore 17
      // 22c: aload 17
      // 22e: invokevirtual net/rim/device/api/io/FileInfo.getFileName ()Ljava/lang/String;
      // 231: astore 8
      // 233: aload 17
      // 235: invokevirtual net/rim/device/api/io/FileInfo.getAttributes ()I
      // 238: istore 13
      // 23a: iload 13
      // 23c: bipush 16
      // 23e: iand
      // 23f: ifeq 246
      // 242: bipush 1
      // 243: goto 247
      // 246: bipush 0
      // 247: istore 7
      // 249: iload 7
      // 24b: ifeq 256
      // 24e: bipush -1
      // 250: i2l
      // 251: lstore 9
      // 253: goto 25d
      // 256: aload 17
      // 258: invokevirtual net/rim/device/api/io/FileInfo.getFileSize ()J
      // 25b: lstore 9
      // 25d: aload 17
      // 25f: invokevirtual net/rim/device/api/io/FileInfo.getLastModified ()J
      // 262: lstore 11
      // 264: goto 28a
      // 267: aload 6
      // 269: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 26e: checkcast java/lang/String
      // 271: astore 8
      // 273: aload 8
      // 275: ldc_w "/"
      // 278: invokevirtual java/lang/String.endsWith (Ljava/lang/String;)Z
      // 27b: istore 7
      // 27d: bipush -1
      // 27f: i2l
      // 280: lstore 9
      // 282: bipush 0
      // 283: i2l
      // 284: lstore 11
      // 286: bipush -1
      // 288: istore 13
      // 28a: aload 0
      // 28b: aload 4
      // 28d: aload 8
      // 28f: iload 7
      // 291: lload 9
      // 293: lload 11
      // 295: iload 13
      // 297: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeFileDirEntry (Ljava/io/OutputStreamWriter;Ljava/lang/String;ZJJI)V
      // 29a: aload 4
      // 29c: getstatic net/rim/device/internal/io/file/ReceiveThread.CRLF Ljava/lang/String;
      // 29f: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 2a2: goto 20a
      // 2a5: aload 4
      // 2a7: ldc_w "</list>"
      // 2aa: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 2ad: aload 4
      // 2af: invokevirtual java/io/OutputStreamWriter.close ()V
      // 2b2: aload 0
      // 2b3: sipush 200
      // 2b6: ldc_w "text/xml"
      // 2b9: aload 3
      // 2ba: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 2bd: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeResponse (ILjava/lang/String;[B)V
      // 2c0: goto 2e4
      // 2c3: aload 0
      // 2c4: sipush 400
      // 2c7: bipush -1
      // 2c9: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 2cc: goto 2e4
      // 2cf: aload 0
      // 2d0: sipush 404
      // 2d3: bipush -1
      // 2d5: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 2d8: goto 2e4
      // 2db: aload 0
      // 2dc: sipush 404
      // 2df: bipush -1
      // 2e1: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 2e4: aload 2
      // 2e5: ifnull 343
      // 2e8: aload 2
      // 2e9: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2ee: return
      // 2ef: astore 3
      // 2f0: return
      // 2f1: astore 3
      // 2f2: aload 2
      // 2f3: ifnull 343
      // 2f6: aload 2
      // 2f7: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2fc: return
      // 2fd: astore 3
      // 2fe: return
      // 2ff: astore 3
      // 300: aload 0
      // 301: sipush 500
      // 304: aload 3
      // 305: invokevirtual net/rim/device/api/io/file/FileIOException.getErrorCode ()I
      // 308: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 30b: aload 2
      // 30c: ifnull 343
      // 30f: aload 2
      // 310: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 315: return
      // 316: astore 3
      // 317: return
      // 318: astore 3
      // 319: aload 0
      // 31a: sipush 500
      // 31d: bipush -1
      // 31f: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 322: aload 2
      // 323: ifnull 343
      // 326: aload 2
      // 327: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 32c: return
      // 32d: astore 3
      // 32e: return
      // 32f: astore 18
      // 331: aload 2
      // 332: ifnull 340
      // 335: aload 2
      // 336: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 33b: goto 340
      // 33e: astore 19
      // 340: aload 18
      // 342: athrow
      // 343: return
      // try (86 -> 88): 89 null
      // try (47 -> 84): 93 null
      // try (96 -> 98): 99 null
      // try (93 -> 94): 93 null
      // try (304 -> 306): 307 null
      // try (2 -> 302): 309 net/rim/device/internal/io/file/ConnectionShutdown
      // try (312 -> 314): 315 null
      // try (2 -> 302): 317 null
      // try (325 -> 327): 328 null
      // try (2 -> 302): 330 null
      // try (337 -> 339): 340 null
      // try (2 -> 302): 342 null
      // try (309 -> 310): 342 null
      // try (317 -> 323): 342 null
      // try (330 -> 335): 342 null
      // try (345 -> 347): 348 null
      // try (342 -> 343): 342 null
   }

   private final void writeFileDirEntry(OutputStreamWriter out, String name, boolean isDir, long size, long lastMod, int attributes) {
      out.write(isDir ? DIRECTORY_START : FILE_START);
      out.write(NAME_START);
      out.write(this.xmlEncode(name));
      out.write(NAME_END);
      if (size != -1) {
         out.write(SIZE_START);
         out.write(Long.toString(size));
         out.write(SIZE_END);
      }

      if (lastMod != 0) {
         out.write(LASTMOD_START);
         out.write(Long.toString(lastMod / 1000));
         out.write(LASTMOD_END);
      }

      if (attributes != -1) {
         out.write(ATTRIBUTES_START);
         if ((attributes & 8) != 0) {
            out.write(72);
         }

         if ((attributes & 2) != 0) {
            out.write(82);
         }

         out.write(ATTRIBUTES_END);
      }

      out.write(isDir ? DIRECTORY_END : FILE_END);
   }

   private final void handleGet(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 2
      // 002: aconst_null
      // 003: astore 3
      // 004: new java/lang/StringBuffer
      // 007: dup
      // 008: ldc_w "file://"
      // 00b: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 00e: aload 1
      // 00f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 012: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 015: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 018: checkcast javax/microedition/io/file/FileConnection
      // 01b: astore 2
      // 01c: aload 2
      // 01d: ifnonnull 023
      // 020: goto 0b3
      // 023: aload 2
      // 024: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 029: ifeq 0a7
      // 02c: aload 2
      // 02d: invokeinterface javax/microedition/io/file/FileConnection.isDirectory ()Z 1
      // 032: ifne 09b
      // 035: bipush 0
      // 036: i2l
      // 037: lstore 4
      // 039: new net/rim/device/api/io/http/HttpHeaders
      // 03c: dup
      // 03d: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 040: astore 6
      // 042: aload 2
      // 043: dup
      // 044: instanceof net/rim/device/api/io/file/ExtendedFileConnection
      // 047: ifne 04e
      // 04a: pop
      // 04b: goto 079
      // 04e: checkcast net/rim/device/api/io/file/ExtendedFileConnection
      // 051: astore 7
      // 053: aload 7
      // 055: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.isContentDRMForwardLocked ()Z 1
      // 05a: ifeq 079
      // 05d: aload 7
      // 05f: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.openRawInputStream ()Ljava/io/InputStream; 1
      // 064: astore 3
      // 065: aload 7
      // 067: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.rawFileSize ()J 1
      // 06c: lstore 4
      // 06e: aload 6
      // 070: ldc_w "DRM-Forward-Locked"
      // 073: ldc_w "true"
      // 076: invokevirtual net/rim/device/api/io/http/HttpHeaders.setProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 079: aload 3
      // 07a: ifnonnull 08c
      // 07d: aload 2
      // 07e: invokeinterface javax/microedition/io/file/FileConnection.openInputStream ()Ljava/io/InputStream; 1
      // 083: astore 3
      // 084: aload 2
      // 085: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 08a: lstore 4
      // 08c: aload 0
      // 08d: sipush 200
      // 090: aload 6
      // 092: lload 4
      // 094: aload 3
      // 095: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeResponse (ILnet/rim/device/api/io/http/HttpHeaders;JLjava/io/InputStream;)V
      // 098: goto 0bc
      // 09b: aload 0
      // 09c: sipush 400
      // 09f: bipush -1
      // 0a1: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 0a4: goto 0bc
      // 0a7: aload 0
      // 0a8: sipush 404
      // 0ab: bipush -1
      // 0ad: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 0b0: goto 0bc
      // 0b3: aload 0
      // 0b4: sipush 404
      // 0b7: bipush -1
      // 0b9: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 0bc: aload 3
      // 0bd: ifnull 0c9
      // 0c0: aload 3
      // 0c1: invokevirtual java/io/InputStream.close ()V
      // 0c4: goto 0c9
      // 0c7: astore 4
      // 0c9: aload 2
      // 0ca: ifnonnull 0d0
      // 0cd: goto 167
      // 0d0: aload 2
      // 0d1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0d6: return
      // 0d7: astore 4
      // 0d9: return
      // 0da: astore 4
      // 0dc: aload 3
      // 0dd: ifnull 0e9
      // 0e0: aload 3
      // 0e1: invokevirtual java/io/InputStream.close ()V
      // 0e4: goto 0e9
      // 0e7: astore 4
      // 0e9: aload 2
      // 0ea: ifnull 167
      // 0ed: aload 2
      // 0ee: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0f3: return
      // 0f4: astore 4
      // 0f6: return
      // 0f7: astore 4
      // 0f9: aload 0
      // 0fa: sipush 500
      // 0fd: aload 4
      // 0ff: invokevirtual net/rim/device/api/io/file/FileIOException.getErrorCode ()I
      // 102: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 105: aload 3
      // 106: ifnull 112
      // 109: aload 3
      // 10a: invokevirtual java/io/InputStream.close ()V
      // 10d: goto 112
      // 110: astore 4
      // 112: aload 2
      // 113: ifnull 167
      // 116: aload 2
      // 117: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 11c: return
      // 11d: astore 4
      // 11f: return
      // 120: astore 4
      // 122: aload 0
      // 123: sipush 500
      // 126: bipush -1
      // 128: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 12b: aload 3
      // 12c: ifnull 138
      // 12f: aload 3
      // 130: invokevirtual java/io/InputStream.close ()V
      // 133: goto 138
      // 136: astore 4
      // 138: aload 2
      // 139: ifnull 167
      // 13c: aload 2
      // 13d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 142: return
      // 143: astore 4
      // 145: return
      // 146: astore 8
      // 148: aload 3
      // 149: ifnull 155
      // 14c: aload 3
      // 14d: invokevirtual java/io/InputStream.close ()V
      // 150: goto 155
      // 153: astore 9
      // 155: aload 2
      // 156: ifnull 164
      // 159: aload 2
      // 15a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 15f: goto 164
      // 162: astore 9
      // 164: aload 8
      // 166: athrow
      // 167: return
      // try (82 -> 84): 85 null
      // try (89 -> 91): 92 null
      // try (4 -> 80): 94 net/rim/device/internal/io/file/ConnectionShutdown
      // try (97 -> 99): 100 null
      // try (103 -> 105): 106 null
      // try (4 -> 80): 108 null
      // try (116 -> 118): 119 null
      // try (122 -> 124): 125 null
      // try (4 -> 80): 127 null
      // try (134 -> 136): 137 null
      // try (140 -> 142): 143 null
      // try (4 -> 80): 145 null
      // try (94 -> 95): 145 null
      // try (108 -> 114): 145 null
      // try (127 -> 132): 145 null
      // try (148 -> 150): 151 null
      // try (154 -> 156): 157 null
      // try (145 -> 146): 145 null
   }

   private final void handleStat(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 2
      // 002: new java/lang/StringBuffer
      // 005: dup
      // 006: ldc_w "file://"
      // 009: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 00c: aload 1
      // 00d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 010: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 013: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 016: checkcast javax/microedition/io/file/FileConnection
      // 019: astore 2
      // 01a: aload 2
      // 01b: ifnonnull 021
      // 01e: goto 0d8
      // 021: aload 2
      // 022: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 027: ifne 02d
      // 02a: goto 0cc
      // 02d: new java/io/ByteArrayOutputStream
      // 030: dup
      // 031: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 034: astore 3
      // 035: new java/io/OutputStreamWriter
      // 038: dup
      // 039: aload 3
      // 03a: ldc_w "UTF-8"
      // 03d: invokespecial java/io/OutputStreamWriter.<init> (Ljava/io/OutputStream;Ljava/lang/String;)V
      // 040: astore 4
      // 042: aload 2
      // 043: invokeinterface javax/microedition/io/file/FileConnection.isDirectory ()Z 1
      // 048: istore 5
      // 04a: bipush -1
      // 04c: i2l
      // 04d: lstore 6
      // 04f: bipush -1
      // 051: istore 8
      // 053: aload 2
      // 054: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 059: lstore 9
      // 05b: iload 5
      // 05d: ifeq 06c
      // 060: aload 2
      // 061: bipush 0
      // 062: invokeinterface javax/microedition/io/file/FileConnection.directorySize (Z)J 2
      // 067: lstore 6
      // 069: goto 074
      // 06c: aload 2
      // 06d: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 072: lstore 6
      // 074: aload 2
      // 075: invokeinterface javax/microedition/io/file/FileConnection.canWrite ()Z 1
      // 07a: ifne 081
      // 07d: bipush 2
      // 07f: istore 8
      // 081: aload 2
      // 082: invokeinterface javax/microedition/io/file/FileConnection.isHidden ()Z 1
      // 087: ifeq 09f
      // 08a: iload 8
      // 08c: bipush -1
      // 08e: if_icmpne 098
      // 091: bipush 8
      // 093: istore 8
      // 095: goto 09f
      // 098: iload 8
      // 09a: bipush 8
      // 09c: ior
      // 09d: istore 8
      // 09f: aload 0
      // 0a0: aload 4
      // 0a2: aload 1
      // 0a3: iload 5
      // 0a5: lload 6
      // 0a7: lload 9
      // 0a9: iload 8
      // 0ab: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeFileDirEntry (Ljava/io/OutputStreamWriter;Ljava/lang/String;ZJJI)V
      // 0ae: aload 4
      // 0b0: getstatic net/rim/device/internal/io/file/ReceiveThread.CRLF Ljava/lang/String;
      // 0b3: invokevirtual java/io/Writer.write (Ljava/lang/String;)V
      // 0b6: aload 4
      // 0b8: invokevirtual java/io/OutputStreamWriter.close ()V
      // 0bb: aload 0
      // 0bc: sipush 200
      // 0bf: ldc_w "text/xml"
      // 0c2: aload 3
      // 0c3: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 0c6: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeResponse (ILjava/lang/String;[B)V
      // 0c9: goto 0e1
      // 0cc: aload 0
      // 0cd: sipush 404
      // 0d0: bipush -1
      // 0d2: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 0d5: goto 0e1
      // 0d8: aload 0
      // 0d9: sipush 404
      // 0dc: bipush -1
      // 0de: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 0e1: aload 2
      // 0e2: ifnull 140
      // 0e5: aload 2
      // 0e6: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0eb: return
      // 0ec: astore 3
      // 0ed: return
      // 0ee: astore 3
      // 0ef: aload 2
      // 0f0: ifnull 140
      // 0f3: aload 2
      // 0f4: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0f9: return
      // 0fa: astore 3
      // 0fb: return
      // 0fc: astore 3
      // 0fd: aload 0
      // 0fe: sipush 500
      // 101: aload 3
      // 102: invokevirtual net/rim/device/api/io/file/FileIOException.getErrorCode ()I
      // 105: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 108: aload 2
      // 109: ifnull 140
      // 10c: aload 2
      // 10d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 112: return
      // 113: astore 3
      // 114: return
      // 115: astore 3
      // 116: aload 0
      // 117: sipush 500
      // 11a: bipush -1
      // 11c: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 11f: aload 2
      // 120: ifnull 140
      // 123: aload 2
      // 124: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 129: return
      // 12a: astore 3
      // 12b: return
      // 12c: astore 11
      // 12e: aload 2
      // 12f: ifnull 13d
      // 132: aload 2
      // 133: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 138: goto 13d
      // 13b: astore 12
      // 13d: aload 11
      // 13f: athrow
      // 140: return
      // try (99 -> 101): 102 null
      // try (2 -> 97): 104 net/rim/device/internal/io/file/ConnectionShutdown
      // try (107 -> 109): 110 null
      // try (2 -> 97): 112 null
      // try (120 -> 122): 123 null
      // try (2 -> 97): 125 null
      // try (132 -> 134): 135 null
      // try (2 -> 97): 137 null
      // try (104 -> 105): 137 null
      // try (112 -> 118): 137 null
      // try (125 -> 130): 137 null
      // try (140 -> 142): 143 null
      // try (137 -> 138): 137 null
   }

   private final void handlePut(String param1, InputStream param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 3
      // 002: aconst_null
      // 003: astore 4
      // 005: bipush 0
      // 006: istore 5
      // 008: new java/lang/StringBuffer
      // 00b: dup
      // 00c: ldc_w "file://"
      // 00f: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 012: aload 1
      // 013: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 016: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 019: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 01c: checkcast javax/microedition/io/file/FileConnection
      // 01f: astore 3
      // 020: aload 3
      // 021: ifnonnull 027
      // 024: goto 0b7
      // 027: aload 3
      // 028: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 02d: ifeq 036
      // 030: aload 3
      // 031: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 036: aload 3
      // 037: invokeinterface javax/microedition/io/file/FileConnection.create ()V 1
      // 03c: aload 1
      // 03d: ldc_w "/store"
      // 040: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 043: ifeq 06e
      // 046: invokestatic net/rim/vm/Memory.fullGC ()V
      // 049: aload 2
      // 04a: dup
      // 04b: instanceof net/rim/device/cldc/io/devicehttp/LengthControlledInputStream
      // 04e: ifne 055
      // 051: pop
      // 052: goto 06e
      // 055: checkcast net/rim/device/cldc/io/devicehttp/LengthControlledInputStream
      // 058: invokevirtual net/rim/device/cldc/io/devicehttp/LengthControlledInputStream.getLength ()I
      // 05b: i2l
      // 05c: invokestatic net/rim/device/internal/io/file/FileSystemOptions.getContentStoreMaxFileSize ()J
      // 05f: lcmp
      // 060: ifle 06e
      // 063: new net/rim/device/api/io/file/FileIOException
      // 066: dup
      // 067: sipush 1008
      // 06a: invokespecial net/rim/device/api/io/file/FileIOException.<init> (I)V
      // 06d: athrow
      // 06e: aload 0
      // 06f: getfield net/rim/device/internal/io/file/ReceiveThread._conn Lnet/rim/device/internal/io/file/Connection;
      // 072: getfield net/rim/device/internal/io/file/Connection._readBufferSize I
      // 075: newarray 8
      // 077: astore 6
      // 079: aload 3
      // 07a: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 07f: astore 4
      // 081: aload 2
      // 082: aload 6
      // 084: invokevirtual java/io/InputStream.read ([B)I
      // 087: istore 7
      // 089: iload 7
      // 08b: bipush -1
      // 08d: if_icmpne 093
      // 090: goto 0a0
      // 093: aload 4
      // 095: aload 6
      // 097: bipush 0
      // 098: iload 7
      // 09a: invokevirtual java/io/OutputStream.write ([BII)V
      // 09d: goto 081
      // 0a0: aload 4
      // 0a2: invokevirtual java/io/OutputStream.close ()V
      // 0a5: aconst_null
      // 0a6: astore 4
      // 0a8: bipush 1
      // 0a9: istore 5
      // 0ab: aload 0
      // 0ac: sipush 200
      // 0af: aconst_null
      // 0b0: aconst_null
      // 0b1: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeResponse (ILjava/lang/String;[B)V
      // 0b4: goto 0c0
      // 0b7: aload 0
      // 0b8: sipush 404
      // 0bb: bipush -1
      // 0bd: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 0c0: aload 4
      // 0c2: ifnull 0cf
      // 0c5: aload 4
      // 0c7: invokevirtual java/io/OutputStream.close ()V
      // 0ca: goto 0cf
      // 0cd: astore 6
      // 0cf: aload 3
      // 0d0: ifnonnull 0d6
      // 0d3: goto 1c8
      // 0d6: iload 5
      // 0d8: ifne 0e6
      // 0db: aload 3
      // 0dc: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 0e1: goto 0e6
      // 0e4: astore 6
      // 0e6: aload 3
      // 0e7: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0ec: return
      // 0ed: astore 6
      // 0ef: return
      // 0f0: astore 6
      // 0f2: aload 4
      // 0f4: ifnull 101
      // 0f7: aload 4
      // 0f9: invokevirtual java/io/OutputStream.close ()V
      // 0fc: goto 101
      // 0ff: astore 6
      // 101: aload 3
      // 102: ifnonnull 108
      // 105: goto 1c8
      // 108: iload 5
      // 10a: ifne 118
      // 10d: aload 3
      // 10e: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 113: goto 118
      // 116: astore 6
      // 118: aload 3
      // 119: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 11e: return
      // 11f: astore 6
      // 121: return
      // 122: astore 6
      // 124: aload 0
      // 125: sipush 500
      // 128: aload 6
      // 12a: invokevirtual net/rim/device/api/io/file/FileIOException.getErrorCode ()I
      // 12d: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 130: aload 4
      // 132: ifnull 13f
      // 135: aload 4
      // 137: invokevirtual java/io/OutputStream.close ()V
      // 13a: goto 13f
      // 13d: astore 6
      // 13f: aload 3
      // 140: ifnull 1c8
      // 143: iload 5
      // 145: ifne 153
      // 148: aload 3
      // 149: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 14e: goto 153
      // 151: astore 6
      // 153: aload 3
      // 154: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 159: return
      // 15a: astore 6
      // 15c: return
      // 15d: astore 6
      // 15f: aload 0
      // 160: sipush 500
      // 163: bipush -1
      // 165: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 168: aload 4
      // 16a: ifnull 177
      // 16d: aload 4
      // 16f: invokevirtual java/io/OutputStream.close ()V
      // 172: goto 177
      // 175: astore 6
      // 177: aload 3
      // 178: ifnull 1c8
      // 17b: iload 5
      // 17d: ifne 18b
      // 180: aload 3
      // 181: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 186: goto 18b
      // 189: astore 6
      // 18b: aload 3
      // 18c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 191: return
      // 192: astore 6
      // 194: return
      // 195: astore 8
      // 197: aload 4
      // 199: ifnull 1a6
      // 19c: aload 4
      // 19e: invokevirtual java/io/OutputStream.close ()V
      // 1a1: goto 1a6
      // 1a4: astore 9
      // 1a6: aload 3
      // 1a7: ifnull 1c5
      // 1aa: iload 5
      // 1ac: ifne 1ba
      // 1af: aload 3
      // 1b0: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 1b5: goto 1ba
      // 1b8: astore 9
      // 1ba: aload 3
      // 1bb: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1c0: goto 1c5
      // 1c3: astore 9
      // 1c5: aload 8
      // 1c7: athrow
      // 1c8: return
      // try (88 -> 90): 91 null
      // try (97 -> 99): 100 null
      // try (101 -> 103): 104 null
      // try (6 -> 86): 106 net/rim/device/internal/io/file/ConnectionShutdown
      // try (109 -> 111): 112 null
      // try (118 -> 120): 121 null
      // try (122 -> 124): 125 null
      // try (6 -> 86): 127 null
      // try (135 -> 137): 138 null
      // try (143 -> 145): 146 null
      // try (147 -> 149): 150 null
      // try (6 -> 86): 152 null
      // try (159 -> 161): 162 null
      // try (167 -> 169): 170 null
      // try (171 -> 173): 174 null
      // try (6 -> 86): 176 null
      // try (106 -> 107): 176 null
      // try (127 -> 133): 176 null
      // try (152 -> 157): 176 null
      // try (179 -> 181): 182 null
      // try (187 -> 189): 190 null
      // try (191 -> 193): 194 null
      // try (176 -> 177): 176 null
   }

   private final void handleDelete(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 2
      // 02: new java/lang/StringBuffer
      // 05: dup
      // 06: ldc_w "file://"
      // 09: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0c: aload 1
      // 0d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 10: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 13: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 16: checkcast javax/microedition/io/file/FileConnection
      // 19: astore 2
      // 1a: aload 2
      // 1b: ifnull 45
      // 1e: aload 2
      // 1f: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 24: ifeq 39
      // 27: aload 2
      // 28: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 2d: aload 0
      // 2e: sipush 200
      // 31: aconst_null
      // 32: aconst_null
      // 33: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeResponse (ILjava/lang/String;[B)V
      // 36: goto 4e
      // 39: aload 0
      // 3a: sipush 404
      // 3d: bipush -1
      // 3f: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 42: goto 4e
      // 45: aload 0
      // 46: sipush 404
      // 49: bipush -1
      // 4b: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 4e: aload 2
      // 4f: ifnull ad
      // 52: aload 2
      // 53: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 58: return
      // 59: astore 3
      // 5a: return
      // 5b: astore 3
      // 5c: aload 2
      // 5d: ifnull ad
      // 60: aload 2
      // 61: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 66: return
      // 67: astore 3
      // 68: return
      // 69: astore 3
      // 6a: aload 0
      // 6b: sipush 500
      // 6e: aload 3
      // 6f: invokevirtual net/rim/device/api/io/file/FileIOException.getErrorCode ()I
      // 72: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 75: aload 2
      // 76: ifnull ad
      // 79: aload 2
      // 7a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 7f: return
      // 80: astore 3
      // 81: return
      // 82: astore 3
      // 83: aload 0
      // 84: sipush 500
      // 87: bipush -1
      // 89: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 8c: aload 2
      // 8d: ifnull ad
      // 90: aload 2
      // 91: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 96: return
      // 97: astore 3
      // 98: return
      // 99: astore 4
      // 9b: aload 2
      // 9c: ifnull aa
      // 9f: aload 2
      // a0: invokeinterface javax/microedition/io/Connection.close ()V 1
      // a5: goto aa
      // a8: astore 5
      // aa: aload 4
      // ac: athrow
      // ad: return
      // try (36 -> 38): 39 null
      // try (2 -> 34): 41 net/rim/device/internal/io/file/ConnectionShutdown
      // try (44 -> 46): 47 null
      // try (2 -> 34): 49 null
      // try (57 -> 59): 60 null
      // try (2 -> 34): 62 null
      // try (69 -> 71): 72 null
      // try (2 -> 34): 74 null
      // try (41 -> 42): 74 null
      // try (49 -> 55): 74 null
      // try (62 -> 67): 74 null
      // try (77 -> 79): 80 null
      // try (74 -> 75): 74 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void moveCopyDirectory(FileConnection oldDirectory, FileConnection newDirectory, boolean deleteOriginal) {
      if (deleteOriginal && StringUtilities.strEqualIgnoreCase(oldDirectory.getPath(), newDirectory.getPath(), 1701707776)) {
         String newName = newDirectory.getName();
         newName = newName.substring(0, newName.length() - 1);
         oldDirectory.rename(newName);
      } else {
         FileConnection nextNewFile = null;
         FileConnection nextOldFile = null;
         boolean var35 = false /* VF: Semaphore variable */;

         try {
            var35 = true;
            newDirectory.mkdir();
            Enumeration listing = oldDirectory.list();

            while (listing.hasMoreElements()) {
               String nextEntry = (String)listing.nextElement();
               nextOldFile = (FileConnection)Connector.open(oldDirectory.getURL() + nextEntry);
               nextNewFile = (FileConnection)Connector.open(newDirectory.getURL() + nextEntry);
               if (nextOldFile.isDirectory()) {
                  this.moveCopyDirectory(nextOldFile, nextNewFile, deleteOriginal);
               } else if (deleteOriginal) {
                  FileUtilities.moveFile(oldDirectory.getURL() + nextEntry, newDirectory.getURL() + nextEntry);
               } else {
                  FileUtilities.copyFile(oldDirectory.getURL() + nextEntry, newDirectory.getURL() + nextEntry);
               }

               nextOldFile.close();
               nextNewFile.close();
            }

            if (deleteOriginal) {
               oldDirectory.delete();
               var35 = false;
            } else {
               var35 = false;
            }
         } finally {
            if (var35) {
               if (nextOldFile != null) {
                  label257:
                  try {
                     nextOldFile.close();
                  } finally {
                     break label257;
                  }
               }

               if (nextNewFile != null) {
                  label253:
                  try {
                     nextNewFile.close();
                  } finally {
                     break label253;
                  }
               }
            }
         }

         if (nextOldFile != null) {
            label263:
            try {
               nextOldFile.close();
            } finally {
               break label263;
            }
         }

         if (nextNewFile != null) {
            try {
               nextNewFile.close();
            } finally {
               return;
            }
         }
      }
   }

   private final void handleMoveCopy(String param1, String param2, boolean param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 4
      // 003: aconst_null
      // 004: astore 5
      // 006: new java/lang/StringBuffer
      // 009: dup
      // 00a: ldc_w "file://"
      // 00d: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 010: aload 1
      // 011: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 014: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 017: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 01a: checkcast javax/microedition/io/file/FileConnection
      // 01d: astore 4
      // 01f: aload 4
      // 021: ifnonnull 027
      // 024: goto 116
      // 027: aload 4
      // 029: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 02e: ifeq 05b
      // 031: aload 0
      // 032: sipush 400
      // 035: bipush -1
      // 037: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 03a: aload 5
      // 03c: ifnull 04b
      // 03f: aload 5
      // 041: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 046: goto 04b
      // 049: astore 6
      // 04b: aload 4
      // 04d: ifnull 05a
      // 050: aload 4
      // 052: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 057: return
      // 058: astore 6
      // 05a: return
      // 05b: new java/lang/StringBuffer
      // 05e: dup
      // 05f: ldc_w "file://"
      // 062: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 065: aload 2
      // 066: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 069: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 06c: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 06f: checkcast javax/microedition/io/file/FileConnection
      // 072: astore 5
      // 074: aload 5
      // 076: ifnonnull 07c
      // 079: goto 116
      // 07c: aload 5
      // 07e: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 083: ifne 089
      // 086: goto 10d
      // 089: aload 5
      // 08b: invokeinterface javax/microedition/io/file/FileConnection.isDirectory ()Z 1
      // 090: ifne 0d5
      // 093: aload 4
      // 095: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 09a: invokestatic net/rim/device/internal/io/file/FileUtilities.isDirectory (Ljava/lang/String;)Z
      // 09d: ifne 0d5
      // 0a0: iload 3
      // 0a1: ifeq 0b8
      // 0a4: aload 5
      // 0a6: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 0ab: aload 4
      // 0ad: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 0b2: invokestatic net/rim/device/internal/io/file/FileUtilities.moveFile (Ljava/lang/String;Ljava/lang/String;)V
      // 0b5: goto 0c9
      // 0b8: aload 5
      // 0ba: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 0bf: aload 4
      // 0c1: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 0c6: invokestatic net/rim/device/internal/io/file/FileUtilities.copyFile (Ljava/lang/String;Ljava/lang/String;)V
      // 0c9: aload 0
      // 0ca: sipush 200
      // 0cd: aconst_null
      // 0ce: aconst_null
      // 0cf: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeResponse (ILjava/lang/String;[B)V
      // 0d2: goto 116
      // 0d5: aload 5
      // 0d7: invokeinterface javax/microedition/io/file/FileConnection.isDirectory ()Z 1
      // 0dc: ifeq 101
      // 0df: aload 4
      // 0e1: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 0e6: invokestatic net/rim/device/internal/io/file/FileUtilities.isDirectory (Ljava/lang/String;)Z
      // 0e9: ifeq 101
      // 0ec: aload 0
      // 0ed: aload 5
      // 0ef: aload 4
      // 0f1: iload 3
      // 0f2: invokespecial net/rim/device/internal/io/file/ReceiveThread.moveCopyDirectory (Ljavax/microedition/io/file/FileConnection;Ljavax/microedition/io/file/FileConnection;Z)V
      // 0f5: aload 0
      // 0f6: sipush 200
      // 0f9: aconst_null
      // 0fa: aconst_null
      // 0fb: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeResponse (ILjava/lang/String;[B)V
      // 0fe: goto 116
      // 101: aload 0
      // 102: sipush 400
      // 105: bipush -1
      // 107: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 10a: goto 116
      // 10d: aload 0
      // 10e: sipush 404
      // 111: bipush -1
      // 113: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 116: aload 5
      // 118: ifnull 127
      // 11b: aload 5
      // 11d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 122: goto 127
      // 125: astore 6
      // 127: aload 4
      // 129: ifnull 1b9
      // 12c: aload 4
      // 12e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 133: return
      // 134: astore 6
      // 136: return
      // 137: astore 6
      // 139: aload 0
      // 13a: sipush 500
      // 13d: aload 6
      // 13f: invokevirtual net/rim/device/api/io/file/FileIOException.getErrorCode ()I
      // 142: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 145: aload 5
      // 147: ifnull 156
      // 14a: aload 5
      // 14c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 151: goto 156
      // 154: astore 6
      // 156: aload 4
      // 158: ifnull 1b9
      // 15b: aload 4
      // 15d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 162: return
      // 163: astore 6
      // 165: return
      // 166: astore 6
      // 168: aload 0
      // 169: sipush 500
      // 16c: bipush -1
      // 16e: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 171: aload 5
      // 173: ifnull 182
      // 176: aload 5
      // 178: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 17d: goto 182
      // 180: astore 6
      // 182: aload 4
      // 184: ifnull 1b9
      // 187: aload 4
      // 189: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 18e: return
      // 18f: astore 6
      // 191: return
      // 192: astore 7
      // 194: aload 5
      // 196: ifnull 1a5
      // 199: aload 5
      // 19b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1a0: goto 1a5
      // 1a3: astore 8
      // 1a5: aload 4
      // 1a7: ifnull 1b6
      // 1aa: aload 4
      // 1ac: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1b1: goto 1b6
      // 1b4: astore 8
      // 1b6: aload 7
      // 1b8: athrow
      // 1b9: return
      // try (26 -> 28): 29 null
      // try (32 -> 34): 35 null
      // try (109 -> 111): 112 null
      // try (115 -> 117): 118 null
      // try (4 -> 24): 120 null
      // try (37 -> 107): 120 null
      // try (128 -> 130): 131 null
      // try (134 -> 136): 137 null
      // try (4 -> 24): 139 null
      // try (37 -> 107): 139 null
      // try (146 -> 148): 149 null
      // try (152 -> 154): 155 null
      // try (4 -> 24): 157 null
      // try (37 -> 107): 157 null
      // try (120 -> 126): 157 null
      // try (139 -> 144): 157 null
      // try (160 -> 162): 163 null
      // try (166 -> 168): 169 null
      // try (157 -> 158): 157 null
   }

   private final void handleMkdir(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 2
      // 02: new java/lang/StringBuffer
      // 05: dup
      // 06: ldc_w "file://"
      // 09: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0c: aload 1
      // 0d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 10: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 13: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 16: checkcast javax/microedition/io/file/FileConnection
      // 19: astore 2
      // 1a: aload 2
      // 1b: ifnull 48
      // 1e: aload 2
      // 1f: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 24: ifeq 33
      // 27: aload 0
      // 28: sipush 404
      // 2b: bipush -1
      // 2d: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 30: goto 42
      // 33: aload 2
      // 34: invokeinterface javax/microedition/io/file/FileConnection.mkdir ()V 1
      // 39: aload 0
      // 3a: sipush 200
      // 3d: aconst_null
      // 3e: aconst_null
      // 3f: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeResponse (ILjava/lang/String;[B)V
      // 42: aload 2
      // 43: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 48: aload 2
      // 49: ifnull a7
      // 4c: aload 2
      // 4d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 52: return
      // 53: astore 3
      // 54: return
      // 55: astore 3
      // 56: aload 2
      // 57: ifnull a7
      // 5a: aload 2
      // 5b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 60: return
      // 61: astore 3
      // 62: return
      // 63: astore 3
      // 64: aload 0
      // 65: sipush 500
      // 68: aload 3
      // 69: invokevirtual net/rim/device/api/io/file/FileIOException.getErrorCode ()I
      // 6c: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 6f: aload 2
      // 70: ifnull a7
      // 73: aload 2
      // 74: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 79: return
      // 7a: astore 3
      // 7b: return
      // 7c: astore 3
      // 7d: aload 0
      // 7e: sipush 500
      // 81: bipush -1
      // 83: invokespecial net/rim/device/internal/io/file/ReceiveThread.writeError (II)V
      // 86: aload 2
      // 87: ifnull a7
      // 8a: aload 2
      // 8b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 90: return
      // 91: astore 3
      // 92: return
      // 93: astore 4
      // 95: aload 2
      // 96: ifnull a4
      // 99: aload 2
      // 9a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 9f: goto a4
      // a2: astore 5
      // a4: aload 4
      // a6: athrow
      // a7: return
      // try (33 -> 35): 36 null
      // try (2 -> 31): 38 net/rim/device/internal/io/file/ConnectionShutdown
      // try (41 -> 43): 44 null
      // try (2 -> 31): 46 null
      // try (54 -> 56): 57 null
      // try (2 -> 31): 59 null
      // try (66 -> 68): 69 null
      // try (2 -> 31): 71 null
      // try (38 -> 39): 71 null
      // try (46 -> 52): 71 null
      // try (59 -> 64): 71 null
      // try (74 -> 76): 77 null
      // try (71 -> 72): 71 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void handleUsbMs(String uri, boolean enable) {
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         if (!"/SDCard".equals(uri)) {
            this.writeError(404, -1);
            return;
         }

         RootRegister rootRegister = RootRegister.getInstance();
         if (enable) {
            rootRegister.enableUSBMassStorageNoPrompt();
         } else {
            rootRegister.usbMSMediumChanged(0);
         }

         if (enable == rootRegister.isMassStorageActive()) {
            this.writeResponse(200, null, null);
            var5 = false;
         } else {
            this.writeError(400, -1);
            var5 = false;
         }
      } finally {
         if (var5) {
            this.writeError(500, -1);
            return;
         }
      }
   }

   private final void processResponse(String[] line) {
      synchronized (this._updateRequests) {
         this._waitingForResponse = false;
         if (this._updateRequests.size() > 0) {
            this.writeRequest((UpdateRequest)this._updateRequests.firstElement());
            this._updateRequests.removeElementAt(0);
            this._waitingForResponse = true;
         }
      }
   }

   private final void processRequest(String[] line, HttpHeaders headers, InputStream in) {
      String method = line[0];
      String uri = line[1];
      if ("LIST".equals(method)) {
         this.handleList(uri);
      } else if ("GET".equals(method)) {
         this.handleGet(uri);
      } else if ("PUT".equals(method)) {
         this.handlePut(uri, in);
      } else if ("STAT".equals(method)) {
         this.handleStat(uri);
      } else if ("DELETE".equals(method)) {
         this.handleDelete(uri);
      } else if ("MOVE".equals(method)) {
         this.handleMoveCopy(uri, headers.getPropertyValue("Original-filename"), true);
      } else if ("COPY".equals(method)) {
         this.handleMoveCopy(uri, headers.getPropertyValue("Original-filename"), false);
      } else if ("MKDIR".equals(method)) {
         this.handleMkdir(uri);
      } else if ("USBMSON".equals(method)) {
         this.handleUsbMs(uri, true);
      } else if ("USBMSOFF".equals(method)) {
         this.handleUsbMs(uri, false);
      } else {
         this.writeError(501, -1);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      while (true) {
         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;
            if (this._running) {
               String lineString = Utilities.receiveLine(this._in, true);
               String[] line = Utilities.processTransmissionLine(lineString);
               if (line != null && line.length == 3) {
                  HttpHeaders headers = new HttpHeaders(this._in);
                  String contentLengthStr = headers.getPropertyValue("content-length");
                  int contentLength = 0;
                  if (contentLengthStr != null) {
                     contentLength = Integer.parseInt(contentLengthStr);
                  }

                  LengthControlledInputStream in = null;
                  if (contentLength >= 0) {
                     in = new LengthControlledInputStream(this._in, contentLength, false, false);
                  }

                  if (line[0] != null && line[0].startsWith(DFTP)) {
                     if (!ITPolicy.getBoolean(24, 61, false)) {
                        this.processResponse(line);
                     }
                  } else {
                     if (line[2] == null || !line[2].startsWith(DFTP)) {
                        throw new IllegalStateException();
                     }

                     if (ITPolicy.getBoolean(24, 61, false)) {
                        this.writeError(503, -1);
                     } else {
                        this.processRequest(line, headers, in);
                     }
                  }

                  if (in != null) {
                     in.skip(Integer.MAX_VALUE);
                     in.close();
                  }
                  continue;
               }

               throw new IllegalStateException();
            }

            var9 = false;
         } catch (ConnectionShutdown var10) {
            return;
         } finally {
            if (var9) {
               this._running = false;
               this._conn.stop();
               return;
            }
         }

         return;
      }
   }
}
