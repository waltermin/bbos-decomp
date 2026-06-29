package net.rim.device.internal.ui;

class HorizontalFetcher implements Fetcher {
   int[] _data;

   void set(int[] data) {
      this._data = data;
   }

   @Override
   public int get(int index) {
      return this._data[index];
   }
}
