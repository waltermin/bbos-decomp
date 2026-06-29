package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.ui.Field;

public interface ExploreCallback {
   void pathSet(Object var1);

   void statusOn();

   void statusOff();

   void currentItemChanged(Field var1, FileItemField var2);
}
