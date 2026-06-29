package net.rim.wica.runtime.metadata.component.ui;

public interface StyleCollection {
   int COLOR_BACKGROUND;
   int COLOR_FOREGROUND;
   int FONT_FAMILY;
   int FONT_SIZE;
   int FONT_STYLE_BOLD;
   int FONT_STYLE_ITALIC;
   int FONT_STYLE_UNDERLINE;
   int BACKGROUND_IMAGE_ID;

   boolean hasProperty(int var1, int var2);

   int getIntProperty(int var1, int var2);

   String getStringProperty(int var1, int var2);
}
