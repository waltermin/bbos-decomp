package net.rim.device.api.smartcard;

public class SmartCardBlockTimeoutException extends SmartCardTimeoutException {
   public SmartCardBlockTimeoutException() {
   }

   public SmartCardBlockTimeoutException(String msg) {
      super(msg);
   }
}
