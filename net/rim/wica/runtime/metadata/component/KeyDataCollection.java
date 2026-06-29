package net.rim.wica.runtime.metadata.component;

import net.rim.wica.common.metadata.component.DataComponentDef;
import net.rim.wica.runtime.metadata.util.ValueResolver;

public interface KeyDataCollection extends DataCollection {
   long create(Object var1);

   long find(Object var1);

   long[] findWhere(String var1, ValueResolver var2);

   long[] retrieveAll(boolean var1);

   void removeAll();

   int getCount();

   Object getPKey(long var1);

   void restoreKey(long var1, Object var3);

   DataComponentDef getKeyDef();
}
