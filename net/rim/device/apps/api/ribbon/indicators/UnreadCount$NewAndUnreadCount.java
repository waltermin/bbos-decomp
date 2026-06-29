package net.rim.device.apps.api.ribbon.indicators;

public class UnreadCount$NewAndUnreadCount extends UnreadCount {
   private int _newCount;

   public UnreadCount$NewAndUnreadCount(int typeIndex, int priority) {
      super(typeIndex, priority);
   }

   @Override
   public int getUnreadCount() {
      return this.getCount();
   }

   @Override
   public int getCount() {
      short count = UnreadCountManager.getCountOptions().getDisplayMessageCount();
      return 1 == count ? super.getUnreadCount() : 0;
   }

   @Override
   public synchronized int modifyUnreadCount(int byHowMany) {
      return this.modifyUnreadCount(byHowMany, false, true, true);
   }

   public synchronized int modifyUnreadCount(int byHowMany, boolean updateNewCount, boolean updateUnreadCount, boolean updateUI) {
      if (updateUnreadCount) {
         this.setUnreadCount(super.getUnreadCount() + byHowMany, updateUI);
      }

      if (updateNewCount) {
         this._newCount += byHowMany;
         if (this._newCount < 0) {
            label41:
            try {
               throw new Object(((StringBuffer)(new Object("New count is invalid: "))).append(this._newCount).toString());
            } finally {
               this._newCount = 0;
               break label41;
            }
         }
      }

      if (super._action != null) {
         short count = UnreadCountManager.getCountOptions().getDisplayMessageCount();
         if (1 == count) {
            if (UnreadCountManager.getCountOptions().getDisplayNewMessageIndicator()) {
               this.updateActionState(updateNewCount, byHowMany);
            }
         } else {
            this.updateActionState(updateNewCount, byHowMany);
         }
      }

      return this.getCount();
   }

   private void updateActionState(boolean updateNewCount, int byHowMany) {
      if (updateNewCount) {
         if (byHowMany > 0) {
            super._action.set(2, "new");
            return;
         }

         if (this._newCount <= 0) {
            super._action.set(2, (String)((Object)null));
         }
      }
   }

   public void updateActionState() {
      super._action.set(2, (String)(this._newCount > 0 ? "new" : (Object)null));
   }

   @Override
   public boolean hasNewStatus() {
      return super.hasNewStatus() && UnreadCountManager.getCountOptions().getDisplayNewMessageIndicator() && this._newCount > 0;
   }

   public void resetNewCount() {
      this._newCount = 0;
   }

   @Override
   protected boolean displayIcon() {
      boolean display = false;
      switch (UnreadCountManager.getCountOptions().getDisplayMessageCount()) {
         case 0:
            return this.hasNewStatus() && this.getVisible();
         default:
            return super.displayIcon();
      }
   }

   @Override
   protected boolean drawCount() {
      switch (UnreadCountManager.getCountOptions().getDisplayMessageCount()) {
         case 0:
            return false;
         default:
            return super.drawCount();
      }
   }
}
