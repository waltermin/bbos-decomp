package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.component.ObjectChoiceField;

final class EmailFilterList$ServiceInfo {
   private ServiceRecord _sr;
   public String _userId;
   public ObjectChoiceField _forward;
   public EmailFilterCollectionListField _listField;

   public EmailFilterList$ServiceInfo(ServiceRecord sr) {
      this._sr = sr;
   }

   @Override
   public final String toString() {
      return this._sr == null ? "" : this._sr.getName();
   }

   public final ServiceRecord getSR() {
      return this._sr;
   }

   final boolean isValid() {
      return this._sr != null && this._userId != null && this._forward != null && this._listField != null;
   }
}
