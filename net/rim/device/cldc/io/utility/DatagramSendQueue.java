package net.rim.device.cldc.io.utility;

import java.io.IOException;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

public final class DatagramSendQueue extends Thread {
   private Datagram[] _sendQueue;
   private Datagram[] _cacheQueue;
   private int _nextDequeue;
   private int _nextEnqueue;
   private int _size;
   private int _cacheSize;
   private DatagramConnection _sendConnection;

   public DatagramSendQueue(int size, DatagramConnection sendConnection) {
      this._sendQueue = new Datagram[size];
      this._cacheQueue = new Datagram[size];
      this._nextDequeue = -1;
      this._nextEnqueue = 0;
      this._size = 0;
      this._cacheSize = 0;
      this._sendConnection = sendConnection;
   }

   public final synchronized void send(Datagram datagram) {
      this.enqueue(datagram);
   }

   private final void enqueue(Datagram datagram) {
      this._sendQueue[this._nextEnqueue++] = datagram;
      if (this._nextDequeue == -1) {
         this._nextDequeue = this._nextEnqueue - 1;
      }

      if (this._nextEnqueue == this._sendQueue.length) {
         this._nextEnqueue = 0;
      }

      if (this._nextEnqueue == this._nextDequeue) {
         this.grow();
      }

      this._size++;
      this.notify();
   }

   private final Datagram dequeue() {
      if (this._nextDequeue == -1) {
         return null;
      }

      Datagram datagram = this._sendQueue[this._nextDequeue];
      this._sendQueue[this._nextDequeue++] = null;
      if (this._nextDequeue == this._sendQueue.length) {
         this._nextDequeue = 0;
      }

      if (this._nextDequeue == this._nextEnqueue) {
         this._nextDequeue = -1;
      }

      this._size--;
      return datagram;
   }

   private final void dequeueAll() {
      if (this._cacheQueue.length != this._sendQueue.length) {
         this._cacheQueue = new Datagram[this._sendQueue.length];
      }

      this._cacheSize = this.size();

      for (int i = 0; i < this._cacheSize; i++) {
         this._cacheQueue[i] = this.dequeue();
      }
   }

   public final int size() {
      return this._size;
   }

   private final void grow() {
      Datagram[] newQueue = new Datagram[this._sendQueue.length * 2];
      int lengthBeforeEnqueue = this._nextEnqueue;
      int lengthAfterEnqueue = this._sendQueue.length - this._nextEnqueue;
      this._nextDequeue = newQueue.length - lengthAfterEnqueue;
      System.arraycopy(this._sendQueue, 0, newQueue, 0, lengthBeforeEnqueue);
      System.arraycopy(this._sendQueue, this._nextEnqueue, newQueue, this._nextDequeue, lengthAfterEnqueue);
      this._sendQueue = newQueue;
   }

   @Override
   public final void run() {
      while (true) {
         synchronized (this) {
            try {
               if (this.size() == 0) {
                  this.wait();
               }
            } catch (InterruptedException var11) {
            }

            this.dequeueAll();
         }

         for (int i = 0; i < this._cacheSize; i++) {
            Datagram datagram = this._cacheQueue[i];
            this._cacheQueue[i] = null;
            if (datagram != null) {
               try {
                  this._sendConnection.send(datagram);
               } catch (IOException e) {
                  e.printStackTrace();
               } finally {
                  Datagram var13 = null;
               }
            }
         }

         this._cacheSize = 0;
      }
   }
}
