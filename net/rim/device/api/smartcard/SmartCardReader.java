package net.rim.device.api.smartcard;

import java.util.Vector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.BackgroundDialog;

public class SmartCardReader {
   private SmartCardReaderSession _currentSession;
   private Vector _listeners = (Vector)(new Object(1));
   private int _readerStatus = -1;
   private CallBackThread _readerCallBackThread = new CallBackThread();
   private CallBackThread _readerSessionCallBackThread;
   private String _cachedLabel;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(7215549882295292649L, "net.rim.device.internal.resource.SmartCard");

   protected SmartCardReader() {
      this._readerCallBackThread.start();
      this._readerSessionCallBackThread = new CallBackThread();
      this._readerSessionCallBackThread.start();
   }

   public final synchronized SmartCardReaderSession openSessionForced() {
      if (this._currentSession != null) {
         synchronized (this._currentSession) {
            if (this._currentSession.isOpen()) {
               ControlledAccess.assertRCISignatures(true);
               this._currentSession.close();
            }
         }
      }

      return this.openSession();
   }

   public final synchronized SmartCardReaderSession openSession() {
      if (this._currentSession != null) {
         synchronized (this._currentSession) {
            while (this._currentSession.isOpen()) {
               try {
                  this._currentSession.wait();
               } finally {
                  continue;
               }
            }
         }
      }

      while (true) {
         try {
            Function function = new SmartCardReader$1(this);
            this._readerCallBackThread.go(function);
            this._currentSession = function.getSmartCardReaderSessionResult();
            if (this._currentSession == null) {
               throw new SmartCardException("Unable to open session");
            }

            this._currentSession.openReaderSession(this._readerSessionCallBackThread);
            return this._currentSession;
         } catch (SmartCardNoReaderPresentException var7) {
            String message = MessageFormat.format(_rb.getString(4), new Object[]{this.getLabel()});
            if (BackgroundDialog.getChoice(message, CommonResource.getStringArray(10041), 0, -2147483644) != 0) {
               throw new SmartCardCancelException();
            }
         }
      }
   }

   protected SmartCardReaderSession openSessionImpl() {
      throw null;
   }

   public final boolean isReaderPresent() {
      Function function = new SmartCardReader$2(this);
      this._readerCallBackThread.go(function);
      return function.getBooleanResult();
   }

   protected boolean isReaderPresentImpl() {
      throw null;
   }

   public final boolean isSmartCardPresent() {
      Function function = new SmartCardReader$3(this);
      this._readerCallBackThread.go(function);
      return function.getBooleanResult();
   }

   protected boolean isSmartCardPresentImpl() {
      throw null;
   }

   public final boolean isInsertionRemovalDetectable() {
      Function function = new SmartCardReader$4(this);
      this._readerCallBackThread.go(function);
      return function.getBooleanResultNoSCException();
   }

   protected boolean isInsertionRemovalDetectableImpl() {
      throw null;
   }

   public final String getLabel() {
      Function function = new SmartCardReader$5(this);
      this._readerCallBackThread.go(function);
      return function.getStringResultNoSCException();
   }

   protected String getLabelImpl() {
      throw null;
   }

   public final String getType() {
      Function function = new SmartCardReader$6(this);
      this._readerCallBackThread.go(function);
      return function.getStringResultNoSCException();
   }

   protected String getTypeImpl() {
      throw null;
   }

   public final boolean addListener(ReaderStatusListener listener) {
      if (!this.isInsertionRemovalDetectable()) {
         return false;
      }

      int initialState = -1;

      try {
         initialState = this.isSmartCardPresent() ? 2 : 0;
      } catch (SmartCardException var6) {
      }

      synchronized (this._listeners) {
         if (this._listeners.indexOf(listener) != -1) {
            return true;
         }

         this._listeners.addElement(listener);
         listener.readerStatus(this, initialState);
         return true;
      }
   }

   public final synchronized boolean removeListener(ReaderStatusListener listener) {
      synchronized (this._listeners) {
         return this._listeners.removeElement(listener);
      }
   }

   protected final void setReaderStatus(int readerStatus) {
      System.out.println(((StringBuffer)(new Object("SRS:"))).append(readerStatus).toString());
      if (this._readerStatus != readerStatus) {
         this._readerStatus = readerStatus;
         synchronized (this._listeners) {
            int size = this._listeners.size();

            for (int i = size - 1; i >= 0; i--) {
               try {
                  ((ReaderStatusListener)this._listeners.elementAt(i)).readerStatus(this, readerStatus);
               } finally {
                  continue;
               }
            }
         }
      }
   }

   public final int getReaderStatus() {
      return this._readerStatus;
   }

   public final void displaySettings(Object context) {
      try {
         this.displaySettingsImpl(context);
      } finally {
         return;
      }
   }

   protected void displaySettingsImpl(Object context) {
   }

   public final boolean isDisplaySettingsAvailable(Object context) {
      try {
         return this.isDisplaySettingsAvailableImpl(context);
      } finally {
         ;
      }
   }

   protected boolean isDisplaySettingsAvailableImpl(Object context) {
      return false;
   }

   @Override
   public String toString() {
      if (this._cachedLabel != null) {
         return this._cachedLabel;
      }

      this._cachedLabel = this.getLabel();
      if (this._cachedLabel == null) {
         this._cachedLabel = super.toString();
      }

      return this._cachedLabel;
   }
}
