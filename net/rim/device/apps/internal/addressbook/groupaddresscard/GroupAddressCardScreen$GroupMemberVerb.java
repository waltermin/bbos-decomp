package net.rim.device.apps.internal.addressbook.groupaddresscard;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class GroupAddressCardScreen$GroupMemberVerb extends Verb {
   private int _type;
   private final GroupAddressCardScreen this$0;

   public GroupAddressCardScreen$GroupMemberVerb(GroupAddressCardScreen _1, int type, int menuOrdering, int resourceId) {
      super(menuOrdering, AddressBookResources.getResourceBundleFamily(), resourceId);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public final Object invoke(Object context) {
      switch (this._type) {
         case -1:
            return null;
         case 0:
         default:
            int selectedIndex = this.this$0._listField.getSelectedIndex();
            VerbProvider acm = (VerbProvider)this.this$0.getAddressCardAt(selectedIndex);
            ContextObject contextObject = new ContextObject(18);
            contextObject.setFlag(45);
            Verb[] verbArray = new Verb[0];
            Verb viewACM = acm.getVerbs(contextObject, verbArray);
            viewACM.invoke(context);
            return acm;
         case 1:
            this.this$0._showNames = !this.this$0._showNames;
            super._rbKey = this.this$0._showNames ? 1008 : 1009;
            super._cachedLocaleCode = -1;
            this.this$0._listField.invalidate();
            return null;
      }
   }
}
