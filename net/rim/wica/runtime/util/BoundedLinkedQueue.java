package net.rim.wica.runtime.util;

public class BoundedLinkedQueue {
   protected BoundedLinkedQueue$LinkedNode head_;
   protected BoundedLinkedQueue$LinkedNode last_;
   protected final Object putGuard_ = new Object();
   protected final Object takeGuard_ = new Object();
   protected int capacity_;
   protected int putSidePutPermits_;
   protected int takeSidePutPermits_ = 0;

   public BoundedLinkedQueue(int capacity) {
      if (capacity <= 0) {
         throw new Object();
      }

      this.capacity_ = capacity;
      this.putSidePutPermits_ = capacity;
      this.head_ = new BoundedLinkedQueue$LinkedNode(null);
      this.last_ = this.head_;
   }

   public BoundedLinkedQueue() {
      this(10);
   }

   protected final int reconcilePutPermits() {
      this.putSidePutPermits_ = this.putSidePutPermits_ + this.takeSidePutPermits_;
      this.takeSidePutPermits_ = 0;
      return this.putSidePutPermits_;
   }

   public synchronized int capacity() {
      return this.capacity_;
   }

   public synchronized int size() {
      return this.capacity_ - (this.takeSidePutPermits_ + this.putSidePutPermits_);
   }

   public void setCapacity(int newCapacity) {
      if (newCapacity <= 0) {
         throw new Object();
      }

      synchronized (this.putGuard_) {
         synchronized (this) {
            this.takeSidePutPermits_ = this.takeSidePutPermits_ + (newCapacity - this.capacity_);
            this.capacity_ = newCapacity;
            this.reconcilePutPermits();
            this.notifyAll();
         }
      }
   }

   protected synchronized Object extract() {
      synchronized (this.head_) {
         Object x = null;
         BoundedLinkedQueue$LinkedNode first = this.head_.next;
         if (first != null) {
            x = first.value;
            first.value = null;
            this.head_ = first;
            this.takeSidePutPermits_++;
            this.notify();
         }

         return x;
      }
   }

   public Object peek() {
      synchronized (this.head_) {
         BoundedLinkedQueue$LinkedNode first = this.head_.next;
         return first != null ? first.value : null;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public Object take() {
      Object x = this.extract();
      if (x != null) {
         return x;
      }

      synchronized (this.takeGuard_) {
         while (true) {
            boolean var7 = false /* VF: Semaphore variable */;

            try {
               var7 = true;
               x = this.extract();
               if (x != null) {
                  var7 = false;
                  return x;
               }

               this.takeGuard_.wait();
            } finally {
               if (var7) {
                  this.takeGuard_.notify();
                  return x;
               }
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public Object poll(long msecs) {
      Object x = this.extract();
      if (x != null) {
         return x;
      }

      synchronized (this.takeGuard_) {
         boolean var12 = false /* VF: Semaphore variable */;

         try {
            var12 = true;
            long ex = msecs;
            long start = msecs <= 0 ? 0 : System.currentTimeMillis();

            while (true) {
               x = this.extract();
               if (x != null || ex <= 0) {
                  var12 = false;
                  return x;
               }

               this.takeGuard_.wait(ex);
               ex = msecs - (System.currentTimeMillis() - start);
            }
         } finally {
            if (var12) {
               this.takeGuard_.notify();
               return x;
            }
         }
      }
   }

   protected final void allowTake() {
      synchronized (this.takeGuard_) {
         this.takeGuard_.notify();
      }
   }

   protected void insert(Object x) {
      this.putSidePutPermits_--;
      BoundedLinkedQueue$LinkedNode p = new BoundedLinkedQueue$LinkedNode(x);
      synchronized (this.last_) {
         this.last_.next = p;
         this.last_ = p;
      }
   }

   public void put(Object x) {
      if (x == null) {
         throw new Object();
      }

      synchronized (this.putGuard_) {
         if (this.putSidePutPermits_ <= 0) {
            synchronized (this) {
               if (this.reconcilePutPermits() <= 0) {
                  try {
                     do {
                        this.wait();
                     } while (this.reconcilePutPermits() <= 0);
                  } finally {
                     ;
                  }
               }
            }
         }

         this.insert(x);
      }

      this.allowTake();
   }

   public boolean offer(Object x, long msecs) {
      if (x == null) {
         throw new Object();
      }

      synchronized (this.putGuard_) {
         if (this.putSidePutPermits_ <= 0) {
            synchronized (this) {
               if (this.reconcilePutPermits() <= 0) {
                  if (msecs <= 0) {
                     return false;
                  }

                  try {
                     label132: {
                        long waitTime = msecs;
                        long start = System.currentTimeMillis();

                        do {
                           this.wait(waitTime);
                           if (this.reconcilePutPermits() > 0) {
                              break label132;
                           }

                           waitTime = msecs - (System.currentTimeMillis() - start);
                        } while (waitTime > 0);

                        return false;
                     }
                  } finally {
                     ;
                  }
               }
            }
         }

         this.insert(x);
      }

      this.allowTake();
      return true;
   }

   public boolean isEmpty() {
      synchronized (this.head_) {
         return this.head_.next == null;
      }
   }
}
