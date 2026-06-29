package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.framework.verb.Verb;

class EmailComposeComboField$SetInsideModelVerb extends Verb {
   private FriendlyNameAddressModel _insideModel;
   private AddressCardModel _addressCard;
   private final EmailComposeComboField this$0;

   public EmailComposeComboField$SetInsideModelVerb(EmailComposeComboField _1, FriendlyNameAddressModel insideModel, AddressCardModel addressCard) {
      super(0);
      this.this$0 = _1;
      this._insideModel = insideModel;
      this._addressCard = addressCard;
      super._verbGroupId = _1._messageType.getVerbGroupId();
   }

   @Override
   public Object invoke(Object parameter) {
      this.this$0.setAddressFromCard(this._addressCard, this._insideModel, false);
      return this._insideModel;
   }

   @Override
   public String toString() {
      return this._insideModel.getAddress();
   }
}
