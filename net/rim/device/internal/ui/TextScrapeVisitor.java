package net.rim.device.internal.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.FieldVisitor;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.IntStack;

public class TextScrapeVisitor implements FieldVisitor {
   private StringBuffer _buffer = new StringBuffer();
   private boolean _addLineFeed = true;
   private IntStack stack = new IntStack();

   public StringBuffer getStringBuffer() {
      return this._buffer;
   }

   @Override
   public boolean visit(Field field, int state) {
      switch (state) {
         case 0:
            return false;
         case 1:
         default:
            this.stack.push(this._addLineFeed ? 1 : 0);
            this._addLineFeed = field instanceof VerticalFieldManager;
            return true;
         case 2:
            boolean wasAddingLineFeeds = this._addLineFeed;
            this._addLineFeed = this.stack.pop() != 0;
            if (!wasAddingLineFeeds && this._addLineFeed) {
               this._buffer.append('\n');
               return true;
            }
            break;
         case 3:
            if (field instanceof FieldLabelProvider) {
               String label = ((FieldLabelProvider)field).getLabel();
               if (label != null && label.length() > 0) {
                  this._buffer.append(label);
                  this._buffer.append(' ');
               }
            }

            this._buffer.append(field.toString());
            if (this._addLineFeed) {
               this._buffer.append('\n');
               return true;
            }
      }

      return true;
   }
}
