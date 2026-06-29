package net.rim.device.cldc.io.sms;

import net.rim.device.api.io.DatagramStatusListener;

class StatusThread$StatusObject {
   protected boolean _dgramIdFlag;
   protected DatagramStatusListener _listener;
   protected int _id;
   protected int _status;
   protected Object _context;

   public StatusThread$StatusObject(boolean dgramIdFlag, DatagramStatusListener listener, int id, int status, Object context) {
      this._dgramIdFlag = dgramIdFlag;
      this._listener = listener;
      this._id = id;
      this._status = status;
      this._context = context;
   }
}
