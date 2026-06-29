package net.rim.device.api.io;

import java.io.IOException;
import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class FileEventDispatcher extends EventDispatcher {
   private int _operationInProgress;
   private int _operationResult;

   final int waitForCompletion(int operation) throws IOException {
      this._operationInProgress = operation;
      this._operationResult = -1;

      try {
         this.wait(60000);
      } catch (InterruptedException var3) {
      }

      if (this._operationResult == -1) {
         throw new IOException("Operation timed out");
      } else {
         return this._operationResult;
      }
   }

   @Override
   public final boolean notify(Message message) {
      synchronized (this) {
         if (message.getSubMessage() == this._operationInProgress) {
            this._operationResult = message.getData0();
            this.notify();
         } else {
            System.out.println("File response out-of-sequence");
         }

         return false;
      }
   }

   @Override
   public final void dispatch(Message message, Object listener) {
   }
}
