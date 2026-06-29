package net.rim.device.apps.internal.memo.resources;

import net.rim.device.api.i18n.ResourceBundle;

public final class MemoResources {
   private static ResourceBundle _strings = ResourceBundle.getBundle(1419048744345184776L, "net.rim.device.apps.internal.resource.Memo");

   private MemoResources() {
   }

   public static final String getString(int id) {
      return _strings.getString(id);
   }
}
