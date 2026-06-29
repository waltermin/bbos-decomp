package net.rim.device.internal.ui.autotext;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.IntHashtable;

final class AutoTextDatabase$Enumerator implements Enumeration {
   private IntHashtable _tables;
   private Enumeration _tableEnumeration;
   private Enumeration _elementEnumeration;
   private Enumeration _nextElementEnumeration;
   private int _localeCode;

   AutoTextDatabase$Enumerator(IntHashtable tables, Locale locale) {
      this._tables = tables;
      Hashtable table = null;
      if (locale != null) {
         this._localeCode = locale.getCode();
         if ((this._localeCode & -65536) == 2053636096) {
            this._localeCode = 1701707776;
         }

         table = (Hashtable)this._tables.get(this._localeCode);
      } else {
         this._tableEnumeration = this._tables.elements();
         if (this._tableEnumeration.hasMoreElements()) {
            table = (Hashtable)this._tableEnumeration.nextElement();
         }
      }

      if (table != null) {
         this._elementEnumeration = table.elements();
      }

      this.fetchNextElementEnumeration();
   }

   private final void fetchNextElementEnumeration() {
      this._nextElementEnumeration = null;
      if (this._tableEnumeration == null) {
         if (this._localeCode == 0) {
            return;
         }

         if ((this._localeCode & 65535) != 0) {
            this._localeCode &= -65536;
         } else {
            this._localeCode = 0;
         }

         Hashtable nextTable = (Hashtable)this._tables.get(this._localeCode);
         if (nextTable != null) {
            this._nextElementEnumeration = nextTable.elements();
         }

         if (this._nextElementEnumeration == null || !this._nextElementEnumeration.hasMoreElements()) {
            this.fetchNextElementEnumeration();
            return;
         }
      } else {
         while (this._tableEnumeration.hasMoreElements() && this._nextElementEnumeration == null) {
            this._nextElementEnumeration = ((Hashtable)this._tableEnumeration.nextElement()).elements();
            if (!this._nextElementEnumeration.hasMoreElements()) {
               this._nextElementEnumeration = null;
            }
         }
      }
   }

   @Override
   public final boolean hasMoreElements() {
      return this._nextElementEnumeration != null || this._elementEnumeration != null && this._elementEnumeration.hasMoreElements();
   }

   @Override
   public final Object nextElement() {
      if (this._elementEnumeration == null || !this._elementEnumeration.hasMoreElements()) {
         this._elementEnumeration = this._nextElementEnumeration;
         this.fetchNextElementEnumeration();
         if (this._elementEnumeration == null) {
            throw new NoSuchElementException();
         }
      }

      return this._elementEnumeration.nextElement();
   }
}
