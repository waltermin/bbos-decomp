package net.rim.wica.runtime.messaging.internal.util;

import net.rim.wica.runtime.util.ReentrantLock;

public class ConcurrentQueue {
   protected ConcurrentQueue$LinkedNode _head;
   protected ConcurrentQueue$LinkedNode _last;
   protected final ReentrantLock _queueGuard = new ReentrantLock();
   protected final ReentrantLock _putGuard = new ReentrantLock();
   protected int _capacity;
   protected int _putSidePutPermits;
   protected int _takeSidePutPermits = 0;
   protected Thread _owner;
   protected int _lock;

   public ConcurrentQueue(int initialcapacity) {
      if (initialcapacity < 0) {
         throw new Object();
      }

      this._capacity = initialcapacity;
      this._putSidePutPermits = initialcapacity;
      this._head = new ConcurrentQueue$LinkedNode(null);
      this._last = this._head;
   }

   public ConcurrentQueue() {
      this(10);
   }

   protected final int reconcilePutPermits() {
      this._putSidePutPermits = this._putSidePutPermits + this._takeSidePutPermits;
      this._takeSidePutPermits = 0;
      return this._putSidePutPermits;
   }

   public int capacity() {
      return this._capacity;
   }

   public int size() {
      int size = 0;
      this._queueGuard.acquire();
      size = this._capacity - (this._takeSidePutPermits + this._putSidePutPermits);
      this._queueGuard.release();
      return size;
   }

   public Object peek() {
      synchronized (this._head) {
         ConcurrentQueue$LinkedNode first = this._head.next;
         return first != null ? first.value : null;
      }
   }

   public Object take() {
      Object x = null;
      this._queueGuard.acquire();
      synchronized (this._head) {
         ConcurrentQueue$LinkedNode first = this._head.next;
         if (first != null) {
            x = first.value;
            first.value = null;
            this._head = first;
            this._takeSidePutPermits++;
         }
      }

      this._queueGuard.release();
      return x;
   }

   protected void insert(Object x) {
      this._putSidePutPermits--;
      ConcurrentQueue$LinkedNode p = new ConcurrentQueue$LinkedNode(x);
      synchronized (this._last) {
         this._last.next = p;
         this._last = p;
      }
   }

   public void put(Object x) {
      if (x == null) {
         throw new Object();
      }

      this._putGuard.acquire();
      if (this._putSidePutPermits <= 0) {
         this._queueGuard.acquire();
         if (this.reconcilePutPermits() <= 0) {
            int increase = this._capacity < 4 ? 10 : this._capacity >>> 2;
            this._putSidePutPermits = increase;
            this._capacity += increase;
         }

         this._queueGuard.release();
      }

      this.insert(x);
      this._putGuard.release();
   }

   public boolean isEmpty() {
      synchronized (this._head) {
         return this._head.next == null;
      }
   }

   public ConcurrentQueue$Iterator iterator() {
      return new ConcurrentQueue$Iterator(this);
   }

   public void lock() {
      this._putGuard.acquire();
      this._queueGuard.acquire();
      this._owner = Thread.currentThread();
      this._lock++;
   }

   public void unlock() {
      if (this._owner == Thread.currentThread()) {
         if (--this._lock == 0) {
            this._owner = null;
         }

         this._putGuard.release();
         this._queueGuard.release();
      }
   }

   public void remove(Object o) {
      this.lock();
      ConcurrentQueue$Iterator i = this.iterator();

      while (i.hasNext()) {
         if (o.equals(i.next())) {
            i.remove();
            break;
         }
      }

      this.unlock();
   }

   public void removeAll() {
      this.lock();
      this._head.next = null;
      this._last = this._head;
      this._takeSidePutPermits = 0;
      this._putSidePutPermits = this._capacity;
      this.unlock();
   }

   public void prepend(ConcurrentQueue queue) {
      if (queue == null) {
         throw new Object();
      }

      this.lock();
      queue.lock();
      if (!queue.isEmpty()) {
         int addedsize = queue.size();
         int requiredcapacity = this.size() + addedsize;
         this._capacity = requiredcapacity > this._capacity ? requiredcapacity : this._capacity;
         this._takeSidePutPermits = 0;
         this._putSidePutPermits = this._capacity - requiredcapacity;
         queue.getLast().next = this._head.next;
         this._head.next = queue.getHead().next;
         if (this._last == this._head) {
            this._last = queue.getLast();
         }

         queue.removeAll();
      }

      queue.unlock();
      this.unlock();
   }

   ConcurrentQueue$LinkedNode getHead() {
      return this._head;
   }

   ConcurrentQueue$LinkedNode getLast() {
      return this._last;
   }
}
