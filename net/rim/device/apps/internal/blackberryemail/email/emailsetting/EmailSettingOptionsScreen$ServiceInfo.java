package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;

final class EmailSettingOptionsScreen$ServiceInfo {
   private ServiceRecord _sr;
   public Field _emailSettingFields;
   public VerticalFieldManager _vfm;

   public EmailSettingOptionsScreen$ServiceInfo(ServiceRecord sr) {
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
