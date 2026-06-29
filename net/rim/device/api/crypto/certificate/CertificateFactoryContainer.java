package net.rim.device.api.crypto.certificate;

final class CertificateFactoryContainer {
   public Certificate[] _certificates;
   public byte[][][] _encodings;
   public int[] _crcs;
   public int _numCerts;

   public CertificateFactoryContainer(int length) {
      this._certificates = new Certificate[length];
      this._encodings = new byte[length][][];
      this._crcs = new int[length];
   }
}
