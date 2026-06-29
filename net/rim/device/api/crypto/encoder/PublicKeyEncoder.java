package net.rim.device.api.crypto.encoder;

import java.util.Hashtable;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.system.ApplicationRegistry;

public class PublicKeyEncoder {
   private static Hashtable _encoderHashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(5978763261827214617L);

   protected PublicKeyEncoder() {
   }

   public static EncodedKey encode(PublicKey key) {
      return encode(key, "X509", 0);
   }

   public static EncodedKey encode(PublicKey key, String encodingAlgorithm) {
      return encode(key, encodingAlgorithm, 0);
   }

   public static EncodedKey encode(PublicKey key, String encodingAlgorithm, long flags) {
      if (key == null) {
         throw new Object();
      }

      PublicKeyEncoder encoder = getEncoder(encodingAlgorithm, key.getAlgorithm());
      return encoder.encodeKey(key, flags);
   }

   protected EncodedKey encodeKey(PublicKey _1, long _2) {
      throw null;
   }

   public static boolean register(PublicKeyEncoder encoder) {
      return encoder == null ? false : Utility.registerAlgorithms(_encoderHashtable, encoder, encoder.getEncodingAlgorithm(), encoder.getKeyAlgorithms());
   }

   protected static PublicKeyEncoder getEncoder(String encodingAlgorithm, String keyAlgorithm) {
      if (encodingAlgorithm != null && keyAlgorithm != null) {
         return (PublicKeyEncoder)Utility.getCoder(_encoderHashtable, encodingAlgorithm, keyAlgorithm);
      } else {
         throw new Object();
      }
   }

   protected String getEncodingAlgorithm() {
      throw null;
   }

   protected String[] getKeyAlgorithms() {
      throw null;
   }
}
