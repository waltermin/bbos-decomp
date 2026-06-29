package net.rim.device.api.crypto;

import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.vm.TraceBack;

final class RIMSignatureSignerFactory1 extends SignatureSignerFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return PKCS1v2SignaturesFacade.available()
         ? new String[]{"RSA", "RSA_PKCS1", "RSA_PKCS1_V20", "RSA_PKCS1_V15", "DSA", "Null"}
         : new String[]{"RSA_PKCS1_V15", "DSA", "Null"};
   }

   @Override
   protected final SignatureSigner createSignatureSigner(String signatureAlgorithm, String digestAlgorithm, PrivateKey key) {
      if (digestAlgorithm == null) {
         if (signatureAlgorithm.equals("RSA_PKCS1_V15")) {
            digestAlgorithm = "MD5";
         } else {
            digestAlgorithm = "SHA1";
         }
      }

      Digest digest = DigestFactory.getInstance(digestAlgorithm);
      if (signatureAlgorithm.equals("DSA")) {
         verifyCaller();
         return new DSASignatureSigner((DSAPrivateKey)key, digest);
      } else if (signatureAlgorithm.startsWith("RSA")) {
         verifyCaller();
         return new PKCS1SignatureSigner((RSAPrivateKey)key, digest, !signatureAlgorithm.equals("RSA_PKCS1_V15"));
      } else if (signatureAlgorithm.equals("Null")) {
         return new NullSignatureSigner();
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static final void verifyCaller() {
      if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(2), 4408146)) {
         throw new ControlledAccessException();
      }
   }
}
