package net.rim.device.api.crypto.encoder;

import java.util.Hashtable;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.system.ApplicationRegistry;

public class SymmetricKeyEncoder {
   private static Hashtable _encoderHashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(-8585333447305736314L);

   protected SymmetricKeyEncoder() {
   }

   public static EncodedKey encode(SymmetricKey key) {
      return encode(key, "PKCS8");
   }

   public static EncodedKey encode(SymmetricKey key, String encodingAlgorithm) {
      if (key == null) {
         throw new IllegalArgumentException();
      }

      SymmetricKeyEncoder encoder = getEncoder(encodingAlgorithm, key.getAlgorithm());
      return encoder.encodeKey(key);
   }

   protected EncodedKey encodeKey(SymmetricKey _1) {
      throw null;
   }

   public static boolean register(SymmetricKeyEncoder encoder) {
      return encoder == null ? false : Utility.registerAlgorithms(_encoderHashtable, encoder, encoder.getEncodingAlgorithm(), encoder.getKeyAlgorithms());
   }

   protected static SymmetricKeyEncoder getEncoder(String encodingAlgorithm, String keyAlgorithm) {
      return (SymmetricKeyEncoder)Utility.getCoder(_encoderHashtable, encodingAlgorithm, keyAlgorithm);
   }

   protected String getEncodingAlgorithm() {
      throw null;
   }

   protected String[] getKeyAlgorithms() {
      throw null;
   }
}
