package net.rim.device.apps.internal.ldap;

public class LDAPBrowserException extends Exception {
   private Exception _e;

   public LDAPBrowserException() {
   }

   public LDAPBrowserException(String message) {
   }

   public LDAPBrowserException(Exception e) {
      this._e = e;
   }

   public LDAPBrowserException(String message, Exception e) {
      super(message);
      this._e = e;
   }

   @Override
   public String getMessage() {
      return this._e != null ? super.getMessage() + this._e.getMessage() : super.getMessage();
   }

   @Override
   public String toString() {
      return this._e != null ? super.toString() + this._e.toString() : super.toString();
   }

   public Exception getException() {
      return this._e;
   }
}
