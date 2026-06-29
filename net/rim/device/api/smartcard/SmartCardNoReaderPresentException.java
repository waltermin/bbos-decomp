package net.rim.device.api.smartcard;

public class SmartCardNoReaderPresentException extends SmartCardException {
   public SmartCardNoReaderPresentException() {
   }

   public SmartCardNoReaderPresentException(String msg) {
      super(msg);
   }
}
