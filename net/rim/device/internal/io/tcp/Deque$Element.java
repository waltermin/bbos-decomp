package net.rim.device.internal.io.tcp;

final class Deque$Element {
   private Object _data;
   private Deque$Element _next;
   private Deque$Element _prev;

   private Deque$Element(Object obj) {
      this._data = obj;
   }

   Deque$Element(Object x0, Deque$1 x1) {
      this(x0);
   }
}
