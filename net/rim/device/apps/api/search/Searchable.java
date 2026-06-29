package net.rim.device.apps.api.search;

import net.rim.device.api.system.EncodedImage;

public interface Searchable {
   long[] getSearchableIds(boolean var1);

   String getName(long var1);

   int getPriority(long var1, Object var3);

   EncodedImage getIcon(long var1);

   boolean isInitiallyEnabled(long var1);

   SearchResultCollection search(long[] var1, Object var2);
}
