package net.rim.device.apps.internal.secureemail;

public class SecureEmailCache$CertificateServerFetchInformation {
   private SecureEmailLookupFailureCache _emailFetchFailureTimes = new SecureEmailLookupFailureCache(3600000);
   private SecureEmailLookupFailureCache _certificateIDFailureTimes = new SecureEmailLookupFailureCache(3600000);
   private long _globalFetchFailureTime = -1;

   private SecureEmailCache$CertificateServerFetchInformation() {
   }

   public long getGlobalFetchFailureTime() {
      return this._globalFetchFailureTime;
   }

   public void setGlobalFetchFailureTime() {
      this._globalFetchFailureTime = System.currentTimeMillis();
   }

   public void putEmailFetchFailureTime(String emailAddress) {
      this._emailFetchFailureTimes.recordFetchFailure(emailAddress);
   }

   public boolean checkEmailFetchFailure(String emailAddress) {
      return this._emailFetchFailureTimes.checkFetchFailure(emailAddress);
   }

   public void putCertIDFetchFailureTime(Object certificateId) {
      this._certificateIDFailureTimes.recordFetchFailure(certificateId);
   }

   public boolean checkCertIDFetchFailure(Object certificateId) {
      return this._certificateIDFailureTimes.checkFetchFailure(certificateId);
   }

   public boolean isCertServerEmailIsEmpty() {
      return this._emailFetchFailureTimes.isEmpty();
   }

   public boolean isCertServerCertIDIsEmpty() {
      return this._certificateIDFailureTimes.isEmpty();
   }

   public void clean() {
      this._emailFetchFailureTimes.clear();
      this._certificateIDFailureTimes.clear();
      this._globalFetchFailureTime = -1;
   }

   SecureEmailCache$CertificateServerFetchInformation(SecureEmailCache$1 x0) {
      this();
   }
}
