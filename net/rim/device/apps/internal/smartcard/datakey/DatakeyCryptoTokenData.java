package net.rim.device.apps.internal.smartcard.datakey;

import net.rim.device.api.crypto.CryptoTokenPrivateKeyData;
import net.rim.device.api.smartcard.SmartCardID;
import net.rim.device.api.util.Persistable;

final class DatakeyCryptoTokenData implements CryptoTokenPrivateKeyData, Persistable {
   private SmartCardID _id;
   private byte[] _privateFile;
   private byte[] _paramFile;

   public DatakeyCryptoTokenData(SmartCardID id, byte[] privateFile) {
      this(id, privateFile, null);
   }

   public DatakeyCryptoTokenData(SmartCardID id, byte[] privateFile, byte[] paramFile) {
      this._id = id;
      this._privateFile = privateFile;
      this._paramFile = paramFile;
   }

   public final SmartCardID getSmartCardID() {
      return this._id;
   }

   public final byte[] getPrivateFile() {
      return this._privateFile;
   }

   public final byte[] getParamFile() {
      return this._paramFile;
   }
}
