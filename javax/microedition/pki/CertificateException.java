package javax.microedition.pki;

import java.io.IOException;

public class CertificateException extends IOException {
   private byte reason;
   private Certificate cert;
   public static final byte BAD_EXTENSIONS;
   public static final byte CERTIFICATE_CHAIN_TOO_LONG;
   public static final byte EXPIRED;
   public static final byte UNAUTHORIZED_INTERMEDIATE_CA;
   public static final byte MISSING_SIGNATURE;
   public static final byte NOT_YET_VALID;
   public static final byte SITENAME_MISMATCH;
   public static final byte UNRECOGNIZED_ISSUER;
   public static final byte UNSUPPORTED_SIGALG;
   public static final byte INAPPROPRIATE_KEY_USAGE;
   public static final byte BROKEN_CHAIN;
   public static final byte ROOT_CA_EXPIRED;
   public static final byte UNSUPPORTED_PUBLIC_KEY_TYPE;
   public static final byte VERIFICATION_FAILED;

   public CertificateException(Certificate certificate, byte status) {
      this.cert = certificate;
      this.reason = status;
   }

   public CertificateException(String message, Certificate certificate, byte status) {
      super(message);
      this.cert = certificate;
      this.reason = status;
   }

   public Certificate getCertificate() {
      return this.cert;
   }

   public byte getReason() {
      return this.reason;
   }
}
