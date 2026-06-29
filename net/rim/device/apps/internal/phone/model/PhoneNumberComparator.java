package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.internal.phone.pattern.SmartDialingOptions;
import net.rim.vm.WeakReference;

public final class PhoneNumberComparator {
   private String _originalA;
   private String _originalB;
   private boolean _isConvertedA;
   private StringBuffer _numberA = (StringBuffer)(new Object());
   private StringBuffer _numberB = (StringBuffer)(new Object());
   private StringBuffer _dtmfA = (StringBuffer)(new Object());
   private StringBuffer _dtmfB = (StringBuffer)(new Object());
   private int _nnl = Math.max(7, SmartDialingOptions.getOptions().getNationalPhoneNumberLength());
   private int _comparisonFlags;
   private static final int A_SUBSET_OF_B = 1;
   private static final int B_SUBSET_OF_A = 2;
   private static final int EXTENSION_MATCH = 4;
   private static final int NNL_MATCH = 8;
   private static final int NUMBER_MATCH = 3;
   private static final int EXACT_MATCH = 7;
   private static final int NO_MATCH = 0;
   private static final long SCRATCH_KEY = -8445063537528043454L;
   private static WeakReference _comparatorWR;

   public static final PhoneNumberComparator getScratchComparator() {
      PhoneNumberComparator comparator = (PhoneNumberComparator)_comparatorWR.get();
      if (comparator == null) {
         comparator = new PhoneNumberComparator();
         _comparatorWR.set(comparator);
      }

      return comparator;
   }

   public PhoneNumberComparator() {
   }

   public PhoneNumberComparator(PhoneNumberModel modelA) {
      this._originalA = modelA.toString();
      this._isConvertedA = false;
   }

   public final boolean isExactMatch() {
      return (this._comparisonFlags & 7) == 7;
   }

   public final boolean isNumberMatch() {
      return (this._comparisonFlags & 3) == 3;
   }

   public final boolean isExtensionMatch() {
      return (this._comparisonFlags & 4) == 4;
   }

   public final boolean isSubsetMatch() {
      return this.isExtensionMatch() && ((this._comparisonFlags & 1) == 1 || (this._comparisonFlags & 2) == 2);
   }

   public final int numberLength() {
      if ((this._comparisonFlags & 1) != 0) {
         return this._numberA.length();
      } else {
         return (this._comparisonFlags & 2) != 0 ? this._numberB.length() : 0;
      }
   }

   public final int extensionLength() {
      return this.isExtensionMatch() ? this._dtmfA.length() : 0;
   }

   public final void compare(PhoneNumberModel modelB) {
      this._originalB = modelB.toString();
      if (this._originalB.equals(this._originalA)) {
         this._comparisonFlags = 7;
      } else {
         PhoneNumberConverter.convertForTransmission(this._numberB, this._dtmfB, this._originalB.toCharArray(), true);
         if (!this._isConvertedA) {
            PhoneNumberConverter.convertForTransmission(this._numberA, this._dtmfA, this._originalA.toCharArray(), true);
            this._isConvertedA = true;
         }

         this.compare();
      }
   }

   public final void compare(PhoneNumberModel modelA, PhoneNumberModel modelB) {
      String a = modelA.toString();
      if (!a.equals(this._originalA)) {
         this._originalA = a;
         this._isConvertedA = false;
      }

      this.compare(modelB);
   }

   private final void compare() {
      this._comparisonFlags = 0;
      if (dtmfMatch(this._dtmfA, this._dtmfB)) {
         this._comparisonFlags |= 4;
      }

      int lenA = this._numberA.length();
      int lenB = this._numberB.length();
      if (lenA == lenB) {
         if (regionMatches(this._numberA, 0, this._numberB, 0, lenB)) {
            this._comparisonFlags |= 3;
         }
      } else if (lenA < lenB) {
         if (lenA >= 7 && regionMatches(this._numberA, 0, this._numberB, lenB - lenA, lenA)) {
            this._comparisonFlags |= 1;
         }
      } else if (lenB >= 7 && regionMatches(this._numberA, lenA - lenB, this._numberB, 0, lenB)) {
         this._comparisonFlags |= 2;
      }

      if (lenA >= this._nnl && lenB >= this._nnl && regionMatches(this._numberA, lenA - this._nnl, this._numberB, lenB - this._nnl, this._nnl)) {
         this._comparisonFlags |= 8;
      }
   }

   private static final boolean regionMatches(StringBuffer strA, int idxA, StringBuffer strB, int idxB, int count) {
      idxA += count;
      idxB += count;

      while (count > 0) {
         idxA--;
         if (strA.charAt(idxA) != strB.charAt(--idxB)) {
            return false;
         }

         count--;
      }

      return true;
   }

   private static final boolean dtmfMatch(StringBuffer dtmfA, StringBuffer dtmfB) {
      int lenA = dtmfA.length();
      int lenB = dtmfB.length();
      if (lenA == 0 && lenB == 0) {
         return true;
      }

      int idxA = 0;
      int idxB = 0;
      char chA = 0;
      char chB = 0;

      while (true) {
         while (idxA < lenA) {
            chA = dtmfA.charAt(idxA);
            if (PhoneNumberServices.isDTMFKey(chA)) {
               break;
            }

            idxA++;
         }

         while (idxB < lenB) {
            chB = dtmfB.charAt(idxB);
            if (PhoneNumberServices.isDTMFKey(chB)) {
               break;
            }

            idxB++;
         }

         if (idxA == lenA || idxB == lenB) {
            return idxA == lenA && idxB == lenB;
         }

         if (chA != chB) {
            return false;
         }

         idxA++;
         idxB++;
      }
   }

   static {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      _comparatorWR = (WeakReference)reg.get(-8445063537528043454L);
      if (_comparatorWR == null) {
         _comparatorWR = (WeakReference)(new Object(null));

         label21:
         try {
            reg.put(-8445063537528043454L, _comparatorWR);
            return;
         } finally {
            break label21;
         }
      }
   }
}
