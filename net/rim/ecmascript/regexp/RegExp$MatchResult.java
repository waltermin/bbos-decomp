package net.rim.ecmascript.regexp;

import java.io.PrintStream;

public class RegExp$MatchResult {
   public int startIndex;
   public int endIndex;
   public String[] captures;
   public String input;

   public RegExp$MatchResult(String in, int start, int end, String[] cap) {
      this.input = in;
      this.startIndex = start;
      this.endIndex = end;
      this.captures = cap;
   }

   public void dump(PrintStream out) {
      System.out.print("(" + this.startIndex + "," + this.endIndex + ") [");
      int i = 0;

      while (true) {
         System.out.print("\"" + this.captures[i] + "\"");
         if (++i == this.captures.length) {
            System.out.println("]");
            return;
         }

         System.out.print(",");
      }
   }
}
