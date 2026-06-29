package javacard.security;

import javacard.framework.CardRuntimeException;

public class CryptoException extends CardRuntimeException {
   public static final short ILLEGAL_VALUE;
   public static final short UNINITIALIZED_KEY;
   public static final short NO_SUCH_ALGORITHM;
   public static final short INVALID_INIT;
   public static final short ILLEGAL_USE;

   public CryptoException(short reason) {
      super(reason);
   }

   public static void throwIt(short reason) {
      throw new CryptoException(reason);
   }
}
