package net.rim.device.api.ui.theme;

import net.rim.device.api.system.Bitmap;
import net.rim.device.resources.Resource;

class RemoteThemeAttributeSetWriter {
   private String _elementName;
   private final Resource _themeResource;
   private final ResourceFetcher _resourceFetcher;
   private final Theme$Writer _themeWriter;
   private final Theme _parentTheme;
   private ThemeAttributeSet$Writer _stockWriter;
   private static final String DEFAULT_ATTRIBUTE_SET_NAME = "";
   private static final String DEFAULT_FONT_ATTRIBUTE_SET_NAME = "default-font";
   private static final String CLIENT_SET_NAME = "client";

   public RemoteThemeAttributeSetWriter(Resource themeResource, ResourceFetcher resourceFetcher, Theme$Writer themeWriter, Theme parentTheme) {
      this._themeResource = themeResource;
      this._resourceFetcher = resourceFetcher;
      this._themeWriter = themeWriter;
      this._parentTheme = parentTheme;
      this._stockWriter = null;
   }

   public Theme$Writer getThemeWriter() {
      return this._themeWriter;
   }

   public boolean isOpen() {
      return this._stockWriter != null;
   }

   public void startElement(String element) {
      if (this.isOpen()) {
         throw new IllegalStateException("startElement called on already opened element");
      }

      this._stockWriter = this._themeWriter.createThemeAttributeSetWriter(element, this._resourceFetcher);
      this._elementName = element;
      if (this._stockWriter != null && this._stockWriter.getThemeAttributeSet() != null) {
         if (!"".equals(element) && !"client".equals(element)) {
            this.copyParentAttributes("default-font");
         }

         this.copyParentAttributes(this._elementName);
      }
   }

   private void copyParentAttributes(String srcElementName) {
      ThemeAttributeSet srcAttributes;
      if ((srcAttributes = this.getParentAttributeSet(srcElementName)) != null) {
         this.copyAttributes(srcAttributes);
      }
   }

   public void endElement() {
      this.checkState();
      this._themeWriter.put(this._stockWriter);
      this._stockWriter = null;
   }

   public void resetElement(String element) {
      this.startElement(element);
      if (this.isOpen()) {
         this.endElement();
      }
   }

   public String getElementName() {
      return this._elementName;
   }

   public void setBackgroundImage(String name) {
      this.checkState();
      byte[] imageBytes = this._themeResource.getResource(name);
      if (imageBytes != null) {
         this._stockWriter.setBackgroundImage(name);
      }
   }

   public void setBackgroundImage(Bitmap bitmap) {
      this.checkState();
      this._stockWriter.setBackgroundImage(bitmap);
   }

   public void setBorder(String borderName) {
      this.checkState();
      this.clearBackgroundImage();
      this._stockWriter.setBorder(borderName);
   }

   private void clearBackgroundImage() {
      ThemeAttributeSet tas = this._stockWriter.getThemeAttributeSet();
      tas._background = null;
   }

   public void setColor(int colorType, int color) {
      this.checkState();
      this._stockWriter.setColor(colorType, color);
   }

   public void setEdges(int edgeType, int top, int right, int bottom, int left) {
      this.checkState();
      this._stockWriter.setEdges(edgeType, top, right, bottom, left);
   }

   public void setPosition(int x, int y, int width, int height) {
      this.checkState();
      this._stockWriter.setPosition(x, y, width, height);
   }

   public void setFontFamily(String fontFamily) {
      this.checkState();
      this._stockWriter.setFontFamily(fontFamily);
   }

   public void setFontStyle(int fontStyle) {
      this.checkState();
      this._stockWriter.setFontStyle(fontStyle);
   }

   public void setFontAntialiasMode(int fontAntialiasMode) {
      this.checkState();
      this._stockWriter.setFontAntialiasMode(fontAntialiasMode);
   }

   public void setFontSize(int fontSize, int fontSizeUnits) {
      this.checkState();
      this._stockWriter.setFontSize(fontSize, fontSizeUnits, false);
   }

   public void setLayout(String layout) {
      this.checkState();
      this._stockWriter.setLayout(layout);
   }

   public void setLayoutParameters(String[] params) {
      this.checkState();
      this._stockWriter.setLayoutParameters(params);
   }

   private void checkState() {
      if (!this.isOpen()) {
         throw new IllegalStateException("element not open (call startElement first)");
      }
   }

   private static boolean isSet(int flag, int set) {
      return (set & flag) != 0;
   }

   private void copyAttributes(ThemeAttributeSet src) {
      this.checkState();
      ThemeAttributeSet dst = this._stockWriter.getThemeAttributeSet();

      for (int colorType = 0; colorType < src._colors.length; colorType++) {
         if (isSet(1 << 0 + colorType, src._set)) {
            this._stockWriter.setColor(colorType, src._colors[colorType]);
         }
      }

      for (int colorType = 0; colorType < src._palettedColors.length; colorType++) {
         if (isSet(1 << 0 + colorType, src._set)) {
            this._stockWriter.setColor(colorType, src._palettedColors[colorType]);
         }
      }

      if (isSet(256, src._set)) {
         this._stockWriter.setFocusStyle(src._focusStyle);
      }

      if (src._fontFamily != null) {
         this._stockWriter.setFontFamily(src._fontFamily);
      }

      if (src._fontSize != 0) {
         this._stockWriter.setFontSize(src._fontSize, src._fontSizeUnits, false);
      }

      if (src._fontStyle != 0) {
         this._stockWriter.setFontStyle(src._fontStyle);
      }

      if (isSet(2097152, src._set)) {
         this._stockWriter.setTextAlign(src._textAlign);
      }

      if (src._maxLineWrap != 0) {
         this._stockWriter.setMaximumLineWrapping(src._maxLineWrap);
      }

      if (src._backgroundName != null) {
         dst._backgroundName = src._backgroundName;
         dst._backgroundLocation = src._backgroundLocation;
         if (src._backgroundName.length() == 0) {
            dst._set |= 1024;
         } else {
            dst._background = src._background;
            dst._set |= 525312;
         }
      } else if (src._background != null) {
         dst._backgroundLocation = null;
         dst._backgroundName = null;
         dst._background = src._background;
         dst._set |= 525312;
      }

      if (isSet(1048576, src._set)) {
         this._stockWriter.setBackgroundOpacity(src._opacity);
      }

      for (int type = 0; type < 4; type++) {
         if (isSet(1 << 15 + type, src._set)) {
            this._stockWriter.setScrollArrow(type, src._scrollArrowName[type]);
         }
      }

      if (isSet(2048, src._set)) {
         this._stockWriter.setBorder(src._borderName);
      }

      if (isSet(4096, src._set)) {
         this._stockWriter.setEdges(0, src._padding.top, src._padding.right, src._padding.bottom, src._padding.left);
      }

      if (isSet(16384, src._set)) {
         this._stockWriter.setEdges(2, src._margin.top, src._margin.right, src._margin.bottom, src._margin.left);
      }

      if (src._layout != null) {
         this._stockWriter.setLayout(src._layout);
      }

      if (src._layoutParams != null) {
         this._stockWriter.setLayoutParameters(src._layoutParams);
      }

      if (src._position != null) {
         this._stockWriter.setPosition(src._position.x, src._position.y, src._position.width, src._position.height);
      }
   }

   private ThemeAttributeSet getParentAttributeSet(String element) {
      if (this._parentTheme == null) {
         return null;
      }

      if (element == null) {
         return this._parentTheme.getAttributeSet(Tag.get(null));
      }

      int idStart = element.indexOf(35);
      int stateStart = element.indexOf(58);
      Tag tag = toTag(element, idStart, stateStart);
      ThemeAttributeSet result;
      if (tag == null) {
         result = null;
      } else {
         String idName = null;
         String stateName = null;
         if (idStart != -1) {
            if (idStart > stateStart) {
               idName = element.substring(idStart + 1);
            } else {
               idName = element.substring(idStart + 1, stateStart);
            }
         }

         if (stateStart != -1) {
            if (stateStart > idStart) {
               stateName = element.substring(stateStart + 1);
            } else {
               stateName = element.substring(stateStart + 1, idStart);
            }
         }

         result = this._parentTheme.getAttributeSet(tag, idName, getStateForName(stateName));
      }

      return result;
   }

   private static Tag toTag(String element, int idStart, int stateStart) {
      int nameLen = element.length();
      if (idStart != -1 && idStart < nameLen) {
         nameLen = idStart;
      }

      if (stateStart != -1 && stateStart < nameLen) {
         nameLen = stateStart;
      }

      return Tag.get(element.substring(0, nameLen));
   }

   private static int getStateForName(String stateName) {
      int state = 0;
      if ("first-child".equals(stateName)) {
         return 1;
      }

      if ("link".equals(stateName)) {
         return 2;
      }

      if ("visited".equals(stateName)) {
         return 3;
      }

      if ("active".equals(stateName)) {
         return 4;
      }

      if ("hover".equals(stateName)) {
         return 5;
      }

      if ("focus".equals(stateName)) {
         return 6;
      }

      if ("disabled".equals(stateName)) {
         return 7;
      }

      if ("disabled-focus".equals(stateName)) {
         state = 8;
      }

      return state;
   }
}
