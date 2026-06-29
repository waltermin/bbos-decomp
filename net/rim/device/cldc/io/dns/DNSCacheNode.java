package net.rim.device.cldc.io.dns;

import java.util.Vector;

public final class DNSCacheNode {
   private String _domainLabel;
   private Vector _resources;
   private int _expiryTime;
   private boolean _nameError;
   private DNSCacheNode _leftChild;
   private DNSCacheNode _rightSibling;

   public DNSCacheNode(String label) {
      this._domainLabel = label;
      this._expiryTime = -1;
   }

   public DNSCacheNode(String label, DNSCachedRR resource) {
      this._domainLabel = label;
      this._resources = new Vector();
      this._resources.addElement(resource);
      this._expiryTime = resource.getExpiryTime();
   }

   public DNSCacheNode(String label, DNSCachedRR[] resources) {
      this._domainLabel = label;
      if (resources != null && resources.length > 0) {
         this._resources = new Vector(resources.length);
         this._expiryTime = resources[0].getExpiryTime();

         for (int i = 0; i < resources.length; i++) {
            this._resources.addElement(resources[i]);
            this._expiryTime = Math.min(this._expiryTime, resources[i].getExpiryTime());
         }
      } else {
         this._expiryTime = -1;
      }
   }

   public final String getDomainLabel() {
      return this._domainLabel;
   }

   public final void addToResources(DNSCachedRR resource) {
      if (this._resources == null) {
         this._resources = new Vector();
         this._nameError = false;
      }

      this._resources.addElement(resource);
      this._expiryTime = this._expiryTime < 0 ? resource.getExpiryTime() : Math.min(this._expiryTime, resource.getExpiryTime());
   }

   public final void addToResources(DNSCachedRR[] resources) {
      if (resources.length != 0) {
         if (this._resources == null) {
            this._resources = new Vector(resources.length);
            this._expiryTime = resources[0].getExpiryTime();
            this._nameError = false;
         } else {
            this._resources.ensureCapacity(this._resources.size() + resources.length);
         }

         for (int i = 0; i < resources.length; i++) {
            this._resources.addElement(resources[i]);
            this._expiryTime = Math.min(this._expiryTime, resources[i].getExpiryTime());
         }
      }
   }

   public final int setResources(DNSCachedRR resource) {
      int size = this._resources == null ? 0 : this._resources.size();
      this._resources = new Vector();
      this._resources.addElement(resource);
      this._expiryTime = resource.getExpiryTime();
      this._nameError = false;
      return size;
   }

   public final int setResources(DNSCachedRR[] resources) {
      int size = this._resources == null ? 0 : this._resources.size();
      if (resources.length > 0) {
         this._resources = new Vector(resources.length);
         this._expiryTime = resources[0].getExpiryTime();

         for (int i = 0; i < resources.length; i++) {
            this._resources.addElement(resources[i]);
            this._expiryTime = Math.min(this._expiryTime, resources[i].getExpiryTime());
         }
      } else {
         this._resources = null;
         this._expiryTime = -1;
      }

      this._nameError = false;
      return size;
   }

   public final int removeResources(int type) {
      if (this._resources == null) {
         return 0;
      }

      int removed = 0;
      this._expiryTime = -1;

      for (int i = this._resources.size() - 1; i >= 0; i--) {
         DNSCachedRR current = (DNSCachedRR)this._resources.elementAt(i);
         if (current.getType() == type) {
            this._resources.removeElementAt(i);
            removed++;
         } else {
            this._expiryTime = this._expiryTime < 0 ? current.getExpiryTime() : Math.min(this._expiryTime, current.getExpiryTime());
         }
      }

      return removed;
   }

   public final int removeResourceWithData(Object data) {
      int newExpiryTime = Integer.MAX_VALUE;
      int found = 0;

      for (int i = this._resources.size() - 1; i >= 0; i--) {
         DNSCachedRR rr = (DNSCachedRR)this._resources.elementAt(i);
         if (rr.getData() == data) {
            found++;
            this._resources.removeElementAt(i);
         } else if (newExpiryTime > rr.getExpiryTime()) {
            newExpiryTime = rr.getExpiryTime();
         }
      }

      this._expiryTime = newExpiryTime;
      return found;
   }

   public final DNSCachedRR[] getResources() {
      if (this._resources == null) {
         return null;
      }

      DNSCachedRR[] resources = new DNSCachedRR[this._resources.size()];
      this._resources.copyInto(resources);
      return resources;
   }

   public final DNSCachedRR[] getResources(int type) {
      if (this._resources == null) {
         return null;
      }

      Vector matched = new Vector(this._resources.size());

      for (int i = this._resources.size() - 1; i >= 0; i--) {
         DNSCachedRR current = (DNSCachedRR)this._resources.elementAt(i);
         if (current.getType() == type) {
            matched.addElement(current);
         }
      }

      if (matched.size() == 0) {
         return null;
      }

      DNSCachedRR[] resources = new DNSCachedRR[matched.size()];
      matched.copyInto(resources);
      return resources;
   }

   public final int getExpiryTime() {
      return this._expiryTime;
   }

   public final int setNameError(int expiryTime) {
      int size = this.deleteData();
      this._expiryTime = expiryTime;
      this._nameError = true;
      return size;
   }

   public final boolean isNameError() {
      return this._nameError;
   }

   public final int deleteData() {
      int size = this._resources == null ? 0 : this._resources.size();
      this._resources = null;
      this._expiryTime = -1;
      this._nameError = false;
      return size;
   }

   private final boolean containsData() {
      return this._nameError || this._resources != null && this._resources.size() != 0;
   }

   public final boolean canBeDeleted() {
      return !this.containsData() && this._leftChild == null;
   }

   final void addChild(DNSCacheNode child) {
      if (child != null) {
         if (this._leftChild == null) {
            this._leftChild = child;
         } else if (child._domainLabel.compareTo(this._leftChild._domainLabel) <= 0) {
            child._rightSibling = this._leftChild;
            this._leftChild = child;
         } else {
            this._leftChild.addSibling(child);
         }
      }
   }

   private final void removeDeadChildren() {
      if (this._leftChild != null) {
         this._leftChild.removeDeadSiblings();
         if (this._leftChild.canBeDeleted()) {
            this._leftChild = this._leftChild._rightSibling;
         }
      }
   }

   final void removeDeadDescendants() {
      for (DNSCacheNode traverser = this._leftChild; traverser != null; traverser = traverser._rightSibling) {
         traverser.removeDeadDescendants();
      }

      this.removeDeadChildren();
   }

   final DNSCacheNode getChild() {
      return this._leftChild;
   }

   final DNSCacheNode getChild(String childLabel) {
      return this._leftChild == null ? null : this._leftChild.getSibling(childLabel);
   }

   private final void addSibling(DNSCacheNode sibling) {
      DNSCacheNode traverser = this;

      while (traverser._rightSibling != null && sibling._domainLabel.compareTo(traverser._rightSibling._domainLabel) >= 0) {
         traverser = traverser._rightSibling;
      }

      sibling._rightSibling = traverser._rightSibling;
      traverser._rightSibling = sibling;
   }

   private final DNSCacheNode getSibling(String siblingLabel) {
      DNSCacheNode traverser = this;

      while (traverser != null && !traverser._domainLabel.equals(siblingLabel)) {
         traverser = traverser._rightSibling;
      }

      return traverser;
   }

   private final void removeDeadSiblings() {
      DNSCacheNode traverser = this;

      while (traverser._rightSibling != null) {
         if (traverser._rightSibling.canBeDeleted()) {
            traverser._rightSibling = traverser._rightSibling._rightSibling;
         } else {
            traverser = traverser._rightSibling;
         }
      }
   }
}
