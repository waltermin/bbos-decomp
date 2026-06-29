package net.rim.device.apps.internal.phone;

import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class RIMPhone$PhoneSource {
   private String _lineNumber = "";

   public final void setLineNumber(String lineNumber) {
      this._lineNumber = ((StringBuffer)(new Object(" "))).append(lineNumber).toString();
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object())).append(PhoneResources.getString(118)).append(this._lineNumber).toString();
   }
}
