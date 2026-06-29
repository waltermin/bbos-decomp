package net.rim.device.api.ui;

import net.rim.tid.awt.event.InputMethodEvent;

public interface TextChangeListener {
   int UNSPECIFIED_TEXT_CHANGE;

   void textValueChanged(Object var1, int var2);

   void inputMethodTextChanged(Object var1, InputMethodEvent var2);
}
