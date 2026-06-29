package net.rim.vm;

public final class TooManyThreadsError extends OutOfMemoryError {
   public TooManyThreadsError() {
   }

   public TooManyThreadsError(String s) {
      super(s);
   }
}
