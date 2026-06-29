package net.rim.device.apps.internal.memo;

import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextProxy;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.memo.MemoModel;
import net.rim.device.apps.api.messaging.messagelist.ForwardAsVerb;
import net.rim.device.apps.api.messaging.messagelist.MessageAttachment;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;
import net.rim.device.apps.internal.memo.resources.MemoResources;
import net.rim.vm.Array;

final class MemoModelImpl
   implements MemoModel,
   SyncObject,
   PaintProvider,
   VerbProvider,
   ConversionProvider,
   KeyProvider,
   EditableProvider,
   EncryptableProvider,
   MatchProvider,
   MessagePartsProvider,
   AccessibleContextProxy {
   private PersistableRIMModel _subjectModel;
   private Object[] _fields = new Object[0];
   int _uid;
   private static final int MAX_MEMO_NOTES_LENGTH;
   private static ContextObjectWR _memoSyncContextWR = (ContextObjectWR)(new Object(8, 35, 19));
   private static final byte[] MEMO_ID = new byte[]{109};
   private static Recognizer _titleModelRecognizer = RecognizerRepository.getRecognizers(-4904857078378172834L);
   private static int[] _hints = new int[0];

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      return !(this._subjectModel instanceof Object) ? 0 : ((PaintProvider)this._subjectModel).paint(g, x, y, width, height, context);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      ForwardAsVerb forwardAsVerb = (ForwardAsVerb)(new Object(this));
      if (forwardAsVerb.canInvoke(null)) {
         Array.resize(verbs, 1);
         verbs[0] = forwardAsVerb;
      } else {
         Array.resize(verbs, 0);
      }

      Verb[] newVerbs = new Object[0];

      for (int i = this.size() - 1; i >= 0; i--) {
         Object field = this.getAt(i);
         if (field instanceof Object) {
            ((VerbProvider)field).getVerbs(context, newVerbs);
            if (newVerbs.length > 0) {
               int base = verbs.length;
               Array.resize(verbs, base + newVerbs.length);
               System.arraycopy(newVerbs, 0, verbs, base, newVerbs.length);
            }
         }
      }

      return null;
   }

   @Override
   public final int match(Object criteria) {
      if (!(criteria instanceof Object)) {
         return Match.match(this, this, (Object[])criteria, _hints);
      } else {
         SearchCriterion crit = (SearchCriterion)criteria;
         if (crit.getType() == 24) {
            return crit.getValue() == this.getUID() ? 1 : 0;
         } else {
            return -1;
         }
      }
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   final RIMModel getSubjectModel() {
      return this._subjectModel;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 35) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addBytes(3, MEMO_ID);
         return syncBuffer.addSubmembers(this, _memoSyncContextWR.getContextObject());
      } else {
         return false;
      }
   }

   @Override
   public final boolean isReadOnly() {
      return ObjectGroup.isInGroup(this);
   }

   @Override
   public final void add(Object element) {
      if (_titleModelRecognizer.recognize(element) && element instanceof Object) {
         this._subjectModel = (PersistableRIMModel)element;
      }

      Arrays.add(this._fields, element);
   }

   @Override
   public final boolean contains(Object element) {
      return Arrays.contains(this._fields, element);
   }

   @Override
   public final void remove(Object element) {
      if (element == this._subjectModel) {
         this._subjectModel = null;
      }

      Arrays.remove(this._fields, element);
   }

   @Override
   public final void removeAll() {
      this._subjectModel = null;
      Array.resize(this._fields, 0);
   }

   @Override
   public final Object makeReadWrite() {
      return this.isReadOnly() ? ObjectGroup.expandGroup(this) : this;
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      int numFields = this.size();
      int numKeys = 0;

      for (int i = 0; i < numFields; i++) {
         Object field = this.getAt(i);
         if (field instanceof Object) {
            numKeys += ((KeyProvider)field).getKeys(context, keyArray, index + numKeys, keyRequested);
         }
      }

      return numKeys;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final CategoriesModel getCategoriesModel() {
      for (int i = this._fields.length - 1; i >= 0; i--) {
         Object o = this._fields[i];
         if (o instanceof Object) {
            return (CategoriesModel)o;
         }
      }

      return null;
   }

   @Override
   public final int size() {
      return this._fields.length;
   }

   @Override
   public final Object getAt(int index) {
      return this._fields[index];
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public final int getIndex(Object element) {
      return Arrays.getIndex(this._fields, element);
   }

   @Override
   public final Object makeReadOnly() {
      if (!this.isReadOnly()) {
         ObjectGroup.createGroupIgnoreTooBig(this);
      }

      return this;
   }

   @Override
   public final TitleModel getTitleModel() {
      for (int i = this._fields.length - 1; i >= 0; i--) {
         Object o = this._fields[i];
         if (o instanceof Object) {
            return (TitleModel)o;
         }
      }

      return null;
   }

   @Override
   public final boolean inbound() {
      return false;
   }

   @Override
   public final boolean allowDescriptiveForwardHeader() {
      return false;
   }

   @Override
   public final String getSender() {
      return null;
   }

   @Override
   public final String[] getRecipients() {
      return null;
   }

   @Override
   public final String getBody() {
      for (int i = this.size() - 1; i > -1; i--) {
         RIMModel model = (RIMModel)this.getAt(i);
         if (model instanceof Object) {
            return ((BodyModel)model).getText();
         }
      }

      return null;
   }

   @Override
   public final String getSubject() {
      return this._subjectModel.toString();
   }

   @Override
   public final String getName() {
      return MemoResources.getString(100);
   }

   @Override
   public final void setRead(Object context) {
   }

   @Override
   public final long getSentDate() {
      return 0;
   }

   @Override
   public final MessageAttachment[] getAttachments() {
      return null;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      for (int i = this.size() - 1; i >= 0; i--) {
         Object field = this.getAt(i);
         if (field instanceof Object && !((EncryptableProvider)field).checkCrypt(compress, encrypt)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      MemoModelImpl newModel = this;
      if (ObjectGroup.isInGroup(newModel)) {
         newModel = (MemoModelImpl)ObjectGroup.expandGroup(newModel);
      }

      for (int i = newModel.size() - 1; i >= 0; i--) {
         Object field = newModel.getAt(i);
         if (field instanceof Object) {
            Object newField = ((EncryptableProvider)field).reCrypt(compress, encrypt);
            if (newField != null) {
               newModel._fields[i] = newField;
            }
         }
      }

      ObjectGroup.createGroupIgnoreTooBig(newModel);
      return newModel;
   }

   @Override
   public final BodyModel getNotesModel() {
      for (int i = this._fields.length - 1; i >= 0; i--) {
         Object o = this._fields[i];
         if (o instanceof Object) {
            return (BodyModel)o;
         }
      }

      return null;
   }

   @Override
   public final AccessibleContext getAccessibleContext() {
      return (AccessibleContext)(new Object(this._fields[0].toString(), 0, 4));
   }

   MemoModelImpl() {
      this(MemoCollectionImpl.getInstance().generateUniqueID());
   }

   MemoModelImpl(int uid) {
      this._uid = uid;
   }
}
