package net.rim.device.api.crypto.cms;

import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;

class Recipient {
   private Certificate _cert;
   private int _type;
   private PrivateKey _privateKey;
   private Certificate _originator;
   private byte[] _password;

   Recipient(Certificate cert, int type, PrivateKey privateKey, Certificate originator, byte[] password) {
      this._cert = cert;
      this._type = type;
      this._privateKey = privateKey;
      this._originator = originator;
      this._password = password;
   }

   Certificate getCertificate() {
      return this._cert;
   }

   int getType() {
      return this._type;
   }

   PrivateKey getPrivateKey() {
      return this._privateKey;
   }

   Certificate getOriginator() {
      return this._originator;
   }

   byte[] getPassword() {
      return this._password;
   }
}
