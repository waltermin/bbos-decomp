package net.rim.device.apps.api.addressbook;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.ui.ToggleableField;

public class AddressModelField extends AddressBookAwareField {
   private Object _context;

   public AddressModelField(RIMModel addressModel, Object context) {
      super(addressModel, context);
      this._context = ContextObject.clone(context);
      this.addFriendlyQualifiedFields();
   }

   private void addFriendlyQualifiedFields() {
      RIMModel viewedModel = this.getModel();
      Object associatedAddressCard = this.getAddressCard();
      Field qualifiedField = this.getQualifiedField(viewedModel, this._context);
      Field friendlyField = null;
      if (ContextObject.getFlag(this._context, 9)) {
         friendlyField = this.getFriendlyField(viewedModel, associatedAddressCard, this._context);
      }

      if (qualifiedField != null) {
         if (friendlyField != null) {
            ToggleableField toggleableField = new ToggleableField(friendlyField, qualifiedField);
            toggleableField.setCookie(viewedModel);
            this.add(toggleableField);
         } else {
            qualifiedField.setCookie(viewedModel);
            this.add(qualifiedField);
         }
      }

      this.setCookie(viewedModel);
   }

   protected Field getFriendlyField(RIMModel addressModel, Object addressCard, Object context) {
      Field friendlyField = null;
      if (addressCard instanceof FieldProvider) {
         FieldProvider fieldProvider = (FieldProvider)addressCard;
         boolean oldNoLabel = ContextObject.getFlag(this._context, 1);
         boolean oldEllipsis = ContextObject.getFlag(this._context, 17);
         boolean oldDetailed = ContextObject.getFlag(this._context, 4);
         boolean oldAddressOnly = ContextObject.getFlag(this._context, 51);
         ContextObject.setFlag(this._context, 1, 17, 51);
         ContextObject.clearFlag(this._context, 4);
         friendlyField = fieldProvider.getField(this._context);
         if (!oldNoLabel) {
            ContextObject.clearFlag(this._context, 1);
         }

         if (!oldEllipsis) {
            ContextObject.clearFlag(this._context, 17);
         }

         if (!oldAddressOnly) {
            ContextObject.clearFlag(this._context, 51);
         }

         if (oldDetailed) {
            ContextObject.setFlag(this._context, 4);
         }
      }

      return friendlyField;
   }

   protected Field getQualifiedField(RIMModel _1, Object _2) {
      throw null;
   }

   @Override
   protected void addressBookUpdated() {
      this.deleteAll();
      this.addFriendlyQualifiedFields();
   }
}
