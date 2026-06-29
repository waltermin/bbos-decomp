package net.rim.tid.awt.event;

public interface InputMethodListener {
   byte IM_TEXT_CHANGED_OK;
   byte IM_TEXT_CHANGED_REFUSED;
   byte CARET_POSITION_CHANGED_OK;
   byte CARET_POSITION_CHANGED_REFUSED;

   int inputMethodTextChanged(InputMethodEvent var1);

   int caretPositionChanged(InputMethodEvent var1);
}
