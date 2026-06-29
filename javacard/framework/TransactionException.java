package javacard.framework;

public class TransactionException extends CardRuntimeException {
   public static final short IN_PROGRESS;
   public static final short NOT_IN_PROGRESS;
   public static final short BUFFER_FULL;
   public static final short INTERNAL_FAILURE;

   public TransactionException(short reason) {
      super(reason);
   }

   public static void throwIt(short reason) {
      throw new TransactionException(reason);
   }
}
