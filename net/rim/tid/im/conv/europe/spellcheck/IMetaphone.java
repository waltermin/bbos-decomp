package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.SLCurrentVariant;

public interface IMetaphone {
   char CONSONANT_KEY;

   void getMetaphoneKey(SLCurrentVariant var1, SLCurrentVariant var2);

   void getVariants(char[] var1, int var2, SpellCheckResultContainer var3);

   String getMetaVowels();
}
