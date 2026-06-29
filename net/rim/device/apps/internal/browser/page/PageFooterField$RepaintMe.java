package net.rim.device.apps.internal.browser.page;

final class PageFooterField$RepaintMe implements Runnable {
   private final PageFooterField this$0;

   PageFooterField$RepaintMe(PageFooterField _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._repaintRunnableId = -1;
      PageFooterField.access$100(this.this$0);
   }
}
