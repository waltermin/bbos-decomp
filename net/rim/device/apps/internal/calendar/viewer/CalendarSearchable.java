package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.search.SearchResultCollection;
import net.rim.device.apps.api.search.Searchable;

final class CalendarSearchable implements Searchable {
   @Override
   public final long[] getSearchableIds(boolean advancedMode) {
      return null;
   }

   @Override
   public final String getName(long id) {
      return ApplicationEntryPoint.removeHotkeyFromDescription(CalendarApp._rb.getString(0));
   }

   @Override
   public final int getPriority(long id, Object context) {
      return 1;
   }

   @Override
   public final EncodedImage getIcon(long id) {
      EncodedImage tempImage = ThemeManager.getActiveTheme().getImage("net_rim_bb_calendar_app.Calendar", true);
      return tempImage != null ? EncodedImage.createEncodedImage(tempImage.getData(), 0, -1) : null;
   }

   @Override
   public final boolean isInitiallyEnabled(long id) {
      return false;
   }

   @Override
   public final SearchResultCollection search(long[] ids, Object criteria) {
      SearchResultCollection results = new CalendarSearchResultCollection(criteria);
      results.loadFrom(CalendarServiceManager.getInstance().getEventsSortedByStartDate());
      return results;
   }
}
