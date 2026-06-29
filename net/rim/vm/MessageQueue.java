package net.rim.vm;

public final class MessageQueue {
   private int[] _device;
   private int[] _event;
   private int[] _subMessage;
   private int[] _dataLength;
   private int[] _data0;
   private int[] _data1;
   private Object[] _object0;
   private Object[] _object1;
   private int _start = 0;
   private int _end = 0;
   private int _capacity = 10;
   private int _maxCapacity;
   private int _processState = 0;
   private boolean _isSystemProcess;
   private static final int START_CAPACITY = 10;
   private static final int CAPACITY_INCREMENT = 10;
   public static final int KEY_DROP_WATERMARK = 30;
   public static final int NATIVE_SOCKET_DROP_WATERMARK = 150;
   public static final int PROCESS_STATE_STARTUP = 0;
   public static final int PROCESS_STATE_HANDLING_EVENTS = 1;
   private static final int MAX_PROCESS_STATES = 2;
   private static final int[] MAX_CAPACITIES_NORMAL_PROCESS = new int[]{30, 50, 51, 4408146, 4801362, 5391186, 5526098, -804651006};
   private static final int[] MAX_CAPACITIES_SYSTEM_PROCESS = new int[]{150, 200, 712179968, 712179968, 1920234561, 16802409, 1701539702, 3102532};

   public MessageQueue() {
      this._maxCapacity = MAX_CAPACITIES_NORMAL_PROCESS[0];
   }

   public final void setProcessState(int state) {
      if (state >= 0 && state <= 2) {
         this._processState = state;
         this.updateMaxCapacity();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void setSystemProcess() {
      this._isSystemProcess = true;
      this.updateMaxCapacity();
   }

   private final void updateMaxCapacity() {
      int maxCapacity;
      if (this._isSystemProcess) {
         maxCapacity = MAX_CAPACITIES_SYSTEM_PROCESS[this._processState];
      } else {
         maxCapacity = MAX_CAPACITIES_NORMAL_PROCESS[this._processState];
      }

      if (this._maxCapacity < maxCapacity) {
         this._maxCapacity = maxCapacity;
      }
   }

   private final void growTo(int newCapacity) {
      this._device = (int[])this.growArray(this._device, new int[newCapacity]);
      this._event = (int[])this.growArray(this._event, new int[newCapacity]);
      this._subMessage = (int[])this.growArray(this._subMessage, new int[newCapacity]);
      this._dataLength = (int[])this.growArray(this._dataLength, new int[newCapacity]);
      this._data0 = (int[])this.growArray(this._data0, new int[newCapacity]);
      this._data1 = (int[])this.growArray(this._data1, new int[newCapacity]);
      this._object0 = (Object[])this.growArray(this._object0, new Object[newCapacity]);
      this._object1 = (Object[])this.growArray(this._object1, new Object[newCapacity]);
      this._end = this._capacity;
      this._start = 0;
      this._capacity = newCapacity;
   }

   private final Object growArray(Object old, Object newArray) {
      if (this._end <= this._start) {
         System.arraycopy(old, this._start, newArray, 0, this._capacity - this._start);
         System.arraycopy(old, 0, newArray, this._capacity - this._start, this._end);
         return newArray;
      } else {
         System.arraycopy(old, this._start, newArray, 0, this._end - this._start);
         return newArray;
      }
   }

   public final boolean enqueue(Message msg) {
      boolean ok = true;

      try {
         int end = this._end;
         this._device[end] = msg._device;
         this._event[end] = msg._event;
         this._subMessage[end] = msg._subMessage;
         this._dataLength[end] = msg._dataLength;
         this._data0[end] = msg._data0;
         this._data1[end] = msg._data1;
         this._object0[end] = msg._object0;
         this._object1[end] = msg._object1;
      } catch (NullPointerException npe) {
         int capacity = this._capacity;
         this._device = new int[capacity];
         this._event = new int[capacity];
         this._subMessage = new int[capacity];
         this._dataLength = new int[capacity];
         this._data0 = new int[capacity];
         this._data1 = new int[capacity];
         this._object0 = new Object[capacity];
         this._object1 = new Object[capacity];
         return this.enqueue(msg);
      }

      this._end = this.plusplus(this._end);
      if (this._start == this._end) {
         if (this._capacity == this._maxCapacity) {
            this._object0[this._start] = null;
            this._object1[this._start] = null;
            this._start = this.plusplus(this._start);
            ok = false;
         } else {
            int newCapacity = this._capacity + 10;
            if (newCapacity > this._maxCapacity) {
               newCapacity = this._maxCapacity;
            }

            this.growTo(newCapacity);
         }
      }

      this.notify();
      return ok;
   }

   public final boolean replaceTail(Message msg) {
      if (this._start == this._end) {
         return false;
      }

      int end = this.minusminus(this._end);
      this._device[end] = msg._device;
      this._event[end] = msg._event;
      this._subMessage[end] = msg._subMessage;
      this._dataLength[end] = msg._dataLength;
      this._data0[end] = msg._data0;
      this._data1[end] = msg._data1;
      this._object0[end] = msg._object0;
      this._object1[end] = msg._object1;
      return true;
   }

   public final boolean dequeue(Message msg, boolean block) {
      if (this._start == this._end) {
         if (!block) {
            return false;
         }

         msg._object0 = null;
         msg._object1 = null;

         while (true) {
            try {
               Object o = this;
               o.wait();
               break;
            } catch (InterruptedException ie) {
            }
         }
      }

      msg._device = this._device[this._start];
      msg._event = this._event[this._start];
      msg._subMessage = this._subMessage[this._start];
      msg._dataLength = this._dataLength[this._start];
      msg._data0 = this._data0[this._start];
      msg._data1 = this._data1[this._start];
      msg._object0 = this._object0[this._start];
      msg._object1 = this._object1[this._start];
      this._object0[this._start] = null;
      this._object1[this._start] = null;
      this._start = this.plusplus(this._start);
      return true;
   }

   public final void flush() {
      while (this._start != this._end) {
         this._object0[this._start] = null;
         this._object1[this._start] = null;
         this._start = this.plusplus(this._start);
      }
   }

   public final boolean alreadyPresent(Message msg) {
      for (int curr = this._start; curr != this._end; curr = this.plusplus(curr)) {
         if (msg._device == this._device[curr]
            && msg._event == this._event[curr]
            && msg._subMessage == this._subMessage[curr]
            && msg._dataLength == this._dataLength[curr]
            && msg._data0 == this._data0[curr]
            && msg._data1 == this._data1[curr]
            && msg._object0 == this._object0[curr]
            && msg._object1 == this._object1[curr]) {
            return true;
         }
      }

      return false;
   }

   public final int getSize() {
      int size = this._end - this._start;
      if (size < 0) {
         size += this._capacity;
      }

      return size;
   }

   public final int getCapacity() {
      return this._capacity - 1;
   }

   public final int getMaxCapacity() {
      return this._maxCapacity - 1;
   }

   public final void setMaxCapacity(int max) {
      if (max + 1 < this._maxCapacity) {
         throw new IllegalArgumentException();
      }

      this._maxCapacity = max + 1;
   }

   private final int plusplus(int value) {
      if (++value == this._capacity) {
         value = 0;
      }

      return value;
   }

   private final int minusminus(int value) {
      return value == 0 ? this._capacity - 1 : value - 1;
   }

   private static final void appendObjType(StringBuffer buff, Object o) {
      if (o != null) {
         buff.append(' ');
         buff.append(Integer.toString(Memory.objectToInt(o), 16));
         buff.append(':');
         String name = o.getClass().getName();
         int dotIndex = name.lastIndexOf(46);
         if (dotIndex != -1) {
            int dimIndex = name.lastIndexOf(91);
            if (dimIndex != -1) {
               name = name.substring(0, dimIndex + 1) + name.substring(dotIndex + 1, name.length() - 1);
            } else {
               name = name.substring(dotIndex + 1);
            }
         }

         buff.append(name);
      }
   }

   @Override
   public final String toString() {
      StringBuffer buf = new StringBuffer();
      this.dumpMessage(buf, this.minusminus(this._start), true);

      for (int i = this._start; i != this._end; i = this.plusplus(i)) {
         this.dumpMessage(buf, i, false);
      }

      return buf.toString();
   }

   private final void dumpMessage(StringBuffer buf, int index, boolean bracketed) {
      if (bracketed) {
         buf.append('[');
      }

      buf.append(Integer.toHexString(this._device[index]));
      buf.append(' ');
      buf.append(Integer.toHexString(this._event[index]));
      if (bracketed) {
         appendObjType(buf, this._object0[index]);
         appendObjType(buf, this._object1[index]);
         buf.append(']');
      }

      buf.append('\n');
   }
}
