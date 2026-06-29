package net.rim.device.apps.internal.secureemail;

final class CertificateHarvester$CertificateHarvesterWorkerThread extends Thread {
   private boolean _terminatingThread;
   private final CertificateHarvester this$0;

   public CertificateHarvester$CertificateHarvesterWorkerThread(CertificateHarvester _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      while (true) {
         RecipientData currentData;
         int currentRecipientIndex;
         synchronized (this.this$0._workerThreadLock) {
            if (!this.this$0._harvesterActive || !this.this$0._selectedSendMethodRequiresCertificates || this._terminatingThread) {
               this.this$0._threadIsRunning = false;
               if (this.this$0._completionDialogOnDisplay) {
                  this.this$0._completionDialog.remove();
                  this.this$0._completionDialogOnDisplay = false;
               }

               return;
            }

            if (this.this$0._queuedRecipientData.isEmpty()) {
               if (this.this$0._completionDialogOnDisplay) {
                  this.this$0._completionDialogOnDisplay = false;
                  this.this$0._completionDialog.remove();
                  return;
               }

               try {
                  this.this$0._workerThreadLock.wait();
                  continue;
               } finally {
                  continue;
               }
            }

            currentData = (RecipientData)this.this$0._queuedRecipientData.firstElement();
            currentRecipientIndex = this.this$0._processedRecipientData.size();
         }

         this.this$0._completionDialog.setRecipientName(currentData.getName());
         this.this$0._completionDialog.setRecipientIndex(currentRecipientIndex);
         this.this$0.harvestCertificates(currentData);
         synchronized (this.this$0._workerThreadLock) {
            if (this.this$0._queuedRecipientData.removeElement(currentData)) {
               this.this$0._processedRecipientData.addElement(currentData);
            }
         }
      }
   }

   public final void forceTerminate() {
      this._terminatingThread = true;
   }
}
