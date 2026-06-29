package net.rim.wica.runtime.metadata.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.UIControl;

public interface TableModel extends Selectable, TableStyling, UIControl, UIContainer {
   @Override
   int getEvenRowStyle();

   @Override
   int getHeaderStyle();

   @Override
   int getOddRowStyle();

   int getRowCount();

   int getSelectedColumnIndex();

   int getSelectedRowIndex();

   int getVisibleRows();

   String getGridlineColor();

   boolean isShowGridline();

   boolean isShowHeader();

   boolean isShowRowSelector();

   void setSelectedColumnIndex(int var1, boolean var2);

   void setSelectedRowIndex(int var1, boolean var2);

   void setFocus(boolean var1);

   UIComponent getComponent(String var1);
}
