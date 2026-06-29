package net.rim.device.apps.internal.task.resources;

import net.rim.device.api.i18n.ResourceBundle;

public final class TaskResources {
   private static ResourceBundle _strings = ResourceBundle.getBundle(2546929077131262015L, "net.rim.device.apps.internal.resource.Task");

   public static final String getString(int id) {
      return _strings.getString(id);
   }
}
