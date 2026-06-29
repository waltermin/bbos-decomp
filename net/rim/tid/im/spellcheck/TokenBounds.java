package net.rim.tid.im.spellcheck;

public class TokenBounds {
   public int start;
   public int end;
   public int previousStart;
   public int previousEnd;
   public byte type;
   public static final byte CORRECT_WORD = 0;
   public static final byte MISSPELLED_WORD = 1;
   public static final byte REPEATED_WORD = 2;
   public static final byte AMBIGUOUS_WORD = 3;
}
