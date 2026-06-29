package net.rim.device.apps.internal.browser.history;

class Folder {
   long _start;
   long _end;
   String _label;
   boolean _isDateFolder;

   public Folder(String label) {
      this._label = label;
      this._isDateFolder = false;
   }

   public Folder(String label, long start, long end) {
      this._end = end;
      this._isDateFolder = true;
      this._label = label;
      this._start = start;
   }
}
