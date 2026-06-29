package net.rim.device.api.crypto.encoder;

import net.rim.device.api.util.Arrays;

public class EncodedKey {
   private byte[] _encodedKey;
   private String _encodingAlgorithm;

   public EncodedKey(byte[] encodedKey, String encodingAlgorithm) {
      this._encodedKey = Arrays.copy(encodedKey);
      this._encodingAlgorithm = encodingAlgorithm;
   }

   public byte[] getEncodedKey() {
      return Arrays.copy(this._encodedKey);
   }

   public String getEncodingAlgorithm() {
      return this._encodingAlgorithm;
   }
}
