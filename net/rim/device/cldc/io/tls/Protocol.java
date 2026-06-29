package net.rim.device.cldc.io.tls;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.ippp.SocketTransportBase;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.system.ITPolicyInternal;

public final class Protocol implements ConnectionBaseInterface {
   private static ResourceBundle _rb = ResourceBundle.getBundle(-320500590281765934L, "net.rim.device.internal.resource.SSL");
   public static final String END_TO_END_REQUIRED = "EndToEndRequired";
   public static final String END_TO_END_DESIRED = "EndToEndDesired";
   private static final String DEVICE_SIDE = "DeviceSide";
   private static final String INTERFACE = "interface";
   private static final String USE_PIPE = "UsePipe";
   private static final String CONNECTION_SETUP = "ConnectionSetup";

   @Override
   public final int getProperties(String name) {
      URL url = (URL)(new Object("ssl", name));
      URLParameters parameters = url.getRIMParameters();
      if (parameters == null) {
         return 1;
      }

      if (parameters.containParameter("interface")) {
         String value = parameters.getValue("interface");
         if (StringUtilities.strEqualIgnoreCase(value, "wifi", 1701707776)) {
            return 18;
         }

         if (StringUtilities.strEqualIgnoreCase(value, "cellular", 1701707776)) {
            return 2;
         }
      } else if (parameters.containParameter("DeviceSide")) {
         String value = parameters.getValue("DeviceSide");
         if (StringUtilities.strEqualIgnoreCase(value, "true", 1701707776) || StringUtilities.strEqual(value, "1")) {
            return 2;
         }
      }

      String uid = SocketTransportBase.findAcceptableConnectionUid(parameters);
      if (uid == null) {
         throw new Object("Invalid url parameter.");
      } else {
         return ITPolicyInternal.verifyITAdminService(uid, false) ? 1 : 2;
      }
   }

   @Override
   public final Connection openPrim(String param1, int param2, boolean param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 4
      // 003: bipush 1
      // 004: istore 5
      // 006: invokestatic net/rim/device/cldc/io/ssl/TLSOptionStore.getOptions ()Lnet/rim/device/cldc/io/ssl/TLSOptionStore;
      // 009: astore 6
      // 00b: new java/lang/Object
      // 00e: dup
      // 00f: ldc_w "ssl"
      // 012: aload 1
      // 013: invokespecial net/rim/device/cldc/io/utility/URL.<init> (Ljava/lang/String;Ljava/lang/String;)V
      // 016: astore 7
      // 018: aload 7
      // 01a: invokevirtual net/rim/device/cldc/io/utility/URL.getRIMParameters ()Lnet/rim/device/cldc/io/utility/URLParameters;
      // 01d: astore 8
      // 01f: aload 8
      // 021: ifnonnull 027
      // 024: goto 121
      // 027: aload 8
      // 029: ldc_w "interface"
      // 02c: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 02f: ifeq 06b
      // 032: aload 8
      // 034: ldc_w "interface"
      // 037: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 03a: astore 9
      // 03c: aload 9
      // 03e: ldc_w "cellular"
      // 041: ldc_w 1701707776
      // 044: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 047: ifne 058
      // 04a: aload 9
      // 04c: ldc_w "wifi"
      // 04f: ldc_w 1701707776
      // 052: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 055: ifeq 06b
      // 058: aload 7
      // 05a: ldc_w "devicessl"
      // 05d: invokevirtual net/rim/device/cldc/io/utility/URL.setScheme (Ljava/lang/String;)V
      // 060: aload 7
      // 062: invokevirtual net/rim/device/cldc/io/utility/URL.toString ()Ljava/lang/String;
      // 065: iload 2
      // 066: iload 3
      // 067: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;IZ)Ljavax/microedition/io/Connection;
      // 06a: areturn
      // 06b: aload 8
      // 06d: ldc_w "DeviceSide"
      // 070: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 073: ifeq 0ac
      // 076: aload 8
      // 078: ldc_w "DeviceSide"
      // 07b: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 07e: astore 9
      // 080: aload 9
      // 082: ldc_w "true"
      // 085: ldc_w 1701707776
      // 088: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 08b: ifne 099
      // 08e: aload 9
      // 090: ldc_w "1"
      // 093: invokestatic net/rim/device/api/util/StringUtilities.strEqual (Ljava/lang/String;Ljava/lang/String;)Z
      // 096: ifeq 0ac
      // 099: aload 7
      // 09b: ldc_w "devicessl"
      // 09e: invokevirtual net/rim/device/cldc/io/utility/URL.setScheme (Ljava/lang/String;)V
      // 0a1: aload 7
      // 0a3: invokevirtual net/rim/device/cldc/io/utility/URL.toString ()Ljava/lang/String;
      // 0a6: iload 2
      // 0a7: iload 3
      // 0a8: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;IZ)Ljavax/microedition/io/Connection;
      // 0ab: areturn
      // 0ac: aload 8
      // 0ae: ldc_w "UsePipe"
      // 0b1: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 0b4: ifnull 0c0
      // 0b7: aload 8
      // 0b9: ldc_w "UsePipe"
      // 0bc: invokevirtual net/rim/device/cldc/io/utility/URLParameters.remove (Ljava/lang/String;)Ljava/lang/String;
      // 0bf: pop
      // 0c0: aload 8
      // 0c2: ldc_w "EndToEndRequired"
      // 0c5: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 0c8: ifnull 0e7
      // 0cb: invokestatic net/rim/device/cldc/io/ssl/SSLOptionsRegistration.doesDeviceSideExist ()Z
      // 0ce: ifeq 0d7
      // 0d1: bipush 1
      // 0d2: istore 4
      // 0d4: goto 0fb
      // 0d7: new net/rim/device/cldc/io/ssl/TLSSecurityException
      // 0da: dup
      // 0db: getstatic net/rim/device/cldc/io/tls/Protocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0de: bipush 13
      // 0e0: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0e3: invokespecial net/rim/device/cldc/io/ssl/TLSSecurityException.<init> (Ljava/lang/String;)V
      // 0e6: athrow
      // 0e7: aload 8
      // 0e9: ldc_w "EndToEndDesired"
      // 0ec: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 0ef: ifnull 0fb
      // 0f2: invokestatic net/rim/device/cldc/io/ssl/SSLOptionsRegistration.doesDeviceSideExist ()Z
      // 0f5: ifeq 0fb
      // 0f8: bipush 1
      // 0f9: istore 4
      // 0fb: aload 8
      // 0fd: ldc_w "ConnectionSetup"
      // 100: invokevirtual net/rim/device/cldc/io/utility/URLParameters.containParameter (Ljava/lang/String;)Z
      // 103: ifeq 121
      // 106: aload 8
      // 108: ldc_w "ConnectionSetup"
      // 10b: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 10e: astore 9
      // 110: aload 9
      // 112: ldc_w "delayed"
      // 115: ldc_w 1701707776
      // 118: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 11b: ifeq 121
      // 11e: bipush 0
      // 11f: istore 5
      // 121: invokestatic net/rim/device/cldc/io/ssl/SSLOptionsRegistration.doesDeviceSideExist ()Z
      // 124: ifeq 138
      // 127: aload 6
      // 129: invokevirtual net/rim/device/cldc/io/ssl/TLSOptionStore.getDefaultImplementation ()I
      // 12c: istore 9
      // 12e: iload 9
      // 130: bipush 2
      // 132: if_icmpne 138
      // 135: bipush 1
      // 136: istore 4
      // 138: iload 4
      // 13a: ifne 144
      // 13d: aload 8
      // 13f: invokestatic net/rim/device/cldc/io/ssl/SSLOptionsRegistration.isDeviceTLSTheOnlySecureConnection (Lnet/rim/device/cldc/io/utility/URLParameters;)Z
      // 142: istore 4
      // 144: iload 4
      // 146: ifne 14c
      // 149: goto 22b
      // 14c: ldc_w "socket://"
      // 14f: astore 9
      // 151: aload 7
      // 153: invokevirtual net/rim/device/cldc/io/utility/URL.getPath ()Ljava/lang/String;
      // 156: ifnonnull 15f
      // 159: ldc_w ""
      // 15c: goto 164
      // 15f: aload 7
      // 161: invokevirtual net/rim/device/cldc/io/utility/URL.getPath ()Ljava/lang/String;
      // 164: astore 10
      // 166: aload 7
      // 168: invokevirtual net/rim/device/cldc/io/utility/URL.getHost ()Ljava/lang/String;
      // 16b: ifnonnull 174
      // 16e: ldc_w ""
      // 171: goto 179
      // 174: aload 7
      // 176: invokevirtual net/rim/device/cldc/io/utility/URL.getHost ()Ljava/lang/String;
      // 179: astore 11
      // 17b: aload 7
      // 17d: invokevirtual net/rim/device/cldc/io/utility/URL.getPort ()I
      // 180: ifne 189
      // 183: sipush 443
      // 186: goto 18e
      // 189: aload 7
      // 18b: invokevirtual net/rim/device/cldc/io/utility/URL.getPort ()I
      // 18e: istore 12
      // 190: new java/lang/Object
      // 193: dup
      // 194: invokespecial java/lang/StringBuffer.<init> ()V
      // 197: astore 13
      // 199: aload 13
      // 19b: aload 9
      // 19d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1a0: pop
      // 1a1: aload 13
      // 1a3: aload 11
      // 1a5: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1a8: bipush 58
      // 1aa: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 1ad: iload 12
      // 1af: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 1b2: aload 10
      // 1b4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1b7: pop
      // 1b8: aload 8
      // 1ba: ifnull 1dc
      // 1bd: aload 8
      // 1bf: ldc_w "UsePipe"
      // 1c2: invokevirtual net/rim/device/cldc/io/utility/URLParameters.getValue (Ljava/lang/String;)Ljava/lang/String;
      // 1c5: ifnull 1d1
      // 1c8: aload 8
      // 1ca: ldc_w "UsePipe"
      // 1cd: invokevirtual net/rim/device/cldc/io/utility/URLParameters.remove (Ljava/lang/String;)Ljava/lang/String;
      // 1d0: pop
      // 1d1: aload 13
      // 1d3: aload 8
      // 1d5: invokevirtual net/rim/device/cldc/io/utility/URLParameters.toString ()Ljava/lang/String;
      // 1d8: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1db: pop
      // 1dc: aload 13
      // 1de: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 1e1: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 1e4: checkcast java/lang/Object
      // 1e7: astore 14
      // 1e9: ldc_w "net.rim.device.api.crypto.tls.TLSConnectionFactory"
      // 1ec: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 1ef: astore 15
      // 1f1: aload 15
      // 1f3: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 1f6: checkcast net/rim/device/cldc/io/ssl/ConnectionFactory
      // 1f9: astore 16
      // 1fb: aload 16
      // 1fd: ldc_w "TLS"
      // 200: aload 14
      // 202: aload 13
      // 204: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 207: iload 5
      // 209: invokeinterface net/rim/device/cldc/io/ssl/ConnectionFactory.createInstance (Ljava/lang/String;Ljavax/microedition/io/StreamConnection;Ljava/lang/String;Z)Ljavax/microedition/io/SecureConnection; 5
      // 20e: areturn
      // 20f: astore 9
      // 211: goto 21b
      // 214: astore 9
      // 216: goto 21b
      // 219: astore 9
      // 21b: new net/rim/device/cldc/io/ssl/TLSSecurityException
      // 21e: dup
      // 21f: getstatic net/rim/device/cldc/io/tls/Protocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 222: bipush 13
      // 224: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 227: invokespecial net/rim/device/cldc/io/ssl/TLSSecurityException.<init> (Ljava/lang/String;)V
      // 22a: athrow
      // 22b: new java/lang/Object
      // 22e: dup
      // 22f: ldc_w "ssl:"
      // 232: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 235: aload 1
      // 236: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 239: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 23c: iload 2
      // 23d: iload 3
      // 23e: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;IZ)Ljavax/microedition/io/Connection;
      // 241: areturn
      // try (136 -> 214): 215 null
      // try (136 -> 214): 217 null
      // try (136 -> 214): 219 null
   }
}
