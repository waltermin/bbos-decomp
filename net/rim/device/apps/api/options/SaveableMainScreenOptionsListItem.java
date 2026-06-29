package net.rim.device.apps.api.options;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;

public class SaveableMainScreenOptionsListItem extends MainScreenOptionsListItem {
   public SaveableMainScreenOptionsListItem(String displayName) {
      super(displayName, new Object(0, 2));
   }

   public SaveableMainScreenOptionsListItem(String displayName, long group) {
      super(displayName, new Object(0, 2), group);
   }

   public SaveableMainScreenOptionsListItem(ResourceBundleFamily rb, int key) {
      super(rb, key, new Object(0, 2));
   }

   public SaveableMainScreenOptionsListItem(ResourceBundleFamily rb, int key, long group) {
      super(rb, key, new Object(0, 2), group);
   }

   @Override
   protected Verb getSaveVerb() {
      return new SaveableMainScreenOptionsListItem$DefaultSaveVerb(this);
   }
}
