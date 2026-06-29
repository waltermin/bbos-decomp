package net.rim.tools.compiler.util;

public final class Tokenizer {
   private String _str;
   private int _count;
   private int _used;
   private int[] _starts;
   private int[] _ends;

   public Tokenizer(String str) {
      this(str, " ", false);
   }

   public Tokenizer(String str, String delim) {
      this(str, delim, false);
   }

   public Tokenizer(String str, String delim, boolean includeDelimiters) {
      this._str = str;
      int num = str.length();
      this._starts = new int[num * 2 + 1];
      this._ends = new int[num * 2 + 1];
      this._starts[this._count] = 0;
      char d = delim.charAt(0);
      int index = 0;

      while (index != -1) {
         index = str.indexOf(d, index);
         if (index != -1) {
            this._ends[this._count++] = index;
            if (includeDelimiters) {
               this._starts[this._count] = index++;
               this._ends[this._count++] = index;
            } else {
               index++;
            }

            this._starts[this._count] = index;
         }
      }

      this._ends[this._count++] = num;
   }

   public final int countTokens() {
      return this._count;
   }

   public final String nextToken() {
      if (!this.hasMoreTokens()) {
         return null;
      }

      int start = this._starts[this._used];
      int end = this._ends[this._used];
      this._used++;

      while (start < end && this._str.charAt(start) == ' ') {
         start++;
      }

      while (start < end && this._str.charAt(end - 1) == ' ') {
         end--;
      }

      return start >= end ? "" : this._str.substring(start, end);
   }

   public final boolean hasMoreTokens() {
      boolean moreTokens = this._used < this.countTokens();
      if (!moreTokens) {
         this._starts = null;
         this._ends = null;
      }

      return moreTokens;
   }
}
