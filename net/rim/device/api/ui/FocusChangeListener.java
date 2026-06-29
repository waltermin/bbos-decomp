package net.rim.device.api.ui;

public interface FocusChangeListener {
   int FOCUS_GAINED;
   int FOCUS_CHANGED;
   int FOCUS_LOST;

   void focusChanged(Field var1, int var2);
}
