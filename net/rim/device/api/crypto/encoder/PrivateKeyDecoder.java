package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.system.ApplicationRegistry;

public class PrivateKeyDecoder {
   private static Hashtable _decoderKeyHashtable;
   private static Hashtable _decoderHashtable;

   protected PrivateKeyDecoder() {
   }

   public static PrivateKey decode(InputStream encodedKey, String encodingAlgorithm, CryptoSystem cryptoSystem, String keyAlgorithm) throws InvalidKeyEncodingException {
      try {
         if (encodedKey != null && encodingAlgorithm != null) {
            PrivateKeyDecoder decoder = (PrivateKeyDecoder)_decoderHashtable.get(encodingAlgorithm);
            if (decoder == null) {
               throw new Object(encodingAlgorithm);
            } else {
               return decoder.decodeKey(encodedKey, cryptoSystem, keyAlgorithm);
            }
         } else {
            throw new Object();
         }
      } finally {
         throw new InvalidKeyEncodingException();
      }
   }

   public static PrivateKey decode(byte[] encodedKey, String encodingAlgorithm, CryptoSystem cryptoSystem, String keyAlgorithm) {
      if (encodedKey == null) {
         throw new Object();
      } else {
         return decode((InputStream)(new Object(encodedKey)), encodingAlgorithm, cryptoSystem, keyAlgorithm);
      }
   }

   protected PrivateKey decodeKey(InputStream _1, CryptoSystem _2, String _3) {
      throw null;
   }

   public static PrivateKey decode(InputStream encodedKey, String encodingAlgorithm) {
      return decode(encodedKey, encodingAlgorithm, null, null);
   }

   public static PrivateKey decode(byte[] encodedKey, String encodingAlgorithm) {
      if (encodedKey == null) {
         throw new Object();
      } else {
         return decode((InputStream)(new Object(encodedKey)), encodingAlgorithm, null, null);
      }
   }

   public static boolean register(PrivateKeyDecoder decoder, PrivateKeyDecoder baseDecoder) {
      if (decoder != null && baseDecoder != null) {
         String encodingSchemeAlgorithm = baseDecoder.getEncodingAlgorithm();
         PrivateKeyDecoder currentBaseDecoder = (PrivateKeyDecoder)_decoderHashtable.get(encodingSchemeAlgorithm);
         if (currentBaseDecoder == null) {
            _decoderHashtable.put(encodingSchemeAlgorithm, baseDecoder);
         } else if (!currentBaseDecoder.getClass().getName().equals(baseDecoder.getClass().getName())) {
            return false;
         }

         String encodingAlgorithm = decoder.getEncodingAlgorithm();
         String[] keyAlgorithms = decoder.getKeyAlgorithms();
         return !encodingAlgorithm.equals(encodingSchemeAlgorithm)
            ? false
            : Utility.registerAlgorithms(_decoderKeyHashtable, decoder, encodingAlgorithm, keyAlgorithms);
      } else {
         return false;
      }
   }

   protected static PrivateKeyDecoder getDecoder(String encodingAlgorithm, String keyAlgorithm) {
      return (PrivateKeyDecoder)Utility.getCoder(_decoderKeyHashtable, encodingAlgorithm, keyAlgorithm);
   }

   protected String getEncodingAlgorithm() {
      throw null;
   }

   protected String[] getKeyAlgorithms() {
      throw null;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _decoderHashtable = ar.getHashtable(4013308683474645231L);
      _decoderKeyHashtable = ar.getHashtable(7274265847536905980L);
   }
}
