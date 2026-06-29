package net.rim.vm;

public final class TooManyProcessesError extends OutOfMemoryError {
   public TooManyProcessesError() {
   }

   public TooManyProcessesError(String s) {
      super(s);
   }
}
