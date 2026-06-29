package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.wica.runtime.access.internal.data.handlers.BooleanFieldHandler;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;
import net.rim.wica.runtime.access.internal.data.handlers.LongFieldHandler;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.internal.component.InnerDataVector;

class LoadItemHelper {
   BuiltinCollection _builtin;
   DataCollection _dc;

   public LoadItemHelper(BuiltinCollection builtin) {
      if (builtin == null) {
         throw new IllegalArgumentException();
      }

      this._builtin = builtin;
      this._dc = this._builtin;
   }

   public void initVector(Object o) {
      if (o instanceof InnerDataVector && this._builtin instanceof InnerDataArrayFieldProvider) {
         ((InnerDataArrayFieldProvider)this._builtin).initializeVector((InnerDataVector)o);
      }
   }

   public Object getObjectFieldValue(long dataHandle, int field) {
      Object retValue = null;
      if ((32768 & this._dc.getDef().getFieldType(field)) != 0) {
         if (this._builtin.loadItemFromDB(dataHandle)) {
            retValue = this._dc.getObjectFieldValue(dataHandle, field);
         }
      } else if (this._builtin.getObjectFieldHandlers() != null) {
         ObjectFieldHandler ofh = (ObjectFieldHandler)this._builtin.getObjectFieldHandlers().get(field);
         if (ofh != null) {
            retValue = ofh.getValue(this._builtin.getDBItemFromHandle(dataHandle));
         }
      }

      return retValue;
   }

   public boolean getBooleanFieldValue(long dataHandle, int field) {
      boolean retValue = false;
      if ((32768 & this._dc.getDef().getFieldType(field)) != 0) {
         if (this._builtin.loadItemFromDB(dataHandle)) {
            retValue = this._dc.getBooleanFieldValue(dataHandle, field);
         }
      } else if (this._builtin.getBooleanFieldHandlers() != null) {
         BooleanFieldHandler bfh = (BooleanFieldHandler)this._builtin.getBooleanFieldHandlers().get(field);
         if (bfh != null) {
            retValue = bfh.getValue(this._builtin.getDBItemFromHandle(dataHandle));
         }
      }

      return retValue;
   }

   public int getIntFieldValue(long dataHandle, int field) {
      int retValue = -1;
      if ((32768 & this._dc.getDef().getFieldType(field)) != 0) {
         if (this._builtin.loadItemFromDB(dataHandle)) {
            retValue = this._dc.getIntFieldValue(dataHandle, field);
         }
      } else if (this._builtin.getIntFieldHandlers() != null) {
         IntFieldHandler ifh = (IntFieldHandler)this._builtin.getIntFieldHandlers().get(field);
         if (ifh != null) {
            retValue = ifh.getValue(this._builtin.getDBItemFromHandle(dataHandle));
         }
      }

      return retValue;
   }

   public long getLongFieldValue(long dataHandle, int field) {
      long retValue = -1;
      if ((32768 & this._dc.getDef().getFieldType(field)) != 0) {
         if (this._builtin.loadItemFromDB(dataHandle)) {
            retValue = this._dc.getLongFieldValue(dataHandle, field);
         }
      } else if (this._builtin.getLongFieldHandlers() != null) {
         LongFieldHandler lfh = (LongFieldHandler)this._builtin.getLongFieldHandlers().get(field);
         if (lfh != null) {
            retValue = lfh.getValue(this._builtin.getDBItemFromHandle(dataHandle));
         }
      }

      return retValue;
   }
}
