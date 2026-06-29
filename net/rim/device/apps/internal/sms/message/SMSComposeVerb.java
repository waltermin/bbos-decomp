package net.rim.device.apps.internal.sms.message;

import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageAppVerb;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.sms.SMSModel;
import net.rim.device.apps.internal.sms.resources.SMSResources;

public class SMSComposeVerb extends ShowMessageAppVerb implements SetParameter, Copyable {
   private PersistableRIMModel _phoneNumber;
   private boolean _useSmartDialing;
   private PersistableRIMModel _addressCard;
   private ContextObject _context;

   protected ContextObject runEditor(ContextObject object, SMSModel model) {
      return ContextObject.castOrCreate(SMSEditorScreen.runEditor(object, model));
   }

   @Override
   public void setParameter(Object context) {
      if (context instanceof Object) {
         this._context = (ContextObject)context;
         if (ContextObject.getFlag(context, 7)) {
            super._ordering = 327984;
         }

         this._phoneNumber = (PersistableRIMModel)this._context.get(247);
         this._useSmartDialing = ContextObject.getFlag(this._context, 117) || ContextObject.getFlag(this._context, 83);
         this._addressCard = (PersistableRIMModel)this._context.get(252);
         if (this._addressCard != null) {
            this._useSmartDialing = true;
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public Object copy() {
      return new SMSComposeVerb();
   }

   @Override
   public String toString() {
      return this.toString(this._context);
   }

   @Override
   public String toString(Object context) {
      RIMModel model = null;
      if (this._phoneNumber != null) {
         model = this._phoneNumber;
      } else {
         model = (RIMModel)ContextObject.get(context, 254);
      }

      if (model != null) {
         String description = null;
         if (model instanceof Object) {
            if (context instanceof Object && this._addressCard != null) {
               ContextObject.put(context, 252, this._addressCard);
            }

            description = ((VerbDescriptionProvider)model).getVerbDescription(context);
         } else {
            description = model.toString();
         }

         if (!ContextObject.getFlag(context, 51) && !ContextObject.getFlag(context, 63)) {
            StringBuffer sb = (StringBuffer)(new Object(SMSResources.getString(192)));
            sb.append(' ');
            sb.append(description);
            description = sb.toString();
         }

         return description;
      } else {
         return SMSResources.getString(2);
      }
   }

   @Override
   public Object doInvoke(Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      contextObject.setFlag(31);
      contextObject.setFlag(37);
      if (contextObject.getFlag(12)) {
         ContextObject.remove(contextObject, -5663326727156203382L);
         contextObject.clearFlag(12);
      }

      contextObject.clearFlag(13);
      if (this._phoneNumber != null) {
         contextObject.put(254, this._phoneNumber);
      }

      if (this._addressCard != null) {
         contextObject.put(252, this._addressCard);
      }

      if (this._useSmartDialing) {
         contextObject.setFlag(117);
      } else {
         contextObject.clearFlag(117);
      }

      SMSMessageModel model = (SMSMessageModel)ContextObject.get(contextObject, -8485899342890396495L);
      if (model == null) {
         model = new SMSMessageModel(contextObject);
      }

      AddressBookServices.setLastSelectedAddress(ContextObject.get(context, 250));
      if (!model.setupAddress(contextObject)) {
         return null;
      }

      model.setFlags(32);
      ContextObject returnContext = this.runEditor(contextObject, model);
      boolean clearTerminalFlag = ContextObject.getFlag(context, 43)
         || ContextObject.getFlag(context, 74)
         || ContextObject.getFlag(context, 55)
         || ContextObject.getPrivateFlag(context, -337556985625701066L, 0);
      if (clearTerminalFlag) {
         returnContext.clearFlag(39);
      } else {
         returnContext.setFlag(39);
      }

      returnContext.clearFlag(40);
      return returnContext;
   }

   public SMSComposeVerb() {
      super(1266944);
   }

   @Override
   public int getVerbGroupId() {
      return 15307058;
   }

   @Override
   public RIMModel getRIMModel() {
      return this._phoneNumber != null ? this._phoneNumber : this._addressCard;
   }
}
