package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;

class PGPUniversalKeyCache$CachedKeyExpiryData {
   private long _expiryTime;
   private Certificate _certificate;
   private KeyStoreData _keyStoreData;

   PGPUniversalKeyCache$CachedKeyExpiryData(Certificate certificate, KeyStoreData keyStoreData) {
      this._expiryTime = Math.min(certificate.getNotAfter(), System.currentTimeMillis() + 86400000);
      this._certificate = certificate;
      this._keyStoreData = keyStoreData;
   }

   long getExpiryTime() {
      return this._expiryTime;
   }

   Certificate getCertificate() {
      return this._certificate;
   }

   KeyStoreData getKeyStoreData() {
      return this._keyStoreData;
   }
}
