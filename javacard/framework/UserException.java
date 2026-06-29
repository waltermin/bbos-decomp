package javacard.framework;

public class UserException extends CardException {
   public UserException() {
      this((short)0);
   }

   public UserException(short reason) {
      super(reason);
   }

   public static void throwIt(short reason) {
      throw new UserException(reason);
   }
}
