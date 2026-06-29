package net.rim.device.apps.internal.addressbook;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.search.SearchResultCollection;
import net.rim.device.apps.api.search.Searchable;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class AddressBookSearchable implements Searchable {
   @Override
   public final long[] getSearchableIds(boolean advancedMode) {
      return null;
   }

   @Override
   public final String getName(long id) {
      return ApplicationEntryPoint.removeHotkeyFromDescription(AddressBookResources.getString(0));
   }

   @Override
   public final int getPriority(long id, Object context) {
      return 2;
   }

   @Override
   public final EncodedImage getIcon(long id) {
      EncodedImage tempImage = ThemeManager.getActiveTheme().getImage("net_rim_bb_addressbook_app.AddressBook", true);
      return tempImage != null ? EncodedImage.createEncodedImage(tempImage.getData(), 0, -1) : null;
   }

   @Override
   public final boolean isInitiallyEnabled(long id) {
      return false;
   }

   @Override
   public final SearchResultCollection search(long[] ids, Object criteria) {
      SearchResultCollection results = new AddressBookSearchResultCollection(criteria);
      results.loadFrom(BlackBerryAddressBook.getAddressBook().getCollection());
      return results;
   }
}
