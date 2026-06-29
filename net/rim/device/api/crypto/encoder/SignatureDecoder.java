package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.crypto.RIMFactoryUtilities;
import net.rim.device.api.system.ApplicationRegistry;

public class SignatureDecoder {
   private static Hashtable _decoderSignatureHashtable;
   private static Hashtable _decoderHashtable;

   protected SignatureDecoder() {
   }

   public static DecodedSignature decode(InputStream encodedSignature, String encodingAlgorithm) {
      return decode(encodedSignature, encodingAlgorithm, null);
   }

   public static DecodedSignature decode(byte[] encodedSignature, String encodingAlgorithm) {
      return decode(encodedSignature, 0, encodingAlgorithm);
   }

   public static DecodedSignature decode(byte[] encodedSignature, int offset, String encodingAlgorithm) {
      if (encodedSignature == null) {
         throw new Object();
      } else {
         return decode((InputStream)(new Object(encodedSignature, offset, encodedSignature.length - offset)), encodingAlgorithm, null);
      }
   }

   public static DecodedSignature decode(InputStream encodedSignature, String encodingAlgorithm, String signatureAlgorithm) {
      if (encodedSignature != null && encodingAlgorithm != null) {
         SignatureDecoder decoder = (SignatureDecoder)_decoderHashtable.get(encodingAlgorithm);
         if (decoder == null) {
            throw new Object(encodingAlgorithm);
         }

         String digestAlgorithm = null;
         if (signatureAlgorithm != null) {
            digestAlgorithm = RIMFactoryUtilities.getRightMostSubAlgorithm(signatureAlgorithm);
            signatureAlgorithm = RIMFactoryUtilities.getLeftMostSubAlgorithm(signatureAlgorithm);
         }

         return decoder.decodeSignature(encodedSignature, signatureAlgorithm, digestAlgorithm);
      } else {
         throw new Object();
      }
   }

   public static DecodedSignature decode(byte[] encodedSignature, int offset, String encodingAlgorithm, String signatureAlgorithm) {
      if (encodedSignature == null) {
         throw new Object();
      } else {
         return decode((InputStream)(new Object(encodedSignature, offset, encodedSignature.length - offset)), encodingAlgorithm, signatureAlgorithm);
      }
   }

   protected DecodedSignature decodeSignature(InputStream _1, String _2, String _3) {
      throw null;
   }

   public static boolean register(SignatureDecoder decoder, SignatureDecoder baseDecoder) {
      if (decoder != null && baseDecoder != null) {
         String encodingSchemeAlgorithm = baseDecoder.getEncodingAlgorithm();
         SignatureDecoder currentBaseDecoder = (SignatureDecoder)_decoderHashtable.get(encodingSchemeAlgorithm);
         if (currentBaseDecoder == null) {
            _decoderHashtable.put(encodingSchemeAlgorithm, baseDecoder);
         } else if (!currentBaseDecoder.getClass().getName().equals(baseDecoder.getClass().getName())) {
            return false;
         }

         String encodingAlgorithm = decoder.getEncodingAlgorithm();
         String[] signatureAlgorithms = decoder.getSignatureAlgorithms();
         return !encodingAlgorithm.equals(encodingSchemeAlgorithm)
            ? false
            : Utility.registerAlgorithms(_decoderSignatureHashtable, decoder, encodingAlgorithm, signatureAlgorithms);
      } else {
         return false;
      }
   }

   protected static SignatureDecoder getDecoder(String encodingAlgorithm, String signatureAlgorithm) {
      if (signatureAlgorithm == null) {
         throw new Object();
      }

      signatureAlgorithm = RIMFactoryUtilities.getLeftMostSubAlgorithm(signatureAlgorithm);
      return (SignatureDecoder)Utility.getCoder(_decoderSignatureHashtable, encodingAlgorithm, signatureAlgorithm);
   }

   protected String getEncodingAlgorithm() {
      throw null;
   }

   protected String[] getSignatureAlgorithms() {
      throw null;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _decoderHashtable = ar.getHashtable(-5135525911758306114L);
      _decoderSignatureHashtable = ar.getHashtable(-8542847872503693932L);
   }
}
