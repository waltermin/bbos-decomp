package net.rim.device.internal.ui.autotext;

final class ClauseSeparator {
   private static String CLAUSE_SEPARATORS = " \n.?!,:;\"“”@'\\(){}[]<>";

   static final boolean isClauseSeparator(char ch) {
      return CLAUSE_SEPARATORS.indexOf(ch) >= 0;
   }

   static final String getClauseSeparatorString() {
      return CLAUSE_SEPARATORS;
   }
}
