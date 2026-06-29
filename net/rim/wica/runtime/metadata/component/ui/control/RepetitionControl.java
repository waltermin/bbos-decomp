package net.rim.wica.runtime.metadata.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.UIControl;

public interface RepetitionControl extends UIControl, UIContainer, Selectable {
   int getRepetitionCount();

   boolean isCollapsible();

   int getVisibleRows();
}
