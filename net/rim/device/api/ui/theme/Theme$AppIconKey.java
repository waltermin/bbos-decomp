package net.rim.device.api.ui.theme;

class Theme$AppIconKey {
   private String _name;
   private int _state;
   private int _hash;
   private int _size;

   Theme$AppIconKey(String name, int state, int size) {
      this._name = name;
      this._state = state;
      this._size = size;
      this._hash = name.hashCode() ^ (state | size << 4);
   }

   @Override
   public boolean equals(Object object) {
      Theme$AppIconKey other = (Theme$AppIconKey)object;
      return this._hash == other._hash && this._state == other._state && this._name.equals(other._name) && this._size == other._size;
   }

   @Override
   public int hashCode() {
      return this._hash;
   }
}
