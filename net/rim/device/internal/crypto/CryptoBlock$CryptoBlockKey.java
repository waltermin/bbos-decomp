package net.rim.device.internal.crypto;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.util.Date;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.LED;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentInternal;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.Memory;

final class CryptoBlock$CryptoBlockKey implements Persistable, SyncObject {
   String _name;
   String _id;
   byte _algorithm;
   int _uid;
   int _keyLength;
   byte[] _data;
   long _expireTime;
   boolean _enterpriseClassKey;
   static final int CBK_NAME;
   static final int CBK_ID;
   static final int CBK_ALGORITHM;
   static final int CBK_KEY_LENGTH;
   static final int CBK_DATA;
   static final int CBK_EXPIRE_TIME;
   static final int CBK_ENTERPRISE_CLASS_KEY;
   static final int CBK_DEVICE_KEY;
   static final int CBK_DEVICE_KEY_ENCODED;
   static final int CBK_MASK_ALL;
   private static final long ID;
   private static PersistentObject _persistentDeviceKey = RIMPersistentStore.getPersistentObject(-6938461211042858691L);
   private static byte[][][] _deviceKey;

   final String getHashKey(boolean useID) {
      return useID ? this._id : this._name;
   }

   final void logAction(String action) {
      if (!InternalServices.isDeviceSecure() && !PersistentContent.isEncryptionEnabled()) {
         String msg = "CB " + action + ':' + this.toString();
         System.out.println(msg);
         EventLogger.logEvent(-8415036407170758647L, msg.getBytes(), 0);
      }
   }

   final byte[] getKey() {
      try {
         byte[] key = Memory.allocRAMOnlyBytes(this._keyLength);
         EncryptionUtilities.decrypt(getDeviceKey(), this._data, 0, this._data.length, key, 0);
         PersistentContent.markAsPlaintext(key);
         return key;
      } catch (IllegalStateException e) {
         System.out.println("CryptoBlock Content Protection IllegalStateException thrown");
         throw e;
      }
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   CryptoBlock$CryptoBlockKey(DataInput input, String name, long expireTime, boolean enterpriseClassKey) {
      try {
         PersistentContent.markAsPlaintext(input);
         this._algorithm = input.readByte();
         this._id = CryptoBlock.readKeyID(input);
         this._uid = UIDGenerator.getUID();
         this._keyLength = CryptoBlock.getKeyLength(this._algorithm);
         byte[] data = Memory.allocRAMOnlyBytes(this._keyLength);
         PersistentContent.markAsPlaintext(data);
         input.readFully(data);
         RandomSource.add(data);
         RandomSource.add(name);
         this._data = new byte[EncryptionUtilities.getCiphertextLength(this._keyLength)];
         EncryptionUtilities.encrypt(getDeviceKey(), data, 0, this._keyLength, this._data, 0);
         this._name = name;
         this._expireTime = expireTime;
         this._enterpriseClassKey = enterpriseClassKey;
      } catch (IOException e) {
         throw new CryptoBlockException();
      }
   }

   static final SyncObject convert(DataBuffer buff, int uid, Object[] key) {
      try {
         String name = null;
         String id = null;
         byte algorithm = 0;
         int keyLength = 0;
         byte[] data = null;
         long expire = 0;
         boolean enterprise = false;
         int mask = 0;

         while (true) {
            int tag = ConverterUtilities.getType(buff);
            switch (tag) {
               case -1:
                  ConverterUtilities.skipField(buff);
                  continue;
               case 0:
               default:
                  name = ConverterUtilities.readString(buff);
                  break;
               case 1:
                  id = ConverterUtilities.readString(buff);
                  break;
               case 2:
                  algorithm = (byte)ConverterUtilities.readInt(buff);
                  break;
               case 3:
                  keyLength = ConverterUtilities.readInt(buff);
                  break;
               case 4:
                  data = ConverterUtilities.readByteArray(buff);
                  break;
               case 5:
                  expire = ConverterUtilities.readLong(buff);
                  break;
               case 6:
                  enterprise = ConverterUtilities.readInt(buff) != 0;
                  break;
               case 7:
                  key[0] = ConverterUtilities.readByteArray(buff);
                  break;
               case 8:
                  char[][][] encodedDeviceKey = ConverterUtilities.readCharArrayArray(buff, false);
                  key[0] = encodedDeviceKey[0];
                  tag = 7;
            }

            mask |= 1 << tag;
            if (tag == 0 && mask == 255) {
               return new CryptoBlock$CryptoBlockKey(name, id, algorithm, uid, keyLength, data, expire, enterprise);
            }
         }
      } catch (EOFException var14) {
         return null;
      }
   }

   static final int sameDeviceKey(Object oldKey) {
      synchronized (_persistentDeviceKey) {
         Object currKey = _persistentDeviceKey.getContents();
         if (!(oldKey instanceof byte[])) {
            if (oldKey instanceof char[]) {
               char[] key1 = (char[])oldKey;
               if (currKey instanceof char[]) {
                  char[] key2 = (char[])currKey;
                  return Arrays.equals(key1, key2) ? 1 : 0;
               }

               if (currKey instanceof byte[]) {
                  return 0;
               }
            }
         } else {
            byte[] key1 = (byte[])oldKey;
            if (currKey instanceof byte[]) {
               byte[] key2 = (byte[])currKey;
               return Arrays.equals(key1, key2) ? 1 : 0;
            }

            if (currKey instanceof char[]) {
               return 0;
            }
         }

         return -1;
      }
   }

   static final void setDeviceKey(Object key, boolean refreshDeviceKey) {
      synchronized (_persistentDeviceKey) {
         _persistentDeviceKey.setContents(key, 4801362);
         _persistentDeviceKey.commit();
         if (refreshDeviceKey) {
            refreshDeviceKey();
         }
      }
   }

   private static final void refreshDeviceKey() {
      synchronized (_persistentDeviceKey) {
         _deviceKey[0] = null;
         getDeviceKey();
      }
   }

   private static final boolean getEncryptFlag() {
      return PersistentContent.isEncryptionEnabled() && ITPolicy.getBoolean(24, 53, false);
   }

   private static final byte[] getDeviceKey() {
      if (_deviceKey == null || _deviceKey[0] == null) {
         synchronized (_persistentDeviceKey) {
            ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
            boolean injectKey = false;
            if (_deviceKey == null) {
               _deviceKey = (byte[][][])((byte[][])appRegistry.get(-6938461211042858691L));
               if (_deviceKey == null) {
                  _deviceKey = new byte[1][][];
                  injectKey = true;
               }
            }

            if (_deviceKey[0] == null) {
               Object encoding = _persistentDeviceKey.getContents();
               if (encoding == null) {
                  _deviceKey[0] = (byte[][])Memory.allocRAMOnlyBytes(32);
                  RandomSource.getBytes((byte[])_deviceKey[0]);
                  encoding = PersistentContent.encode(Arrays.copy((byte[])_deviceKey[0]), false, getEncryptFlag());
                  setDeviceKey(encoding, false);
               } else {
                  _deviceKey[0] = (byte[][])PersistentContentInternal.decodeByteArray(encoding, false, true);
                  if (PersistentContent.isEncrypted(encoding)) {
                     LED.setState(0);
                     RadioInternal.reactivateRadios();
                  }
               }

               if (injectKey) {
                  appRegistry.put(-6938461211042858691L, new ControlledAccess(_deviceKey, CodeSigningKey.getBuiltInKey(4801362)));
               }
            }
         }
      }

      return (byte[])_deviceKey[0];
   }

   public static final boolean areMasterKeysEncrypted() {
      synchronized (_persistentDeviceKey) {
         return PersistentContent.isEncrypted(_persistentDeviceKey.getContents());
      }
   }

   public static final boolean areMasterKeysAvailable() {
      try {
         return getDeviceKey() != null;
      } catch (IllegalStateException e) {
         return false;
      }
   }

   static final boolean convert(SyncObject object, DataBuffer buff) {
      if (!(object instanceof CryptoBlock$CryptoBlockKey)) {
         return false;
      }

      CryptoBlock$CryptoBlockKey cbk = (CryptoBlock$CryptoBlockKey)object;
      ConverterUtilities.writeString(buff, 1, cbk._id);
      ConverterUtilities.writeInt(buff, 2, cbk._algorithm);
      ConverterUtilities.writeInt(buff, 3, cbk._keyLength);
      ConverterUtilities.writeByteArray(buff, 4, cbk._data);
      ConverterUtilities.writeLong(buff, 5, cbk._expireTime);
      ConverterUtilities.writeInt(buff, 6, cbk._enterpriseClassKey ? 1 : 0);
      Object encoding = _persistentDeviceKey.getContents();
      if (!(encoding instanceof byte[])) {
         if (!(encoding instanceof char[])) {
            return false;
         }

         char[] key = (char[])encoding;
         char[][][] encodedKey = new char[][][]{(char[][])key};
         ConverterUtilities.writeCharArrayArray(buff, 8, encodedKey);
      } else {
         byte[] key = (byte[])encoding;
         ConverterUtilities.writeByteArray(buff, 7, key);
      }

      ConverterUtilities.writeString(buff, 0, cbk._name);
      return true;
   }

   public static final void registerPersistentContentListener() {
      PersistentContent.addListener(new CryptoBlock$CryptoBlockKey$MyPersistentContentListener(null));
   }

   CryptoBlock$CryptoBlockKey(String name, String id, byte algorithm, int uid, int keyLength, byte[] data, long expireTime, boolean enterpriseClassKey) {
      this._name = name;
      this._id = id;
      this._algorithm = algorithm;
      this._uid = uid;
      this._keyLength = keyLength;
      this._data = data;
      this._expireTime = expireTime;
      this._enterpriseClassKey = enterpriseClassKey;
      RandomSource.add(data);
      RandomSource.add(name);
   }

   @Override
   public final String toString() {
      StringBuffer buff = new StringBuffer();
      buff.append('"').append(this._name).append('"');
      buff.append('(').append(this._id).append(')');
      buff.append(Integer.toHexString(this._uid));
      if (this._expireTime != -1) {
         Date d = new Date(this._expireTime);
         buff.append(' ').append(d);
      }

      return buff.toString();
   }

   @Override
   public final boolean equals(Object o) {
      if (!(o instanceof CryptoBlock$CryptoBlockKey)) {
         return super.equals(o);
      }

      CryptoBlock$CryptoBlockKey that = (CryptoBlock$CryptoBlockKey)o;
      return this == that
         ? true
         : this._algorithm == that._algorithm
            && this._uid == that._uid
            && this._keyLength == that._keyLength
            && this._expireTime == that._expireTime
            && this._enterpriseClassKey == that._enterpriseClassKey
            && this._name.equals(that._name)
            && this._id.equals(that._id)
            && Arrays.equals(this.getKey(), that.getKey());
   }
}
