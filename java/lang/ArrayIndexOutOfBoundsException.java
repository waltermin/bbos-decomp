package java.lang;

public class ArrayIndexOutOfBoundsException extends IndexOutOfBoundsException {
   public ArrayIndexOutOfBoundsException() {
   }

   public ArrayIndexOutOfBoundsException(int index) {
      super(Integer.toString(index));
   }

   public ArrayIndexOutOfBoundsException(String s) {
      super(s);
   }
}
