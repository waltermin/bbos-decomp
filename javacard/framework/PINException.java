package javacard.framework;

public class PINException extends CardRuntimeException {
   public static final short ILLEGAL_VALUE = 1;

   public PINException(short reason) {
      super(reason);
   }

   public static void throwIt(short reason) {
      throw new PINException(reason);
   }
}
