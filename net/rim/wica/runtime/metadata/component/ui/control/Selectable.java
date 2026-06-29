package net.rim.wica.runtime.metadata.component.ui.control;

public interface Selectable {
   boolean isMultiSelect();

   void setSelectedIndex(int var1, boolean var2);

   int getSelectedIndex();

   boolean isSelected(int var1);

   void addSelectedIndex(int var1, boolean var2);

   void removeSelectedIndex(int var1, boolean var2);

   int[] getSelectedIndices();

   void setSelectedIndices(int[] var1, boolean var2);

   Object getSelectedData();

   long getSelectedDataType();
}
