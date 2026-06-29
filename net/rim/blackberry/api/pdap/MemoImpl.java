package net.rim.blackberry.api.pdap;

import javax.microedition.pim.FieldFullException;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMList;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.memo.MemoModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;
import net.rim.vm.Array;

public final class MemoImpl extends PIMItemImpl implements BlackBerryMemo {
   private boolean _committed;
   private MemoListImpl _memoList;
   private MemoModel _memoModel;
   private MemoModel _committedMemoModel;
   private boolean _isModified;
   private String _uncommittedUID;
   private static String UID_READ_ONLY_MESSAGE = "UID is a read-only field.";
   private static Factory _memoModelFactory;
   private static Factory _titleModelFactory;
   private static Factory _bodyModelFactory;

   private MemoImpl() {
      this._memoModel = (MemoModel)_memoModelFactory.createInstance(null);
      this._uncommittedUID = null;
      this._committed = false;
      this._isModified = true;
   }

   MemoImpl(MemoListImpl memoList) {
      this();
      this._memoList = memoList;
   }

   MemoImpl(BlackBerryMemo memo, MemoListImpl memoList) {
      this(memoList);
      int[] fields = memo.getFields();

      for (int i = fields.length - 1; i >= 0; i--) {
         int field = fields[i];
         if (memoList.isSupportedField(field)) {
            int type = memoList.getFieldDataType(field);
            int numValues = memo.countValues(field);

            for (int j = 0; j < numValues; j++) {
               int attributes = memo.getAttributes(field, j);
               switch (type) {
                  case 4:
                     try {
                        this.addString(field, attributes, memo.getString(field, j));
                     } catch (FieldFullException var12) {
                     }
                     break;
                  default:
                     throw new Object();
               }
            }
         }
      }

      String[] categories = memo.getCategories();

      for (int i = categories.length - 1; i >= 0; i--) {
         try {
            this.addToCategory(categories[i]);
         } catch (PIMException var11) {
         }
      }
   }

   MemoImpl(MemoModel input, MemoListImpl memoList) {
      this._memoList = memoList;
      this._uncommittedUID = null;
      this._memoModel = input;
      this._committed = true;
      this._isModified = false;
      this.commitAndUpdateMemoModel();
   }

   public final MemoModel getMemoModel() {
      return this._committedMemoModel == null ? this._memoModel : this._committedMemoModel;
   }

   final void removeFromList() {
      this._memoList = null;
   }

   @Override
   public final boolean equals(Object o) {
      if (!(o instanceof MemoImpl)) {
         if (o instanceof BlackBerryMemo) {
            BlackBerryMemo memo = (BlackBerryMemo)o;
            if (memo.countValues(102) > 0) {
               String uid = memo.getString(102, 0);
               return String.valueOf(this._memoModel.getUID()).equals(uid);
            }
         }

         return false;
      } else {
         MemoImpl memo = (MemoImpl)o;
         return this._memoModel.getUID() == memo._memoModel.getUID();
      }
   }

   private final void commitAndUpdateMemoModel() {
      MemoModel committedMemoModel = (MemoModel)((EditableProvider)this._memoModel).makeReadOnly();
      if (!this._committed || this._isModified) {
         this._memoList.commitMemo(committedMemoModel);
      }

      this._committedMemoModel = committedMemoModel;
      this._memoModel = (MemoModel)((EditableProvider)committedMemoModel).makeReadWrite();
      super._categoriesModel = this._memoModel.getCategoriesModel();
      this._committed = true;
      this._isModified = false;
   }

   @Override
   public final PIMList getPIMList() {
      return this._memoList;
   }

   @Override
   public final void commit() throws PIMException {
      if (this._memoList == null) {
         throw new PIMException();
      }

      if (this._memoList._closed) {
         throw new PIMException("Memo list is closed.", 2);
      }

      if (this._memoList._mode == 1) {
         throw new Object();
      }

      if (this._isModified) {
         if (this._memoModel.getTitleModel() == null) {
            throw new PIMException("Memo must have a title.");
         }

         this.commitAndUpdateMemoModel();
      }
   }

   @Override
   public final boolean isModified() {
      return this._isModified;
   }

   @Override
   public final int[] getFields() {
      int[] fields = new int[3];
      int index = -1;
      if (this.countValues(100) > 0) {
         fields[++index] = 100;
      }

      if (this.countValues(101) > 0) {
         fields[++index] = 101;
      }

      if (this.countValues(102) > 0) {
         fields[++index] = 102;
      }

      Array.resize(fields, index + 1);
      return fields;
   }

   @Override
   public final void addDate(int field, int attributes, long value) {
      throw new Object();
   }

   @Override
   public final long getDate(int field, int index) {
      throw new Object();
   }

   @Override
   public final void setDate(int field, int index, int attributes, long value) {
      throw new Object();
   }

   @Override
   public final void addInt(int field, int attributes, int value) {
      throw new Object();
   }

   @Override
   public final int getInt(int field, int index) {
      throw new Object();
   }

   @Override
   public final void setInt(int field, int index, int attributes, int value) {
      throw new Object();
   }

   @Override
   public final void addString(int field, int attributes, String value) {
      this.checkFieldNotFull(field);
      if (value == null) {
         throw new Object();
      }

      switch (field) {
         case 99:
            throw new Object();
         case 100:
         default:
            BodyModel notesModel = (BodyModel)_bodyModelFactory.createInstance(value);
            this._memoModel.add(notesModel);
            break;
         case 101:
            TitleModel titleModel = (TitleModel)_titleModelFactory.createInstance(value);
            this._memoModel.add(titleModel);
            break;
         case 102:
            if (this._committed) {
               throw new Object(UID_READ_ONLY_MESSAGE);
            }

            this._uncommittedUID = value;
      }

      this._isModified = true;
   }

   @Override
   public final String getString(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 99:
            throw new Object();
         case 100:
         default:
            return this._memoModel.getNotesModel().getText();
         case 101:
            return this._memoModel.getTitleModel().getTitle();
         case 102:
            return this._committed ? String.valueOf(this._memoModel.getUID()) : this._uncommittedUID;
      }
   }

   @Override
   public final void setString(int field, int index, int attributes, String value) {
      this.checkIndex(field, index);
      if (value == null) {
         throw new Object();
      }

      switch (field) {
         case 99:
            throw new Object();
         case 100:
         default:
            this._memoModel.getNotesModel().setText(value);
            break;
         case 101:
            this._memoModel.getTitleModel().setTitle(value);
            break;
         case 102:
            if (this._committed) {
               throw new Object(UID_READ_ONLY_MESSAGE);
            }

            this._uncommittedUID = value;
      }

      this._isModified = true;
   }

   @Override
   public final int countValues(int field) {
      switch (field) {
         case 99:
            throw new Object();
         case 100:
         default:
            if (this._memoModel.getNotesModel() == null) {
               return 0;
            }

            return 1;
         case 101:
            if (this._memoModel.getTitleModel() == null) {
               return 0;
            }

            return 1;
         case 102:
            if (this._committed) {
               return 1;
            } else {
               return this._uncommittedUID == null ? 0 : 1;
            }
      }
   }

   @Override
   public final void removeValue(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 99:
            throw new Object();
         case 100:
         default:
            BodyModel notesModel = this._memoModel.getNotesModel();
            this._memoModel.remove(notesModel);
            break;
         case 101:
            TitleModel titleModel = this._memoModel.getTitleModel();
            this._memoModel.remove(titleModel);
            break;
         case 102:
            if (this._committed) {
               throw new Object(UID_READ_ONLY_MESSAGE);
            }

            this._uncommittedUID = null;
      }

      this._isModified = true;
   }

   @Override
   public final void addToCategory(String category) {
      if (this.addCategoryToModel(this._memoModel, category)) {
         this._isModified = true;
      }
   }

   @Override
   public final void removeFromCategory(String category) {
      if (this.removeCategoryFromModel(this._memoModel, category)) {
         this._isModified = true;
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _memoModelFactory = (Factory)ar.waitFor(-3309887157296949036L);
      _titleModelFactory = (Factory)ar.waitFor(-4904857078378172834L);
      _bodyModelFactory = (Factory)ar.waitFor(2096811533660483L);
   }
}
