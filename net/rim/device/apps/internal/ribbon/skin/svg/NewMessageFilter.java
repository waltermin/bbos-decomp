package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VisibilityControl;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.MessageFilter;

public class NewMessageFilter extends MessageFilter {
   public NewMessageFilter(ReadableList messages) {
      super(messages, (byte)8);
   }

   @Override
   public void reset(Collection collection, Object hint) {
      if (ContextObject.getFlag(hint, 62)) {
         this.removeOldItemsFromSubSet();
         super._collectionListenerManager.fireReset(this, hint);
      } else {
         super.reset(collection, hint);
      }
   }

   @Override
   protected boolean passes(Object message) {
      return message instanceof VisibilityControl && message instanceof ActionProvider ? super.passes(message) : false;
   }

   private synchronized void removeOldItemsFromSubSet() {
      synchronized (FolderHierarchies.getLockObject()) {
         if (super._subset != null && !super._subset.isEmpty()) {
            int length = super._subset.size();

            for (int i = length - 1; i >= 0; i--) {
               if (!this.passes(super._subset.elementAt(i))) {
                  super._subset.removeElementAt(i);
               }
            }
         }
      }
   }
}
