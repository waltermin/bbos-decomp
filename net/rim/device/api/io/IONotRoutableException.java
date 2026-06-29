package net.rim.device.api.io;

import java.io.IOException;

public class IONotRoutableException extends IOException {
   public IONotRoutableException() {
   }

   public IONotRoutableException(String msg) {
      super(msg);
   }
}
