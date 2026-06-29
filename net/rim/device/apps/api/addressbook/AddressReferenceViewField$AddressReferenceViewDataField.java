package net.rim.device.apps.api.addressbook;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;

public final class AddressReferenceViewField$AddressReferenceViewDataField extends Field {
   private final AddressReferenceViewField this$0;

   AddressReferenceViewField$AddressReferenceViewDataField(AddressReferenceViewField _1) {
      super(1170935903116328960L);
      this.this$0 = _1;
   }

   @Override
   protected final void layout(int width, int height) {
      Font font = this.getFont();
      height = font.getHeight();
      if (this.this$0._friendlyName != null && this.this$0._friendlyName.length() > 0) {
         TextMetrics tmpTextMetrics = Ui.getTmpTextMetrics();
         this.this$0._textInfo = font.measureText(this.this$0._friendlyName, 0, this.this$0._friendlyName.length(), null, tmpTextMetrics);
         int baseline = font.getBaseline();
         int above = Math.max(-tmpTextMetrics.iBoundsTlY, baseline);
         int below = Math.max(tmpTextMetrics.iBoundsBrY, font.getDescent());
         height = Math.max(height, above + below);
         this.this$0._textInfo = above - baseline << 8 | height;
         Ui.returnTmpTextMetrics(tmpTextMetrics);
      } else {
         this.this$0._textInfo = 0;
      }

      this.setExtent(width, height);
   }

   private final int getAddressOffset(String address) {
      Font font = this.getFont();
      int xoffset = font.getBounds(this.this$0._label);
      if (this.this$0._addressTrustIndicatorBitmap != null) {
         xoffset += this.this$0._addressTrustIndicatorBitmap.getWidth() + 1;
      }

      if (this.this$0._rightJustified) {
         int length = this.getFont().getBounds(address);
         int newOffset = this.getWidth() - length;
         if (newOffset > xoffset) {
            xoffset = newOffset;
         }
      }

      return xoffset;
   }

   @Override
   protected final void paint(Graphics graphics) {
      String address = this.this$0._qualifiedVisible ? this.this$0._qualifiedAddress : this.this$0._friendlyName;
      int xOffset = this.getAddressOffset(address);
      int yOffset = this.this$0._qualifiedVisible ? 0 : this.this$0._textInfo >> 8;
      int iconOffset = graphics.drawText(this.this$0._label, 0, yOffset);
      if (this.this$0._addressTrustIndicatorBitmap != null) {
         graphics.drawBitmap(
            iconOffset,
            yOffset,
            this.this$0._addressTrustIndicatorBitmap.getWidth(),
            this.this$0._addressTrustIndicatorBitmap.getHeight(),
            this.this$0._addressTrustIndicatorBitmap,
            0,
            0
         );
      }

      graphics.drawText(address, xOffset, yOffset, 64, this.getWidth() - xOffset);
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      Font font = this.getFont();
      String address = this.this$0._qualifiedVisible ? this.this$0._qualifiedAddress : this.this$0._friendlyName;
      int length = font.getBounds(address);
      rect.set(this.getAddressOffset(address), 0, length, this.this$0._textInfo & 0xFF);
   }

   public final AddressReferenceViewField getAddressReferenceViewField() {
      return this.this$0;
   }

   @Override
   public final boolean isSelectionCopyable() {
      return this.getAddressReferenceViewField().isSelectionCopyable();
   }

   @Override
   public final void selectionCopy(Clipboard cb) {
      this.getAddressReferenceViewField().selectionCopy(cb);
   }
}
