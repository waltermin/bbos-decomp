package net.rim.device.apps.internal.docview.gui;

class ParsingProgressThread$1 extends Thread {
   private final ParsingProgressThread this$0;

   ParsingProgressThread$1(ParsingProgressThread _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._coreData.getParsingData().setStopFlag((byte)2);
   }
}
