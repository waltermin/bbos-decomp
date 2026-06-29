package net.rim.device.apps.internal.smartcard.piv;

import net.rim.device.api.crypto.CryptoTokenPrivateKeyData;
import net.rim.device.api.smartcard.SmartCardID;
import net.rim.device.api.util.Persistable;

final class PIVCryptoTokenData implements CryptoTokenPrivateKeyData, Persistable {
   private SmartCardID _id;
   private byte _keyReference;
   private byte _algorithmReference;

   public PIVCryptoTokenData(SmartCardID id, byte algorithmReference, byte keyReference) {
      this._id = id;
      this._algorithmReference = algorithmReference;
      this._keyReference = keyReference;
   }

   public final SmartCardID getSmartCardID() {
      return this._id;
   }

   public final byte getAlgorithmReference() {
      return this._algorithmReference;
   }

   public final byte getKeyReference() {
      return this._keyReference;
   }
}
