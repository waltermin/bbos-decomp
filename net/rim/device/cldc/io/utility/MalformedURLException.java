package net.rim.device.cldc.io.utility;

import java.io.IOException;

public final class MalformedURLException extends IOException {
   public MalformedURLException() {
   }

   public MalformedURLException(String msg) {
      super(msg);
   }
}
