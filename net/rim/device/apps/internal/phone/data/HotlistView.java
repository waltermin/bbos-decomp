package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class HotlistView extends PhoneListView implements ListFieldCallback, HotlistEventListener {
   private Hotlist _hotlist;
   private boolean _refreshPending;
   private boolean _invalidatePending;
   private static final long MANAGER_FLAGS = 3459063580983296000L;
   private static final int[] HOTLIST_BASE_CONTEXT_FLAGS = new int[]{
      3, 58, 4, 17, -804651007, 4, -804782076, 9832200, 9831900, -804651006, 10, 20, -804651003, 16, 8, 128
   };

   public HotlistView(Application app) {
      super(app, 0, 1);
   }

   @Override
   protected final String getDeletePrompt(boolean multipleItemsSelected) {
      return multipleItemsSelected ? PhoneResources.getString(6040) : PhoneResources.getString(119);
   }

   @Override
   protected final void getVerbContextFlags(PhoneListItem item, ContextObject context) {
      super.getVerbContextFlags(item, context);
      context.setFlag(49);
   }

   @Override
   protected final void loadItems() {
      this._hotlist = Hotlist.getInstance();
      if (this._hotlist == null) {
         throw new RuntimeException("Not hotlist instance.");
      }

      this._hotlist.setListener(this);
      super._items = this._hotlist;
   }

   @Override
   protected final ContextObject getPaintingContextObject(PhoneListItem itemToPaint) {
      ContextObject context = super.getPaintingContextObject(itemToPaint);
      context.setFlags(HOTLIST_BASE_CONTEXT_FLAGS);
      ContextObject.put(context, 9045827404276417370L, this);
      PhoneUtilities.setPrivateFlag(context, 9);
      return context;
   }

   @Override
   protected final void setSelectedIndex() {
      if (PhoneOptions.getOptions().getPhoneListViewType() == 0) {
         this.setSelectedIndex(0);
      } else {
         super.setSelectedIndex();
      }
   }

   @Override
   public final void hotlistEventNotify(int eventId) {
      boolean newUpdatePending = false;
      switch (eventId) {
         case 0:
            break;
         case 1:
         case 5:
         default:
            if (!this._invalidatePending) {
               newUpdatePending = true;
            }

            this._invalidatePending = true;
            break;
         case 2:
         case 3:
         case 4:
            if (!this._refreshPending) {
               newUpdatePending = true;
            }

            this._refreshPending = true;
      }

      if (newUpdatePending) {
         super._app.invokeLater(this);
      }
   }

   @Override
   protected final void updateOnCorrectThread() {
      this.hotlistEventNotify(4);
   }

   @Override
   public final void run() {
      if (this._invalidatePending) {
         this._invalidatePending = false;
         this.invalidate();
      }

      if (this._refreshPending) {
         this._refreshPending = false;
         this.refreshList();
      }
   }
}
