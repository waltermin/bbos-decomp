package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;

final class OTAFMOptionsScreen$ServiceInfo {
   private ServiceRecord _sr;
   private Field _otafmFields;

   public OTAFMOptionsScreen$ServiceInfo(ServiceRecord sr) {
      this._sr = sr;
   }

   @Override
   public final String toString() {
      return this._sr == null ? "" : this._sr.getName();
   }

   public final ServiceRecord getSR() {
      return this._sr;
   }
}
