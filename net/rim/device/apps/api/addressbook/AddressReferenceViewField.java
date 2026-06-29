package net.rim.device.apps.api.addressbook;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.Tooltip;
import net.rim.device.api.ui.container.Tooltip$TooltipProvider;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.framework.model.AddressVerifier;
import net.rim.device.apps.api.framework.model.AddressVerifierAwareField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.internal.system.InternalServices;

public class AddressReferenceViewField extends HorizontalFieldManager implements AddressVerifierAwareField, Tooltip$TooltipProvider {
   private AddressReference _address;
   private String _label;
   private boolean _rightJustified;
   private String _friendlyName;
   private String _qualifiedAddress;
   private boolean _qualifiedVisible;
   private int _textInfo;
   private AddressVerifier _addressVerifier = null;
   private Bitmap _addressTrustIndicatorBitmap;
   private AddressReferenceViewField$AddressReferenceViewDataField _dataField;
   private AddressReferenceViewField$TooltipFieldManager _tooltipField;
   private boolean _trustedAddressCheckingEnabled = false;
   private static final Bitmap UNTRUSTED_ADDRESS_BITMAP = Bitmap.getBitmapResource("net_rim_bb_framework_api", "untrustedaddress.png");
   private static final int UNTRUSTED_ADDRESS_BITMAP_MARGIN = 1;
   private static Tag EMAIL_ADDRESS_TOOLTIP_TAG = Tag.create("email-address-tooltip");
   private static Tag EMAIL_ADDRESS_TOOLTIP_ADDRESS_TEXT_TAG = Tag.create("email-address-tooltip-address-text");
   private static Tag EMAIL_ADDRESS_TOOLTIP_NAME_TEXT_TAG = Tag.create("email-address-tooltip-name-text");
   private static Tag EMAIL_ADDRESS_TOOLTIP_PHOTO_TAG = Tag.create("email-address-tooltip-photo");
   private static final int TOOLTIP_DURATION_MS = 15000;

   public AddressReferenceViewField(AddressReference address, String label, long justification, Object context) {
      super(1170935903116328960L);
      this._address = address;
      this._label = label;
      this._rightJustified = justification == 8589934592L;
      this._addressVerifier = (AddressVerifier)ContextObject.get(context, 9120441889802231811L);
      this.setAddressValues();
      this._dataField = new AddressReferenceViewField$AddressReferenceViewDataField(this);
      this.add(this._dataField);
   }

   @Override
   protected void setCookieInternal(Object cookie) {
      super.setCookieInternal(cookie);
      this._dataField.setCookie(cookie);
   }

   private void setAddressValues() {
      RIMModel addressModel = this._address.getAddressBookEntry();
      if (addressModel != null) {
         this._friendlyName = addressModel.toString();
      } else {
         RIMModel insideModel = this._address.getInsideModel();
         if (!(insideModel instanceof FriendlyNameAddressModel)) {
            if (insideModel instanceof ConversionProvider) {
               String[] addressAndFriendlyName = new String[2];
               if (((ConversionProvider)insideModel).convert(new ContextObject(10), addressAndFriendlyName)) {
                  this._qualifiedAddress = addressAndFriendlyName[0];
                  if (addressAndFriendlyName[1] != null && addressAndFriendlyName[1].length() != 0) {
                     this._friendlyName = addressAndFriendlyName[1];
                  }
               }
            }
         } else {
            FriendlyNameAddressModel friendlyModel = (FriendlyNameAddressModel)insideModel;
            this._friendlyName = friendlyModel.getFriendlyName();
            this._qualifiedAddress = friendlyModel.getAddress();
         }
      }

      if (this._friendlyName == null && this._qualifiedAddress != null) {
         this._friendlyName = this._qualifiedAddress;
      }

      this.verifyAddress(null);
   }

   private boolean isQualifiedAddressAvailable() {
      if (this._qualifiedAddress == null) {
         RIMModel insideModel = this._address.getInsideModel();
         if (insideModel instanceof FriendlyNameAddressModel) {
            FriendlyNameAddressModel friendlyModel = (FriendlyNameAddressModel)insideModel;
            this._qualifiedAddress = friendlyModel.getAddress();
         }
      }

      return this._qualifiedAddress != null;
   }

   public boolean isFriendlyVisible() {
      return !this._qualifiedVisible;
   }

   @Override
   public boolean isSelectionCopyable() {
      return true;
   }

   @Override
   public void selectionCopy(Clipboard cb) {
      cb.put(this._qualifiedVisible ? this._qualifiedAddress : this._friendlyName);
   }

   public void toggleVisibleField() {
      if (this.isQualifiedAddressAvailable()) {
         this._qualifiedVisible = !this._qualifiedVisible;
         this.invalidate();
      }
   }

   public void reInitalizeField() {
      this.setAddressValues();
      this.invalidate();
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      if (!InternalServices.isReducedFormFactor() && Keypad.key(keycode) == 81) {
         this.toggleVisibleField();
         return true;
      } else {
         return super.keyUp(keycode, time);
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      if (Keypad.key(keycode) == 21) {
         Field field = this.getLeafFieldWithFocus();
         if (field != null && ControllerUtilities.invokeApplicationKeyVerb(this._address.getInsideModel())) {
            return true;
         }
      }

      return super.keyDown(keycode, time);
   }

   public Verb getToggleVerb(ResourceBundleFamily rbf, int resId) {
      return new ToggleAddressReferenceVerb(this, rbf, resId);
   }

   private boolean isAddressTrusted() {
      if (!this._trustedAddressCheckingEnabled || this._addressVerifier == null) {
         return true;
      } else {
         return !this.isQualifiedAddressAvailable() ? true : this._addressVerifier.isAddressTrusted(this._qualifiedAddress, null);
      }
   }

   @Override
   public void verifyAddress(Object context) {
      if (!this.isAddressTrusted()) {
         if (this._addressTrustIndicatorBitmap == null) {
            this._addressTrustIndicatorBitmap = UNTRUSTED_ADDRESS_BITMAP;
            return;
         }
      } else if (this._addressTrustIndicatorBitmap != null) {
         this._addressTrustIndicatorBitmap = null;
      }
   }

   @Override
   public void provideTooltip(Tooltip tooltip) {
      UiApplication app = UiApplication.getUiApplication();
      synchronized (app.getAppEventLock()) {
         Field leafField = this.getLeafFieldWithFocus();
         if (leafField != null && leafField.isFocus()) {
            if (this._tooltipField == null) {
               String name = this._friendlyName;
               if (name == null) {
                  return;
               }

               String address = null;
               if (this.isQualifiedAddressAvailable()) {
                  address = this._qualifiedAddress;
               }

               Field photoField = createFieldForDisplayPictureModel(getDisplayPictureModelFromAddress(this._address));
               if (photoField != null) {
                  photoField.setTag(EMAIL_ADDRESS_TOOLTIP_PHOTO_TAG);
               }

               if ((address == null || address.length() == 0 || name.equals(address)) && photoField == null) {
                  return;
               }

               long tooltipFlags = 2;
               if (this._rightJustified) {
                  tooltipFlags |= 1;
               }

               this._tooltipField = new AddressReferenceViewField$TooltipFieldManager(this._dataField, tooltipFlags);
               this._tooltipField.setPhotoField(photoField);
               this._tooltipField.setAddress(address);
               this._tooltipField.setName(name);
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

   private static DisplayPictureModel getDisplayPictureModelFromAddress(AddressReference address) {
      if (address != null) {
         RIMModel addressModel = address.getInsideModel();
         if (addressModel instanceof EmailAddressModel) {
            Object addressCard = AddressBookServices.reverseLookup(addressModel);
            if (addressCard instanceof AddressCardModel) {
               return ((AddressCardModel)addressCard).getContactPicture(null);
            }
         }
      }

      return null;
   }

   private static Field createFieldForDisplayPictureModel(DisplayPictureModel pictureModel) {
      BitmapField field = null;
      if (pictureModel != null) {
         int scale = Fixed32.toFP(2);
         byte[] imageData = pictureModel.getDisplayPicture();
         if (imageData != null) {
            field = new BitmapField(null, 36028797018963968L);
            EncodedImage image = EncodedImage.createEncodedImage(imageData, 0, imageData.length);
            image = image.scaleImage32(scale, scale);
            field.setImage(image);
         }
      }

      return field;
   }
}
