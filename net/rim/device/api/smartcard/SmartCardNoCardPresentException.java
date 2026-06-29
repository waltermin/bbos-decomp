package net.rim.device.api.smartcard;

public class SmartCardNoCardPresentException extends SmartCardException {
   public SmartCardNoCardPresentException() {
   }

   public SmartCardNoCardPresentException(String msg) {
      super(msg);
   }
}
