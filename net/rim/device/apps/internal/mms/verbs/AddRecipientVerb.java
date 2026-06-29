package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.ui.RecipientFieldManager;

public final class AddRecipientVerb extends AbstractComposeVerb {
   private String _label;
   private RecipientFieldManager _recipientFieldMgr;

   public AddRecipientVerb(String label, int ordering, RecipientFieldManager recipientListField) {
      super(ordering);
      this._label = label;
      this._recipientFieldMgr = recipientListField;
   }

   @Override
   public final Object copy() {
      return new AddRecipientVerb(this._label, this.getOrdering(), this._recipientFieldMgr);
   }

   @Override
   public final String toString(Object context) {
      return this._label;
   }

   @Override
   public final Object doInvoke(Object context) {
      if (super._address == null) {
         this.resolveAddress();
      }

      if (super._address != null && !MMSUtilities.modelIsAGroupWithAllInvalidAddresses(super._address)) {
         this._recipientFieldMgr.addRecipient(super._address, super._addressCard);
         this._recipientFieldMgr.setDirty(true);
      }

      return null;
   }
}
