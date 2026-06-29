package net.rim.device.api.crypto;

import net.rim.vm.Process;

public class CryptoSelfTestError extends Error {
   public CryptoSelfTestError() {
      Process.killProcessIfThisThreadDies(true);
   }

   public CryptoSelfTestError(String msg) {
      super(msg);
      Process.killProcessIfThisThreadDies(true);
   }
}
