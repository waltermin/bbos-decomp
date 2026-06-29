package net.rim.wica.runtime.metadata.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.DateFormatter;
import net.rim.wica.runtime.metadata.component.ui.UIControl;

public interface ChoiceControl extends UIControl, Selectable, DateFormatter {
   int CT_SINGLE_LIST;
   int CT_MULTI_LIST;
   int CT_DROPDOWN;
   int CT_RADIO;
   int CT_CHECKBOX;

   String getFormat();

   int getChoiceType();

   int getVisibleRows();

   boolean getBorder();
}
