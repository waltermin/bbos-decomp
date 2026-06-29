package net.rim.device.api.crypto.pgp;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.DHPrivateKey;
import net.rim.device.api.crypto.DSAPrivateKey;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.RSAPrivateKey;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.crypto.pgp.PGPPrivateKeyPacket;
import net.rim.device.internal.crypto.pgp.PGPUtilities;
import net.rim.vm.UnGroupable;

public class PGPPrivateKey implements PrivateKey, UnGroupable {
   private byte[] _parentKeyID;
   private byte[][] _subKeyIDs;
   private PGPPrivateKeyPacket[] _packets;
   private PGPPasswordTicket _ticket;
   private String[] _ticketPromptParameters;

   public PrivateKey getParentKey() {
      if (this._ticket == null) {
         this._ticket = new PGPPasswordTicket(0, this._ticketPromptParameters);
      }

      return this._packets[0].getPrivateKey(this._ticket);
   }

   public PrivateKey[] getSubKeys() {
      if (this._ticket == null) {
         this._ticket = new PGPPasswordTicket(0, this._ticketPromptParameters);
      }

      int numPackets = this._packets.length;
      PrivateKey[] subKeys = new Object[numPackets - 1];

      for (int i = 1; i < numPackets; i++) {
         subKeys[i - 1] = this._packets[i].getPrivateKey(this._ticket);
      }

      return subKeys;
   }

   public byte[] encode() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/pgp/PGPPrivateKey._ticket Lnet/rim/device/api/crypto/pgp/PGPPasswordTicket;
      // 04: ifnonnull 17
      // 07: aload 0
      // 08: new net/rim/device/api/crypto/pgp/PGPPasswordTicket
      // 0b: dup
      // 0c: bipush 0
      // 0d: aload 0
      // 0e: getfield net/rim/device/api/crypto/pgp/PGPPrivateKey._ticketPromptParameters [Ljava/lang/String;
      // 11: invokespecial net/rim/device/api/crypto/pgp/PGPPasswordTicket.<init> (I[Ljava/lang/String;)V
      // 14: putfield net/rim/device/api/crypto/pgp/PGPPrivateKey._ticket Lnet/rim/device/api/crypto/pgp/PGPPasswordTicket;
      // 17: new java/lang/Object
      // 1a: dup
      // 1b: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 1e: astore 1
      // 1f: new java/lang/Object
      // 22: dup
      // 23: aload 1
      // 24: invokespecial java/io/DataOutputStream.<init> (Ljava/io/OutputStream;)V
      // 27: astore 2
      // 28: bipush 5
      // 2a: istore 3
      // 2b: aload 0
      // 2c: getfield net/rim/device/api/crypto/pgp/PGPPrivateKey._packets [Lnet/rim/device/internal/crypto/pgp/PGPPrivateKeyPacket;
      // 2f: arraylength
      // 30: istore 4
      // 32: bipush 0
      // 33: istore 5
      // 35: iload 5
      // 37: iload 4
      // 39: if_icmpge be
      // 3c: aconst_null
      // 3d: astore 6
      // 3f: aload 0
      // 40: getfield net/rim/device/api/crypto/pgp/PGPPrivateKey._packets [Lnet/rim/device/internal/crypto/pgp/PGPPrivateKeyPacket;
      // 43: iload 5
      // 45: aaload
      // 46: invokevirtual net/rim/device/internal/crypto/pgp/PGPPrivateKeyPacket.getSymmetricKeyAlgorithm ()I
      // 49: ifeq 99
      // 4c: new java/lang/Object
      // 4f: dup
      // 50: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 53: astore 7
      // 55: aload 0
      // 56: getfield net/rim/device/api/crypto/pgp/PGPPrivateKey._packets [Lnet/rim/device/internal/crypto/pgp/PGPPrivateKeyPacket;
      // 59: iload 5
      // 5b: aaload
      // 5c: invokevirtual net/rim/device/internal/crypto/pgp/PGPPacket.getEncoding ()[B
      // 5f: astore 8
      // 61: aload 0
      // 62: getfield net/rim/device/api/crypto/pgp/PGPPrivateKey._packets [Lnet/rim/device/internal/crypto/pgp/PGPPrivateKeyPacket;
      // 65: iload 5
      // 67: aaload
      // 68: invokevirtual net/rim/device/internal/crypto/pgp/PGPPublicKeyPacket.getPublicKeyLength ()I
      // 6b: istore 9
      // 6d: aload 7
      // 6f: aload 8
      // 71: bipush 0
      // 72: iload 9
      // 74: invokevirtual java/io/ByteArrayOutputStream.write ([BII)V
      // 77: aload 7
      // 79: bipush 0
      // 7a: invokevirtual java/io/ByteArrayOutputStream.write (I)V
      // 7d: aload 0
      // 7e: aload 0
      // 7f: getfield net/rim/device/api/crypto/pgp/PGPPrivateKey._packets [Lnet/rim/device/internal/crypto/pgp/PGPPrivateKeyPacket;
      // 82: iload 5
      // 84: aaload
      // 85: aload 7
      // 87: invokespecial net/rim/device/api/crypto/pgp/PGPPrivateKey.writePrivateKeyDataAndChecksum (Lnet/rim/device/internal/crypto/pgp/PGPPrivateKeyPacket;Ljava/io/OutputStream;)V
      // 8a: aload 7
      // 8c: invokevirtual java/io/ByteArrayOutputStream.close ()V
      // 8f: aload 7
      // 91: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 94: astore 6
      // 96: goto a5
      // 99: aload 0
      // 9a: getfield net/rim/device/api/crypto/pgp/PGPPrivateKey._packets [Lnet/rim/device/internal/crypto/pgp/PGPPrivateKeyPacket;
      // 9d: iload 5
      // 9f: aaload
      // a0: invokevirtual net/rim/device/internal/crypto/pgp/PGPPacket.getEncoding ()[B
      // a3: astore 6
      // a5: aload 2
      // a6: iload 3
      // a7: aload 6
      // a9: arraylength
      // aa: bipush 4
      // ac: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.writeTagAndLength (Ljava/io/OutputStream;III)V
      // af: aload 2
      // b0: aload 6
      // b2: invokevirtual java/io/OutputStream.write ([B)V
      // b5: bipush 7
      // b7: istore 3
      // b8: iinc 5 1
      // bb: goto 35
      // be: aload 2
      // bf: invokevirtual java/io/DataOutputStream.close ()V
      // c2: aload 1
      // c3: invokevirtual java/io/ByteArrayOutputStream.close ()V
      // c6: aload 1
      // c7: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // ca: areturn
      // cb: astore 1
      // cc: new java/lang/Object
      // cf: dup
      // d0: aload 1
      // d1: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // d4: invokespecial net/rim/device/api/crypto/CryptoTokenException.<init> (Ljava/lang/String;)V
      // d7: athrow
      // d8: astore 1
      // d9: new java/lang/Object
      // dc: dup
      // dd: aload 1
      // de: invokevirtual net/rim/device/api/crypto/pgp/PGPEncodingException.toString ()Ljava/lang/String;
      // e1: invokespecial net/rim/device/api/crypto/CryptoTokenException.<init> (Ljava/lang/String;)V
      // e4: athrow
      // e5: astore 1
      // e6: new java/lang/Object
      // e9: dup
      // ea: aload 1
      // eb: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreCancelException.toString ()Ljava/lang/String;
      // ee: invokespecial net/rim/device/api/crypto/CryptoTokenException.<init> (Ljava/lang/String;)V
      // f1: athrow
      // try (0 -> 101): 102 null
      // try (0 -> 101): 109 net/rim/device/api/crypto/pgp/PGPEncodingException
      // try (0 -> 101): 116 null
   }

   public byte[] getKeyID() {
      return this._parentKeyID;
   }

   public byte[][] getSubKeyIDs() {
      return this._subKeyIDs;
   }

   public PrivateKey getSubKey(int index) {
      if (index >= 0 && index < this._packets.length - 1) {
         PrivateKey[] subKeys = this.getSubKeys();
         return subKeys[index];
      } else {
         throw new Object();
      }
   }

   public PrivateKey getPrivateKey(byte[] keyID) {
      if (keyID != null && keyID.length > 0) {
         if (Arrays.equals(keyID, this._parentKeyID)) {
            return this.getParentKey();
         }

         int numSubKeys = this._subKeyIDs.length;

         for (int i = 0; i < numSubKeys; i++) {
            if (Arrays.equals(keyID, this._subKeyIDs[i])) {
               return this.getSubKey(i);
            }
         }

         return null;
      } else {
         throw new Object();
      }
   }

   public void clearPasswordTicket() {
      this._ticket = null;
   }

   public String[] getTicketPromptParameters() {
      return this._ticketPromptParameters;
   }

   public PGPPrivateKeyPacket[] getPackets() {
      return this._packets;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void verify() {
      boolean var8 = false /* VF: Semaphore variable */;
      boolean var12 = false /* VF: Semaphore variable */;

      try {
         label63: {
            try {
               var12 = true;
               var8 = true;
               this.getParentKey().verify();
               PrivateKey[] e = this.getSubKeys();
               int length = e.length;

               for (int i = 0; i < length; i++) {
                  e[i].verify();
               }

               var8 = false;
               var12 = false;
               break label63;
            } catch (PGPEncodingException var13) {
               var12 = false;
            } finally {
               if (var12) {
                  throw new Object();
               }
            }

            throw new Object();
         }
      } finally {
         if (var8) {
            this.clearPasswordTicket();
         }
      }

      this.clearPasswordTicket();
   }

   @Override
   public CryptoSystem getCryptoSystem() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.getParentKey ()Lnet/rim/device/api/crypto/PrivateKey;
      // 04: invokeinterface net/rim/device/api/crypto/PrivateKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 09: areturn
      // 0a: astore 1
      // 0b: aconst_null
      // 0c: areturn
      // 0d: astore 1
      // 0e: aconst_null
      // 0f: areturn
      // 10: astore 1
      // 11: aconst_null
      // 12: areturn
      // try (0 -> 3): 4 net/rim/device/api/crypto/pgp/PGPEncodingException
      // try (0 -> 3): 7 null
      // try (0 -> 3): 10 null
   }

   @Override
   public String getAlgorithm() {
      return "PGP";
   }

   public PGPPrivateKey(PGPPrivateKeyPacket[] packets, String pgpKeyDisplayName) {
      if (packets != null && packets.length >= 1) {
         this._packets = packets;
         this._parentKeyID = this._packets[0].getKeyID();
         int length = this._packets.length;
         this._subKeyIDs = new byte[length - 1][];

         for (int i = 1; i < length; i++) {
            this._subKeyIDs[i - 1] = this._packets[i].getKeyID();
         }

         if (pgpKeyDisplayName != null) {
            this._ticketPromptParameters = new Object[]{pgpKeyDisplayName};
         }
      } else {
         throw new Object();
      }
   }

   public PGPPrivateKey(PGPPrivateKeyPacket[] packets) {
      this(packets, null);
   }

   private void writePrivateKeyDataAndChecksum(PGPPrivateKeyPacket packet, OutputStream output) {
      PrivateKey privateKey = packet.getPrivateKey(this._ticket);
      ByteArrayOutputStream checksumStream = (ByteArrayOutputStream)(new Object());
      if (!(privateKey instanceof Object)) {
         if (!(privateKey instanceof Object)) {
            if (!(privateKey instanceof Object)) {
               throw new Object();
            }

            DHPrivateKey dhKey = (DHPrivateKey)privateKey;
            PGPUtilities.writeMPI(checksumStream, dhKey.getPrivateKeyData());
         } else {
            DSAPrivateKey dsaKey = (DSAPrivateKey)privateKey;
            PGPUtilities.writeMPI(checksumStream, dsaKey.getPrivateKeyData());
         }
      } else {
         RSAPrivateKey rsaKey = (RSAPrivateKey)privateKey;
         byte[] D = rsaKey.getD();
         byte[] P = rsaKey.getP();
         byte[] Q = rsaKey.getQ();
         if (D == null || P == null || Q == null) {
            throw new Object();
         }

         PGPUtilities.writeMPI(checksumStream, D);
         PGPUtilities.writeMPI(checksumStream, P);
         PGPUtilities.writeMPI(checksumStream, Q);
         byte[] QInvModP = rsaKey.getQInvModP();
         if (QInvModP != null) {
            PGPUtilities.writeMPI(checksumStream, QInvModP);
         }
      }

      checksumStream.close();
      byte[] privateKeyData = checksumStream.toByteArray();
      int length = privateKeyData.length;
      int checksum = 0;

      for (int i = 0; i < length; i++) {
         checksum += privateKeyData[i] & 255;
      }

      checksum &= 65535;
      output.write(privateKeyData);
      output.write((byte)(checksum >> 8));
      output.write((byte)checksum);
   }
}
