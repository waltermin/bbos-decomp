package net.rim.device.api.crypto.tls;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.util.DataBuffer;

public class HandshakeProtocol implements HandshakeProtocolConstants {
   public void helloRequest(DataBuffer _1) {
      throw null;
   }

   public void clientHello() {
      throw null;
   }

   public void serverHello(DataBuffer _1) {
      throw null;
   }

   public void serverCertificate(DataBuffer _1) {
      throw null;
   }

   public void serverKeyExchange(DataBuffer _1) {
      throw null;
   }

   public void serverCertificateRequest(DataBuffer _1) {
      throw null;
   }

   public void serverHelloDone(DataBuffer _1) {
      throw null;
   }

   public void clientCertificate() {
      throw null;
   }

   public void clientKeyExchange() {
      throw null;
   }

   public void clientCertificateVerify() {
      throw null;
   }

   public void changeCipherSpec(DataBuffer _1) {
      throw null;
   }

   public void finished(DataBuffer _1) {
      throw null;
   }

   public KeyMaterial generateKeyMaterial(byte[] _1, byte[] _2, byte[] _3) {
      throw null;
   }

   public byte[] getCipherSuites() {
      throw null;
   }

   public byte[] getCompressionAlgorithms() {
      return new byte[]{0};
   }

   protected void connect() {
      throw null;
   }

   public int getLocalVersion() {
      throw null;
   }

   public RandomStructure getRandom(int numRandomBytes) {
      byte[] randomData = new byte[numRandomBytes];
      RandomSource.getBytes(randomData);
      return new RandomStructure((int)(System.currentTimeMillis() / 1000 & -1), randomData);
   }
}
