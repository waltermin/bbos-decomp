package net.rim.device.apps.internal.secureemail.sendmethods;

import javax.microedition.io.Connection;
import net.rim.device.api.crypto.certificate.LDAPCertificateFetch;
import net.rim.device.api.crypto.certificate.status.CertificateStatusQuery;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.internal.secureemail.SecureEmailResources;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerOperationListener;
import net.rim.device.internal.ui.component.PleaseWaitDialog;
import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

public class SecureEmailSendDialog extends PleaseWaitDialog implements SecureEmailServerOperationListener, FieldChangeListener {
   private GaugeField _recipientGauge;
   private LabelField _recipientName;
   private LabelField _recipientAction;
   private HorizontalFieldManager _buttonFieldManager;
   private ButtonField _abortButtonField;
   private LDAPCertificateFetch _ldapCertificateFetch;
   private CertificateStatusQuery _certificateStatusQuery;
   private Connection _serverConnection;
   private boolean _serverConnectionAborted;
   private int _numGaugeStepsPerRecipient;
   private int _currentMaxGaugeValue;
   private Object _applicationEventLock = Application.getEventLock();

   public SecureEmailSendDialog(PleaseWaitWorkerThread workerThread) {
      super(workerThread);
   }

   @Override
   public void setMessage(String message) {
      synchronized (this._applicationEventLock) {
         DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
         dfm.getCustomManager().deleteAll();
         this._buttonFieldManager = null;
         this._recipientName = null;
         this._recipientAction = null;
         super.setMessage(message);
      }
   }

   public void setNumRecipients(int numRecipients, int numGaugeStepsPerRecipient) {
      synchronized (this._applicationEventLock) {
         this._recipientGauge = (GaugeField)(new Object(null, 0, numRecipients * numGaugeStepsPerRecipient, 0, 4));
      }

      this._numGaugeStepsPerRecipient = numGaugeStepsPerRecipient;
      this._currentMaxGaugeValue = 0;
   }

   public void stepGauge() {
      int newGaugeValue = this._recipientGauge.getValue() + 1;
      if (newGaugeValue <= this._currentMaxGaugeValue) {
         synchronized (this._applicationEventLock) {
            this._recipientGauge.setValue(newGaugeValue);
         }
      }
   }

   public void finishGauge() {
      synchronized (this._applicationEventLock) {
         this._recipientGauge.setValue(this._recipientGauge.getValueMax());
      }
   }

   public void setRecipient(String recipientName) {
      synchronized (this._applicationEventLock) {
         this.prepareRecipientFields();
         this.prepareButtonManager();
         this._recipientName.setText(recipientName);
         this._recipientAction.setText("");
         this._recipientGauge.setValue(this._currentMaxGaugeValue);
         this._currentMaxGaugeValue = this._currentMaxGaugeValue + this._numGaugeStepsPerRecipient;
      }
   }

   public void setRecipientAction(String recipientAction) {
      synchronized (this._applicationEventLock) {
         this._recipientAction.setText(recipientAction);
      }
   }

   public void setLDAPCertificateFetch(LDAPCertificateFetch ldapCertificateFetch) {
      synchronized (this._applicationEventLock) {
         synchronized (this) {
            this._ldapCertificateFetch = ldapCertificateFetch;
            this.addAbortButton();
         }
      }
   }

   public void clearLDAPCertificateFetch() {
      synchronized (this._applicationEventLock) {
         synchronized (this) {
            this._ldapCertificateFetch = null;
            this.removeAbortButton();
         }
      }
   }

   public void setCertificateStatusQuery(CertificateStatusQuery certificateStatusQuery) {
      synchronized (this._applicationEventLock) {
         synchronized (this) {
            this._certificateStatusQuery = certificateStatusQuery;
            this.addAbortButton();
         }
      }
   }

   public void clearCertificateStatusQuery() {
      synchronized (this._applicationEventLock) {
         synchronized (this) {
            this._certificateStatusQuery = null;
            this.removeAbortButton();
         }
      }
   }

   @Override
   public void setServerConnection(Connection serverConnection) {
      synchronized (this._applicationEventLock) {
         synchronized (this) {
            this._serverConnection = serverConnection;
            this._serverConnectionAborted = false;
            this.addAbortButton();
         }
      }
   }

   @Override
   public void clearServerConnection() {
      synchronized (this._applicationEventLock) {
         synchronized (this) {
            this._serverConnection = null;
            this.removeAbortButton();
         }
      }
   }

   @Override
   public boolean wasServerConnectionAborted() {
      return this._serverConnectionAborted;
   }

   @Override
   public void updateServerOperationProgress(String progressString) {
      if (this._recipientName != null) {
         this.setRecipientAction(progressString);
      } else {
         this.setMessage(progressString);
      }
   }

   @Override
   public synchronized void fieldChanged(Field field, int context) {
      if (field == this._abortButtonField) {
         this.abortCurrentOperationIfPossible();
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.abortCurrentOperationIfPossible();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   private void abortCurrentOperationIfPossible() {
      if (this._ldapCertificateFetch != null) {
         this._ldapCertificateFetch.abortFetch();
      } else if (this._certificateStatusQuery != null) {
         this._certificateStatusQuery.terminateQuery();
      } else if (this._serverConnection != null) {
         try {
            this._serverConnectionAborted = true;
            this._serverConnection.close();
         } finally {
            return;
         }
      }
   }

   private void addAbortButton() {
      this.prepareButtonManager();
      this._buttonFieldManager.add(this._abortButtonField);
      this._abortButtonField.setFocus();
   }

   private void removeAbortButton() {
      this._buttonFieldManager.delete(this._abortButtonField);
   }

   private void prepareButtonManager() {
      if (this._buttonFieldManager == null) {
         DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
         this._abortButtonField = (ButtonField)(new Object(SecureEmailResources.getString(165), 12884901888L));
         this._abortButtonField.setChangeListener(this);
         this._buttonFieldManager = (HorizontalFieldManager)(new Object(12884901888L));
         dfm.addCustomField(this._buttonFieldManager);
         this._buttonFieldManager.add(this._abortButtonField);
         this._buttonFieldManager.add((Field)(new Object(this._buttonFieldManager.getHeight())));
         this._buttonFieldManager.delete(this._abortButtonField);
      }
   }

   private void prepareRecipientFields() {
      if (this._recipientName == null) {
         DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
         this._recipientName = (LabelField)(new Object("", 64));
         this._recipientAction = (LabelField)(new Object("", 64));
         dfm.addCustomField((Field)(new Object()));
         dfm.addCustomField((Field)(new Object(6)));
         dfm.addCustomField(this._recipientGauge);
         dfm.addCustomField(this._recipientName);
         HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(1152921504606846976L));
         hfm.add((Field)(new Object(12)));
         hfm.add(this._recipientAction);
         dfm.addCustomField(hfm);
      }
   }
}
