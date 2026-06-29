package net.rim.wica.runtime.metadata.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.Command;
import net.rim.wica.runtime.metadata.component.ui.UIControl;

public interface ButtonControl extends UIControl, Command {
   int getResourceId();

   Object getImageValue();
}
