package net.rim.device.apps.internal.secureemail.server;

import javax.microedition.io.Connection;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.internal.secureemail.SecureEmailResources;
import net.rim.device.internal.ui.component.PleaseWaitDialog;
import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;
import net.rim.device.internal.ui.component.VerticalSpacerField;

public class AbortablePleaseWaitDialog extends PleaseWaitDialog implements SecureEmailServerOperationListener, FieldChangeListener {
   private HorizontalFieldManager _buttonFieldManager;
   private ButtonField _abortButtonField;
   private VerticalSpacerField _abortButtonSpacer;
   private Connection _serverConnection;
   private boolean _serverConnectionAborted;
   private Object _applicationEventLock = Application.getEventLock();

   public AbortablePleaseWaitDialog(PleaseWaitWorkerThread workerThread) {
      this(null, workerThread);
   }

   public AbortablePleaseWaitDialog(String message, PleaseWaitWorkerThread workerThread) {
      super(message, workerThread);
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      this._buttonFieldManager = new HorizontalFieldManager(12884901888L);
      dfm.addCustomField(this._buttonFieldManager);
      this._abortButtonField = new ButtonField(SecureEmailResources.getString(165), 12884901888L);
      this._abortButtonField.setChangeListener(this);
   }

   @Override
   public void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         if (this._abortButtonSpacer != null) {
            return;
         }

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
   }

   @Override
   public void updateServerOperationProgress(String string) {
      synchronized (this._applicationEventLock) {
         this.setMessage(string);
      }
   }

   @Override
   public void setServerConnection(Connection serverConnection) {
      synchronized (this._applicationEventLock) {
         synchronized (this) {
            this._serverConnection = serverConnection;
            this._serverConnectionAborted = false;
            this._buttonFieldManager.add(this._abortButtonField);
            this._abortButtonField.setFocus();
            this.updateDisplay();
         }
      }
   }

   @Override
   public void clearServerConnection() {
      synchronized (this._applicationEventLock) {
         synchronized (this) {
            this._serverConnection = null;
            if (this._abortButtonField.getManager() == this._buttonFieldManager) {
               this._buttonFieldManager.delete(this._abortButtonField);
            }
         }
      }
   }

   @Override
   public boolean wasServerConnectionAborted() {
      return this._serverConnectionAborted;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.abortIfPossible();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._abortButtonField) {
         this.abortIfPossible();
      }
   }

   private void abortIfPossible() {
      synchronized (this._applicationEventLock) {
         synchronized (this) {
            if (this._serverConnection != null) {
               this._serverConnectionAborted = true;
               new AbortablePleaseWaitDialog$CloseConnectionThread(this._serverConnection).start();
               this.clearServerConnection();
               this.updateServerOperationProgress(SecureEmailResources.getString(194));
            }
         }
      }
   }
}
