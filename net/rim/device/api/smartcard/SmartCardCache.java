package net.rim.device.api.smartcard;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;

final class SmartCardCache implements ReaderStatusListener {
   private Hashtable _hashtable = (Hashtable)(new Object());
   private SmartCardCache$Data _emptyData = new SmartCardCache$Data(null);
   private static final long ID = -5307011363447012213L;
   private static SmartCardCache _instance;

   final void setATR(SmartCardReader reader, AnswerToReset atr) {
      if (this.checkReader(reader)) {
         this.createData(reader)._atr = atr;
      }
   }

   final SmartCardID getSmartCardID(SmartCardReader reader) {
      return this.getData(reader)._id;
   }

   final void setSmartCardID(SmartCardReader reader, SmartCardID smartCardID) {
      if (this.checkReader(reader)) {
         this.createData(reader)._id = smartCardID;
      }
   }

   final int getMaxLoginAttempts(SmartCardReader reader) {
      return this.getData(reader)._maxLoginAttempts;
   }

   final void setMaxLoginAttempts(SmartCardReader reader, int maxAttempts) {
      if (this.checkReader(reader)) {
         this.createData(reader)._maxLoginAttempts = maxAttempts;
      }
   }

   final int getRemainingLoginAttempts(SmartCardReader reader) {
      return this.getData(reader)._remainingLoginAttempts;
   }

   final void setRemainingLoginAttempts(SmartCardReader reader, int remainingAttempts) {
      if (this.checkReader(reader)) {
         this.createData(reader)._remainingLoginAttempts = remainingAttempts;
      }
   }

   final void login(SmartCardReader reader, boolean success) {
      if (this.checkReader(reader)) {
         SmartCardCache$Data data = this.createData(reader);
         data._remainingLoginAttempts = success ? data._maxLoginAttempts : Math.max(data._remainingLoginAttempts - 1, -1);
      }
   }

   final AnswerToReset getATR(SmartCardReader reader) {
      return this.getData(reader)._atr;
   }

   @Override
   public final void readerStatus(SmartCardReader reader, int status) {
      if (status == 1 || status == 0) {
         this._hashtable.remove(reader);
      }
   }

   private final SmartCardCache$Data createData(SmartCardReader reader) {
      SmartCardCache$Data data = (SmartCardCache$Data)this._hashtable.get(reader);
      if (data == null) {
         data = new SmartCardCache$Data(null);
         this._hashtable.put(reader, data);
      }

      return data;
   }

   static final SmartCardCache getInstance() {
      return _instance;
   }

   private final SmartCardCache$Data getData(SmartCardReader reader) {
      SmartCardCache$Data data = (SmartCardCache$Data)this._hashtable.get(reader);
      if (data == null) {
         data = this._emptyData;
      }

      return data;
   }

   private final boolean checkReader(SmartCardReader reader) {
      if (reader == null) {
         throw new Object();
      }

      if (!reader.isInsertionRemovalDetectable()) {
         return false;
      }

      if (!this._hashtable.containsKey(reader)) {
         reader.addListener(this);
      }

      return true;
   }

   private SmartCardCache() {
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      _instance = (SmartCardCache)registry.getOrWaitFor(-5307011363447012213L);
      if (_instance == null) {
         _instance = new SmartCardCache();
         registry.put(-5307011363447012213L, _instance);
      }
   }
}
