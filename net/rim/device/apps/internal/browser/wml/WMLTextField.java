package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.api.util.StringPatternEnumerator;
import net.rim.device.api.util.StringPatternRepository$Internal;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.ui.BrowserTextField;
import net.rim.device.apps.internal.browser.util.FontCache;
import net.rim.device.apps.internal.browser.util.LinkType;

public final class WMLTextField extends BrowserTextField {
   private String _initText;
   private int[] _initOffsets;
   private byte[] _attributes;
   private Font[] _fonts;
   private int[] _foregroundColors;
   private int[] _backgroundColors;
   private WMLVariable[] _vars;
   private short[] _initVarStarts;
   private WMLAnchorVerb[] _initAnchorVerbs;
   private WMLAnchorVerb[] _anchorVerbs;
   private boolean _scan;

   WMLTextField(
      BrowserContentImpl browserContent,
      Font defaultFont,
      String text,
      int[] offsets,
      byte[] attributes,
      Font[] fonts,
      int[] foregroundColors,
      int[] backgroundColors,
      WMLVariable[] vars,
      short[] varStarts,
      WMLAnchorVerb[] anchorVerbs,
      String defaultFace,
      boolean scan,
      long style,
      boolean minimalMenuMode
   ) {
      this(
         browserContent,
         defaultFont,
         text,
         style | 67108864,
         minimalMenuMode,
         StringPatternRepository$Internal.getStringPatterns(),
         scanForActiveRegions(defaultFont, scan, text, offsets, attributes, fonts, foregroundColors, backgroundColors, anchorVerbs, defaultFace)
      );
      this._initText = text;
      this._initOffsets = offsets;
      this._attributes = attributes;
      this._fonts = fonts;
      this._foregroundColors = foregroundColors;
      this._backgroundColors = backgroundColors;
      this._vars = vars;
      this._initVarStarts = varStarts;
      this._initAnchorVerbs = anchorVerbs;
      this._scan = scan;
   }

   protected WMLTextField(
      BrowserContentImpl browserContent,
      Font defaultFont,
      String text,
      long style,
      boolean minimalMenuMode,
      StringPatternContainer patterns,
      WMLTextField$WMLRegionQueue wrq
   ) {
      super(browserContent, defaultFont, text, style, minimalMenuMode, patterns, wrq);
      this._anchorVerbs = wrq._anchorVerbs;
   }

   @Override
   public final void setText(String text) {
      super.setText(text);
   }

   final void setText(String text, int[] offsets, byte[] attributes, Font[] fonts, int[] foregroundColors, int[] backgroundColors, WMLAnchorVerb[] anchorVerbs) {
      WMLTextField$WMLRegionQueue wrq = scanForActiveRegions(
         this.getFont(), this._scan, text, offsets, attributes, fonts, foregroundColors, backgroundColors, anchorVerbs, null
      );
      this.setText(text, wrq);
      this._anchorVerbs = wrq._anchorVerbs;
   }

   final void updateValues() {
      String text = this._initText;
      int[] offsets = new int[this._initOffsets.length];
      System.arraycopy(this._initOffsets, 0, offsets, 0, this._initOffsets.length);
      short[] varStarts = new short[this._initVarStarts.length];
      System.arraycopy(this._initVarStarts, 0, varStarts, 0, this._initVarStarts.length);
      WMLAnchorVerb[] anchorVerbs = new WMLAnchorVerb[this._initAnchorVerbs.length];

      for (int i = 0; i < this._initAnchorVerbs.length; i++) {
         anchorVerbs[i] = new WMLAnchorVerb(this._initAnchorVerbs[i]);
      }

      for (int i = 0; i < this._vars.length; i++) {
         String substring = text.substring(0, varStarts[i]);
         String value = "";
         if (this._vars[i].isComposed()) {
            value = this._vars[i].getName();
         } else {
            value = this._vars[i].getValue();
         }

         if (value == null) {
            value = "";
         }

         substring = substring.concat(value);
         substring = substring.concat(text.substring(varStarts[i] + 1));
         text = substring;
         int length = value.length() - 1;

         for (int j = this.getOffsetIndex(varStarts[i], offsets); j < offsets.length; j++) {
            offsets[j] = (short)(offsets[j] + length);
         }

         for (int j = this.getAnchorVerbIndex(varStarts[i], anchorVerbs); j < anchorVerbs.length; j++) {
            WMLAnchorVerb anchorVerb = anchorVerbs[j];
            if (anchorVerb.getStartOffset() > varStarts[i]) {
               anchorVerb.setStartOffset((short)(anchorVerb.getStartOffset() + length));
            }

            anchorVerb.setEndOffset((short)(anchorVerb.getEndOffset() + length));
         }

         for (int j = i + 1; j < varStarts.length; j++) {
            varStarts[j] = (short)(varStarts[j] + length);
         }
      }

      this.setText(text, offsets, this._attributes, this._fonts, this._foregroundColors, this._backgroundColors, anchorVerbs);
   }

   private final int getOffsetIndex(int location, int[] offsets) {
      int i = 0;

      for (i = 0; i < offsets.length; i++) {
         if (offsets[i] > location) {
            return i;
         }
      }

      return i;
   }

   private final int getAnchorVerbIndex(int location, WMLAnchorVerb[] anchorVerbs) {
      int i = 0;

      for (i = 0; i < anchorVerbs.length; i++) {
         if (anchorVerbs[i].getEndOffset() > location) {
            return i;
         }
      }

      return i;
   }

   @Override
   protected final long getRegionCookieID(int region) {
      long cookieID = super.getRegionCookieID(region);
      String href = null;
      if (this._anchorVerbs != null && this._anchorVerbs[region] != null) {
         href = this._anchorVerbs[region].getURL();
      }

      if (href != null) {
         cookieID = LinkType.getLinkType(href);
      }

      return cookieID;
   }

   @Override
   protected final boolean regionHasCookieInternal(int region) {
      return this._anchorVerbs != null && region >= 0 && region < this._anchorVerbs.length && this._anchorVerbs[region] != null
         || super.regionHasCookieInternal(region);
   }

   @Override
   public final boolean isCookieValid(int id) {
      return super.isCookieValid(id) || this._anchorVerbs != null && id < this._anchorVerbs.length && this._anchorVerbs[id] != null;
   }

   @Override
   public final Object getCookie(int id) {
      return this._anchorVerbs != null && id < this._anchorVerbs.length && id >= 0 && this._anchorVerbs[id] != null
         ? new WMLAnchorModel(this._anchorVerbs[id])
         : super.getCookie(id);
   }

   @Override
   protected final MenuItem addCookieMenuItems(CookieProvider provider, int cookieId, ContextMenu contextMenu, Object context) {
      Object cookie = this.getRegionCookie(cookieId);
      return this.addCookieMenuItems(provider, cookie, contextMenu, context);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (Keypad.key(keycode) == 21) {
         int region = this.getRegion(this.getCursorPosition());
         String href;
         if (this.regionHasCookieInternal(region) && (href = this.getRegionURL(region)) != null) {
            StringPatternEnumerator enumeration = new StringPatternEnumerator(href, StringPatternRepository$Internal.getStringPatterns());
            if (enumeration.hasMoreMatches()) {
               StringPattern$Match match = new StringPattern$Match();
               enumeration.nextMatch(match);
               if (match.beginIndex == 0 && match.endIndex == href.length() && match.id == 532879436795165891L) {
                  ContextObject initialData = new ContextObject();
                  initialData.put(253, href);
                  RIMModel phoneNumberModel = (RIMModel)FactoryUtil.createInstance(532879436795165891L, initialData);
                  ControllerUtilities.invokeApplicationKeyVerb(phoneNumberModel);
                  return true;
               }
            }
         }
      }

      return super.keyDown(keycode, time);
   }

   @Override
   protected final String getRegionURL(int region) {
      return this._anchorVerbs != null && this._anchorVerbs[region] != null ? this._anchorVerbs[region].getURL() : super.getRegionURL(region);
   }

   private static final WMLTextField$WMLRegionQueue scanForActiveRegions(
      Font defaultFont,
      boolean scan,
      String text,
      int[] offsets,
      byte[] attributes,
      Font[] fonts,
      int[] foregroundColors,
      int[] backgroundColors,
      WMLAnchorVerb[] anchorVerbs,
      String defaultFace
   ) {
      WMLTextField$WMLRegionQueue wrq = new WMLTextField$WMLRegionQueue(0, fonts.length);

      for (int i = 0; i < fonts.length; i++) {
         wrq.appendFont(fonts[i], foregroundColors != null ? foregroundColors[i] : -1, backgroundColors != null ? backgroundColors[i] : -1);
      }

      StringPatternEnumerator stringEnum = new StringPatternEnumerator(text, StringPatternRepository$Internal.getStringPatterns());
      StringPattern$Match stringMatch = new StringPattern$Match();
      int anchorVerbIndex = 0;
      int anchorVerbsLength = anchorVerbs == null ? 0 : anchorVerbs.length;
      byte[] underlinedAttributes = null;

      for (int i = 0; i < offsets.length - 1; i++) {
         int startOffset = offsets[i];
         int endOffset = offsets[i + 1];
         if (anchorVerbIndex < anchorVerbsLength
            && startOffset >= anchorVerbs[anchorVerbIndex].getStartOffset()
            && endOffset <= anchorVerbs[anchorVerbIndex].getEndOffset()) {
            wrq.appendRegion(endOffset, attributes[i], 0, anchorVerbs[anchorVerbIndex]);
            if (endOffset == anchorVerbs[anchorVerbIndex].getEndOffset()) {
               anchorVerbIndex++;
            }
         } else {
            if (scan) {
               stringEnum.reset(text, startOffset, endOffset);

               for (; stringEnum.hasMoreMatches(); wrq.appendRegion(stringMatch.endIndex, underlinedAttributes[i], stringMatch.id)) {
                  stringEnum.nextMatch(stringMatch);
                  wrq.appendRegion(stringMatch.beginIndex, attributes[i], 0);
                  if (underlinedAttributes == null) {
                     underlinedAttributes = new byte[attributes.length];
                  }

                  if (underlinedAttributes[i] == 0) {
                     Font currentFont = fonts[attributes[i]] != null ? fonts[attributes[i]] : (defaultFont != null ? defaultFont : Font.getDefault());
                     Font underlinedFont = FontCache.getInstance()
                        .getFont(defaultFont, currentFont.getStyle() | 4 | 8, currentFont.getHeight(), 0, null, defaultFace);
                     underlinedAttributes[i] = wrq.appendFont(underlinedFont, 4856319, backgroundColors != null ? backgroundColors[attributes[i]] : -1);
                  }
               }
            }

            wrq.appendRegion(endOffset, attributes[i], 0);
         }
      }

      stringEnum.reset(null, 0, 0);
      wrq.trim();
      return wrq;
   }
}
