package net.rim.tid.im.conv.europe.repository;

public interface RegularExpression {
   RegularExpression$EmptyCharacterIterator EMPTY_ITER;

   boolean accept(RegularExpressionState var1, char var2);

   boolean accept(RegularExpressionState var1, String var2);

   boolean accept(RegularExpressionState var1, char[] var2);

   boolean acceptsLength(RegularExpressionState var1, int var2, boolean var3, boolean var4);

   RegularExpression$SimpleCharacterIterator getAcceptableChars(RegularExpressionState var1, String var2);

   RegularExpression$SimpleCharacterIterator getAcceptableChars(RegularExpressionState var1, LearningGlobalAlphabet var2);
}
