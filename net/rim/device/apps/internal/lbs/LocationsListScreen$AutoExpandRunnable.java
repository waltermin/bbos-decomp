package net.rim.device.apps.internal.lbs;

final class LocationsListScreen$AutoExpandRunnable implements Runnable {
   private final LocationsListScreen this$0;

   LocationsListScreen$AutoExpandRunnable(LocationsListScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      if (this.this$0._activeField == this.this$0._listActions) {
         this.this$0._expanded = false;
         this.this$0._numSubItems = 0;
         this.this$0._list.setSize(this.this$0._field._currentPOIs.length);
      } else {
         this.this$0._selectedIndex = this.this$0._list.getSelectedIndex();
         if (this.this$0._selectedIndex > this.this$0._selectedPOI) {
            if (this.this$0._selectedIndex > this.this$0._selectedPOI + this.this$0._numSubItems) {
               this.this$0._expanded = true;
               LocationsListScreen.access$1220(this.this$0, this.this$0._numSubItems);
               this.this$0._location = this.this$0._field._currentPOIs[this.this$0._selectedIndex];
               this.this$0._selectedPOI = Math.min(this.this$0._selectedIndex, this.this$0._numPOIs - 1);
               this.this$0._numSubItems = 1;
               if (this.this$0._field._currentPOIs[this.this$0._selectedPOI]._address != null
                  && this.this$0._field._currentPOIs[this.this$0._selectedPOI]._address.length() > 0) {
                  LocationsListScreen.access$1008(this.this$0);
               }

               if (this.this$0._field._currentPOIs[this.this$0._selectedPOI]._phone != null
                  && this.this$0._field._currentPOIs[this.this$0._selectedPOI]._phone.length() > 0) {
                  LocationsListScreen.access$1008(this.this$0);
               }

               this.this$0._list.setSize(this.this$0._field._currentPOIs.length + this.this$0._numSubItems);
               this.this$0._list.setSelectedIndex(this.this$0._selectedIndex);
            }
         } else {
            this.this$0._expanded = true;
            this.this$0._selectedPOI = Math.min(this.this$0._selectedIndex, this.this$0._numPOIs - 1);
            this.this$0._location = this.this$0._field._currentPOIs[this.this$0._selectedIndex];
            this.this$0._numSubItems = 1;
            if (this.this$0._field._currentPOIs[this.this$0._selectedPOI]._address != null
               && this.this$0._field._currentPOIs[this.this$0._selectedPOI]._address.length() > 0) {
               LocationsListScreen.access$1008(this.this$0);
            }

            if (this.this$0._field._currentPOIs[this.this$0._selectedPOI]._phone != null
               && this.this$0._field._currentPOIs[this.this$0._selectedPOI]._phone.length() > 0) {
               LocationsListScreen.access$1008(this.this$0);
            }

            this.this$0._list.setSize(this.this$0._field._currentPOIs.length + this.this$0._numSubItems);
            this.this$0._list.setSelectedIndex(this.this$0._selectedIndex);
         }
      }
   }
}
