package net.rim.device.cldc.io.dns;

import java.util.Vector;
import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.util.SimpleSortingVector;

public final class DNSCache implements LowMemoryListener {
   private DNSCacheNode _treeHead = new DNSCacheNode("");
   private SimpleSortingVector _expiryOrder = new SimpleSortingVector();
   private int _maxSize;
   private int _curSize;

   public DNSCache() {
      this._expiryOrder.setSort(false);
      this._expiryOrder.setSortComparator(new DNSCache$1(this));
      this._maxSize = -1;
      LowMemoryManager.addLowMemoryListener(this);
   }

   public final void emptyCache() {
      LowMemoryManager.markAsRecoverable(this._treeHead);
      this._treeHead = new DNSCacheNode("");
      this._expiryOrder.removeAllElements();
      this._curSize = 0;
   }

   public final void addToCache(DNSRequest req) {
      this.addToCache(req, getCurrentTime());
   }

   public final void addToCache(DNSRequest req, int timeStamp) {
      DNSMessageIPv4 answer = req.getAnswer();
      if (answer.getTC() != 512) {
         Vector[] responseRRs = new Vector[]{answer.getAnswers(), answer.getAuthorities(), answer.getAdditional()};
         int total = 0;

         for (int i = 0; i < responseRRs.length; i++) {
            if (this._maxSize >= 0 && responseRRs[i].size() > this._maxSize) {
               responseRRs[i] = new Vector();
            } else {
               total += responseRRs[i].size();
            }

            for (int j = responseRRs[i].size() - 1; j >= 0; j--) {
               DNSMessageIPv4Resource tempResource = (DNSMessageIPv4Resource)responseRRs[i].elementAt(j);
               DNSCacheNode tempNode = this.getNode(tempResource.getName());
               if (tempNode != null) {
                  this._curSize = this._curSize - tempNode.removeResources(tempResource.getType());
               }
            }
         }

         if (this._maxSize >= 0) {
            this.pruneToSize(this._maxSize - total);
         }

         for (int i = 0; i < responseRRs.length; i++) {
            for (int j = responseRRs[i].size() - 1; j >= 0; j--) {
               DNSMessageIPv4Resource tempResource = (DNSMessageIPv4Resource)responseRRs[i].elementAt(j);
               DNSCacheNode tempNode = this.createNode(tempResource.getName());
               tempNode.addToResources(new DNSCachedRR(tempResource, timeStamp));
               this._curSize++;
            }
         }
      }
   }

   public final void addNameError(DNSRequest req) {
      this.addNameError(req, getCurrentTime());
   }

   public final void addNameError(DNSRequest req, int timeStamp) {
      Vector authorities = req.getAnswer().getAuthorities();

      for (int i = authorities.size() - 1; i >= 0; i--) {
         DNSMessageIPv4Resource record = (DNSMessageIPv4Resource)authorities.elementAt(i);
         if (record.getType() == 6) {
            this.createNode(req.getQueryString()).setNameError(timeStamp + ((DNSMessageIPv4Resource$SOAData)record.getData()).minimum);
            return;
         }
      }
   }

   public final Vector lookup(String domain, int type) {
      return this.lookupInternal(domain, type, null);
   }

   private final Vector lookupInternal(String domain, int type, Vector touchedLeafs) {
      DNSCacheNode domainNode = this.getNode(domain);
      if (domainNode == null || domainNode.getExpiryTime() < getCurrentTime()) {
         return null;
      }

      if (domainNode.isNameError()) {
         throw new DNSException(7);
      }

      if (touchedLeafs != null && touchedLeafs.contains(domainNode)) {
         return null;
      }

      Vector finalMatches = null;
      DNSCachedRR[] matched = domainNode.getResources(type);
      if (matched != null && matched.length != 0) {
         finalMatches = new Vector(matched.length);

         for (int i = 0; i < matched.length; i++) {
            finalMatches.addElement(matched[i].getData());
         }
      } else {
         matched = domainNode.getResources(5);
         if (matched == null || matched.length == 0) {
            return null;
         }

         if (touchedLeafs == null) {
            touchedLeafs = new Vector();
         }

         touchedLeafs.addElement(domainNode);

         for (int i = matched.length - 1; i >= 0; i--) {
            finalMatches = this.lookupInternal((String)matched[i].getData(), type, touchedLeafs);
            if (finalMatches != null) {
               return finalMatches;
            }
         }
      }

      return finalMatches;
   }

   private final DNSCacheNode getNode(String domain) {
      String[] labels = splitDomain(domain);
      DNSCacheNode traverser = this._treeHead;

      for (int i = labels.length - 1; i >= 0; i--) {
         traverser = traverser.getChild(labels[i]);
         if (traverser == null) {
            return null;
         }
      }

      return traverser;
   }

   private final DNSCacheNode createNode(String domain) {
      String[] labels = splitDomain(domain);
      DNSCacheNode traverser = this._treeHead;

      for (int i = labels.length - 1; i >= 0; i--) {
         DNSCacheNode next = traverser.getChild(labels[i]);
         if (next == null) {
            next = new DNSCacheNode(labels[i]);
            traverser.addChild(next);
            if (i == 0) {
               this._expiryOrder.addElement(next);
            }
         }

         traverser = next;
      }

      return traverser;
   }

   public final void removeExpired() {
      int now = getCurrentTime();
      this._expiryOrder.reSort();

      while (this.removeIfExpired(now)) {
      }

      this._treeHead.removeDeadDescendants();
   }

   private final boolean removeIfExpired(int nowTime) {
      if (this._expiryOrder.size() > 0) {
         DNSCacheNode test = (DNSCacheNode)this._expiryOrder.elementAt(0);
         if (test.getExpiryTime() < nowTime) {
            this._curSize = this._curSize - test.deleteData();
            this._expiryOrder.removeElementAt(0);
            return true;
         }
      }

      return false;
   }

   public final void removeFromCache(String domain, Object cachedData) {
      this.removeFromCacheInternal(domain, cachedData, null);
   }

   private final boolean removeFromCacheInternal(String domain, Object cachedData, Vector touchedLeafs) {
      DNSCacheNode domainNode = this.getNode(domain);
      if (domainNode != null) {
         int num = domainNode.removeResourceWithData(cachedData);
         if (num > 0) {
            this._treeHead.removeDeadDescendants();
            if (domainNode.canBeDeleted()) {
               for (int i = this._expiryOrder.size() - 1; i >= 0; i--) {
                  if (this._expiryOrder.elementAt(i) == domainNode) {
                     this._expiryOrder.removeElementAt(i);
                     return true;
                  }
               }
            }

            return true;
         }

         DNSCachedRR[] matched = domainNode.getResources(5);
         if (matched != null && matched.length > 0) {
            if (touchedLeafs == null) {
               touchedLeafs = new Vector();
            }

            touchedLeafs.addElement(domainNode);

            for (int i = matched.length - 1; i >= 0; i--) {
               if (this.removeFromCacheInternal((String)matched[i].getData(), cachedData, touchedLeafs)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private final void pruneToSize(int size) {
      this._expiryOrder.reSort();
      int index = 0;

      while (this._curSize > size) {
         DNSCacheNode nextExpiry;
         try {
            nextExpiry = (DNSCacheNode)this._expiryOrder.elementAt(index);
         } catch (ArrayIndexOutOfBoundsException aioobe) {
            return;
         }

         if (nextExpiry.isNameError()) {
            index++;
         } else {
            this._curSize = this._curSize - nextExpiry.deleteData();
            this._expiryOrder.removeElementAt(index);
         }
      }
   }

   public final void setMaxSize(int size) {
      this._maxSize = size;
      if (this._maxSize >= 0) {
         this.pruneToSize(this._maxSize);
         this._treeHead.removeDeadDescendants();
      }
   }

   public final int getMaxSize() {
      return this._maxSize;
   }

   public final int getCurSize() {
      return this._curSize;
   }

   public static final int getCurrentTime() {
      return (int)(System.currentTimeMillis() / 1000);
   }

   public static final String[] splitDomain(String domain) {
      int prevIndex = 0;
      Vector pieces = new Vector();

      int index;
      do {
         index = domain.indexOf(46, prevIndex);
         if (index < 0) {
            pieces.addElement(domain.substring(prevIndex));
         } else {
            pieces.addElement(domain.substring(prevIndex, index));
            prevIndex = index + 1;
         }
      } while (index > 0 && prevIndex < domain.length());

      String[] done = new String[pieces.size()];
      pieces.copyInto(done);
      return done;
   }

   @Override
   public final boolean freeStaleObject(int priority) {
      int curSize = this._curSize;
      if (priority == 0) {
         this.removeExpired();
      } else {
         this.emptyCache();
      }

      return this._curSize < curSize;
   }
}
