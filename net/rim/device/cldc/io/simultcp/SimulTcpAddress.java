package net.rim.device.cldc.io.simultcp;

import net.rim.device.internal.io.streamdatagram.StreamDatagramAddressBase;
import net.rim.device.internal.io.tcp.TcpConnectionIdentifier;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;
import net.rim.device.internal.system.TCPPacketHeader;

public final class SimulTcpAddress extends StreamDatagramAddressBase implements SimulTcpConstants, TcpConnectionIdentifier {
   private int _connectionLocalPort = -1;

   final void setIpAddressAsInt(int ipAddressAsInt) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void setPort(int port) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void setConnectionLocalPort(int port) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final int getConnectionDestinationPort() {
      return this.getDestPort();
   }

   @Override
   public final String getConnectionApn() {
      return this.getApnName();
   }

   @Override
   public final int getConnectionLocalPort() {
      return this._connectionLocalPort == -1 ? super._localPort : this._connectionLocalPort;
   }

   @Override
   public final int getConnectionIpAddress() {
      return this.getIpAddress();
   }

   public SimulTcpAddress(String address) {
      this(address, null);
   }

   public SimulTcpAddress(String address, String apn) {
      if (address.length() == 0) {
         super._ipAddress = -1;
         super._port = -1;
         super._localPort = -1;
         super._apnName = null;
         super._address = "";
      } else {
         this.parseAddress(address);
         this.processApnInfo(apn);
      }
   }

   private final void processApnInfo(String apn) {
      if (apn != null) {
         super._apnName = apn;
      } else {
         super._apnName = TunnelCredentialsProvider.getInstance().getApn();
         super._apnUsername = TunnelCredentialsProvider.getInstance().getApnUsername();
         super._apnPassword = TunnelCredentialsProvider.getInstance().getApnPassword();
      }
   }

   public SimulTcpAddress(byte[] ipAddress, int destPort, int srcPort, String apn, int apnOffset, int apnLength) {
      super._isListenAddress = false;
      super._ipAddress = TCPPacketHeader.IPv4ByteArrayToInt(ipAddress);
      if (super._ipAddress == -1) {
         super._isListenAddress = true;
         if (destPort != -1) {
            destPort = -1;
         }
      }

      super._port = destPort;
      if (super._isListenAddress) {
         throw new Object();
      }

      if (srcPort != -1 && destPort != -1) {
         super._localPort = srcPort;
         super._port = destPort;
      } else {
         if (destPort == -1) {
            throw new Object("A destPort should always be included if the Listen state is not used!");
         }

         srcPort = -1;
         super._localPort = -1;
      }

      super._address = StreamDatagramAddressBase.makeAddress(false, super._ipAddress != 0 ? ipAddress : null, destPort, srcPort);
      this.processApnInfo(apn);
   }

   public SimulTcpAddress(byte[] ipAddress, int destPort, int srcPort, String apn) {
      this(ipAddress, destPort, srcPort, apn, 0, 0);
   }

   public SimulTcpAddress(byte[] ipAddress, int destPort) {
      this(ipAddress, destPort, -1, null);
   }

   public SimulTcpAddress(byte[] ipAddress, int destPort, int srcPort) {
      this(ipAddress, destPort, srcPort, null);
   }

   public SimulTcpAddress(byte[] ipAddress, int destPort, String apn) {
      this(ipAddress, destPort, -1, apn, 0, 0);
   }

   @Override
   public final void setLocalPort(int port) {
      if (port != super._localPort) {
         super._address = StreamDatagramAddressBase.makeAddress(
            false, super._ipAddress != 0 ? TCPPacketHeader.IPv4IntToByteArray(super._ipAddress) : null, super._port, port
         );
      }

      super._localPort = port;
   }

   @Override
   protected final void parseAddress(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 2
      // 002: bipush 0
      // 003: istore 3
      // 004: aload 1
      // 005: invokevirtual java/lang/String.length ()I
      // 008: istore 4
      // 00a: bipush 0
      // 00b: istore 5
      // 00d: bipush 0
      // 00e: istore 6
      // 010: aload 1
      // 011: getstatic net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.SLASH_SLASH Ljava/lang/String;
      // 014: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 017: ifeq 021
      // 01a: bipush 2
      // 01c: istore 6
      // 01e: goto 02c
      // 021: aload 1
      // 022: getstatic net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.COMPARISON_STRING Ljava/lang/String;
      // 025: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 028: bipush 1
      // 029: iadd
      // 02a: istore 6
      // 02c: aload 1
      // 02d: bipush 58
      // 02f: iload 6
      // 031: invokevirtual java/lang/String.indexOf (II)I
      // 034: bipush -1
      // 036: if_icmpne 057
      // 039: aload 1
      // 03a: getstatic net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.SLASH_SLASH Ljava/lang/String;
      // 03d: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 040: ifeq 057
      // 043: new java/lang/Object
      // 046: dup
      // 047: invokespecial java/lang/StringBuffer.<init> ()V
      // 04a: aload 1
      // 04b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 04e: bipush 58
      // 050: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 053: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 056: astore 1
      // 057: iload 4
      // 059: bipush 2
      // 05b: if_icmpge 061
      // 05e: goto 1b7
      // 061: aload 1
      // 062: bipush 0
      // 063: invokevirtual java/lang/String.charAt (I)C
      // 066: bipush 47
      // 068: if_icmpeq 06e
      // 06b: goto 1b7
      // 06e: aload 1
      // 06f: bipush 1
      // 070: invokevirtual java/lang/String.charAt (I)C
      // 073: bipush 47
      // 075: if_icmpeq 07b
      // 078: goto 1b7
      // 07b: aload 0
      // 07c: bipush 0
      // 07d: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._ipAddress I
      // 080: aload 1
      // 081: bipush 58
      // 083: bipush 2
      // 085: invokevirtual java/lang/String.indexOf (II)I
      // 088: istore 3
      // 089: iload 3
      // 08a: bipush -1
      // 08c: if_icmpeq 095
      // 08f: iload 3
      // 090: bipush 2
      // 092: if_icmpne 09d
      // 095: aload 0
      // 096: bipush 1
      // 097: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._isListenAddress Z
      // 09a: goto 1b7
      // 09d: aload 0
      // 09e: bipush 0
      // 09f: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._isListenAddress Z
      // 0a2: aload 1
      // 0a3: bipush 2
      // 0a5: iload 3
      // 0a6: invokestatic net/rim/device/api/io/DatagramAddressBase.isDomainName (Ljava/lang/String;II)Z
      // 0a9: ifne 0b9
      // 0ac: aload 0
      // 0ad: aload 1
      // 0ae: bipush 2
      // 0b0: invokestatic net/rim/device/api/io/DatagramAddressBase.parseIpAddressInt (Ljava/lang/String;I)I
      // 0b3: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._ipAddress I
      // 0b6: goto 1b4
      // 0b9: bipush 0
      // 0ba: istore 7
      // 0bc: iload 7
      // 0be: invokestatic net/rim/device/api/system/RadioInfo.isPDPContextActive (I)Z
      // 0c1: ifeq 0c7
      // 0c4: goto 0df
      // 0c7: iload 7
      // 0c9: bipush 8
      // 0cb: if_icmpne 0d9
      // 0ce: new java/lang/Object
      // 0d1: dup
      // 0d2: ldc_w "No active pdp context found"
      // 0d5: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 0d8: athrow
      // 0d9: iinc 7 1
      // 0dc: goto 0bc
      // 0df: aconst_null
      // 0e0: astore 8
      // 0e2: invokestatic net/rim/device/cldc/io/dns/DNSResolverIPv4.instance ()Lnet/rim/device/cldc/io/dns/DNSResolverIPv4;
      // 0e5: aload 1
      // 0e6: bipush 2
      // 0e8: iload 3
      // 0e9: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 0ec: iload 7
      // 0ee: invokevirtual net/rim/device/cldc/io/dns/DNSResolverIPv4.getAddressByHostname (Ljava/lang/String;I)Ljava/util/Vector;
      // 0f1: astore 9
      // 0f3: aload 9
      // 0f5: ifnull 112
      // 0f8: aload 9
      // 0fa: invokevirtual java/util/Vector.size ()I
      // 0fd: ifle 112
      // 100: aload 9
      // 102: aload 9
      // 104: invokevirtual java/util/Vector.size ()I
      // 107: invokestatic net/rim/device/api/crypto/RandomSource.getInt (I)I
      // 10a: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 10d: checkcast [B
      // 110: astore 8
      // 112: aload 8
      // 114: ifnonnull 122
      // 117: new java/lang/Object
      // 11a: dup
      // 11b: ldc_w "DNS query returned no results"
      // 11e: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 121: athrow
      // 122: new java/lang/Object
      // 125: dup
      // 126: bipush 15
      // 128: invokespecial java/lang/StringBuffer.<init> (I)V
      // 12b: astore 10
      // 12d: bipush 0
      // 12e: istore 11
      // 130: iload 11
      // 132: bipush 4
      // 134: if_icmpge 15b
      // 137: aload 10
      // 139: aload 8
      // 13b: iload 11
      // 13d: baload
      // 13e: sipush 255
      // 141: iand
      // 142: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 145: pop
      // 146: iload 11
      // 148: bipush 3
      // 14a: if_icmpeq 155
      // 14d: aload 10
      // 14f: bipush 46
      // 151: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 154: pop
      // 155: iinc 11 1
      // 158: goto 130
      // 15b: aload 10
      // 15d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 160: astore 11
      // 162: aload 0
      // 163: aload 11
      // 165: bipush 0
      // 166: invokestatic net/rim/device/api/io/DatagramAddressBase.parseIpAddressInt (Ljava/lang/String;I)I
      // 169: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._ipAddress I
      // 16c: new java/lang/Object
      // 16f: dup
      // 170: getstatic net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.SLASH_SLASH Ljava/lang/String;
      // 173: invokevirtual java/lang/String.length ()I
      // 176: aload 11
      // 178: invokevirtual java/lang/String.length ()I
      // 17b: iadd
      // 17c: aload 1
      // 17d: invokevirtual java/lang/String.length ()I
      // 180: iload 3
      // 181: isub
      // 182: iadd
      // 183: invokespecial java/lang/StringBuffer.<init> (I)V
      // 186: astore 10
      // 188: aload 10
      // 18a: getstatic net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.SLASH_SLASH Ljava/lang/String;
      // 18d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 190: pop
      // 191: aload 10
      // 193: aload 11
      // 195: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 198: pop
      // 199: aload 10
      // 19b: aload 1
      // 19c: iload 3
      // 19d: aload 1
      // 19e: invokevirtual java/lang/String.length ()I
      // 1a1: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 1a4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1a7: pop
      // 1a8: aload 10
      // 1aa: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 1ad: astore 1
      // 1ae: aload 1
      // 1af: invokevirtual java/lang/String.length ()I
      // 1b2: istore 4
      // 1b4: bipush 1
      // 1b5: istore 5
      // 1b7: aload 0
      // 1b8: aload 0
      // 1b9: bipush -1
      // 1bb: dup_x1
      // 1bc: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 1bf: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._port I
      // 1c2: aload 1
      // 1c3: bipush 58
      // 1c5: bipush 2
      // 1c7: invokevirtual java/lang/String.indexOf (II)I
      // 1ca: istore 3
      // 1cb: iload 5
      // 1cd: ifne 1e1
      // 1d0: iload 5
      // 1d2: ifeq 1d8
      // 1d5: goto 2c1
      // 1d8: iload 3
      // 1d9: bipush 2
      // 1db: if_icmpeq 1e1
      // 1de: goto 2c1
      // 1e1: iload 4
      // 1e3: iload 3
      // 1e4: if_icmpgt 1ea
      // 1e7: goto 2c1
      // 1ea: aload 1
      // 1eb: iload 3
      // 1ec: invokevirtual java/lang/String.charAt (I)C
      // 1ef: bipush 58
      // 1f1: if_icmpeq 1f7
      // 1f4: goto 2c1
      // 1f7: iload 3
      // 1f8: bipush 1
      // 1f9: iadd
      // 1fa: istore 2
      // 1fb: aload 1
      // 1fc: iload 2
      // 1fd: invokestatic net/rim/device/api/io/DatagramAddressBase.indexOfNextDelim (Ljava/lang/String;I)I
      // 200: istore 3
      // 201: iload 3
      // 202: iload 2
      // 203: if_icmpgt 211
      // 206: new java/lang/Object
      // 209: dup
      // 20a: ldc_w "Bad DEST_PORT"
      // 20d: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 210: athrow
      // 211: aload 1
      // 212: iload 2
      // 213: iload 3
      // 214: bipush 10
      // 216: invokestatic net/rim/device/api/io/DatagramAddressBase.parseInt (Ljava/lang/String;III)I
      // 219: istore 7
      // 21b: iload 7
      // 21d: iflt 228
      // 220: iload 7
      // 222: ldc_w 65535
      // 225: if_icmple 233
      // 228: new java/lang/Object
      // 22b: dup
      // 22c: ldc_w "Invalid DEST_PORT"
      // 22f: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 232: athrow
      // 233: iload 5
      // 235: ifeq 241
      // 238: aload 0
      // 239: iload 7
      // 23b: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._port I
      // 23e: goto 247
      // 241: aload 0
      // 242: iload 7
      // 244: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 247: iload 5
      // 249: ifeq 2c1
      // 24c: iload 4
      // 24e: iload 3
      // 24f: if_icmple 2bb
      // 252: aload 1
      // 253: iload 3
      // 254: invokevirtual java/lang/String.charAt (I)C
      // 257: bipush 59
      // 259: if_icmpne 2bb
      // 25c: iload 3
      // 25d: bipush 1
      // 25e: iadd
      // 25f: istore 2
      // 260: aload 1
      // 261: iload 2
      // 262: invokestatic net/rim/device/api/io/DatagramAddressBase.indexOfNextDelim (Ljava/lang/String;I)I
      // 265: istore 3
      // 266: iload 3
      // 267: iload 2
      // 268: if_icmpgt 276
      // 26b: new java/lang/Object
      // 26e: dup
      // 26f: ldc_w "Bad SRC_PORT"
      // 272: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 275: athrow
      // 276: aload 1
      // 277: iload 2
      // 278: iload 3
      // 279: bipush 10
      // 27b: invokestatic net/rim/device/api/io/DatagramAddressBase.parseInt (Ljava/lang/String;III)I
      // 27e: istore 8
      // 280: iload 8
      // 282: iflt 28d
      // 285: iload 8
      // 287: ldc_w 65535
      // 28a: if_icmple 298
      // 28d: new java/lang/Object
      // 290: dup
      // 291: ldc_w "Invalid SRC_PORT"
      // 294: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 297: athrow
      // 298: aload 0
      // 299: iload 8
      // 29b: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 29e: bipush 1
      // 29f: invokestatic net/rim/device/api/system/ControlledAccess.assertRRISignatures (Z)V
      // 2a2: goto 2c1
      // 2a5: astore 8
      // 2a7: aload 0
      // 2a8: bipush -1
      // 2aa: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 2ad: goto 2c1
      // 2b0: astore 8
      // 2b2: aload 0
      // 2b3: bipush -1
      // 2b5: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 2b8: goto 2c1
      // 2bb: aload 0
      // 2bc: bipush -1
      // 2be: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 2c1: aload 0
      // 2c2: bipush 0
      // 2c3: aload 0
      // 2c4: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._ipAddress I
      // 2c7: ifeq 2d4
      // 2ca: aload 0
      // 2cb: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._ipAddress I
      // 2ce: invokestatic net/rim/device/internal/system/TCPPacketHeader.IPv4IntToByteArray (I)[B
      // 2d1: goto 2d5
      // 2d4: aconst_null
      // 2d5: aload 0
      // 2d6: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._port I
      // 2d9: aload 0
      // 2da: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 2dd: invokestatic net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.makeAddress (Z[BII)Ljava/lang/String;
      // 2e0: putfield net/rim/device/api/io/DatagramAddressBase._address Ljava/lang/String;
      // 2e3: return
      // try (306 -> 327): 328 null
      // try (306 -> 327): 333 null
   }

   @Override
   public final String getConnectionAddress() {
      return StreamDatagramAddressBase.makeAddress(
         true, super._ipAddress != 0 ? TCPPacketHeader.IPv4IntToByteArray(super._ipAddress) : null, super._port, super._localPort
      );
   }

   @Override
   public final int getIpAddress() {
      return super._ipAddress;
   }

   @Override
   public final boolean equals(Object dgramAddress) {
      if (dgramAddress == this) {
         return true;
      }

      if (!(dgramAddress instanceof SimulTcpAddress)) {
         return false;
      }

      SimulTcpAddress address = (SimulTcpAddress)dgramAddress;
      return (super._ipAddress == -1 || address._ipAddress == -1 || super._ipAddress == address._ipAddress)
         && (super._port == -1 || address._port == -1 || super._port == address._port)
         && (super._localPort == -1 || address._localPort == -1 || super._localPort == address._localPort)
         && (super._apnName == null || this.compareApn(address._apnName));
   }

   public SimulTcpAddress(SimulTcpAddress tcpAddressToClone) {
      super._ipAddress = tcpAddressToClone.getIpAddress();
      super._port = tcpAddressToClone.getDestPort();
      super._localPort = tcpAddressToClone.getLocalPort();
      super._apnName = tcpAddressToClone.getApnName();
      super._apnUsername = tcpAddressToClone.getApnUsername();
      super._apnPassword = tcpAddressToClone.getApnPassword();
      super._isListenAddress = tcpAddressToClone.isListenAddress();
      super._address = tcpAddressToClone.getAddress();
      super._key = tcpAddressToClone.getKey();
   }

   public SimulTcpAddress() {
   }
}
