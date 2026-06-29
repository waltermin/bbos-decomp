package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

public class MessageListUI$OpenItemVerb extends Verb implements Runnable {
   private int _index;
   private ContextObject _context;
   private int _resourceIdWithinCommonResources;
   private final MessageListUI this$0;

   public MessageListUI$OpenItemVerb(MessageListUI _1, int menuOrdering, int resourceIdWithinCommonResources) {
      super(menuOrdering);
      this.this$0 = _1;
      this._resourceIdWithinCommonResources = resourceIdWithinCommonResources;
   }

   public void setItem(int index, ContextObject context) {
      this._index = index;
      this._context = context;
   }

   @Override
   public Object invoke(Object parameter) {
      if (this._index == -1 && this._resourceIdWithinCommonResources == 1550) {
         this._index = this.this$0.checkItemIndex(278390328807340479L, true, false, null);
      } else {
         this.this$0._selectedIndexManager.setSelectedIndex(this._index);
      }

      UiApplication app = UiApplication.getUiApplication();
      app.suspendPainting(true);
      app.popScreen(app.getActiveScreen());
      app.invokeLater(this);
      return null;
   }

   @Override
   public void run() {
      RIMModel model = null;
      if (this._index >= 0) {
         model = (RIMModel)this.this$0._sortedSeparatedItems.getAt(this._index);
      }

      UiApplication app = UiApplication.getUiApplication();
      app.suspendPainting(false);
      if (model instanceof Object) {
         Object savedSelectedItem = this._context.get(250);
         if (savedSelectedItem != model) {
            this._context.put(250, model);
         }

         ActionProvider actionProvider = (ActionProvider)model;
         actionProvider.perform(6099736323056465049L, this._context);
         if (savedSelectedItem != model) {
            if (savedSelectedItem != null) {
               this._context.put(250, savedSelectedItem);
               return;
            }

            this._context.remove(250);
            return;
         }
      } else {
         app.repaint();
      }
   }

   @Override
   public String toString() {
      return CommonResources.getString(this._resourceIdWithinCommonResources);
   }
}
