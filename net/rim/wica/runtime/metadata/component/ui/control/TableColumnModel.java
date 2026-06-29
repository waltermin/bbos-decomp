package net.rim.wica.runtime.metadata.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.UIControl;

public interface TableColumnModel extends UIControl, TableStyling {
   String getCell(int var1);

   @Override
   int getEvenRowStyle();

   String getFormat();

   @Override
   int getHeaderStyle();

   @Override
   int getOddRowStyle();

   String getTitle();

   void setTitle(String var1);

   @Override
   int getType();

   double getWidthPercentage();

   boolean isAutoSized();

   boolean isFrozen();

   void setFrozen(boolean var1, boolean var2);
}
