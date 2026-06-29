package net.rim.device.apps.internal.activation;

import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.crypto.OTAKeyGenCrypto;

public final class OTAKeyGenEvent {
   byte _keyGenerationType;
   long _timeStamp;
   int _transactionId;
   String _emailAddress;
   byte _command;
   byte _requestType;
   byte[] _deviceAuthenticationPublicKey;
   byte[] _deviceLongTermPublicKey;
   byte[] _serviceAuthenticationPublicKey;
   byte[] _serviceLongTermPublicKey;
   byte _encryptionAlgorithm;
   int _keySequenceHint;
   byte[] _keyHash;
   String _fullKeyId;
   byte _reKeyAlgorithm;
   String _serviceUID;
   byte[] _deviceCapabilities;
   byte _abortReason;
   int _pin;
   byte _networkType;
   byte[] _masterKey;
   OTAKeyGenCrypto _cryptoContext;

   OTAKeyGenEvent() {
      this._transactionId = UIDGenerator.getUID();
      this.init();
   }

   OTAKeyGenEvent(int transactionId) {
      this._transactionId = transactionId;
      this.init();
   }

   private final void init() {
      this._timeStamp = System.currentTimeMillis();
      this._encryptionAlgorithm = 1;
      this._abortReason = 0;
      this._pin = DeviceInfo.getDeviceId();
      this._networkType = (byte)RadioInfo.getNetworkType();
      this._reKeyAlgorithm = 1;
   }

   public final int getTransactionId() {
      return this._transactionId;
   }
}
