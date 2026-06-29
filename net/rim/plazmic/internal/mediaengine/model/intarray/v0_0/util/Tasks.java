package net.rim.plazmic.internal.mediaengine.model.intarray.v0_0.util;

public final class Tasks {
   private int[] _heap = new int[200];
   private int _heapEnd;
   private int _numQueue;
   private int _currentQueueCur;
   private int _currentQueueEnd;
   private int _bufferQueueCur;
   private int _bufferQueueTail;
   private int _bufferQueueEnd;
   private int[] _bufferQueue;
   private int[] _currentQueue = new int[222];
   private int[] _currentStack;
   private int _currentStackEnd;
   private int[] _bufferStack;
   private int _bufferStackEnd;
   private int[] _eventList;
   private int _eventListEnd;
   private boolean _eventActive;
   private int[] _bufferEventQueue;
   private int[] _currentEventQueue;
   private int _bufferEventQueueEnd;
   private int _currentEventQueueEnd;
   private int _currentEventQueueHead;
   private static final int HEAP_GROWTH;
   private static final int QUEUE_GROWTH;
   private static final int EVENTLIST_GROWTH;
   private static final int STACK_SIZE;
   private static final int INITIAL_HEAP_SIZE;
   private static final int INITIAL_QUEUE_SIZE;
   private static final int INITIAL_EVENTLIST_SIZE;
   private static final int INITIAL_EVENTQUEUE_SIZE;
   private static final int EVENTQUEUE_GROWTH;
   public static final int QUEUE_ELEMENT_SIZE;

   public Tasks() {
      this._bufferQueue = new int[222];
      this._eventList = new int[100];
      this._currentStack = new int[50];
      this._bufferStack = new int[50];
      this._bufferEventQueue = new int[20];
      this._currentEventQueue = new int[20];
      this.init();
   }

   public final void init() {
      this._heapEnd = 0;
      this._currentQueueCur = this._currentQueueEnd = -1;
      this._bufferQueueCur = this._bufferQueueTail = this._bufferQueueEnd = -1;
      this._eventListEnd = -1;
      this._numQueue = 0;
      this._bufferStackEnd = this._currentStackEnd = -1;
      this._currentEventQueueHead = -1;
      this._bufferEventQueueEnd = -1;
      this._currentEventQueueEnd = -1;
   }

   public final synchronized void captureQueueSnapshot() {
      int[] swap = this._currentQueue;
      this._currentQueue = this._bufferQueue;
      this._bufferQueue = swap;
      swap = this._currentStack;
      this._currentStack = this._bufferStack;
      this._bufferStack = swap;
      this._currentStackEnd = this._bufferStackEnd;
      this._bufferStackEnd = -1;
      this._currentQueueCur = this._bufferQueueCur;
      this._currentQueueEnd = this._bufferQueueEnd;
      this._bufferQueueCur = this._bufferQueueTail = this._bufferQueueEnd = -1;
      this._numQueue = 0;
      swap = this._currentEventQueue;
      this._currentEventQueue = this._bufferEventQueue;
      this._bufferEventQueue = swap;
      this._currentEventQueueEnd = this._bufferEventQueueEnd;
      this._currentEventQueueHead = this._currentEventQueueEnd != -1 ? 0 : -1;
      this._bufferEventQueueEnd = -1;
   }

   public final void setEventActive(boolean active) {
      this._eventActive = active;
   }

   public final synchronized void enqueue(int type, int value) {
      if (!this._eventActive) {
         if (this._bufferQueueEnd == -1) {
            this._bufferQueueCur = 0;
         }

         int nextPosition = this._bufferQueueEnd + 1;
         if (nextPosition + 2 >= this._bufferQueue.length) {
            if (this._bufferStackEnd != -1) {
               nextPosition = this._bufferStack[this._bufferStackEnd--];
            } else {
               this._bufferQueue = this.resize(this._bufferQueue, 102);
               this._bufferQueueEnd += 3;
            }
         } else {
            this._bufferQueueEnd += 3;
         }

         this._bufferQueue[nextPosition] = type;
         this._bufferQueue[nextPosition + 1] = value;
         this._bufferQueue[nextPosition + 2] = -1;
         if (this._numQueue != 0) {
            this._bufferQueue[this._bufferQueueTail + 2] = nextPosition;
         }

         this._bufferQueueTail = nextPosition;
         this._numQueue++;
      } else {
         this._eventListEnd++;
         if (this._eventListEnd + 1 >= this._eventList.length) {
            this._eventList = this.resize(this._eventList, 50);
         }

         this._eventList[this._eventListEnd++] = type;
         this._eventList[this._eventListEnd] = value;
      }
   }

   public final synchronized void enqueueEvent(int type, int value) {
      if (this._bufferEventQueueEnd + 2 >= this._bufferEventQueue.length) {
         this._bufferEventQueue = this.resize(this._bufferEventQueue, 10);
      }

      this._bufferEventQueue[++this._bufferEventQueueEnd] = type;
      this._bufferEventQueue[++this._bufferEventQueueEnd] = value;
   }

   public final void mergeEventList() {
      if (this._eventListEnd != -1) {
         int writePosition = 0;
         int link = this._currentQueueCur;

         int i;
         for (i = this._eventListEnd - 1; i >= 0 && this._currentStackEnd != -1; i -= 2) {
            writePosition = this._currentStack[this._currentStackEnd--];
            this._currentQueue[writePosition] = this._eventList[i];
            this._currentQueue[writePosition + 1] = this._eventList[i + 1];
            this._currentQueue[writePosition + 2] = link;
            link = writePosition;
         }

         int reqLength = ((i >> 1) + 1) * 3;
         if (reqLength + this._currentQueueEnd >= this._currentQueue.length) {
            if (reqLength > 102) {
               this._currentQueue = this.resize(this._currentQueue, reqLength);
            } else {
               this._currentQueue = this.resize(this._currentQueue, 102);
            }
         }

         writePosition = this._currentQueueEnd + 1;

         while (i >= 0) {
            this._currentQueue[writePosition++] = this._eventList[i];
            this._currentQueue[writePosition++] = this._eventList[i + 1];
            this._currentQueue[writePosition++] = link;
            link = writePosition - 3;
            i -= 2;
         }

         this._currentQueueEnd = writePosition - 1;
         this._eventListEnd = -1;
         this._currentQueueCur = link;
      }
   }

   public final void dequeue(int[] ret) {
      if (ret == null) {
         throw new Object("Invalid Array: null");
      }

      if (ret == null || ret.length < 2) {
         throw new Object("Invalid Array: Size is too small.");
      }

      if (this._currentQueueCur != -1) {
         ret[0] = this._currentQueue[this._currentQueueCur];
         ret[1] = this._currentQueue[this._currentQueueCur + 1];
         if (this._currentStackEnd + 1 < this._currentStack.length) {
            this._currentStack[++this._currentStackEnd] = this._currentQueueCur;
         }

         this._currentQueueCur = this._currentQueue[this._currentQueueCur + 2];
         if (this._currentQueueCur == -1) {
            this._currentQueueEnd = -1;
            this._currentStackEnd = -1;
         }
      } else if (this._currentEventQueueHead != -1) {
         ret[0] = this._currentEventQueue[this._currentEventQueueHead++];
         ret[1] = this._currentEventQueue[this._currentEventQueueHead++];
         if (this._currentEventQueueHead >= this._currentEventQueueEnd) {
            this._currentEventQueueHead = this._currentEventQueueEnd = -1;
         }
      }
   }

   public final boolean queueHasMoreElements() {
      return this._currentQueueCur != -1 || this._currentEventQueueEnd != -1;
   }

   public final int removeAllInstancesOf(int type, int value) {
      int found = 0;
      int current = this._bufferQueueCur;
      int lastlink = -1;

      while (current != -1) {
         if (this._bufferQueue[current] == type && this._bufferQueue[current + 1] == value) {
            if (this._bufferStackEnd + 1 < this._bufferStack.length) {
               this._bufferStack[++this._bufferStackEnd] = current;
            }

            current = this._bufferQueue[current + 2];
            if (lastlink == -1) {
               this._bufferQueueCur = current;
               if (current == -1) {
                  this._bufferQueueTail = -1;
                  this._bufferQueueEnd = -1;
                  this._bufferStackEnd = -1;
               }
            } else {
               this._bufferQueue[lastlink] = current;
               if (current == -1) {
                  this._bufferQueueTail = lastlink - 2;
               }
            }

            this._numQueue--;
            found++;
         } else {
            lastlink = current + 2;
            current = this._bufferQueue[lastlink];
         }
      }

      return found;
   }

   public final void heapInsert(int value) {
      if (this._heapEnd == this._heap.length - 1) {
         this._heap = this.resize(this._heap, 109);
      }

      int curPos = this._heapEnd++;

      while (curPos > 0) {
         int parent = curPos - (2 - curPos % 2) >> 1;
         if (this._heap[parent] <= value) {
            break;
         }

         this._heap[curPos] = this._heap[parent];
         curPos = parent;
      }

      this._heap[curPos] = value;
   }

   public final int peekInHeap() {
      return this._heapEnd == 0 ? -1 : this._heap[0];
   }

   public final int removeFromHeap() {
      if (this._heapEnd == 0) {
         return -1;
      }

      int ret = this._heap[0];

      while (this._heap[0] == ret && this._heapEnd != 0) {
         this._heap[0] = this._heap[--this._heapEnd];
         int cur = 0;

         while (cur < this._heapEnd) {
            int left = (cur << 1) + 1;
            int right = (cur << 1) + 2;
            if (left < this._heapEnd && this._heap[left] <= this._heap[right] && this._heap[left] < this._heap[cur]) {
               int tmp = this._heap[left];
               this._heap[left] = this._heap[cur];
               this._heap[cur] = tmp;
               cur = left;
            } else {
               if (right >= this._heapEnd || this._heap[left] <= this._heap[right] || this._heap[right] >= this._heap[cur]) {
                  break;
               }

               int tmp = this._heap[right];
               this._heap[right] = this._heap[cur];
               this._heap[cur] = tmp;
               cur = right;
            }
         }
      }

      return ret;
   }

   public final boolean heapHasElements() {
      return this._heapEnd > 0;
   }

   private final int[] resize(int[] array, int incSize) {
      int[] ret = new int[array.length + incSize];
      System.arraycopy(array, 0, ret, 0, array.length);
      return ret;
   }

   public final int queueSize() {
      int temp = 0;
      if (this._bufferEventQueueEnd != -1) {
         temp = this._bufferEventQueueEnd + 1 >> 1;
      }

      return this._numQueue + temp;
   }
}
