package com.sun.cldc.i18n;

import net.rim.device.api.system.PersistentContent;

class Helper$LocalStrongReferences {
   private Object _universalReaderReference;
   private Object _lastReaderReference;
   private Object _lastWriterReference;
   private static Helper$ReferenceCleaner _referenceCleaner = Helper$ReferenceCleaner.getInstance();

   public Helper$LocalStrongReferences() {
      _referenceCleaner.addLocalStrongReferences(this);
   }

   public void storeUniversalReaderStrongReference(Object reference) {
      if (_referenceCleaner.keepStrongReferences()) {
         this._universalReaderReference = reference;
      } else {
         PersistentContent.markAsPlaintext(reference);
      }
   }

   public void storeLastReaderStrongReference(Object reference) {
      if (_referenceCleaner.keepStrongReferences()) {
         this._lastReaderReference = reference;
      } else {
         PersistentContent.markAsPlaintext(reference);
      }
   }

   public void storeLastWriterStrongReference(Object reference) {
      if (_referenceCleaner.keepStrongReferences()) {
         this._lastWriterReference = reference;
      } else {
         PersistentContent.markAsPlaintext(reference);
      }
   }

   public void clearStrongReferences() {
      this._universalReaderReference = null;
      this._lastReaderReference = null;
      this._lastWriterReference = null;
   }

   @Override
   public int hashCode() {
      int hashCode = 0;
      if (this._universalReaderReference != null) {
         hashCode ^= this._universalReaderReference.hashCode();
      }

      if (this._lastReaderReference != null) {
         hashCode ^= this._lastReaderReference.hashCode();
      }

      if (this._lastWriterReference != null) {
         hashCode ^= this._lastWriterReference.hashCode();
      }

      return hashCode;
   }
}
