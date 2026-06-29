package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.mms.ui.RecipientFieldManager;

public final class ChangeRecipientVerb extends AbstractComposeVerb {
   private Field _originalField;
   private RecipientFieldManager _recipientFieldMgr;

   public ChangeRecipientVerb(Field originalField, RecipientFieldManager recipientListField) {
      super(16859728);
      this._originalField = originalField;
      this._recipientFieldMgr = recipientListField;
   }

   @Override
   public final Object copy() {
      return new ChangeRecipientVerb(this._originalField, this._recipientFieldMgr);
   }

   @Override
   public final String toString(Object context) {
      return EmailResources.getString(2);
   }

   @Override
   public final Object doInvoke(Object context) {
      if (super._address == null) {
         this.resolveAddress();
      }

      if (super._address != null) {
         this._recipientFieldMgr.replaceRecipient(this._originalField, super._address);
         this._recipientFieldMgr.setDirty(true);
      }

      return null;
   }
}
