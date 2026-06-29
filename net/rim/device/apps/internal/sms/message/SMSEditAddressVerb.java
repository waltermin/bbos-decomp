package net.rim.device.apps.internal.sms.message;

import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.sms.SMSModel;
import net.rim.device.apps.internal.sms.SMSService;
import net.rim.device.apps.internal.sms.resources.SMSResources;

class SMSEditAddressVerb extends Verb {
   private SMSEditorScreen _editScreen;
   private SMSModel _SMSModel;
   private RIMModel _selectedAddress;
   private int _type;
   static final int ADD = 0;
   static final int CHANGE = 1;
   static final int DELETE = 2;

   SMSEditAddressVerb(SMSEditorScreen editScreen, int type, RIMModel selectedAddress) {
      this((SMSModel)editScreen.getModel(), type, selectedAddress);
      this._editScreen = editScreen;
   }

   SMSEditAddressVerb(SMSModel model, int type, RIMModel selectedAddress) {
      super(0, SMSResources.getResourceBundle(), 0);
      switch (type) {
         case -1:
            throw new Object();
         case 0:
         default:
            super._ordering = 16859648;
            super._rbKey = 413;
            break;
         case 1:
            super._ordering = 16859728;
            super._rbKey = 750;
            if (selectedAddress == null) {
               throw new Object();
            }
            break;
         case 2:
            super._ordering = 16879616;
            super._rbKey = 416;
            if (selectedAddress == null) {
               throw new Object();
            }
      }

      this._type = type;
      this._SMSModel = model;
      this._selectedAddress = selectedAddress;
   }

   @Override
   public Object invoke(Object context) {
      PersistableRIMModel newAddress = null;
      if (this._SMSModel != null) {
         if (this._type != 2) {
            AddressSelectionContext selectionContext = SMSMessageModel.getAddressSelectionContext();
            newAddress = SMSMessageModel.selectAddress(selectionContext);
            if (newAddress == null || !SMSService.validateAddress(newAddress)) {
               return null;
            }

            Object selectedSource = selectionContext.getSelectedSource();
            if (selectedSource != null && selectedSource != newAddress) {
               this._SMSModel.setFlags(64);
            } else {
               this._SMSModel.clearFlags(64);
            }
         }

         switch (this._type) {
            case -1:
               break;
            case 0:
            default:
               this._SMSModel.addAddress(newAddress);
               break;
            case 1:
               this._SMSModel.changeAddress(this._selectedAddress, newAddress);
               break;
            case 2:
               this._SMSModel.removeAddress(this._selectedAddress);
         }
      }

      if (this._editScreen != null) {
         ContextObject result = (ContextObject)(new Object(39));
         this._editScreen._runAgain = true;
         this._editScreen.setDirty(true);
         return result;
      } else {
         return newAddress;
      }
   }
}
