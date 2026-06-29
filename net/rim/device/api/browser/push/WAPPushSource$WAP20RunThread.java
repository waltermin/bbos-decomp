package net.rim.device.api.browser.push;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Calendar;
import java.util.TimeZone;
import javax.microedition.io.StreamConnectionNotifier;
import net.rim.device.api.browser.util.UserAgent;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.HttpServerConnection;
import net.rim.device.api.io.http.TCPPushInputStream;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.http.HttpServerProtocolBase;
import net.rim.device.cldc.io.http.StatusLine;
import net.rim.device.internal.browser.wap.ClientID;

final class WAPPushSource$WAP20RunThread extends Thread {
   private int _numRetries;
   private boolean _ppgConnect;
   private SimpleDateFormat _dateFormat;
   private final WAPPushSource this$0;

   public WAPPushSource$WAP20RunThread(WAPPushSource _1, boolean ppgConnect) {
      this.this$0 = _1;
      this._ppgConnect = ppgConnect;
      this._dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
   }

   private final void removeOldNotifier(boolean tryClose) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         Object obj = ar.get(-477495422972569712L);
         if (!(obj instanceof IntHashtable)) {
            ar.put(-477495422972569712L, new IntHashtable(1));
         } else {
            IntHashtable table = (IntHashtable)obj;
            StreamConnectionNotifier source = (StreamConnectionNotifier)table.remove(this.this$0._port);
            if (tryClose && source != null) {
               try {
                  source.close();
               } finally {
                  return;
               }
            }
         }
      }
   }

   private final void runPPGConnect() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 2
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
      // 001: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 004: getfield net/rim/device/api/browser/push/WAPPushSource._ppgAddress Ljava/lang/String;
      // 007: ifnonnull 00d
      // 00a: goto 29c
      // 00d: ldc2_w -1133226195824034738
      // 010: ldc_w 1349542771
      // 013: bipush 5
      // 015: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 018: pop
      // 019: new java/lang/StringBuffer
      // 01c: dup
      // 01d: invokespecial java/lang/StringBuffer.<init> ()V
      // 020: aload 0
      // 021: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 024: getfield net/rim/device/api/browser/push/WAPPushSource._tlsVersion I
      // 027: ifeq 030
      // 02a: ldc_w "tls"
      // 02d: goto 033
      // 030: ldc_w "socket"
      // 033: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 036: ldc_w "://"
      // 039: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 03c: aload 0
      // 03d: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 040: getfield net/rim/device/api/browser/push/WAPPushSource._ppgAddress Ljava/lang/String;
      // 043: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 046: ldc_w ";DeviceSide=true"
      // 049: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 04c: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 04f: astore 1
      // 050: aload 0
      // 051: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 054: getfield net/rim/device/api/browser/push/PushSource._apn Ljava/lang/String;
      // 057: ifnull 079
      // 05a: new java/lang/StringBuffer
      // 05d: dup
      // 05e: invokespecial java/lang/StringBuffer.<init> ()V
      // 061: aload 1
      // 062: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 065: ldc_w ";Apn="
      // 068: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 06b: aload 0
      // 06c: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 06f: getfield net/rim/device/api/browser/push/PushSource._apn Ljava/lang/String;
      // 072: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 075: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 078: astore 1
      // 079: aload 0
      // 07a: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 07d: getfield net/rim/device/api/browser/push/PushSource._apnUsername Ljava/lang/String;
      // 080: ifnull 0a2
      // 083: new java/lang/StringBuffer
      // 086: dup
      // 087: invokespecial java/lang/StringBuffer.<init> ()V
      // 08a: aload 1
      // 08b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 08e: ldc_w ";TunnelAuthUsername="
      // 091: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 094: aload 0
      // 095: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 098: getfield net/rim/device/api/browser/push/PushSource._apnUsername Ljava/lang/String;
      // 09b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 09e: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0a1: astore 1
      // 0a2: aload 0
      // 0a3: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 0a6: getfield net/rim/device/api/browser/push/PushSource._apnPassword Ljava/lang/String;
      // 0a9: ifnull 0cb
      // 0ac: new java/lang/StringBuffer
      // 0af: dup
      // 0b0: invokespecial java/lang/StringBuffer.<init> ()V
      // 0b3: aload 1
      // 0b4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0b7: ldc_w ";TunnelAuthPassword="
      // 0ba: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0bd: aload 0
      // 0be: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 0c1: getfield net/rim/device/api/browser/push/PushSource._apnPassword Ljava/lang/String;
      // 0c4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0c7: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0ca: astore 1
      // 0cb: new net/rim/device/cldc/io/utility/URL
      // 0ce: dup
      // 0cf: aload 1
      // 0d0: invokespecial net/rim/device/cldc/io/utility/URL.<init> (Ljava/lang/String;)V
      // 0d3: astore 2
      // 0d4: goto 0f8
      // 0d7: astore 3
      // 0d8: ldc2_w -1133226195824034738
      // 0db: new java/lang/StringBuffer
      // 0de: dup
      // 0df: ldc_w "PPce\n"
      // 0e2: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0e5: aload 3
      // 0e6: invokevirtual net/rim/device/cldc/io/utility/MalformedURLException.toString ()Ljava/lang/String;
      // 0e9: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0ec: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0ef: invokevirtual java/lang/String.getBytes ()[B
      // 0f2: bipush 0
      // 0f3: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 0f6: pop
      // 0f7: return
      // 0f8: aconst_null
      // 0f9: astore 3
      // 0fa: aconst_null
      // 0fb: astore 4
      // 0fd: ldc2_w -1133226195824034738
      // 100: new java/lang/StringBuffer
      // 103: dup
      // 104: ldc_w "PGou "
      // 107: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 10a: aload 1
      // 10b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 10e: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 111: invokevirtual java/lang/String.getBytes ()[B
      // 114: bipush 5
      // 116: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 119: pop
      // 11a: aload 0
      // 11b: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 11e: aload 1
      // 11f: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 122: checkcast javax/microedition/io/SocketConnection
      // 125: putfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 128: aload 0
      // 129: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 12c: getfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 12f: ifnull 1aa
      // 132: aload 0
      // 133: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 136: getfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 139: invokeinterface javax/microedition/io/InputConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 13e: astore 3
      // 13f: aload 0
      // 140: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 143: getfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 146: invokeinterface javax/microedition/io/OutputConnection.openDataOutputStream ()Ljava/io/DataOutputStream; 1
      // 14b: astore 4
      // 14d: aload 0
      // 14e: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 151: getfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 154: invokeinterface javax/microedition/io/SocketConnection.getLocalPort ()I 1
      // 159: pop
      // 15a: goto 162
      // 15d: astore 5
      // 15f: goto 1b6
      // 162: new net/rim/device/cldc/io/http/HttpServerProtocolBase
      // 165: dup
      // 166: aload 2
      // 167: aconst_null
      // 168: aload 0
      // 169: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 16c: getfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 16f: aload 3
      // 170: aload 4
      // 172: invokespecial net/rim/device/cldc/io/http/HttpServerProtocolBase.<init> (Lnet/rim/device/cldc/io/utility/URL;Lnet/rim/device/cldc/io/http/HttpServerSocketConnectionBase;Ljavax/microedition/io/StreamConnection;Ljava/io/DataInputStream;Ljava/io/DataOutputStream;)V
      // 175: astore 5
      // 177: aload 0
      // 178: aload 5
      // 17a: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.processPush (Lnet/rim/device/api/io/http/HttpServerConnection;)V
      // 17d: aload 5
      // 17f: invokevirtual net/rim/device/cldc/io/http/HttpServerProtocolBase.close ()V
      // 182: goto 14d
      // 185: astore 6
      // 187: ldc2_w -1133226195824034738
      // 18a: new java/lang/StringBuffer
      // 18d: dup
      // 18e: ldc_w "PPce\n"
      // 191: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 194: aload 6
      // 196: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // 199: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 19c: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 19f: invokevirtual java/lang/String.getBytes ()[B
      // 1a2: bipush 0
      // 1a3: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 1a6: pop
      // 1a7: goto 14d
      // 1aa: ldc2_w -1133226195824034738
      // 1ad: ldc_w 1349411687
      // 1b0: bipush 5
      // 1b2: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 1b5: pop
      // 1b6: aload 3
      // 1b7: ifnull 1c3
      // 1ba: aload 3
      // 1bb: invokevirtual java/io/DataInputStream.close ()V
      // 1be: goto 1c3
      // 1c1: astore 5
      // 1c3: aload 4
      // 1c5: ifnull 1d2
      // 1c8: aload 4
      // 1ca: invokevirtual java/io/DataOutputStream.close ()V
      // 1cd: goto 1d2
      // 1d0: astore 5
      // 1d2: aload 0
      // 1d3: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1d6: getfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 1d9: ifnull 1ed
      // 1dc: aload 0
      // 1dd: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1e0: getfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 1e3: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1e8: goto 1ed
      // 1eb: astore 5
      // 1ed: aload 0
      // 1ee: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1f1: aconst_null
      // 1f2: putfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 1f5: return
      // 1f6: astore 5
      // 1f8: ldc2_w -1133226195824034738
      // 1fb: new java/lang/StringBuffer
      // 1fe: dup
      // 1ff: ldc_w "PPce\n"
      // 202: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 205: aload 5
      // 207: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // 20a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 20d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 210: invokevirtual java/lang/String.getBytes ()[B
      // 213: bipush 0
      // 214: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 217: pop
      // 218: aload 3
      // 219: ifnull 225
      // 21c: aload 3
      // 21d: invokevirtual java/io/DataInputStream.close ()V
      // 220: goto 225
      // 223: astore 5
      // 225: aload 4
      // 227: ifnull 234
      // 22a: aload 4
      // 22c: invokevirtual java/io/DataOutputStream.close ()V
      // 22f: goto 234
      // 232: astore 5
      // 234: aload 0
      // 235: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 238: getfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 23b: ifnull 24f
      // 23e: aload 0
      // 23f: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 242: getfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 245: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 24a: goto 24f
      // 24d: astore 5
      // 24f: aload 0
      // 250: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 253: aconst_null
      // 254: putfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 257: return
      // 258: astore 7
      // 25a: aload 3
      // 25b: ifnull 267
      // 25e: aload 3
      // 25f: invokevirtual java/io/DataInputStream.close ()V
      // 262: goto 267
      // 265: astore 8
      // 267: aload 4
      // 269: ifnull 276
      // 26c: aload 4
      // 26e: invokevirtual java/io/DataOutputStream.close ()V
      // 271: goto 276
      // 274: astore 8
      // 276: aload 0
      // 277: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 27a: getfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 27d: ifnull 291
      // 280: aload 0
      // 281: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 284: getfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 287: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 28c: goto 291
      // 28f: astore 8
      // 291: aload 0
      // 292: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 295: aconst_null
      // 296: putfield net/rim/device/api/browser/push/WAPPushSource._ppgConnection Ljavax/microedition/io/SocketConnection;
      // 299: aload 7
      // 29b: athrow
      // 29c: ldc2_w -1133226195824034738
      // 29f: ldc_w 1349412723
      // 2a2: bipush 5
      // 2a4: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 2a7: pop
      // 2a8: return
      // try (82 -> 87): 88 null
      // try (139 -> 144): 145 null
      // try (161 -> 163): 164 null
      // try (184 -> 188): 189 null
      // try (190 -> 194): 195 null
      // try (196 -> 204): 205 null
      // try (107 -> 184): 211 null
      // try (225 -> 229): 230 null
      // try (231 -> 235): 236 null
      // try (237 -> 245): 246 null
      // try (107 -> 184): 252 null
      // try (211 -> 225): 252 null
      // try (253 -> 257): 258 null
      // try (259 -> 263): 264 null
      // try (265 -> 273): 274 null
      // try (252 -> 253): 252 null
   }

   public final void resetRetries() {
      this._numRetries = 0;
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread._ppgConnect Z
      // 004: ifeq 00c
      // 007: aload 0
      // 008: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.runPPGConnect ()V
      // 00b: return
      // 00c: aload 0
      // 00d: bipush 1
      // 00e: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.removeOldNotifier (Z)V
      // 011: aload 0
      // 012: bipush 0
      // 013: putfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread._numRetries I
      // 016: bipush 0
      // 017: istore 1
      // 018: aload 0
      // 019: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 01c: getfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 01f: ifne 025
      // 022: goto 55c
      // 025: aload 0
      // 026: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread._numRetries I
      // 029: aload 0
      // 02a: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 02d: getfield net/rim/device/api/browser/push/WAPPushSource._retryCount I
      // 030: if_icmplt 060
      // 033: aload 0
      // 034: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 037: dup
      // 038: astore 2
      // 039: monitorenter
      // 03a: aload 0
      // 03b: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 03e: bipush 0
      // 03f: putfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 042: aload 2
      // 043: monitorexit
      // 044: goto 04c
      // 047: astore 3
      // 048: aload 2
      // 049: monitorexit
      // 04a: aload 3
      // 04b: athrow
      // 04c: aload 0
      // 04d: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 050: getfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 053: aload 0
      // 054: if_acmpne 05f
      // 057: aload 0
      // 058: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 05b: aconst_null
      // 05c: putfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 05f: return
      // 060: iload 1
      // 061: ifne 067
      // 064: goto 16e
      // 067: bipush 0
      // 068: istore 1
      // 069: aload 0
      // 06a: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread._numRetries I
      // 06d: ifgt 073
      // 070: goto 16e
      // 073: sipush 5000
      // 076: i2l
      // 077: lstore 2
      // 078: aload 0
      // 079: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 07c: getfield net/rim/device/api/browser/push/WAPPushSource._retryMode I
      // 07f: bipush 2
      // 081: iand
      // 082: ifeq 101
      // 085: aload 0
      // 086: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread._numRetries I
      // 089: tableswitch 51 0 8 115 51 59 67 75 83 91 99 107
      // 0bc: sipush 5000
      // 0bf: i2l
      // 0c0: lstore 2
      // 0c1: goto 101
      // 0c4: ldc_w 60000
      // 0c7: i2l
      // 0c8: lstore 2
      // 0c9: goto 101
      // 0cc: ldc_w 300000
      // 0cf: i2l
      // 0d0: lstore 2
      // 0d1: goto 101
      // 0d4: ldc_w 600000
      // 0d7: i2l
      // 0d8: lstore 2
      // 0d9: goto 101
      // 0dc: ldc_w 3600000
      // 0df: i2l
      // 0e0: lstore 2
      // 0e1: goto 101
      // 0e4: ldc_w 7200000
      // 0e7: i2l
      // 0e8: lstore 2
      // 0e9: goto 101
      // 0ec: ldc_w 21600000
      // 0ef: i2l
      // 0f0: lstore 2
      // 0f1: goto 101
      // 0f4: ldc_w 43200000
      // 0f7: i2l
      // 0f8: lstore 2
      // 0f9: goto 101
      // 0fc: ldc_w 86400000
      // 0ff: i2l
      // 100: lstore 2
      // 101: aload 0
      // 102: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 105: dup
      // 106: astore 4
      // 108: monitorenter
      // 109: ldc2_w -1133226195824034738
      // 10c: new java/lang/StringBuffer
      // 10f: dup
      // 110: ldc_w "BO "
      // 113: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 116: lload 2
      // 117: invokevirtual java/lang/StringBuffer.append (J)Ljava/lang/StringBuffer;
      // 11a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 11d: invokevirtual java/lang/String.getBytes ()[B
      // 120: bipush 5
      // 122: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 125: pop
      // 126: aload 0
      // 127: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 12a: lload 2
      // 12b: invokevirtual java/lang/Object.wait (J)V
      // 12e: goto 133
      // 131: astore 5
      // 133: ldc2_w -1133226195824034738
      // 136: ldc_w 1349804386
      // 139: bipush 5
      // 13b: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 13e: pop
      // 13f: aload 0
      // 140: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 143: getfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 146: ifne 160
      // 149: aload 4
      // 14b: monitorexit
      // 14c: aload 0
      // 14d: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 150: getfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 153: aload 0
      // 154: if_acmpne 15f
      // 157: aload 0
      // 158: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 15b: aconst_null
      // 15c: putfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 15f: return
      // 160: aload 4
      // 162: monitorexit
      // 163: goto 16e
      // 166: astore 6
      // 168: aload 4
      // 16a: monitorexit
      // 16b: aload 6
      // 16d: athrow
      // 16e: aload 0
      // 16f: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 172: dup
      // 173: astore 2
      // 174: monitorenter
      // 175: invokestatic net/rim/device/api/system/RadioInfo.getNetworkService ()I
      // 178: bipush 4
      // 17a: iand
      // 17b: ifne 1e8
      // 17e: ldc2_w -1133226195824034738
      // 181: ldc_w 1349810020
      // 184: bipush 5
      // 186: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 189: pop
      // 18a: aload 0
      // 18b: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 18e: invokevirtual java/lang/Object.wait ()V
      // 191: goto 195
      // 194: astore 3
      // 195: ldc2_w -1133226195824034738
      // 198: ldc_w 1349804407
      // 19b: bipush 5
      // 19d: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 1a0: pop
      // 1a1: aload 0
      // 1a2: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1a5: getfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 1a8: ifne 175
      // 1ab: aload 2
      // 1ac: monitorexit
      // 1ad: aload 0
      // 1ae: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1b1: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 1b4: ifnull 1d4
      // 1b7: aload 0
      // 1b8: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1bb: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 1be: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1c3: goto 1c7
      // 1c6: astore 3
      // 1c7: aload 0
      // 1c8: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1cb: aconst_null
      // 1cc: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 1cf: aload 0
      // 1d0: bipush 0
      // 1d1: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.removeOldNotifier (Z)V
      // 1d4: aload 0
      // 1d5: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1d8: getfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 1db: aload 0
      // 1dc: if_acmpne 1e7
      // 1df: aload 0
      // 1e0: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1e3: aconst_null
      // 1e4: putfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 1e7: return
      // 1e8: aload 2
      // 1e9: monitorexit
      // 1ea: goto 1f4
      // 1ed: astore 7
      // 1ef: aload 2
      // 1f0: monitorexit
      // 1f1: aload 7
      // 1f3: athrow
      // 1f4: aload 0
      // 1f5: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 1f8: getfield net/rim/device/api/browser/push/WAPPushSource._tlsVersion I
      // 1fb: ifeq 204
      // 1fe: ldc_w "https"
      // 201: goto 207
      // 204: ldc_w "http"
      // 207: astore 2
      // 208: aload 0
      // 209: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread._numRetries I
      // 20c: ldc_w 2147483646
      // 20f: if_icmpeq 21c
      // 212: aload 0
      // 213: aload 0
      // 214: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread._numRetries I
      // 217: bipush 1
      // 218: iadd
      // 219: putfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread._numRetries I
      // 21c: new java/lang/StringBuffer
      // 21f: dup
      // 220: invokespecial java/lang/StringBuffer.<init> ()V
      // 223: aload 2
      // 224: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 227: ldc_w "://:"
      // 22a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 22d: aload 0
      // 22e: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 231: getfield net/rim/device/api/browser/push/PushSource._port I
      // 234: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 237: ldc_w ";DeviceSide=true"
      // 23a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 23d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 240: astore 3
      // 241: aload 0
      // 242: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 245: getfield net/rim/device/api/browser/push/PushSource._apn Ljava/lang/String;
      // 248: ifnull 26a
      // 24b: new java/lang/StringBuffer
      // 24e: dup
      // 24f: invokespecial java/lang/StringBuffer.<init> ()V
      // 252: aload 3
      // 253: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 256: ldc_w ";Apn="
      // 259: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 25c: aload 0
      // 25d: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 260: getfield net/rim/device/api/browser/push/PushSource._apn Ljava/lang/String;
      // 263: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 266: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 269: astore 3
      // 26a: aload 0
      // 26b: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 26e: getfield net/rim/device/api/browser/push/PushSource._apnUsername Ljava/lang/String;
      // 271: ifnull 293
      // 274: new java/lang/StringBuffer
      // 277: dup
      // 278: invokespecial java/lang/StringBuffer.<init> ()V
      // 27b: aload 3
      // 27c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 27f: ldc_w ";TunnelAuthUsername="
      // 282: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 285: aload 0
      // 286: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 289: getfield net/rim/device/api/browser/push/PushSource._apnUsername Ljava/lang/String;
      // 28c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 28f: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 292: astore 3
      // 293: aload 0
      // 294: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 297: getfield net/rim/device/api/browser/push/PushSource._apnPassword Ljava/lang/String;
      // 29a: ifnull 2bc
      // 29d: new java/lang/StringBuffer
      // 2a0: dup
      // 2a1: invokespecial java/lang/StringBuffer.<init> ()V
      // 2a4: aload 3
      // 2a5: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2a8: ldc_w ";TunnelAuthPassword="
      // 2ab: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2ae: aload 0
      // 2af: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 2b2: getfield net/rim/device/api/browser/push/PushSource._apnPassword Ljava/lang/String;
      // 2b5: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2b8: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2bb: astore 3
      // 2bc: aload 0
      // 2bd: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 2c0: aload 3
      // 2c1: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 2c4: checkcast javax/microedition/io/ServerSocketConnection
      // 2c7: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 2ca: aload 0
      // 2cb: bipush 0
      // 2cc: putfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread._numRetries I
      // 2cf: aload 0
      // 2d0: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 2d3: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 2d6: ifnonnull 333
      // 2d9: aload 0
      // 2da: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 2dd: dup
      // 2de: astore 4
      // 2e0: monitorenter
      // 2e1: aload 0
      // 2e2: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 2e5: bipush 0
      // 2e6: putfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 2e9: aload 4
      // 2eb: monitorexit
      // 2ec: goto 2f7
      // 2ef: astore 8
      // 2f1: aload 4
      // 2f3: monitorexit
      // 2f4: aload 8
      // 2f6: athrow
      // 2f7: aload 0
      // 2f8: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 2fb: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 2fe: ifnull 31f
      // 301: aload 0
      // 302: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 305: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 308: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 30d: goto 312
      // 310: astore 4
      // 312: aload 0
      // 313: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 316: aconst_null
      // 317: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 31a: aload 0
      // 31b: bipush 0
      // 31c: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.removeOldNotifier (Z)V
      // 31f: aload 0
      // 320: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 323: getfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 326: aload 0
      // 327: if_acmpne 332
      // 32a: aload 0
      // 32b: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 32e: aconst_null
      // 32f: putfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 332: return
      // 333: aload 0
      // 334: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 337: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 33a: dup
      // 33b: instanceof net/rim/device/cldc/io/devicehttps/HttpsServerSocket
      // 33e: ifne 345
      // 341: pop
      // 342: goto 359
      // 345: checkcast net/rim/device/cldc/io/devicehttps/HttpsServerSocket
      // 348: aload 0
      // 349: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 34c: getfield net/rim/device/api/browser/push/WAPPushSource._certificate Ljava/lang/Object;
      // 34f: aload 0
      // 350: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 353: getfield net/rim/device/api/browser/push/WAPPushSource._privateKey Ljava/lang/Object;
      // 356: invokevirtual net/rim/device/cldc/io/devicehttps/HttpsServerSocket.setCertificateAndKey (Ljava/lang/Object;Ljava/lang/Object;)V
      // 359: invokestatic net/rim/device/api/system/ApplicationRegistry.getApplicationRegistry ()Lnet/rim/device/api/system/ApplicationRegistry;
      // 35c: astore 4
      // 35e: aload 4
      // 360: dup
      // 361: astore 5
      // 363: monitorenter
      // 364: aload 4
      // 366: ldc2_w -477495422972569712
      // 369: invokevirtual net/rim/device/api/system/ApplicationRegistry.get (J)Ljava/lang/Object;
      // 36c: astore 6
      // 36e: aload 6
      // 370: dup
      // 371: instanceof net/rim/device/api/util/IntHashtable
      // 374: ifne 37b
      // 377: pop
      // 378: goto 394
      // 37b: checkcast net/rim/device/api/util/IntHashtable
      // 37e: astore 7
      // 380: aload 7
      // 382: aload 0
      // 383: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 386: getfield net/rim/device/api/browser/push/PushSource._port I
      // 389: aload 0
      // 38a: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 38d: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 390: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 393: pop
      // 394: aload 5
      // 396: monitorexit
      // 397: goto 3a2
      // 39a: astore 9
      // 39c: aload 5
      // 39e: monitorexit
      // 39f: aload 9
      // 3a1: athrow
      // 3a2: bipush 1
      // 3a3: istore 5
      // 3a5: iload 5
      // 3a7: aload 0
      // 3a8: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 3ab: getfield net/rim/device/api/browser/push/WAPPushSource._threadPoolSize I
      // 3ae: if_icmpge 3c6
      // 3b1: new net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread$WAP20AcceptAndOpenThread
      // 3b4: dup
      // 3b5: aload 0
      // 3b6: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread$WAP20AcceptAndOpenThread.<init> (Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;)V
      // 3b9: astore 6
      // 3bb: aload 6
      // 3bd: invokevirtual java/lang/Thread.start ()V
      // 3c0: iinc 5 1
      // 3c3: goto 3a5
      // 3c6: aload 0
      // 3c7: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.doAcceptAndOpen ()V
      // 3ca: aload 0
      // 3cb: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 3ce: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 3d1: ifnonnull 3d7
      // 3d4: goto 018
      // 3d7: aload 0
      // 3d8: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 3db: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 3de: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3e3: goto 3e7
      // 3e6: astore 2
      // 3e7: aload 0
      // 3e8: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 3eb: aconst_null
      // 3ec: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 3ef: aload 0
      // 3f0: bipush 0
      // 3f1: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.removeOldNotifier (Z)V
      // 3f4: goto 018
      // 3f7: astore 2
      // 3f8: aload 0
      // 3f9: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 3fc: dup
      // 3fd: astore 3
      // 3fe: monitorenter
      // 3ff: aload 0
      // 400: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 403: bipush 0
      // 404: putfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 407: aload 3
      // 408: monitorexit
      // 409: goto 413
      // 40c: astore 10
      // 40e: aload 3
      // 40f: monitorexit
      // 410: aload 10
      // 412: athrow
      // 413: aload 0
      // 414: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 417: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 41a: ifnull 43a
      // 41d: aload 0
      // 41e: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 421: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 424: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 429: goto 42d
      // 42c: astore 3
      // 42d: aload 0
      // 42e: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 431: aconst_null
      // 432: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 435: aload 0
      // 436: bipush 0
      // 437: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.removeOldNotifier (Z)V
      // 43a: aload 0
      // 43b: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 43e: getfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 441: aload 0
      // 442: if_acmpne 44d
      // 445: aload 0
      // 446: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 449: aconst_null
      // 44a: putfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 44d: return
      // 44e: astore 2
      // 44f: ldc2_w -1133226195824034738
      // 452: new java/lang/StringBuffer
      // 455: dup
      // 456: ldc_w "PPex\n"
      // 459: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 45c: aload 2
      // 45d: invokevirtual net/rim/device/cldc/io/ssl/TLSIOException.toString ()Ljava/lang/String;
      // 460: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 463: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 466: invokevirtual java/lang/String.getBytes ()[B
      // 469: bipush 0
      // 46a: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 46d: pop
      // 46e: aload 0
      // 46f: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 472: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 475: ifnonnull 47b
      // 478: goto 018
      // 47b: aload 0
      // 47c: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 47f: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 482: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 487: goto 48b
      // 48a: astore 2
      // 48b: aload 0
      // 48c: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 48f: aconst_null
      // 490: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 493: aload 0
      // 494: bipush 0
      // 495: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.removeOldNotifier (Z)V
      // 498: goto 018
      // 49b: astore 2
      // 49c: aload 0
      // 49d: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 4a0: getfield net/rim/device/api/browser/push/WAPPushSource._processing Z
      // 4a3: ifne 4e1
      // 4a6: aload 0
      // 4a7: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 4aa: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 4ad: ifnull 4cd
      // 4b0: aload 0
      // 4b1: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 4b4: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 4b7: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 4bc: goto 4c0
      // 4bf: astore 3
      // 4c0: aload 0
      // 4c1: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 4c4: aconst_null
      // 4c5: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 4c8: aload 0
      // 4c9: bipush 0
      // 4ca: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.removeOldNotifier (Z)V
      // 4cd: aload 0
      // 4ce: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 4d1: getfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 4d4: aload 0
      // 4d5: if_acmpne 4e0
      // 4d8: aload 0
      // 4d9: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 4dc: aconst_null
      // 4dd: putfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 4e0: return
      // 4e1: ldc2_w -1133226195824034738
      // 4e4: new java/lang/StringBuffer
      // 4e7: dup
      // 4e8: ldc_w "PPex\n"
      // 4eb: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 4ee: aload 2
      // 4ef: invokevirtual java/lang/Throwable.toString ()Ljava/lang/String;
      // 4f2: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 4f5: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 4f8: invokevirtual java/lang/String.getBytes ()[B
      // 4fb: bipush 0
      // 4fc: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 4ff: pop
      // 500: bipush 1
      // 501: istore 1
      // 502: aload 0
      // 503: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 506: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 509: ifnonnull 50f
      // 50c: goto 018
      // 50f: aload 0
      // 510: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 513: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 516: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 51b: goto 51f
      // 51e: astore 2
      // 51f: aload 0
      // 520: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 523: aconst_null
      // 524: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 527: aload 0
      // 528: bipush 0
      // 529: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.removeOldNotifier (Z)V
      // 52c: goto 018
      // 52f: astore 11
      // 531: aload 0
      // 532: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 535: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 538: ifnull 559
      // 53b: aload 0
      // 53c: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 53f: getfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 542: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 547: goto 54c
      // 54a: astore 12
      // 54c: aload 0
      // 54d: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 550: aconst_null
      // 551: putfield net/rim/device/api/browser/push/WAPPushSource._notifier Ljavax/microedition/io/ServerSocketConnection;
      // 554: aload 0
      // 555: bipush 0
      // 556: invokespecial net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.removeOldNotifier (Z)V
      // 559: aload 11
      // 55b: athrow
      // 55c: aload 0
      // 55d: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 560: getfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 563: aload 0
      // 564: if_acmpne 588
      // 567: aload 0
      // 568: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 56b: aconst_null
      // 56c: putfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 56f: return
      // 570: astore 13
      // 572: aload 0
      // 573: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 576: getfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 579: aload 0
      // 57a: if_acmpne 585
      // 57d: aload 0
      // 57e: getfield net/rim/device/api/browser/push/WAPPushSource$WAP20RunThread.this$0 Lnet/rim/device/api/browser/push/WAPPushSource;
      // 581: aconst_null
      // 582: putfield net/rim/device/api/browser/push/WAPPushSource._wap2Thread Lnet/rim/device/api/browser/push/WAPPushSource$WAP20RunThread;
      // 585: aload 13
      // 587: athrow
      // 588: return
      // try (30 -> 36): 37 null
      // try (37 -> 40): 37 null
      // try (125 -> 129): 130 null
      // try (113 -> 142): 155 null
      // try (152 -> 154): 155 null
      // try (155 -> 158): 155 null
      // try (174 -> 177): 178 null
      // try (194 -> 198): 199 null
      // try (165 -> 190): 220 null
      // try (217 -> 219): 220 null
      // try (220 -> 223): 220 null
      // try (327 -> 333): 334 null
      // try (334 -> 337): 334 null
      // try (343 -> 347): 348 null
      // try (388 -> 411): 412 null
      // try (412 -> 415): 412 null
      // try (440 -> 444): 445 null
      // try (160 -> 190): 454 null
      // try (217 -> 339): 454 null
      // try (366 -> 435): 454 null
      // try (460 -> 466): 467 null
      // try (467 -> 470): 467 null
      // try (476 -> 480): 481 null
      // try (160 -> 190): 499 null
      // try (217 -> 339): 499 null
      // try (366 -> 435): 499 null
      // try (518 -> 522): 523 null
      // try (160 -> 190): 532 null
      // try (217 -> 339): 532 null
      // try (366 -> 435): 532 null
      // try (541 -> 545): 546 null
      // try (584 -> 588): 589 null
      // try (160 -> 190): 598 null
      // try (217 -> 339): 598 null
      // try (366 -> 435): 598 null
      // try (454 -> 472): 598 null
      // try (499 -> 513): 598 null
      // try (532 -> 537): 598 null
      // try (564 -> 579): 598 null
      // try (603 -> 607): 608 null
      // try (598 -> 599): 598 null
      // try (14 -> 42): 628 null
      // try (52 -> 142): 628 null
      // try (152 -> 207): 628 null
      // try (217 -> 356): 628 null
      // try (366 -> 489): 628 null
      // try (499 -> 554): 628 null
      // try (564 -> 618): 628 null
      // try (628 -> 629): 628 null
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void doAcceptAndOpen() {
      while (this.this$0._processing) {
         HttpServerConnection connection = (HttpServerConnection)this.this$0._notifier.acceptAndOpen();
         this.processPush(connection);

         try {
            connection.close();
            HttpServerConnection var5 = null;
         } catch (Throwable var4) {
            EventLogger.logEvent(-1133226195824034738L, ("PPce\n" + e.toString()).getBytes(), 0);
            continue;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void processPush(HttpServerConnection connection) {
      DataInputStream in = null;
      boolean var75 = false /* VF: Semaphore variable */;

      label1129: {
         try {
            label1094:
            try {
               var75 = true;
               String e = UserAgent.getDefaultUserAgent();
               connection.setResponseProperty("Server", e);
               Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
               connection.setResponseProperty("Date", this._dateFormat.format(calendar));
               String method = connection.getRequestMethod();
               String url = connection.getRequestURI();
               if (EventLogger.getMinimumLevel() >= 5) {
                  System.out.println("Received push");
                  System.out.println(method + ' ' + url + ' ' + connection.getVersion());

                  for (int i = 0; i < Integer.MAX_VALUE; i++) {
                     String key = connection.getHeaderFieldKey(i);
                     if (key == null) {
                        break;
                     }

                     System.out.println(key + ": " + connection.getHeaderField(i));
                  }
               }

               if (!StringUtilities.strEqual(url, "/wappush")) {
                  if (StringUtilities.strEqual(url, "/automatedTestCaseUrl")) {
                     connection.setResponseCode(200);
                     connection.setResponseProperty("content-type", "text/html");
                     DataOutputStream out = connection.openDataOutputStream();
                     out.write("<html>Browser Group Rulez!!</html>".getBytes());
                     out.close();
                     var75 = false;
                  } else {
                     connection.setResponseCode(404);
                     var75 = false;
                  }
               } else {
                  connection.setResponseProperty("X-Wap-Push-OTA-Version", "1.0");
                  String receivedCPITag = connection.getHeaderField("X-Wap-CPITag");
                  String[] cpiValues = WAPPushSource.getCPIValues();
                  String calculatedCPITag = WAPPushSource.getCPIHash(cpiValues);
                  if (!StringUtilities.strEqual(method, "POST")) {
                     if (!StringUtilities.strEqual(method, "OPTIONS")) {
                        connection.setResponseCode(405);
                        connection.setResponseProperty("Allow", "POST, OPTIONS");
                        var75 = false;
                     } else {
                        connection.setResponseCode(204);
                        String indicationStr = "00";
                        if (RadioInfo.areWAFsSupported(8)) {
                           indicationStr = "12";
                        }

                        connection.setResponseProperty("X-Wap-Bearer-Indication", indicationStr);
                        connection.setResponseProperty("Cache-Control", "no-cache");
                        connection.setResponseProperty("X-Wap-Push-User-Agent", e);
                        String clientId = ClientID.getClientId(this.this$0._clientIdType, this.this$0._apn);
                        if (clientId != null) {
                           connection.setResponseProperty("X-Wap-Terminal-ID", clientId);
                        }

                        if (StringUtilities.strEqual(receivedCPITag, calculatedCPITag)) {
                           connection.setResponseProperty("X-Wap-Push-Status", Integer.toString(500));
                           var75 = false;
                        } else {
                           connection.setResponseProperty("X-Wap-CPITag", calculatedCPITag);
                           connection.setResponseProperty("X-Wap-Push-Status", Integer.toString(501));

                           for (int i = 0; i < WAPPushSource.CPI_VALUES.length; i++) {
                              if (cpiValues[i] != null) {
                                 connection.setResponseProperty(WAPPushSource.CPI_VALUES[i], cpiValues[i]);
                              }
                           }

                           var75 = false;
                        }
                     }
                  } else if (!StringUtilities.strEqual(receivedCPITag, calculatedCPITag)) {
                     connection.setResponseCode(204);
                     connection.setResponseProperty("X-Wap-CPITag", calculatedCPITag);
                     connection.setResponseProperty("X-Wap-Push-Status", Integer.toString(256));
                     var75 = false;
                  } else {
                     String contentType = connection.getHeaderField("content-type");
                     in = connection.openDataInputStream();
                     HttpHeaders headers = new HttpHeaders();
                     if (StringUtilities.strEqualIgnoreCase(contentType, "application/http", 1701707776)) {
                        StatusLine sl = new StatusLine();
                        sl.readFromStream(in);
                        headers.readFromStream(in);
                     } else {
                        String key = null;

                        for (int i = 0; (key = connection.getHeaderFieldKey(i)) != null; i++) {
                           headers.setProperty(key, connection.getHeaderField(i));
                        }
                     }

                     TCPPushInputStream pushIn = new TCPPushInputStream(connection, in);
                     pushIn.setDoWAPResponse(true);
                     this.this$0._listener.messageReceived(headers, pushIn);
                     var75 = false;
                  }
               }
               break label1129;
            } catch (Throwable var90) {
               EventLogger.logEvent(-1133226195824034738L, ("PPpp\n" + e.toString()).getBytes(), 0);
               var75 = false;
               break label1094;
            }
         } finally {
            if (var75) {
               if (connection instanceof HttpServerProtocolBase && EventLogger.getMinimumLevel() >= 5) {
                  label1022:
                  try {
                     HttpServerProtocolBase baseConnection = (HttpServerProtocolBase)connection;
                     System.out.println("Returning to server");

                     for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        String key = baseConnection.getResponsePropertyKey(i);
                        if (key == null) {
                           break;
                        }

                        System.out.println(key + ": " + baseConnection.getResponseProperty(i));
                     }
                  } finally {
                     break label1022;
                  }
               }

               if (in != null) {
                  label1011:
                  try {
                     in.close();
                  } finally {
                     break label1011;
                  }
               }
            }
         }

         if (connection instanceof HttpServerProtocolBase && EventLogger.getMinimumLevel() >= 5) {
            label1048:
            try {
               HttpServerProtocolBase baseConnection = (HttpServerProtocolBase)connection;
               System.out.println("Returning to server");

               for (int i = 0; i < Integer.MAX_VALUE; i++) {
                  String key = baseConnection.getResponsePropertyKey(i);
                  if (key == null) {
                     break;
                  }

                  System.out.println(key + ": " + baseConnection.getResponseProperty(i));
               }
            } finally {
               break label1048;
            }
         }

         if (in != null) {
            try {
               in.close();
               return;
            } finally {
               return;
            }
         }

         return;
      }

      if (connection instanceof HttpServerProtocolBase && EventLogger.getMinimumLevel() >= 5) {
         label1035:
         try {
            HttpServerProtocolBase baseConnection = (HttpServerProtocolBase)connection;
            System.out.println("Returning to server");

            for (int i = 0; i < Integer.MAX_VALUE; i++) {
               String key = baseConnection.getResponsePropertyKey(i);
               if (key == null) {
                  break;
               }

               System.out.println(key + ": " + baseConnection.getResponseProperty(i));
            }
         } finally {
            break label1035;
         }
      }

      if (in != null) {
         try {
            in.close();
         } finally {
            return;
         }
      }
   }
}
