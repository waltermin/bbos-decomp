package net.rim.device.api.io;

import java.io.IOException;

public class FileNotFoundException extends IOException {
   public FileNotFoundException() {
   }

   public FileNotFoundException(String message) {
      super(message);
   }
}
