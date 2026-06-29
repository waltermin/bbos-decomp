package net.rim.wica.transport.handshake;

public class EncodedCertificate {
   private byte[] _certificate;

   public EncodedCertificate(byte[] certificate) {
      this._certificate = certificate;
   }

   public byte[] getCertificate() {
      return this._certificate;
   }
}
