package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

interface MoveableListFieldCallback extends ListFieldCallback {
   void drawListRow(ListField var1, Graphics var2, int var3, int var4, int var5, int var6);

   void moveFinished(ListField var1, int var2, int var3);
}
