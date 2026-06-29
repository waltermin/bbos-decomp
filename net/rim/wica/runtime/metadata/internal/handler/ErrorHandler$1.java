package net.rim.wica.runtime.metadata.internal.handler;

import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.metadata.component.REError;

class ErrorHandler$1 implements Runnable {
   private final REError val$error;
   private final ErrorHandler this$0;

   ErrorHandler$1(ErrorHandler this$0, REError val$error) {
      this.this$0 = this$0;
      this.val$error = val$error;
   }

   @Override
   public void run() {
      StringBuffer message = new StringBuffer(this.val$error.getDescription());
      message.append("\nError Code: ");
      message.append(this.val$error.getCode());
      message.append("\nDetails: ");
      message.append(this.val$error.getErrorDescriptor());
      Logger.log(message.toString(), 3);
      int errorClass = ErrorHandler.getErrorClass(this.val$error);
      if (errorClass != 4 && errorClass != 5) {
         if (this.this$0._errorScript != -1) {
            this.this$0._scriptEngine.call(this.this$0._errorScript, this.val$error);
         }
      } else if (this.this$0._uiService != null) {
         this.this$0._uiService.displayModalDialog(-1, this.val$error.getDescription());
      }

      if (errorClass >= 3) {
         this.this$0._runtime.stop(this.val$error != null, false);
      }
   }
}
