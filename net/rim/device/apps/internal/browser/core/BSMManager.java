package net.rim.device.apps.internal.browser.core;

import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.options.BrowserConfigChangeListener;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.stack.CacheNode;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.device.apps.internal.browser.stack.RawDataCacheListener;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.device.apps.internal.browser.util.BSMUtil;

final class BSMManager implements RawDataCacheListener, BrowserConfigChangeListener {
   private Vector _ackEntries;
   private BSMThread _bsmThread;
   private String _specificUID;
   private StreamConnection _connectConnection;
   private InputStream _connectInputStream;
   private boolean _bsmEnabled;
   private boolean _activated;
   private DataBuffer[] _preparedConnectData = new Object[2];
   private boolean _bsmSupported = true;
   private BrowserSession _browserSession;
   private int _failedConnectAttemptsCount;
   private Object _activationLock = new Object();
   private static byte[] HDR_VALUE = " BSM/1.1\r\n Content-Length: ".getBytes();
   private static byte[] CRLF_CRLF = "\r\n\r\n".getBytes();
   private static int MAX_CONNECT_ATTEMPTS = 5;

   final void startup() {
      BrowserDaemonRegistry.addBrowserConfigChangeListener(this);
      this.activate();
   }

   public final String getBSMHost() {
      return this._specificUID;
   }

   final void sendAcks() {
      if (this.isConnected()) {
         DataBuffer buffer = null;
         synchronized (this) {
            if (this._ackEntries.size() != 0) {
               buffer = BSMUtil.getAddData(this._browserSession, this._ackEntries);
               this._ackEntries.removeAllElements();
               if (this._bsmThread != null) {
                  BSMRequest item = new BSMRequest(this, buffer);
                  this._bsmThread.addToQueue(item);
               }
            }
         }
      }
   }

   public final void connect() {
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
      // 001: getfield net/rim/device/apps/internal/browser/core/BSMManager._browserSession Lnet/rim/device/apps/internal/browser/core/BrowserSession;
      // 004: invokevirtual net/rim/device/apps/internal/browser/core/BrowserSession.getConfig ()Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 007: astore 1
      // 008: aconst_null
      // 009: astore 2
      // 00a: invokestatic java/lang/System.currentTimeMillis ()J
      // 00d: lstore 3
      // 00e: bipush 0
      // 00f: istore 5
      // 011: aload 0
      // 012: aload 0
      // 013: astore 6
      // 015: monitorenter
      // 016: aload 0
      // 017: getfield net/rim/device/apps/internal/browser/core/BSMManager._bsmEnabled Z
      // 01a: ifeq 036
      // 01d: aload 0
      // 01e: getfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 021: ifnonnull 036
      // 024: aload 0
      // 025: getfield net/rim/device/apps/internal/browser/core/BSMManager._bsmThread Lnet/rim/device/apps/internal/browser/core/BSMThread;
      // 028: ifnonnull 036
      // 02b: aload 0
      // 02c: getfield net/rim/device/apps/internal/browser/core/BSMManager._bsmSupported Z
      // 02f: ifeq 036
      // 032: aload 1
      // 033: ifnonnull 03a
      // 036: aload 6
      // 038: monitorexit
      // 039: return
      // 03a: aload 0
      // 03b: aconst_null
      // 03c: putfield net/rim/device/apps/internal/browser/core/BSMManager._specificUID Ljava/lang/String;
      // 03f: aload 0
      // 040: invokespecial net/rim/device/apps/internal/browser/core/BSMManager.openConnection ()Ljavax/microedition/io/StreamConnection;
      // 043: astore 2
      // 044: aload 2
      // 045: ifnonnull 04c
      // 048: aload 6
      // 04a: monitorexit
      // 04b: return
      // 04c: aload 0
      // 04d: aload 2
      // 04e: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 051: aload 0
      // 052: new net/rim/device/apps/internal/browser/core/BSMThread
      // 055: dup
      // 056: invokespecial net/rim/device/apps/internal/browser/core/BSMThread.<init> ()V
      // 059: putfield net/rim/device/apps/internal/browser/core/BSMManager._bsmThread Lnet/rim/device/apps/internal/browser/core/BSMThread;
      // 05c: aload 6
      // 05e: monitorexit
      // 05f: goto 06a
      // 062: astore 7
      // 064: aload 6
      // 066: monitorexit
      // 067: aload 7
      // 069: athrow
      // 06a: bipush 0
      // 06b: istore 6
      // 06d: aload 2
      // 06e: invokeinterface javax/microedition/io/OutputConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 073: checkcast java/lang/Object
      // 076: astore 7
      // 078: aload 7
      // 07a: bipush 0
      // 07b: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.setAutoFlushMode (Z)V
      // 07e: aload 0
      // 07f: getfield net/rim/device/apps/internal/browser/core/BSMManager._preparedConnectData [Lnet/rim/device/api/util/DataBuffer;
      // 082: bipush 0
      // 083: aaload
      // 084: ifnull 090
      // 087: aload 0
      // 088: getfield net/rim/device/apps/internal/browser/core/BSMManager._preparedConnectData [Lnet/rim/device/api/util/DataBuffer;
      // 08b: bipush 1
      // 08c: aaload
      // 08d: ifnonnull 094
      // 090: aload 0
      // 091: invokespecial net/rim/device/apps/internal/browser/core/BSMManager.prepareConnectData ()V
      // 094: aload 0
      // 095: aload 0
      // 096: astore 8
      // 098: monitorenter
      // 099: aload 0
      // 09a: getfield net/rim/device/apps/internal/browser/core/BSMManager._preparedConnectData [Lnet/rim/device/api/util/DataBuffer;
      // 09d: bipush 0
      // 09e: aaload
      // 09f: invokevirtual net/rim/device/api/util/DataBuffer.getArrayLength ()I
      // 0a2: aload 0
      // 0a3: getfield net/rim/device/apps/internal/browser/core/BSMManager._preparedConnectData [Lnet/rim/device/api/util/DataBuffer;
      // 0a6: bipush 1
      // 0a7: aaload
      // 0a8: invokevirtual net/rim/device/api/util/DataBuffer.getArrayLength ()I
      // 0ab: iadd
      // 0ac: istore 9
      // 0ae: iload 9
      // 0b0: istore 5
      // 0b2: iload 9
      // 0b4: ldc_w 64000
      // 0b7: if_icmple 0dc
      // 0ba: aload 1
      // 0bb: bipush 12
      // 0bd: invokevirtual net/rim/device/apps/internal/browser/options/BrowserConfigRecord.getPropertyAsInt (I)I
      // 0c0: bipush 1
      // 0c1: if_icmpeq 0dc
      // 0c4: bipush 1
      // 0c5: istore 6
      // 0c7: aload 0
      // 0c8: getfield net/rim/device/apps/internal/browser/core/BSMManager._preparedConnectData [Lnet/rim/device/api/util/DataBuffer;
      // 0cb: bipush 0
      // 0cc: aaload
      // 0cd: bipush 1
      // 0ce: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 0d1: aload 0
      // 0d2: getfield net/rim/device/apps/internal/browser/core/BSMManager._preparedConnectData [Lnet/rim/device/api/util/DataBuffer;
      // 0d5: bipush 0
      // 0d6: aaload
      // 0d7: invokevirtual net/rim/device/api/util/DataBuffer.getArrayLength ()I
      // 0da: istore 9
      // 0dc: aload 0
      // 0dd: getfield net/rim/device/apps/internal/browser/core/BSMManager._preparedConnectData [Lnet/rim/device/api/util/DataBuffer;
      // 0e0: bipush 0
      // 0e1: aaload
      // 0e2: astore 10
      // 0e4: aload 7
      // 0e6: ldc_w "CONNECT"
      // 0e9: invokevirtual java/lang/String.getBytes ()[B
      // 0ec: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([B)V
      // 0ef: aload 7
      // 0f1: getstatic net/rim/device/apps/internal/browser/core/BSMManager.HDR_VALUE [B
      // 0f4: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([B)V
      // 0f7: aload 7
      // 0f9: iload 9
      // 0fb: invokestatic java/lang/Integer.toString (I)Ljava/lang/String;
      // 0fe: invokevirtual java/lang/String.getBytes ()[B
      // 101: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([B)V
      // 104: aload 7
      // 106: getstatic net/rim/device/apps/internal/browser/core/BSMManager.CRLF_CRLF [B
      // 109: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([B)V
      // 10c: aload 7
      // 10e: aload 10
      // 110: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 113: aload 10
      // 115: invokevirtual net/rim/device/api/util/DataBuffer.getArrayStart ()I
      // 118: aload 10
      // 11a: invokevirtual net/rim/device/api/util/DataBuffer.getArrayLength ()I
      // 11d: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([BII)V
      // 120: iload 6
      // 122: ifne 141
      // 125: aload 0
      // 126: getfield net/rim/device/apps/internal/browser/core/BSMManager._preparedConnectData [Lnet/rim/device/api/util/DataBuffer;
      // 129: bipush 1
      // 12a: aaload
      // 12b: astore 10
      // 12d: aload 7
      // 12f: aload 10
      // 131: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 134: aload 10
      // 136: invokevirtual net/rim/device/api/util/DataBuffer.getArrayStart ()I
      // 139: aload 10
      // 13b: invokevirtual net/rim/device/api/util/DataBuffer.getArrayLength ()I
      // 13e: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([BII)V
      // 141: aload 8
      // 143: monitorexit
      // 144: goto 14f
      // 147: astore 11
      // 149: aload 8
      // 14b: monitorexit
      // 14c: aload 11
      // 14e: athrow
      // 14f: aload 7
      // 151: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.flush ()V
      // 154: aload 7
      // 156: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.close ()V
      // 159: aload 2
      // 15a: invokeinterface javax/microedition/io/InputConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 15f: astore 8
      // 161: aload 0
      // 162: aload 0
      // 163: astore 9
      // 165: monitorenter
      // 166: aload 0
      // 167: aload 8
      // 169: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectInputStream Ljava/io/InputStream;
      // 16c: aload 9
      // 16e: monitorexit
      // 16f: goto 17a
      // 172: astore 12
      // 174: aload 9
      // 176: monitorexit
      // 177: aload 12
      // 179: athrow
      // 17a: aload 8
      // 17c: invokevirtual java/io/DataInputStream.readUTF ()Ljava/lang/String;
      // 17f: astore 9
      // 181: aload 9
      // 183: ifnull 1f9
      // 186: aload 9
      // 188: ldc_w "200"
      // 18b: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 18e: bipush -1
      // 190: if_icmpeq 1f9
      // 193: aload 0
      // 194: aload 0
      // 195: astore 10
      // 197: monitorenter
      // 198: aload 2
      // 199: instanceof java/lang/Object
      // 19c: ifeq 1aa
      // 19f: aload 0
      // 1a0: aload 2
      // 1a1: checkcast java/lang/Object
      // 1a4: invokevirtual net/rim/device/cldc/io/ippp/StreamProtocol.getSpecificUID ()Ljava/lang/String;
      // 1a7: putfield net/rim/device/apps/internal/browser/core/BSMManager._specificUID Ljava/lang/String;
      // 1aa: aload 0
      // 1ab: getfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 1ae: ifnonnull 1b8
      // 1b1: aload 0
      // 1b2: invokevirtual net/rim/device/apps/internal/browser/core/BSMManager.disconnect ()V
      // 1b5: goto 1dd
      // 1b8: aload 0
      // 1b9: getfield net/rim/device/apps/internal/browser/core/BSMManager._bsmThread Lnet/rim/device/apps/internal/browser/core/BSMThread;
      // 1bc: ifnull 1dd
      // 1bf: aload 0
      // 1c0: getfield net/rim/device/apps/internal/browser/core/BSMManager._bsmThread Lnet/rim/device/apps/internal/browser/core/BSMThread;
      // 1c3: invokevirtual java/lang/Thread.start ()V
      // 1c6: iload 6
      // 1c8: ifeq 1dd
      // 1cb: aload 0
      // 1cc: aload 0
      // 1cd: getfield net/rim/device/apps/internal/browser/core/BSMManager._browserSession Lnet/rim/device/apps/internal/browser/core/BrowserSession;
      // 1d0: bipush 1
      // 1d1: invokestatic net/rim/device/apps/internal/browser/util/BSMUtil.getNewBuffer (Lnet/rim/device/apps/internal/browser/core/BrowserSession;Z)Lnet/rim/device/api/util/DataBuffer;
      // 1d4: aload 0
      // 1d5: getfield net/rim/device/apps/internal/browser/core/BSMManager._preparedConnectData [Lnet/rim/device/api/util/DataBuffer;
      // 1d8: bipush 1
      // 1d9: aaload
      // 1da: invokevirtual net/rim/device/apps/internal/browser/core/BSMManager.update (Lnet/rim/device/api/util/DataBuffer;Lnet/rim/device/api/util/DataBuffer;)V
      // 1dd: aload 0
      // 1de: getfield net/rim/device/apps/internal/browser/core/BSMManager._preparedConnectData [Lnet/rim/device/api/util/DataBuffer;
      // 1e1: bipush 0
      // 1e2: aconst_null
      // 1e3: aastore
      // 1e4: aload 0
      // 1e5: getfield net/rim/device/apps/internal/browser/core/BSMManager._preparedConnectData [Lnet/rim/device/api/util/DataBuffer;
      // 1e8: bipush 1
      // 1e9: aconst_null
      // 1ea: aastore
      // 1eb: aload 10
      // 1ed: monitorexit
      // 1ee: goto 229
      // 1f1: astore 13
      // 1f3: aload 10
      // 1f5: monitorexit
      // 1f6: aload 13
      // 1f8: athrow
      // 1f9: aload 0
      // 1fa: aload 0
      // 1fb: getfield net/rim/device/apps/internal/browser/core/BSMManager._failedConnectAttemptsCount I
      // 1fe: bipush 1
      // 1ff: iadd
      // 200: putfield net/rim/device/apps/internal/browser/core/BSMManager._failedConnectAttemptsCount I
      // 203: goto 229
      // 206: astore 9
      // 208: aload 9
      // 20a: dup
      // 20b: instanceof java/lang/Object
      // 20e: ifne 215
      // 211: pop
      // 212: goto 225
      // 215: checkcast java/lang/Object
      // 218: invokevirtual net/rim/device/cldc/io/ippp/SocketBaseIOException.getExceptionCode ()I
      // 21b: bipush 127
      // 21d: if_icmpne 225
      // 220: aload 0
      // 221: bipush 0
      // 222: putfield net/rim/device/apps/internal/browser/core/BSMManager._bsmSupported Z
      // 225: aload 0
      // 226: invokevirtual net/rim/device/apps/internal/browser/core/BSMManager.endSession ()V
      // 229: aload 8
      // 22b: invokevirtual java/io/DataInputStream.close ()V
      // 22e: aload 0
      // 22f: aload 0
      // 230: astore 16
      // 232: monitorenter
      // 233: aload 0
      // 234: getfield net/rim/device/apps/internal/browser/core/BSMManager._failedConnectAttemptsCount I
      // 237: getstatic net/rim/device/apps/internal/browser/core/BSMManager.MAX_CONNECT_ATTEMPTS I
      // 23a: if_icmplt 242
      // 23d: aload 0
      // 23e: bipush 0
      // 23f: putfield net/rim/device/apps/internal/browser/core/BSMManager._bsmSupported Z
      // 242: aload 0
      // 243: getfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 246: ifnull 252
      // 249: aload 0
      // 24a: getfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 24d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 252: aload 0
      // 253: aconst_null
      // 254: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 257: aload 0
      // 258: aconst_null
      // 259: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectInputStream Ljava/io/InputStream;
      // 25c: goto 27d
      // 25f: astore 17
      // 261: aload 0
      // 262: aconst_null
      // 263: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 266: aload 0
      // 267: aconst_null
      // 268: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectInputStream Ljava/io/InputStream;
      // 26b: goto 27d
      // 26e: astore 18
      // 270: aload 0
      // 271: aconst_null
      // 272: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 275: aload 0
      // 276: aconst_null
      // 277: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectInputStream Ljava/io/InputStream;
      // 27a: aload 18
      // 27c: athrow
      // 27d: aload 16
      // 27f: monitorexit
      // 280: goto 28b
      // 283: astore 19
      // 285: aload 16
      // 287: monitorexit
      // 288: aload 19
      // 28a: athrow
      // 28b: aload 0
      // 28c: getfield net/rim/device/apps/internal/browser/core/BSMManager._bsmSupported Z
      // 28f: ifne 296
      // 292: aload 0
      // 293: invokespecial net/rim/device/apps/internal/browser/core/BSMManager.deactivate ()V
      // 296: aload 0
      // 297: invokevirtual net/rim/device/apps/internal/browser/core/BSMManager.isConnected ()Z
      // 29a: ifne 2a0
      // 29d: goto 483
      // 2a0: new java/lang/Object
      // 2a3: dup
      // 2a4: ldc_w "BSM: "
      // 2a7: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 2aa: astore 16
      // 2ac: invokestatic java/lang/System.currentTimeMillis ()J
      // 2af: lstore 17
      // 2b1: aload 16
      // 2b3: ldc_w "connected. "
      // 2b6: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2b9: pop
      // 2ba: aload 16
      // 2bc: iload 5
      // 2be: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 2c1: pop
      // 2c2: aload 16
      // 2c4: ldc_w "bytes; "
      // 2c7: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2ca: pop
      // 2cb: aload 16
      // 2cd: lload 17
      // 2cf: lload 3
      // 2d0: lsub
      // 2d1: sipush 1000
      // 2d4: i2l
      // 2d5: ldiv
      // 2d6: invokevirtual java/lang/StringBuffer.append (J)Ljava/lang/StringBuffer;
      // 2d9: pop
      // 2da: aload 16
      // 2dc: ldc_w "secs"
      // 2df: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2e2: pop
      // 2e3: ldc2_w 1907089860548946979
      // 2e6: aload 16
      // 2e8: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2eb: invokevirtual java/lang/String.getBytes ()[B
      // 2ee: bipush 0
      // 2ef: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 2f2: pop
      // 2f3: return
      // 2f4: astore 6
      // 2f6: aload 0
      // 2f7: aload 0
      // 2f8: astore 16
      // 2fa: monitorenter
      // 2fb: aload 0
      // 2fc: getfield net/rim/device/apps/internal/browser/core/BSMManager._failedConnectAttemptsCount I
      // 2ff: getstatic net/rim/device/apps/internal/browser/core/BSMManager.MAX_CONNECT_ATTEMPTS I
      // 302: if_icmplt 30a
      // 305: aload 0
      // 306: bipush 0
      // 307: putfield net/rim/device/apps/internal/browser/core/BSMManager._bsmSupported Z
      // 30a: aload 0
      // 30b: getfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 30e: ifnull 31a
      // 311: aload 0
      // 312: getfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 315: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 31a: aload 0
      // 31b: aconst_null
      // 31c: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 31f: aload 0
      // 320: aconst_null
      // 321: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectInputStream Ljava/io/InputStream;
      // 324: goto 345
      // 327: astore 17
      // 329: aload 0
      // 32a: aconst_null
      // 32b: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 32e: aload 0
      // 32f: aconst_null
      // 330: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectInputStream Ljava/io/InputStream;
      // 333: goto 345
      // 336: astore 18
      // 338: aload 0
      // 339: aconst_null
      // 33a: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 33d: aload 0
      // 33e: aconst_null
      // 33f: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectInputStream Ljava/io/InputStream;
      // 342: aload 18
      // 344: athrow
      // 345: aload 16
      // 347: monitorexit
      // 348: goto 353
      // 34b: astore 19
      // 34d: aload 16
      // 34f: monitorexit
      // 350: aload 19
      // 352: athrow
      // 353: aload 0
      // 354: getfield net/rim/device/apps/internal/browser/core/BSMManager._bsmSupported Z
      // 357: ifne 35e
      // 35a: aload 0
      // 35b: invokespecial net/rim/device/apps/internal/browser/core/BSMManager.deactivate ()V
      // 35e: aload 0
      // 35f: invokevirtual net/rim/device/apps/internal/browser/core/BSMManager.isConnected ()Z
      // 362: ifne 368
      // 365: goto 483
      // 368: new java/lang/Object
      // 36b: dup
      // 36c: ldc_w "BSM: "
      // 36f: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 372: astore 16
      // 374: invokestatic java/lang/System.currentTimeMillis ()J
      // 377: lstore 17
      // 379: aload 16
      // 37b: ldc_w "connected. "
      // 37e: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 381: pop
      // 382: aload 16
      // 384: iload 5
      // 386: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 389: pop
      // 38a: aload 16
      // 38c: ldc_w "bytes; "
      // 38f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 392: pop
      // 393: aload 16
      // 395: lload 17
      // 397: lload 3
      // 398: lsub
      // 399: sipush 1000
      // 39c: i2l
      // 39d: ldiv
      // 39e: invokevirtual java/lang/StringBuffer.append (J)Ljava/lang/StringBuffer;
      // 3a1: pop
      // 3a2: aload 16
      // 3a4: ldc_w "secs"
      // 3a7: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 3aa: pop
      // 3ab: ldc2_w 1907089860548946979
      // 3ae: aload 16
      // 3b0: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 3b3: invokevirtual java/lang/String.getBytes ()[B
      // 3b6: bipush 0
      // 3b7: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 3ba: pop
      // 3bb: return
      // 3bc: astore 14
      // 3be: aload 0
      // 3bf: aload 0
      // 3c0: astore 16
      // 3c2: monitorenter
      // 3c3: aload 0
      // 3c4: getfield net/rim/device/apps/internal/browser/core/BSMManager._failedConnectAttemptsCount I
      // 3c7: getstatic net/rim/device/apps/internal/browser/core/BSMManager.MAX_CONNECT_ATTEMPTS I
      // 3ca: if_icmplt 3d2
      // 3cd: aload 0
      // 3ce: bipush 0
      // 3cf: putfield net/rim/device/apps/internal/browser/core/BSMManager._bsmSupported Z
      // 3d2: aload 0
      // 3d3: getfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 3d6: ifnull 3e2
      // 3d9: aload 0
      // 3da: getfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 3dd: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3e2: aload 0
      // 3e3: aconst_null
      // 3e4: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 3e7: aload 0
      // 3e8: aconst_null
      // 3e9: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectInputStream Ljava/io/InputStream;
      // 3ec: goto 40d
      // 3ef: astore 17
      // 3f1: aload 0
      // 3f2: aconst_null
      // 3f3: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 3f6: aload 0
      // 3f7: aconst_null
      // 3f8: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectInputStream Ljava/io/InputStream;
      // 3fb: goto 40d
      // 3fe: astore 18
      // 400: aload 0
      // 401: aconst_null
      // 402: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectConnection Ljavax/microedition/io/StreamConnection;
      // 405: aload 0
      // 406: aconst_null
      // 407: putfield net/rim/device/apps/internal/browser/core/BSMManager._connectInputStream Ljava/io/InputStream;
      // 40a: aload 18
      // 40c: athrow
      // 40d: aload 16
      // 40f: monitorexit
      // 410: goto 41b
      // 413: astore 19
      // 415: aload 16
      // 417: monitorexit
      // 418: aload 19
      // 41a: athrow
      // 41b: aload 0
      // 41c: getfield net/rim/device/apps/internal/browser/core/BSMManager._bsmSupported Z
      // 41f: ifne 426
      // 422: aload 0
      // 423: invokespecial net/rim/device/apps/internal/browser/core/BSMManager.deactivate ()V
      // 426: aload 0
      // 427: invokevirtual net/rim/device/apps/internal/browser/core/BSMManager.isConnected ()Z
      // 42a: ifeq 480
      // 42d: new java/lang/Object
      // 430: dup
      // 431: ldc_w "BSM: "
      // 434: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 437: astore 16
      // 439: invokestatic java/lang/System.currentTimeMillis ()J
      // 43c: lstore 17
      // 43e: aload 16
      // 440: ldc_w "connected. "
      // 443: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 446: pop
      // 447: aload 16
      // 449: iload 5
      // 44b: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 44e: pop
      // 44f: aload 16
      // 451: ldc_w "bytes; "
      // 454: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 457: pop
      // 458: aload 16
      // 45a: lload 17
      // 45c: lload 3
      // 45d: lsub
      // 45e: sipush 1000
      // 461: i2l
      // 462: ldiv
      // 463: invokevirtual java/lang/StringBuffer.append (J)Ljava/lang/StringBuffer;
      // 466: pop
      // 467: aload 16
      // 469: ldc_w "secs"
      // 46c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 46f: pop
      // 470: ldc2_w 1907089860548946979
      // 473: aload 16
      // 475: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 478: invokevirtual java/lang/String.getBytes ()[B
      // 47b: bipush 0
      // 47c: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 47f: pop
      // 480: aload 14
      // 482: athrow
      // 483: return
      // try (14 -> 30): 53 null
      // try (31 -> 41): 53 null
      // try (42 -> 52): 53 null
      // try (53 -> 56): 53 null
      // try (83 -> 164): 165 null
      // try (165 -> 168): 165 null
      // try (181 -> 186): 187 null
      // try (187 -> 190): 187 null
      // try (206 -> 250): 251 null
      // try (251 -> 254): 251 null
      // try (192 -> 262): 263 null
      // try (58 -> 281): 379 null
      // try (58 -> 281): 478 null
      // try (379 -> 380): 478 null
      // try (478 -> 479): 478 null
      // try (490 -> 496): 503 null
      // try (391 -> 397): 404 null
      // try (292 -> 298): 305 null
      // try (490 -> 496): 511 null
      // try (391 -> 397): 412 null
      // try (292 -> 298): 313 null
      // try (503 -> 504): 511 null
      // try (404 -> 405): 412 null
      // try (305 -> 306): 313 null
      // try (511 -> 512): 511 null
      // try (412 -> 413): 412 null
      // try (313 -> 314): 313 null
      // try (483 -> 522): 523 null
      // try (384 -> 423): 424 null
      // try (285 -> 324): 325 null
      // try (523 -> 526): 523 null
      // try (424 -> 427): 424 null
      // try (325 -> 328): 325 null
   }

   final void disconnect() {
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
      // 00: aload 0
      // 01: invokevirtual net/rim/device/apps/internal/browser/core/BSMManager.isConnected ()Z
      // 04: ifne 08
      // 07: return
      // 08: aload 0
      // 09: invokespecial net/rim/device/apps/internal/browser/core/BSMManager.openConnection ()Ljavax/microedition/io/StreamConnection;
      // 0c: astore 1
      // 0d: aload 1
      // 0e: ifnonnull 12
      // 11: return
      // 12: aload 0
      // 13: getfield net/rim/device/apps/internal/browser/core/BSMManager._browserSession Lnet/rim/device/apps/internal/browser/core/BrowserSession;
      // 16: invokestatic net/rim/device/apps/internal/browser/util/BSMUtil.getDisconnectData (Lnet/rim/device/apps/internal/browser/core/BrowserSession;)Lnet/rim/device/api/util/DataBuffer;
      // 19: astore 2
      // 1a: aload 2
      // 1b: ifnonnull 1f
      // 1e: return
      // 1f: aload 1
      // 20: invokeinterface javax/microedition/io/OutputConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 25: checkcast java/lang/Object
      // 28: astore 3
      // 29: aload 3
      // 2a: bipush 0
      // 2b: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.setAutoFlushMode (Z)V
      // 2e: aload 3
      // 2f: ldc_w "DISCONNECT"
      // 32: invokevirtual java/lang/String.getBytes ()[B
      // 35: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([B)V
      // 38: aload 3
      // 39: getstatic net/rim/device/apps/internal/browser/core/BSMManager.HDR_VALUE [B
      // 3c: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([B)V
      // 3f: aload 3
      // 40: aload 2
      // 41: invokevirtual net/rim/device/api/util/DataBuffer.getArrayLength ()I
      // 44: invokestatic java/lang/Integer.toString (I)Ljava/lang/String;
      // 47: invokevirtual java/lang/String.getBytes ()[B
      // 4a: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([B)V
      // 4d: aload 3
      // 4e: getstatic net/rim/device/apps/internal/browser/core/BSMManager.CRLF_CRLF [B
      // 51: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([B)V
      // 54: aload 3
      // 55: aload 2
      // 56: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 59: aload 2
      // 5a: invokevirtual net/rim/device/api/util/DataBuffer.getArrayStart ()I
      // 5d: aload 2
      // 5e: invokevirtual net/rim/device/api/util/DataBuffer.getArrayLength ()I
      // 61: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([BII)V
      // 64: aload 3
      // 65: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.flush ()V
      // 68: aload 3
      // 69: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.close ()V
      // 6c: aload 0
      // 6d: invokevirtual net/rim/device/apps/internal/browser/core/BSMManager.endSession ()V
      // 70: aload 1
      // 71: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 76: return
      // 77: astore 3
      // 78: return
      // 79: astore 3
      // 7a: aload 0
      // 7b: invokevirtual net/rim/device/apps/internal/browser/core/BSMManager.endSession ()V
      // 7e: aload 1
      // 7f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 84: return
      // 85: astore 3
      // 86: return
      // 87: astore 4
      // 89: aload 0
      // 8a: invokevirtual net/rim/device/apps/internal/browser/core/BSMManager.endSession ()V
      // 8d: aload 1
      // 8e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 93: goto 98
      // 96: astore 5
      // 98: aload 4
      // 9a: athrow
      // try (54 -> 56): 57 null
      // try (17 -> 52): 59 null
      // try (62 -> 64): 65 null
      // try (17 -> 52): 67 null
      // try (59 -> 60): 67 null
      // try (70 -> 72): 73 null
      // try (67 -> 68): 67 null
   }

   final void update(DataBuffer param1, DataBuffer param2) {
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
      // 00: aload 0
      // 01: invokevirtual net/rim/device/apps/internal/browser/core/BSMManager.isConnected ()Z
      // 04: ifeq 0b
      // 07: aload 1
      // 08: ifnonnull 0c
      // 0b: return
      // 0c: aload 0
      // 0d: invokespecial net/rim/device/apps/internal/browser/core/BSMManager.openConnection ()Ljavax/microedition/io/StreamConnection;
      // 10: astore 3
      // 11: aload 3
      // 12: ifnonnull 16
      // 15: return
      // 16: aload 1
      // 17: invokevirtual net/rim/device/api/util/DataBuffer.getArrayLength ()I
      // 1a: istore 4
      // 1c: aload 2
      // 1d: ifnull 29
      // 20: iload 4
      // 22: aload 2
      // 23: invokevirtual net/rim/device/api/util/DataBuffer.getArrayLength ()I
      // 26: iadd
      // 27: istore 4
      // 29: aload 3
      // 2a: invokeinterface javax/microedition/io/OutputConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 2f: checkcast java/lang/Object
      // 32: astore 5
      // 34: aload 5
      // 36: bipush 0
      // 37: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.setAutoFlushMode (Z)V
      // 3a: aload 5
      // 3c: ldc_w "UPDATE"
      // 3f: invokevirtual java/lang/String.getBytes ()[B
      // 42: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([B)V
      // 45: aload 5
      // 47: getstatic net/rim/device/apps/internal/browser/core/BSMManager.HDR_VALUE [B
      // 4a: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([B)V
      // 4d: aload 5
      // 4f: iload 4
      // 51: invokestatic java/lang/Integer.toString (I)Ljava/lang/String;
      // 54: invokevirtual java/lang/String.getBytes ()[B
      // 57: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([B)V
      // 5a: aload 5
      // 5c: getstatic net/rim/device/apps/internal/browser/core/BSMManager.CRLF_CRLF [B
      // 5f: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([B)V
      // 62: aload 5
      // 64: aload 1
      // 65: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 68: aload 1
      // 69: invokevirtual net/rim/device/api/util/DataBuffer.getArrayStart ()I
      // 6c: aload 1
      // 6d: invokevirtual net/rim/device/api/util/DataBuffer.getArrayLength ()I
      // 70: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([BII)V
      // 73: aload 2
      // 74: ifnull 88
      // 77: aload 5
      // 79: aload 2
      // 7a: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 7d: aload 2
      // 7e: invokevirtual net/rim/device/api/util/DataBuffer.getArrayStart ()I
      // 81: aload 2
      // 82: invokevirtual net/rim/device/api/util/DataBuffer.getArrayLength ()I
      // 85: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.write ([BII)V
      // 88: aload 5
      // 8a: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.flush ()V
      // 8d: aload 5
      // 8f: invokevirtual net/rim/device/cldc/io/ippp/SocketOutputStream.close ()V
      // 92: aload 3
      // 93: invokeinterface javax/microedition/io/InputConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 98: astore 6
      // 9a: aload 6
      // 9c: invokevirtual java/io/DataInputStream.readUTF ()Ljava/lang/String;
      // 9f: astore 7
      // a1: aload 7
      // a3: ifnull b3
      // a6: aload 7
      // a8: ldc_w "200"
      // ab: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // ae: bipush -1
      // b0: if_icmpne b3
      // b3: aload 6
      // b5: invokevirtual java/io/DataInputStream.close ()V
      // b8: goto d3
      // bb: astore 7
      // bd: aload 0
      // be: invokevirtual net/rim/device/apps/internal/browser/core/BSMManager.endSession ()V
      // c1: aload 6
      // c3: invokevirtual java/io/DataInputStream.close ()V
      // c6: goto d3
      // c9: astore 8
      // cb: aload 6
      // cd: invokevirtual java/io/DataInputStream.close ()V
      // d0: aload 8
      // d2: athrow
      // d3: aload 3
      // d4: invokeinterface javax/microedition/io/Connection.close ()V 1
      // d9: return
      // da: astore 4
      // dc: return
      // dd: astore 4
      // df: aload 3
      // e0: invokeinterface javax/microedition/io/Connection.close ()V 1
      // e5: return
      // e6: astore 4
      // e8: return
      // e9: astore 9
      // eb: aload 3
      // ec: invokeinterface javax/microedition/io/Connection.close ()V 1
      // f1: goto f6
      // f4: astore 10
      // f6: aload 9
      // f8: athrow
      // try (69 -> 79): 82 null
      // try (69 -> 79): 88 null
      // try (82 -> 85): 88 null
      // try (88 -> 89): 88 null
      // try (93 -> 95): 96 null
      // try (12 -> 93): 98 null
      // try (99 -> 101): 102 null
      // try (12 -> 93): 104 null
      // try (98 -> 99): 104 null
      // try (105 -> 107): 108 null
      // try (104 -> 105): 104 null
   }

   public final boolean isConnected() {
      return this._bsmThread != null;
   }

   public final synchronized void endSession() {
      if (this._bsmThread != null) {
         this._bsmThread.setDone(true);
         this._bsmThread.addExclusiveToQueue(new BSMRequest(null));
         this._bsmThread = null;
         this._ackEntries.removeAllElements();
         this._preparedConnectData[0] = null;
         this._preparedConnectData[1] = null;
         this._failedConnectAttemptsCount = 0;
         this._specificUID = null;
      }
   }

   @Override
   public final void cacheChanged(int action, Object item) {
      switch (action) {
         case -1:
            return;
         case 0:
         default:
            if (!this.isConnected()) {
               return;
            } else {
               CacheNode node = (CacheNode)item;
               synchronized (this) {
                  this._ackEntries.addElement(node);
                  return;
               }
            }
         case 1:
            if (!this.isConnected()) {
               return;
            } else if (!(item instanceof Object)) {
               return;
            } else {
               synchronized (this) {
                  if (this._ackEntries.size() > 0) {
                     this.sendAcks();
                  }
               }

               Vector nodes = (Vector)item;
               DataBuffer buffer = BSMUtil.getRemoveData(this._browserSession, nodes);
               synchronized (this) {
                  if (this._bsmThread != null) {
                     BSMRequest request = new BSMRequest(this, buffer);
                     this._bsmThread.addToQueue(request);
                  }

                  return;
               }
            }
         case 2:
            synchronized (this) {
               this._ackEntries.removeAllElements();
               if (this._bsmThread != null) {
                  DataBuffer buffer = BSMUtil.getClearData(this._browserSession);
                  BSMRequest request = new BSMRequest(this, buffer);
                  this._bsmThread.addExclusiveToQueue(request);
               } else {
                  this.prepareConnectData();
               }
            }
      }
   }

   @Override
   public final void browserConfigChanged() {
      boolean bsmEnabled = this.isConfigBSMEnabled();
      if (bsmEnabled != this._bsmEnabled) {
         this._bsmEnabled = bsmEnabled;
         if (bsmEnabled) {
            this.activate();
            return;
         }

         this.deactivate();
      }
   }

   @Override
   public final void browserConfigInvalid() {
      this.deactivate();
      BrowserDaemonRegistry.removeBrowserConfigChangeListener(this);
   }

   private final StreamConnection openConnection() {
      String url = "ippp://bsmhandler;ConnectionHandler=bsm";
      BrowserConfigRecord configRecord = this._browserSession.getConfig();
      String uid = configRecord.getPropertyAsString(4);
      if (uid != null && (uid.length() > 1 || uid.length() == 1 && uid.charAt(0) != '-')) {
         url = ((StringBuffer)(new Object())).append(url).append(";ConnectionUID=").append(uid).toString();
      }

      if (this._specificUID != null) {
         url = ((StringBuffer)(new Object())).append(url).append(";SpecificUID=").append(this._specificUID).toString();
      }

      try {
         return (StreamConnection)Connector.open(url);
      } finally {
         ;
      }
   }

   BSMManager(BrowserSession browserSession) {
      Asserts.productionArgumentAssert(browserSession != null);
      this._browserSession = browserSession;
      this._bsmEnabled = this.isConfigBSMEnabled();
      this._ackEntries = (Vector)(new Object());
   }

   private final void activate() {
      synchronized (this._activationLock) {
         if (!this._activated && this._bsmEnabled && this._bsmSupported) {
            BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
            RawDataCache rawDataCache = browserImpl.getRawDataCache();
            rawDataCache.addRawDataCacheListener(this);
            new BSMManager$1(this).start();
            this._activated = true;
         }
      }
   }

   private final void deactivate() {
      synchronized (this._activationLock) {
         if (this._activated) {
            BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
            RawDataCache rawDataCache = browserImpl.getRawDataCache();
            rawDataCache.removeRawDataCacheListener(this);
            synchronized (this) {
               this._ackEntries.removeAllElements();
               this._preparedConnectData[0] = null;
               this._preparedConnectData[1] = null;
               this._failedConnectAttemptsCount = 0;
               if (this._connectConnection != null) {
                  if (this._connectInputStream != null) {
                     label87:
                     try {
                        this._connectInputStream.close();
                     } finally {
                        break label87;
                     }

                     this._connectInputStream = null;
                  }

                  label84:
                  try {
                     this._connectConnection.close();
                     this._connectConnection = null;
                  } finally {
                     break label84;
                  }
               }

               if (this._bsmThread != null) {
                  BSMRequest item = new BSMRequest(this);
                  this._bsmThread.setDone(true);
                  this._bsmThread.addExclusiveToQueue(item);
               }

               this._activated = false;
               this._specificUID = null;
            }
         }
      }
   }

   private final void prepareConnectData() {
      if (this._bsmEnabled) {
         DataBuffer[] data = BSMUtil.getConnectData(this._browserSession);
         if (data != null) {
            synchronized (this) {
               this._preparedConnectData[0] = data[0];
               this._preparedConnectData[1] = data[1];
            }
         }
      }
   }

   private final boolean isConfigBSMEnabled() {
      BrowserConfigRecord configRecord = this._browserSession.getConfig();
      return configRecord != null && StringUtilities.strEqualIgnoreCase(configRecord.getPropertyAsString(3), BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)
         ? configRecord.getPropertyAsBooleanWithOverride((byte)43)
         : false;
   }
}
