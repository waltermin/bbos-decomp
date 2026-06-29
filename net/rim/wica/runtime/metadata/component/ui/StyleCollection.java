package net.rim.wica.runtime.metadata.component.ui;

public interface StyleCollection {
   int COLOR_BACKGROUND = 0;
   int COLOR_FOREGROUND = 1;
   int FONT_FAMILY = 2;
   int FONT_SIZE = 3;
   int FONT_STYLE_BOLD = 4;
   int FONT_STYLE_ITALIC = 5;
   int FONT_STYLE_UNDERLINE = 7;
   int BACKGROUND_IMAGE_ID = 8;

   boolean hasProperty(int var1, int var2);

   int getIntProperty(int var1, int var2);

   String getStringProperty(int var1, int var2);
}
