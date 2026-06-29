package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.crypto.InvalidCryptoSystemException;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.InvalidKeyException;
import net.rim.device.api.crypto.Key;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateFactory;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.encoder.EncodedKey;
import net.rim.device.api.crypto.encoder.PrivateKeyEncoder;
import net.rim.device.api.crypto.encoder.SymmetricKeyEncoder;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.vm.Array;
import net.rim.vm.Memory;
import net.rim.vm.UnGroupable;

public final class RIMKeyStoreData implements KeyStoreData, Persistable, SyncObject, UnGroupable {
   protected RIMKeyStoreData$Payload _payload;
   boolean _syncObjectDirty = true;

   final void ungroupData() {
      if (Memory.isObjectInGroup(this._payload)) {
         this._payload = (RIMKeyStoreData$Payload)Memory.expandGroup(this._payload);
      }
   }

   final void groupData() {
      Memory.createGroup(this._payload);
   }

   final void removeCertificate() {
      if (this._payload._encoding != null) {
         CertificateRepository.getInstance().removeCertificate(this._payload._certificate);
      }
   }

   @Override
   public final int getUID() {
      return this._payload._uid;
   }

   final byte[] getHash(Key param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnonnull 06
      // 04: aconst_null
      // 05: areturn
      // 06: aload 1
      // 07: dup
      // 08: instanceof net/rim/device/api/crypto/PrivateKey
      // 0b: ifne 12
      // 0e: pop
      // 0f: goto 23
      // 12: checkcast net/rim/device/api/crypto/PrivateKey
      // 15: aload 0
      // 16: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 19: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._privateKeyEncodingAlgorithm Ljava/lang/String;
      // 1c: invokestatic net/rim/device/api/crypto/encoder/PrivateKeyEncoder.encode (Lnet/rim/device/api/crypto/PrivateKey;Ljava/lang/String;)Lnet/rim/device/api/crypto/encoder/EncodedKey;
      // 1f: astore 2
      // 20: goto 48
      // 23: aload 1
      // 24: dup
      // 25: instanceof net/rim/device/api/crypto/SymmetricKey
      // 28: ifne 2f
      // 2b: pop
      // 2c: goto 40
      // 2f: checkcast net/rim/device/api/crypto/SymmetricKey
      // 32: aload 0
      // 33: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 36: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._symmetricKeyEncodingAlgorithm Ljava/lang/String;
      // 39: invokestatic net/rim/device/api/crypto/encoder/SymmetricKeyEncoder.encode (Lnet/rim/device/api/crypto/SymmetricKey;Ljava/lang/String;)Lnet/rim/device/api/crypto/encoder/EncodedKey;
      // 3c: astore 2
      // 3d: goto 48
      // 40: new java/lang/RuntimeException
      // 43: dup
      // 44: invokespecial java/lang/RuntimeException.<init> ()V
      // 47: athrow
      // 48: aload 2
      // 49: invokevirtual net/rim/device/api/crypto/encoder/EncodedKey.getEncodedKey ()[B
      // 4c: astore 3
      // 4d: new net/rim/device/api/crypto/SHA1Digest
      // 50: dup
      // 51: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 54: astore 4
      // 56: aload 4
      // 58: aload 3
      // 59: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 5c: aload 4
      // 5e: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ()[B
      // 61: areturn
      // 62: astore 3
      // 63: aconst_null
      // 64: areturn
      // 65: astore 3
      // 66: aconst_null
      // 67: areturn
      // try (4 -> 46): 47 null
      // try (4 -> 46): 50 null
   }

   public final String getPrivateKeyEncodingAlgorithm() {
      return this._payload._privateKeyEncodingAlgorithm;
   }

   public final boolean setSecurityLevel(int param1, KeyStoreTicket param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getSecurityLevel ()I
      // 004: istore 3
      // 005: iload 1
      // 006: iload 3
      // 007: if_icmpne 00c
      // 00a: bipush 1
      // 00b: ireturn
      // 00c: invokestatic net/rim/device/api/crypto/keystore/KeyStoreManager.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStoreManager;
      // 00f: aload 0
      // 010: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManager.getKeyStore (Lnet/rim/device/api/crypto/keystore/KeyStoreData;)Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 013: astore 4
      // 015: aload 4
      // 017: checkcast net/rim/device/api/crypto/keystore/RIMKeyStore
      // 01a: astore 5
      // 01c: aload 0
      // 01d: sipush 7025
      // 020: invokestatic net/rim/device/api/crypto/keystore/KeyStoreResources.getString (I)Ljava/lang/String;
      // 023: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getTicket (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;
      // 026: astore 6
      // 028: aconst_null
      // 029: astore 7
      // 02b: aload 0
      // 02c: aload 0
      // 02d: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 030: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._privateKey Ljava/lang/Object;
      // 033: ifnull 03a
      // 036: bipush 1
      // 037: goto 03b
      // 03a: bipush 0
      // 03b: aload 6
      // 03d: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.getKey (ZLnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/Key;
      // 040: astore 7
      // 042: goto 04c
      // 045: astore 8
      // 047: goto 04c
      // 04a: astore 8
      // 04c: aload 7
      // 04e: ifnonnull 053
      // 051: bipush 0
      // 052: ireturn
      // 053: aconst_null
      // 054: astore 8
      // 056: aload 0
      // 057: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 05a: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._privateKey Ljava/lang/Object;
      // 05d: ifnull 06c
      // 060: aload 0
      // 061: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 064: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._privateKeyEncodingAlgorithm Ljava/lang/String;
      // 067: astore 8
      // 069: goto 099
      // 06c: aload 0
      // 06d: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 070: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._symmetricKey Ljava/lang/Object;
      // 073: ifnull 082
      // 076: aload 0
      // 077: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 07a: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._symmetricKeyEncodingAlgorithm Ljava/lang/String;
      // 07d: astore 8
      // 07f: goto 099
      // 082: bipush 0
      // 083: istore 9
      // 085: aload 0
      // 086: bipush 1
      // 087: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._syncObjectDirty Z
      // 08a: aload 0
      // 08b: aload 0
      // 08c: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getLabel ()Ljava/lang/String;
      // 08f: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.computeHashCode (Ljava/lang/String;)V
      // 092: aload 0
      // 093: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.groupData ()V
      // 096: iload 9
      // 098: ireturn
      // 099: aload 0
      // 09a: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.ungroupData ()V
      // 09d: aload 0
      // 09e: aload 7
      // 0a0: aload 8
      // 0a2: iload 1
      // 0a3: aload 2
      // 0a4: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.savePrivateKey (Lnet/rim/device/api/crypto/Key;Ljava/lang/String;ILnet/rim/device/api/crypto/keystore/KeyStoreTicket;)V
      // 0a7: aload 0
      // 0a8: bipush 1
      // 0a9: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._syncObjectDirty Z
      // 0ac: aload 0
      // 0ad: aload 0
      // 0ae: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getLabel ()Ljava/lang/String;
      // 0b1: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.computeHashCode (Ljava/lang/String;)V
      // 0b4: aload 0
      // 0b5: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.groupData ()V
      // 0b8: goto 14e
      // 0bb: astore 8
      // 0bd: bipush 0
      // 0be: istore 9
      // 0c0: aload 0
      // 0c1: bipush 1
      // 0c2: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._syncObjectDirty Z
      // 0c5: aload 0
      // 0c6: aload 0
      // 0c7: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getLabel ()Ljava/lang/String;
      // 0ca: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.computeHashCode (Ljava/lang/String;)V
      // 0cd: aload 0
      // 0ce: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.groupData ()V
      // 0d1: iload 9
      // 0d3: ireturn
      // 0d4: astore 8
      // 0d6: bipush 0
      // 0d7: istore 9
      // 0d9: aload 0
      // 0da: bipush 1
      // 0db: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._syncObjectDirty Z
      // 0de: aload 0
      // 0df: aload 0
      // 0e0: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getLabel ()Ljava/lang/String;
      // 0e3: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.computeHashCode (Ljava/lang/String;)V
      // 0e6: aload 0
      // 0e7: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.groupData ()V
      // 0ea: iload 9
      // 0ec: ireturn
      // 0ed: astore 8
      // 0ef: bipush 0
      // 0f0: istore 9
      // 0f2: aload 0
      // 0f3: bipush 1
      // 0f4: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._syncObjectDirty Z
      // 0f7: aload 0
      // 0f8: aload 0
      // 0f9: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getLabel ()Ljava/lang/String;
      // 0fc: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.computeHashCode (Ljava/lang/String;)V
      // 0ff: aload 0
      // 100: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.groupData ()V
      // 103: iload 9
      // 105: ireturn
      // 106: astore 8
      // 108: bipush 0
      // 109: istore 9
      // 10b: aload 0
      // 10c: bipush 1
      // 10d: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._syncObjectDirty Z
      // 110: aload 0
      // 111: aload 0
      // 112: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getLabel ()Ljava/lang/String;
      // 115: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.computeHashCode (Ljava/lang/String;)V
      // 118: aload 0
      // 119: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.groupData ()V
      // 11c: iload 9
      // 11e: ireturn
      // 11f: astore 8
      // 121: bipush 0
      // 122: istore 9
      // 124: aload 0
      // 125: bipush 1
      // 126: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._syncObjectDirty Z
      // 129: aload 0
      // 12a: aload 0
      // 12b: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getLabel ()Ljava/lang/String;
      // 12e: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.computeHashCode (Ljava/lang/String;)V
      // 131: aload 0
      // 132: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.groupData ()V
      // 135: iload 9
      // 137: ireturn
      // 138: astore 10
      // 13a: aload 0
      // 13b: bipush 1
      // 13c: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._syncObjectDirty Z
      // 13f: aload 0
      // 140: aload 0
      // 141: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getLabel ()Ljava/lang/String;
      // 144: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.computeHashCode (Ljava/lang/String;)V
      // 147: aload 0
      // 148: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.groupData ()V
      // 14b: aload 10
      // 14d: athrow
      // 14e: aload 5
      // 150: ifnull 159
      // 153: aload 5
      // 155: aload 0
      // 156: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStore.reinitializeIndices (Lnet/rim/device/api/crypto/keystore/KeyStoreData;)V
      // 159: bipush 1
      // 15a: ireturn
      // try (22 -> 33): 34 null
      // try (22 -> 33): 36 net/rim/device/api/crypto/keystore/KeyStoreDecodeException
      // try (41 -> 63): 92 null
      // try (74 -> 82): 92 null
      // try (41 -> 63): 106 net/rim/device/api/crypto/InvalidKeyEncodingException
      // try (74 -> 82): 106 net/rim/device/api/crypto/InvalidKeyEncodingException
      // try (41 -> 63): 120 null
      // try (74 -> 82): 120 null
      // try (41 -> 63): 134 null
      // try (74 -> 82): 134 null
      // try (41 -> 63): 148 null
      // try (74 -> 82): 148 null
      // try (41 -> 63): 162 null
      // try (74 -> 82): 162 null
      // try (92 -> 95): 162 null
      // try (106 -> 109): 162 null
      // try (120 -> 123): 162 null
      // try (134 -> 137): 162 null
      // try (148 -> 151): 162 null
      // try (162 -> 163): 162 null
   }

   @Override
   public final void setLabel(String newLabel) {
      String oldLabel = this._payload.getLabel();
      String defaultLabel = newLabel != null ? newLabel : oldLabel;
      newLabel = this.promptForLabel(defaultLabel);
      if (!StringUtilities.strEqual(newLabel, oldLabel)) {
         KeyStore keyStore = KeyStoreManager.getInstance().getKeyStore(this);
         RIMKeyStore rimKeyStore = (RIMKeyStore)keyStore;
         if (rimKeyStore != null) {
            rimKeyStore.getTicket(KeyStoreResources.getString(7015));
         }

         this.ungroupData();
         this._payload.setLabel(newLabel);
         this.computeHashCode(newLabel);
         this.groupData();
         if (rimKeyStore != null) {
            rimKeyStore.reinitializeIndices(this);
         }

         this._syncObjectDirty = true;
      }
   }

   @Override
   public final Certificate getCertificate() {
      if (this._payload._encoding == null) {
         return null;
      }

      Certificate certificate = CertificateRepository.getInstance().getCertificate(this._payload._certificate);
      if (certificate == null) {
         try {
            return CertificateFactory.getInstance(this._payload._type, this._payload._encoding);
         } finally {
            ;
         }
      } else {
         return certificate;
      }
   }

   @Override
   public final String getLabel() {
      return this._payload.getLabel();
   }

   @Override
   public final void changePassword() {
      if (this.isPrivateKeySet() || this.isSymmetricKeySet()) {
         int currentPasswordVersion = KeyStoreManagerHelper.getInstance().getPasswordVersion();
         if (this._payload._privateKey instanceof byte[]) {
            try {
               int currentSecurityLevel = KeyStoreUtilitiesInternal.getSecurityLevel((byte[])this._payload._privateKey);
               int securityLevel = KeyStoreUtilitiesInternal.getAppropriateSecurityLevel(currentSecurityLevel, this.getKeyUsage());
               int passwordVersion = KeyStoreUtilitiesInternal.getPasswordVersion((byte[])this._payload._privateKey);
               if (currentPasswordVersion == passwordVersion && currentSecurityLevel == securityLevel) {
                  return;
               }

               KeyStorePasswordManager passwordManager = KeyStorePasswordManager.getInstance();
               this._payload._privateKey = passwordManager.decryptReEncrypt(securityLevel, (byte[])this._payload._privateKey);
               this._syncObjectDirty = true;
            } catch (KeyStoreDecodeException e) {
               throw new KeyStoreDecodeRuntimeException();
            }
         }

         if (this._payload._symmetricKey instanceof byte[]) {
            try {
               int currentSecurityLevel = KeyStoreUtilitiesInternal.getSecurityLevel((byte[])this._payload._symmetricKey);
               int securityLevel = KeyStoreUtilitiesInternal.getAppropriateSecurityLevel(currentSecurityLevel, this.getKeyUsage());
               int passwordVersion = KeyStoreUtilitiesInternal.getPasswordVersion((byte[])this._payload._symmetricKey);
               if (currentPasswordVersion != passwordVersion || currentSecurityLevel != securityLevel) {
                  KeyStorePasswordManager passwordManager = KeyStorePasswordManager.getInstance();
                  this._payload._symmetricKey = passwordManager.decryptReEncrypt(securityLevel, (byte[])this._payload._symmetricKey);
                  this._syncObjectDirty = true;
               }
            } catch (KeyStoreDecodeException e) {
               throw new KeyStoreDecodeRuntimeException();
            }
         }
      }
   }

   @Override
   public final int queryKeyUsage(long purpose) {
      if (this._payload._encoding != null) {
         Certificate certificate = this.getCertificate();
         if (certificate != null) {
            return certificate.queryKeyUsage(purpose);
         }
      }

      return (this._payload._keyUsage & purpose) != 0 ? 1 : 0;
   }

   @Override
   public final byte[][] getAssociatedData(long association) {
      return this._payload._associations == null ? (byte[][])null : (byte[][])this._payload._associations.get(association);
   }

   @Override
   public final AssociatedData[] getAssociatedData() {
      if (this._payload._associations == null) {
         return null;
      }

      AssociatedData[] dataArray = new AssociatedData[this._payload._associations.size()];
      int counter = 0;
      LongEnumeration enumeration = this._payload._associations.keys();

      while (enumeration.hasMoreElements()) {
         long association = enumeration.nextElement();
         byte[][] data = (byte[][])this._payload._associations.get(association);
         dataArray[counter++] = new AssociatedData(association, data);
      }

      return dataArray;
   }

   @Override
   public final KeyStoreDataTicket getTicket() {
      return this.protectedDataProvideAuthentication() ? null : new RIMKeyStoreDataTicket(null, this);
   }

   @Override
   public final KeyStoreDataTicket getTicket(String prompt) {
      return this.protectedDataProvideAuthentication() ? null : new RIMKeyStoreDataTicket(prompt, this);
   }

   @Override
   public final boolean checkTicket(KeyStoreDataTicket ticket) {
      if (ticket == null) {
         int securityLevel = this.getSecurityLevel();
         return securityLevel == 1 || securityLevel == 0;
      } else {
         return !(ticket instanceof RIMKeyStoreDataTicket) ? false : ((RIMKeyStoreDataTicket)ticket).access(this);
      }
   }

   @Override
   public final int getSecurityLevel() {
      if (!this.isPrivateKeySet() && !this.isSymmetricKeySet()) {
         return 0;
      }

      try {
         if (!(this._payload._privateKey instanceof byte[])) {
            return !(this._payload._symmetricKey instanceof byte[]) ? 2 : KeyStoreUtilitiesInternal.getSecurityLevel((byte[])this._payload._symmetricKey);
         } else {
            return KeyStoreUtilitiesInternal.getSecurityLevel((byte[])this._payload._privateKey);
         }
      } catch (KeyStoreDecodeException e) {
         return 0;
      }
   }

   @Override
   public final int getPasswordVersion() {
      try {
         if (!(this._payload._privateKey instanceof byte[])) {
            return !(this._payload._symmetricKey instanceof byte[]) ? -1 : KeyStoreUtilitiesInternal.getPasswordVersion((byte[])this._payload._symmetricKey);
         } else {
            return KeyStoreUtilitiesInternal.getPasswordVersion((byte[])this._payload._privateKey);
         }
      } catch (KeyStoreDecodeException e) {
         return -1;
      }
   }

   @Override
   public final SymmetricKey getSymmetricKey(KeyStoreDataTicket ticket) {
      return (SymmetricKey)this.getKey(false, ticket);
   }

   @Override
   public final PublicKey getPublicKey() {
      if (this._payload._publicKey == null) {
         Certificate certificate = this.getCertificate();
         if (certificate != null) {
            try {
               return certificate.getPublicKey();
            } catch (InvalidCryptoSystemException e) {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return this._payload._publicKey;
      }
   }

   @Override
   public final PrivateKey getPrivateKey(KeyStoreDataTicket ticket) {
      return (PrivateKey)this.getKey(true, ticket);
   }

   @Override
   public final boolean isSymmetricKeySet() {
      return this._payload._symmetricKey != null;
   }

   @Override
   public final boolean isPrivateKeySet() {
      return this._payload._privateKey != null;
   }

   private final long getKeyUsage() {
      if (this._payload._encoding != null) {
         Certificate certificate = this.getCertificate();
         if (certificate != null) {
            long keyUsage = 0;

            for (int i = 0; i < 15; i++) {
               long currentUsage = (long)1 << i;
               if (certificate.queryKeyUsage(currentUsage) != 0) {
                  keyUsage |= currentUsage;
               }
            }

            return keyUsage;
         }
      }

      return this._payload._keyUsage;
   }

   private final KeyStorePasswordTicket getPasswordTicket(KeyStoreDataTicket ticket) {
      return !(ticket instanceof RIMKeyStoreDataTicket) ? null : ((RIMKeyStoreDataTicket)ticket).getPasswordTicket();
   }

   private final KeyStorePasswordTicket getPasswordTicket(KeyStoreTicket ticket) {
      if (!(ticket instanceof RIMKeyStoreTicket)) {
         return !(ticket instanceof SyncableRIMKeyStoreTicket) ? null : ((SyncableRIMKeyStoreTicket)ticket).getPasswordTicket();
      } else {
         return ((RIMKeyStoreTicket)ticket).getPasswordTicket();
      }
   }

   private final boolean protectedDataProvideAuthentication() {
      if (!(this._payload._privateKey instanceof PrivateKey)) {
         if (!(this._payload._symmetricKey instanceof SymmetricKey)) {
            return false;
         }

         SymmetricKey key = (SymmetricKey)this._payload._symmetricKey;
         return key.getSymmetricCryptoToken().providesUserAuthentication();
      } else {
         PrivateKey key = (PrivateKey)this._payload._privateKey;
         return key.getCryptoSystem().getAsymmetricCryptoToken().providesUserAuthentication();
      }
   }

   @Override
   public final int hashCode() {
      return this._payload._hashCode;
   }

   private final void computeHashCode(String label) {
      if (this._payload._encoding != null) {
         this._payload._hashCode = this._payload._hashCode ^ HashCodeCalculator.getCRC32(this._payload._encoding);
      }

      if (this._payload._publicKey != null) {
         this._payload._hashCode = this._payload._hashCode ^ this._payload._publicKey.hashCode();
      }

      if (this._payload._privateKey instanceof PrivateKey) {
         this._payload._hashCode = this._payload._hashCode ^ ((PrivateKey)this._payload._privateKey).hashCode();
      } else if (this._payload._privateKey instanceof byte[]) {
         this._payload._hashCode = this._payload._hashCode ^ HashCodeCalculator.getCRC32((byte[])this._payload._privateKey);
      }

      if (this._payload._symmetricKey instanceof SymmetricKey) {
         this._payload._hashCode = this._payload._hashCode ^ ((SymmetricKey)this._payload._symmetricKey).hashCode();
      } else if (this._payload._symmetricKey instanceof byte[]) {
         this._payload._hashCode = this._payload._hashCode ^ HashCodeCalculator.getCRC32((byte[])this._payload._symmetricKey);
      }

      if (label != null) {
         this._payload._hashCode = this._payload._hashCode ^ label.hashCode();
      }

      this._payload._hashCode = (int)(this._payload._hashCode ^ this._payload._keyUsage >> 4);
      this._payload._hashCode = (int)(this._payload._hashCode ^ this._payload._keyUsage);
   }

   RIMKeyStoreData(
      AssociatedData[] associatedData,
      String label,
      byte[] symmetricKey,
      String symmetricKeyEncodingAlgorithm,
      int uid,
      int[] hashes,
      long[] indices,
      long[] notUsed
   ) {
      this._payload = new RIMKeyStoreData$Payload();
      this.createAssociations(associatedData);
      this._payload._uid = uid;
      this._payload._symmetricKey = symmetricKey;
      this._payload._symmetricKeyEncodingAlgorithm = symmetricKeyEncodingAlgorithm;
      this._payload._hashes = hashes;
      this._payload._indices = indices;
      this._payload._notUsed = notUsed;
      if (label == null) {
         label = KeyStoreResources.getString(8);
      }

      this._payload.setLabel(label);
      this.computeHashCode(label);
   }

   RIMKeyStoreData(String label, byte[] encoding, String type, CertificateStatus certStatus) {
      this._payload = new RIMKeyStoreData$Payload();
      this._payload._certificate = CertificateRepository.getInstance().addCertificate(encoding, type);
      if (Memory.isObjectInGroup(encoding)) {
         encoding = Arrays.copy(encoding);
      }

      this._payload._encoding = encoding;
      this._payload._type = type;
      this._payload._uid = CRC32.update(0, encoding);
      this.setCertificateStatus(null, encoding, type, certStatus, true);
      this._payload.setLabel(label);
      this.computeHashCode(label);
   }

   private final void createAssociations(AssociatedData[] associatedData) {
      this._payload._associations = new LongHashtable();
      if (associatedData != null) {
         long association = 0;

         for (int i = 0; i < associatedData.length; i++) {
            if (associatedData[i] != null) {
               association = associatedData[i].getAssociation();
               byte[][] data = (byte[][])this._payload._associations.get(association);
               byte[][] newData = associatedData[i].getData();
               if (data != null) {
                  int offset = data.length;
                  Array.resize(data, data.length + newData.length);
                  System.arraycopy(newData, 0, data, offset, newData.length);
                  this._payload._associations.put(association, data);
               } else {
                  this._payload._associations.put(association, newData);
               }
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void savePrivateKey(Key key, String encodingAlgorithm, int securityLevel, KeyStoreTicket ticket) {
      if (key != null) {
         if (encodingAlgorithm == null) {
            encodingAlgorithm = "KeyStore";
         }

         boolean var9 = false /* VF: Semaphore variable */;

         EncodedKey encodedKey;
         try {
            var9 = true;
            if (!(key instanceof PrivateKey)) {
               if (!(key instanceof SymmetricKey)) {
                  throw new RuntimeException();
               }

               encodedKey = SymmetricKeyEncoder.encode((SymmetricKey)key, encodingAlgorithm);
               this._payload._symmetricKeyEncodingAlgorithm = encodingAlgorithm;
               var9 = false;
            } else {
               encodedKey = PrivateKeyEncoder.encode((PrivateKey)key, encodingAlgorithm);
               this._payload._privateKeyEncodingAlgorithm = encodingAlgorithm;
               var9 = false;
            }
         } finally {
            if (var9) {
               if (key instanceof PrivateKey) {
                  this._payload._privateKey = key;
                  this._payload._privateKeyEncodingAlgorithm = null;
                  return;
               }

               if (key instanceof SymmetricKey) {
                  this._payload._symmetricKey = key;
                  this._payload._symmetricKeyEncodingAlgorithm = null;
               }

               return;
            }
         }

         byte[] keyEncoding = encodedKey.getEncodedKey();
         KeyStorePasswordManager passwordManager = KeyStorePasswordManager.getInstance();
         if (key instanceof PrivateKey) {
            this._payload._privateKey = passwordManager.encrypt(securityLevel, keyEncoding, this._payload.getLabel(), this.getPasswordTicket(ticket));
            return;
         }

         if (key instanceof SymmetricKey) {
            this._payload._symmetricKey = passwordManager.encrypt(securityLevel, keyEncoding, this._payload.getLabel(), this.getPasswordTicket(ticket));
         }
      }
   }

   private final void setCertificateStatus(
      Certificate certificate, byte[] certificateEncoding, String certificateType, CertificateStatus certificateStatus, boolean silentAccess
   ) {
      if (certificateStatus == null) {
         if (certificate != null) {
            certificateStatus = certificate.getStatus();
         }

         if (certificateStatus == null) {
            certificateStatus = new CertificateStatus();
         }
      }

      try {
         CertificateStatusManager.getInstance().setStatus(certificate, certificateEncoding, certificateType, certificateStatus, null, silentAccess);
      } catch (InvalidTimeException var7) {
      } catch (BackwardStatusException var8) {
      } catch (KeyStoreCancelException var9) {
      }
   }

   private final String promptForLabel(String existingLabel) throws KeyStoreCancelException {
      Certificate certificate = this.getCertificate();
      if (existingLabel == null && certificate != null) {
         existingLabel = certificate.getSubjectFriendlyName();
      }

      String dialogPrompt = null;
      if (existingLabel != null) {
         dialogPrompt = MessageFormat.format(KeyStoreResources.getString(7014), new String[]{existingLabel});
      } else {
         dialogPrompt = KeyStoreResources.getString(7);
      }

      KeyStore keyStore = KeyStoreManager.getInstance().getKeyStore(this);
      EnterCertificateLabelDialog dialog = new EnterCertificateLabelDialog(dialogPrompt, certificate, keyStore, existingLabel, 134217728);
      BackgroundDialog.show(dialog);
      if (dialog.getCloseReason() == 0) {
         return dialog.getText();
      } else {
         throw new KeyStoreCancelException();
      }
   }

   private final Key getKey(boolean param1, KeyStoreDataTicket param2) throws KeyStoreDecodeException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 004: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._privateKey Ljava/lang/Object;
      // 007: ifnonnull 00e
      // 00a: iload 1
      // 00b: ifne 01c
      // 00e: aload 0
      // 00f: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 012: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._symmetricKey Ljava/lang/Object;
      // 015: ifnonnull 01e
      // 018: iload 1
      // 019: ifne 01e
      // 01c: aconst_null
      // 01d: areturn
      // 01e: iload 1
      // 01f: ifeq 05a
      // 022: aload 0
      // 023: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 026: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._privateKey Ljava/lang/Object;
      // 029: dup
      // 02a: instanceof net/rim/device/api/crypto/PrivateKey
      // 02d: ifne 034
      // 030: pop
      // 031: goto 05a
      // 034: checkcast net/rim/device/api/crypto/PrivateKey
      // 037: astore 3
      // 038: aload 3
      // 039: invokeinterface net/rim/device/api/crypto/PrivateKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 03e: invokeinterface net/rim/device/api/crypto/CryptoSystem.getAsymmetricCryptoToken ()Lnet/rim/device/api/crypto/AsymmetricCryptoToken; 1
      // 043: invokeinterface net/rim/device/api/crypto/CryptoToken.providesUserAuthentication ()Z 1
      // 048: ifne 058
      // 04b: aload 0
      // 04c: aload 2
      // 04d: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.checkTicket (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Z
      // 050: ifne 058
      // 053: aload 0
      // 054: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getTicket ()Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;
      // 057: pop
      // 058: aload 3
      // 059: areturn
      // 05a: iload 1
      // 05b: ifne 091
      // 05e: aload 0
      // 05f: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 062: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._symmetricKey Ljava/lang/Object;
      // 065: dup
      // 066: instanceof net/rim/device/api/crypto/SymmetricKey
      // 069: ifne 070
      // 06c: pop
      // 06d: goto 091
      // 070: checkcast net/rim/device/api/crypto/SymmetricKey
      // 073: astore 3
      // 074: aload 3
      // 075: invokeinterface net/rim/device/api/crypto/SymmetricKey.getSymmetricCryptoToken ()Lnet/rim/device/api/crypto/SymmetricCryptoToken; 1
      // 07a: invokeinterface net/rim/device/api/crypto/CryptoToken.providesUserAuthentication ()Z 1
      // 07f: ifne 08f
      // 082: aload 0
      // 083: aload 2
      // 084: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.checkTicket (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Z
      // 087: ifne 08f
      // 08a: aload 0
      // 08b: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getTicket ()Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;
      // 08e: pop
      // 08f: aload 3
      // 090: areturn
      // 091: aload 0
      // 092: aload 2
      // 093: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.checkTicket (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Z
      // 096: ifne 09e
      // 099: aload 0
      // 09a: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getTicket ()Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;
      // 09d: astore 2
      // 09e: invokestatic net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStoreManagerHelper;
      // 0a1: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getPasswordVersion ()I
      // 0a4: istore 3
      // 0a5: aload 0
      // 0a6: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getPasswordVersion ()I
      // 0a9: istore 4
      // 0ab: aload 0
      // 0ac: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.getSecurityLevel ()I
      // 0af: istore 5
      // 0b1: iload 5
      // 0b3: bipush 1
      // 0b4: if_icmpeq 152
      // 0b7: iload 3
      // 0b8: iload 4
      // 0ba: if_icmpeq 152
      // 0bd: iload 5
      // 0bf: aload 0
      // 0c0: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.getKeyUsage ()J
      // 0c3: invokestatic net/rim/device/api/crypto/keystore/KeyStoreUtilitiesInternal.getAppropriateSecurityLevel (IJ)I
      // 0c6: istore 5
      // 0c8: invokestatic net/rim/device/api/crypto/keystore/KeyStorePasswordManager.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStorePasswordManager;
      // 0cb: astore 6
      // 0cd: bipush 0
      // 0ce: istore 7
      // 0d0: aload 0
      // 0d1: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 0d4: invokestatic net/rim/vm/Memory.isObjectInGroup (Ljava/lang/Object;)Z
      // 0d7: ifeq 0e1
      // 0da: aload 0
      // 0db: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.ungroupData ()V
      // 0de: bipush 1
      // 0df: istore 7
      // 0e1: aload 0
      // 0e2: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 0e5: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._privateKey Ljava/lang/Object;
      // 0e8: instanceof [B
      // 0eb: ifeq 109
      // 0ee: aload 0
      // 0ef: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 0f2: aload 6
      // 0f4: iload 5
      // 0f6: aload 0
      // 0f7: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 0fa: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._privateKey Ljava/lang/Object;
      // 0fd: checkcast [B
      // 100: invokevirtual net/rim/device/api/crypto/keystore/KeyStorePasswordManager.decryptReEncrypt (I[B)[B
      // 103: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._privateKey Ljava/lang/Object;
      // 106: goto 12e
      // 109: aload 0
      // 10a: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 10d: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._symmetricKey Ljava/lang/Object;
      // 110: instanceof [B
      // 113: ifeq 12e
      // 116: aload 0
      // 117: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 11a: aload 6
      // 11c: iload 5
      // 11e: aload 0
      // 11f: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 122: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._symmetricKey Ljava/lang/Object;
      // 125: checkcast [B
      // 128: invokevirtual net/rim/device/api/crypto/keystore/KeyStorePasswordManager.decryptReEncrypt (I[B)[B
      // 12b: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._symmetricKey Ljava/lang/Object;
      // 12e: iload 7
      // 130: ifeq 152
      // 133: aload 0
      // 134: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.groupData ()V
      // 137: goto 152
      // 13a: astore 8
      // 13c: new net/rim/device/api/crypto/keystore/KeyStoreCancelException
      // 13f: dup
      // 140: invokespecial net/rim/device/api/crypto/keystore/KeyStoreCancelException.<init> ()V
      // 143: athrow
      // 144: astore 9
      // 146: iload 7
      // 148: ifeq 14f
      // 14b: aload 0
      // 14c: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.groupData ()V
      // 14f: aload 9
      // 151: athrow
      // 152: aload 0
      // 153: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 156: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._privateKey Ljava/lang/Object;
      // 159: instanceof [B
      // 15c: ifeq 184
      // 15f: invokestatic net/rim/device/api/crypto/keystore/KeyStorePasswordManager.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStorePasswordManager;
      // 162: astore 7
      // 164: aload 7
      // 166: aload 0
      // 167: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 16a: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._privateKey Ljava/lang/Object;
      // 16d: checkcast [B
      // 170: aload 0
      // 171: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 174: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload.getLabel ()Ljava/lang/String;
      // 177: aload 0
      // 178: aload 2
      // 179: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.getPasswordTicket (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/keystore/KeyStorePasswordTicket;
      // 17c: invokevirtual net/rim/device/api/crypto/keystore/KeyStorePasswordManager.decrypt ([BLjava/lang/String;Lnet/rim/device/api/crypto/keystore/KeyStorePasswordTicket;)[B
      // 17f: astore 6
      // 181: goto 1be
      // 184: aload 0
      // 185: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 188: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._symmetricKey Ljava/lang/Object;
      // 18b: instanceof [B
      // 18e: ifeq 1b6
      // 191: invokestatic net/rim/device/api/crypto/keystore/KeyStorePasswordManager.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStorePasswordManager;
      // 194: astore 7
      // 196: aload 7
      // 198: aload 0
      // 199: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 19c: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._symmetricKey Ljava/lang/Object;
      // 19f: checkcast [B
      // 1a2: aload 0
      // 1a3: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 1a6: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload.getLabel ()Ljava/lang/String;
      // 1a9: aload 0
      // 1aa: aload 2
      // 1ab: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.getPasswordTicket (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/keystore/KeyStorePasswordTicket;
      // 1ae: invokevirtual net/rim/device/api/crypto/keystore/KeyStorePasswordManager.decrypt ([BLjava/lang/String;Lnet/rim/device/api/crypto/keystore/KeyStorePasswordTicket;)[B
      // 1b1: astore 6
      // 1b3: goto 1be
      // 1b6: new net/rim/device/api/crypto/keystore/KeyStoreDecodeException
      // 1b9: dup
      // 1ba: invokespecial net/rim/device/api/crypto/keystore/KeyStoreDecodeException.<init> ()V
      // 1bd: athrow
      // 1be: new java/io/ByteArrayInputStream
      // 1c1: dup
      // 1c2: aload 6
      // 1c4: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 1c7: astore 7
      // 1c9: iload 1
      // 1ca: ifeq 1da
      // 1cd: aload 7
      // 1cf: aload 0
      // 1d0: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 1d3: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._privateKeyEncodingAlgorithm Ljava/lang/String;
      // 1d6: invokestatic net/rim/device/api/crypto/encoder/PrivateKeyDecoder.decode (Ljava/io/InputStream;Ljava/lang/String;)Lnet/rim/device/api/crypto/PrivateKey;
      // 1d9: areturn
      // 1da: aload 7
      // 1dc: aload 0
      // 1dd: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 1e0: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._symmetricKeyEncodingAlgorithm Ljava/lang/String;
      // 1e3: invokestatic net/rim/device/api/crypto/encoder/SymmetricKeyDecoder.decode (Ljava/io/InputStream;Ljava/lang/String;)Lnet/rim/device/api/crypto/SymmetricKey;
      // 1e6: areturn
      // 1e7: astore 7
      // 1e9: goto 202
      // 1ec: astore 7
      // 1ee: goto 202
      // 1f1: astore 7
      // 1f3: goto 202
      // 1f6: astore 7
      // 1f8: goto 202
      // 1fb: astore 7
      // 1fd: goto 202
      // 200: astore 7
      // 202: new net/rim/device/api/crypto/keystore/KeyStoreDecodeException
      // 205: dup
      // 206: invokespecial net/rim/device/api/crypto/keystore/KeyStoreDecodeException.<init> ()V
      // 209: athrow
      // try (96 -> 135): 140 net/rim/device/api/crypto/keystore/KeyStoreDecodeException
      // try (96 -> 135): 145 null
      // try (140 -> 146): 145 null
      // try (198 -> 210): 217 null
      // try (211 -> 216): 217 null
      // try (198 -> 210): 219 net/rim/device/api/crypto/InvalidKeyEncodingException
      // try (211 -> 216): 219 net/rim/device/api/crypto/InvalidKeyEncodingException
      // try (198 -> 210): 221 null
      // try (211 -> 216): 221 null
      // try (198 -> 210): 223 net/rim/device/api/crypto/InvalidCryptoSystemException
      // try (211 -> 216): 223 net/rim/device/api/crypto/InvalidCryptoSystemException
      // try (198 -> 210): 225 null
      // try (211 -> 216): 225 null
      // try (198 -> 210): 227 net/rim/device/api/crypto/UnsupportedCryptoSystemException
      // try (211 -> 216): 227 net/rim/device/api/crypto/UnsupportedCryptoSystemException
   }

   RIMKeyStoreData(
      AssociatedData[] associatedData,
      String label,
      byte[] privateKey,
      String privateKeyEncodingAlgorithm,
      PublicKey publicKey,
      long keyUsage,
      byte[] certificateEncoding,
      String certificateType,
      CertificateStatus certStatus,
      int uid,
      int[] hashes,
      long[] indices,
      long[] notUsed
   ) {
      this._payload = new RIMKeyStoreData$Payload();
      if (certificateEncoding == null) {
         if (certStatus != null) {
            throw new IllegalArgumentException();
         }
      } else {
         this._payload._certificate = CertificateRepository.getInstance().addCertificate(certificateEncoding, certificateType);
         if (Memory.isObjectInGroup(certificateEncoding)) {
            certificateEncoding = Arrays.copy(certificateEncoding);
         }

         this._payload._encoding = certificateEncoding;
         this._payload._type = certificateType;
         this.setCertificateStatus(null, certificateEncoding, certificateType, certStatus, true);
      }

      this.createAssociations(associatedData);
      this._payload._uid = uid;
      this._payload._publicKey = publicKey;
      this._payload._keyUsage = keyUsage;
      this._payload._privateKey = privateKey;
      this._payload._privateKeyEncodingAlgorithm = privateKeyEncodingAlgorithm;
      this._payload._hashes = hashes;
      this._payload._indices = indices;
      this._payload._notUsed = notUsed;
      if (label == null) {
         label = KeyStoreResources.getString(8);
      }

      this._payload.setLabel(label);
      this.computeHashCode(label);
   }

   public RIMKeyStoreData(
      AssociatedData[] associatedData, String label, SymmetricKey symmetricKey, String symmetricKeyEncodingAlgorithm, int securityLevel, KeyStoreTicket ticket
   ) {
      this._payload = new RIMKeyStoreData$Payload();
      this._payload._uid = RandomSource.getInt();
      this.createAssociations(associatedData);
      securityLevel = KeyStoreUtilitiesInternal.getAppropriateSecurityLevel(securityLevel, 0);
      this.savePrivateKey(symmetricKey, symmetricKeyEncodingAlgorithm, securityLevel, ticket);
      if (label == null) {
         label = this.promptForLabel(null);
      }

      this._payload.setLabel(label);
      this.computeHashCode(label);
   }

   public RIMKeyStoreData(
      AssociatedData[] param1,
      String param2,
      PrivateKey param3,
      String param4,
      int param5,
      PublicKey param6,
      long param7,
      Certificate param9,
      CertificateStatus param10,
      KeyStoreTicket param11
   ) throws CryptoTokenException, CryptoUnsupportedOperationException, InvalidKeyEncodingException, InvalidKeyException, NoSuchAlgorithmException, KeyStoreCancelException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokespecial java/lang/Object.<init> ()V
      // 004: aload 0
      // 005: bipush 1
      // 006: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._syncObjectDirty Z
      // 009: aload 0
      // 00a: new net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload
      // 00d: dup
      // 00e: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload.<init> ()V
      // 011: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 014: aload 9
      // 016: ifnonnull 030
      // 019: aload 0
      // 01a: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 01d: invokestatic net/rim/device/api/crypto/RandomSource.getInt ()I
      // 020: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._uid I
      // 023: aload 10
      // 025: ifnull 086
      // 028: new java/lang/IllegalArgumentException
      // 02b: dup
      // 02c: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 02f: athrow
      // 030: aload 0
      // 031: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 034: invokestatic net/rim/device/api/crypto/keystore/CertificateRepository.getInstance ()Lnet/rim/device/api/crypto/keystore/CertificateRepository;
      // 037: aload 9
      // 039: invokevirtual net/rim/device/api/crypto/keystore/CertificateRepository.addCertificate (Lnet/rim/device/api/crypto/certificate/Certificate;)J
      // 03c: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._certificate J
      // 03f: aload 9
      // 041: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getEncoding ()[B 1
      // 046: astore 12
      // 048: aload 12
      // 04a: invokestatic net/rim/vm/Memory.isObjectInGroup (Ljava/lang/Object;)Z
      // 04d: ifeq 057
      // 050: aload 12
      // 052: invokestatic net/rim/device/api/util/Arrays.copy ([B)[B
      // 055: astore 12
      // 057: aload 0
      // 058: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 05b: aload 12
      // 05d: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._encoding [B
      // 060: aload 0
      // 061: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 064: aload 9
      // 066: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getType ()Ljava/lang/String; 1
      // 06b: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._type Ljava/lang/String;
      // 06e: aload 0
      // 06f: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 072: bipush 0
      // 073: aload 12
      // 075: invokestatic net/rim/device/api/util/CRC32.update (I[B)I
      // 078: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._uid I
      // 07b: aload 0
      // 07c: aload 9
      // 07e: aconst_null
      // 07f: aconst_null
      // 080: aload 10
      // 082: bipush 0
      // 083: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.setCertificateStatus (Lnet/rim/device/api/crypto/certificate/Certificate;[BLjava/lang/String;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Z)V
      // 086: aload 0
      // 087: aload 1
      // 088: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.createAssociations ([Lnet/rim/device/api/crypto/keystore/AssociatedData;)V
      // 08b: aload 0
      // 08c: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 08f: aload 6
      // 091: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 094: aload 0
      // 095: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 098: lload 7
      // 09a: putfield net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload._keyUsage J
      // 09d: iload 5
      // 09f: lload 7
      // 0a1: invokestatic net/rim/device/api/crypto/keystore/KeyStoreUtilitiesInternal.getAppropriateSecurityLevel (IJ)I
      // 0a4: istore 5
      // 0a6: aload 0
      // 0a7: aload 3
      // 0a8: aload 4
      // 0aa: iload 5
      // 0ac: aload 11
      // 0ae: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.savePrivateKey (Lnet/rim/device/api/crypto/Key;Ljava/lang/String;ILnet/rim/device/api/crypto/keystore/KeyStoreTicket;)V
      // 0b1: goto 0ea
      // 0b4: astore 12
      // 0b6: aload 0
      // 0b7: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.removeCertificate ()V
      // 0ba: aload 12
      // 0bc: athrow
      // 0bd: astore 12
      // 0bf: aload 0
      // 0c0: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.removeCertificate ()V
      // 0c3: aload 12
      // 0c5: athrow
      // 0c6: astore 12
      // 0c8: aload 0
      // 0c9: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.removeCertificate ()V
      // 0cc: aload 12
      // 0ce: athrow
      // 0cf: astore 12
      // 0d1: aload 0
      // 0d2: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.removeCertificate ()V
      // 0d5: aload 12
      // 0d7: athrow
      // 0d8: astore 12
      // 0da: aload 0
      // 0db: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.removeCertificate ()V
      // 0de: aload 12
      // 0e0: athrow
      // 0e1: astore 12
      // 0e3: aload 0
      // 0e4: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData.removeCertificate ()V
      // 0e7: aload 12
      // 0e9: athrow
      // 0ea: aload 2
      // 0eb: ifnonnull 0f4
      // 0ee: aload 0
      // 0ef: aconst_null
      // 0f0: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.promptForLabel (Ljava/lang/String;)Ljava/lang/String;
      // 0f3: astore 2
      // 0f4: aload 0
      // 0f5: getfield net/rim/device/api/crypto/keystore/RIMKeyStoreData._payload Lnet/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload;
      // 0f8: aload 2
      // 0f9: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStoreData$Payload.setLabel (Ljava/lang/String;)V
      // 0fc: aload 0
      // 0fd: aload 2
      // 0fe: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.computeHashCode (Ljava/lang/String;)V
      // 101: return
      // try (74 -> 80): 81 null
      // try (74 -> 80): 86 net/rim/device/api/crypto/InvalidKeyEncodingException
      // try (74 -> 80): 91 null
      // try (74 -> 80): 96 null
      // try (74 -> 80): 101 net/rim/device/api/crypto/keystore/KeyStoreCancelException
      // try (74 -> 80): 106 null
   }
}
