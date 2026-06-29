package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.browser.util.ImageMap;
import org.w3c.dom.html2.HTMLAnchorElement;

public interface SecondaryURLNode {
   Field getUIField();

   void setUIField(Field var1);

   int getVspace();

   int getHspace();

   int getWidth();

   int getHeight();

   void setWidth(int var1);

   void setHeight(int var1);

   String getSrc();

   int getReplaceTag();

   void setReplaceTag(int var1);

   Object getCookie();

   long getStyle();

   HTMLAnchorElement getLink();

   String getAlt();

   ImageMap getImageMap();
}
