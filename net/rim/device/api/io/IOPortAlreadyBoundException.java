package net.rim.device.api.io;

import java.io.IOException;

public class IOPortAlreadyBoundException extends IOException {
   public IOPortAlreadyBoundException() {
   }

   public IOPortAlreadyBoundException(String msg) {
      super(msg);
   }
}
