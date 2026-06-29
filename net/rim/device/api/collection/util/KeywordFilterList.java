package net.rim.device.api.collection.util;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.FilterStatusListener;
import net.rim.device.api.collection.ReadableList;

public interface KeywordFilterList extends ReadableList, CollectionEventSource {
   void setCriteria(Object var1, FilterStatusListener var2);

   Object getCriteria();

   KeywordSearcher getSearcher();

   void setSuffix(String var1);

   String getSuffix();

   void waitForComplete();

   boolean matches(Object var1);

   void searchPrefixes(String[] var1);

   void reset(Collection var1);
}
