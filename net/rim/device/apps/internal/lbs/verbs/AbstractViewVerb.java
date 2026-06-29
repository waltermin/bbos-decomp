package net.rim.device.apps.internal.lbs.verbs;

import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.addressbook.mailingaddress.MailingAddressModelImpl;

class AbstractViewVerb extends Verb implements SetParameter, Copyable, MemoryCleanerListener {
   protected PersistableRIMModel _address;
   private PersistableRIMModel _addressCard;
   private ContextObject _context;

   @Override
   public String getDescription() {
      return null;
   }

   @Override
   public boolean cleanNow(int event) {
      if (event == 10 && this._address != null) {
         this._address = null;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void setParameter(Object context) {
      this._context = ContextObject.clone(context);
      super._ordering = 1131008;
      this._address = (PersistableRIMModel)this._context.get(250);
      this._addressCard = (PersistableRIMModel)this._context.get(252);
      if (this._address instanceof Object) {
         MailingAddressModelImpl model = (MailingAddressModelImpl)this._address;
         if (model.getType() == 1) {
            super._ordering++;
         }
      }
   }

   @Override
   public Object copy() {
      throw null;
   }

   @Override
   public Object invoke(Object context) {
      return null;
   }

   @Override
   public RIMModel getRIMModel() {
      return this._address != null ? this._address : this._addressCard;
   }

   @Override
   public final String toString() {
      return this.toString(this._context);
   }

   public AbstractViewVerb(int ordering) {
      super(ordering);
      MemoryCleanerDaemon.addWeakListener(this, false);
   }

   @Override
   public String toString(Object _1) {
      throw null;
   }
}
