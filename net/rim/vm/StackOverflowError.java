package net.rim.vm;

public final class StackOverflowError extends VirtualMachineError {
   public StackOverflowError() {
   }

   public StackOverflowError(String s) {
      super(s);
   }
}
