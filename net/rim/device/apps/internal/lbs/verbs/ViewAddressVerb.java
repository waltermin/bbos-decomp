package net.rim.device.apps.internal.lbs.verbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.internal.addressbook.mailingaddress.MailingAddressModelImpl;
import net.rim.device.apps.internal.lbs.EULA;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.LBSOptions;
import net.rim.device.apps.internal.lbs.Location;
import net.rim.device.apps.internal.lbs.finder.FindAddress;
import net.rim.device.apps.internal.lbs.finder.FinderHistory;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class ViewAddressVerb extends AbstractViewVerb implements ConditionalVerb {
   private int _stringID;

   public ViewAddressVerb() {
      this(1131008, 31);
   }

   public ViewAddressVerb(int menuOrdering, int stringID) {
      this(7201896, menuOrdering, stringID);
   }

   public ViewAddressVerb(int verbGroupId, int menuOrdering, int stringID) {
      super(menuOrdering);
      this._stringID = stringID;
      super._verbGroupId = verbGroupId;
   }

   @Override
   public final Object copy() {
      return new ViewAddressVerb(this.getVerbGroupId(), this.getOrdering(), this._stringID);
   }

   @Override
   public final String toString(Object context) {
      if (!(super._address instanceof MailingAddressModelImpl)) {
         return LBSResources.getString(31);
      }

      MailingAddressModelImpl model = (MailingAddressModelImpl)super._address;
      String formatString = LBSResources.getString(32);
      String toFormat = LBSResources.getString(model.getType() == 0 ? 34 : 33);
      return MessageFormat.format(formatString, new String[]{toFormat});
   }

   public static final Object doAction(MailingAddressModel model, Object contextObject) {
      FindAddress finder = new FindAddress(new ViewAddressVerb().toString(), false);
      Location location = (Location)finder.invoke(model);
      if (location != null) {
         FinderHistory.popItems();
         LBSApplication.displayMap(location);
      }

      return null;
   }

   @Override
   public final Object invoke(Object context) {
      if (new EULA().confirmAgreement() && !LBSOptions.getInstance().isDisabled()) {
         ContextObject contextObject = ContextObject.clone(context);
         Object model = ContextObject.get(contextObject, 254);
         Object results = null;
         if (!(model instanceof MailingAddressModel)) {
            if (super._address instanceof MailingAddressModel) {
               results = doAction((MailingAddressModel)super._address, contextObject);
            }
         } else {
            results = doAction((MailingAddressModel)model, contextObject);
         }

         ContextObject returnContext = ContextObject.castOrCreate(super.invoke(contextObject));
         returnContext.setFlag(39);
         if (results != null) {
            returnContext.clearFlag(40);
         } else {
            returnContext.setFlag(40);
         }

         return returnContext;
      } else {
         return null;
      }
   }

   @Override
   public final boolean canInvoke(Object parameter) {
      return true;
   }
}
