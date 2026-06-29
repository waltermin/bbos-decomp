package net.rim.device.apps.internal.browser.html;

import java.util.Stack;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.GlyphMetrics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntStack;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.api.util.StringPatternEnumerator;
import net.rim.device.api.util.StringPatternRepository$Internal;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.ui.TextFlowManager;
import net.rim.device.apps.internal.browser.util.FontCache;
import net.rim.device.apps.internal.browser.util.LinkType;
import net.rim.device.internal.ui.RichText;
import net.rim.vm.Array;
import org.w3c.dom.html2.HTMLElement;

final class TextUtilities {
   private StringPatternEnumerator _stringEnum;
   private StringPattern$Match _stringMatch = (StringPattern$Match)(new Object());
   private HTMLElement _currentFocusCookie;
   private FontCache _fontCache;
   private int _defaultLinkColor;
   private String _renderingEncoding;
   private Font _defaultFont;
   private int[] _fontSizes;
   private int _minimumFontHeight;
   private int _minimumFontStyle;
   private DataBuffer _properties;
   private boolean _autoMatch;
   private long _anchorId;
   private IntStack _fontVariantStack;
   private IntStack _fontWeightStack;
   private IntStack _textDecorationStack;
   private IntStack _textTransformStack;
   private int _currentTextTransform = 97;
   private int _linkColor;
   private IntStack _alignmentStack;
   private IntStack _directionStack;
   private int _currentDirection;
   private IntStack _listIndexStack;
   private IntStack _listTypeStack;
   private Stack _listImageStack;
   private TextFlowManager _textFlowManager;
   private Object _appEventLock;
   private HTMLBrowserContent _browserContent;
   private LongIntHashtable _cachedColorValues;
   private int[] _foregroundHSLCache;
   private int[] _backgroundHSLCache;
   private int _defaultParagDir;
   static final int FONT_SIZE_XX_SMALL;
   static final int FONT_SIZE_X_SMALL;
   static final int FONT_SIZE_SMALL;
   static final int FONT_SIZE_MEDIUM;
   static final int FONT_SIZE_LARGE;
   static final int FONT_SIZE_X_LARGE;
   static final int FONT_SIZE_XX_LARGE;
   static final int FONT_SIZE_XXX_LARGE;
   static final int FONT_SIZE_SMALLER;
   static final int FONT_SIZE_LARGER;
   static final int FONT_STYLE_NORMAL;
   static final int FONT_STYLE_ITALIC;
   static final int FONT_STYLE_OBLIQUE;
   static final int FONT_WEIGHT_NORMAL;
   static final int FONT_WEIGHT_BOLD;
   static final int FONT_WEIGHT_EXTRA_BOLD;
   static final int FONT_WEIGHT_BOLDER;
   static final int FONT_WEIGHT_LIGHTER;
   static final int TEXT_DECORATION_NONE;
   static final int TEXT_DECORATION_UNDERLINE;
   static final int TEXT_DECORATION_OVERLINE;
   static final int TEXT_DECORATION_LINE_THROUGH;
   static final int TEXT_DECORATION_BLINK;
   private static final int HUE;
   private static final int SAT;
   private static final int LIGHT;
   private static final boolean IS_COLOR = Display.isColor();
   private static final int FONT_SIZE_DELTA;
   private static final int SUPPORTED_FLAGS_MASK;
   private static final int INDIVIDUAL_FLAG_MASK;
   private static final int TEXT_COLOR;
   private static final int LINK_COLOR;
   private static String SMALL_CAPS_FONT_FACE = "BBCapitals";
   private static final String[] ROMAN_CHARS = new String[]{"m", "cm", "d", "cd", "c", "xc", "l", "xl", "x", "ix", "v", "iv", "i"};
   private static final int[] ROMAN_VALUES = new int[]{
      1000,
      900,
      500,
      400,
      100,
      90,
      50,
      40,
      10,
      9,
      5,
      4,
      1,
      191365376,
      1929445379,
      1952858410,
      1929445387,
      7618858,
      1093300993,
      1769108596,
      6635362,
      1110078209,
      208627301,
      712179968,
      8378196,
      2133488385,
      712179968,
      6609848,
      1802466817,
      1684947301,
      221527,
      1802466817,
      185683045,
      1870004480,
      -1571658389,
      2120376686,
      -977993472,
      3867392,
      2032147206,
      538641920,
      185554041,
      106628608,
      40633856,
      2144818277,
      40633856,
      134250341,
      556140544,
      7955044,
      1950361096,
      2032535552,
      638058622,
      638058626
   };

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   TextUtilities(HTMLBrowserContent browserContent, Object appEventLock, RenderingOptions options) {
      this._browserContent = browserContent;
      this._defaultLinkColor = 4856319;
      this._linkColor = this._defaultLinkColor;
      this._defaultParagDir = RichText.getDefaultParagDirection() == 0 ? 0 : 8;
      this._currentDirection = this._defaultParagDir;
      this._stringEnum = (StringPatternEnumerator)(new Object(null, filterStringPatterns(StringPatternRepository$Internal.getStringPatterns())));
      this._minimumFontHeight = Ui.convertSize(options.getPropertyWithIntValue(4550690918222697397L, 35, 6), 2, 0);
      this._minimumFontStyle = options.getPropertyWithIntValue(4550690918222697397L, 36, 0);
      int defaultFontHeight = Ui.convertSize(options.getPropertyWithIntValue(4550690918222697397L, 32, 8), 2, 0);
      this._fontSizes = new int[8];
      this._fontSizes[0] = (defaultFontHeight * 3 + 3) / 5;
      this._fontSizes[1] = (defaultFontHeight * 3 + 2) / 4;
      this._fontSizes[2] = (defaultFontHeight * 8 + 5) / 9;
      this._fontSizes[3] = defaultFontHeight;
      this._fontSizes[4] = (defaultFontHeight * 6 + 3) / 5;
      this._fontSizes[5] = (defaultFontHeight * 3 + 1) / 2;
      this._fontSizes[6] = defaultFontHeight * 2;
      this._fontSizes[7] = defaultFontHeight * 3;
      boolean var7 = false /* VF: Semaphore variable */;

      label27:
      try {
         var7 = true;
         this._defaultFont = FontFamily.forName(options.getPropertyWithStringValue(4550690918222697397L, 31, "BBMillbank"))
            .getFont(this._minimumFontStyle, options.getPropertyWithIntValue(4550690918222697397L, 32, 8), 2);
         var7 = false;
      } finally {
         if (var7) {
            this._defaultFont = Font.getDefault();
            break label27;
         }
      }

      this._fontCache = FontCache.getInstance();
      this._autoMatch = true;
      this._alignmentStack = (IntStack)(new Object());
      this._listIndexStack = (IntStack)(new Object());
      this._listTypeStack = (IntStack)(new Object());
      this._listImageStack = (Stack)(new Object());
      this._fontVariantStack = (IntStack)(new Object());
      this._fontWeightStack = (IntStack)(new Object());
      this._directionStack = (IntStack)(new Object());
      this._textDecorationStack = (IntStack)(new Object());
      this._textTransformStack = (IntStack)(new Object());
      this._cachedColorValues = (LongIntHashtable)(new Object());
      this._foregroundHSLCache = new int[3];
      this._backgroundHSLCache = new int[3];
      this._appEventLock = appEventLock;
   }

   private static final StringPatternContainer filterStringPatterns(StringPatternContainer stringPatternContainer) {
      if (stringPatternContainer == null) {
         return (StringPatternContainer)(new Object(new Object[0]));
      }

      int size = stringPatternContainer.size();
      StringPattern[] stringPatterns = new Object[size];
      int newPatternCount = 0;

      for (int i = 0; i < size; i++) {
         StringPattern stringPattern = (StringPattern)stringPatternContainer.getAt(i);
         long matchID = stringPattern.getPatternTypeIdentifier();
         if (matchID == 3797587162219887872L || matchID == -2985347935260258684L || matchID == 4246852237058296601L || matchID == 532879436795165891L) {
            stringPatterns[newPatternCount++] = stringPattern;
         }
      }

      Array.resize(stringPatterns, newPatternCount);
      return (StringPatternContainer)(new Object(stringPatterns));
   }

   final void setDefaultColors(int linkColor) {
      if (linkColor != -1) {
         this._defaultLinkColor = linkColor;
         this._linkColor = this._defaultLinkColor;
      }
   }

   final void setTextFlowManager(TextFlowManager mgr) {
      this._textFlowManager = mgr;
   }

   final void setRenderingEncodingHint(String encoding) {
      this._renderingEncoding = encoding;
   }

   final void setAutoMatch(boolean autoMatch) {
      this._autoMatch = autoMatch;
   }

   final Font getDefaultFont() {
      return this._defaultFont;
   }

   final int getLinkColor() {
      return this._linkColor;
   }

   final void setFontStyle(HTMLGenericElement element, int style) {
      if (style == 0) {
         element.setFontStyle(element.getFontStyle() & -3);
      } else {
         if (style == 1 || style == 2) {
            element.setFontStyle(element.getFontStyle() | 2);
         }
      }
   }

   final void setFontVariant(HTMLGenericElement element, int variant) {
      this._fontVariantStack.push(variant);
      if (variant == 134) {
         element.setFontFamily(SMALL_CAPS_FONT_FACE);
      }
   }

   final void resetFontVariant() {
      if (!this._fontVariantStack.isEmpty()) {
         this._fontVariantStack.pop();
      }
   }

   final void setFontWeight(HTMLGenericElement element, int weight) {
      int currentWeight = 400;
      if (!this._fontWeightStack.isEmpty()) {
         currentWeight = this._fontWeightStack.peek();
      }

      switch (weight) {
         case 0:
            break;
         case 1:
         default:
            if (currentWeight < 700) {
               weight = 700;
            } else if (currentWeight < 900) {
               weight = 900;
            } else {
               weight = Math.min(currentWeight + 100, 900);
            }
            break;
         case 2:
            if (currentWeight >= 900) {
               weight = 800;
            } else if (currentWeight >= 700) {
               weight = 600;
            } else {
               weight = Math.max(currentWeight - 100, 100);
            }
      }

      this._fontWeightStack.push(weight);
      if (this._minimumFontStyle == 1 && weight < 700) {
         weight = 700;
      } else if (this._minimumFontStyle == 64 && weight < 900) {
         weight = 900;
      }

      if (weight >= 900) {
         element.setFontStyle(element.getFontStyle() | 64);
      } else {
         if (weight >= 700) {
            element.setFontStyle(element.getFontStyle() | 1);
         }
      }
   }

   final void resetFontWeight() {
      if (!this._fontWeightStack.isEmpty()) {
         this._fontWeightStack.pop();
      }
   }

   final void setTextDecoration(HTMLGenericElement element, int decoration) {
      boolean blinking = false;
      if (!this._textDecorationStack.isEmpty()) {
         blinking = (this._textDecorationStack.peek() & 8) != 0;
      }

      this._textDecorationStack.push(decoration);
      int styleOr = 0;
      if ((decoration & 1) != 0) {
         styleOr |= 4;
      }

      if ((decoration & 4) != 0) {
         styleOr |= 32;
      }

      element.setFontStyle(element.getFontStyle() | styleOr);
      if ((decoration & 8) != 0) {
         if (!blinking) {
            synchronized (this._appEventLock) {
               this._textFlowManager.pushCell(Display.getWidth(), 16, 500, 0, Integer.MAX_VALUE, null);
               return;
            }
         }
      } else if (blinking) {
         synchronized (this._appEventLock) {
            this._textFlowManager.popCell();
            return;
         }
      }
   }

   final void resetTextDecoration() {
      boolean blinking = false;
      int decoration = 0;
      if (!this._textDecorationStack.isEmpty()) {
         blinking = (this._textDecorationStack.pop() & 8) != 0;
         if (!this._textDecorationStack.isEmpty()) {
            decoration = this._textDecorationStack.peek();
         }
      }

      if ((decoration & 8) != 0) {
         if (!blinking) {
            synchronized (this._appEventLock) {
               this._textFlowManager.pushCell(Display.getWidth(), 16, 500, 0, Integer.MAX_VALUE, null);
               return;
            }
         }
      } else if (blinking) {
         synchronized (this._appEventLock) {
            this._textFlowManager.popCell();
            return;
         }
      }
   }

   final void setTextTransform(int transform) {
      this._textTransformStack.push(transform);
      this._currentTextTransform = transform;
   }

   final void resetTextTransform() {
      this._currentTextTransform = 97;
      if (!this._textTransformStack.isEmpty()) {
         this._textTransformStack.pop();
         if (!this._textTransformStack.isEmpty()) {
            this._currentTextTransform = this._textTransformStack.peek();
         }
      }
   }

   final void setSubscript(boolean on) {
   }

   final void setSuperscript(boolean on) {
   }

   final Font deriveFont(HTMLGenericElement element) {
      return this._fontCache.getFont(this._defaultFont, element.getFontStyle(), element.getFontHeight(), 0, this._renderingEncoding, element.getFontFamily());
   }

   final void setFont(HTMLGenericElement element) {
      if (element.getFont() == null) {
         element.setFont(this.deriveFont(element));
      }
   }

   final int getCurrentFontXHeight(HTMLGenericElement element) {
      Font f = this.deriveFont(element);
      GlyphMetrics metrics = (GlyphMetrics)(new Object());
      return f.getGlyphMetrics((char)120, metrics) == 0 ? metrics.iBitmapHeight : f.getHeight() / 2;
   }

   final void setFontSize(HTMLGenericElement element, int size) {
      this.setFontSize(element, MathUtilities.clamp(0, size, 7), false);
   }

   final void setFontSize(HTMLGenericElement element, int size, boolean pixels) {
      if (!pixels) {
         if (size >= 0 && size <= 7) {
            size = this._fontSizes[size];
         } else if (size == 9 || size == 8) {
            int currentSize = element.getFontHeight();
            int index = Arrays.binarySearch(this._fontSizes, currentSize);
            if (size == 9) {
               if (index >= 0 && index < 7) {
                  size = this._fontSizes[index + 1];
               } else if (this._fontSizes[0] > currentSize) {
                  size = this._fontSizes[0];
               } else if (this._fontSizes[7] <= currentSize) {
                  size = (currentSize / this._fontSizes[3] + 1) * this._fontSizes[3];
               }
            } else if (size == 8) {
               if (index > 0 && index <= 7) {
                  size = this._fontSizes[index - 1];
               } else if (this._fontSizes[0] >= currentSize) {
                  size = this._minimumFontHeight;
               } else if (this._fontSizes[7] < currentSize) {
                  size = (currentSize / this._fontSizes[3] - 1) * this._fontSizes[3];
               }
            }
         }
      }

      if (size < this._minimumFontHeight) {
         size = this._minimumFontHeight;
      }

      element.setFontHeight(size);
   }

   final void setFontFace(HTMLGenericElement element, String face) {
      if (!this._fontVariantStack.isEmpty() && this._fontVariantStack.peek() == 134) {
         face = SMALL_CAPS_FONT_FACE;
      }

      if (face != null) {
         element.setFontFamily(face);
      }
   }

   final void setAlignment(HTMLGenericElement element, int alignment) {
      if (alignment != -1) {
         switch (alignment) {
            case 7:
               break;
            case 8:
               element.setAlignment((short)1);
               break;
            case 9:
               element.setAlignment((short)3);
               return;
            case 10:
            default:
               element.setAlignment((short)2);
               return;
         }
      }
   }

   final void setDirection(HTMLGenericElement element, int direction, boolean append) {
      synchronized (this._appEventLock) {
         if (direction != -1) {
            this._currentDirection = direction;
            element.setDirection((short)direction);
         }

         this._directionStack.push(direction);
         if (append && direction != -1) {
            if (direction == 0) {
               this.appendDirectionCharacter('\u202a');
            } else {
               this.appendDirectionCharacter('\u202b');
            }
         }
      }
   }

   final int getAlignment() {
      return !this._alignmentStack.isEmpty() ? this._alignmentStack.peek() : 8;
   }

   final void resetAlignment() {
      this.resetAlignment(true);
   }

   final void resetAlignment(boolean forceBreak) {
      if (!this._alignmentStack.isEmpty()) {
         this._alignmentStack.pop();
      }

      if (!this._alignmentStack.isEmpty()) {
         this.updateAlignment(this._alignmentStack.peek(), forceBreak);
      } else {
         this.updateAlignment(-1, forceBreak);
      }
   }

   final void resetDirection(boolean append) {
      synchronized (this._appEventLock) {
         int oldDirection = -1;
         if (!this._directionStack.isEmpty()) {
            oldDirection = this._directionStack.pop();
         }

         if (append && oldDirection != -1) {
            this.appendDirectionCharacter('\u202c');
         }

         this._currentDirection = -1;

         for (int i = this._directionStack.size() - 1; i >= 0 && this._currentDirection == -1; i--) {
            this._currentDirection = this._directionStack.elementAt(i);
         }

         if (this._currentDirection == -1) {
            this._currentDirection = this._defaultParagDir;
         }
      }
   }

   private final void updateAlignment(int alignment, boolean forceBreak) {
      synchronized (this._appEventLock) {
         ;
      }
   }

   final void startAnchor(HTMLElement element, String href) {
      if (element != null && href != null) {
         this._anchorId = LinkType.getLinkType(href);
         this._currentFocusCookie = element;
         synchronized (this._appEventLock) {
            this._textFlowManager.startFocusRegion(this._currentFocusCookie, this._anchorId);
         }
      }
   }

   final long getCurrentAnchorId() {
      return this._anchorId;
   }

   final void endAnchor() {
      if (this._currentFocusCookie != null) {
         synchronized (this._appEventLock) {
            if (this._textFlowManager.isFocusRegionOpen()) {
               this._textFlowManager.endFocusRegion();
            }
         }

         this._anchorId = 0;
         this._currentFocusCookie = null;
      }
   }

   final int startList(int listType, int bulletStyle, String bulletUrl, int start) {
      if (bulletStyle == 34) {
         label13:
         switch (listType & 0xFF) {
            case 29:
            case 60:
            case 95:
               switch (this._listTypeStack.size()) {
                  case -1:
                     bulletStyle = 140;
                     break label13;
                  case 0:
                  default:
                     bulletStyle = 36;
                     break label13;
                  case 1:
                     bulletStyle = 24;
                     break label13;
               }
            case 65:
               bulletStyle = 32;
         }
      }

      this._listTypeStack.push(bulletStyle);
      this._listImageStack.push(bulletUrl);
      this._listIndexStack.push(start - 1);
      return this._listTypeStack.size();
   }

   final int endList() {
      if (!this._listIndexStack.isEmpty()) {
         this._listIndexStack.pop();
      }

      if (!this._listTypeStack.isEmpty()) {
         this._listTypeStack.pop();
      }

      if (!this._listImageStack.isEmpty()) {
         this._listImageStack.pop();
      }

      return this._listTypeStack.size();
   }

   final String addListItem(int value, int bulletStyle, boolean append) {
      if (this._listTypeStack.isEmpty()) {
         return null;
      }

      if (bulletStyle == 34) {
         bulletStyle = this._listTypeStack.peek();
      }

      int index = 1;
      int depth = this._listIndexStack.size();
      if (depth > 0) {
         index = this._listIndexStack.pop() + 1;
      }

      if (value != Integer.MAX_VALUE) {
         index = value;
      }

      this._listIndexStack.push(index);
      String text = convertListItemValue(index, bulletStyle);
      if (append) {
         synchronized (this._appEventLock) {
            this._textFlowManager.appendText(text);
         }
      }

      return text;
   }

   final String getLastListImage() {
      return (String)(!this._listImageStack.isEmpty() ? this._listImageStack.peek() : null);
   }

   private final String transformText(String text) {
      switch (this._currentTextTransform) {
         case 18:
            int[] offsets = new int[(text.length() >> 1) + 1];
            int size = StringUtilities.stringToWords(text, offsets, 0);
            char[] buffer = text.toCharArray();

            for (int i = 0; i < size; i++) {
               buffer[offsets[i]] = CharacterUtilities.toUpperCase(buffer[offsets[i]]);
            }

            return (String)(new Object(buffer));
         case 82:
            return text.toLowerCase();
         case 166:
            return text.toUpperCase();
         default:
            return text;
      }
   }

   final void startFocusRegionIfRequired() {
      synchronized (this._appEventLock) {
         if (this._currentFocusCookie != null && !this._textFlowManager.isFocusRegionOpen()) {
            this._textFlowManager.startFocusRegion(this._currentFocusCookie, this._anchorId);
         }
      }
   }

   final void appendText(HTMLGenericElement currentElement, String text) {
      if (text.length() != 0) {
         text = this.transformText(text);
         if (this._currentFocusCookie != null) {
            this.startFocusRegionIfRequired();
            this._textFlowManager.appendText(text);
         } else {
            this.appendTextWithScan(currentElement, text, 0, text.length());
         }
      }
   }

   final void appendDirectionCharacter(char aChar) {
      this._textFlowManager.appendText(String.valueOf(aChar));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void appendText(HTMLGenericElement currentElement, int index, String[] strings, byte[] propertiesData, boolean appendNewLine) {
      String text = strings[index];
      if (text.length() != 0) {
         if (appendNewLine) {
            this._textFlowManager.appendText("\n");
         }

         if (propertiesData != null && propertiesData.length != 0) {
            if (this._properties == null) {
               this._properties = (DataBuffer)(new Object());
            }

            boolean focusRegionOpen = false;
            boolean var30 = false /* VF: Semaphore variable */;

            label344: {
               label356: {
                  label357: {
                     try {
                        try {
                           var30 = true;
                           this._properties.setData(propertiesData, 0, propertiesData.length, true);
                           if (this._properties.readByte() != 1) {
                              this._textFlowManager.appendText(text);
                              var30 = false;
                              break label344;
                           }

                           int regionCount = this._properties.readShort();
                           if (regionCount <= 0) {
                              this._textFlowManager.appendText(text);
                              var30 = false;
                              break label356;
                           }

                           this._properties.readShort();
                           short alignment = 0;
                           switch (this._properties.readUnsignedByte()) {
                              case 7:
                                 break;
                              case 8:
                                 alignment = 1;
                                 break;
                              case 9:
                                 alignment = 3;
                                 break;
                              case 10:
                              default:
                                 alignment = 2;
                           }

                           String fontFace = null;
                           int regionStart = this._properties.readCompressedInt();
                           int lastRegionIndex = regionCount - 1;
                           int lastRegionReference = -1;
                           long lastRegionReferenceId = -1;

                           for (int regionIndex = 0; regionIndex <= lastRegionIndex; regionIndex++) {
                              HTMLGenericElement element = new HTMLBaseGenericElement(4, null, 0);
                              element.inherit(currentElement);
                              this._textFlowManager.pushRegion(element);
                              element.setAlignment(alignment);
                              int regionReference = -1;
                              int regionSize = 3;
                              int regionAttributes = this._properties.readCompressedInt();
                              int fontStyle = regionAttributes & 15;
                              if ((regionAttributes >> 3 & 1) != 0) {
                                 regionReference = this._properties.readCompressedInt();
                                 element._foregroundColour = this.getContrastColour(element, this._defaultLinkColor);
                                 fontStyle &= -9;
                                 fontStyle |= 4;
                              }

                              if ((regionAttributes >> 4 & 1) != 0) {
                                 regionSize = this._properties.readCompressedInt();
                              }

                              if ((regionAttributes >> 9 & 1) != 0) {
                                 element._foregroundColour = this.getContrastColour(element, this._properties.readCompressedInt());
                              }

                              if ((regionAttributes >> 10 & 1) != 0) {
                                 this._properties.readCompressedInt();
                              }

                              if ((regionAttributes >> 11 & 1) != 0) {
                                 fontFace = strings[this._properties.readCompressedInt()];
                              } else {
                                 fontFace = null;
                              }

                              if (fontFace != null) {
                                 element.setFontFamily(fontFace);
                              }

                              int regionEnd = regionIndex != lastRegionIndex ? this._properties.readCompressedInt() : text.length();
                              int regionLength = regionEnd - regionStart;
                              if (regionLength > 0) {
                                 if (regionReference != -1) {
                                    if (focusRegionOpen && regionReference != lastRegionReference) {
                                       this._textFlowManager.endFocusRegion();
                                       focusRegionOpen = false;
                                       lastRegionReference = -1;
                                    }

                                    String href = strings[regionReference];
                                    if (this._autoMatch) {
                                       long linkType = LinkType.getLinkType(href);
                                       if (linkType != lastRegionReferenceId && focusRegionOpen) {
                                          this._textFlowManager.endFocusRegion();
                                          focusRegionOpen = false;
                                          lastRegionReferenceId = -1;
                                       }

                                       if (!focusRegionOpen) {
                                          this._textFlowManager.startFocusRegion(href, linkType);
                                          focusRegionOpen = true;
                                          lastRegionReferenceId = linkType;
                                       }
                                    } else if (!focusRegionOpen) {
                                       this._textFlowManager.startFocusRegion(strings[regionReference], 5019899335844518230L);
                                       focusRegionOpen = true;
                                       lastRegionReferenceId = 5019899335844518230L;
                                    }
                                 } else if (focusRegionOpen) {
                                    this._textFlowManager.endFocusRegion();
                                    focusRegionOpen = false;
                                    lastRegionReferenceId = -1;
                                    lastRegionReference = -1;
                                 }

                                 this.setFontSize(element, regionSize);
                                 element.setFontStyle(fontStyle);
                                 if (regionReference != -1) {
                                    this._textFlowManager.appendText(text, regionStart, regionLength);
                                    lastRegionReference = regionReference;
                                 } else {
                                    this.appendTextWithScan(element, text, regionStart, regionLength);
                                 }
                              }

                              regionStart = regionEnd;
                              this._textFlowManager.popRegion();
                           }

                           var30 = false;
                        } catch (Throwable var33) {
                           if (focusRegionOpen) {
                              this._textFlowManager.appendText(" ");
                              this._textFlowManager.endFocusRegion();
                              focusRegionOpen = false;
                           }

                           this._textFlowManager.appendText(text);
                           RIMGlobalMessagePoster.postGlobalEvent(-2269441167196113981L, 0, 0, anException, null);
                           var30 = false;
                           break label357;
                        }
                     } finally {
                        if (var30) {
                           if (focusRegionOpen) {
                              this._textFlowManager.endFocusRegion();
                           }
                        }
                     }

                     if (focusRegionOpen) {
                        this._textFlowManager.endFocusRegion();
                        return;
                     }

                     return;
                  }

                  if (focusRegionOpen) {
                     this._textFlowManager.endFocusRegion();
                     return;
                  }

                  return;
               }

               if (focusRegionOpen) {
                  this._textFlowManager.endFocusRegion();
               }

               return;
            }

            if (focusRegionOpen) {
               this._textFlowManager.endFocusRegion();
            }
         } else {
            this._textFlowManager.appendText(text);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void appendTextWithScan(HTMLGenericElement currentElement, String text, int regionStart, int regionLength) {
      int regionEnd = regionStart + regionLength;
      if (this._autoMatch) {
         this._stringEnum.reset(text, regionStart, regionEnd);

         while (true) {
            boolean hasMatch = this._stringEnum.hasMoreMatches();
            if (!hasMatch) {
               break;
            }

            this._stringEnum.nextMatch(this._stringMatch);
            if (regionStart < this._stringMatch.beginIndex) {
               this._textFlowManager.appendText(text, regionStart, this._stringMatch.beginIndex - regionStart);
               regionStart = this._stringMatch.beginIndex;
            }

            HTMLGenericElement element = new HTMLBaseGenericElement(4, null, 0);
            element.inherit(currentElement);
            element.setFontStyle(element.getFontStyle() | 4 | 8);
            this.setFont(element);
            this._textFlowManager.pushRegion(element);
            this.adjustToContrastColour(element);
            String url = text.substring(regionStart, this._stringMatch.endIndex);
            if (this._stringMatch.id == 5019899335844518230L) {
               String tempUrl = null;
               if (this._browserContent != null) {
                  tempUrl = URI.getAbsoluteURL(url, null);
               }

               if (tempUrl != null) {
                  url = tempUrl;
               }
            }

            boolean focusOpen = false;
            boolean var12 = false /* VF: Semaphore variable */;

            try {
               var12 = true;
               this._textFlowManager.startFocusRegion(url, this._stringMatch.id);
               focusOpen = true;
               this._textFlowManager.appendText(text, regionStart, this._stringMatch.endIndex - regionStart);
               var12 = false;
            } finally {
               if (var12) {
                  if (focusOpen) {
                     this._textFlowManager.endFocusRegion();
                  }
               }
            }

            if (focusOpen) {
               this._textFlowManager.endFocusRegion();
            }

            this._textFlowManager.popRegion();
            regionStart = this._stringMatch.endIndex;
         }
      }

      if (regionStart < regionEnd) {
         this._textFlowManager.appendText(text, regionStart, regionEnd - regionStart);
      }
   }

   public final void adjustToContrastColour(HTMLGenericElement element) {
      if (element._foregroundColour != -1) {
         element._foregroundColour = this.getContrastColour(element, element._foregroundColour);
      }
   }

   public final int getContrastColour(HTMLGenericElement element, int originalColour) {
      if (!IS_COLOR) {
         return 0;
      }

      if (this._textFlowManager.isBackgroundImageSet()) {
         return originalColour;
      }

      int currentBackground = element._backgroundColour;
      if (currentBackground == -1) {
         currentBackground = 16777215;
      }

      return this.getContrastColour(originalColour, currentBackground);
   }

   public final int getContrastColour(int originalColor, int backgroundColor) {
      if (originalColor != -1 && backgroundColor != -1) {
         long key = backgroundColor & 4294967295L | (long)originalColor << 32;
         if (this._cachedColorValues.containsKey(key)) {
            return this._cachedColorValues.get(key);
         }

         this.RGBtoHSL(backgroundColor, this._backgroundHSLCache);
         this.RGBtoHSL(originalColor, this._foregroundHSLCache);
         int satDiff = this._backgroundHSLCache[1] - this._foregroundHSLCache[1];
         if (this._backgroundHSLCache[2] >= 250 || this._foregroundHSLCache[2] >= 250) {
            satDiff = 0;
         }

         if (this._foregroundHSLCache[1] > 26 && satDiff >= 0 && satDiff < 64) {
            this._foregroundHSLCache[1] = this._backgroundHSLCache[1] - 64;
         } else if (this._foregroundHSLCache[1] > 26 && satDiff < 0 && satDiff > -64) {
            this._foregroundHSLCache[1] = this._backgroundHSLCache[1] + 64;
         }

         int lightDiff = this._backgroundHSLCache[2] - this._foregroundHSLCache[2];
         if (lightDiff >= 0 && lightDiff < 64) {
            this._foregroundHSLCache[2] = this._backgroundHSLCache[2] - 64;
         } else if (lightDiff < 0 && lightDiff > -64) {
            this._foregroundHSLCache[2] = this._backgroundHSLCache[2] + 64;
         }

         for (int i = 0; i < 3; i++) {
            if (this._foregroundHSLCache[i] >= 256) {
               this._foregroundHSLCache[i] = this._foregroundHSLCache[i] - 256;
            } else if (this._foregroundHSLCache[i] < 0) {
               this._foregroundHSLCache[i] = this._foregroundHSLCache[i] + 256;
            }
         }

         int result = this.HSLtoRGB(this._foregroundHSLCache);
         this._cachedColorValues.put(key, result);
         return result;
      } else {
         return originalColor;
      }
   }

   private final void RGBtoHSL(int rgb, int[] result) {
      int red = rgb >> 16 & 0xFF;
      int green = rgb >> 8 & 0xFF;
      int blue = rgb & 0xFF;
      int min = Math.min(red, Math.min(green, blue));
      int max = Math.max(red, Math.max(green, blue));
      int l = (max + min + 1) / 2;
      int range = max - min;
      int s;
      int h;
      if (max == min) {
         s = 0;
         h = 0;
      } else {
         if (l < 128) {
            int div = max + min;
            s = (255 * range + (div >> 1)) / div;
         } else {
            int div = 510 - max - min;
            s = (255 * range + (div >> 1)) / div;
         }

         if (red == max) {
            h = 60 * (green - blue);
         } else if (green == max) {
            h = 120 * range + 60 * (blue - red);
         } else {
            h = 240 * range + 60 * (red - green);
         }

         h /= range;
         if (h < 0) {
            h += 360;
         }
      }

      result[0] = (255 * h + 180) / 360;
      result[1] = s;
      result[2] = l;
   }

   private final int HSLtoRGB(int[] hsl) {
      int hue = hsl[0];
      int sat = hsl[1];
      int light = hsl[2];
      int r = 0;
      int g = 0;
      int b = 0;
      if (sat == 0) {
         r = g = b = hsl[2];
      } else {
         int temp2;
         if (light < 128) {
            temp2 = (light * (255 + sat) + 127) / 255;
         } else {
            temp2 = light + sat - (light * sat + 127) / 255;
         }

         int temp1 = (512 * light + 128) / 256 - temp2;
         if (temp1 < 0) {
            temp1 = 0;
         }

         for (int i = 0; i < 3; i++) {
            int temp3 = 0;
            switch (i) {
               case -1:
                  break;
               case 0:
               default:
                  temp3 = hue + 85;
                  break;
               case 1:
                  temp3 = hue;
                  break;
               case 2:
                  temp3 = hue - 85;
            }

            if (temp3 < 0) {
               temp3 += 256;
            } else if (temp3 >= 256) {
               temp3 -= 256;
            }

            int color;
            if (temp3 < 42) {
               color = temp1 + (((temp2 - temp1) * 1536 * temp3 + 128) / 256 + 128) / 256;
            } else if (temp3 < 128) {
               color = temp2;
            } else if (temp3 < 170) {
               color = temp1 + (((temp2 - temp1) * (170 - temp3) * 1536 + 128) / 256 + 128) / 256;
            } else {
               color = temp1;
            }

            switch (i) {
               case -1:
                  break;
               case 0:
               default:
                  r = color;
                  break;
               case 1:
                  g = color;
                  break;
               case 2:
                  b = color;
            }
         }
      }

      return r << 16 | g << 8 | b;
   }

   private static final String convertListItemValue(int value, int type) {
      if (value <= 0) {
         type = 32;
      }

      StringBuffer result = (StringBuffer)(new Object());
      boolean appendDot = true;
      switch (type) {
         case 24:
            result.append('◦');
            appendDot = false;
            break;
         case 33:
            NumberUtilities.appendNumber(result, value, 10, 2);
            break;
         case 36:
            result.append('•');
            appendDot = false;
            break;
         case 79:
            appendCharacterValueFromInteger(result, value, 'α', 24, type);
            break;
         case 80:
         case 181:
            appendCharacterValueFromInteger(result, value, 'a', 26, type);
            break;
         case 81:
         case 165:
            for (int i = 0; i < ROMAN_VALUES.length; i++) {
               while (ROMAN_VALUES[i] <= value) {
                  String str = ROMAN_CHARS[i];
                  if (type == 165) {
                     str = StringUtilities.toUpperCase(str, 1701707776);
                  }

                  result.append(str);
                  value -= ROMAN_VALUES[i];
               }
            }
            break;
         case 97:
            appendDot = false;
            break;
         case 140:
            result.append('▪');
            appendDot = false;
            break;
         case 164:
         case 182:
            appendCharacterValueFromInteger(result, value, 'A', 26, type);
            break;
         default:
            NumberUtilities.appendNumber(result, value);
      }

      if (appendDot) {
         result.append('.');
      }

      result.append(' ');
      return result.toString();
   }

   private static final void appendCharacterValueFromInteger(StringBuffer result, int value, char startChar, int numLetters, int type) {
      value--;

      while (value >= 0) {
         int divisor = value / numLetters;
         int remainder = value - divisor * numLetters;
         char theChar = (char)(remainder + startChar);
         if (type == 79 && theChar > 961) {
            theChar++;
         }

         result.append(theChar);
         value = divisor - 1;
      }

      result.reverse();
   }
}
