package net.rim.device.apps.internal.blackberryemail.email.filters;

import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;
import net.rim.vm.Array;

class EmailFilterModelImpl
   implements SyncObject,
   PaintProvider,
   VerbProvider,
   ConversionProvider,
   KeyProvider,
   EditableProvider,
   EncryptableProvider,
   PersistableRIMModel,
   ReadableList,
   WritableSet {
   TitleModel _filterName;
   boolean _enabled = true;
   int _order;
   Vector _fields;
   int _uid;
   Object _additionalData;
   String _userId;
   public static final int SEND_DIRECTLY_TO_ME = 1;
   public static final int CC_TO_ME = 2;
   public static final int BCC_TO_ME = 4;
   public static final int IMPORTANCE_LOW = 1;
   public static final int IMPORTANCE_NOMAL = 2;
   public static final int IMPORTANCE_HIGH = 4;
   public static final int SENSITIVITY_NORMAL = 1;
   public static final int SENSITIVITY_PERSONAL = 2;
   public static final int SENSITIVITY_PRIVATE = 4;
   public static final int SENSITIVITY_CONFIDENTIAL = 8;
   public static final int DO_NOT_FORWARD = 1;
   public static final int FORWARD_LEVEL1_NOTIFICATION = 2;
   public static final int FORWARD_HEADER_ONLY = 4;
   private static Recognizer _titleModelRecognizer = RecognizerRepository.getRecognizers(-4904857078378172834L);
   private static ContextObjectWR _filterSyncContextWR = (ContextObjectWR)(new Object(33, 19));

   @Override
   public int paint(Graphics g, int x, int y, int width, int height, Object context) {
      return this._filterName != null ? ((PaintProvider)this._filterName).paint(g, x, y, width, height, context) : 0;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Verb[] newVerbs = new Object[0];
      Array.resize(verbs, 0);

      for (int i = 0; i < this.size(); i++) {
         Object itemField = this.getAt(i);
         if (itemField instanceof Object) {
            VerbProvider verbProvider = (VerbProvider)itemField;
            verbProvider.getVerbs(context, newVerbs);
            int newCount = newVerbs.length;
            if (newCount > 0) {
               int base = verbs.length;
               Array.resize(verbs, base + newCount);
               System.arraycopy(newVerbs, 0, verbs, base, newCount);
            }
         }
      }

      return null;
   }

   public void setOrder(int order) {
      this._order = order + 1;
   }

   @Override
   public int getUID() {
      return this._uid;
   }

   public void toggleStatus() {
      this._enabled = !this._enabled;
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 33) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addInt(2, this._order, 4);
         syncBuffer.addInt(3, this._enabled ? 1 : 0, 4);
         return syncBuffer.addSubmembers(this, _filterSyncContextWR.getContextObject());
      } else {
         return false;
      }
   }

   public boolean getStatus() {
      return this._enabled;
   }

   public String getName() {
      return this._filterName == null ? "" : this._filterName.getTitle();
   }

   public int getOrder() {
      return this._order;
   }

   @Override
   public boolean contains(Object element) {
      return this._fields.contains(element);
   }

   @Override
   public void remove(Object element) {
      if (_titleModelRecognizer.recognize(element)) {
         this._filterName = null;
      }

      this._fields.removeElement(element);
   }

   @Override
   public void removeAll() {
      this._filterName = null;
      this._fields.removeAllElements();
   }

   @Override
   public void add(Object element) {
      if (_titleModelRecognizer.recognize(element)) {
         this._filterName = (TitleModel)element;
         this._fields.addElement(element);
      } else {
         if (!this.containsEmailFilterBody()) {
            this._fields.addElement(element);
         }
      }
   }

   @Override
   public int getIndex(Object element) {
      return this._fields.indexOf(element);
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public int size() {
      return this._fields.size();
   }

   @Override
   public Object getAt(int index) {
      return this._fields.elementAt(index);
   }

   @Override
   public int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return !(this._filterName instanceof Object) ? 0 : ((KeyProvider)this._filterName).getKeys(context, keyArray, index, keyRequested);
   }

   @Override
   public int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public Object makeReadOnly() {
      ObjectGroup.createGroupIgnoreTooBig(this);
      return this;
   }

   @Override
   public Object makeReadWrite() {
      return ObjectGroup.isInGroup(this) ? ObjectGroup.expandGroup(this) : this;
   }

   @Override
   public boolean isReadOnly() {
      return ObjectGroup.isInGroup(this);
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      if (this._filterName instanceof Object && !((EncryptableProvider)this._filterName).checkCrypt(compress, encrypt)) {
         return false;
      }

      int numSubmembers = this._fields.size();

      for (int i = 0; i < numSubmembers; i++) {
         Object object = this._fields.elementAt(i);
         if (object instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)object;
            if (!encryptable.checkCrypt(compress, encrypt)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      EmailFilterModelImpl newModel = (EmailFilterModelImpl)this.makeReadWrite();
      if (newModel._filterName instanceof Object) {
         TitleModel newTitleModel = (TitleModel)((EncryptableProvider)newModel._filterName).reCrypt(compress, encrypt);
         if (newTitleModel != null) {
            newModel._filterName = newTitleModel;
         }
      }

      int numSubmembers = newModel._fields.size();

      for (int i = 0; i < numSubmembers; i++) {
         Object object = newModel._fields.elementAt(i);
         if (object instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)object;
            Object newObject = encryptable.reCrypt(compress, encrypt);
            if (newObject != null) {
               newModel._fields.setElementAt(newObject, i);
            }
         }
      }

      return newModel.makeReadOnly();
   }

   private boolean containsEmailFilterBody() {
      for (int i = 0; i < this._fields.size(); i++) {
         Object o = this._fields.elementAt(i);
         if (o instanceof EmailFilterBodyModelImpl) {
            return true;
         }
      }

      return false;
   }

   EmailFilterModelImpl(EmailFilterModelImpl m) {
      if (ObjectGroup.isInGroup(m)) {
         m = (EmailFilterModelImpl)ObjectGroup.expandGroup(m);
      }

      this._additionalData = m._additionalData;
      this._userId = m._userId;
      this._enabled = m._enabled;
      this._fields = m._fields;
      this._filterName = m._filterName;
      this._order = m._order;
      this._uid = m._uid;
   }

   EmailFilterModelImpl() {
      this._fields = (Vector)(new Object());
   }

   EmailFilterModelImpl(Object initialData, int type) {
      this();
      if (initialData instanceof Object) {
         this._userId = (String)initialData;
      } else if (initialData instanceof EmailMessageModel) {
         EmailMessageModel m = (EmailMessageModel)initialData;
         ServiceRecord sr = m.getServiceRecordForMessage();
         if (sr == null) {
            throw new Object("No service record for message.");
         }

         this._userId = String.valueOf(sr.getUserId());
         this.add(new EmailFilterBodyModelImpl(m, type));
      }

      this._uid = EmailFilterCollectionImpl.getInstance(this._userId).generateUniqueID();
   }
}
