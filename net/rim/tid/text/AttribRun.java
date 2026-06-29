package net.rim.tid.text;

public final class AttribRun {
   public int iStart;
   public int iLength;
   public int iAttrib;

   public AttribRun() {
   }

   public AttribRun(int aStart, int aLength, int aAttrib) {
      this.iStart = aStart;
      this.iLength = aLength;
      this.iAttrib = aAttrib;
   }

   public final void init(AttribRun aRun) {
      this.iStart = aRun.iStart;
      this.iLength = aRun.iLength;
      this.iAttrib = aRun.iAttrib;
   }
}
