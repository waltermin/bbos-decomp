package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.search.SearchCriterion;

public interface MessageSearchableSubItem {
   long[] getSubItemIds(boolean var1);

   String getName(long var1);

   void modifySearchCriteria(long var1, SearchCriterion[] var3);
}
