package net.rim.device.api.servicebook.selector;

import net.rim.device.api.servicebook.ServiceBook;

public interface SRSelectorCallback {
   int chooseNewDefault(ServiceBook var1, String var2, int var3, boolean var4);

   void defaultChanged(int var1);
}
