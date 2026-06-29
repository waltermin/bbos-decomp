package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.lookup.ALPConfiguration;
import net.rim.device.apps.internal.addressbook.lookup.SearchEntryDialog;
import net.rim.tid.awt.im.InputContext;

final class AddressBookListUI$SimpleVerb extends Verb {
   int _type;
   private final AddressBookListUI this$0;
   static final int TYPE_OPTIONS;
   static final int TYPE_LOOKUP;
   static final int TYPE_CANCEL;
   static final int TYPE_SHOW_ALL;

   AddressBookListUI$SimpleVerb(AddressBookListUI _1, int ordering, ResourceBundleFamily rb, int rbKey, int type) {
      super(ordering, rb, rbKey);
      this.this$0 = _1;
      this._type = type;
   }

   final int getType() {
      return this._type;
   }

   @Override
   public final Object invoke(Object parameter) {
      Object result = null;
      switch (this._type) {
         case 1:
         default:
            AddressBookServices.getAddressBookOptions().editOptions();
            return result;
         case 2:
            InputContext.getInstance().endComposition();
            String search_pattern = this.this$0.getSearchPattern();
            if (search_pattern == null || search_pattern.length() == 0) {
               search_pattern = SearchEntryDialog.getSearchPattern();
               if (search_pattern == null) {
                  return null;
               }
            }

            result = ALPConfiguration.getManager()
               .createRequest(search_pattern, AddressBookServices.getAddressBookOptions().getSortOrder(), -8892319056465090102L, null);
            AddressBookServices.getAddressBookOptions().setComposePreference((byte)2);
            return result;
         case 3:
            this.this$0.terminateScreen();
            return result;
         case 4:
            this.this$0.setSearchPattern(null);
         case 0:
            return result;
      }
   }
}
