package net.rim.device.api.browser.plugin;

public interface BrowserPageContext {
   int STYLE_NO_VERTICAL_SCROLLBAR = 2;
   int STYLE_SHOW_IN_FULL_SCREEN = 4;
   int STYLE_OVERLAY_FOOTER = 8;
   int STYLE_SHOW_FOOTER_ALWAYS = 16;
   int STYLE_VERTICAL_SCROLL_ON_LEFT = 32;
   int STYLE_NO_HORIZONTAL_SCROLLBAR = 64;
   int DISPLAY_STYLE = 2;

   boolean getPropertyWithBooleanValue(int var1, boolean var2);

   String getPropertyWithStringValue(int var1, String var2);

   int getPropertyWithIntValue(int var1, int var2);

   Object getPropertyWithObjectValue(int var1, Object var2);
}
