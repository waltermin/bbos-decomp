package javacard.framework;

public class TransactionException extends CardRuntimeException {
   public static final short IN_PROGRESS = 1;
   public static final short NOT_IN_PROGRESS = 2;
   public static final short BUFFER_FULL = 3;
   public static final short INTERNAL_FAILURE = 4;

   public TransactionException(short reason) {
      super(reason);
   }

   public static void throwIt(short reason) {
      throw new TransactionException(reason);
   }
}
