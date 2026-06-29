package net.rim.device.internal.io.tcp;

import java.io.IOException;
import net.rim.device.internal.io.streamdatagram.StreamDatagramAddressBase;
import net.rim.device.internal.io.tunnel.TunnelCredentialsProvider;
import net.rim.device.internal.system.TCPPacketHeader;

public final class TcpAddress extends StreamDatagramAddressBase implements TcpConstants, TcpConnectionIdentifier {
   public TcpAddress() {
   }

   public TcpAddress(TcpAddress tcpAddressToClone) {
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

   public TcpAddress(byte[] ipAddress, int destPort) {
      this(ipAddress, destPort, -1, null);
   }

   public TcpAddress(byte[] ipAddress, int destPort, int srcPort) {
      this(ipAddress, destPort, srcPort, null);
   }

   public TcpAddress(byte[] ipAddress, int destPort, String apn) {
      this(ipAddress, destPort, -1, apn, 0, apn != null ? apn.length() : 0);
   }

   public TcpAddress(byte[] ipAddress, int destPort, int srcPort, String apn) {
      this(ipAddress, destPort, srcPort, apn, 0, apn != null ? apn.length() : 0);
   }

   public TcpAddress(byte[] ipAddress, int destPort, int srcPort, String apn, int apnOffset, int apnLength) {
      if (apn != null && apn.length() > apnLength) {
         apn = apn.substring(apnOffset, apnLength);
      }

      this.processApnInfo(apn);
      super._ipAddress = TCPPacketHeader.IPv4ByteArrayToInt(ipAddress);
      if (super._ipAddress == -1) {
         super._isListenAddress = true;
         if (destPort != -1) {
            destPort = -1;
         }
      }

      super._port = destPort;
      if (super._isListenAddress) {
         if (srcPort == -1) {
            super._localPort = srcPort;
         } else {
            super._localPort = srcPort;
         }
      } else if (srcPort != -1 && destPort != -1) {
         super._localPort = srcPort;
         super._port = destPort;
      } else {
         if (destPort == -1) {
            throw new RuntimeException();
         }

         super._localPort = destPort;
      }

      super._address = StreamDatagramAddressBase.makeAddress(false, super._ipAddress != 0 ? ipAddress : null, destPort, srcPort);
   }

   public TcpAddress(String address) {
      this(address, null);
   }

   public TcpAddress(String address, String apn) {
      if (address.length() == 0) {
         super._ipAddress = -1;
         super._port = -1;
         super._localPort = -1;
         super._apnName = null;
         super._address = "";
      } else {
         this.processApnInfo(apn);
         this.parseAddress(address);
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

   @Override
   public final int getConnectionIpAddress() {
      return this.getIpAddress();
   }

   @Override
   public final int getConnectionLocalPort() {
      return this.getLocalPort();
   }

   @Override
   public final void setConnectionLocalPort(int port) {
      this.setLocalPort(port);
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
   protected final void parseAddress(String param1) throws IOException {
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
      // 043: new java/lang/StringBuffer
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
      // 05e: goto 193
      // 061: aload 1
      // 062: bipush 0
      // 063: invokevirtual java/lang/String.charAt (I)C
      // 066: bipush 47
      // 068: if_icmpeq 06e
      // 06b: goto 193
      // 06e: aload 1
      // 06f: bipush 1
      // 070: invokevirtual java/lang/String.charAt (I)C
      // 073: bipush 47
      // 075: if_icmpeq 07b
      // 078: goto 193
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
      // 09a: goto 193
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
      // 0b6: goto 190
      // 0b9: aconst_null
      // 0ba: astore 7
      // 0bc: invokestatic net/rim/device/cldc/io/dns/DNSResolverIPv4.instance ()Lnet/rim/device/cldc/io/dns/DNSResolverIPv4;
      // 0bf: aload 1
      // 0c0: bipush 2
      // 0c2: iload 3
      // 0c3: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 0c6: aload 0
      // 0c7: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._apnName Ljava/lang/String;
      // 0ca: invokevirtual net/rim/device/cldc/io/dns/DNSResolverIPv4.getAddressByHostname (Ljava/lang/String;Ljava/lang/String;)Ljava/util/Vector;
      // 0cd: astore 8
      // 0cf: aload 8
      // 0d1: ifnull 0ee
      // 0d4: aload 8
      // 0d6: invokevirtual java/util/Vector.size ()I
      // 0d9: ifle 0ee
      // 0dc: aload 8
      // 0de: aload 8
      // 0e0: invokevirtual java/util/Vector.size ()I
      // 0e3: invokestatic net/rim/device/api/crypto/RandomSource.getInt (I)I
      // 0e6: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 0e9: checkcast [B
      // 0ec: astore 7
      // 0ee: aload 7
      // 0f0: ifnonnull 0fe
      // 0f3: new java/io/IOException
      // 0f6: dup
      // 0f7: ldc_w "DNS query returned no results"
      // 0fa: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 0fd: athrow
      // 0fe: new java/lang/StringBuffer
      // 101: dup
      // 102: bipush 15
      // 104: invokespecial java/lang/StringBuffer.<init> (I)V
      // 107: astore 9
      // 109: bipush 0
      // 10a: istore 10
      // 10c: iload 10
      // 10e: bipush 4
      // 110: if_icmpge 137
      // 113: aload 9
      // 115: aload 7
      // 117: iload 10
      // 119: baload
      // 11a: sipush 255
      // 11d: iand
      // 11e: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 121: pop
      // 122: iload 10
      // 124: bipush 3
      // 126: if_icmpeq 131
      // 129: aload 9
      // 12b: bipush 46
      // 12d: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 130: pop
      // 131: iinc 10 1
      // 134: goto 10c
      // 137: aload 9
      // 139: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 13c: astore 10
      // 13e: aload 0
      // 13f: aload 10
      // 141: bipush 0
      // 142: invokestatic net/rim/device/api/io/DatagramAddressBase.parseIpAddressInt (Ljava/lang/String;I)I
      // 145: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._ipAddress I
      // 148: new java/lang/StringBuffer
      // 14b: dup
      // 14c: getstatic net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.SLASH_SLASH Ljava/lang/String;
      // 14f: invokevirtual java/lang/String.length ()I
      // 152: aload 10
      // 154: invokevirtual java/lang/String.length ()I
      // 157: iadd
      // 158: aload 1
      // 159: invokevirtual java/lang/String.length ()I
      // 15c: iload 3
      // 15d: isub
      // 15e: iadd
      // 15f: invokespecial java/lang/StringBuffer.<init> (I)V
      // 162: astore 9
      // 164: aload 9
      // 166: getstatic net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.SLASH_SLASH Ljava/lang/String;
      // 169: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 16c: pop
      // 16d: aload 9
      // 16f: aload 10
      // 171: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 174: pop
      // 175: aload 9
      // 177: aload 1
      // 178: iload 3
      // 179: aload 1
      // 17a: invokevirtual java/lang/String.length ()I
      // 17d: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 180: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 183: pop
      // 184: aload 9
      // 186: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 189: astore 1
      // 18a: aload 1
      // 18b: invokevirtual java/lang/String.length ()I
      // 18e: istore 4
      // 190: bipush 1
      // 191: istore 5
      // 193: aload 0
      // 194: aload 0
      // 195: bipush -1
      // 197: dup_x1
      // 198: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 19b: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._port I
      // 19e: aload 1
      // 19f: bipush 58
      // 1a1: bipush 2
      // 1a3: invokevirtual java/lang/String.indexOf (II)I
      // 1a6: istore 3
      // 1a7: iload 5
      // 1a9: ifne 1bd
      // 1ac: iload 5
      // 1ae: ifeq 1b4
      // 1b1: goto 29d
      // 1b4: iload 3
      // 1b5: bipush 2
      // 1b7: if_icmpeq 1bd
      // 1ba: goto 29d
      // 1bd: iload 4
      // 1bf: iload 3
      // 1c0: if_icmpgt 1c6
      // 1c3: goto 29d
      // 1c6: aload 1
      // 1c7: iload 3
      // 1c8: invokevirtual java/lang/String.charAt (I)C
      // 1cb: bipush 58
      // 1cd: if_icmpeq 1d3
      // 1d0: goto 29d
      // 1d3: iload 3
      // 1d4: bipush 1
      // 1d5: iadd
      // 1d6: istore 2
      // 1d7: aload 1
      // 1d8: iload 2
      // 1d9: invokestatic net/rim/device/api/io/DatagramAddressBase.indexOfNextDelim (Ljava/lang/String;I)I
      // 1dc: istore 3
      // 1dd: iload 3
      // 1de: iload 2
      // 1df: if_icmpgt 1ed
      // 1e2: new java/lang/IllegalArgumentException
      // 1e5: dup
      // 1e6: ldc_w "Bad DEST_PORT"
      // 1e9: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 1ec: athrow
      // 1ed: aload 1
      // 1ee: iload 2
      // 1ef: iload 3
      // 1f0: bipush 10
      // 1f2: invokestatic net/rim/device/api/io/DatagramAddressBase.parseInt (Ljava/lang/String;III)I
      // 1f5: istore 7
      // 1f7: iload 7
      // 1f9: iflt 204
      // 1fc: iload 7
      // 1fe: ldc_w 65535
      // 201: if_icmple 20f
      // 204: new java/lang/IllegalArgumentException
      // 207: dup
      // 208: ldc_w "Invalid DEST_PORT"
      // 20b: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 20e: athrow
      // 20f: iload 5
      // 211: ifeq 21d
      // 214: aload 0
      // 215: iload 7
      // 217: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._port I
      // 21a: goto 223
      // 21d: aload 0
      // 21e: iload 7
      // 220: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 223: iload 5
      // 225: ifeq 29d
      // 228: iload 4
      // 22a: iload 3
      // 22b: if_icmple 297
      // 22e: aload 1
      // 22f: iload 3
      // 230: invokevirtual java/lang/String.charAt (I)C
      // 233: bipush 59
      // 235: if_icmpne 297
      // 238: iload 3
      // 239: bipush 1
      // 23a: iadd
      // 23b: istore 2
      // 23c: aload 1
      // 23d: iload 2
      // 23e: invokestatic net/rim/device/api/io/DatagramAddressBase.indexOfNextDelim (Ljava/lang/String;I)I
      // 241: istore 3
      // 242: iload 3
      // 243: iload 2
      // 244: if_icmpgt 252
      // 247: new java/lang/IllegalArgumentException
      // 24a: dup
      // 24b: ldc_w "Bad SRC_PORT"
      // 24e: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 251: athrow
      // 252: aload 1
      // 253: iload 2
      // 254: iload 3
      // 255: bipush 10
      // 257: invokestatic net/rim/device/api/io/DatagramAddressBase.parseInt (Ljava/lang/String;III)I
      // 25a: istore 8
      // 25c: iload 8
      // 25e: iflt 269
      // 261: iload 8
      // 263: ldc_w 65535
      // 266: if_icmple 274
      // 269: new java/lang/IllegalArgumentException
      // 26c: dup
      // 26d: ldc_w "Invalid SRC_PORT"
      // 270: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 273: athrow
      // 274: aload 0
      // 275: iload 8
      // 277: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 27a: bipush 1
      // 27b: invokestatic net/rim/device/api/system/ControlledAccess.assertRRISignatures (Z)V
      // 27e: goto 29d
      // 281: astore 8
      // 283: aload 0
      // 284: bipush -1
      // 286: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 289: goto 29d
      // 28c: astore 8
      // 28e: aload 0
      // 28f: bipush -1
      // 291: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 294: goto 29d
      // 297: aload 0
      // 298: bipush -1
      // 29a: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 29d: aload 0
      // 29e: bipush 0
      // 29f: aload 0
      // 2a0: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._ipAddress I
      // 2a3: ifeq 2b0
      // 2a6: aload 0
      // 2a7: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._ipAddress I
      // 2aa: invokestatic net/rim/device/internal/system/TCPPacketHeader.IPv4IntToByteArray (I)[B
      // 2ad: goto 2b1
      // 2b0: aconst_null
      // 2b1: aload 0
      // 2b2: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._port I
      // 2b5: aload 0
      // 2b6: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase._localPort I
      // 2b9: invokestatic net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.makeAddress (Z[BII)Ljava/lang/String;
      // 2bc: putfield net/rim/device/api/io/DatagramAddressBase._address Ljava/lang/String;
      // 2bf: return
      // try (291 -> 312): 313 null
      // try (291 -> 312): 318 null
   }

   @Override
   public final void setLocalPort(int port) {
      if (port != this.getLocalPort()) {
         super._address = StreamDatagramAddressBase.makeAddress(
            false, super._ipAddress != 0 ? TCPPacketHeader.IPv4IntToByteArray(super._ipAddress) : null, super._port, port
         );
      }

      super._localPort = port;
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

      if (!(dgramAddress instanceof TcpAddress)) {
         return false;
      }

      TcpAddress address = (TcpAddress)dgramAddress;
      return (super._ipAddress == -1 || address._ipAddress == -1 || super._ipAddress == address._ipAddress)
         && (super._port == -1 || address._port == -1 || super._port == address._port)
         && (super._localPort == -1 || address._localPort == -1 || super._localPort == address._localPort)
         && (super._apnName == null || this.compareApn(address._apnName));
   }

   @Override
   public final int hashCode() {
      int hash = 7;
      hash = 31 * hash + super._ipAddress;
      hash = 31 * hash + super._port;
      hash = 31 * hash + super._localPort;
      if (super._apnName != null) {
         hash = 31 * hash + super._apnName.hashCode();
      }

      return hash;
   }
}
