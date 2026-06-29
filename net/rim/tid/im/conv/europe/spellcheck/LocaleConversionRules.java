package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.SLCurrentVariant;

public interface LocaleConversionRules {
   int wordExists(char[] var1, int var2, int var3, char[] var4);

   boolean exists(Word var1);

   void getVariants(char[] var1, int var2, SpellCheckResultContainer var3);

   void getVariants(Word var1, SpellCheckResultContainer var2);

   boolean modifyInserted(SLCurrentVariant var1);
}
