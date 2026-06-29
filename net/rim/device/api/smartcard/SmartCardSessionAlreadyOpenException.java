package net.rim.device.api.smartcard;

public class SmartCardSessionAlreadyOpenException extends SmartCardException {
   public SmartCardSessionAlreadyOpenException() {
   }

   public SmartCardSessionAlreadyOpenException(String msg) {
      super(msg);
   }
}
