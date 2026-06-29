package net.rim.device.apps.internal.memorycleaner;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.options.OptionsItemVerb;

final class MemoryCleanerOptionsItem$CleanItemVerb extends OptionsItemVerb {
   int _item;
   private final MemoryCleanerOptionsItem this$0;

   MemoryCleanerOptionsItem$CleanItemVerb(MemoryCleanerOptionsItem _1, int item) {
      super(MemoryCleanerOptionsItem._rb.getString(10) + MemoryCleanerOptionsItem.getListenerDescription(_1._listeners[item]), 10000);
      this.this$0 = _1;
      this._item = item;
   }

   @Override
   public final Object invoke(Object parameter) {
      label17:
      try {
         this.this$0._listeners[this._item].cleanNow(4);
      } finally {
         break label17;
      }

      Dialog.inform(MessageFormat.format(MemoryCleanerOptionsItem._rb.getString(15), new String[]{this.this$0._listeners[this._item].getDescription()}));
      return null;
   }
}
