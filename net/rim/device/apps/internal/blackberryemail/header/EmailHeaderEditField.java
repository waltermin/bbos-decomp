package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.Tooltip;
import net.rim.device.api.ui.container.Tooltip$TooltipProvider;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.DisplayPictureModel;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.AddressVerifier;
import net.rim.device.apps.api.framework.model.AddressVerifierAwareField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.internal.blackberryemail.email.ThemeUtilities;

final class EmailHeaderEditField extends HorizontalFieldManager implements AddressVerifierAwareField, Tooltip$TooltipProvider {
   Field _insideField;
   boolean _isResolvedAddressBookLookup;
   Object _addressModel;
   private AddressVerifier _addressVerifier = null;
   private Field _addressTrustIndicatorField;
   private EmailHeaderEditField$TooltipFieldManager _tooltipField;
   private static final Bitmap UNTRUSTED_ADDRESS_BITMAP = Bitmap.getBitmapResource("net_rim_bb_framework_api", "untrustedaddress.png");
   private static final int TOOLTIP_DURATION_MS;

   EmailHeaderEditField(Object context) {
      super(1152921504606846976L);
      this._isResolvedAddressBookLookup = ContextObject.getFlag(context, 114);
      this._addressVerifier = (AddressVerifier)ContextObject.get(context, 9120441889802231811L);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (Keypad.key(keycode) == 21) {
         Field field = this.getLeafFieldWithFocus();
         if (field != null && ControllerUtilities.invokeApplicationKeyVerb(field.getCookie())) {
            return true;
         }
      }

      return super.keyDown(keycode, time);
   }

   private final boolean isAddressTrusted() {
      if (this._addressVerifier == null) {
         return true;
      }

      if (this._addressModel instanceof Object) {
         return this._addressVerifier.isAddressTrusted(((FriendlyNameAddressModel)this._addressModel).getAddress(), null);
      }

      if (this._addressModel instanceof Object) {
         GroupAddressCardModel groupAddressCard = (GroupAddressCardModel)this._addressModel;

         for (int i = groupAddressCard.size() - 1; i >= 0; i--) {
            RIMModel addressModel = groupAddressCard.getAddressModelAt(i);
            if (addressModel instanceof Object && !this._addressVerifier.isAddressTrusted(((FriendlyNameAddressModel)addressModel).getAddress(), null)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public final void verifyAddress(Object context) {
      if (!this.isAddressTrusted()) {
         if (this._addressTrustIndicatorField == null) {
            BitmapField iconField = (BitmapField)(new Object(UNTRUSTED_ADDRESS_BITMAP));
            iconField.setSpace(1, 0);
            this._addressTrustIndicatorField = iconField;
            this.insert(this._addressTrustIndicatorField, 0);
            return;
         }
      } else if (this._addressTrustIndicatorField != null) {
         this.delete(this._addressTrustIndicatorField);
         this._addressTrustIndicatorField = null;
      }
   }

   @Override
   public final void provideTooltip(Tooltip tooltip) {
      UiApplication app = UiApplication.getUiApplication();
      synchronized (app.getAppEventLock()) {
         Field leafField = this.getLeafFieldWithFocus();
         if (leafField != null && leafField.isFocus()) {
            if (this._tooltipField == null) {
               if (!(this._insideField instanceof Object)) {
                  return;
               }

               if (!(this._addressModel instanceof Object)) {
                  return;
               }

               EmailAddressModel addressModel = (EmailAddressModel)this._addressModel;
               Object addressCardObj = AddressBookServices.reverseLookup(addressModel);
               AddressCardModel addressCard = null;
               if (addressCardObj instanceof Object) {
                  addressCard = (AddressCardModel)addressCardObj;
               }

               String address = addressModel.getAddress();
               String name = null;
               if (addressCard != null) {
                  name = addressCard.getName().toString();
               } else {
                  name = ((FriendlyNameAddressModel)this._addressModel).getFriendlyName();
               }

               Field photoField = null;
               if (addressCard != null) {
                  photoField = createFieldForDisplayPictureModel(addressCard.getContactPicture(null));
                  if (photoField != null) {
                     photoField.setTag(ThemeUtilities.EMAIL_ADDRESS_TOOLTIP_PHOTO_TAG);
                  }
               }

               if ((address == null || address.length() == 0) && photoField == null) {
                  return;
               }

               this._tooltipField = new EmailHeaderEditField$TooltipFieldManager(this._insideField, 0);
               this._tooltipField.setPhotoField(photoField);
               this._tooltipField.setName(name);
               this._tooltipField.setAddress(address);
            }

            XYPoint tooltipPosition = this._tooltipField.getTooltipPosition();
            if (tooltipPosition != null) {
               tooltip.setPosition(tooltipPosition.x, tooltipPosition.y);
               tooltip.setDuration(15000);
               tooltip.setContent(this._tooltipField);
            }
         }
      }
   }

   private static final Field createFieldForDisplayPictureModel(DisplayPictureModel pictureModel) {
      BitmapField field = null;
      if (pictureModel != null) {
         int scale = Fixed32.toFP(2);
         byte[] imageData = pictureModel.getDisplayPicture();
         if (imageData != null) {
            field = (BitmapField)(new Object(null, 36028797018963968L));
            EncodedImage image = EncodedImage.createEncodedImage(imageData, 0, imageData.length);
            image = image.scaleImage32(scale, scale);
            field.setImage(image);
         }
      }

      return field;
   }
}
