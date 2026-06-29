package net.rim.device.api.ui;

public interface FocusChangeListener {
   int FOCUS_GAINED = 1;
   int FOCUS_CHANGED = 2;
   int FOCUS_LOST = 3;

   void focusChanged(Field var1, int var2);
}
