package net.rim.device.api.system;

public final class ControlledAccessException extends SecurityException {
   private CodeSigningKey _key;
   private String _deniedPermission;

   public ControlledAccessException() {
   }

   public ControlledAccessException(String msg) {
      super(msg);
   }

   public ControlledAccessException(String msg, String deniedPermissionString) {
      super(msg);
      this._deniedPermission = deniedPermissionString;
   }

   public ControlledAccessException(CodeSigningKey key) {
      this._key = key;
   }

   public final CodeSigningKey getCodeSigningKey() {
      return this._key;
   }

   public final String getDeniedPermissionString() {
      return this._deniedPermission;
   }
}
