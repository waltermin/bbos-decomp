package net.rim.vm;

public final class VMError extends VirtualMachineError {
   public VMError() {
   }

   public VMError(String s) {
      super(s);
   }
}
