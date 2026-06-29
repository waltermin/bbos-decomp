package net.rim.device.apps.api.ribbon.indicators;

import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

class VoicemailIconManager$VoicemailIconIndicator implements Indicator, TestPoint {
   private int _index;
   private StringBuffer _workBuffer;
   private final VoicemailIconManager this$0;

   private VoicemailIconManager$VoicemailIconIndicator(VoicemailIconManager _1, int index) {
      this.this$0 = _1;
      this._index = index;
   }

   @Override
   public int draw(Graphics graphics, int width, int height, int flags) {
      int drawWidth = 0;
      int iconWidth = VoicemailIconManager._icons.getWidth(graphics.getFont());
      int count = this.getDisplayCount();
      if (count == -1) {
         int xpos = 0;
         if ((flags & 1) == 0) {
            xpos = width - iconWidth;
         }

         drawWidth = VoicemailIconManager._icons.paint(graphics, xpos, 0, this._index);
      } else {
         if (this._workBuffer == null) {
            this._workBuffer = (StringBuffer)(new Object());
         } else {
            this._workBuffer.setLength(0);
         }

         if ((flags & 4) == 0) {
            this._workBuffer.append(count);
            drawWidth = graphics.getFont().getBounds(this._workBuffer, 0, this._workBuffer.length());
            drawWidth += this.getGap(graphics.getFont());
         }

         drawWidth += iconWidth;
         int xpos = 0;
         if ((flags & 1) != 0) {
            xpos = width - drawWidth;
         }

         if ((flags & 1) != 0) {
            xpos += VoicemailIconManager._icons.paint(graphics, xpos, 0, this._index);
            xpos += this.getGap(graphics.getFont());
         }

         xpos += graphics.drawText(this._workBuffer, 0, this._workBuffer.length(), xpos, 0, 6, width);
         if ((flags & 2) != 0) {
            if ((flags & 4) == 0) {
               xpos += this.getGap(graphics.getFont());
            }

            xpos += VoicemailIconManager._icons.paint(graphics, xpos, 0, this._index);
         }
      }

      return drawWidth;
   }

   private int getGap(Font font) {
      return font.getFontFamily().getName().equals("System") ? 0 : font.getHeight() / 8;
   }

   @Override
   public int getWidth(Graphics graphics) {
      int count = this.getDisplayCount();
      switch (count) {
         case -2:
            if (this._workBuffer == null) {
               this._workBuffer = (StringBuffer)(new Object());
            } else {
               this._workBuffer.setLength(0);
            }

            this._workBuffer.append(count);
            int width = graphics.getFont().getBounds(this._workBuffer, 0, this._workBuffer.length());
            width += this.getGap(graphics.getFont());
            return width + VoicemailIconManager._icons.getWidth(graphics.getFont());
         case -1:
         default:
            return VoicemailIconManager._icons.getWidth(graphics.getFont());
         case 0:
            return 0;
      }
   }

   @Override
   public int getHeight(Graphics graphics) {
      return graphics.getFont().getHeight();
   }

   @Override
   public int getPriority() {
      return 3 + this._index;
   }

   @Override
   public String getTypeName() {
      return this._index > -1 && this._index < SMSPacketHeader.INDICATOR_TYPE_NAMES.length ? SMSPacketHeader.INDICATOR_TYPE_NAMES[this._index] : "";
   }

   private int getDisplayCount() {
      return this._index == 0 ? this.this$0.getVoicemailCount() : this.this$0.getIndicatorCount(this._index);
   }

   @Override
   public void test(Object id, Object testvalue) {
      if (testvalue instanceof Object) {
         Integer integer = (Integer)testvalue;
         this.this$0.setIndicator(this._index, integer);
      }
   }

   VoicemailIconManager$VoicemailIconIndicator(VoicemailIconManager x0, int x1, VoicemailIconManager$1 x2) {
      this(x0, x1);
   }
}
