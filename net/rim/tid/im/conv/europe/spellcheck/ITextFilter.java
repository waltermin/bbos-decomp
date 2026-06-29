package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.device.internal.ui.StringBufferGap;

public interface ITextFilter {
   int filter(StringBufferGap var1, int var2, int var3, boolean var4);

   void reset();
}
