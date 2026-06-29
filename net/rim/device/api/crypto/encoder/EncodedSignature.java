package net.rim.device.api.crypto.encoder;

import net.rim.device.api.util.Arrays;

public class EncodedSignature {
   private byte[] _encodedSignature;
   private String _encodingAlgorithm;

   public EncodedSignature(byte[] encodedSignature, String encodingAlgorithm) {
      this._encodedSignature = encodedSignature;
      this._encodingAlgorithm = encodingAlgorithm;
   }

   public byte[] getEncodedSignature() {
      return Arrays.copy(this._encodedSignature);
   }

   public String getEncodingAlgorithm() {
      return this._encodingAlgorithm;
   }
}
