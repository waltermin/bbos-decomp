package net.rim.wica.runtime.metadata.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.DateFormatter;
import net.rim.wica.runtime.metadata.component.ui.UIControl;

public interface ChoiceControl extends UIControl, Selectable, DateFormatter {
   int CT_SINGLE_LIST = 0;
   int CT_MULTI_LIST = 1;
   int CT_DROPDOWN = 2;
   int CT_RADIO = 3;
   int CT_CHECKBOX = 4;

   String getFormat();

   int getChoiceType();

   int getVisibleRows();

   boolean getBorder();
}
