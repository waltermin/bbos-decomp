package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.messaging.resources.MessageResources;

class PhoneListView$DeleteStatusDialog extends Dialog {
   private boolean _deleting;
   private int[] _selectedItems;
   private PhoneListView _phoneListView;
   private final PhoneListView this$0;

   public PhoneListView$DeleteStatusDialog(PhoneListView _1, PhoneListView phoneListView, int[] selectedItems) {
      super(MessageResources.getString(66), null, null, 0, Bitmap.getPredefinedBitmap(3), 33554432);
      this.this$0 = _1;
      this._deleting = false;
      this._phoneListView = phoneListView;
      this._selectedItems = selectedItems;
   }

   @Override
   protected void paint(Graphics graphics) {
      super.paint(graphics);
      if (!this._deleting) {
         this._deleting = true;
         new PhoneListView$DeleteStatusDialog$1(this).start();
      }
   }
}
