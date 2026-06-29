package net.rim.device.apps.internal.phone;

import java.util.Vector;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.apps.api.ui.SelfDrawingListField;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;

final class MemberCallsListField extends SelfDrawingListField {
   private Vector _calls;

   public MemberCallsListField(Vector calls) {
      super(0, 0);
      this._calls = calls;
      this.updateList(calls);
   }

   final void updateList(Vector calls) {
      if (calls != null) {
         this._calls = calls;
         this.setSize(calls.size());
      } else {
         this.setSize(0);
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (this._calls != null) {
         int size = this._calls.size();
         if (index <= size) {
            Object callerID = ((LiveCall)this._calls.elementAt(index)).getDisplayCallerIDInfo();
            if (callerID instanceof CallerIDInfo) {
               CallerIDInfo cidi = (CallerIDInfo)callerID;
               String cidiString = cidi.getDisplayString();
               graphics.drawText(cidiString, 0, y, 64, width);
            }
         }
      }
   }
}
