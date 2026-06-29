package net.rim.device.api.ui.component;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.EmoticonStringPattern;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.api.util.StringPatternEnumerator;
import net.rim.device.api.util.StringPatternRepository$Internal;
import net.rim.device.internal.ui.FormatParams;
import net.rim.tid.text.AttributedString;
import net.rim.vm.Array;

public class ActiveRichTextField extends RichTextField implements CookieProvider {
   private StringPatternContainer _patterns;
   protected long[] _cookieID;
   protected IntHashtable _cookieIDs;
   private SmileySupport _smileySupport;
   static final int MAX_IDLE_TIMEOUT = 1000;
   public static final int SCANFLAG_DISABLE_ALL_THREADING = 0;
   public static final int SCANFLAG_THREAD_ON_CREATE = 1;
   private static int _scanFlags = 1;
   protected static final long INVALID_COOKIE_ID = 0L;

   protected void executeBackgroundScan() {
      BackgroundScanThread.post(new ActiveRichTextField$StringPatternScanner(this, null));
   }

   protected Object getContextMenuContext() {
      return null;
   }

   protected int drawText(Graphics graphics, String text, int offset, int len, int x, int y, int flags, int width) {
      return 0;
   }

   protected Manager getMainScreenManager() {
      return null;
   }

   protected MenuItem addCookieMenuItems(CookieProvider provider, int cookieId, ContextMenu contextMenu, Object context) {
      if (this._cookieIDs != null && this._cookieIDs.containsKey(cookieId) && cookieId >= 0 && this._cookieIDs.get(cookieId) != null) {
         long[] cookieIDs = (long[])this._cookieIDs.get(cookieId);
         int numCookies = cookieIDs.length;
         Object[] cookies = new Object[numCookies];

         for (int i = 0; i < numCookies; i++) {
            cookies[i] = super._arSupport.createCookie(super._text, cookieIDs[i]);
         }

         return ActiveRegionSupport.addCookieMenuItems(provider, cookies, contextMenu, context);
      } else {
         return null;
      }
   }

   protected MenuItem addCookieMenuItems(CookieProvider provider, Object cookie, ContextMenu contextMenu, Object context) {
      return ActiveRegionSupport.addCookieMenuItems(provider, cookie, contextMenu, context);
   }

   public boolean regionHasCookie() {
      return super._arSupport.isInCookieRegion(this.getCursorPosition());
   }

   public boolean regionHasCookie(int region) {
      return this._cookieIDs == null ? false : this._cookieIDs.get(region) != null;
   }

   public void setText(String text, int[] offsets, byte[] attributes, Font[] fonts, int[] foregroundColors, int[] backgroundColors) {
      this.setText(
         text,
         scanForActiveRegions(text, offsets, attributes, fonts, this.getFont(), foregroundColors, backgroundColors, this.getLabelLength(), this._patterns)
      );
   }

   protected void setText(String text, ActiveRichTextField$RegionQueue rq) {
      this._cookieID = rq.getSingleCookieRegions();
      this._cookieIDs = rq.cookieID;
      super.setText(text, rq.offsets, rq.attributes, rq.fonts, null);
      this.setAttributes(rq.foregroundColors, rq.backgroundColors);
   }

   protected int super_scrollVertically(int amount) {
      return super.scrollVertically(amount);
   }

   public void setText(String name, String text, Font[] fonts, int[] foregroundColors) {
      String finalText = name + text;
      int[] offsets = new int[]{0, name.length(), finalText.length()};
      byte[] attributes = new byte[]{0, 1};
      this.setText(finalText, offsets, attributes, fonts, foregroundColors, null);
   }

   @Override
   public Object getCookieWithFocus() {
      return super._arSupport.getCookieWithFocus(this.getCursorPosition());
   }

   public static int getScanFlags() {
      return _scanFlags;
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      Object cookie = this.getCookieWithFocus();
      ActiveFieldCookie afc = null;
      switch (key) {
         case 10:
            if (cookie instanceof Object[]) {
               Object[] cookies = (Object[])cookie;

               for (int i = 0; i < cookies.length; i++) {
                  Object var11 = cookies[i];
                  if (cookies[i] instanceof ActiveFieldCookie) {
                     afc = (ActiveFieldCookie)var11;
                     break;
                  }
               }
            } else if (cookie instanceof ActiveFieldCookie) {
               afc = (ActiveFieldCookie)cookie;
            }

            if (afc != null) {
               MenuItem item = afc.getFocusVerbs(this, this.getContextMenuContext(), new Vector());
               if (item != null) {
                  item.run();
                  return true;
               }
            }
            break;
         case 21:
            if (cookie instanceof Object[]) {
               Object[] cookies = (Object[])cookie;

               for (int i = 0; i < cookies.length; i++) {
                  Object var10000 = cookies[i];
                  if (cookies[i] instanceof ActiveFieldCookie) {
                     afc = (ActiveFieldCookie)var10000;
                     break;
                  }
               }
            } else if (cookie instanceof ActiveFieldCookie) {
               afc = (ActiveFieldCookie)cookie;
            }

            if (afc != null && afc.invokeApplicationKeyVerb()) {
               return true;
            }
      }

      return super.keyDown(keycode, time);
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu, int instance) {
      super.makeContextMenu(contextMenu, instance);
      if (instance != 65537) {
         int cookieId = super._arSupport.getCookieWithFocusId(this.getCursorPosition());
         MenuItem defaultItem = this.addCookieMenuItems(this, cookieId, contextMenu, this.getContextMenuContext());
         if (defaultItem != null) {
            contextMenu.setDefaultItem(defaultItem);
            return;
         }

         AbstractString text = AbstractStringWrapper.createInstance(this.getText());
         int curIndex = this.getCursorPosition();
         ActiveRegionSupport.addCookieMenuItems(this, text, 0, curIndex, text.length(), this._patterns, contextMenu, null, instance);
      }
   }

   public ActiveRichTextField(String text) {
      this(text, 0);
   }

   protected ActiveRichTextField(String text, long style, StringPatternContainer patterns, ActiveRichTextField$RegionQueue rq) {
      super(text, rq.offsets, rq.attributes, rq.fonts, null, style);
      this._smileySupport = new SmileySupport(this);
      this.setAttributes(rq.foregroundColors, rq.backgroundColors);
      this._cookieID = rq.getSingleCookieRegions();
      this._cookieIDs = rq.cookieID;
      this._patterns = patterns;
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (this.isSelecting()) {
         XYRect focusRect = Ui.getTmpXYRect();
         super.getFocusRect(focusRect);
         graphics.pushContext(focusRect, 0, 0);
         Ui.returnTmpXYRect(focusRect);
         super.drawFocus(graphics, on);
         graphics.popContext();
      } else if (!this.regionHasCookie()) {
         XYRect focusRect = Ui.getTmpXYRect();
         super.getFocusRect(focusRect);
         graphics.pushContext(focusRect, 0, 0);
         Ui.returnTmpXYRect(focusRect);
         super.drawFocus(graphics, on);
         graphics.popContext();
      } else {
         this.highlightSelectedArea(graphics, on, super._arSupport.getSameCookieRunStart(this), super._arSupport.getSameCookieRunEnd(this));
         if (!this.getScreen().isScrollBehaviourView()) {
            XYRect focusRect = Ui.getTmpXYRect();
            super.getFocusRect(focusRect);
            int y = focusRect.y;
            int height = focusRect.height >> 2;
            y += focusRect.height - height;
            this.drawHighlightRegion(graphics, 1, on, focusRect.x, y, focusRect.width, height);
            Ui.returnTmpXYRect(focusRect);
         }
      }
   }

   @Override
   public void getFocusRect(XYRect rect) {
      super.getFocusRect(rect);
      super._arSupport.getFocusRect(rect, this);
   }

   private static ActiveRichTextField$RegionQueue scanForActiveRegions(String text, Font defaultFont, int labelLength, StringPatternContainer patterns) {
      if (text != null && text.length() != 0) {
         StringPatternEnumerator patternEnum = new StringPatternEnumerator(text, patterns);
         if (!patternEnum.hasMoreMatches()) {
            return new ActiveRichTextField$RegionQueue(0, 0);
         }

         ActiveRichTextField$RegionQueue rq = new ActiveRichTextField$RegionQueue(0, 2);
         byte attrNormal = rq.appendFont(null);
         byte attrUnderlined = rq.appendFont(defaultFont.derive(defaultFont.getStyle() | 4 | 8));
         StringPattern$Match match = new StringPattern$Match();
         int lastBegin = -1;
         int lastEnd = -1;

         while (patternEnum.hasMoreMatches()) {
            patternEnum.nextMatch(match);
            int beginIndex = labelLength + match.beginIndex;
            int endIndex = labelLength + match.endIndex;
            if (lastBegin == beginIndex && lastEnd == endIndex) {
               rq.appendCookieID(match.id);
            } else {
               rq.appendRegion(beginIndex, attrNormal, 0);
               rq.appendRegion(endIndex, attrUnderlined, match.id);
               lastBegin = beginIndex;
               lastEnd = endIndex;
            }
         }

         rq.appendRegion(text.length(), attrNormal, 0);
         rq.trim();
         return rq;
      } else {
         return new ActiveRichTextField$RegionQueue(0, 0);
      }
   }

   private static ActiveRichTextField$RegionQueue scanForActiveRegions(
      String text,
      int[] offsets,
      byte[] attributes,
      Font[] fonts,
      Font defaultFont,
      int[] foregroundColors,
      int[] backgroundColors,
      int labelLength,
      StringPatternContainer patterns
   ) {
      if (offsets == null) {
         return scanForActiveRegions(text, defaultFont, labelLength, patterns);
      }

      ActiveRichTextField$RegionQueue rq = new ActiveRichTextField$RegionQueue(0, fonts.length);

      for (int i = 0; i < fonts.length; i++) {
         rq.appendFont(fonts[i], foregroundColors != null ? foregroundColors[i] : -1, backgroundColors != null ? backgroundColors[i] : -1);
      }

      StringPatternEnumerator patternEnum = new StringPatternEnumerator(text, patterns);
      StringPattern$Match patternMatch = new StringPattern$Match();
      byte[] underlinedAttributes = null;

      for (int i = 0; i < offsets.length - 1; i++) {
         int startOffset = offsets[i];
         int endOffset = offsets[i + 1];
         patternEnum.reset(text, startOffset, endOffset);
         int lastBegin = -1;
         int lastEnd = -1;

         while (patternEnum.hasMoreMatches()) {
            patternEnum.nextMatch(patternMatch);
            if (lastBegin == patternMatch.beginIndex && lastEnd == patternMatch.endIndex) {
               rq.appendCookieID(patternMatch.id);
            } else {
               rq.appendRegion(patternMatch.beginIndex, attributes[i], 0);
               if (underlinedAttributes == null) {
                  underlinedAttributes = new byte[attributes.length];
               }

               if (underlinedAttributes[i] == 0) {
                  Font currentFont = fonts[attributes[i]] != null ? fonts[attributes[i]] : defaultFont;
                  Font underlinedFont = currentFont.derive(currentFont.getStyle() | 4 | 8);
                  underlinedAttributes[i] = rq.appendFont(
                     underlinedFont,
                     foregroundColors != null ? foregroundColors[attributes[i]] : -1,
                     backgroundColors != null ? backgroundColors[attributes[i]] : -1
                  );
               }

               rq.appendRegion(patternMatch.endIndex, underlinedAttributes[i], patternMatch.id);
               lastBegin = patternMatch.beginIndex;
               lastEnd = patternMatch.endIndex;
            }
         }

         rq.appendRegion(endOffset, attributes[i], 0);
      }

      rq.trim();
      return rq;
   }

   @Override
   protected int scrollVertically(int amount) {
      if (amount == 0) {
         return 0;
      }

      if (this.isSelecting()) {
         return super.scrollVertically(amount);
      }

      if (this.getScreen().isScrollBehaviourView()) {
         boolean hasScrolled;
         if (amount < 0) {
            hasScrolled = super._arSupport.scrollToPrevActiveRegion(this, this.getCursorPosition());
         } else {
            hasScrolled = super._arSupport.scrollToNextActiveRegion(this, this.getCursorPosition());
         }

         if (hasScrolled) {
            this.setCursorPosition(super._arSupport.getRunStart(), 0);
            return 0;
         } else {
            return amount;
         }
      } else {
         amount = super._arSupport.scrollVertically(amount, this, this.getCursorLine()._line);
         if (amount != 0) {
            return super.scrollVertically(amount + super._arSupport.getAdjustedAmount());
         }

         super.scrollVertically(super._arSupport.getAdjustedAmount());
         if (super._arSupport.endsOnCookie()) {
            int newPos = MathUtilities.clamp(super._arSupport.getRunStart(), this.getCaretPosition(), super._arSupport.getRunEnd() - 1);
            this.setSelection(newPos, true, newPos);
         }

         return 0;
      }
   }

   @Override
   public void selectionCopy(Clipboard cb) {
      if (!this.isSelecting() && super._arSupport.isInCookieRegion(this.getCaretPosition())) {
         cb.put(super._arSupport.getCurrentRegionText(this.getCaretPosition(), super._text));
      } else {
         super.selectionCopy(cb);
      }
   }

   @Override
   public boolean isSelectionCopyable() {
      return this.isSelecting() ? super.isSelectionCopyable() : super._arSupport.isInCookieRegion(this.getCaretPosition());
   }

   public static void setScanFlags(int flags) {
      _scanFlags = flags;
   }

   @Override
   public void setText(String text) {
      this.setText(text, scanForActiveRegions(text, this.getFont(), this.getLabelLength(), this._patterns));
   }

   @Override
   public void setText(String text, int[] offsets, byte[] attributes, Font[] fonts) {
      this.setText(text, offsets, attributes, fonts, null, null);
   }

   public ActiveRichTextField(String text, long style) {
      this(text, null, null, null, null, null, style);
   }

   @Override
   public Object getRegionCookie() {
      return super._arSupport.getCookieWithFocus(this.getCursorPosition());
   }

   private void setText(AttributedString text, ActiveRichTextField$RegionQueue rq) {
      this._cookieID = rq.getSingleCookieRegions();
      this._cookieIDs = rq.cookieID;
      super.setText(text, rq.offsets, rq.attributes, rq.fonts, null);
      this.setAttributes(rq.foregroundColors, rq.backgroundColors);
   }

   private void setTextFromBackgroundScanner(
      String text, int[] offsets, byte[] attributes, Font[] fonts, int[] foreColors, int[] bgColors, ActiveRichTextField$RegionQueue rq
   ) {
      Object lockObject = this;

      try {
         lockObject = Application.getEventLock();
      } catch (IllegalStateException var15) {
      }

      synchronized (lockObject) {
         AttributedString attribText = this.getAttributedText();
         attribText = new AttributedString(attribText, this.getLabelLength(), attribText.length());
         if (this.getText().equals(text)
            && Arrays.equals(offsets, this.getOffsets())
            && Arrays.equals(attributes, this.getAttributes())
            && Arrays.equals(fonts, this.getFonts(false))
            && Arrays.equals(foreColors, this.getForegroundColors())
            && Arrays.equals(bgColors, this.getBackgroundColors())) {
            int curPos = this.getCursorPosition();
            int savedScrollPos = 0;
            Manager scrollableManager = this.getManager();

            while (scrollableManager != null && !scrollableManager.isStyle(281474976710656L)) {
               scrollableManager = scrollableManager.getManager();
            }

            if (scrollableManager != null) {
               savedScrollPos = scrollableManager.getVerticalScroll();
            }

            attribText.setAttrib(0, attribText.length(), this.getDefaultFontAttributes(), -1, 0, -1);
            this.setText(attribText, rq);
            this.setCursorPosition(curPos);
            if (scrollableManager != null) {
               scrollableManager.setVerticalScroll(savedScrollPos);
            }
         }
      }
   }

   @Override
   public Object getRegionCookie(int region) {
      return super._arSupport.getCookieForRegionIndex(region);
   }

   @Override
   public void setAttributes(int[] colorForeground, int[] colorBackground) {
      if (colorForeground != null) {
         this.correctColorArray(colorForeground);
      }

      if (colorBackground != null) {
         this.correctColorArray(colorBackground);
      }

      super.setAttributes(colorForeground, colorBackground);
   }

   private void correctColorArray(int[] array) {
      Font[] fonts = this.getFonts(false);
      if (fonts.length - array.length != 1) {
         Array.resize(array, fonts.length);
      } else {
         Array.resize(array, fonts.length);
         array[fonts.length - 1] = -1;
      }
   }

   public ActiveRichTextField(
      String text, int[] offsets, byte[] attributes, Font[] fonts, int[] foregroundColors, int[] backgroundColors, long style, StringPatternContainer patterns
   ) {
      super(text, offsets, attributes, fonts, style);
      this.setAttributes(foregroundColors, backgroundColors);
      this._smileySupport = new SmileySupport(this);
      if (patterns != null) {
         for (int index = 0; index < patterns.size(); index++) {
            Object pattern = patterns.getAt(index);
            if (pattern instanceof EmoticonStringPattern) {
               this._smileySupport.setPattern((EmoticonStringPattern)pattern);
               if (patterns.size() > 1) {
                  StringPattern[] elements = new StringPattern[patterns.size() - 1];

                  for (int index1 = 0; index1 < patterns.size(); index1++) {
                     if (index1 != index) {
                        elements[index1 < index ? index1 : index1 - 1] = (StringPattern)patterns.getAt(index1);
                     }
                  }

                  this._patterns = new StringPatternContainer(elements);
               }
               break;
            }
         }
      }

      if (this._patterns == null) {
         this._patterns = StringPatternRepository$Internal.getStringPatterns();
      }

      boolean doBackgroundScan = (_scanFlags & 1) != 0;
      if (text == null || text.length() <= 32) {
         doBackgroundScan = false;
      }

      if (offsets == null) {
         if (doBackgroundScan) {
            BackgroundScanThread.post(new ActiveRichTextField$StringPatternScanner(this, null));
         } else {
            this.setText(text);
         }
      } else if (doBackgroundScan) {
         BackgroundScanThread.post(new ActiveRichTextField$StringPatternScanner(this, null));
      } else {
         this.setText(text, offsets, attributes, fonts, foregroundColors, backgroundColors);
      }
   }

   @Override
   protected void notifyTextChanged(FormatParams aParams, boolean aIsInsertionOrDeletion) {
      if (aIsInsertionOrDeletion) {
         this._smileySupport.scanForSmileys(aParams);
      }

      super.notifyTextChanged(aParams, aIsInsertionOrDeletion);
   }

   @Override
   public String getText(int offset, int length) {
      return !this._smileySupport.isSmileyAvailable() ? super.getText(offset, length) : this._smileySupport.getDecodedString(offset, offset + length);
   }

   @Override
   public int getDecodedTextLength(int start, int end) {
      return this._smileySupport != null && this._smileySupport.isSmileyAvailable() ? this._smileySupport.getDecodedStringLength(start, end) : end - start;
   }

   public ActiveRichTextField(String text, int[] offsets, byte[] attributes, Font[] fonts, int[] foregroundColors, int[] backgroundColors, long style) {
      this(text, offsets, attributes, fonts, foregroundColors, backgroundColors, style, StringPatternRepository$Internal.getStringPatterns());
   }

   @Override
   public boolean isCookieValid(int id) {
      return this._cookieIDs != null && this._cookieIDs.containsKey(id) && id >= 0 && this._cookieIDs.get(id) != null;
   }

   @Override
   public Object getCookie(int id) {
      if (this._cookieIDs != null && this._cookieIDs.containsKey(id) && id >= 0 && this._cookieIDs.get(id) != null) {
         long[] cookieIDs = (long[])this._cookieIDs.get(id);
         int numCookies = cookieIDs.length;
         if (numCookies > 1) {
            Object[] cookies = new Object[numCookies];

            for (int i = 0; i < cookieIDs.length; i++) {
               cookies[i] = super._arSupport.createCookie(super._text, cookieIDs[i]);
            }

            return cookies;
         } else {
            return numCookies == 1 ? super._arSupport.createCookie(super._text, cookieIDs[0]) : null;
         }
      } else {
         return null;
      }
   }
}
