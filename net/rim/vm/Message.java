package net.rim.vm;

public final class Message {
   int _device;
   int _event;
   int _subMessage;
   int _dataLength;
   int _dataPtr_UNUSED;
   int _data0;
   int _data1;
   Object _object0;
   Object _object1;

   public final native boolean get();

   public static final native void abortGet(Thread var0);

   public final native void post();

   public Message() {
   }

   public Message(int device, int event) {
      this._device = device;
      this._event = event;
   }

   public Message(int device, int event, int subMessage) {
      this._device = device;
      this._event = event;
      this._subMessage = subMessage;
   }

   public Message(int device, int event, int subMessage, int data0, int data1) {
      this._device = device;
      this._event = event;
      this._subMessage = subMessage;
      this._data0 = data0;
      this._data1 = data1;
   }

   public Message(int device, int event, Object object0, Object object1) {
      this._device = device;
      this._event = event;
      this._object0 = object0;
      this._object1 = object1;
   }

   public Message(int device, int event, int subMessage, int data0, int data1, Object object0, Object object1) {
      this._device = device;
      this._event = event;
      this._subMessage = subMessage;
      this._data0 = data0;
      this._data1 = data1;
      this._object0 = object0;
      this._object1 = object1;
   }

   public final int getDevice() {
      return this._device;
   }

   public final int getEvent() {
      return this._event;
   }

   public final int getSubMessage() {
      return this._subMessage;
   }

   public final int getDataLength() {
      return this._dataLength;
   }

   public final int getData0() {
      return this._data0;
   }

   public final int getData1() {
      return this._data1;
   }

   public final Object getObject0() {
      return this._object0;
   }

   public final Object getObject1() {
      return this._object1;
   }

   public final void setDevice(int device) {
      this._device = device;
   }

   public final void setEvent(int event) {
      this._event = event;
   }

   public final void setSubMessage(int subMessage) {
      this._subMessage = subMessage;
   }

   public final void setDataLength(int dataLength) {
      this._dataLength = dataLength;
   }

   public final void setData0(int data) {
      this._data0 = data;
   }

   public final void setData1(int data) {
      this._data1 = data;
   }

   public final void setObject0(Object data) {
      this._object0 = data;
   }

   public final void setObject1(Object data) {
      this._object1 = data;
   }

   public final void copy(Message message) {
      this._device = message._device;
      this._event = message._event;
      this._subMessage = message._subMessage;
      this._dataLength = message._dataLength;
      this._data0 = message._data0;
      this._data1 = message._data1;
      this._object0 = message._object0;
      this._object1 = message._object1;
   }

   @Override
   public final String toString() {
      return "device: " + this._device + "; event: " + this._event + "; submsg: " + this._subMessage;
   }
}
