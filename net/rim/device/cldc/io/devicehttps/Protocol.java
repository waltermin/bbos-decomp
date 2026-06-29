package net.rim.device.cldc.io.devicehttps;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.SecureConnection;
import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.devicehttp.HttpConnectionManager;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;

public final class Protocol implements ConnectionBaseInterface {
   private static final String APN = "apn";
   private static final String DEVICE_SIDE = "deviceside";
   private static final String CONNECTION_UID = "connectionuid";
   private static final String CONNECTION_SETUP = "connectionsetup";
   private static final String INTERFACE = "interface";
   private static final String RETRY_NO_CONTEXT = "retrynocontext";
   private static final String TUNNEL_AUTH_USERNAME = "tunnelauthusername";
   private static final String TUNNEL_AUTH_PASSWORD = "tunnelauthpassword";

   @Override
   public final int getProperties(String name) {
      return 2;
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      URL url = (URL)(new Object("https", name));
      boolean connectionNotifier = url.getHost() == null && url.getPath() == null;
      return connectionNotifier ? this.doConnectionNotify(url, mode, timeouts) : this.doConnection(url, mode, timeouts);
   }

   private final Connection doConnection(URL url, int mode, boolean timeouts) {
      StringBuffer urlToOpen = (StringBuffer)(new Object("devicessl://"));
      String hostName = url.getHost();
      int port = url.getPort();
      if (port == 0) {
         port = 443;
      }

      if (hostName == null) {
         hostName = "127.0.0.1";
      }

      urlToOpen.append(hostName).append(':').append(port);
      URLParameters params = url.getRIMParameters();
      if (params != null) {
         if (params.containParameter("interface")) {
            urlToOpen.append(";interface=");
            urlToOpen.append(params.getValue("interface"));
         }

         if (params.containParameter("deviceside")) {
            urlToOpen.append(";deviceside=");
            urlToOpen.append(params.getValue("deviceside"));
         }

         if (params.containParameter("retrynocontext")) {
            urlToOpen.append(";retrynocontext=");
            urlToOpen.append(params.getValue("retrynocontext"));
         }
      }

      String apn = null;
      String apnUsername = null;
      String apnPassword = null;
      boolean persistentConnections = false;
      boolean useHttp11 = true;
      ServiceRecord rec = null;
      if (params != null) {
         if (params.containParameter("connectionuid")) {
            ServiceBook sb = ServiceBook.getSB();
            String uid = params.getValue("connectionuid");
            if (uid != null) {
               urlToOpen.append(";connectionuid=");
               urlToOpen.append(uid);
               rec = sb.getRecordByUidAndCid(uid, WPTCPServiceRecord.SERVICE_CID);
            }
         } else if (!params.containParameter("interface") || !StringUtilities.strEqualIgnoreCase(params.getValue("interface"), "wifi", 1701707776)) {
            rec = net.rim.device.cldc.io.socket.Protocol.getDefaultTcpServiceBook();
         }
      } else {
         rec = net.rim.device.cldc.io.socket.Protocol.getDefaultTcpServiceBook();
      }

      if (rec != null) {
         WPTCPServiceRecord record = WPTCPServiceRecord.getRecord(rec);
         if (record != null) {
            persistentConnections = record.getPropertyAsBoolean(2);
            useHttp11 = record.getPropertyAsBoolean(3);
            HostRoutingTable hrt = rec.getAttachedHrt();
            if (hrt != null) {
               HostRoutingInfo hri = hrt.getHris()[0];
               if (hri instanceof GprsHRI) {
                  GprsHRI gprshri = (GprsHRI)hri;
                  apn = gprshri.getApn();
                  apnUsername = gprshri.getApnUsername();
                  apnPassword = gprshri.getApnPassword();
               }
            }
         }
      }

      if (params != null) {
         if (params.containParameter("apn")) {
            apn = params.getValue("apn");
         }

         if (params.containParameter("tunnelauthusername")) {
            apnUsername = params.getValue("tunnelauthusername");
         }

         if (params.containParameter("tunnelauthpassword")) {
            apnPassword = params.getValue("tunnelauthpassword");
         }
      }

      boolean startHandshake = true;
      if (params != null && params.containParameter("connectionsetup")) {
         urlToOpen.append(";connectionsetup=");
         String value = params.getValue("connectionsetup");
         urlToOpen.append(value);
         if (StringUtilities.strEqualIgnoreCase(value, "delayed", 1701707776)) {
            startHandshake = false;
         }
      }

      if (apn != null) {
         urlToOpen.append(";APN=");
         urlToOpen.append(apn);
      }

      if (apnUsername != null) {
         urlToOpen.append(";TunnelAuthUsername=");
         urlToOpen.append(apnUsername);
      }

      if (apnPassword != null) {
         urlToOpen.append(";TunnelAuthPassword=");
         urlToOpen.append(apnPassword);
      }

      try {
         String originalHost = urlToOpen.toString();
         if (persistentConnections) {
            return HttpConnectionManager.getInstance().getConnection(originalHost, mode, timeouts, url, false, useHttp11, rec);
         }

         SecureConnection secureConnection = (SecureConnection)Connector.open(originalHost, mode, timeouts);
         if (secureConnection != null) {
            return new ClientProtocol(
               originalHost,
               url,
               secureConnection,
               startHandshake ? secureConnection.openDataInputStream() : null,
               secureConnection.openDataOutputStream(),
               false,
               useHttp11,
               mode,
               timeouts,
               persistentConnections,
               rec,
               false
            );
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   private final Connection doConnectionNotify(URL param1, int param2, boolean param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: new java/lang/Object
      // 003: dup
      // 004: ldc_w "socket://"
      // 007: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 00a: astore 4
      // 00c: aload 1
      // 00d: invokevirtual net/rim/device/cldc/io/utility/URL.getRIMParameters ()Lnet/rim/device/cldc/io/utility/URLParameters;
      // 010: astore 5
      // 012: aconst_null
      // 013: astore 6
      // 015: aconst_null
      // 016: astore 7
      // 018: aconst_null
      // 019: astore 8
      // 01b: aconst_null
      // 01c: astore 9
      // 01e: aconst_null
      // 01f: astore 10
      // 021: aconst_null
      // 022: astore 11
      // 024: aload 5
      // 026: ifnull 0a7
      // 029: aload 5
      // 02b: ldc_w "apn"
      // 02e: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 031: ifeq 03e
      // 034: aload 5
      // 036: ldc_w "apn"
      // 039: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 03c: astore 7
      // 03e: aload 5
      // 040: ldc_w "tunnelauthusername"
      // 043: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 046: ifeq 053
      // 049: aload 5
      // 04b: ldc_w "tunnelauthusername"
      // 04e: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 051: astore 8
      // 053: aload 5
      // 055: ldc_w "tunnelauthpassword"
      // 058: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 05b: ifeq 068
      // 05e: aload 5
      // 060: ldc_w "tunnelauthpassword"
      // 063: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 066: astore 9
      // 068: aload 5
      // 06a: ldc_w "interface"
      // 06d: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 070: ifeq 07d
      // 073: aload 5
      // 075: ldc_w "interface"
      // 078: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 07b: astore 6
      // 07d: aload 5
      // 07f: ldc_w "deviceside"
      // 082: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 085: ifeq 092
      // 088: aload 5
      // 08a: ldc_w "deviceside"
      // 08d: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 090: astore 10
      // 092: aload 5
      // 094: ldc_w "retrynocontext"
      // 097: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 09a: ifeq 0a7
      // 09d: aload 5
      // 09f: ldc_w "retrynocontext"
      // 0a2: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 0a5: astore 11
      // 0a7: aload 1
      // 0a8: invokevirtual net/rim/device/cldc/io/utility/URL.getPort ()I
      // 0ab: istore 12
      // 0ad: iload 12
      // 0af: ifne 0b7
      // 0b2: sipush 443
      // 0b5: istore 12
      // 0b7: aload 4
      // 0b9: bipush 58
      // 0bb: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 0be: iload 12
      // 0c0: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 0c3: pop
      // 0c4: aload 6
      // 0c6: ifnull 0da
      // 0c9: aload 4
      // 0cb: ldc_w ";interface="
      // 0ce: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0d1: pop
      // 0d2: aload 4
      // 0d4: aload 6
      // 0d6: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0d9: pop
      // 0da: aload 10
      // 0dc: ifnull 0f0
      // 0df: aload 4
      // 0e1: ldc_w ";deviceside="
      // 0e4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0e7: pop
      // 0e8: aload 4
      // 0ea: aload 10
      // 0ec: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0ef: pop
      // 0f0: aload 11
      // 0f2: ifnull 106
      // 0f5: aload 4
      // 0f7: ldc_w ";retrynocontext="
      // 0fa: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0fd: pop
      // 0fe: aload 4
      // 100: aload 11
      // 102: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 105: pop
      // 106: aload 7
      // 108: ifnull 11c
      // 10b: aload 4
      // 10d: ldc_w ";APN="
      // 110: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 113: pop
      // 114: aload 4
      // 116: aload 7
      // 118: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 11b: pop
      // 11c: aload 8
      // 11e: ifnull 132
      // 121: aload 4
      // 123: ldc_w ";TunnelAuthUsername="
      // 126: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 129: pop
      // 12a: aload 4
      // 12c: aload 8
      // 12e: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 131: pop
      // 132: aload 9
      // 134: ifnull 148
      // 137: aload 4
      // 139: ldc_w ";TunnelAuthPassword="
      // 13c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 13f: pop
      // 140: aload 4
      // 142: aload 9
      // 144: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 147: pop
      // 148: aload 4
      // 14a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 14d: iload 2
      // 14e: iload 3
      // 14f: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;IZ)Ljavax/microedition/io/Connection;
      // 152: checkcast java/lang/Object
      // 155: astore 13
      // 157: aload 13
      // 159: ifnull 193
      // 15c: ldc_w "net.rim.device.api.crypto.tls.TLSConnectionFactory"
      // 15f: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 162: astore 14
      // 164: aload 14
      // 166: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 169: checkcast net/rim/device/cldc/io/ssl/ConnectionFactory
      // 16c: astore 15
      // 16e: new net/rim/device/cldc/io/devicehttps/HttpsServerSocket
      // 171: dup
      // 172: aload 1
      // 173: aload 13
      // 175: aload 15
      // 177: invokespecial net/rim/device/cldc/io/devicehttps/HttpsServerSocket.<init> (Lnet/rim/device/cldc/io/utility/URL;Ljavax/microedition/io/ServerSocketConnection;Lnet/rim/device/cldc/io/ssl/ConnectionFactory;)V
      // 17a: areturn
      // 17b: astore 14
      // 17d: goto 187
      // 180: astore 14
      // 182: goto 187
      // 185: astore 14
      // 187: aload 13
      // 189: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 18e: goto 193
      // 191: astore 13
      // 193: new java/lang/Object
      // 196: dup
      // 197: invokespecial javax/microedition/io/ConnectionNotFoundException.<init> ()V
      // 19a: athrow
      // try (152 -> 165): 166 null
      // try (152 -> 165): 168 null
      // try (152 -> 165): 170 null
      // try (143 -> 165): 174 null
      // try (166 -> 173): 174 null
   }
}
