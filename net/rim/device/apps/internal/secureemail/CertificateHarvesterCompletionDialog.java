package net.rim.device.apps.internal.secureemail;

import javax.microedition.io.Connection;
import net.rim.device.api.crypto.certificate.LDAPCertificateFetch;
import net.rim.device.api.crypto.certificate.status.CertificateStatusQuery;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerOperationListener;
import net.rim.device.internal.ui.component.HorizontalSpacerField;
import net.rim.device.internal.ui.component.VerticalSpacerField;

public class CertificateHarvesterCompletionDialog extends PopupScreen implements SecureEmailServerOperationListener, FieldChangeListener {
   private RichTextField _messageField;
   private GaugeField _recipientGauge;
   private LabelField _recipientName;
   private LabelField _recipientAction;
   private HorizontalFieldManager _buttonFieldManager;
   private ButtonField _abortButtonField;
   private VerticalSpacerField _abortButtonSpacer;
   private LDAPCertificateFetch _ldapCertificateFetch;
   private CertificateStatusQuery _certificateStatusQuery;
   private Connection _serverConnection;
   private boolean _serverConnectionAborted;
   private int _numGaugeStepsPerRecipient;
   private int _currentMaxGaugeValue;
   private Object _applicationEventLock;
   private Object _onDisplayLock = new Object();
   protected static final int NUM_GAUGE_STEPS_PER_RECIPIENT = 4;

   public void setNumRecipients(int numRecipients) {
      synchronized (this._applicationEventLock) {
         this._recipientGauge.reset(null, 0, numRecipients * 4, 0);
      }

      this._numGaugeStepsPerRecipient = 4;
   }

   public void display() {
      Ui.getUiEngine().pushModalScreen(this);
   }

   public void remove() {
      this.waitForOnDisplay();
      Application.getApplication().invokeLater(new CertificateHarvesterCompletionDialog$ScreenPopper(this));
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

   public void setRecipientName(String recipientName) {
      synchronized (this._applicationEventLock) {
         this._recipientName.setText(recipientName);
         this._recipientAction.setText("");
      }
   }

   public void setRecipientIndex(int recipientIndex) {
      synchronized (this._applicationEventLock) {
         int currentMinGaugeValue = recipientIndex * 4;
         this._recipientGauge.setValue(currentMinGaugeValue);
         this._currentMaxGaugeValue = currentMinGaugeValue + this._numGaugeStepsPerRecipient;
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

   public void waitForOnDisplay() {
      synchronized (this._onDisplayLock) {
         if (!this.isUiEngineAttached()) {
            try {
               this._onDisplayLock.wait();
            } finally {
               return;
            }
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
      this.setRecipientAction(progressString);
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._abortButtonField) {
         this.abortCurrentOperationIfPossible();
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
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.abortCurrentOperationIfPossible();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   private void abortCurrentOperationIfPossible() {
      synchronized (this._applicationEventLock) {
         synchronized (this) {
            if (this._ldapCertificateFetch != null) {
               new CertificateHarvesterCompletionDialog$AbortLDAPFetchThread(this._ldapCertificateFetch).start();
               this.removeAbortButton();
            } else if (this._certificateStatusQuery != null) {
               new CertificateHarvesterCompletionDialog$TerminateCertificateStatusQueryThread(this._certificateStatusQuery).start();
               this.removeAbortButton();
            } else if (this._serverConnection != null) {
               this._serverConnectionAborted = true;
               new CertificateHarvesterCompletionDialog$CloseConnectionThread(this._serverConnection).start();
               this.removeAbortButton();
            }
         }
      }
   }

   private void addAbortButton() {
      this._buttonFieldManager.add(this._abortButtonField);
   }

   private void removeAbortButton() {
      if (this._abortButtonField.getManager() == this._buttonFieldManager) {
         this._buttonFieldManager.delete(this._abortButtonField);
      }
   }

   @Override
   public void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         if (this._abortButtonSpacer == null) {
            boolean abortButtonAdded = this._abortButtonField.getManager() == this._buttonFieldManager;
            if (!abortButtonAdded) {
               this._buttonFieldManager.add(this._abortButtonField);
            }

            this._abortButtonSpacer = new VerticalSpacerField(this._buttonFieldManager.getHeight());
            this._buttonFieldManager.add(this._abortButtonSpacer);
            if (!abortButtonAdded) {
               this._buttonFieldManager.delete(this._abortButtonField);
            }
         }

         synchronized (this._onDisplayLock) {
            this._onDisplayLock.notifyAll();
         }
      }
   }

   public CertificateHarvesterCompletionDialog() {
      super(new DialogFieldManager());
      this._applicationEventLock = Application.getEventLock();
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setIcon(new BitmapField(Bitmap.getPredefinedBitmap(3)));
      this._messageField = new RichTextField(SecureEmailResources.getString(15), 36028797018963968L);
      dfm.setMessage(this._messageField);
      dfm.addCustomField(new SeparatorField());
      dfm.addCustomField(new VerticalSpacerField(6));
      this._currentMaxGaugeValue = 0;
      this._recipientGauge = new GaugeField();
      dfm.addCustomField(this._recipientGauge);
      this._recipientName = new LabelField("", 64);
      dfm.addCustomField(this._recipientName);
      this._recipientAction = new LabelField("", 64);
      HorizontalFieldManager hfm = new HorizontalFieldManager(1152921504606846976L);
      hfm.add(new HorizontalSpacerField(12));
      hfm.add(this._recipientAction);
      dfm.addCustomField(hfm);
      this._buttonFieldManager = new HorizontalFieldManager(12884901888L);
      dfm.addCustomField(this._buttonFieldManager);
      this._abortButtonField = new ButtonField(SecureEmailResources.getString(165), 12884901888L);
      this._abortButtonField.setChangeListener(this);
   }
}
