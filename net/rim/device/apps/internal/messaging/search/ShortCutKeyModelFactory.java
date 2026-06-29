package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.ui.Keypad;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

final class ShortCutKeyModelFactory extends RIMModelFactory {
   private static ShortCutKeyModelFactory _factory = new ShortCutKeyModelFactory();

   private ShortCutKeyModelFactory() {
   }

   @Override
   public final Object createInstance(Object context) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(context, 255);
         if (sb != null) {
            try {
               char c = (char)sb.getInt(2, true);
               StringBuffer chars = Keypad.getLayout().getComplementaryChars(c, 0);
               if (chars != null && chars.charAt(0) == c) {
                  ShortCutKeyModel m = new ShortCutKeyModel(null);
                  m._value = c;
                  return m;
               } else {
                  return null;
               }
            } finally {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return new ShortCutKeyModel(context);
      }
   }

   @Override
   public final int getMinimumCount(Object context) {
      return 0;
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public final boolean recognize(Object o) {
      if (ContextObject.getFlag(o, 22) && ContextObject.getFlag(o, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(o, 255);
         return sb != null && sb.getFieldType() == 2;
      } else {
         return o instanceof ShortCutKeyModel;
      }
   }

   public static final synchronized RIMModelFactory getInstance() {
      return _factory;
   }
}
