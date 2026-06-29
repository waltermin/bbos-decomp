package net.rim.device.api.io;

import java.io.IOException;

public class IOFormatException extends IOException {
   public IOFormatException() {
   }

   public IOFormatException(String msg) {
      super(msg);
   }
}
