package net.rim.device.cldc.io.ippp;

public final class Queue {
   protected int _connectionID;
   protected long _timestamp;
   protected SocketDatagram[] _table;
   protected short _port;

   public Queue(int connectionID, int maxSize) {
      this(connectionID, maxSize, System.currentTimeMillis());
   }

   public Queue(int connectionID, int maxSize, long timestamp) {
      this._connectionID = connectionID;
      this._timestamp = timestamp;
      this._table = new SocketDatagram[256];
   }

   public final short getPort() {
      return this._port;
   }

   public final void setPort(short port) {
      this._port = port;
   }

   public final int getConnectionID() {
      return this._connectionID;
   }

   public final void setConnectionID(int connectionID) {
      this._connectionID = connectionID;
   }

   public final long getTimestamp() {
      return this._timestamp;
   }

   public final void setTimestamp(long timestamp) {
      this._timestamp = timestamp;
   }

   public final synchronized boolean put(int sequence, SocketDatagram datagram) {
      int offset = sequence & 0xFF;
      if (this._table[offset] != null) {
         return false;
      }

      this._table[offset] = datagram;
      return true;
   }

   public final synchronized SocketDatagram get(int sequence) {
      int offset = sequence & 0xFF;
      if (offset >= this._table.length) {
         return null;
      }

      SocketDatagram anObject = this._table[offset];
      if (anObject == null) {
         return null;
      }

      this._table[offset] = null;
      return anObject;
   }
}
