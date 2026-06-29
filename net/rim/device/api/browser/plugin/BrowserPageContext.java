package net.rim.device.api.browser.plugin;

public interface BrowserPageContext {
   int STYLE_NO_VERTICAL_SCROLLBAR;
   int STYLE_SHOW_IN_FULL_SCREEN;
   int STYLE_OVERLAY_FOOTER;
   int STYLE_SHOW_FOOTER_ALWAYS;
   int STYLE_VERTICAL_SCROLL_ON_LEFT;
   int STYLE_NO_HORIZONTAL_SCROLLBAR;
   int DISPLAY_STYLE;

   boolean getPropertyWithBooleanValue(int var1, boolean var2);

   String getPropertyWithStringValue(int var1, String var2);

   int getPropertyWithIntValue(int var1, int var2);

   Object getPropertyWithObjectValue(int var1, Object var2);
}
