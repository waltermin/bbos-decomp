package net.rim.device.internal.ui.autotext;

final class SentenceTerminator {
   private static String SENTENCE_TERMINATORS = ".\n!?";

   static final boolean isSentenceTerminator(char ch) {
      return SENTENCE_TERMINATORS.indexOf(ch) >= 0;
   }
}
