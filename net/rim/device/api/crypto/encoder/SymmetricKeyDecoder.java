package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.system.ApplicationRegistry;

public class SymmetricKeyDecoder {
   private static Hashtable _decoderKeyHashtable;
   private static Hashtable _decoderHashtable;

   protected SymmetricKeyDecoder() {
   }

   public static SymmetricKey decode(InputStream encodedKey, String encodingAlgorithm) {
      return decode(encodedKey, encodingAlgorithm, null);
   }

   public static SymmetricKey decode(byte[] encodedKey, String encodingAlgorithm) {
      if (encodedKey == null) {
         throw new Object();
      } else {
         return decode((InputStream)(new Object(encodedKey)), encodingAlgorithm, null);
      }
   }

   public static SymmetricKey decode(InputStream encodedKey, String encodingAlgorithm, String keyAlgorithm) {
      try {
         if (encodedKey != null && encodingAlgorithm != null) {
            SymmetricKeyDecoder decoder = (SymmetricKeyDecoder)_decoderHashtable.get(encodingAlgorithm);
            if (decoder == null) {
               throw new Object(encodingAlgorithm);
            } else {
               return decoder.decodeKey(encodedKey, keyAlgorithm);
            }
         } else {
            throw new Object();
         }
      } finally {
         throw new InvalidKeyEncodingException();
      }
   }

   public static SymmetricKey decode(byte[] encodedKey, String encodingAlgorithm, String keyAlgorithm) {
      if (encodedKey == null) {
         throw new Object();
      } else {
         return decode((InputStream)(new Object(encodedKey)), encodingAlgorithm, keyAlgorithm);
      }
   }

   protected SymmetricKey decodeKey(InputStream _1, String _2) {
      throw null;
   }

   public static boolean register(SymmetricKeyDecoder decoder, SymmetricKeyDecoder baseDecoder) {
      if (decoder != null && baseDecoder != null) {
         String baseAlgorithm = baseDecoder.getEncodingAlgorithm();
         SymmetricKeyDecoder currentBaseDecoder = (SymmetricKeyDecoder)_decoderHashtable.get(baseAlgorithm);
         if (currentBaseDecoder == null) {
            _decoderHashtable.put(baseAlgorithm, baseDecoder);
         } else if (!currentBaseDecoder.getClass().getName().equals(baseDecoder.getClass().getName())) {
            return false;
         }

         String encodingAlgorithm = decoder.getEncodingAlgorithm();
         if (!encodingAlgorithm.equals(baseAlgorithm)) {
            return false;
         }

         String[] keyAlgorithms = decoder.getKeyAlgorithms();
         return Utility.registerAlgorithms(_decoderKeyHashtable, decoder, encodingAlgorithm, keyAlgorithms);
      } else {
         return false;
      }
   }

   protected static SymmetricKeyDecoder getDecoder(String encodingAlgorithm, String keyAlgorithm) {
      return (SymmetricKeyDecoder)Utility.getCoder(_decoderKeyHashtable, encodingAlgorithm, keyAlgorithm);
   }

   protected String getEncodingAlgorithm() {
      throw null;
   }

   protected String[] getKeyAlgorithms() {
      throw null;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _decoderHashtable = ar.getHashtable(6903919152049026831L);
      _decoderKeyHashtable = ar.getHashtable(4085561434257091375L);
   }
}
