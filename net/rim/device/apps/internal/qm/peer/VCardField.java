package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.ui.CommonResources;

final class VCardField extends MessageField implements FieldChangeListener {
   private VCardMessage _message;
   private AddressCardModel _address;
   private FlowFieldManager _ffm = (FlowFieldManager)(new Object(12884901888L));
   private ButtonField _addButton;
   private ButtonField _cancelButton;
   VerticalFieldManager _manager = (VerticalFieldManager)(new Object());

   VCardField(VCardMessage message) {
      this._message = message;
      this._address = message.getAddressCard();
      Field field = ((FieldProvider)this._address).getField(new Object(45, 54, 11));
      if (field != null) {
         int state = message.getState();
         this._manager.add((Field)(new Object()));
         switch (state) {
            case -1:
               break;
            case 0:
            default:
               this._manager.add(this.getTextField(state));
               this._addButton = (ButtonField)(new Object(PeerResources.getString(2047)));
               this._addButton.setChangeListener(this);
               this._cancelButton = (ButtonField)(new Object(CommonResources.getString(9042)));
               this._cancelButton.setChangeListener(this);
               this._ffm.add(this._addButton);
               this._ffm.add(this._cancelButton);
               break;
            case 1:
               this._ffm.add(this.getTextField(state));
               break;
            case 2:
               this._ffm.add(this.getTextField(state));
         }

         this._manager.add(field);
         this._manager.add(this._ffm);
         this._manager.add((Field)(new Object()));
         this.add(this._manager);
      }
   }

   private final Field getTextField(int state) {
      RichTextField rtf = null;
      switch (state) {
         case 0:
         default:
            rtf = (RichTextField)(new Object(PeerResources.getString(2046)));
            rtf.setAttributes(new int[]{32768, -804651007, 40960, -804651005}, new int[]{16777215, 1866989824, 727916, 1987005697});
            return rtf;
         case 1:
            rtf = (RichTextField)(new Object(PeerResources.getString(2048)));
            rtf.setAttributes(new int[]{32768, -804651007, 40960, -804651005}, new int[]{16777215, 1866989824, 727916, 1987005697});
            return rtf;
         case 2:
            rtf = (RichTextField)(new Object(PeerResources.getString(891)));
            rtf.setAttributes(new int[]{16711680, -804651007, 32768, -804651007}, new int[]{16777215, 1866989824, 727916, 1987005697});
         case -1:
            return rtf;
      }
   }

   @Override
   public final void update() {
   }

   @Override
   public final MessengerMessage getMessage() {
      return this._message;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._addButton) {
         AddressBookServices.addAddressCard(this._address);
         this._message.setState(1);
         this._ffm.deleteAll();
         this._ffm.add(this.getTextField(1));
      } else {
         if (field == this._cancelButton) {
            this._message.setState(2);
            this._ffm.deleteAll();
            this._ffm.add(this.getTextField(2));
         }
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      Field field = this.getLeafFieldWithFocus();
      if (field != this._addButton && field != this._cancelButton) {
         return super.trackwheelClick(status, time);
      }

      this.fieldChanged(field, 0);
      return true;
   }
}
