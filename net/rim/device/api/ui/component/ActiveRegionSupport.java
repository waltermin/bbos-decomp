package net.rim.device.api.ui.component;

import java.util.Vector;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.internal.ui.ArticInterface$Line;
import net.rim.device.internal.ui.ArticInterface$LineInfo;
import net.rim.device.internal.ui.Formatter;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.AttributedString$Iterator;

class ActiveRegionSupport {
   private int _currentRunIndex;
   private int _currentRunStart;
   private AttributedString$Iterator _runIterator;
   private ActiveRegionSupport$ActiveRegionFieldIf _arField;
   private int _adjustedAmount;
   private boolean _endsOnCookie;
   private static final long COOKIES_ATTRIB_MASK = 65504L;
   private static final long COOKIES_ATTRIB_SHIFT = 5L;

   ActiveRegionSupport(AttributedString$Iterator iterator, ActiveRegionSupport$ActiveRegionFieldIf arField) {
      this._runIterator = iterator;
      this._arField = arField;
      this.init();
   }

   public boolean isInCookieRegion(int caretPos) {
      this.adjustCurrentRun(caretPos);
      return this.isInCookieRegion();
   }

   private boolean isInCookieRegion() {
      long shiftedCookieId = this._runIterator.runXAttrib() & 65504;
      return shiftedCookieId != 0 && this._arField.isCookieValid((int)(shiftedCookieId >> 5) - 1);
   }

   public int getRunStart() {
      return this._currentRunStart;
   }

   public int getRunEnd() {
      return this._currentRunStart + this._runIterator.runLength();
   }

   public long getRunAttrib() {
      return this._runIterator.runAttrib();
   }

   public long getRunXAttrib() {
      return this._runIterator.runXAttrib();
   }

   public int pos() {
      return this._runIterator.pos();
   }

   public boolean nextActiveRegion() {
      boolean hasNext = true;

      while (hasNext) {
         if (this.isInCookieRegion()) {
            return true;
         }

         hasNext = this.nextRun();
      }

      return false;
   }

   public boolean prevActiveRegion() {
      boolean hasPrev = true;

      while (hasPrev) {
         if (this.isInCookieRegion()) {
            return true;
         }

         hasPrev = this.prevRun();
      }

      return false;
   }

   public void getFocusRect(XYRect rect, TextField field) {
      if (this.isInCookieRegion(field.getCaretPosition())) {
         int firstOffset = this.getSameCookieRunStart(this._arField);
         int lastOffset = this.getSameCookieRunEnd(this._arField);
         if (firstOffset < lastOffset) {
            XYRect tempRect = Ui.getTmpXYRect();
            ArticInterface$LineInfo info = field.getLineInfoForDocPos(firstOffset, true);
            ArticInterface$Line line = info._line;
            int y = info._top;

            for (int offset = info._start; line != null && offset < lastOffset; line = line._next) {
               int offset1 = offset <= firstOffset ? firstOffset : offset;
               int offset2 = offset + line._textLength > lastOffset ? lastOffset : offset + line._textLength;
               Formatter.getTextBounds(offset1, offset2, tempRect, line, offset, y);
               rect.union(tempRect);
               y += tempRect.height;
               offset += line._textLength + line._skippedCharacters;
            }

            Ui.returnTmpXYRect(tempRect);
         }
      }
   }

   public int scrollVertically(int amount, TextField field, ArticInterface$Line curLine) {
      this._adjustedAmount = 0;
      this._endsOnCookie = true;
      if (amount > 0) {
         for (boolean hasActiveRegion = this.scrollToNextActiveRegion((ActiveRegionSupport$ActiveRegionFieldIf)field, field.getCaretPosition());
            hasActiveRegion;
            hasActiveRegion = this.nextActiveRegion()
         ) {
            int newPos = this._currentRunStart;
            ArticInterface$LineInfo info = field.getLineInfoForDocPos(newPos, true);
            ArticInterface$Line newLine = info._line;
            int skipLineCount = 0;

            for (int amountRemain = amount; amountRemain > 0 && newLine != curLine; skipLineCount++) {
               curLine = curLine._next;
               amountRemain--;
            }

            if (newLine != curLine) {
               this._endsOnCookie = false;
            }

            this._adjustedAmount += skipLineCount;
            amount -= Math.max(1, skipLineCount);
            curLine = newLine;
            if (amount == 0) {
               return amount;
            }

            this.nextRun();
         }
      } else {
         for (boolean hasActiveRegion = this.scrollToPrevActiveRegion((ActiveRegionSupport$ActiveRegionFieldIf)field, field.getCaretPosition());
            hasActiveRegion;
            hasActiveRegion = this.prevActiveRegion()
         ) {
            int newPos = this.getRunEnd() - 1;
            ArticInterface$LineInfo info = field.getLineInfoForDocPos(newPos, true);
            ArticInterface$Line newLine = info._line;
            int skipLineCount = 0;

            for (int amountRemain = -amount; amountRemain > 0 && newLine != curLine; skipLineCount++) {
               curLine = curLine._prev;
               amountRemain--;
            }

            if (newLine != curLine) {
               this._endsOnCookie = false;
            }

            this._adjustedAmount -= skipLineCount;
            amount += Math.max(1, skipLineCount);
            curLine = newLine;
            if (amount == 0) {
               return amount;
            }

            this.prevRun();
         }
      }

      return amount;
   }

   public int getAdjustedAmount() {
      return this._adjustedAmount;
   }

   public boolean endsOnCookie() {
      return this._endsOnCookie;
   }

   public int getRegion(int curPos) {
      return !this.adjustCurrentRun(curPos) ? -1 : this._currentRunIndex;
   }

   public String getRegionText(int region, AttributedString text) {
      return !this.adjustCurrentRunForIndex(region) ? null : text.getText(this._currentRunStart, this.getRunEnd());
   }

   public String getCurrentRegionText(int curPos, AttributedString text) {
      this.adjustCurrentRun(curPos);
      return text.getText(this._currentRunStart, this.getRunEnd());
   }

   public void init() {
      this._currentRunStart = 0;
      this._currentRunIndex = 0;
      this._runIterator.set(0, this._runIterator.text().length());
   }

   public boolean adjustCurrentRunForIndex(int index) {
      while (this._currentRunIndex < index) {
         if (!this.nextRun()) {
            return false;
         }
      }

      while (this._currentRunIndex > index) {
         if (!this.prevRun()) {
            return false;
         }
      }

      return true;
   }

   public static void addCookieMenuItems(
      CookieProvider provider,
      AbstractString str,
      int minIndex,
      int curIndex,
      int maxIndex,
      StringPatternContainer patterns,
      ContextMenu contextMenu,
      Object context,
      int instance
   ) {
      if (patterns != null) {
         StringPattern$Match match = new StringPattern$Match();

         for (int idx = 0; idx < patterns.size(); idx++) {
            StringPattern pattern = (StringPattern)patterns.getAt(idx);

            try {
               if (pattern.findMatch(str, minIndex, curIndex, maxIndex, match)) {
                  char[] data = new char[match.endIndex - match.beginIndex];
                  str.getChars(match.beginIndex, match.endIndex, data, 0);
                  Object cookie = createCookie(match.id, new String(data));
                  if (cookie instanceof ActiveFieldCookie) {
                     ActiveFieldCookie afc = (ActiveFieldCookie)cookie;
                     Vector items = new Vector();
                     MenuItem defaultItem = afc.getFocusVerbs(provider, context, items);
                     if (instance == 0) {
                        for (int i = 0; i < items.size(); i++) {
                           if (items.elementAt(i) instanceof MenuItem) {
                              contextMenu.addItem((MenuItem)items.elementAt(i));
                           }
                        }
                     } else if (defaultItem != null && contextMenu != null) {
                        contextMenu.addItem(defaultItem);
                     }
                  }
               }
            } catch (IndexOutOfBoundsException var18) {
            }
         }
      }
   }

   static MenuItem addCookieMenuItems(CookieProvider provider, Object cookie, ContextMenu contextMenu, Object context) {
      MenuItem defaultItem = null;
      if (!(cookie instanceof ActiveFieldCookie)) {
         if (cookie instanceof Object[]) {
            Object[] cookies = (Object[])cookie;

            for (int i = 0; i < cookies.length; i++) {
               Object var10000 = cookies[i];
               if (cookies[i] instanceof ActiveFieldCookie) {
                  MenuItem currDefault = cookieToMenuItem((ActiveFieldCookie)var10000, provider, contextMenu, context);
                  if (currDefault != null) {
                     defaultItem = currDefault;
                  }
               }
            }
         }
      } else {
         defaultItem = cookieToMenuItem((ActiveFieldCookie)cookie, provider, contextMenu, context);
      }

      return defaultItem;
   }

   private static MenuItem cookieToMenuItem(ActiveFieldCookie cookie, CookieProvider provider, ContextMenu contextMenu, Object context) {
      MenuItem defaultItem = null;
      Vector items = new Vector();
      defaultItem = cookie.getFocusVerbs(provider, context, items);
      int count = items.size();

      for (int i = 0; i < count; i++) {
         MenuItem menuItem = (MenuItem)items.elementAt(i);
         if (contextMenu != null) {
            contextMenu.addItem(menuItem);
         }
      }

      return defaultItem;
   }

   private static Object createCookie(long cookieID, String text) {
      ActiveFieldContext context = new ActiveFieldContext(text);
      context.setID(cookieID);
      return FactoryUtil.createInstance(cookieID, context);
   }

   public Object getCookieWithFocus(int curPos) {
      if (!this.isInCookieRegion(curPos)) {
         return null;
      }

      int cookieId = (int)((this._runIterator.runXAttrib() & 65504) >> 5) - 1;
      return this._arField.getCookie(cookieId);
   }

   public int getCookieWithFocusId(int curPos) {
      return !this.isInCookieRegion(curPos) ? -1 : (int)((this._runIterator.runXAttrib() & 65504) >> 5) - 1;
   }

   public Object getCookieForRegionIndex(int regionIndex) {
      if (!this.adjustCurrentRunForIndex(regionIndex)) {
         return null;
      }

      int cookieId = (int)((this._runIterator.runXAttrib() & 65504) >> 5) - 1;
      return this._arField.getCookie(cookieId);
   }

   private boolean adjustCurrentRun(int offset) {
      while (offset >= this._currentRunStart + this._runIterator.runLength()) {
         if (!this.nextRun()) {
            return false;
         }
      }

      while (this._currentRunStart > offset) {
         if (!this.prevRun()) {
            return false;
         }
      }

      return true;
   }

   public boolean scrollToNextActiveRegion(ActiveRegionSupport$ActiveRegionFieldIf arField, int curPos) {
      boolean hasNext = true;
      if (this.isInCookieRegion(curPos)) {
         hasNext = this.nextDifferentCookieRun(arField);
      }

      return hasNext && this.nextActiveRegion();
   }

   public boolean scrollToPrevActiveRegion(ActiveRegionSupport$ActiveRegionFieldIf arField, int curPos) {
      boolean hasPrev = true;
      if (this.isInCookieRegion(curPos)) {
         hasPrev = this.prevDifferentCookieRun(arField);
      }

      return hasPrev && this.prevActiveRegion();
   }

   public int getSameCookieRunStart(ActiveRegionSupport$ActiveRegionFieldIf arField) {
      int initialRunId = this._currentRunIndex;
      int start = this._currentRunStart;

      while (this.prevRun() && arField.regionsHaveSameCookie(this._currentRunIndex, this._currentRunIndex + 1)) {
         start = this._currentRunStart;
      }

      this.adjustCurrentRunForIndex(initialRunId);
      return start;
   }

   public int getSameCookieRunEnd(ActiveRegionSupport$ActiveRegionFieldIf arField) {
      int initialRunId = this._currentRunIndex;
      int end = this._currentRunStart + this._runIterator.runLength();

      while (this.nextRun() && arField.regionsHaveSameCookie(this._currentRunIndex - 1, this._currentRunIndex)) {
         end = this._currentRunStart + this._runIterator.runLength();
      }

      this.adjustCurrentRunForIndex(initialRunId);
      return end;
   }

   Object createCookie(AttributedString text, long cookieId) {
      try {
         String cookieText = text.getText(this._currentRunStart, this.getRunEnd());
         return createCookie(cookieId, cookieText);
      } catch (IndexOutOfBoundsException e) {
         return null;
      }
   }

   private boolean nextDifferentCookieRun(ActiveRegionSupport$ActiveRegionFieldIf arField) {
      while (this.nextRun()) {
         if (!arField.regionsHaveSameCookie(this._currentRunIndex - 1, this._currentRunIndex)) {
            return true;
         }
      }

      return false;
   }

   private boolean prevDifferentCookieRun(ActiveRegionSupport$ActiveRegionFieldIf arField) {
      while (this.prevRun()) {
         if (!arField.regionsHaveSameCookie(this._currentRunIndex, this._currentRunIndex + 1)) {
            return true;
         }
      }

      return false;
   }

   private boolean nextRun() {
      boolean hasNext = this._runIterator.next();
      if (hasNext) {
         this._currentRunStart = this._runIterator.pos();
         this._currentRunIndex++;
      }

      return hasNext;
   }

   private boolean prevRun() {
      boolean hasPrev = this._runIterator.prev();
      if (hasPrev) {
         this._currentRunStart = this._runIterator.pos();
         this._currentRunIndex--;
      }

      return hasPrev;
   }
}
