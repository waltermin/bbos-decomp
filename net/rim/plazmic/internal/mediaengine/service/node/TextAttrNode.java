package net.rim.plazmic.internal.mediaengine.service.node;

public interface TextAttrNode {
   int FONT_STYLE_NORMAL;
   int FONT_STYLE_ITALIC;
   int FONT_STYLE_OBLIQUE;
   int DEFAULT_FONT_STYLE;
   int TEXT_ANCHOR_START;
   int TEXT_ANCHOR_MIDDLE;
   int TEXT_ANCHOR_END;
   int DEFAULT_TEXT_ANCHOR;
   int DECORATION_NONE;
   int DECORATION_UNDERLINE;
   int DECORATION_UNDERLINE_DOTTED;
   int DECORATION_UNDERLINE_BROKEN_LINE;
   int DECORATION_LINE_THROUGH;
   int DEFAULT_DECORATION;
   int FONT_WEIGHT_100;
   int FONT_WEIGHT_200;
   int FONT_WEIGHT_300;
   int FONT_WEIGHT_400;
   int FONT_WEIGHT_500;
   int FONT_WEIGHT_600;
   int FONT_WEIGHT_700;
   int FONT_WEIGHT_800;
   int FONT_WEIGHT_900;
   int FONT_WEIGHT_PLAIN;
   int FONT_WEIGHT_BOLD;
   int FONT_WEIGHT_BOLDER;
   int FONT_WEIGHT_LIGHTER;
   int DEFAULT_FONT_WEIGHT;
   int DEFAULT_FONT_SIZE;
   String DEFAULT_FONT_FAMILY;
   int DEFAULT_FONT_FAMILY_CODE;

   int getFontSize();

   void setFontSize(int var1);

   int getFontWeight();

   void setFontWeight(int var1);

   int getFontStyle();

   void setFontStyle(int var1);

   String getFontFamily();

   void setFontFamily(String var1);

   int getTextDecoration();

   void setTextDecoration(int var1);
}
