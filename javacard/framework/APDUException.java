package javacard.framework;

public class APDUException extends CardRuntimeException {
   public static final short BAD_LENGTH;
   public static final short BUFFER_BOUNDS;
   public static final short ILLEGAL_USE;
   public static final short IO_ERROR;
   public static final short NO_T0_GETRESPONSE;
   public static final short T1_IFD_ABORT;
   public static final short NO_T0_REISSUE;

   public APDUException(short reason) {
      super(reason);
   }

   public static void throwIt(short reason) {
      throw new APDUException(reason);
   }
}
