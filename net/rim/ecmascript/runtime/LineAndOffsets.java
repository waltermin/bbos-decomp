package net.rim.ecmascript.runtime;

class LineAndOffsets {
   int line;
   int offset;
   int tokenOffset;
   boolean hasBreakpoint;

   void resetLineInfo() {
      this.line = -1;
      this.tokenOffset = 0;
      this.offset = Integer.MAX_VALUE;
      this.hasBreakpoint = false;
   }

   LineAndOffsets() {
      this.resetLineInfo();
   }
}
