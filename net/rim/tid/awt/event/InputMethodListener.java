package net.rim.tid.awt.event;

public interface InputMethodListener {
   byte IM_TEXT_CHANGED_OK = 0;
   byte IM_TEXT_CHANGED_REFUSED = 1;
   byte CARET_POSITION_CHANGED_OK = 0;
   byte CARET_POSITION_CHANGED_REFUSED = 1;

   int inputMethodTextChanged(InputMethodEvent var1);

   int caretPositionChanged(InputMethodEvent var1);
}
