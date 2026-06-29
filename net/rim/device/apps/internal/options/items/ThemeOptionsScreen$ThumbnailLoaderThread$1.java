package net.rim.device.apps.internal.options.items;

final class ThemeOptionsScreen$ThumbnailLoaderThread$1 implements Runnable {
   private final int val$index;
   private final ThemeOptionsScreen$ThumbnailLoaderThread this$1;

   ThemeOptionsScreen$ThumbnailLoaderThread$1(ThemeOptionsScreen$ThumbnailLoaderThread _1, int _2) {
      this.this$1 = _1;
      this.val$index = _2;
   }

   @Override
   public final void run() {
      if (this.val$index == this.this$1.this$0.fixIndex(this.this$1.this$0._listField.getSelectedIndex())) {
         this.this$1.this$0._previewField.displayBitmap(this.this$1.this$0.getPreviewBitmap(this.val$index));
      }
   }
}
