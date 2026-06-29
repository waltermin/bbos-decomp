package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.system.EventLogger;

public class SecureEmailSignatureField$VerifyThread extends Thread {
   private boolean _checkSignature;
   private boolean _checkTrust;
   private final SecureEmailSignatureField this$0;

   SecureEmailSignatureField$VerifyThread(SecureEmailSignatureField _1, boolean checkSignature, boolean checkTrust) {
      this.this$0 = _1;
      this._checkSignature = checkSignature;
      this._checkTrust = checkTrust;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      Thread.currentThread().setPriority(1);
      synchronized (this.this$0._verifyThreadLock) {
         synchronized (this.this$0._threadWaitingLock) {
            this.this$0._threadWaiting = false;
         }

         if (this._checkSignature) {
            label120:
            try {
               this.this$0.verifySignature();
               if (this.this$0._signatureStatus.getStatus() == 1) {
                  String hashDigestName = this.this$0.getSignatureDigestName();
                  if (DigestFactory.isDigestWeakByPolicy(hashDigestName)) {
                     this.this$0._signatureStatus.setThrowable(new SecureEmailWeakHashDigestException());
                  }
               }
            } catch (Throwable var15) {
               EventLogger.logEvent(this.this$0._secureEmailFactory.getEventLoggerGUID(), e.toString().getBytes());
               this.this$0._signatureStatus.setThrowable(e);
               break label120;
            }
         } else if (this.this$0._signerCertificate == null) {
            this.this$0.getSignerCertificateWithoutVerifying();
         }

         this.this$0.getSignerCertificateChainAndProperties();
         if (this.this$0._signerCertificate != null) {
            String signerName = SecureEmailUtilities.getLabelOrFriendlyName(
               this.this$0._signerCertificate, this.this$0._secureEmailFactory.getPreferredKeyStore()
            );
            this.this$0._signatureStatus.setSignerName(signerName);
         }

         this.this$0._signatureStatus.updateStatus();
         SecureEmailCache.getInstance()
            .putSignatureStatus(
               this.this$0._messageModel,
               this.this$0._signatureStatus.getStatus(),
               this.this$0._signatureStatus.getSignerName(),
               this.this$0._signatureStatus.getDetails()
            );
         if (this.this$0._signerCertificateChain == null) {
            if (this.this$0._moreAvailable && this.this$0._besSignerCertificateHash == null) {
               this.this$0._trustStatus.setStatus(9);
            } else {
               this.this$0._trustStatus.setStatus(6);
            }
         } else {
            if (this._checkTrust) {
               label108:
               try {
                  this.this$0.verifyTrust();
               } catch (Throwable var14) {
                  EventLogger.logEvent(this.this$0._secureEmailFactory.getEventLoggerGUID(), e.toString().getBytes());
                  this.this$0._trustStatus.setThrowable(e);
                  break label108;
               }
            }

            this.this$0.checkSendingAddress();
         }

         this.this$0._trustStatus.updateStatus();
         SecureEmailCache.getInstance().putTrustStatus(this.this$0._messageModel, this.this$0._trustStatus.getStatus(), this.this$0._trustStatus.getDetails());
      }
   }
}
