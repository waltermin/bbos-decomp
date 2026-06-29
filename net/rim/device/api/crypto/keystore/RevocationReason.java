package net.rim.device.api.crypto.keystore;

public final class RevocationReason {
   public static final int CRL_REASON_UNSPECIFIED = 0;
   public static final int CRL_REASON_KEY_COMPROMISE = 1;
   public static final int CRL_REASON_CA_COMPROMISE = 2;
   public static final int CRL_REASON_AFFILIATION_CHANGED = 3;
   public static final int CRL_REASON_SUPERSEDED = 4;
   public static final int CRL_REASON_CESSATION_OF_OPERATION = 5;
   public static final int CRL_REASON_CERTIFICATE_HOLD = 6;
   public static final int CRL_REASON_REMOVE_FROM_CRL = 7;
   public static final int CRL_REASON_KEY_RETIRED = 8;
   public static final int CRL_REASON_USER_ID_INVALID = 9;

   private RevocationReason() {
   }

   public static final String getRevocationReason(int revocationReason) {
      if (revocationReason < -1 || revocationReason > 9) {
         throw new IllegalArgumentException();
      }

      if (revocationReason == -1) {
         return null;
      }

      String[] reasons = KeyStoreResources.getStringArray(6066);
      return reasons[revocationReason];
   }
}
