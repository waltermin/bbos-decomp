package net.rim.device.api.crypto.keystore;

public final class RevocationReason {
   public static final int CRL_REASON_UNSPECIFIED;
   public static final int CRL_REASON_KEY_COMPROMISE;
   public static final int CRL_REASON_CA_COMPROMISE;
   public static final int CRL_REASON_AFFILIATION_CHANGED;
   public static final int CRL_REASON_SUPERSEDED;
   public static final int CRL_REASON_CESSATION_OF_OPERATION;
   public static final int CRL_REASON_CERTIFICATE_HOLD;
   public static final int CRL_REASON_REMOVE_FROM_CRL;
   public static final int CRL_REASON_KEY_RETIRED;
   public static final int CRL_REASON_USER_ID_INVALID;

   private RevocationReason() {
   }

   public static final String getRevocationReason(int revocationReason) {
      if (revocationReason < -1 || revocationReason > 9) {
         throw new Object();
      }

      if (revocationReason == -1) {
         return null;
      }

      String[] reasons = KeyStoreResources.getStringArray(6066);
      return reasons[revocationReason];
   }
}
