package net.rim.device.cldc.io.ippp;

import java.util.Vector;
import net.rim.device.api.util.IntHashtable;

public class Cache {
   protected int _maxSize;
   protected int _maxPending;
   protected IntHashtable _mapOfConnectionIDToQueue;
   protected IntHashtable _mapOfPortToQueue;

   public Cache(int maximumSize) {
      this._maxSize = maximumSize;
      this._maxPending = 10;
      this.clear();
   }

   public Cache(int maximumSize, int maximumPending) {
      this._maxSize = maximumSize;
      if (maximumPending > 0) {
         this._maxPending = maximumPending;
      } else {
         this._maxPending = 10;
      }

      this.clear();
   }

   public synchronized boolean put(Queue queue) {
      if (this.contains(queue)) {
         return false;
      }

      if (this.getSize() >= this.getMaxSize()) {
         return false;
      }

      int port = queue.getPort() & '\uffff';
      Vector pendingQueues = null;
      if (this._mapOfPortToQueue.containsKey(port)) {
         pendingQueues = (Vector)this._mapOfPortToQueue.get(port);
      } else {
         pendingQueues = new Vector(this._maxPending);
      }

      if (pendingQueues.size() >= this._maxPending) {
         return false;
      }

      pendingQueues.addElement(queue);
      this._mapOfConnectionIDToQueue.put(queue.getConnectionID(), queue);
      this._mapOfPortToQueue.put(port, pendingQueues);
      return true;
   }

   public synchronized boolean update(Queue queue, short port) {
      if (queue.getPort() == port) {
         return true;
      }

      if (!this.contains(queue)) {
         return false;
      }

      int originalPort = queue.getPort() & '\uffff';
      if (!this._mapOfPortToQueue.containsKey(originalPort)) {
         return false;
      }

      Vector pendingQueues = (Vector)this._mapOfPortToQueue.get(originalPort);
      pendingQueues.removeElement(queue);
      if (pendingQueues.size() == 0) {
         this._mapOfPortToQueue.remove(originalPort);
      }

      queue.setPort(port);
      int destPort = port & '\uffff';
      if (this._mapOfPortToQueue.containsKey(destPort)) {
         pendingQueues = (Vector)this._mapOfPortToQueue.get(destPort);
      } else {
         pendingQueues = new Vector(this._maxPending);
      }

      if (pendingQueues.size() >= this._maxPending) {
         return false;
      }

      pendingQueues.addElement(queue);
      this._mapOfPortToQueue.put(destPort, pendingQueues);
      return true;
   }

   public synchronized boolean contains(Queue queue) {
      if (queue != null) {
         return this.contains(queue.getConnectionID());
      } else {
         throw new IllegalArgumentException("queue is null");
      }
   }

   public synchronized boolean contains(int connectionID) {
      Queue queue = this.get(connectionID);
      return queue != null;
   }

   public synchronized boolean contains(short port) {
      return this.get(port) != null;
   }

   public synchronized Queue get(int connectionID) {
      return (Queue)this._mapOfConnectionIDToQueue.get(connectionID);
   }

   public synchronized int getConnectionID(short port) {
      Vector pendingQueues = (Vector)this._mapOfPortToQueue.get(port & 65535);
      if (pendingQueues != null && pendingQueues.size() > 0) {
         Queue queue = (Queue)pendingQueues.elementAt(0);
         if (queue.getPort() == port) {
            return queue.getConnectionID();
         }
      }

      return 0;
   }

   public synchronized int getSize() {
      return this._mapOfConnectionIDToQueue.size();
   }

   public synchronized int getMaxSize() {
      return this._maxSize;
   }

   public synchronized int getMaxPending() {
      return this._maxPending;
   }

   public synchronized Queue remove(short port) {
      Vector pendingQueues = null;
      Queue queue = null;
      int key = port & '\uffff';
      pendingQueues = (Vector)this._mapOfPortToQueue.get(key);
      if (pendingQueues != null && pendingQueues.size() > 0) {
         queue = (Queue)pendingQueues.elementAt(0);
         if (queue != null) {
            if (pendingQueues.size() > 1) {
               pendingQueues.removeElementAt(0);
            } else {
               this._mapOfPortToQueue.remove(key);
            }

            this._mapOfConnectionIDToQueue.remove(queue.getConnectionID());
         }
      }

      return queue;
   }

   public synchronized Queue remove(int connectionID) {
      Queue queue = null;
      queue = (Queue)this._mapOfConnectionIDToQueue.get(connectionID);
      if (queue != null) {
         this._mapOfConnectionIDToQueue.remove(connectionID);
         int port = queue.getPort() & '\uffff';
         Vector pendingQueues = (Vector)this._mapOfPortToQueue.get(port);
         if (pendingQueues != null) {
            int pending = pendingQueues.size();

            for (int i = 0; i < pending; i++) {
               Queue q = (Queue)pendingQueues.elementAt(i);
               if (q.getConnectionID() == connectionID) {
                  if (pendingQueues.size() > 1) {
                     pendingQueues.removeElementAt(i);
                     return queue;
                  }

                  this._mapOfPortToQueue.remove(port);
                  return queue;
               }
            }
         }
      }

      return queue;
   }

   public synchronized void clear() {
      this._mapOfPortToQueue = new IntHashtable();
      this._mapOfConnectionIDToQueue = new IntHashtable();
   }
}
