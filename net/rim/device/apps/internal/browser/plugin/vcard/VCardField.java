package net.rim.device.apps.internal.browser.plugin.vcard;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.ui.ButtonBorder;

final class VCardField extends VerticalFieldManager implements FieldChangeListener {
   private AddressCardModel _address;
   private ButtonField _buttonField;
   private boolean _added;
   private static final Tag BUTTON_TAG = Tag.create("browser-button");

   VCardField(long fieldStyle, AddressCardModel addressCard) {
      super(fieldStyle);
      this._address = addressCard;
      Field f = ((FieldProvider)this._address).getField(new Object(45, 54, 11));
      if (f != null) {
         this.add(f);
         boolean color = Graphics.isColor();
         this._buttonField = (ButtonField)(new Object(BrowserResources.getString(720), 98304));
         if (color) {
            this._buttonField.setTag(BUTTON_TAG);
            ButtonBorder border = (ButtonBorder)(new Object(0, 14079694, null));
            this._buttonField.setBorder(0, border);
            this._buttonField.setBorder(7, border);
            border = (ButtonBorder)(new Object(1, 14079694, null));
            this._buttonField.setBorder(6, border);
            this._buttonField.setBorder(8, border);
            border = (ButtonBorder)(new Object(2, 14079694, null));
            this._buttonField.setBorder(4, border);
         }

         this._buttonField.setChangeListener(this);
         this.add(this._buttonField);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (!this._added && field == this._buttonField && context != Integer.MIN_VALUE) {
         AddressBookServices.addAddressCard(this._address);
         Dialog.alert(BrowserResources.getString(721));
         this._buttonField.setEditable(false);
         this._added = true;
      }
   }
}
