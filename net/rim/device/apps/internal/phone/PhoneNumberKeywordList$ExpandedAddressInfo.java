package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.device.internal.ui.Image;
import net.rim.vm.Array;

final class PhoneNumberKeywordList$ExpandedAddressInfo {
   private AddressCardModel _addressCard;
   private PhoneNumberModel[] _phoneNumbers = new PhoneNumberModel[0];
   private int _indexSelected;
   private boolean _showNumbers;

   PhoneNumberKeywordList$ExpandedAddressInfo(AddressCardModel addressCard, int index) {
      this._addressCard = addressCard;
      this._indexSelected = index;
      this._showNumbers = false;
      int size = this._addressCard.size();
      boolean hasFax = false;

      for (int i = 0; i < size; i++) {
         Object o = this._addressCard.getAt(i);
         if (o instanceof PhoneNumberModel) {
            PhoneNumberModel pnm = (PhoneNumberModel)o;
            Array.resize(this._phoneNumbers, this._phoneNumbers.length + 1);
            if (hasFax) {
               this._phoneNumbers[this._phoneNumbers.length - 1] = this._phoneNumbers[this._phoneNumbers.length - 2];
               this._phoneNumbers[this._phoneNumbers.length - 2] = pnm;
            } else {
               this._phoneNumbers[this._phoneNumbers.length - 1] = pnm;
               if (pnm.getType() == 7) {
                  hasFax = true;
               }
            }
         }
      }
   }

   public final int numEntries() {
      return this._phoneNumbers.length;
   }

   public final AddressCardModel getAddressCard() {
      return this._addressCard;
   }

   public final PhoneNumberModel getSelectedPhoneNumber(int index) {
      index -= this._indexSelected;
      return this._phoneNumbers.length > index && index >= 0 ? this._phoneNumbers[index] : null;
   }

   public final int getIndex() {
      return this._indexSelected;
   }

   public final void setShowNumbers(boolean showNumbers) {
      this._showNumbers = showNumbers;
   }

   public final boolean showNumbers() {
      return this._showNumbers;
   }

   public final void drawItem(Graphics graphics, int index, int y, int width, boolean drawSendIcon) {
      index -= this._indexSelected + 1;
      PhoneNumberModel pnm = this._phoneNumbers[index];
      String typeString = pnm.getTypeString();
      String numberString = pnm.toString();
      if (drawSendIcon) {
         int fontHeight = Font.getDefault().getHeight();
         Image returnImage = PhoneNumberInput.getReturnKeyImage(0);
         int imageWidth = returnImage.getWidth(fontHeight, fontHeight);
         int imageHeight = returnImage.getHeight(fontHeight, fontHeight);
         returnImage.paint(graphics, width - imageWidth, y + (fontHeight - imageHeight >> 1), imageWidth, imageHeight);
         width -= imageWidth;
      }

      graphics.drawText(typeString + ": " + PhoneNumberServices.convertForDisplayWithExtension(numberString, false), 10, y, 64, width - 10);
   }
}
