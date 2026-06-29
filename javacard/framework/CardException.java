package javacard.framework;

public class CardException extends Exception {
   private short _reason;

   public CardException(short reason) {
      this.setReason(reason);
   }

   public short getReason() {
      return this._reason;
   }

   public void setReason(short reason) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public static void throwIt(short reason) throws CardException {
      throw new CardException(reason);
   }
}
