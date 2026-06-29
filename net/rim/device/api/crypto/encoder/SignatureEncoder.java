package net.rim.device.api.crypto.encoder;

import java.util.Hashtable;
import net.rim.device.api.crypto.RIMFactoryUtilities;
import net.rim.device.api.crypto.SignatureSigner;
import net.rim.device.api.system.ApplicationRegistry;

public class SignatureEncoder {
   private static Hashtable _encoderHashtable = ApplicationRegistry.getApplicationRegistry().getHashtable(9059615835806864038L);

   protected SignatureEncoder() {
   }

   public static EncodedSignature encode(SignatureSigner signer, String encodingAlgorithm) {
      if (signer == null) {
         throw new Object();
      }

      SignatureEncoder encoder = getEncoder(encodingAlgorithm, signer.getAlgorithm());
      return encoder.encodeSignature(signer);
   }

   protected EncodedSignature encodeSignature(SignatureSigner _1) {
      throw null;
   }

   public static boolean register(SignatureEncoder encoder) {
      return encoder == null ? false : Utility.registerAlgorithms(_encoderHashtable, encoder, encoder.getEncodingAlgorithm(), encoder.getSignatureAlgorithms());
   }

   protected static SignatureEncoder getEncoder(String encodingAlgorithm, String signatureAlgorithm) {
      if (encodingAlgorithm != null && signatureAlgorithm != null) {
         signatureAlgorithm = RIMFactoryUtilities.getLeftMostSubAlgorithm(signatureAlgorithm);
         return (SignatureEncoder)Utility.getCoder(_encoderHashtable, encodingAlgorithm, signatureAlgorithm);
      } else {
         throw new Object();
      }
   }

   protected String getEncodingAlgorithm() {
      throw null;
   }

   protected String[] getSignatureAlgorithms() {
      throw null;
   }
}
