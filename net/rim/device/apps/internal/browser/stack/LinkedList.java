package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.util.Persistable;

final class LinkedList implements Persistable {
   CacheNode _head;
   CacheNode _tail;

   public final void insertTail(CacheNode node) {
      if (this._tail != null) {
         this._tail.setNext(node);
         node.setPrev(this._tail);
         this._tail = node;
      } else {
         this._head = this._tail = node;
         node.setPrev(null);
      }

      node.setNext(null);
   }

   public final CacheNode removeHead() {
      CacheNode oldNode = this._head;
      if (this._tail == oldNode) {
         this._head = this._tail = null;
      } else {
         this._head = oldNode.getNext();
         this._head.setPrev(null);
      }

      if (oldNode != null) {
         oldNode.setPrev(null);
         oldNode.setNext(null);
      }

      return oldNode;
   }
}
