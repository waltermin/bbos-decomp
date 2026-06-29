package net.rim.device.api.io;

import java.io.IOException;

public class IORefusedException extends IOException {
   public IORefusedException() {
   }

   public IORefusedException(String msg) {
      super(msg);
   }
}
