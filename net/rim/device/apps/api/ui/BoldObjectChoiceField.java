package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.internal.i18n.CommonResource;

public class BoldObjectChoiceField extends ObjectChoiceField {
   private boolean[] _boldChoices;
   private int _selectedX;
   private int _selectedWidth;
   private Font _font;
   private Font _boldFont;

   public BoldObjectChoiceField() {
   }

   public BoldObjectChoiceField(String label, Object[] choices, boolean[] boldChoices, int initialIndex, long style) {
      super(label, choices, initialIndex, style);
      this._boldChoices = boldChoices;
   }

   @Override
   public void getFocusRect(XYRect rect) {
      int x = this._selectedX;
      int fontHeight = this.getFont().getHeight();
      rect.set(x, this.getHeight() - fontHeight, this._selectedWidth + 1, fontHeight);
   }

   @Override
   public int getWidthOfChoices() {
      this.prepareFonts(this.getFont());
      int width = 0;
      int numChoices = this.getSize();

      for (int lv = numChoices - 1; lv >= 0; lv--) {
         int w = this.getWidthOfChoice(lv);
         if (width < w) {
            width = w;
         }
      }

      return width;
   }

   public void setChoices(Object[] choices, boolean[] boldChoices) {
      this._boldChoices = boldChoices;
      this.setChoices(choices);
   }

   public boolean isBold(int index) {
      return this._boldChoices[index];
   }

   @Override
   protected void paint(Graphics graphics) {
      this.prepareFonts(this.getFont());
      int fontHeight = this._font.getHeight();
      String selectedChoice;
      boolean isSelectedChoiceBold;
      if (this.getSize() == 0) {
         selectedChoice = CommonResource.getString(1012);
         this._selectedWidth = this._font.getBounds(selectedChoice);
         isSelectedChoiceBold = false;
      } else {
         int selectedIndex = this.getSelectedIndex();
         selectedChoice = this.getChoice(selectedIndex).toString();
         this._selectedWidth = this.getWidthOfChoice(selectedIndex);
         isSelectedChoiceBold = this.isBold(selectedIndex);
      }

      int flags = 64;
      String label = this.getLabel();
      switch ((int)((this.getStyle() & 12884901888L) >>> 32)) {
         case -1:
            break;
         case 0:
            flags |= 5;
            this._selectedX = this.getWidth() - this._selectedWidth - 1;
            break;
         case 1:
         default:
            flags |= 6;
            this._selectedX = this._font.getBounds(label);
            break;
         case 2:
            flags |= 5;
            this._selectedX = this.getWidth() - this._selectedWidth - 1;
            break;
         case 3:
            flags |= 4;
            this._selectedX = this.getWidth() - this._selectedWidth - 1;
      }

      graphics.drawText(label, 0, 0, 64, this.getWidth());
      if (isSelectedChoiceBold) {
         Font oldFont = graphics.getFont();
         graphics.setFont(this._boldFont);
         graphics.drawText(selectedChoice, this._selectedX + 1, this.getHeight() - fontHeight, flags, this.getWidth() - this._selectedX - 1);
         graphics.setFont(oldFont);
      } else {
         graphics.drawText(selectedChoice, this._selectedX + 1, this.getHeight() - fontHeight, flags, this.getWidth() - this._selectedX - 1);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void drawChoice(int index, Graphics graphics, int x, int y, int flags, int width) {
      this.prepareFonts(this.getFont());
      if (this.isBold(index)) {
         graphics.setFont(this._boldFont);
      }

      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         super.drawChoice(index, graphics, x, y, flags, width);
         var9 = false;
      } finally {
         if (var9) {
            graphics.setFont(this._font);
         }
      }

      graphics.setFont(this._font);
   }

   @Override
   protected int getWidthOfChoice(int index) {
      this.prepareFonts(this.getFont());
      return this.isBold(index) ? this._boldFont.getBounds(this.getChoice(index).toString()) : this._font.getBounds(this.getChoice(index).toString());
   }

   protected void prepareFonts(Font font) {
      if (font != this._font) {
         this._font = font;
         this._boldFont = font.derive(1);
         if (this._boldFont == font) {
            this._boldFont = font.derive(64);
         }
      }
   }
}
