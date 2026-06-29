package net.rim.device.apps.internal.memo;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.search.SearchResultCollection;
import net.rim.device.apps.api.search.Searchable;
import net.rim.device.apps.internal.memo.resources.MemoResources;

final class MemoSearchable implements Searchable {
   @Override
   public final long[] getSearchableIds(boolean advancedMode) {
      return null;
   }

   @Override
   public final String getName(long id) {
      return ApplicationEntryPoint.removeHotkeyFromDescription(MemoResources.getString(0));
   }

   @Override
   public final int getPriority(long id, Object context) {
      return 3;
   }

   @Override
   public final EncodedImage getIcon(long id) {
      EncodedImage tempImage = ThemeManager.getActiveTheme().getImage("net_rim_bb_memo_app", true);
      return tempImage != null ? EncodedImage.createEncodedImage(tempImage.getData(), 0, -1) : null;
   }

   @Override
   public final boolean isInitiallyEnabled(long id) {
      return false;
   }

   @Override
   public final SearchResultCollection search(long[] ids, Object criteria) {
      SearchResultCollection results = new MemoSearchResultCollection(criteria);
      results.loadFrom(MemoCollectionImpl.getInstance());
      return results;
   }
}
