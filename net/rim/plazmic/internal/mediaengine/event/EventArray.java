package net.rim.plazmic.internal.mediaengine.event;

import net.rim.plazmic.internal.mediaengine.util.Array;

public class EventArray implements Array {
   private long[] _times;
   private long[] _uids;
   private Object[] _listeners;
   private Object[] _data;
   private int[] _events;
   private int[] _eventParam;
   private long[] _eventParamLong;

   @Override
   public void init(int capacity) {
      this._times = new long[capacity];
      this._uids = new long[capacity];
      this._events = new int[capacity];
      this._eventParam = new int[capacity];
      this._eventParamLong = new long[capacity];
      this._listeners = new Object[capacity];
      this._data = new Object[capacity];
   }

   @Override
   public void growTo(int newCapacity, int start, int end, boolean wrap) {
      int capacity = this.length();
      this._times = (long[])this.copy(this._times, new long[newCapacity], start, end, capacity, wrap);
      this._uids = (long[])this.copy(this._uids, new long[newCapacity], start, end, capacity, wrap);
      this._listeners = (Object[])this.copy(this._listeners, new Object[newCapacity], start, end, capacity, wrap);
      this._data = (Object[])this.copy(this._data, new Object[newCapacity], start, end, capacity, wrap);
      this._events = (int[])this.copy(this._events, new int[newCapacity], start, end, capacity, wrap);
      this._eventParam = (int[])this.copy(this._eventParam, new int[newCapacity], start, end, capacity, wrap);
      this._eventParamLong = (long[])this.copy(this._eventParamLong, new long[newCapacity], start, end, capacity, wrap);
   }

   private Object copy(Object src, Object dst, int start, int end, int capacity, boolean wrap) {
      if (wrap) {
         System.arraycopy(src, start, dst, 0, capacity - start);
         System.arraycopy(src, 0, dst, capacity - start, end);
         return dst;
      } else {
         System.arraycopy(src, start, dst, 0, end - start);
         return dst;
      }
   }

   @Override
   public int compare(int pos1, int pos2) {
      if (this._times[pos1] == this._times[pos2]) {
         if (this._uids[pos1] == this._uids[pos2]) {
            return 0;
         }

         if (this._uids[pos1] < this._uids[pos2]) {
            return -1;
         }
      } else if (this._times[pos1] < this._times[pos2]) {
         return -1;
      }

      return 1;
   }

   @Override
   public boolean equals(int index, Object o) {
      Event ev = (Event)o;
      return this._times[index] == ev._time
         && this._listeners[index] == ev._listener
         && this._events[index] == ev._event
         && this._eventParam[index] == ev._eventParam
         && this._eventParamLong[index] == ev._eventParamLong
         && this._data[index] == ev._data;
   }

   @Override
   public void swap(int pos1, int pos2) {
      long longValue = this._times[pos2];
      this._times[pos2] = this._times[pos1];
      this._times[pos1] = longValue;
      longValue = this._uids[pos2];
      this._uids[pos2] = this._uids[pos1];
      this._uids[pos1] = longValue;
      Object object = this._listeners[pos2];
      this._listeners[pos2] = this._listeners[pos1];
      this._listeners[pos1] = object;
      int intValue = this._events[pos2];
      this._events[pos2] = this._events[pos1];
      this._events[pos1] = intValue;
      intValue = this._eventParam[pos2];
      this._eventParam[pos2] = this._eventParam[pos1];
      this._eventParam[pos1] = intValue;
      longValue = this._eventParamLong[pos2];
      this._eventParamLong[pos2] = this._eventParamLong[pos1];
      this._eventParamLong[pos1] = longValue;
      object = this._data[pos2];
      this._data[pos2] = this._data[pos1];
      this._data[pos1] = object;
   }

   @Override
   public void copy(int src, int dst, int count) {
      if (count == 1) {
         this._times[dst] = this._times[src];
         this._listeners[dst] = this._listeners[src];
         this._events[dst] = this._events[src];
         this._eventParam[dst] = this._eventParam[src];
         this._eventParamLong[dst] = this._eventParamLong[src];
         this._data[dst] = this._data[src];
         this._uids[dst] = this._uids[src];
      } else {
         System.arraycopy(this._times, src, this._times, dst, count);
         System.arraycopy(this._listeners, src, this._listeners, dst, count);
         System.arraycopy(this._events, src, this._events, dst, count);
         System.arraycopy(this._eventParam, src, this._eventParam, dst, count);
         System.arraycopy(this._eventParamLong, src, this._eventParamLong, dst, count);
         System.arraycopy(this._data, src, this._data, dst, count);
         System.arraycopy(this._uids, src, this._uids, dst, count);
      }
   }

   @Override
   public void set(Object data, int index) {
      Event event = (Event)data;
      this._times[index] = event._time;
      this._uids[index] = event._uid;
      this._events[index] = event._event;
      this._eventParamLong[index] = event._eventParamLong;
      this._listeners[index] = event._listener;
      this._data[index] = event._data;
      this._eventParam[index] = event._eventParam;
   }

   @Override
   public void get(Object data, int index) {
      Event event = (Event)data;
      event._time = this._times[index];
      event._uid = this._uids[index];
      event._event = this._events[index];
      event._eventParamLong = this._eventParamLong[index];
      event._listener = this._listeners[index];
      event._data = this._data[index];
      event._eventParam = this._eventParam[index];
   }

   @Override
   public void clear(int index) {
      this._times[index] = 0;
      this._events[index] = 0;
      this._listeners[index] = null;
      this._data[index] = null;
      this._eventParam[index] = 0;
      this._eventParamLong[index] = 0;
      this._uids[index] = 0;
   }

   @Override
   public int length() {
      return this._times == null ? 0 : this._times.length;
   }
}
