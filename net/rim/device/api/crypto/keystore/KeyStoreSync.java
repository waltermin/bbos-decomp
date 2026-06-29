package net.rim.device.api.crypto.keystore;

import java.util.Enumeration;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.encoder.PublicKeyEncoder;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.synchronization.MinimalSyncCollection;
import net.rim.vm.Memory;

class KeyStoreSync implements SyncCollection, SyncConverter, MinimalSyncCollection, SyncEventListener {
   private String _name;
   private SyncableRIMKeyStore _keyStore;
   private boolean _anyIncomingCerts;
   private boolean _anyPrivateKeysUploaded;
   private String MSCAPI_STRING = "MSCAPI";
   private static final long KEY_STORE_SYNC_CONVERTER;
   private static final int LABEL;
   private static final int CERTIFICATE_TYPE;
   private static final int CERTIFICATE;
   private static final int CERT_STATUS;
   private static final int PRODUCED_AT;
   private static final int THIS_UPDATE;
   private static final int NEXT_UPDATE;
   private static final int REVOCATION_DATE;
   private static final int REVOCATION_REASON;
   private static final int PUBLIC_KEY_ENCODING;
   private static final int PUBLIC_KEY;
   private static final int PRIVATE_KEY_ENCODING;
   private static final int PRIVATE_KEY;
   private static final int SALT;
   private static final int HASH;
   private static final int SECURITY_LEVEL;
   private static final int INDEX;
   private static final int PRIVATE_KEY_EXISTS;
   private static final int SYMMETRIC_KEY;
   private static final int SYMMETRIC_KEY_ENCODING;
   private static final int PROTOCOL_VERSION;
   private static final int PASSWORD_VERSION;
   private static final int CURRENT_PROTOCOL_VERSION;

   public KeyStoreSync(String name, SyncableRIMKeyStore keyStore) {
      this._name = name;
      this._keyStore = keyStore;
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.addSyncEventListener(this);
      }
   }

   @Override
   public boolean addSyncObject(SyncObject param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStoreManagerHelper;
      // 03: astore 2
      // 04: aload 2
      // 05: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getAllowBackupRestore ()Z
      // 08: ifne 14
      // 0b: aload 2
      // 0c: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getAccess ()Z
      // 0f: ifne 14
      // 12: bipush 1
      // 13: ireturn
      // 14: aload 0
      // 15: getfield net/rim/device/api/crypto/keystore/KeyStoreSync._keyStore Lnet/rim/device/api/crypto/keystore/SyncableRIMKeyStore;
      // 18: aload 2
      // 19: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getHash ()[B
      // 1c: invokevirtual net/rim/device/api/crypto/keystore/SyncableRIMKeyStore.getTicket ([B)Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;
      // 1f: astore 3
      // 20: aload 1
      // 21: dup
      // 22: instanceof net/rim/device/api/crypto/keystore/KeyStoreDataHelper
      // 25: ifne 2c
      // 28: pop
      // 29: goto c7
      // 2c: checkcast net/rim/device/api/crypto/keystore/KeyStoreDataHelper
      // 2f: astore 4
      // 31: aload 4
      // 33: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getSymmetricKey ()[B
      // 36: ifnull 6c
      // 39: aload 0
      // 3a: getfield net/rim/device/api/crypto/keystore/KeyStoreSync._keyStore Lnet/rim/device/api/crypto/keystore/SyncableRIMKeyStore;
      // 3d: aload 4
      // 3f: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getAssociatedData ()[Lnet/rim/device/api/crypto/keystore/AssociatedData;
      // 42: aload 4
      // 44: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getLabel ()Ljava/lang/String;
      // 47: aload 4
      // 49: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getSymmetricKey ()[B
      // 4c: aload 4
      // 4e: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getSymmetricKeyEncodingAlgorithm ()Ljava/lang/String;
      // 51: aload 4
      // 53: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getUID ()I
      // 56: aload 4
      // 58: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getHashes ()[I
      // 5b: aload 4
      // 5d: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getIndices ()[J
      // 60: aload 4
      // 62: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getNotUsed ()[J
      // 65: aload 3
      // 66: invokevirtual net/rim/device/api/crypto/keystore/SyncableRIMKeyStore.set ([Lnet/rim/device/api/crypto/BlockEncryptor;Ljava/lang/String;[BLjava/lang/String;I[I[J[JLnet/rim/device/api/crypto/keystore/KeyStoreTicket;)V
      // 69: goto b5
      // 6c: aload 0
      // 6d: getfield net/rim/device/api/crypto/keystore/KeyStoreSync._keyStore Lnet/rim/device/api/crypto/keystore/SyncableRIMKeyStore;
      // 70: aload 4
      // 72: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getAssociatedData ()[Lnet/rim/device/api/crypto/keystore/AssociatedData;
      // 75: aload 4
      // 77: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getLabel ()Ljava/lang/String;
      // 7a: aload 4
      // 7c: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getPrivateKey ()[B
      // 7f: aload 4
      // 81: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getPrivateKeyEncodingAlgorithm ()Ljava/lang/String;
      // 84: aload 4
      // 86: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 89: aload 4
      // 8b: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getKeyUsage ()J
      // 8e: aload 4
      // 90: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getCertificate ()[B
      // 93: aload 4
      // 95: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getCertificateType ()Ljava/lang/String;
      // 98: aload 4
      // 9a: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getCertificateStatus ()Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 9d: aload 4
      // 9f: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getUID ()I
      // a2: aload 4
      // a4: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getHashes ()[I
      // a7: aload 4
      // a9: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getIndices ()[J
      // ac: aload 4
      // ae: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getNotUsed ()[J
      // b1: aload 3
      // b2: invokevirtual net/rim/device/api/crypto/keystore/SyncableRIMKeyStore.set ([Lnet/rim/device/api/crypto/BlockEncryptor;Ljava/lang/String;[BLjava/lang/String;Ljava/lang/Object;J[BLjava/lang/String;Ljava/lang/Object;I[I[J[JLnet/rim/device/api/crypto/keystore/KeyStoreTicket;)V
      // b5: bipush 1
      // b6: ireturn
      // b7: astore 3
      // b8: bipush 0
      // b9: ireturn
      // ba: astore 3
      // bb: bipush 0
      // bc: ireturn
      // bd: astore 3
      // be: bipush 0
      // bf: ireturn
      // c0: astore 3
      // c1: bipush 0
      // c2: ireturn
      // c3: astore 3
      // c4: bipush 0
      // c5: ireturn
      // c6: astore 3
      // c7: bipush 0
      // c8: ireturn
      // try (10 -> 79): 80 null
      // try (10 -> 79): 83 net/rim/device/api/crypto/InvalidKeyEncodingException
      // try (10 -> 79): 86 null
      // try (10 -> 79): 89 null
      // try (10 -> 79): 92 net/rim/device/api/crypto/keystore/KeyStoreCancelException
      // try (10 -> 79): 95 null
   }

   @Override
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (oldObject instanceof KeyStoreData && newObject instanceof KeyStoreDataHelper) {
         KeyStoreManagerHelper keyStoreManagerHelper = KeyStoreManagerHelper.getInstance();
         if (keyStoreManagerHelper.getAccess()) {
            KeyStoreDataHelper newHelper = (KeyStoreDataHelper)newObject;
            KeyStoreData data = (KeyStoreData)oldObject;
            CertificateStatus certStatus = newHelper.getCertificateStatus();
            if (certStatus == null) {
               return false;
            }

            Certificate certificate = data.getCertificate();
            if (certificate != null) {
               CertificateStatusManager manager = CertificateStatusManager.getInstance();

               try {
                  return manager.setStatus(certificate, null, null, certStatus, null, true);
               } catch (BackwardStatusException var10) {
                  return false;
               } catch (InvalidTimeException var11) {
                  return false;
               } catch (KeyStoreCancelException var12) {
               }
            }
         }
      }

      return false;
   }

   @Override
   public boolean removeSyncObject(SyncObject param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStoreManagerHelper;
      // 03: astore 2
      // 04: aload 2
      // 05: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getAccess ()Z
      // 08: ifne 0e
      // 0b: goto e0
      // 0e: aload 1
      // 0f: dup
      // 10: instanceof net/rim/device/api/crypto/keystore/KeyStoreDataHelper
      // 13: ifne 1a
      // 16: pop
      // 17: goto a8
      // 1a: checkcast net/rim/device/api/crypto/keystore/KeyStoreDataHelper
      // 1d: astore 3
      // 1e: aload 3
      // 1f: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getSymmetricKey ()[B
      // 22: ifnull 51
      // 25: new net/rim/device/api/crypto/keystore/RIMKeyStoreData
      // 28: dup
      // 29: aload 3
      // 2a: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getAssociatedData ()[Lnet/rim/device/api/crypto/keystore/AssociatedData;
      // 2d: aload 3
      // 2e: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getLabel ()Ljava/lang/String;
      // 31: aload 3
      // 32: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getSymmetricKey ()[B
      // 35: aload 3
      // 36: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getSymmetricKeyEncodingAlgorithm ()Ljava/lang/String;
      // 39: aload 3
      // 3a: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getUID ()I
      // 3d: aload 3
      // 3e: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getHashes ()[I
      // 41: aload 3
      // 42: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getIndices ()[J
      // 45: aload 3
      // 46: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getNotUsed ()[J
      // 49: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.<init> ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;[BLjava/lang/String;I[I[J[J)V
      // 4c: astore 4
      // 4e: goto 8e
      // 51: new net/rim/device/api/crypto/keystore/RIMKeyStoreData
      // 54: dup
      // 55: aload 3
      // 56: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getAssociatedData ()[Lnet/rim/device/api/crypto/keystore/AssociatedData;
      // 59: aload 3
      // 5a: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getLabel ()Ljava/lang/String;
      // 5d: aload 3
      // 5e: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getPrivateKey ()[B
      // 61: aload 3
      // 62: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getPrivateKeyEncodingAlgorithm ()Ljava/lang/String;
      // 65: aload 3
      // 66: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 69: aload 3
      // 6a: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getKeyUsage ()J
      // 6d: aload 3
      // 6e: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getCertificate ()[B
      // 71: aload 3
      // 72: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getCertificateType ()Ljava/lang/String;
      // 75: aload 3
      // 76: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getCertificateStatus ()Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 79: aload 3
      // 7a: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getUID ()I
      // 7d: aload 3
      // 7e: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getHashes ()[I
      // 81: aload 3
      // 82: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getIndices ()[J
      // 85: aload 3
      // 86: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreDataHelper.getNotUsed ()[J
      // 89: invokespecial net/rim/device/api/crypto/keystore/RIMKeyStoreData.<init> ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;[BLjava/lang/String;Lnet/rim/device/api/crypto/PublicKey;J[BLjava/lang/String;Lnet/rim/device/api/crypto/certificate/CertificateStatus;I[I[J[J)V
      // 8c: astore 4
      // 8e: aload 0
      // 8f: getfield net/rim/device/api/crypto/keystore/KeyStoreSync._keyStore Lnet/rim/device/api/crypto/keystore/SyncableRIMKeyStore;
      // 92: aload 4
      // 94: new net/rim/device/api/crypto/keystore/SyncableRIMKeyStoreTicket
      // 97: dup
      // 98: aload 2
      // 99: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getHash ()[B
      // 9c: aload 0
      // 9d: getfield net/rim/device/api/crypto/keystore/KeyStoreSync._keyStore Lnet/rim/device/api/crypto/keystore/SyncableRIMKeyStore;
      // a0: invokespecial net/rim/device/api/crypto/keystore/SyncableRIMKeyStoreTicket.<init> ([BLnet/rim/device/api/crypto/keystore/KeyStore;)V
      // a3: invokevirtual net/rim/device/api/crypto/keystore/PersistableRIMKeyStore.removeKey (Ljava/lang/Object;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)V
      // a6: bipush 1
      // a7: ireturn
      // a8: aload 1
      // a9: dup
      // aa: instanceof net/rim/device/api/crypto/keystore/KeyStoreData
      // ad: ifne b4
      // b0: pop
      // b1: goto de
      // b4: checkcast net/rim/device/api/crypto/keystore/KeyStoreData
      // b7: astore 3
      // b8: aload 0
      // b9: getfield net/rim/device/api/crypto/keystore/KeyStoreSync._keyStore Lnet/rim/device/api/crypto/keystore/SyncableRIMKeyStore;
      // bc: aload 3
      // bd: new net/rim/device/api/crypto/keystore/SyncableRIMKeyStoreTicket
      // c0: dup
      // c1: aload 2
      // c2: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getHash ()[B
      // c5: aload 0
      // c6: getfield net/rim/device/api/crypto/keystore/KeyStoreSync._keyStore Lnet/rim/device/api/crypto/keystore/SyncableRIMKeyStore;
      // c9: invokespecial net/rim/device/api/crypto/keystore/SyncableRIMKeyStoreTicket.<init> ([BLnet/rim/device/api/crypto/keystore/KeyStore;)V
      // cc: invokevirtual net/rim/device/api/crypto/keystore/PersistableRIMKeyStore.removeKey (Ljava/lang/Object;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)V
      // cf: bipush 1
      // d0: ireturn
      // d1: astore 3
      // d2: bipush 0
      // d3: ireturn
      // d4: astore 3
      // d5: bipush 0
      // d6: ireturn
      // d7: astore 3
      // d8: bipush 0
      // d9: ireturn
      // da: astore 3
      // db: bipush 0
      // dc: ireturn
      // dd: astore 3
      // de: bipush 0
      // df: ireturn
      // e0: bipush 1
      // e1: ireturn
      // try (6 -> 80): 102 null
      // try (81 -> 101): 102 null
      // try (6 -> 80): 105 net/rim/device/api/crypto/InvalidKeyEncodingException
      // try (81 -> 101): 105 net/rim/device/api/crypto/InvalidKeyEncodingException
      // try (6 -> 80): 108 null
      // try (81 -> 101): 108 null
      // try (6 -> 80): 111 null
      // try (81 -> 101): 111 null
      // try (6 -> 80): 114 net/rim/device/api/crypto/keystore/KeyStoreCancelException
      // try (81 -> 101): 114 net/rim/device/api/crypto/keystore/KeyStoreCancelException
   }

   @Override
   public boolean removeAllSyncObjects() {
      KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();
      if (!helper.getAllowBackupRestore() && !helper.getAccess()) {
         return true;
      }

      try {
         KeyStoreTicket ticket = new SyncableRIMKeyStoreTicket(helper.getHash(), this._keyStore);
         this._keyStore.removeAllKeys(ticket);
         Memory.persistentGC();
         return true;
      } catch (KeyStoreCancelException var3) {
         return false;
      }
   }

   @Override
   public SyncObject[] getSyncObjects() {
      KeyStoreManagerHelper keyStoreManagerHelper = KeyStoreManagerHelper.getInstance();
      if (!keyStoreManagerHelper.getAllowBackupRestore() && !keyStoreManagerHelper.getAccess()) {
         return new Object[0];
      }

      int numElements = this._keyStore.size();
      Enumeration enumeration = this._keyStore.elements();
      SyncObject[] objects = new Object[numElements];

      for (int i = 0; i < numElements; i++) {
         objects[i] = (SyncObject)enumeration.nextElement();
      }

      return objects;
   }

   @Override
   public SyncObject getSyncObject(int uid) {
      KeyStoreManagerHelper keyStoreManagerHelper = KeyStoreManagerHelper.getInstance();
      if (keyStoreManagerHelper.getAccess()) {
         Enumeration e = this._keyStore.elements();

         while (e.hasMoreElements()) {
            SyncObject element = (SyncObject)e.nextElement();
            if (element.getUID() == uid) {
               return element;
            }
         }
      }

      return null;
   }

   @Override
   public boolean isSyncObjectDirty(SyncObject object) {
      return !(object instanceof RIMKeyStoreData) ? false : ((RIMKeyStoreData)object)._syncObjectDirty;
   }

   @Override
   public void setSyncObjectDirty(SyncObject object) {
      if (object instanceof RIMKeyStoreData) {
         ((RIMKeyStoreData)object)._syncObjectDirty = true;
      }
   }

   @Override
   public void clearSyncObjectDirty(SyncObject object) {
      if (object instanceof RIMKeyStoreData) {
         ((RIMKeyStoreData)object)._syncObjectDirty = false;
      }
   }

   @Override
   public int getSyncObjectCount() {
      KeyStoreManagerHelper keyStoreManagerHelper = KeyStoreManagerHelper.getInstance();
      return !keyStoreManagerHelper.getAllowBackupRestore() && !keyStoreManagerHelper.getAccess() ? 0 : this._keyStore.size();
   }

   @Override
   public int getSyncVersion() {
      return 0;
   }

   @Override
   public String getSyncName() {
      return this._name;
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public void beginTransaction() {
   }

   @Override
   public void endTransaction() {
   }

   @Override
   public void syncEventOccurred(int eventId, Object object) {
      switch (eventId) {
         case 1:
         default:
            this._anyIncomingCerts = false;
            this._anyPrivateKeysUploaded = false;
            return;
         case 2:
            this.expandCertificates();
         case 0:
      }
   }

   private void expandCertificates() {
      if (this._anyPrivateKeysUploaded) {
         KeyStoreITPolicyListener.launchITPolicyCheck(false);
      }

      if (this._anyIncomingCerts) {
         KeyStoreCertificateThread thread = new KeyStoreCertificateThread();
         thread.setPriority(1);
         thread.start();
      }
   }

   @Override
   public boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof KeyStoreData)) {
         return false;
      }

      RIMKeyStoreData data = (RIMKeyStoreData)object;
      ConverterUtilities.writeInt(buffer, 27, 1);
      if (data.getLabel() != null) {
         ConverterUtilities.writeStringIntellisync(buffer, 1, data.getLabel());
      }

      Certificate certificate = data.getCertificate();
      if (certificate == null) {
         if (data.getPublicKey() != null) {
            label185:
            try {
               ConverterUtilities.writeByteArray(buffer, 13, PublicKeyEncoder.encode(data.getPublicKey(), this.MSCAPI_STRING).getEncodedKey());
               ConverterUtilities.writeStringIntellisync(buffer, 12, this.MSCAPI_STRING);
            } finally {
               break label185;
            }
         }
      } else {
         String certType = certificate.getType();
         if (certType != null) {
            ConverterUtilities.writeStringIntellisync(buffer, 4, certType);
         }

         CertificateStatusManager manager = CertificateStatusManager.getInstance();
         CertificateStatus status = manager.getStatus(certificate);
         if (status == null) {
            status = new CertificateStatus();
         }

         ConverterUtilities.convertInt(buffer, 6, status.getStatus(), 4);
         ConverterUtilities.writeLong(buffer, 7, status.getProducedAtTime());
         ConverterUtilities.writeLong(buffer, 8, status.getThisUpdateTime());
         ConverterUtilities.writeLong(buffer, 9, status.getNextUpdateTime());
         if (status.getRevocationTime() != 0) {
            ConverterUtilities.writeLong(buffer, 10, status.getRevocationTime());
         }

         if (status.getRevocationReason() != -1) {
            ConverterUtilities.convertInt(buffer, 11, status.getRevocationReason(), 4);
         }

         ConverterUtilities.writeByteArray(buffer, 5, certificate.getEncoding());
      }

      if (data.isPrivateKeySet() && ITPolicy.getInteger(24, 47, 0) == 0 && !ITPolicy.getBoolean(24, 32, false) && data._payload._privateKey instanceof byte[]) {
         try {
            byte[] privateKey = (byte[])data._payload._privateKey;
            ConverterUtilities.writeByteArray(buffer, 19, KeyStoreUtilitiesInternal.getKeyMaterial(privateKey, 0, privateKey.length));
            ConverterUtilities.writeByteArray(buffer, 20, KeyStoreUtilitiesInternal.getSalt(privateKey, 0, privateKey.length));
            ConverterUtilities.writeByteArray(buffer, 21, KeyStoreUtilitiesInternal.getHash(privateKey));
            ConverterUtilities.writeInt(buffer, 29, KeyStoreUtilitiesInternal.getPasswordVersion(privateKey));
            ConverterUtilities.writeStringIntellisync(buffer, 18, data._payload._privateKeyEncodingAlgorithm);
         } catch (KeyStoreDecodeException var17) {
         }
      }

      if (data.isSymmetricKeySet()
         && ITPolicy.getInteger(24, 47, 0) == 0
         && !ITPolicy.getBoolean(24, 32, false)
         && data._payload._symmetricKey instanceof byte[]) {
         try {
            byte[] symmetricKey = (byte[])data._payload._symmetricKey;
            ConverterUtilities.writeByteArray(buffer, 25, KeyStoreUtilitiesInternal.getKeyMaterial(symmetricKey, 0, symmetricKey.length));
            ConverterUtilities.writeByteArray(buffer, 20, KeyStoreUtilitiesInternal.getSalt(symmetricKey, 0, symmetricKey.length));
            ConverterUtilities.writeByteArray(buffer, 21, KeyStoreUtilitiesInternal.getHash(symmetricKey));
            ConverterUtilities.writeInt(buffer, 29, KeyStoreUtilitiesInternal.getPasswordVersion(symmetricKey));
            ConverterUtilities.writeStringIntellisync(buffer, 26, data._payload._symmetricKeyEncodingAlgorithm);
         } catch (KeyStoreDecodeException var16) {
         }
      }

      try {
         if (data.isPrivateKeySet() && data._payload._privateKey instanceof byte[]) {
            ConverterUtilities.writeInt(buffer, 22, KeyStoreUtilitiesInternal.getSecurityLevel((byte[])data._payload._privateKey));
         } else if (data.isSymmetricKeySet() && data._payload._symmetricKey instanceof byte[]) {
            ConverterUtilities.writeInt(buffer, 22, KeyStoreUtilitiesInternal.getSecurityLevel((byte[])data._payload._symmetricKey));
         } else {
            ConverterUtilities.writeInt(buffer, 22, 0);
         }
      } catch (KeyStoreDecodeException e) {
         ConverterUtilities.writeInt(buffer, 22, 0);
      }

      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      IntHashtable hashtable = (IntHashtable)registry.get(-6244618566307895102L);
      if (hashtable != null) {
         Enumeration enumeration = hashtable.elements();

         while (enumeration.hasMoreElements()) {
            Registration registration = (Registration)enumeration.nextElement();
            if (registration.getUp()) {
               byte[][][] associations = data.getAssociatedData(registration.getAssociatedDataConstant());
               if (associations != null) {
                  for (int m = 0; m < associations.length; m++) {
                     ConverterUtilities.writeByteArray(buffer, registration.getSyncConstant(), (byte[])associations[m]);
                  }
               }
            }
         }
      }

      ConverterUtilities.convertInt(buffer, 24, data.isPrivateKeySet() ? 1 : 0, 1);
      return true;
   }

   @Override
   public SyncObject convert(DataBuffer param1, int param2, int param3) {
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
      // 006: aconst_null
      // 007: astore 6
      // 009: bipush 0
      // 00a: i2l
      // 00b: lstore 7
      // 00d: aconst_null
      // 00e: astore 9
      // 010: aconst_null
      // 011: astore 10
      // 013: aconst_null
      // 014: astore 11
      // 016: aconst_null
      // 017: astore 12
      // 019: aconst_null
      // 01a: astore 13
      // 01c: aconst_null
      // 01d: astore 14
      // 01f: aconst_null
      // 020: astore 15
      // 022: aconst_null
      // 023: astore 16
      // 025: aconst_null
      // 026: astore 17
      // 028: aconst_null
      // 029: astore 18
      // 02b: bipush -2
      // 02d: istore 19
      // 02f: bipush -1
      // 031: i2l
      // 032: lstore 20
      // 034: bipush -1
      // 036: i2l
      // 037: lstore 22
      // 039: bipush -1
      // 03b: i2l
      // 03c: lstore 24
      // 03e: bipush -1
      // 040: i2l
      // 041: lstore 26
      // 043: bipush -1
      // 045: istore 28
      // 047: bipush 0
      // 048: newarray 10
      // 04a: astore 29
      // 04c: bipush 0
      // 04d: newarray 11
      // 04f: astore 30
      // 051: bipush 0
      // 052: newarray 11
      // 054: astore 31
      // 056: bipush 0
      // 057: istore 32
      // 059: aconst_null
      // 05a: astore 33
      // 05c: bipush -1
      // 05e: istore 34
      // 060: bipush 1
      // 061: istore 35
      // 063: aload 1
      // 064: bipush 27
      // 066: invokestatic net/rim/device/api/synchronization/ConverterUtilities.findType (Lnet/rim/device/api/util/DataBuffer;I)Z
      // 069: ifeq 072
      // 06c: aload 1
      // 06d: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 070: istore 35
      // 072: invokestatic net/rim/device/api/system/ApplicationRegistry.getApplicationRegistry ()Lnet/rim/device/api/system/ApplicationRegistry;
      // 075: astore 36
      // 077: aload 36
      // 079: ldc2_w -6244618566307895102
      // 07c: invokevirtual net/rim/device/api/system/ApplicationRegistry.get (J)Ljava/lang/Object;
      // 07f: checkcast java/lang/Object
      // 082: astore 37
      // 084: aload 37
      // 086: ifnonnull 08c
      // 089: goto 146
      // 08c: aload 37
      // 08e: invokevirtual net/rim/device/api/util/IntHashtable.elements ()Ljava/util/Enumeration;
      // 091: astore 38
      // 093: bipush 0
      // 094: istore 40
      // 096: bipush 0
      // 097: istore 41
      // 099: aload 38
      // 09b: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 0a0: ifne 0a6
      // 0a3: goto 146
      // 0a6: aload 1
      // 0a7: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 0aa: aload 38
      // 0ac: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0b1: checkcast net/rim/device/api/crypto/keystore/Registration
      // 0b4: astore 39
      // 0b6: aload 39
      // 0b8: invokevirtual net/rim/device/api/crypto/keystore/Registration.getDown ()Z
      // 0bb: ifeq 099
      // 0be: aload 39
      // 0c0: invokevirtual net/rim/device/api/crypto/keystore/Registration.getSyncConstant ()I
      // 0c3: istore 40
      // 0c5: bipush 0
      // 0c6: istore 41
      // 0c8: bipush 0
      // 0c9: multianewarray 1810 1
      // 0cd: astore 42
      // 0cf: aload 1
      // 0d0: iload 40
      // 0d2: invokestatic net/rim/device/api/synchronization/ConverterUtilities.findType (Lnet/rim/device/api/util/DataBuffer;I)Z
      // 0d5: ifeq 0f0
      // 0d8: aload 42
      // 0da: iinc 41 1
      // 0dd: iload 41
      // 0df: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 0e2: aload 42
      // 0e4: iload 41
      // 0e6: bipush 1
      // 0e7: isub
      // 0e8: aload 1
      // 0e9: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 0ec: aastore
      // 0ed: goto 0cf
      // 0f0: aload 13
      // 0f2: ifnonnull 0fb
      // 0f5: bipush 0
      // 0f6: anewarray 1830
      // 0f9: astore 13
      // 0fb: aload 13
      // 0fd: arraylength
      // 0fe: istore 43
      // 100: aload 13
      // 102: iload 43
      // 104: iload 41
      // 106: iadd
      // 107: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 10a: iload 41
      // 10c: anewarray 1838
      // 10f: astore 44
      // 111: bipush 0
      // 112: istore 45
      // 114: iload 45
      // 116: iload 41
      // 118: if_icmpge 137
      // 11b: aload 44
      // 11d: iload 45
      // 11f: new net/rim/device/api/crypto/BlockEncryptor
      // 122: dup
      // 123: aload 39
      // 125: invokevirtual net/rim/device/api/crypto/keystore/Registration.getAssociatedDataConstant ()J
      // 128: aload 42
      // 12a: iload 45
      // 12c: aaload
      // 12d: invokespecial net/rim/device/api/crypto/keystore/AssociatedData.<init> (J[B)V
      // 130: aastore
      // 131: iinc 45 1
      // 134: goto 114
      // 137: aload 44
      // 139: bipush 0
      // 13a: aload 13
      // 13c: iload 43
      // 13e: iload 41
      // 140: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 143: goto 099
      // 146: aload 1
      // 147: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 14a: bipush 0
      // 14b: istore 38
      // 14d: aload 1
      // 14e: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 151: ifeq 157
      // 154: goto 30f
      // 157: aload 1
      // 158: invokestatic net/rim/device/api/synchronization/ConverterUtilities.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 15b: istore 38
      // 15d: iload 38
      // 15f: tableswitch 133 0 29 425 133 425 425 142 151 160 169 178 187 196 205 214 223 425 425 425 425 246 232 278 296 287 305 425 255 269 425 425 416
      // 1e4: aload 1
      // 1e5: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 1e8: astore 4
      // 1ea: goto 14d
      // 1ed: aload 1
      // 1ee: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 1f1: astore 14
      // 1f3: goto 14d
      // 1f6: aload 1
      // 1f7: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 1fa: astore 9
      // 1fc: goto 14d
      // 1ff: aload 1
      // 200: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 203: istore 19
      // 205: goto 14d
      // 208: aload 1
      // 209: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readLong (Lnet/rim/device/api/util/DataBuffer;)J
      // 20c: lstore 20
      // 20e: goto 14d
      // 211: aload 1
      // 212: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readLong (Lnet/rim/device/api/util/DataBuffer;)J
      // 215: lstore 22
      // 217: goto 14d
      // 21a: aload 1
      // 21b: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readLong (Lnet/rim/device/api/util/DataBuffer;)J
      // 21e: lstore 24
      // 220: goto 14d
      // 223: aload 1
      // 224: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readLong (Lnet/rim/device/api/util/DataBuffer;)J
      // 227: lstore 26
      // 229: goto 14d
      // 22c: aload 1
      // 22d: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 230: istore 28
      // 232: goto 14d
      // 235: aload 1
      // 236: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 239: astore 15
      // 23b: goto 14d
      // 23e: aload 1
      // 23f: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 242: astore 5
      // 244: goto 14d
      // 247: aload 1
      // 248: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 24b: astore 10
      // 24d: aload 0
      // 24e: bipush 1
      // 24f: putfield net/rim/device/api/crypto/keystore/KeyStoreSync._anyPrivateKeysUploaded Z
      // 252: goto 14d
      // 255: aload 1
      // 256: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 259: astore 16
      // 25b: goto 14d
      // 25e: aload 1
      // 25f: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 262: astore 11
      // 264: aload 0
      // 265: bipush 1
      // 266: putfield net/rim/device/api/crypto/keystore/KeyStoreSync._anyPrivateKeysUploaded Z
      // 269: goto 14d
      // 26c: aload 1
      // 26d: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 270: astore 17
      // 272: goto 14d
      // 275: aload 1
      // 276: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 279: astore 12
      // 27b: goto 14d
      // 27e: aload 1
      // 27f: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 282: istore 32
      // 284: goto 14d
      // 287: aload 1
      // 288: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 28b: astore 33
      // 28d: goto 14d
      // 290: aload 1
      // 291: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 294: astore 39
      // 296: aload 39
      // 298: arraylength
      // 299: bipush 12
      // 29b: if_icmpne 2d7
      // 29e: aload 29
      // 2a0: arraylength
      // 2a1: istore 40
      // 2a3: aload 29
      // 2a5: iinc 40 1
      // 2a8: iload 40
      // 2aa: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 2ad: aload 30
      // 2af: iload 40
      // 2b1: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 2b4: aload 30
      // 2b6: iload 40
      // 2b8: bipush 1
      // 2b9: isub
      // 2ba: aload 39
      // 2bc: bipush 0
      // 2bd: bipush 8
      // 2bf: invokestatic net/rim/device/api/crypto/keystore/KeyStoreSync.convert ([BII)J
      // 2c2: lastore
      // 2c3: aload 29
      // 2c5: iload 40
      // 2c7: bipush 1
      // 2c8: isub
      // 2c9: aload 39
      // 2cb: bipush 8
      // 2cd: bipush 4
      // 2cf: invokestatic net/rim/device/api/crypto/keystore/KeyStoreSync.convert ([BII)J
      // 2d2: l2i
      // 2d3: iastore
      // 2d4: goto 14d
      // 2d7: aload 39
      // 2d9: arraylength
      // 2da: bipush 8
      // 2dc: if_icmpne 2fd
      // 2df: aload 31
      // 2e1: arraylength
      // 2e2: istore 40
      // 2e4: aload 31
      // 2e6: iload 40
      // 2e8: bipush 1
      // 2e9: iadd
      // 2ea: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 2ed: aload 31
      // 2ef: iload 40
      // 2f1: aload 39
      // 2f3: bipush 0
      // 2f4: bipush 8
      // 2f6: invokestatic net/rim/device/api/crypto/keystore/KeyStoreSync.convert ([BII)J
      // 2f9: lastore
      // 2fa: goto 14d
      // 2fd: aconst_null
      // 2fe: areturn
      // 2ff: aload 1
      // 300: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 303: istore 34
      // 305: goto 14d
      // 308: aload 1
      // 309: invokestatic net/rim/device/api/synchronization/ConverterUtilities.skipField (Lnet/rim/device/api/util/DataBuffer;)V
      // 30c: goto 14d
      // 30f: aload 15
      // 311: ifnonnull 31a
      // 314: aload 0
      // 315: getfield net/rim/device/api/crypto/keystore/KeyStoreSync.MSCAPI_STRING Ljava/lang/String;
      // 318: astore 15
      // 31a: aload 5
      // 31c: ifnull 32f
      // 31f: aload 5
      // 321: aload 15
      // 323: invokestatic net/rim/device/api/crypto/encoder/PublicKeyDecoder.decode ([BLjava/lang/String;)Lnet/rim/device/api/crypto/PublicKey;
      // 326: astore 6
      // 328: aload 6
      // 32a: invokeinterface net/rim/device/api/crypto/PublicKey.verify ()V 1
      // 32f: aload 16
      // 331: ifnonnull 33a
      // 334: aload 0
      // 335: getfield net/rim/device/api/crypto/keystore/KeyStoreSync.MSCAPI_STRING Ljava/lang/String;
      // 338: astore 16
      // 33a: iload 34
      // 33c: bipush -1
      // 33e: if_icmpne 35a
      // 341: bipush 0
      // 342: istore 34
      // 344: iload 32
      // 346: bipush 2
      // 348: if_icmpeq 352
      // 34b: iload 32
      // 34d: bipush 3
      // 34f: if_icmpne 35a
      // 352: invokestatic net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStoreManagerHelper;
      // 355: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getPasswordVersion ()I
      // 358: istore 34
      // 35a: aload 10
      // 35c: ifnonnull 364
      // 35f: aload 11
      // 361: ifnull 372
      // 364: iload 32
      // 366: lload 7
      // 368: invokestatic net/rim/device/api/crypto/keystore/KeyStoreUtilitiesInternal.getAppropriateSecurityLevel (IJ)I
      // 36b: iload 32
      // 36d: if_icmpeq 372
      // 370: aconst_null
      // 371: areturn
      // 372: aload 10
      // 374: ifnull 386
      // 377: aload 10
      // 379: aload 12
      // 37b: aload 33
      // 37d: iload 34
      // 37f: iload 32
      // 381: invokestatic net/rim/device/api/crypto/keystore/KeyStoreUtilitiesInternal.encode ([B[B[BII)[B
      // 384: astore 10
      // 386: aload 11
      // 388: ifnull 39a
      // 38b: aload 11
      // 38d: aload 12
      // 38f: aload 33
      // 391: iload 34
      // 393: iload 32
      // 395: invokestatic net/rim/device/api/crypto/keystore/KeyStoreUtilitiesInternal.encode ([B[B[BII)[B
      // 398: astore 11
      // 39a: iload 19
      // 39c: bipush -2
      // 39e: if_icmpeq 3b6
      // 3a1: new net/rim/device/api/crypto/certificate/CertificateStatus
      // 3a4: dup
      // 3a5: iload 19
      // 3a7: lload 20
      // 3a9: lload 22
      // 3ab: lload 24
      // 3ad: lload 26
      // 3af: iload 28
      // 3b1: invokespecial net/rim/device/api/crypto/certificate/CertificateStatus.<init> (IJJJJI)V
      // 3b4: astore 18
      // 3b6: aload 9
      // 3b8: ifnull 3c0
      // 3bb: aload 0
      // 3bc: bipush 1
      // 3bd: putfield net/rim/device/api/crypto/keystore/KeyStoreSync._anyIncomingCerts Z
      // 3c0: new net/rim/device/api/crypto/keystore/KeyStoreDataHelper
      // 3c3: dup
      // 3c4: aload 13
      // 3c6: aload 4
      // 3c8: aload 10
      // 3ca: aload 16
      // 3cc: aload 11
      // 3ce: aload 17
      // 3d0: aload 6
      // 3d2: lload 7
      // 3d4: aload 9
      // 3d6: aload 14
      // 3d8: aload 18
      // 3da: iload 3
      // 3db: aload 29
      // 3dd: aload 30
      // 3df: aload 31
      // 3e1: invokespecial net/rim/device/api/crypto/keystore/KeyStoreDataHelper.<init> ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;[BLjava/lang/String;[BLjava/lang/String;Lnet/rim/device/api/crypto/PublicKey;J[BLjava/lang/String;Lnet/rim/device/api/crypto/certificate/CertificateStatus;I[I[J[J)V
      // 3e4: areturn
      // 3e5: astore 35
      // 3e7: aconst_null
      // 3e8: areturn
      // 3e9: astore 35
      // 3eb: aconst_null
      // 3ec: areturn
      // try (60 -> 313): 419 null
      // try (314 -> 363): 419 null
      // try (364 -> 418): 419 null
      // try (60 -> 313): 422 null
      // try (314 -> 363): 422 null
      // try (364 -> 418): 422 null
   }

   static long convert(byte[] x) {
      return convert(x, 0, x == null ? 0 : x.length);
   }

   static long convert(byte[] x, int offset, int length) {
      if (length <= 8 && x != null && offset >= 0 && length >= 0 && x.length - length >= offset) {
         long result = 0;
         int totalLength = offset + length;

         for (int i = offset; i < totalLength; i++) {
            long temp = (long)x[i] << (length - i) * 8 - 8;
            temp &= (long)255 << (length - i) * 8 - 8;
            result |= temp;
         }

         return result;
      } else {
         throw new Object();
      }
   }

   static byte[] convert(long x) {
      byte[] result = new byte[8];

      for (int i = 7; i >= 0; i--) {
         result[i] = (byte)x;
         x >>>= 8;
      }

      return result;
   }
}
