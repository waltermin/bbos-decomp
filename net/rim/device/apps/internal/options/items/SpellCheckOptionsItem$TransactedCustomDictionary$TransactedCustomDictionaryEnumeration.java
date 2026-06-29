package net.rim.device.apps.internal.options.items;

import java.util.Enumeration;

final class SpellCheckOptionsItem$TransactedCustomDictionary$TransactedCustomDictionaryEnumeration implements Enumeration {
   private Enumeration _enumer;
   private Enumeration _addedEnumer;
   private Object _next;
   private final SpellCheckOptionsItem$TransactedCustomDictionary this$0;

   SpellCheckOptionsItem$TransactedCustomDictionary$TransactedCustomDictionaryEnumeration(SpellCheckOptionsItem$TransactedCustomDictionary _1) {
      this.this$0 = _1;
   }

   final void reset() {
      this._enumer = this.this$0._cleared ? null : this.this$0._customDic.getElements();
      this._addedEnumer = this.this$0._added.keys();
      this._next = null;
   }

   @Override
   public final boolean hasMoreElements() {
      if (this._next != null) {
         return true;
      }

      if (this._enumer != null) {
         while (this._enumer.hasMoreElements()) {
            this._next = this._enumer.nextElement();
            if (!this.this$0._deleted.containsKey(this._next)) {
               return true;
            }
         }
      }

      return this._addedEnumer.hasMoreElements();
   }

   @Override
   public final Object nextElement() {
      if (this._next != null) {
         Object next = this._next;
         this._next = null;
         return next;
      }

      if (this._enumer != null) {
         while (this._enumer.hasMoreElements()) {
            Object next = this._enumer.nextElement();
            if (!this.this$0._deleted.containsKey(next)) {
               return next;
            }
         }
      }

      return this._addedEnumer.nextElement();
   }
}
