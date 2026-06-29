package net.rim.wica.runtime.messaging.internal.inbound;

import java.util.Hashtable;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.internal.util.ConcurrentQueue;
import net.rim.wica.runtime.messaging.internal.util.ConcurrentQueue$Iterator;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;

final class WicletQueueManager$ReplacementAlgorithm {
   private WicletQueueManager$ReplacementAlgorithm() {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final boolean replace(Hashtable table, ConcurrentQueue q, Message m) {
      boolean var10 = false /* VF: Semaphore variable */;

      int size;
      label103: {
         boolean var17;
         label102: {
            label101: {
               label100: {
                  try {
                     label98:
                     try {
                        var10 = true;
                        String e = m.openReadableDataStream().readString();
                        WicletQueueManager$Position pos = (WicletQueueManager$Position)table.get(e);
                        if (pos == null) {
                           size = replacementNotFound(table, q, m, e, pos);
                           var10 = false;
                           break label103;
                        }

                        q.lock();
                        size = q.size();
                        if (size < pos._posFromTail) {
                           var17 = replacementNotFound(table, q, m, e, pos);
                           var10 = false;
                           break label102;
                        }

                        if (size > pos._posFromHead + pos._posFromTail && tryOriginalPosition(q, m, e, pos)) {
                           var17 = true;
                           var10 = false;
                           break label101;
                        }

                        var17 = iterate(table, q, Math.min(size, pos._posFromHead), m, e, pos);
                        var10 = false;
                        break label100;
                     } catch (Throwable var13) {
                        InternalLogger.logError("MDS Runtime Messaging", "Could not process keep last notification", e, m);
                        var10 = false;
                        break label98;
                     }
                  } finally {
                     if (var10) {
                        q.unlock();
                     }
                  }

                  q.unlock();
                  return false;
               }

               q.unlock();
               return var17;
            }

            q.unlock();
            return var17;
         }

         q.unlock();
         return var17;
      }

      q.unlock();
      return (boolean)size;
   }

   private static final boolean replacementNotFound(Hashtable table, ConcurrentQueue q, Message m, String id, WicletQueueManager$Position position) {
      q.put(m);
      if (position == null) {
         position = new WicletQueueManager$Position(q.size() - 1, 0);
         table.put(id, position);
         return false;
      } else {
         position.update(q.size() - 1, 0);
         return false;
      }
   }

   private static final boolean tryOriginalPosition(ConcurrentQueue q, Message m, String id, WicletQueueManager$Position position) {
      ConcurrentQueue$Iterator i = q.iterator();
      i.skip(position._posFromHead);
      Message existing = (Message)i.next();
      if (canReplace(existing, m, id)) {
         i.replace(m);
         position._posFromTail = q.size() - position._posFromHead - 1;
         return true;
      } else {
         return false;
      }
   }

   private static final boolean iterate(Hashtable table, ConcurrentQueue q, int endPosition, Message current, String id, WicletQueueManager$Position position) {
      ConcurrentQueue$Iterator i = q.iterator();

      for (int j = 0; j < endPosition; j++) {
         Message existing = (Message)i.next();
         if (canReplace(existing, current, id)) {
            i.replace(current);
            position.update(j, q.size() - j - 1);
            return true;
         }
      }

      return replacementNotFound(table, q, current, id, position);
   }

   private static final boolean canReplace(Message existing, Message current, String id) {
      return existing.getMessageCode() == current.getMessageCode() && existing.openReadableDataStream().readString().equals(id);
   }
}
