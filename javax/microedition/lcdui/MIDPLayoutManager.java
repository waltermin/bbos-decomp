package javax.microedition.lcdui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.FlowFieldManager;

class MIDPLayoutManager extends FlowFieldManager {
   private static final int MAX_HEIGHT;

   public MIDPLayoutManager() {
      super(1153220571769602048L);
   }

   private int layoutChildren(int width, int maxHeight, int heightUsed) {
      int numFields = this.getFieldCount();
      int widthUsed = 0;
      int maxHeightOfRow = 0;
      int startField = 0;
      boolean forceNextItemToNewLine = false;

      for (int i = startField; i < numFields; i++) {
         Field field = this.getField(i);
         Item peerItem = (Item)field.getCookie();
         int peerItemLayoutStyle = peerItem.getLayout();
         int prefWidth = peerItem.getPreferredWidth();
         boolean tooWideForCurrentRow = prefWidth > width - widthUsed && widthUsed != 0;
         if (tooWideForCurrentRow && (peerItemLayoutStyle & 1024) != 0) {
            int minWidth = peerItem.getMinimumWidth();
            if (minWidth <= width - widthUsed) {
               tooWideForCurrentRow = false;
            }
         }

         if (!tooWideForCurrentRow && (peerItem.getLayout() & 256) == 0 && !forceNextItemToNewLine) {
            this.layoutChild(field, width - widthUsed, maxHeight - heightUsed);
            prefWidth = field.getWidth();
            this.setPositionChild(field, widthUsed, heightUsed);
            widthUsed += prefWidth;
            int temp = field.getHeight();
            if (maxHeightOfRow < temp) {
               maxHeightOfRow = temp;
            }

            if (widthUsed >= width) {
               widthUsed = 0;
               heightUsed += maxHeightOfRow;
               maxHeightOfRow = 0;
            }
         } else {
            int var16 = 0;
            heightUsed += maxHeightOfRow;
            this.layoutChild(field, width - var16, maxHeight - heightUsed);
            prefWidth = field.getWidth();
            this.setPositionChild(field, var16, heightUsed);
            maxHeightOfRow = field.getHeight();
            widthUsed = var16 + prefWidth;
            if (widthUsed >= width) {
               widthUsed = 0;
               heightUsed += maxHeightOfRow;
               maxHeightOfRow = 0;
            }
         }

         forceNextItemToNewLine = (peerItem.getLayout() & 512) != 0;
      }

      if ((this.getStyle() & 12884901888L) != 0) {
         int dx = width - widthUsed >> 1;

         for (int i2 = startField; i2 < numFields; i2++) {
            Field f = this.getField(i2);
            this.setPositionChild(f, f.getLeft() + dx, f.getTop());
         }
      }

      return heightUsed + maxHeightOfRow;
   }

   @Override
   protected void sublayout(int width, int height) {
      int heightAvail = height;
      if ((this.getStyle() & 281474976710656L) > 0) {
         heightAvail = 1073741823;
      }

      int virtualHeight = this.layoutChildren(width, heightAvail, 0);
      if ((this.getStyle() & 2305843009213693952L) > 0) {
         this.setExtent(width, height);
      } else {
         this.setExtent(width, Math.min(height, virtualHeight));
      }

      this.setVirtualExtent(width, virtualHeight);
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      Field fieldWithFocus = this.getFieldWithFocus();
      return fieldWithFocus != null && fieldWithFocus.getCookie() instanceof CustomItem ? false : super.navigationMovement(dx, dy, status, time);
   }
}
