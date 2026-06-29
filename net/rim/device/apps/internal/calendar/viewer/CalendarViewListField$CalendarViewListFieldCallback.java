package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.accessibility.AccessibleContext;

interface CalendarViewListField$CalendarViewListFieldCallback extends AccessibleContext {
   void layout(Object var1, int var2, int var3);

   void drawFocus(Object var1, Graphics var2, boolean var3);

   int getPreferredWidth(Object var1);

   int getRowHeight(Object var1, int var2);

   void drawListRow(Object var1, Graphics var2, int var3, int var4, int var5, int var6);

   boolean allowFocusAt(Object var1, int var2);

   void focusMoved(Object var1, int var2, int var3, int var4, int var5);

   String getEmptyString(Object var1);

   int getBackgroundColor();
}
