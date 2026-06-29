package net.rim.plazmic.internal.mediaengine.util;

public class ProfileVariable extends ProfileElement {
   private int _value;
   private boolean _locked;
   private int[] _savedValue;
   private String[] _savedName;
   private long[] _savedTime;
   private boolean _save;
   private boolean _saveWrap;
   private int _iSave;

   ProfileVariable(String name) {
      super(name);
      this.initialize();
   }

   public synchronized int set(int iValue) {
      if (!this._locked) {
         this._value = iValue;
      }

      if (this._save) {
         this._iSave++;
         if (this._iSave >= this._savedValue.length) {
            if (!this._saveWrap) {
               this._iSave = this._savedValue.length - 1;
               return this._value;
            }

            this._iSave = 0;
         }

         this._savedValue[this._iSave] = this._value;
         this._savedTime[this._iSave] = System.currentTimeMillis();
         this._savedName[this._iSave] = ProfileManager.getSymbol(this._value);
      }

      return this._value;
   }

   public int get() {
      return this._value;
   }

   @Override
   public void reset() {
      this._value = -1;
      if (this._save) {
         this.clearSaved();
      }
   }

   @Override
   public void initialize() {
      this._locked = false;
      this._save = false;
      this._value = -1;
      this._iSave = -1;
      this._saveWrap = false;
   }

   public synchronized boolean overWrite(int iValue) {
      if (this._locked) {
         this._value = iValue;
         return true;
      } else {
         return false;
      }
   }

   public synchronized void lock() {
      this._locked = true;
   }

   public synchronized void release() {
      this._locked = false;
   }

   public boolean isLocked() {
      return this._locked;
   }

   public boolean setSave(int capacity, boolean wrap) {
      this._save = false;
      this._saveWrap = wrap;
      if (capacity > 0) {
         if (this._savedValue == null || this._savedValue.length < capacity) {
            this._savedValue = new int[capacity];
            this._savedTime = new long[capacity];
            this._savedName = new Object[capacity];
         }

         if (this._savedValue != null && this._savedTime != null && this._savedName != null) {
            this._save = true;
            this._iSave = -1;
         }
      }

      return this._save;
   }

   public boolean unsetSave() {
      this._save = false;
      this._iSave = -1;
      return true;
   }

   public void clearSaved() {
      this._iSave = -1;
   }

   public boolean isSaved() {
      return this._save;
   }

   public int getSavedSize() {
      return this._iSave + 1;
   }

   public int getSavedValue(int iSaved) {
      return iSaved > 0 && iSaved <= this._iSave ? this._savedValue[iSaved] : -1;
   }

   public long getSavedTime(int iSaved) {
      return iSaved > 0 && iSaved <= this._iSave ? this._savedTime[iSaved] : -1;
   }

   public String getSavedName(int iSaved) {
      return iSaved > 0 && iSaved <= this._iSave ? this._savedName[iSaved] : null;
   }
}
