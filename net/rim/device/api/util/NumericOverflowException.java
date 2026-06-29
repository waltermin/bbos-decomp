package net.rim.device.api.util;

public class NumericOverflowException extends RuntimeException {
   public NumericOverflowException() {
      super("Number exceeded boundaries.");
   }

   public NumericOverflowException(String message) {
      super(message);
   }
}
