package net.rim.device.apps.internal.smartcard.gsacac;

import net.rim.device.api.crypto.CryptoTokenPrivateKeyData;
import net.rim.device.api.smartcard.SmartCardID;
import net.rim.device.api.util.Persistable;

final class CACCryptoTokenData implements CryptoTokenPrivateKeyData, Persistable {
   private SmartCardID _id;
   private byte _file;

   public CACCryptoTokenData(SmartCardID id, byte file) {
      this._id = id;
      this._file = file;
   }

   public final SmartCardID getSmartCardID() {
      return this._id;
   }

   public final byte getFile() {
      return this._file;
   }
}
