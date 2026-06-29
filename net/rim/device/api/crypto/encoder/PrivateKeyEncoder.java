package net.rim.device.api.crypto.encoder;

import java.util.Hashtable;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.system.ApplicationRegistry;

public class PrivateKeyEncoder {
   private static Hashtable _encoderHashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(9040438085974277747L);

   protected PrivateKeyEncoder() {
   }

   public static EncodedKey encode(PrivateKey key) {
      return encode(key, "PKCS8");
   }

   public static EncodedKey encode(PrivateKey key, String encodingAlgorithm) {
      if (key == null) {
         throw new Object();
      }

      PrivateKeyEncoder encoder = getEncoder(encodingAlgorithm, key.getAlgorithm());
      return encoder.encodeKey(key);
   }

   protected EncodedKey encodeKey(PrivateKey _1) {
      throw null;
   }

   public static boolean register(PrivateKeyEncoder encoder) {
      return encoder == null ? false : Utility.registerAlgorithms(_encoderHashtable, encoder, encoder.getEncodingAlgorithm(), encoder.getKeyAlgorithms());
   }

   protected static PrivateKeyEncoder getEncoder(String encodingAlgorithm, String keyAlgorithm) {
      if (encodingAlgorithm != null && keyAlgorithm != null) {
         return (PrivateKeyEncoder)Utility.getCoder(_encoderHashtable, encodingAlgorithm, keyAlgorithm);
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
