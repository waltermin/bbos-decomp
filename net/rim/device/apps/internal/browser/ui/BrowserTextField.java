package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.api.util.StringPatternEnumerator;
import net.rim.device.api.util.StringPatternRepository$Internal;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.common.BrowserPhoneConfirmation;
import net.rim.device.apps.internal.browser.model.HTTPAddressModel;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.util.FontCache;

public class BrowserTextField extends ActiveRichTextField {
   private BrowserContentImpl _browserContent;
   protected TextFieldLink[] _links;
   private boolean _minimalMenuMode;
   public static final int FONT_DEFAULT;
   public static final int FONT_UNDERLINED;
   public static final int FONT_BOLD;
   public static final int FONT_BOLD_UNDERLINED;
   public static final int FONT_ITALIC;
   public static final int FONT_SMALL;
   public static final int FONT_MEDIUM;
   public static final int FONT_LARGE;
   public static final int LINK_COLOR;
   public static final int HIGHLIGHT_LINK_COLOR;
   private static ContextObject _browserContextObject = (ContextObject)(new Object(2, 96, 61));

   public BrowserTextField(
      BrowserContentImpl browserContent,
      Font defaultFont,
      String text,
      int[] offsets,
      byte[] attributes,
      Font[] fonts,
      int[] foregroundColors,
      int[] backgroundColors,
      TextFieldLink[] links,
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
         scanForActiveRegions(defaultFont, scan, text, offsets, attributes, fonts, foregroundColors, backgroundColors, links)
      );
   }

   protected BrowserTextField(
      BrowserContentImpl browserContent,
      Font defaultFont,
      String text,
      long style,
      boolean minimalMenuMode,
      StringPatternContainer patterns,
      BrowserTextField$BrowserRegionQueue brq
   ) {
      super(text, style, patterns, brq);
      this._links = brq._links;
      this._browserContent = browserContent;
      this._minimalMenuMode = minimalMenuMode;
      this.setFont(defaultFont);
   }

   @Override
   protected Object getContextMenuContext() {
      return _browserContextObject;
   }

   @Override
   public void setText(String text) {
      super.setText(text);
      this._links = null;
   }

   protected void setText(String text, BrowserTextField$BrowserRegionQueue brq) {
      super.setText(text, brq);
      this._links = brq._links;
   }

   protected long getRegionCookieID(int region) {
      if (super._cookieIDs == null) {
         return 0;
      }

      long[] ids = (long[])super._cookieIDs.get(region);
      return ids != null && ids.length > 0 ? ids[0] : 0;
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (this.isSelecting()) {
         super.drawFocus(graphics, on);
      } else if (!this.regionHasCookie()) {
         super.drawFocus(graphics, on);
      } else {
         this.highlightSelectedArea(graphics, on, this.getRunStart(), this.getRunEnd());
      }
   }

   @Override
   public void applyTheme() {
      super.applyTheme();
      if (Graphics.isColor()) {
         Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
         ThemeAttributeSet$Writer fieldThemeAttributes = themeWriter.createThemeAttributeSetWriter(null);
         fieldThemeAttributes.setFocusStyle(3);
         fieldThemeAttributes.setColor(4, 3100495);
         fieldThemeAttributes.setColor(5, 16772045);
         this.setThemeAttributeSet(fieldThemeAttributes.getThemeAttributeSet());
      }
   }

   @Override
   public boolean isSelectable() {
      return this._minimalMenuMode ? false : super.isSelectable();
   }

   @Override
   public boolean isSelectionCopyable() {
      return this._minimalMenuMode ? false : super.isSelectionCopyable();
   }

   private Object getRegionCookieInternal(int region) {
      if (!this.regionHasCookieInternal(region)) {
         Object cookie = super.getCookie(region);
         if (cookie instanceof HTTPAddressModel) {
            HTTPAddressModel model = (HTTPAddressModel)cookie;
            model.setBrowserContent(this._browserContent);
         }

         return cookie;
      } else {
         long cookieID = this.getRegionCookieID(region);
         if (cookieID == 0) {
            return null;
         }

         ContextObject context = (ContextObject)(new Object());
         String regionString = this.getRegionURL(region);
         context.put(253, regionString);
         context.put(-442409970680484936L, this._browserContent);
         return FactoryUtil.createInstance(cookieID, context);
      }
   }

   protected boolean regionHasCookieInternal(int region) {
      return this._links != null && region >= 0 && region < this._links.length && this._links[region] != null;
   }

   protected String getRegionURL(int region) {
      return this._links[region].getURL();
   }

   private static BrowserTextField$BrowserRegionQueue scanForActiveRegions(
      Font defaultFont,
      boolean scan,
      String text,
      int[] offsets,
      byte[] attributes,
      Font[] fonts,
      int[] foregroundColors,
      int[] backgroundColors,
      TextFieldLink[] links
   ) {
      BrowserTextField$BrowserRegionQueue brq = new BrowserTextField$BrowserRegionQueue(0, fonts.length);

      for (int i = 0; i < fonts.length; i++) {
         brq.appendFont(fonts[i], foregroundColors != null ? foregroundColors[i] : -1, backgroundColors != null ? backgroundColors[i] : -1);
      }

      StringPatternEnumerator stringEnum = (StringPatternEnumerator)(new Object(text, StringPatternRepository$Internal.getStringPatterns()));
      StringPattern$Match stringMatch = (StringPattern$Match)(new Object());
      int linkIndex = 0;
      int linksLength = links == null ? 0 : links.length;
      byte[] underlinedAttributes = null;

      for (int i = 0; i < offsets.length - 1; i++) {
         int startOffset = offsets[i];
         int endOffset = offsets[i + 1];
         if (linkIndex < linksLength && startOffset >= links[linkIndex].getStartOffset() && endOffset <= links[linkIndex].getEndOffset()) {
            boolean matchFound = false;
            String href = links[linkIndex].getURL();
            stringEnum.reset(href, 0, href.length());
            if (stringEnum.hasMoreMatches()) {
               stringEnum.nextMatch(stringMatch);
               if (stringMatch.beginIndex == 0 && stringMatch.endIndex == href.length()) {
                  matchFound = true;
                  brq.appendRegion(endOffset, attributes[i], stringMatch.id, links[linkIndex]);
               }
            }

            if (!matchFound) {
               brq.appendRegion(endOffset, attributes[i], 5019899335844518230L, links[linkIndex]);
            }

            if (endOffset == links[linkIndex].getEndOffset()) {
               linkIndex++;
            }
         } else {
            if (scan) {
               stringEnum.reset(text, startOffset, endOffset);
               int lastBegin = -1;
               int lastEnd = -1;

               while (stringEnum.hasMoreMatches()) {
                  stringEnum.nextMatch(stringMatch);
                  if (lastBegin == stringMatch.beginIndex && lastEnd == stringMatch.endIndex) {
                     brq.appendCookieID(stringMatch.id);
                  } else {
                     brq.appendRegion(stringMatch.beginIndex, attributes[i], 0);
                     if (underlinedAttributes == null) {
                        underlinedAttributes = new byte[attributes.length];
                     }

                     if (underlinedAttributes[i] == 0) {
                        Font currentFont = fonts[attributes[i]] != null ? fonts[attributes[i]] : (defaultFont != null ? defaultFont : Font.getDefault());
                        Font underlinedFont = FontCache.getInstance().getFont(defaultFont, currentFont.getStyle() | 4 | 8, currentFont.getHeight());
                        underlinedAttributes[i] = brq.appendFont(underlinedFont, 4856319, backgroundColors != null ? backgroundColors[attributes[i]] : -1);
                     }

                     brq.appendRegion(stringMatch.endIndex, underlinedAttributes[i], stringMatch.id);
                     lastBegin = stringMatch.beginIndex;
                     lastEnd = stringMatch.endIndex;
                  }
               }
            }

            brq.appendRegion(endOffset, attributes[i], 0);
         }
      }

      stringEnum.reset(null, 0, 0);
      brq.trim();
      return brq;
   }

   @Override
   protected boolean invokeAction(int action) {
      if (action == 1 && this._browserContent != null && (this._browserContent.getRenderingFlags() & 32) == 0) {
         Verb defaultVerb = this._browserContent.getDefaultVerbUnderCursor();
         if (defaultVerb != null) {
            defaultVerb.invoke(new Object(61));
            return true;
         }
      }

      return super.invokeAction(action);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == '\n' && this.regionHasCookie()) {
         return false;
      } else {
         return key == ' ' ? false : super.keyChar(key, status, time);
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      return false;
   }

   @Override
   public boolean isCookieValid(int id) {
      return super.isCookieValid(id) || this._links != null && id < this._links.length && this._links[id] != null;
   }

   @Override
   public Object getCookie(int id) {
      return this._links != null && id < this._links.length && id >= 0 && this._links[id] != null ? this.getRegionCookieInternal(id) : super.getCookie(id);
   }

   @Override
   protected MenuItem addCookieMenuItems(CookieProvider provider, int cookieId, ContextMenu contextMenu, Object context) {
      Object cookie = this.getRegionCookie(cookieId);
      return this.addCookieMenuItems(provider, cookie, contextMenu, context);
   }

   static {
      _browserContextObject.setFlag(83);
      _browserContextObject.setFlag(117);
      _browserContextObject.put(8128293842573788963L, new BrowserPhoneConfirmation());
   }
}
