package net.rim.device.apps.internal.browser.core;

class SecondaryURLManager$StateInfo {
   SecondaryURLNode[] _nodes;
   int _state;
   String _url;
   static final int URL_NOT_REQUESTED;
   static final int URL_REQUESTED;
   static final int URL_WAITING;

   public SecondaryURLManager$StateInfo(String url, int state) {
      this._url = url;
      this._nodes = new SecondaryURLNode[0];
      this._state = state;
   }

   public SecondaryURLManager$StateInfo(String url, SecondaryURLNode obj) {
      this._url = url;
      this._nodes = new SecondaryURLNode[1];
      this._nodes[0] = obj;
   }
}
