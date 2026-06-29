package net.rim.device.api.system;

public final class PersistentContentException extends RuntimeException {
   private int _badObjectId;

   public final int getObjectId() {
      return this._badObjectId;
   }
}
