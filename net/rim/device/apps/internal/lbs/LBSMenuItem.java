package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public class LBSMenuItem extends MenuItem {
   public static final int BASIC_PRIORITY;
   public static final int MEDIUM_PRIORITY;
   public static final int HIGH_PRIORITY;
   public static int CONTEXT_ORDER;
   public static int MODE_ORDER;

   LBSMenuItem(int resourceId, int order) {
      super(LBSResources.getResourceBundle(), resourceId, order, 1000);
   }

   LBSMenuItem(int resourceId, int order, int priority) {
      super(LBSResources.getResourceBundle(), resourceId, order, priority);
   }

   boolean isVisible() {
      return true;
   }

   int getResourceId() {
      return -1;
   }

   @Override
   public String toString() {
      int resourceId = this.getResourceId();
      return resourceId == -1 ? super.toString() : LBSResources.getString(resourceId);
   }
}
