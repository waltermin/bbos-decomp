package net.rim.wica.runtime.metadata.internal;

import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.internal.def.Definitions;
import net.rim.wica.runtime.metadata.util.ValueResolver;
import net.rim.wica.runtime.util.LongVector;

public interface WicletEx extends Wiclet {
   @Override
   WicletRuntime getRuntime();

   void clear();

   void clear(int var1);

   void save();

   Object getValue(long var1, int[] var3, int var4, int var5);

   int setValue(long var1, int[] var3, int var4, int var5, Object var6);

   long getRef(long var1, int[] var3, int var4, int var5);

   void findWhere(LongVector var1, DataCollection var2, long[] var3, String var4, ValueResolver var5);

   Definitions getDefinitions();

   boolean isBackground();
}
