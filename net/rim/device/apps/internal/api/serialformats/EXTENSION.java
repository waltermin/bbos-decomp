package net.rim.device.apps.internal.api.serialformats;

final class EXTENSION {
   private String _name;
   private String _data;
   private EXTENSION _next;
   private EXTENSION _current;

   public EXTENSION(String name, String data) {
      this._name = name;
      this._data = data;
   }

   public final void setNext(EXTENSION next) {
      this._next = next;
   }

   public final EXTENSION getNext() {
      return this._next;
   }

   public final String getName() {
      return this._name;
   }

   public final String getData() {
      return this._data;
   }

   public final void reset() {
      this._current = null;
   }

   public final EXTENSION getNextExtension() {
      return this._current;
   }

   public final boolean hasMoreExtension() {
      if (this._next == null) {
         return false;
      } else if (this._current == null) {
         this._current = this._next;
         return true;
      } else {
         this._current = this._current.getNext();
         return this._current != null;
      }
   }
}
