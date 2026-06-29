package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$TextControlInfo$AppendTextCallbackAction$1 implements Runnable {
   private final DocViewTextDisplayField$TextControlInfo$AppendTextCallbackAction this$2;

   DocViewTextDisplayField$TextControlInfo$AppendTextCallbackAction$1(DocViewTextDisplayField$TextControlInfo$AppendTextCallbackAction _1) {
      this.this$2 = _1;
   }

   @Override
   public void run() {
      this.this$2
         .this$1
         ._richTextField
         .appendCustomText(
            this.this$2._text,
            this.this$2.this$1._appendOffsets,
            this.this$2.this$1._appendAttributes,
            this.this$2.this$1._appendFonts,
            this.this$2._cookies,
            this.this$2.this$1._foreColors,
            this.this$2.this$1._bgColors
         );
      this.this$2.this$1._appendOffsets = null;
      this.this$2.this$1._appendAttributes = null;
      this.this$2.this$1._appendFonts = null;
   }
}
