package javacard.framework;

public class CardRuntimeException extends RuntimeException {
   private short _reason;

   public CardRuntimeException(short reason) {
      this.setReason(reason);
   }

   public short getReason() {
      return this._reason;
   }

   public void setReason(short reason) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public static void throwIt(short reason) {
      throw new CardRuntimeException(reason);
   }
}
