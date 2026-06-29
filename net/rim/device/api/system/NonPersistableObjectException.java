package net.rim.device.api.system;

public final class NonPersistableObjectException extends RuntimeException {
   private Object _nonPersistableObject;

   public final Object getObject() {
      return this._nonPersistableObject;
   }
}
