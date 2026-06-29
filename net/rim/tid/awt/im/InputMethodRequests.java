package net.rim.tid.awt.im;

import net.rim.device.api.ui.XYRect;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.AttributedTextIterator;
import net.rim.tid.text.TextHitInfo;

public interface InputMethodRequests {
   int getInsertPositionOffset();

   int getCommittedTextLength();

   AttributedTextIterator cancelLatestCommittedText();

   AttributedTextIterator getSelectedText();

   int getSelectionOffset();

   int getSelectionStart();

   int getSelectionEnd();

   AttributedTextIterator getText(int var1, int var2, boolean var3);

   AttributedTextIterator getCommittedText(int var1, int var2, String[] var3);

   TextHitInfo getLocationOffset(int var1, int var2);

   void setComposedText(int var1, int var2);

   AttributedString getAttributedText();

   int getComposedTextStart();

   int getComposedTextEnd();

   int getLabelLength();

   int getCaretPosition();

   int getAnchorPosition();

   int getLatestCommittedTextStart();

   int getLatestCommittedTextEnd();

   void getTextLocation(TextHitInfo var1, XYRect var2);

   Object getIMCookieCache();
}
