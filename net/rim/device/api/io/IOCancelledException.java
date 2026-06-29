package net.rim.device.api.io;

import java.io.IOException;

public class IOCancelledException extends IOException {
   public IOCancelledException() {
   }

   public IOCancelledException(String msg) {
      super(msg);
   }
}
