package com.sun.cldc.i18n;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.WeakReference;

class Helper$ReferenceCleaner implements PersistentContentListener {
   private boolean _keepStrongReferences = true;
   private Vector _references = new Vector();

   private Helper$ReferenceCleaner() {
   }

   public static Helper$ReferenceCleaner getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      if (ar == null) {
         return new Helper$ReferenceCleaner();
      }

      Helper$ReferenceCleaner referenceCleaner = (Helper$ReferenceCleaner)ar.getOrWaitFor(4119503239558518103L);
      if (referenceCleaner == null) {
         referenceCleaner = new Helper$ReferenceCleaner();
         ar.put(4119503239558518103L, referenceCleaner);
      }

      return referenceCleaner;
   }

   public synchronized void addLocalStrongReferences(Helper$LocalStrongReferences localStrongReferences) {
      WeakReferenceUtilities.purge(this._references);
      this._references.addElement(new WeakReference(localStrongReferences));
   }

   public boolean keepStrongReferences() {
      return this._keepStrongReferences;
   }

   @Override
   public synchronized void persistentContentStateChanged(int state) {
      if (state == 1) {
         this._keepStrongReferences = true;
      } else {
         if (this._keepStrongReferences) {
            this._keepStrongReferences = false;

            for (int i = this._references.size() - 1; i >= 0; i--) {
               Helper$LocalStrongReferences localStrongReferences = (Helper$LocalStrongReferences)((WeakReference)this._references.elementAt(i)).get();
               if (localStrongReferences != null) {
                  localStrongReferences.clearStrongReferences();
               } else {
                  this._references.removeElementAt(i);
               }
            }
         }
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
   }
}
