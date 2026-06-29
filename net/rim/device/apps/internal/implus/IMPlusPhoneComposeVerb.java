package net.rim.device.apps.internal.implus;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;

final class IMPlusPhoneComposeVerb extends Verb implements SetParameter, Copyable {
   private PersistableRIMModel _phoneNumber;
   private PersistableRIMModel _addressCard;
   private ContextObject _context;
   private boolean _isFax;

   public IMPlusPhoneComposeVerb(boolean isFax) {
      super(1266768);
      this._isFax = isFax;
      if (this._isFax) {
         super._ordering = 1266805;
      }
   }

   @Override
   public final Object copy() {
      return new IMPlusPhoneComposeVerb(this._isFax);
   }

   @Override
   public final void setParameter(Object context) {
      if (context instanceof ContextObject) {
         this._context = (ContextObject)context;
         if (ContextObject.getFlag(context, 7)) {
            super._ordering = this._isFax ? 328016 : 327973;
         }

         this._phoneNumber = (PersistableRIMModel)this._context.get(247);
         this._addressCard = (PersistableRIMModel)this._context.get(252);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String toString() {
      return this.toString(this._context);
   }

   @Override
   public final String toString(Object context) {
      if (this._phoneNumber != null) {
         String address;
         if (!(this._phoneNumber instanceof VerbDescriptionProvider)) {
            address = this._phoneNumber.toString();
         } else {
            VerbDescriptionProvider verbDescriptionProvider = (VerbDescriptionProvider)this._phoneNumber;
            if (context instanceof ContextObject && this._addressCard != null) {
               ContextObject.put(context, 252, this._addressCard);
            }

            address = verbDescriptionProvider.getVerbDescription(context);
         }

         boolean addressOnly = ContextObject.getFlag(context, 51);
         if (addressOnly) {
            return address;
         }

         StringBuffer buf = new StringBuffer();
         String prefix = IMPlusResources.getString(17);
         buf.append(prefix);
         buf.append(' ');
         buf.append(address);
         return buf.toString();
      } else {
         return IMPlusResources.getString(19);
      }
   }

   @Override
   public final Object invoke(Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      if (this._phoneNumber != null) {
         contextObject.put(254, this._phoneNumber);
      }

      return IMPlusCmimeListener.getInstance()._composeIMPlusVerb.invoke(contextObject);
   }

   @Override
   public final int getVerbGroupId() {
      return 1187214;
   }

   @Override
   public final RIMModel getRIMModel() {
      return this._phoneNumber != null ? this._phoneNumber : this._addressCard;
   }
}
