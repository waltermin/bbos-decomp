package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.MediaIcon;
import net.rim.vm.Array;

class SmileySymbolScreen$SmileySymbolField extends SymbolScreen$SymbolField {
   private final SmileySymbolScreen this$0;

   protected SmileySymbolScreen$SmileySymbolField(SmileySymbolScreen _1) {
      super(_1);
      this.this$0 = _1;
   }

   @Override
   protected int getMaxSymbolHeight() {
      return Math.max(super.getMaxSymbolHeight(), SmileySymbolScreen._smileyFacility.emoticonSize());
   }

   @Override
   protected int getMaxSymbolWidth() {
      return Math.max(super.getMaxSymbolWidth(), SmileySymbolScreen._smileyFacility.emoticonSize());
   }

   @Override
   protected int getNumPages() {
      return super.getNumPages() + SmileySymbolScreen._smileyFacility.emoticonScreenLayouts().length;
   }

   @Override
   protected int[] getPages() {
      int[] pages = super.getPages();
      int[][][] smileyMaps = (int[][][])SmileySymbolScreen._smileyFacility.emoticonScreenLayouts();
      if (smileyMaps != null) {
         int nonEmptyPages = pages.length;
         Array.resize(pages, nonEmptyPages + smileyMaps.length);
         int numPages = super.getNumPages();

         for (int page = numPages; page < numPages + smileyMaps.length; page++) {
            pages[nonEmptyPages] = page;
            nonEmptyPages++;
         }
      }

      return pages;
   }

   @Override
   protected void initCurrentPageMap(int page) {
      if (page < super._pagesStandard) {
         super.initCurrentPageMap(page);
      } else {
         this.updatePageNumber();
         int[] smileymap = SmileySymbolScreen._smileyFacility.emoticonScreenLayouts()[page - super._pagesStandard];
         super._map.clear();

         for (int i = smileymap.length - 1; i >= 0; i--) {
            super._map.put(65 + i, smileymap[i]);
         }

         super._map.put(90, 8646);
         this.updateDescription();
      }
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      int result = super.moveFocus(amount, status, time);
      this.updateDescription();
      return result;
   }

   @Override
   protected void updateDescription() {
      String description = null;
      char focusChar = this.getFocusChar();
      if (focusChar == 8646) {
         description = "next page";
      } else if (super._pages[super._currentPage] >= super._pagesStandard) {
         int replacement = super._map.get(focusChar);
         if (replacement != -1) {
            description = SmileySymbolScreen._smileyFacility.emoticonDescription(replacement);
         }
      }

      this.setDescription(description);
   }

   @Override
   protected void paintSymbol(Graphics graphics, char symbol, int x, int y, int width, int height) {
      boolean charm = InternalServices.isReducedFormFactor();
      int _symKeyWidth = super._keyWidth;
      if (symbol == 8646) {
         int icon = super._currentPage < this.getNumPagesAvailable() - 1 ? 0 : 4;
         if (icon == 0 || icon == 4) {
            MediaIcon.COLLECTION.paint(graphics, x, y, width, height, icon);
            return;
         }
      } else {
         if (super._pages[super._currentPage] < super._pagesStandard) {
            graphics.drawText(symbol, x + (symbol == 8646 && !charm ? _symKeyWidth - super._keyWidth >> 1 : 0), y, 4, super._keyWidth - 1);
            return;
         }

         int size = SmileySymbolScreen._smileyFacility.emoticonSize();
         int xSym = x + (width - size >> 1);
         int ySym = y + (height - size >> 1);
         SmileySymbolScreen._smileyFacility.drawEmoticon(graphics, symbol, xSym, ySym);
      }
   }

   @Override
   protected void reset() {
      super.reset();
      this.setDescription(null);
   }

   @Override
   void send(char ch) {
      Screen screen = this.this$0.getTarget().getScreen();
      if (super._pages[super._currentPage] < super._pagesStandard) {
         if (this.this$0._targetField instanceof BasicEditField && ((BasicEditField)this.this$0._targetField).validate(ch)) {
            screen.processKeyEvent(513, ch, 32768, 0);
            return;
         }
      } else if (this.this$0._targetField instanceof BasicEditField) {
         ((BasicEditField)this.this$0._targetField).insert(SmileySymbolScreen._smileyFacility.emoticonReplacementText(ch), 0, true, true);
         screen.processKeyEvent(513, ' ', 32768, 0);
      }
   }
}
