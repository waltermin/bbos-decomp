package java.lang;

public class IndexOutOfBoundsException extends RuntimeException {
   private int index;
   private boolean indexSet;
   private int maxIndex;
   private boolean maxIndexSet;

   public IndexOutOfBoundsException() {
   }

   @Override
   public String getMessage() {
      if (!this.indexSet) {
         return super.getMessage();
      } else {
         return !this.maxIndexSet ? "Index " + this.index + " out of bounds." : "Index " + this.index + " >= " + this.maxIndex;
      }
   }

   public IndexOutOfBoundsException(String s) {
      super(s);
   }
}
