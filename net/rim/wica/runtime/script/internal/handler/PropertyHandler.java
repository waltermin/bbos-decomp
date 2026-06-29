package net.rim.wica.runtime.script.internal.handler;

import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;

public interface PropertyHandler {
   long getProperty(Component var1, String var2);

   long getProperty(DataCollection var1, long var2, String var4);

   void putProperty(Component var1, String var2, long var3);

   void putProperty(DataCollection var1, long var2, String var4, long var5);
}
