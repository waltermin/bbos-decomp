package net.rim.plazmic.internal.mediaengine.service.node;

public interface TextAttrNode {
   int FONT_STYLE_NORMAL = 1;
   int FONT_STYLE_ITALIC = 2;
   int FONT_STYLE_OBLIQUE = 3;
   int DEFAULT_FONT_STYLE = 1;
   int TEXT_ANCHOR_START = 1;
   int TEXT_ANCHOR_MIDDLE = 2;
   int TEXT_ANCHOR_END = 3;
   int DEFAULT_TEXT_ANCHOR = 1;
   int DECORATION_NONE = 1;
   int DECORATION_UNDERLINE = 2;
   int DECORATION_UNDERLINE_DOTTED = 3;
   int DECORATION_UNDERLINE_BROKEN_LINE = 4;
   int DECORATION_LINE_THROUGH = 5;
   int DEFAULT_DECORATION = 1;
   int FONT_WEIGHT_100 = 100;
   int FONT_WEIGHT_200 = 200;
   int FONT_WEIGHT_300 = 300;
   int FONT_WEIGHT_400 = 400;
   int FONT_WEIGHT_500 = 500;
   int FONT_WEIGHT_600 = 600;
   int FONT_WEIGHT_700 = 700;
   int FONT_WEIGHT_800 = 800;
   int FONT_WEIGHT_900 = 900;
   int FONT_WEIGHT_PLAIN = 400;
   int FONT_WEIGHT_BOLD = 700;
   int FONT_WEIGHT_BOLDER = 900;
   int FONT_WEIGHT_LIGHTER = 100;
   int DEFAULT_FONT_WEIGHT = 400;
   int DEFAULT_FONT_SIZE = 12;
   String DEFAULT_FONT_FAMILY = "BBMillbank";
   int DEFAULT_FONT_FAMILY_CODE = -1;

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
