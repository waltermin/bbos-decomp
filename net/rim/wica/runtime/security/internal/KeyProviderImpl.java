package net.rim.wica.runtime.security.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentInternal;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.vm.Array;
import net.rim.vm.Memory;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.util.LongVector;
import net.rim.wica.transport.security.Key;
import net.rim.wica.transport.security.KeyPair;
import net.rim.wica.transport.security.KeyProvider;
import net.rim.wica.transport.security.KeyProviderException;
import net.rim.wica.transport.security.KeyType;
import net.rim.wica.transport.security.SecurityProviderException;

final class KeyProviderImpl implements KeyProvider, PersistentContentListener {
   private SecurityProviderImpl _securityProvider;
   private PersistenceService _persistenceService;
   private LongHashtable _activeKeys;
   private LongHashtable _recoveryKeys;
   private LongVector _recoveryInfo;
   private int _lastRecorded;
   private KeyPair _keyPair;
   private LongHashtable _publicAGKeys;
   private static final int ACTIVE_INDEX_PRIMARY_REG_KEY = 0;
   private static final int ACTIVE_INDEX_SECONDARY_REG_KEY = 1;
   private static final int ACTIVE_INDEX_RESET_KEY = 2;
   private static final int RECOVERY_INDEX_PENDING_REG_KEY = 0;
   private static final int RECOVERY_INDEX_PENDING_RESET_KEY = 1;
   private static final int RECOVERY_INDEX_RESET_KEY = 2;
   static final int RECOVERY_INFO_MAX_SIZE = 10;

   final synchronized void setSecurityInfo(long agId, int securityVersion, Key[] keys, boolean reset) {
      if (securityVersion == 1) {
         this._publicAGKeys.put(agId, keys[0]);
         this.saveAGPublicKeys(agId);
      } else {
         Key[] activeKeys = (Key[])this._activeKeys.get(agId);
         if (activeKeys == null) {
            this._publicAGKeys.remove(agId);
            this.saveAGPublicKeys(agId);
            activeKeys = new Key[3];
            this._activeKeys.put(agId, activeKeys);
         } else if (reset) {
            activeKeys[1] = null;
         } else {
            activeKeys[1] = activeKeys[0];
         }

         activeKeys[0] = keys[0];
         this.saveRegKeys(agId);
         if (keys.length > 1) {
            activeKeys[2] = keys[1];
            this.saveResetKeys(agId);
         }

         this.removeRecoveryInfo(agId);
      }
   }

   final synchronized void wipeSecurityInfo(long agId) {
      this.removeSecurityInfo(agId, false);
   }

   final synchronized void cacheSecurityInfo(long agId) {
      this.removeSecurityInfo(agId, true);
   }

   final synchronized void setRecoveryKeys(long agId, Key[] keys) {
      Key[] info = this.findRecoveryInfo(agId, true);
      info[0] = keys[0];
      info[1] = keys[1];
      this.saveRecoveryInfo(agId);
   }

   final synchronized Key getRecoveryResetKey(long id) {
      Key[] keys = (Key[])this._recoveryKeys.get(id);
      return keys == null ? null : keys[2];
   }

   final synchronized Key getPublicKey() {
      return this.getKeyPair().getPublicKey();
   }

   final synchronized Key getPendingRegistrationKey(long id) {
      Key[] keys = (Key[])this._recoveryKeys.get(id);
      return keys == null ? null : keys[0];
   }

   final synchronized Key getPendingResetKey(long id) {
      Key[] keys = (Key[])this._recoveryKeys.get(id);
      return keys == null ? null : keys[1];
   }

   @Override
   public final synchronized Key getSecondaryRegistrationKey(long id) {
      Key[] keys = (Key[])this._activeKeys.get(id);
      return keys == null ? null : keys[1];
   }

   @Override
   public final synchronized Key getResetKey(long id) {
      Key[] keys = (Key[])this._activeKeys.get(id);
      return keys == null ? null : keys[2];
   }

   @Override
   public final synchronized Key getPrimaryRegistrationKey(long id) {
      Key[] keys = (Key[])this._activeKeys.get(id);
      return keys == null ? null : keys[0];
   }

   @Override
   public final synchronized Key getRemotePublicKey(long id) {
      return (Key)this._publicAGKeys.get(id);
   }

   @Override
   public final synchronized Key getPrivateKey() {
      return this.getKeyPair().getPrivateKey();
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final synchronized void persistentContentModeChanged(int generation) {
      this.saveKeyPair();
      this.saveRegKeys(-1);
      this.recryptRecoveryRegKeys();
   }

   KeyProviderImpl(SecurityProviderImpl sp, PersistenceService ps) {
      this._securityProvider = sp;
      this._persistenceService = ps;
      PersistentContent.addWeakListener(this);
      this.loadAllKeys();
   }

   private final Key[] findRecoveryInfo(long id, boolean create) {
      Key[] keys = (Key[])this._recoveryKeys.get(id);
      if (keys == null && create) {
         keys = new Key[3];
         this._recoveryKeys.put(id, keys);
         int size = this._recoveryInfo.size();
         if (size == 10) {
            if (this._lastRecorded == size) {
               this._lastRecorded = 0;
            }

            long agId = this._recoveryInfo.elementAt(this._lastRecorded);
            this._recoveryKeys.remove(agId);
            this._recoveryInfo.setElementAt(id, this._lastRecorded);
            this._lastRecorded++;
         } else {
            this._recoveryInfo.addElement(id);
            this._lastRecorded = this._recoveryInfo.size();
         }
      }

      return keys;
   }

   private final void removeRecoveryInfo(long agId) {
      this._recoveryKeys.remove(agId);
      int index = this._recoveryInfo.indexOf(agId);
      if (index != -1) {
         if (this._lastRecorded > 0 && this._lastRecorded <= index + 1) {
            this._lastRecorded--;
         }

         this._recoveryInfo.removeElementAt(index);
      }

      this.saveRecoveryInfo(agId);
   }

   private final void saveRegKeys(long agId) {
      try {
         this._persistenceService.storeRegKeys(this.serializeKeys(this._activeKeys, 0, true));
         this._persistenceService.storeSecondaryRegKeys(this.serializeKeys(this._activeKeys, 1, true));
      } finally {
         return;
      }
   }

   private final void saveResetKeys(long agId) {
      try {
         this._persistenceService.storeResetKeys(this.serializeKeys(this._activeKeys, 2, false));
      } finally {
         return;
      }
   }

   private final void saveKeyPair() {
      if (this._keyPair != null) {
         try {
            byte[] encodedKey = this._securityProvider.encodeKey(this._keyPair.getPrivateKey());
            Object encryptedKey = this.encryptKey(encodedKey);
            encodedKey = this._securityProvider.encodeKey(this._keyPair.getPublicKey());
            this._persistenceService.storeREKeyPair(new Object[]{encryptedKey, encodedKey});
         } finally {
            return;
         }
      }
   }

   private final void saveAGPublicKeys(long agId) {
      try {
         this._persistenceService.storeAGPublicKeys(this.serializeKeys(this._publicAGKeys, -1, false));
      } finally {
         return;
      }
   }

   private final void saveRecoveryInfo(long agId) {
      DataBuffer regDataBuffer = (DataBuffer)(new Object());
      DataBuffer resetDataBuffer = (DataBuffer)(new Object());
      resetDataBuffer.writeByte(1);
      regDataBuffer.writeByte(1);
      int length = this._recoveryInfo.size();
      resetDataBuffer.writeCompressedInt(length);
      regDataBuffer.writeCompressedInt(length);

      try {
         byte[] emptyByteArray = new byte[0];

         for (int i = 0; i < length; i++) {
            long id = this._recoveryInfo.elementAt(i);
            Key[] keys = (Key[])this._recoveryKeys.get(id);
            if (keys != null) {
               resetDataBuffer.writeCompressedLong(id);
               regDataBuffer.writeCompressedLong(id);
               Key k = keys[1];
               byte[] encodedKey = k != null ? this._securityProvider.encodeKey(k) : emptyByteArray;
               resetDataBuffer.writeByteArray(encodedKey);
               k = keys[2];
               encodedKey = k != null ? this._securityProvider.encodeKey(k) : emptyByteArray;
               resetDataBuffer.writeByteArray(encodedKey);
               k = keys[0];
               if (k != null) {
                  encodedKey = this._securityProvider.encodeKey(k);
                  encodedKey = PersistentContent.convertEncodingToByteArray(this.encryptKey(encodedKey));
               } else {
                  encodedKey = emptyByteArray;
               }

               regDataBuffer.writeByteArray(encodedKey);
            }
         }

         resetDataBuffer.writeByte(this._lastRecorded);
         this._persistenceService.storeRecoveryResetKeys(resetDataBuffer.toArray());
         this._persistenceService.storeRecoveryRegKeys(regDataBuffer.toArray());
      } catch (SecurityProviderException e) {
         Logger.log("KP-SRIF");
      }
   }

   private final void recryptRecoveryRegKeys() {
      DataBuffer regDataBuffer = (DataBuffer)(new Object());
      regDataBuffer.writeByte(1);
      int length = this._recoveryInfo.size();
      regDataBuffer.writeCompressedInt(length);

      try {
         byte[] emptyByteArray = new byte[0];

         for (int i = 0; i < length; i++) {
            long id = this._recoveryInfo.elementAt(i);
            Key[] keys = (Key[])this._recoveryKeys.get(id);
            if (keys != null) {
               regDataBuffer.writeCompressedLong(id);
               Key k = keys[0];
               byte[] encodedKey = k != null ? this._securityProvider.encodeKey(k) : emptyByteArray;
               encodedKey = PersistentContent.convertEncodingToByteArray(this.encryptKey(encodedKey));
               regDataBuffer.writeByteArray(encodedKey);
            }
         }

         this._persistenceService.storeRecoveryRegKeys(regDataBuffer.toArray());
      } catch (SecurityProviderException e) {
         Logger.log("KP-RRKF");
      }
   }

   private final void loadRecoveryKeys() {
      this._recoveryKeys = (LongHashtable)(new Object());
      this._recoveryInfo = new LongVector();

      try {
         byte[] b = this._persistenceService.loadRecoveryResetKeys();
         if (b != null) {
            DataBuffer resetDataBuffer = (DataBuffer)(new Object(b, 0, b.length, true));
            b = this._persistenceService.loadRecoveryRegKeys();
            DataBuffer regDataBuffer = (DataBuffer)(b != null ? new Object(b, 0, b.length, true) : null);
            if (resetDataBuffer.readByte() == 1) {
               int length = resetDataBuffer.readCompressedInt();
               if (regDataBuffer != null) {
                  regDataBuffer.readByte();
                  regDataBuffer.readCompressedInt();
               }

               for (int i = 0; i < length; i++) {
                  long id = resetDataBuffer.readCompressedLong();
                  this._recoveryInfo.addElement(id);
                  Key[] keys = new Key[3];
                  this._recoveryKeys.put(id, keys);
                  byte[] encodedKey = resetDataBuffer.readByteArray();
                  keys[1] = encodedKey.length == 0 ? null : this._securityProvider.decodeKey(KeyType.AES, encodedKey);
                  encodedKey = resetDataBuffer.readByteArray();
                  keys[2] = encodedKey.length == 0 ? null : this._securityProvider.decodeKey(KeyType.AES, encodedKey);
                  if (regDataBuffer != null) {
                     if (regDataBuffer.readCompressedLong() != id) {
                        throw new Object("Incorrect id");
                     }

                     encodedKey = regDataBuffer.readByteArray();
                     if (encodedKey.length != 0) {
                        Object encoding = PersistentContent.convertByteArrayToEncoding(encodedKey);
                        encodedKey = this.decryptKey(encoding);
                        keys[0] = this._securityProvider.decodeKey(KeyType.AES, encodedKey);
                     } else {
                        keys[0] = null;
                     }
                  }
               }

               this._lastRecorded = resetDataBuffer.readByte();
               return;
            }
         }
      } finally {
         ;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void loadAllKeys() {
      this._activeKeys = (LongHashtable)(new Object());
      this._publicAGKeys = (LongHashtable)(new Object());
      this.loadRecoveryKeys();
      boolean var23 = false /* VF: Semaphore variable */;

      label122:
      try {
         var23 = true;
         this.createKeys(this._persistenceService.loadRegKeys(), this._activeKeys, 0, 3, KeyType.AES, true);
         this.createKeys(this._persistenceService.loadSecondaryRegKeys(), this._activeKeys, 1, 3, KeyType.AES, true);
         var23 = false;
      } finally {
         if (var23) {
            this._activeKeys.clear();
            break label122;
         }
      }

      boolean var18 = false /* VF: Semaphore variable */;

      label118:
      try {
         var18 = true;
         this.createKeys(this._persistenceService.loadResetKeys(), this._activeKeys, 2, 3, KeyType.AES, false);
         var18 = false;
      } finally {
         if (var18) {
            this._activeKeys.clear();
            break label118;
         }
      }

      boolean var13 = false /* VF: Semaphore variable */;

      label114:
      try {
         var13 = true;
         this.createKeys(this._persistenceService.loadAGPublicKeys(), this._publicAGKeys, -1, 0, KeyType.RSA, false);
         var13 = false;
      } finally {
         if (var13) {
            this._publicAGKeys.clear();
            break label114;
         }
      }

      boolean var8 = false /* VF: Semaphore variable */;

      try {
         var8 = true;
         Object[] e = this._persistenceService.loadREKeyPair();
         if (e == null) {
            this._keyPair = null;
            return;
         }

         Key privateKey = this._securityProvider.decodeKey(KeyType.ECDSA, this.decryptKey(e[0]), false);
         Key publicKey = this._securityProvider.decodeKey(KeyType.ECDSA, (byte[])e[1], true);
         this._keyPair = new KeyPair(privateKey, publicKey);
         var8 = false;
      } finally {
         if (var8) {
            this._keyPair = null;
            return;
         }
      }
   }

   private final void removeSecurityInfo(long agId, boolean cache) {
      if (this._activeKeys.containsKey(agId)) {
         Key[] keys = (Key[])this._activeKeys.get(agId);
         if (cache) {
            Key[] recoveryKeys = this.findRecoveryInfo(agId, true);
            recoveryKeys[2] = keys[2];
            recoveryKeys[1] = null;
            recoveryKeys[0] = null;
            this.saveRecoveryInfo(agId);
         } else {
            this.removeRecoveryInfo(agId);
         }

         this._activeKeys.remove(agId);
         this.saveRegKeys(agId);
         this.saveResetKeys(agId);
      } else {
         Key[] recoveryKeys = this.findRecoveryInfo(agId, false);
         if (recoveryKeys != null) {
            if (!cache) {
               this.removeRecoveryInfo(agId);
            } else {
               recoveryKeys[1] = null;
               recoveryKeys[0] = null;
               this.saveRecoveryInfo(agId);
            }
         }
      }

      if (this._publicAGKeys.containsKey(agId)) {
         this._publicAGKeys.remove(agId);
         this.saveAGPublicKeys(agId);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final KeyPair getKeyPair() {
      if (this._keyPair == null) {
         try {
            this._keyPair = this._securityProvider.generateKeyPair(KeyType.ECDSA);
            this.saveKeyPair();
         } catch (Throwable var3) {
            throw new KeyProviderException("Could not generate a private/public key pair", e);
         }
      }

      return this._keyPair;
   }

   private final byte[] decryptKey(Object encryptedKey) {
      if (encryptedKey == null) {
         return null;
      }

      byte[] key = PersistentContentInternal.decodeByteArray(encryptedKey, false, true);
      if (key == encryptedKey) {
         byte[] plaintext = (byte[])encryptedKey;
         PersistentContent.markAsPlaintext(plaintext);
         key = Memory.allocRAMOnlyBytes(plaintext.length);
         PersistentContent.markAsPlaintext(key);
         System.arraycopy(plaintext, 0, key, 0, plaintext.length);
      }

      return key;
   }

   private final Object encryptKey(byte[] key) {
      PersistentContent.markAsPlaintext(key);
      return key != null && PersistentContent.isEncryptionEnabled() ? PersistentContent.encode(key, false, true) : key;
   }

   private final byte[] serializeKeys(LongHashtable keys, int index, boolean protect) {
      if (keys.size() <= 0) {
         return new byte[0];
      }

      ByteArrayOutputStream out = (ByteArrayOutputStream)(new Object());
      DataOutputStream dataStream = (DataOutputStream)(new Object(out));
      dataStream.writeByte(1);
      dataStream.writeInt(keys.size());
      LongEnumeration i = keys.keys();

      while (i.hasMoreElements()) {
         long agId = i.nextElement();
         Key key;
         if (index == -1) {
            key = (Key)keys.get(agId);
         } else {
            Key[] keyArray = (Key[])keys.get(agId);
            key = keyArray[index];
         }

         dataStream.writeLong(agId);
         if (key != null) {
            byte[] encodedKey = this._securityProvider.encodeKey(key);
            if (protect) {
               encodedKey = PersistentContent.convertEncodingToByteArray(this.encryptKey(encodedKey));
            }

            dataStream.writeInt(encodedKey.length);
            dataStream.write(encodedKey);
         } else {
            dataStream.writeInt(0);
         }
      }

      return out.toByteArray();
   }

   private final void createKeys(byte[] store, LongHashtable keys, int index, int size, KeyType keyType, boolean isProtected) {
      if (store != null && store.length > 0) {
         ByteArrayInputStream in = (ByteArrayInputStream)(new Object(store));
         DataInputStream dataStream = (DataInputStream)(new Object(in));
         byte[] encodedKey = null;
         if (dataStream.readByte() == 1) {
            int count = dataStream.readInt();

            while (count-- > 0) {
               long agId = dataStream.readLong();
               int length = dataStream.readInt();
               Key keyValue = null;
               if (length != 0) {
                  if (encodedKey == null) {
                     encodedKey = new byte[length];
                  } else if (encodedKey.length != length) {
                     Array.resize(encodedKey, length);
                  }

                  dataStream.read(encodedKey, 0, length);
                  if (isProtected) {
                     Object encoding = PersistentContent.convertByteArrayToEncoding(encodedKey);
                     encodedKey = this.decryptKey(encoding);
                  }

                  keyValue = this._securityProvider.decodeKey(keyType, encodedKey);
               }

               if (size == 0) {
                  keys.put(agId, keyValue);
               } else {
                  Key[] k = (Key[])keys.get(agId);
                  if (k == null) {
                     k = new Key[3];
                     keys.put(agId, k);
                  }

                  k[index] = keyValue;
               }
            }
         }
      }
   }
}
