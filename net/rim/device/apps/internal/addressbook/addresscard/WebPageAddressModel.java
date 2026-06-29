package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.text.URLTextFilter;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.apps.api.browser.BrowserServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.SyncFieldIDProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.vm.Array;

public final class WebPageAddressModel implements PersistableRIMModel, FieldProvider, ConversionProvider, VerbProvider, SyncFieldIDProvider {
   private Object _addressEncoding;

   public final void setAddress(String address) {
      if (address == null) {
         address = "";
      }

      this._addressEncoding = PersistentContent.encode(address, false, true);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      if (!ContextObject.getFlag(context, 0) && !ContextObject.getFlag(context, 5) && BrowserServices.isBrowserAvailable()) {
         Field uiField = (Field)ContextObject.get(context, 9045827404276417370L);
         String address = this.getAddress();
         if (uiField != null && uiField.getCookie() instanceof WebPageAddressModel && address != null && address.length() > 0) {
            Array.resize(verbs, 1);
            verbs[0] = new WebPageAddressModel$FollowLinkVerb(address);
            return verbs[0];
         }
      }

      return null;
   }

   public final String getAddress() {
      try {
         return PersistentContent.decodeString(this._addressEncoding);
      } finally {
         ;
      }
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (field instanceof Object && field.isEditable()) {
         BasicEditField editField = (BasicEditField)field;
         this.setAddress(editField.getText().trim());
         return this.getAddress().length() > 0;
      } else {
         return false;
      }
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 19)) {
         String address = this.getAddress();
         if (address != null && address.length() > 0) {
            ((SyncBuffer)target).addField(this.getSyncFieldId(null), address);
         }

         return true;
      } else if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 54) && target instanceof Object) {
         String address = this.getAddress();
         if (address != null && address.length() > 0) {
            ((StringBuffer)target).append("\rURL:").append(address);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final int getSyncFieldId(Object context) {
      return 54;
   }

   @Override
   public final int getOrder(Object context) {
      if (ContextObject.getFlag(context, 11)) {
         return ContextObject.getFlag(context, 0) ? 4600 : 5600;
      } else {
         return 0;
      }
   }

   @Override
   public final Field getField(Object context) {
      String address = this.getAddress();
      AbstractStringWrapper addressWrapper = AbstractStringWrapper.createInstance(address);
      URLTextFilter filter = (URLTextFilter)(new Object());
      long flags = ContextObject.getFlag(context, 0) ? 4503599627370496L : 9007199254740992L;
      flags |= 2199023255552L;
      if (filter.validate(addressWrapper)) {
         flags |= 117440512;
      }

      Field field = (Field)(new Object(AddressBookResources.getString(1723), address, 2048, flags));
      field.setCookie(this);
      return field;
   }

   @Override
   public final String toString() {
      return this.getAddress();
   }

   public WebPageAddressModel(Object initialData) {
      String address = null;
      if (!(initialData instanceof Object)) {
         if (initialData instanceof Object) {
            address = (String)ContextObject.get(initialData, 253);
         }
      } else {
         address = (String)initialData;
      }

      this.setAddress(address);
   }
}
