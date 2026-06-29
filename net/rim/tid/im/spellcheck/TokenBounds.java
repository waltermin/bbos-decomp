package net.rim.tid.im.spellcheck;

public class TokenBounds {
   public int start;
   public int end;
   public int previousStart;
   public int previousEnd;
   public byte type;
   public static final byte CORRECT_WORD;
   public static final byte MISSPELLED_WORD;
   public static final byte REPEATED_WORD;
   public static final byte AMBIGUOUS_WORD;
}
