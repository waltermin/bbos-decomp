package net.rim.device.apps.internal.secureemail;

import java.util.Vector;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.properties.MessagePropertiesListener;
import net.rim.device.apps.internal.blackberryemail.properties.TransitoryMessagePropertiesModel;

public class CertificateHarvester implements MessagePropertiesListener {
   private TransitoryMessagePropertiesModel _messagePropertiesModel;
   private CertificateHarvester$CertificateHarvesterWorkerThread _workerThread;
   private Vector _queuedRecipientData = new Vector();
   private Vector _processedRecipientData = new Vector();
   private boolean _harvesterActive;
   private boolean _threadIsRunning;
   private boolean _selectedSendMethodRequiresCertificates;
   private Object _workerThreadLock = new Object();
   private boolean _isPIN;
   protected CertificateHarvesterCompletionDialog _completionDialog;
   private boolean _completionDialogOnDisplay;
   public static final int PRIORITY_KEY_STORE_LDAP = 0;
   public static final int PRIORITY_SECURE_EMAIL_SERVER = 1;
   private static final boolean DEBUG = false;

   public void setMessagePropertiesModel(TransitoryMessagePropertiesModel messagePropertiesModel) {
      this._messagePropertiesModel = messagePropertiesModel;
      this._messagePropertiesModel.addMessagePropertiesListener(this);
   }

   protected void harvestCertificates(RecipientData _1) {
      throw null;
   }

   public long[] getEncodingUIDs() {
      throw null;
   }

   public int getPriority() {
      throw null;
   }

   public String getDisplayName() {
      throw null;
   }

   public Vector getProcessedRecipients() {
      synchronized (this._workerThreadLock) {
         if (!this._selectedSendMethodRequiresCertificates) {
            while (!this._queuedRecipientData.isEmpty()) {
               RecipientData currentRecipientData = (RecipientData)this._queuedRecipientData.firstElement();
               this._queuedRecipientData.removeElementAt(0);
               this._processedRecipientData.addElement(currentRecipientData);
            }
         } else if (!this._queuedRecipientData.isEmpty()) {
            this._completionDialogOnDisplay = true;
         }
      }

      if (this._completionDialogOnDisplay) {
         this._completionDialog.display();
      }

      this.terminateWorkerThread();
      return this._processedRecipientData;
   }

   public void recipientAdded(RecipientData recipientData) {
      this.queueRecipient(recipientData);
   }

   protected void removeQueueRecipient(RecipientData recipientData) {
      synchronized (this._workerThreadLock) {
         int recipientDataVectorSize = this._queuedRecipientData.size();

         for (int i = recipientDataVectorSize - 1; i >= 0; i--) {
            RecipientData currentQueuedRecipientData = (RecipientData)this._queuedRecipientData.elementAt(i);
            if (recipientData.equals(currentQueuedRecipientData)) {
               this._queuedRecipientData.removeElement(currentQueuedRecipientData);
            }
         }

         recipientDataVectorSize = this._processedRecipientData.size();

         for (int j = recipientDataVectorSize - 1; j >= 0; j--) {
            RecipientData currentProcessedRecipientData = (RecipientData)this._processedRecipientData.elementAt(j);
            if (recipientData.equals(currentProcessedRecipientData)) {
               this._processedRecipientData.removeElement(currentProcessedRecipientData);
            }
         }

         int numRecipients = this._queuedRecipientData.size() + this._processedRecipientData.size();
         if (numRecipients > 0) {
            this._completionDialog.setNumRecipients(numRecipients);
            this._completionDialog.setRecipientIndex(this._processedRecipientData.size());
         }

         this._workerThreadLock.notify();
      }
   }

   public void recipientRemoved(RecipientData recipientData) {
      this.removeQueueRecipient(recipientData);
   }

   protected void queueRecipient(RecipientData recipientData) {
      synchronized (this._workerThreadLock) {
         if (!this._harvesterActive) {
            throw new IllegalStateException("Cannot queue a recipient to an inactive harvester");
         }

         int recipientDataVectorSize = this._queuedRecipientData.size();

         for (int i = 0; i < recipientDataVectorSize; i++) {
            RecipientData currentQueuedRecipientData = (RecipientData)this._queuedRecipientData.elementAt(i);
            if (recipientData.equals(currentQueuedRecipientData)) {
               return;
            }
         }

         recipientDataVectorSize = this._processedRecipientData.size();

         for (int j = 0; j < recipientDataVectorSize; j++) {
            RecipientData currentProcessedRecipientData = (RecipientData)this._processedRecipientData.elementAt(j);
            if (recipientData.equals(currentProcessedRecipientData)) {
               return;
            }
         }

         this._queuedRecipientData.addElement(recipientData);
         int numRecipients = this._queuedRecipientData.size() + this._processedRecipientData.size();
         this._completionDialog.setNumRecipients(numRecipients);
         this._completionDialog.setRecipientIndex(this._processedRecipientData.size());
         this._workerThreadLock.notify();
      }
   }

   protected boolean harvestCertificatesForAction(int encodingAction) {
      return (encodingAction & 2) != 0;
   }

   public void reactivate() {
      synchronized (this._workerThreadLock) {
         if (this._harvesterActive) {
            throw new IllegalStateException("Cannot reactivate an active harvester");
         }

         while (!this._processedRecipientData.isEmpty()) {
            RecipientData currentRecipientData = (RecipientData)this._processedRecipientData.firstElement();
            this._processedRecipientData.removeElementAt(0);
            currentRecipientData.resetCertificateLists();
            this._queuedRecipientData.addElement(currentRecipientData);
         }

         int numRecipients = this._queuedRecipientData.size() + this._processedRecipientData.size();
         if (numRecipients > 0) {
            this._completionDialog.setNumRecipients(numRecipients);
         }

         this._completionDialog.setRecipientIndex(0);
         this._harvesterActive = true;
         if (this._selectedSendMethodRequiresCertificates) {
            this._threadIsRunning = true;
            this._workerThread = new CertificateHarvester$CertificateHarvesterWorkerThread(this);
            this._workerThread.start();
         }
      }
   }

   public void terminate() {
      if (this._messagePropertiesModel != null) {
         this._messagePropertiesModel.removeMessagePropertiesListener(this);
      }

      this.terminateWorkerThread();
   }

   @Override
   public void sendMethodSelected(SendMethod selectedSendMethod, Object context) {
      long sendMethodEncodingUID = selectedSendMethod.getEncodingUID();
      long[] harvesterEncodingUIDs = this.getEncodingUIDs();
      synchronized (this._workerThreadLock) {
         if (Arrays.getIndex(harvesterEncodingUIDs, sendMethodEncodingUID) >= 0 && this.harvestCertificatesForAction(selectedSendMethod.getEncodingAction())) {
            this._selectedSendMethodRequiresCertificates = true;
            if (!this._threadIsRunning) {
               this._threadIsRunning = true;
               this._workerThread = new CertificateHarvester$CertificateHarvesterWorkerThread(this);
               this._workerThread.start();
            }
         } else {
            this._selectedSendMethodRequiresCertificates = false;
            if (this._threadIsRunning) {
               this._workerThreadLock.notify();
            }
         }
      }
   }

   @Override
   public void recipientRemoved(EmailHeaderModel emailHeaderModel, Object context) {
      RecipientData[] recipientData = SecureEmailUtilities.getRecipientData(emailHeaderModel, this._isPIN);
      int numRecipientData = recipientData != null ? recipientData.length : 0;

      for (int i = 0; i < numRecipientData; i++) {
         this.removeQueueRecipient(recipientData[i]);
      }
   }

   @Override
   public void recipientAdded(EmailHeaderModel emailHeaderModel, Object context) {
      this.removeQueueRecipientByModel(emailHeaderModel);
      RecipientData[] recipientData = SecureEmailUtilities.getRecipientData(emailHeaderModel, this._isPIN);
      int numRecipientData = recipientData != null ? recipientData.length : 0;

      for (int i = 0; i < numRecipientData; i++) {
         this.queueRecipient(recipientData[i]);
      }
   }

   private void terminateWorkerThread() {
      synchronized (this._workerThreadLock) {
         this._harvesterActive = false;
         if (this._threadIsRunning) {
            this._workerThread.forceTerminate();
            this._workerThreadLock.notify();
         }
      }
   }

   private void removeQueueRecipientByModel(EmailHeaderModel emailHeaderModel) {
      synchronized (this._workerThreadLock) {
         int recipientDataVectorSize = this._queuedRecipientData.size();

         for (int i = recipientDataVectorSize - 1; i >= 0; i--) {
            RecipientData currentQueuedRecipientData = (RecipientData)this._queuedRecipientData.elementAt(i);
            if (currentQueuedRecipientData.getEmailHeaderModel() == emailHeaderModel) {
               this._queuedRecipientData.removeElement(currentQueuedRecipientData);
            }
         }

         recipientDataVectorSize = this._processedRecipientData.size();

         for (int j = recipientDataVectorSize - 1; j >= 0; j--) {
            RecipientData currentProcessedRecipientData = (RecipientData)this._processedRecipientData.elementAt(j);
            if (currentProcessedRecipientData.getEmailHeaderModel() == emailHeaderModel) {
               this._processedRecipientData.removeElement(currentProcessedRecipientData);
            }
         }

         int numRecipients = this._queuedRecipientData.size() + this._processedRecipientData.size();
         if (numRecipients > 0) {
            this._completionDialog.setNumRecipients(numRecipients);
            this._completionDialog.setRecipientIndex(this._processedRecipientData.size());
         }

         this._workerThreadLock.notify();
      }
   }

   public CertificateHarvester(boolean isPIN) {
      this._isPIN = isPIN;
      this._completionDialog = new CertificateHarvesterCompletionDialog();
      this._harvesterActive = true;
   }
}
