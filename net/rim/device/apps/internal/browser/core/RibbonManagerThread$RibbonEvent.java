package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.servicebook.ServiceRecord;

final class RibbonManagerThread$RibbonEvent implements Runnable {
   private int _eventType;
   private boolean _refresh;
   private int _serviceRecordId;
   private ServiceRecord _serviceRecord;
   private final RibbonManagerThread this$0;
   static final int ADD;
   static final int REMOVE;

   RibbonManagerThread$RibbonEvent(RibbonManagerThread _1, int type, int id, boolean refresh, ServiceRecord rec) {
      this.this$0 = _1;
      this._eventType = type;
      this._serviceRecordId = id;
      this._refresh = refresh;
      this._serviceRecord = rec;
   }

   @Override
   public final void run() {
      switch (this._eventType) {
         case 1:
         default:
            this.this$0.addIconToRibbon(this._serviceRecord, this._refresh);
            return;
         case 2:
            this.this$0.removeIconFromRibbon(this._serviceRecordId);
         case 0:
      }
   }
}
