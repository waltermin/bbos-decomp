package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.search.MessageSearch;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;
import net.rim.device.internal.ui.UiInternal;

final class ShortCutKeyModel implements PersistableRIMModel, FieldProvider, PaintProvider, ConversionProvider {
   char _value;

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      int xoffset = g.drawText(" (", x, y, 0, width);
      xoffset += g.drawText(this._value, x + xoffset, y, 0, width - xoffset);
      return xoffset + g.drawText((char)41, x + xoffset, y, 0, width - xoffset);
   }

   protected final char getShortCutKey() {
      return this._value;
   }

   public final Verb[] getVerbs(Object context) {
      return null;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      KeyChoiceField cf = (KeyChoiceField)field;
      int ix = cf.getSelectedIndex();
      if (ix > 0) {
         Object o = cf.getChoice(ix);
         String s = o.toString();
         this._value = s.charAt(0);
         return true;
      } else {
         this._value = 0;
         return false;
      }
   }

   @Override
   public final Field getField(Object context) {
      MessageSearchImpl search = (MessageSearchImpl)MessageSearch.getInstance();
      String shortcutPrompt = MessageFormat.format(SearchResources.getString(7), new Object[]{UiInternal.BUNDLE.getString(7)});
      return this._value > 0
         ? new KeyChoiceField(shortcutPrompt, search.getUnusedHotKeys(), (Character)(new Object(this._value)))
         : new KeyChoiceField(shortcutPrompt, search.getUnusedHotKeys(), 0);
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         if (this._value != 0) {
            syncBuffer.addInt(2, this._value, 2);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final int getOrder(Object context) {
      return 6000;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   ShortCutKeyModel(Object initialData) {
      if (initialData instanceof Object) {
         Character c = (Character)initialData;
         this._value = c;
      }
   }
}
