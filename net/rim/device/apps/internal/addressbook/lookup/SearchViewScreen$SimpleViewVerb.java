package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.AddressBookUtilities;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class SearchViewScreen$SimpleViewVerb extends Verb {
   private int _resId;
   private final SearchViewScreen this$0;

   private SearchViewScreen$SimpleViewVerb(SearchViewScreen _1, int ordering, int resId) {
      super(ordering);
      this.this$0 = _1;
      this._resId = resId;
   }

   @Override
   public final String toString() {
      return AddressBookResources.getString(this._resId);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void AddAllAddresses() {
      boolean ignore_duplicates = false;
      boolean replace_duplicates = false;
      int curr_index = 0;
      int listSize = this.this$0._list.getSize();

      while (curr_index < listSize) {
         RIMModel curr_element = this.this$0._request.getAddress(curr_index);

         try {
            try {
               AddressBookServices.getAddressBook().addAddressCard(curr_element);
            } catch (Throwable var20) {
               Object[] duplicates = e.GetDuplicates();
               if (replace_duplicates) {
                  AddressBookServices.getAddressBook().updateAddressCard(duplicates[0], curr_element);
               } else if (!ignore_duplicates) {
                  int prompt_resId = 1711;
                  String prompt_resStr = AddressBookResources.getString(prompt_resId);
                  String[] prompt_parms = new String[]{duplicates[0].toString()};
                  String prompt = MessageFormat.format(prompt_resStr, prompt_parms);
                  String[] choices = new String[]{
                     AddressBookResources.getString(1712),
                     AddressBookResources.getString(1713),
                     AddressBookResources.getString(1714),
                     AddressBookResources.getString(1715)
                  };
                  int[] values = new int[]{
                     1712,
                     1713,
                     1714,
                     1715,
                     1834680576,
                     1970512004,
                     16807020,
                     1701539702,
                     1870004480,
                     1665230187,
                     16784500,
                     1701539702,
                     1634100548,
                     1299475573,
                     1950971155,
                     1979777083
                  };
                  Dialog dlg = new Dialog(prompt, choices, values, 1712, Bitmap.getPredefinedBitmap(1));
                  dlg.setEscapeEnabled(true);
                  dlg.doModal();
                  switch (dlg.getSelectedValue()) {
                     case 1712:
                        break;
                     case 1713:
                     default:
                        ignore_duplicates = true;
                        break;
                     case 1714:
                        AddressBookServices.getAddressBook().forceUpdateAddressCard(duplicates[0], curr_element);
                        break;
                     case 1715:
                        AddressBookServices.getAddressBook().forceUpdateAddressCard(duplicates[0], curr_element);
                        replace_duplicates = true;
                  }
               }
               continue;
            }
         } finally {
            curr_index++;
         }
      }
   }

   @Override
   public final Object invoke(Object parameter) {
      Object selectedElement = SearchViewScreen.access$500(this.this$0);
      boolean terminateScreen = false;
      switch (this._resId) {
         case 204:
            String newSearchPattern = this.this$0.getSearchPattern();
            if (newSearchPattern == null || newSearchPattern.length() == 0) {
               newSearchPattern = SearchEntryDialog.getSearchPattern(this.this$0._request.getSearchString());
            }

            if (newSearchPattern != null) {
               this.this$0._manager.updateSearchPattern(this.this$0._request, newSearchPattern);
               terminateScreen = true;
            }
            break;
         case 1705:
            if (AddressBookUtilities.confirmDelete(selectedElement)) {
               this.this$0._manager.deleteRequestItem(this.this$0._request, selectedElement);
            }
            break;
         case 1706:
            Request.addLookupResultToAddressBook(selectedElement);
            if (this.this$0._request.getIncludedMatches() == 0) {
               this.this$0._manager.deleteRequest(this.this$0._request);
               terminateScreen = true;
            }
            break;
         case 1707:
            this.AddAllAddresses();
            if (this.this$0._request.getIncludedMatches() == 0) {
               this.this$0._manager.deleteRequest(this.this$0._request);
               terminateScreen = true;
            }
            break;
         case 1708:
            this.this$0._manager.resolveRequestItem(this.this$0._request, selectedElement);
            terminateScreen = true;
            break;
         case 1721:
            this.this$0._manager.moreRequest(this.this$0._request);
      }

      if (terminateScreen) {
         this.this$0.terminateScreen();
      }

      return null;
   }

   SearchViewScreen$SimpleViewVerb(SearchViewScreen x0, int x1, int x2, SearchViewScreen$1 x3) {
      this(x0, x1, x2);
   }
}
