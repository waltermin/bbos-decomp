package net.rim.device.api.io;

import java.io.IOException;

public class IODatagramLengthException extends IOException {
   public IODatagramLengthException() {
   }

   public IODatagramLengthException(String msg) {
      super(msg);
   }
}
