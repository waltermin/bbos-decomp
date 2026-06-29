package net.rim.device.api.crypto;

import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.vm.TraceBack;

final class RIMSignatureSignerFactory3 extends SignatureSignerFactory {
   @Override
   protected final String[] getFactoryAlgorithms() {
      return new String[]{"EC", "ECDSA", "RSA_PSS", "ECNR", "RSA_X931"};
   }

   @Override
   protected final SignatureSigner createSignatureSigner(String signatureAlgorithm, String digestAlgorithm, PrivateKey key) {
      if (digestAlgorithm == null) {
         digestAlgorithm = "SHA1";
      }

      Digest digest = DigestFactory.getInstance(digestAlgorithm);
      if (signatureAlgorithm.equals("ECNR")) {
         verifyCaller();
         return new ECNRSignatureSigner((ECPrivateKey)key, digest);
      } else if (signatureAlgorithm.equals("EC") || signatureAlgorithm.equals("ECDSA")) {
         verifyCaller();
         return new ECDSASignatureSigner((ECPrivateKey)key, digest);
      } else if (signatureAlgorithm.equals("RSA_PSS")) {
         verifyCaller();
         return new PSSSignatureSigner((RSAPrivateKey)key, digest);
      } else if (signatureAlgorithm.equals("RSA_X931")) {
         verifyCaller();
         return new X931SignatureSigner((RSAPrivateKey)key, digest);
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
