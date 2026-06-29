package net.rim.device.api.smartcard;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.LEDEngine;
import net.rim.vm.TraceBack;

public class SmartCardReaderSession {
   private SmartCardReader _reader;
   private boolean _readerSessionOpen;
   private boolean _smartCardSessionOpen;
   private CallBackThread _callBackThread;
   private LEDEngine _ledEngine;
   private static SmartCardCache _cache = SmartCardCache.getInstance();
   private static final boolean DEBUG;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(7215549882295292649L, "net.rim.device.internal.resource.SmartCard");

   protected SmartCardReaderSession(SmartCardReader reader) {
      this._reader = reader;
      if (!InternalServices.isFermion()) {
         this._ledEngine = LEDEngine.getInstance();
      }
   }

   final synchronized void openReaderSession(CallBackThread callBackThread) {
      this._readerSessionOpen = true;
      this._callBackThread = callBackThread;
      if (this._ledEngine != null && SmartCardOptions.getInstance().getEnableLEDFlashingOnOpenSession()) {
         this._ledEngine.setFlag(32);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized void close() {
      if (this._readerSessionOpen) {
         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;
            Function function = new SmartCardReaderSession$1(this);
            this._callBackThread.go(function);
            function.checkExceptionNoSmartCard();
            var9 = false;
         } finally {
            if (var9) {
               if (this._ledEngine != null) {
                  this._ledEngine.clearFlag(32);
               }

               this._readerSessionOpen = false;
               synchronized (this) {
                  this.notifyAll();
               }
            }
         }

         if (this._ledEngine != null) {
            this._ledEngine.clearFlag(32);
         }

         this._readerSessionOpen = false;
         synchronized (this) {
            this.notifyAll();
         }
      }
   }

   protected void closeImpl() {
      throw null;
   }

   final synchronized void openSmartCardSession() {
      if (!this._readerSessionOpen) {
         throw new SmartCardSessionClosedException();
      }

      if (this._smartCardSessionOpen) {
         throw new SmartCardSessionAlreadyOpenException();
      }

      this._smartCardSessionOpen = true;
   }

   final synchronized void closeSmartCardSession() {
      this._smartCardSessionOpen = false;
   }

   public final boolean isOpen() {
      return this._readerSessionOpen;
   }

   public final synchronized void sendAPDU(CommandAPDU commandAPDU, ResponseAPDU responseAPDU) {
      if (!this._readerSessionOpen) {
         throw new SmartCardSessionClosedException();
      }

      if (commandAPDU != null && responseAPDU != null) {
         Function function = new SmartCardReaderSession$2(this, commandAPDU, responseAPDU);
         this._callBackThread.go(function);
         function.checkException();
      } else {
         throw new Object();
      }
   }

   protected void sendAPDUImpl(CommandAPDU _1, ResponseAPDU _2) {
      throw null;
   }

   public final synchronized void sendAPDUs(CommandAPDUGroup commandAPDUs, ResponseAPDUGroup responseAPDUs) {
      if (!this._readerSessionOpen) {
         throw new SmartCardSessionClosedException();
      }

      if (commandAPDUs.getNumAPDUs() >= 1 && commandAPDUs.getNumAPDUs() == responseAPDUs.getNumAPDUs()) {
         Function function = new SmartCardReaderSession$3(this, commandAPDUs, responseAPDUs);
         this._callBackThread.go(function);
         function.checkException();
      } else {
         throw new Object();
      }
   }

   protected void sendAPDUsImpl(CommandAPDUGroup commandAPDUs, ResponseAPDUGroup responseAPDUs) {
      int numAPDUs = commandAPDUs.getNumAPDUs();

      for (int i = 0; i < numAPDUs; i++) {
         this.sendAPDUImpl(commandAPDUs.getAPDU(i), responseAPDUs.getAPDU(i));
      }
   }

   public final synchronized void sendAPDUs(CommandAPDUGroup commandAPDUs, ResponseAPDU responseAPDU) {
      int numAPDUs = commandAPDUs.getNumAPDUs();
      ResponseAPDU[] responseAPDUs = new ResponseAPDU[numAPDUs];
      ResponseAPDU dummyResponseAPDU = new ResponseAPDU();

      for (int i = numAPDUs - 2; i >= 0; i--) {
         responseAPDUs[i] = dummyResponseAPDU;
      }

      responseAPDUs[numAPDUs - 1] = responseAPDU;
      this.sendAPDUs(commandAPDUs, new ResponseAPDUGroup(responseAPDUs, numAPDUs));
   }

   public final void negotiateProtocol(SmartCardCapabilities cardCapabilities) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (!this._readerSessionOpen) {
         throw new SmartCardSessionClosedException();
      }

      Function function = new SmartCardReaderSession$4(this, cardCapabilities);
      this._callBackThread.go(function);
      function.checkException();
   }

   protected void negotiateProtocolImpl(SmartCardCapabilities _1) {
      throw null;
   }

   public final void setProtocol(int protocol) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (!this._readerSessionOpen) {
         throw new SmartCardSessionClosedException();
      }

      Function function = new SmartCardReaderSession$5(this, protocol);
      this._callBackThread.go(function);
      function.checkException();
   }

   protected void setProtocolImpl(int protocol) {
      throw new SmartCardUnsupportedOperationException();
   }

   public final SmartCard getSmartCard() {
      if (!this._readerSessionOpen) {
         throw new SmartCardSessionClosedException();
      } else {
         return SmartCardFactory.getSmartCard(this.getAnswerToReset());
      }
   }

   public final SmartCardReader getSmartCardReader() {
      return this._reader;
   }

   private void waitForSmartCardInsertion() {
      SmartCardPromptDialog.promptUserToInsertSmartCard(_rb.getString(5), new Object[]{this._reader.getLabel()}, this._reader);
   }

   public final AnswerToReset getAnswerToReset() {
      return this.getAnswerToReset(true);
   }

   public final AnswerToReset getAnswerToReset(boolean allowUI) {
      if (!this._readerSessionOpen) {
         throw new SmartCardSessionClosedException();
      }

      AnswerToReset atr = _cache.getATR(this._reader);
      if (atr != null) {
         return atr;
      }

      if (allowUI) {
         this.waitForSmartCardInsertion();
      }

      Function function = new SmartCardReaderSession$6(this);
      this._callBackThread.go(function);
      atr = function.getAnswerToResetResult();
      _cache.setATR(this._reader, atr);
      return atr;
   }

   protected AnswerToReset getAnswerToResetImpl() {
      throw null;
   }
}
