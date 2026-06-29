package net.rim.device.apps.internal.supl;

final class SuplException extends Exception {
   StatusCode error;

   SuplException() {
   }

   SuplException(byte errorCode) {
      this.error = new StatusCode(errorCode);
   }
}
