package net.rim.device.api.smartcard;

class Function implements Runnable {
   protected boolean _booleanResult;
   protected Object _objectResult;
   protected Object _param1;
   protected Object _param2;
   protected int _paramInt;
   private Exception _exception;

   public SmartCardReaderSession getSmartCardReaderSessionResult() {
      this.checkException();
      return (SmartCardReaderSession)this._objectResult;
   }

   protected void call() {
      throw null;
   }

   public void checkException() throws SmartCardException {
      if (!(this._exception instanceof SmartCardException)) {
         this.checkExceptionNoSmartCard();
      } else {
         throw (SmartCardException)this._exception;
      }
   }

   public void checkExceptionNoSmartCard() {
      if (this._exception != null) {
         throw new Object(this._exception.toString());
      }
   }

   public boolean getBooleanResult() {
      this.checkException();
      return this._booleanResult;
   }

   public boolean getBooleanResultNoSCException() {
      this.checkExceptionNoSmartCard();
      return this._booleanResult;
   }

   public String getStringResult() {
      this.checkException();
      return (String)this._objectResult;
   }

   public String getStringResultNoSCException() {
      this.checkExceptionNoSmartCard();
      return (String)this._objectResult;
   }

   public AnswerToReset getAnswerToResetResult() {
      this.checkException();
      return (AnswerToReset)this._objectResult;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      try {
         this.call();
      } catch (Throwable var3) {
         this._exception = e;
         return;
      }
   }

   Function(Object param1, Object param2) {
      this._param1 = param1;
      this._param2 = param2;
   }

   Function(int paramInt) {
      this._paramInt = paramInt;
   }

   Function(Object param1) {
      this._param1 = param1;
   }

   Function() {
   }
}
