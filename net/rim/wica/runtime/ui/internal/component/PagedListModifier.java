package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;

public interface PagedListModifier {
   void deleteRange(int var1, int var2);

   int getFieldCount();

   void add(Field var1);

   void add(int var1, int var2);

   void update(int var1, int var2);

   void setSelected(int var1);

   void beginReconstruction();

   void endReconstruction();

   void unFocus();
}
