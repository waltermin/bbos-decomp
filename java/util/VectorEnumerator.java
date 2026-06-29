package java.util;

final class VectorEnumerator implements Enumeration {
   Vector vector;
   int count;

   VectorEnumerator(Vector v) {
      this.vector = v;
      this.count = 0;
   }

   @Override
   public final boolean hasMoreElements() {
      return this.count < this.vector.elementCount;
   }

   @Override
   public final Object nextElement() {
      synchronized (this.vector) {
         if (this.count < this.vector.elementCount) {
            return this.vector.elementData[this.count++];
         }
      }

      throw new NoSuchElementException("VectorEnumerator");
   }
}
