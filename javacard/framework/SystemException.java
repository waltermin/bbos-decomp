package javacard.framework;

public class SystemException extends CardRuntimeException {
   public static final short ILLEGAL_VALUE;
   public static final short NO_TRANSIENT_SPACE;
   public static final short ILLEGAL_TRANSIENT;
   public static final short ILLEGAL_AID;
   public static final short NO_RESOURCE;
   public static final short ILLEGAL_USE;

   public SystemException(short reason) {
      super(reason);
   }

   public static void throwIt(short reason) {
      throw new SystemException(reason);
   }
}
