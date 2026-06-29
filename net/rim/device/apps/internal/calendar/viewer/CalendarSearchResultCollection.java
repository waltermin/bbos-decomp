package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.collection.LongKeyProviderAdaptorComparator;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.calendar.caldb.CalendarLongKeyAdaptor;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.search.SearchResultCollection;

final class CalendarSearchResultCollection extends SearchResultCollection implements FieldProvider {
   public CalendarSearchResultCollection(Object criteria) {
      super((SearchCriterion[])criteria, new LongKeyProviderAdaptorComparator(new CalendarLongKeyAdaptor(-7347526267900023482L)), true, false);
   }

   @Override
   public final Field getField(Object context) {
      return new CalendarSearchResultField(this).getField();
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      return 0;
   }
}
