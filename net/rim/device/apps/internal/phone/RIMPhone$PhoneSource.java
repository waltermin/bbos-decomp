package net.rim.device.apps.internal.phone;

import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class RIMPhone$PhoneSource {
   private String _lineNumber = "";

   public final void setLineNumber(String lineNumber) {
      this._lineNumber = " " + lineNumber;
   }

   @Override
   public final String toString() {
      return PhoneResources.getString(118) + this._lineNumber;
   }
}
