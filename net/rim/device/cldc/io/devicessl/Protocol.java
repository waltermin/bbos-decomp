package net.rim.device.cldc.io.devicessl;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import net.rim.device.cldc.io.utility.URL;

public final class Protocol implements ConnectionBaseInterface {
   private static String CONNECTION_SETUP = "ConnectionSetup";
   private static final String APN = "apn";
   private static final String CONNECTION_UID = "connectionuid";
   private static final String DEVICE_SIDE = "deviceside";
   private static final String INTERFACE = "interface";
   private static final String LOCAL_PORT = "localport";
   private static final String RETRY_NO_CONTEXT = "retrynocontext";
   private static final String TUNNEL_AUTH_USERNAME = "tunnelauthusername";
   private static final String TUNNEL_AUTH_PASSWORD = "tunnelauthpassword";

   @Override
   public final int getProperties(String name) {
      return 2;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      URL url = new URL("tls", name);
      boolean connectionNotifier = url.getHost() == null && url.getPath() == null;
      return connectionNotifier ? null : this.doConnection(url, mode, timeouts);
   }

   private final Connection doConnection(URL param1, int param2, boolean param3) throws ConnectionNotFoundException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: new java/lang/StringBuffer
      // 003: dup
      // 004: ldc_w "socket://"
      // 007: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 00a: astore 4
      // 00c: aload 1
      // 00d: invokevirtual net/rim/device/cldc/io/utility/URL.getHost ()Ljava/lang/String;
      // 010: astore 5
      // 012: aload 1
      // 013: invokevirtual net/rim/device/cldc/io/utility/URL.getPort ()I
      // 016: istore 6
      // 018: iload 6
      // 01a: ifne 022
      // 01d: sipush 443
      // 020: istore 6
      // 022: aload 5
      // 024: ifnonnull 02c
      // 027: ldc_w "127.0.0.1"
      // 02a: astore 5
      // 02c: aload 4
      // 02e: aload 5
      // 030: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 033: bipush 58
      // 035: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 038: iload 6
      // 03a: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 03d: pop
      // 03e: aload 1
      // 03f: invokevirtual net/rim/device/cldc/io/utility/URL.getRIMParameters ()Lnet/rim/device/cldc/io/utility/URLParameters;
      // 042: astore 7
      // 044: aload 7
      // 046: ifnonnull 04c
      // 049: goto 0f6
      // 04c: aload 7
      // 04e: ldc_w "interface"
      // 051: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 054: ifeq 06e
      // 057: aload 4
      // 059: ldc_w ";interface="
      // 05c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 05f: pop
      // 060: aload 4
      // 062: aload 7
      // 064: ldc_w "interface"
      // 067: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 06a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 06d: pop
      // 06e: aload 7
      // 070: ldc_w "deviceside"
      // 073: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 076: ifeq 090
      // 079: aload 4
      // 07b: ldc_w ";deviceside="
      // 07e: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 081: pop
      // 082: aload 4
      // 084: aload 7
      // 086: ldc_w "deviceside"
      // 089: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 08c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 08f: pop
      // 090: aload 7
      // 092: ldc_w "connectionuid"
      // 095: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 098: ifeq 0b2
      // 09b: aload 4
      // 09d: ldc_w ";connectionuid="
      // 0a0: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0a3: pop
      // 0a4: aload 4
      // 0a6: aload 7
      // 0a8: ldc_w "connectionuid"
      // 0ab: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 0ae: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0b1: pop
      // 0b2: aload 7
      // 0b4: ldc_w "retrynocontext"
      // 0b7: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 0ba: ifeq 0d4
      // 0bd: aload 4
      // 0bf: ldc_w ";retrynocontext="
      // 0c2: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0c5: pop
      // 0c6: aload 4
      // 0c8: aload 7
      // 0ca: ldc_w "retrynocontext"
      // 0cd: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 0d0: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0d3: pop
      // 0d4: aload 7
      // 0d6: ldc_w "localport"
      // 0d9: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 0dc: ifeq 0f6
      // 0df: aload 4
      // 0e1: ldc_w ";localport="
      // 0e4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0e7: pop
      // 0e8: aload 4
      // 0ea: aload 7
      // 0ec: ldc_w "localport"
      // 0ef: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 0f2: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0f5: pop
      // 0f6: bipush 1
      // 0f7: istore 8
      // 0f9: aload 7
      // 0fb: ifnull 124
      // 0fe: aload 7
      // 100: getstatic net/rim/device/cldc/io/devicessl/Protocol.CONNECTION_SETUP Ljava/lang/String;
      // 103: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 106: ifeq 124
      // 109: aload 7
      // 10b: getstatic net/rim/device/cldc/io/devicessl/Protocol.CONNECTION_SETUP Ljava/lang/String;
      // 10e: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 111: astore 9
      // 113: aload 9
      // 115: ldc_w "delayed"
      // 118: ldc_w 1701707776
      // 11b: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 11e: ifeq 124
      // 121: bipush 0
      // 122: istore 8
      // 124: aload 7
      // 126: ifnull 14b
      // 129: aload 7
      // 12b: ldc_w "apn"
      // 12e: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 131: ifeq 14b
      // 134: aload 4
      // 136: ldc_w ";apn="
      // 139: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 13c: pop
      // 13d: aload 4
      // 13f: aload 7
      // 141: ldc_w "apn"
      // 144: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 147: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 14a: pop
      // 14b: aload 7
      // 14d: ifnull 172
      // 150: aload 7
      // 152: ldc_w "tunnelauthusername"
      // 155: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 158: ifeq 172
      // 15b: aload 4
      // 15d: ldc_w ";TunnelAuthUsername="
      // 160: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 163: pop
      // 164: aload 4
      // 166: aload 7
      // 168: ldc_w "tunnelauthusername"
      // 16b: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 16e: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 171: pop
      // 172: aload 7
      // 174: ifnull 199
      // 177: aload 7
      // 179: ldc_w "tunnelauthpassword"
      // 17c: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 17f: ifeq 199
      // 182: aload 4
      // 184: ldc_w ";TunnelAuthPassword="
      // 187: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 18a: pop
      // 18b: aload 4
      // 18d: aload 7
      // 18f: ldc_w "tunnelauthpassword"
      // 192: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 195: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 198: pop
      // 199: aload 4
      // 19b: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 19e: iload 2
      // 19f: iload 3
      // 1a0: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;IZ)Ljavax/microedition/io/Connection;
      // 1a3: checkcast javax/microedition/io/SocketConnection
      // 1a6: astore 9
      // 1a8: aload 9
      // 1aa: ifnull 1f9
      // 1ad: ldc_w "net.rim.device.api.crypto.tls.TLSConnectionFactory"
      // 1b0: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 1b3: astore 10
      // 1b5: aload 10
      // 1b7: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 1ba: checkcast net/rim/device/cldc/io/ssl/ConnectionFactory
      // 1bd: astore 11
      // 1bf: aload 11
      // 1c1: invokestatic net/rim/device/cldc/io/ssl/TLSOptionStore.getOptions ()Lnet/rim/device/cldc/io/ssl/TLSOptionStore;
      // 1c4: invokevirtual net/rim/device/cldc/io/ssl/TLSOptionStore.useTLS ()Z
      // 1c7: ifeq 1d0
      // 1ca: ldc_w "TLS"
      // 1cd: goto 1d3
      // 1d0: ldc_w "SSL"
      // 1d3: aload 9
      // 1d5: aload 1
      // 1d6: invokevirtual net/rim/device/cldc/io/utility/URL.toString ()Ljava/lang/String;
      // 1d9: iload 8
      // 1db: invokeinterface net/rim/device/cldc/io/ssl/ConnectionFactory.createInstance (Ljava/lang/String;Ljavax/microedition/io/StreamConnection;Ljava/lang/String;Z)Ljavax/microedition/io/SecureConnection; 5
      // 1e0: areturn
      // 1e1: astore 10
      // 1e3: goto 1ed
      // 1e6: astore 10
      // 1e8: goto 1ed
      // 1eb: astore 10
      // 1ed: aload 9
      // 1ef: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1f4: goto 1f9
      // 1f7: astore 9
      // 1f9: new javax/microedition/io/ConnectionNotFoundException
      // 1fc: dup
      // 1fd: invokespecial javax/microedition/io/ConnectionNotFoundException.<init> ()V
      // 200: athrow
      // try (179 -> 198): 199 null
      // try (179 -> 198): 201 null
      // try (179 -> 198): 203 null
      // try (170 -> 198): 207 null
      // try (199 -> 206): 207 null
   }
}
