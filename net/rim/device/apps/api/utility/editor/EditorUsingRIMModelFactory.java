package net.rim.device.apps.api.utility.editor;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;

public class EditorUsingRIMModelFactory extends ModelScreen {
   private ReadableList _modelWithSubmembers;
   private Vector _insertedModels = (Vector)(new Object());
   private Vector _deletedModels = (Vector)(new Object());
   private boolean _modelInsertedViaMenu;
   private boolean _isActive;
   private RIMModelFactory[] _mf;
   private OrderedFieldScreen _orderedFields;
   private int _separatorInterval;
   private boolean _redisplaying;

   protected EditorUsingRIMModelFactory(long style, Object context, String title, long factoryID, int separatorInterval) {
      super(style, title, context);
      this._separatorInterval = separatorInterval;
      if (factoryID != 0) {
         this._mf = RIMModelFactoryRepository.getModelFactories(factoryID);
      }
   }

   protected EditorUsingRIMModelFactory(Object context, String title, long factoryID, int separatorInterval) {
      this(0, context, title, factoryID, separatorInterval);
   }

   public EditorUsingRIMModelFactory(Object context, String title, int separatorInterval) {
      this(context, title, 0, separatorInterval);
   }

   protected Manager createManagerForField(Field field, int order) {
      return null;
   }

   protected int getOrderForManagerForField(Field field, int order) {
      return -1;
   }

   @Override
   public void setModel(Object model) {
      if (model instanceof Object && model instanceof Object) {
         if (this._redisplaying) {
            this.deleteAll();
            this._insertedModels.removeAllElements();
            this._deletedModels.removeAllElements();
         }

         super.setModel(model);
         this._modelWithSubmembers = (ReadableList)model;
         this._orderedFields = new OrderedFieldScreen(this, this._separatorInterval);
         this.populateModels();
         this.produceFields();
         this.setFocus();
         Field field = this.getLeafFieldWithFocus();
         if (field instanceof Object) {
            EditField editField = (EditField)field;
            editField.setCursorPosition(editField.getTextLength());
         }

         this._isActive = true;
         this._redisplaying = true;
      } else {
         throw new Object();
      }
   }

   @Override
   public Object getModel(boolean removeEmpty) {
      this.grabDataFromEdit(removeEmpty);
      return super.getModel();
   }

   @Override
   public Object getModel() {
      return this.getModel(true);
   }

   @Override
   public boolean isDirty() {
      return this._modelInsertedViaMenu || super.isDirty() || this._deletedModels.size() > 0;
   }

   protected void addInsertedModels() {
      WritableSet model = (WritableSet)this._modelWithSubmembers;
      int size = this._insertedModels.size();

      for (int i = 0; i < size; i++) {
         Object obj = this._insertedModels.elementAt(i);
         if (!model.contains(obj)) {
            model.add(obj);
         }
      }

      this._insertedModels.removeAllElements();
   }

   protected void removeDeletedModels() {
      WritableSet model = (WritableSet)this._modelWithSubmembers;
      int size = this._deletedModels.size();

      for (int i = 0; i < size; i++) {
         Object obj = this._deletedModels.elementAt(i);
         if (model.contains(obj)) {
            model.remove(obj);
         }
      }

      this._deletedModels.removeAllElements();
   }

   private Field insertFieldForModel(Object m) {
      Field field = null;
      if (m instanceof FieldProvider) {
         FieldProvider fieldProvider = (FieldProvider)m;
         field = fieldProvider.getField(super._context);
         if (field != null) {
            if (field.getCookie() != null && field.getCookie() != m) {
               System.out.println("Unexpected cookie found on return from FieldProvider.getField");
            }

            field.setCookie(m);
            int order = fieldProvider.getOrder(super._context);
            this._orderedFields.insertField(field, order);
         }
      }

      return field;
   }

   public void insertModel(Object m) {
      if (m != null) {
         this._insertedModels.addElement(m);
         Field field = this.insertFieldForModel(m);
         if (field != null && field.isFocusable() && this._isActive) {
            try {
               field.setFocus();
            } finally {
               return;
            }
         }
      }
   }

   public void deleteModel(Object m) {
      boolean insertedModel = false;

      for (int i = this._insertedModels.size() - 1; i >= 0; i--) {
         if (this._insertedModels.elementAt(i) == m) {
            this._insertedModels.removeElementAt(i);
            insertedModel = true;
            break;
         }
      }

      if (!insertedModel) {
         this._deletedModels.addElement(m);
      }

      Field f = this.findField(m);
      if (f != null) {
         this._orderedFields.removeField(f);
      }
   }

   public void replaceModel(Object mOld, Object mNew, boolean allModels) {
      for (int i = this._insertedModels.size() - 1; i >= 0; i--) {
         if (this._insertedModels.elementAt(i) == mOld) {
            this._insertedModels.setElementAt(mNew, i);
            return;
         }
      }

      if (allModels) {
         WritableSet model = (WritableSet)this._modelWithSubmembers;
         if (model != null) {
            model.remove(mOld);
            model.add(mNew);
         }
      }
   }

   public void replaceField(Field oldField, Field newField, int order) {
      this._orderedFields.removeField(oldField);
      this._orderedFields.insertField(newField, order);
   }

   public void replaceFieldsWithSameOrder(Field oldField, Field newField) {
      this._orderedFields.replaceField(oldField, newField);
   }

   protected void populateModels() {
      if (this._mf != null) {
         for (int i = 0; i < this._mf.length; i++) {
            Object[] m = SubmemberUtilities.getSubmembers(this._modelWithSubmembers, this._mf[i]);
            int minCount = this._mf[i].getMinimumCount(super._context);
            if (minCount > 0) {
               int modelsToAdd = minCount - m.length;

               for (int j = 1; j <= modelsToAdd; j++) {
                  Object o = this._mf[i].createInstance(super._context);
                  this.insertModel(o);
               }
            }
         }
      }
   }

   protected void produceFields() {
      int numSubmembers = this._modelWithSubmembers.size();

      for (int j = 0; j < numSubmembers; j++) {
         this.insertFieldForModel(this._modelWithSubmembers.getAt(j));
      }
   }

   public RIMModelFactory[] getModelFactories() {
      return this._mf;
   }

   @Override
   protected boolean handleKeyChar(char key, int status, int time) {
      if (super.handleKeyChar(key, status, time)) {
         return true;
      }

      if (key == 127 || Keypad.getAltedChar(key) == 127) {
         Field fieldWithFocus = this.getModelFieldWithFocus();
         Field leafFieldWithFocus = this.getLeafFieldWithFocus();
         if (fieldWithFocus != null && leafFieldWithFocus != null && !leafFieldWithFocus.isEditable()) {
            Object cookie = fieldWithFocus.getCookie();
            if (cookie instanceof RIMModel) {
               RIMModel model = (RIMModel)cookie;
               if (this.canDeleteModel(model)) {
                  this.deleteModel(model);
                  return true;
               }
            }
         }
      }

      return false;
   }

   protected int getModelCount(Recognizer recognizer) {
      int modelCount = SubmemberUtilities.getSubmembers(this._modelWithSubmembers, recognizer).length;

      for (int j = 0; j < this._insertedModels.size(); j++) {
         if (recognizer.recognize(this._insertedModels.elementAt(j))) {
            modelCount++;
         }
      }

      for (int var4 = 0; var4 < this._deletedModels.size(); var4++) {
         if (recognizer.recognize(this._deletedModels.elementAt(var4))) {
            modelCount--;
         }
      }

      return modelCount;
   }

   protected Object[] getAttachedModels(Recognizer recognizer) {
      Object[] attachedModels = new Object[0];
      if (recognizer == null) {
         return attachedModels;
      }

      int j = 0;
      Object[] models = SubmemberUtilities.getSubmembers(this._modelWithSubmembers, recognizer);
      if (models != null) {
         for (int var5 = 0; var5 < models.length; var5++) {
            if (!this._deletedModels.contains(models[var5])) {
               Arrays.add(attachedModels, models[var5]);
            }
         }
      }

      for (int var6 = 0; var6 < this._insertedModels.size(); var6++) {
         if (recognizer.recognize(this._insertedModels.elementAt(var6))) {
            Arrays.add(attachedModels, this._insertedModels.elementAt(var6));
         }
      }

      return attachedModels;
   }

   protected RIMModelFactory getFactoryForModel(RIMModel model) {
      for (int i = this._mf.length - 1; i >= 0; i--) {
         if (this._mf[i].recognize(model)) {
            return this._mf[i];
         }
      }

      return null;
   }

   protected boolean canDeleteModel(RIMModel model) {
      RIMModelFactory factory = (RIMModelFactory)this.getFactoryForModel(model);
      if (factory != null) {
         int modelCount = this.getModelCount(factory);
         if (modelCount > 0 && modelCount > factory.getMinimumCount(super._context)) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (instance != 65536) {
         if (ContextObject.getFlag(super._context, 0)) {
            RIMModel producedModel = null;
            boolean addDeleteVerb = false;
            Field fieldWithFocus = this.getModelFieldWithFocus();
            if (fieldWithFocus != null) {
               Object cookie = fieldWithFocus.getCookie();
               if (cookie instanceof RIMModel) {
                  producedModel = (RIMModel)cookie;
               }
            }

            if (this._mf != null) {
               for (int i = 0; i < this._mf.length; i++) {
                  RIMModelFactory mf = this._mf[i];
                  boolean addVerbs = false;
                  int modelCount = this.getModelCount(mf);
                  if (modelCount < mf.getMaximumCount(super._context)) {
                     addVerbs = true;
                  }

                  if (producedModel != null && mf.recognize(producedModel) && modelCount > 0 && modelCount > mf.getMinimumCount(super._context)) {
                     addDeleteVerb = true;
                  }

                  if (addVerbs) {
                     Verb[] newVerbs = mf.getVerbs(super._context);
                     if (newVerbs != null) {
                        for (int j = newVerbs.length - 1; j >= 0; j--) {
                           Verb v = new EditorUsingRIMModelFactory$AddWrapperVerb(this, newVerbs[j]);
                           menu.add(v);
                        }
                     }
                  }
               }
            }

            if (addDeleteVerb) {
               Verb v = new EditorUsingRIMModelFactory$DeleteModelVerb(this, producedModel);
               menu.add(v);
            }
         }
      }
   }

   protected void grabDataFromEdit(boolean removeEmpty) {
      this.grabDataFromEdit(this.getMainManager(), removeEmpty);
      this.removeDeletedModels();
      this.addInsertedModels();
      this.ensureModelsInScreenOrder();
   }

   private void grabDataFromEdit(Manager manager, boolean removeEmpty) {
      int numFields = manager.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field field = manager.getField(i);
         Object cookie = field.getCookie();
         if (!(cookie instanceof FieldProvider)) {
            if (field instanceof Object) {
               this.grabDataFromEdit((Manager)field, removeEmpty);
            }
         } else {
            FieldProvider fp = (FieldProvider)cookie;
            if (!fp.grabDataFromField(field, super._context) && removeEmpty && !this._insertedModels.removeElement(cookie)) {
               this._deletedModels.addElement(cookie);
            }
         }
      }
   }

   public Enumeration getFieldsFromEdit() {
      return new EditorUsingRIMModelFactory$EditorFieldEnumerator(this);
   }

   private int getModelsInScreenOrder(WritableSet submodelSet, Manager manager, Object[] models, int startIndex) {
      int numFields = manager.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field field = manager.getField(i);
         Object cookie = field.getCookie();
         if (cookie == null) {
            if (field instanceof Object) {
               startIndex = this.getModelsInScreenOrder(submodelSet, (Manager)field, models, startIndex);
            }
         } else if (cookie instanceof FieldProvider && cookie instanceof Object && submodelSet.contains(cookie)) {
            submodelSet.remove(cookie);
            models[startIndex++] = cookie;
         }
      }

      return startIndex;
   }

   protected void ensureModelsInScreenOrder() {
      WritableSet submodelSet = (WritableSet)this._modelWithSubmembers;
      Object[] orderedModels = new Object[this._modelWithSubmembers.size()];
      int index = this.getModelsInScreenOrder(submodelSet, this.getMainManager(), orderedModels, 0);

      for (int i = this._modelWithSubmembers.size() - 1; i >= 0; i--) {
         Object model = this._modelWithSubmembers.getAt(i);
         if (model instanceof Object) {
            submodelSet.remove(model);
            orderedModels[index++] = model;
         }
      }

      for (int i = 0; i < index; i++) {
         submodelSet.add(orderedModels[i]);
      }
   }

   protected boolean validateDataFromEdit() {
      return this.validateFields() < 0;
   }

   private int validateFields(Manager manager, int currentFieldProviderIndex) {
      int numFields = manager.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field field = manager.getField(i);
         Object cookie = field.getCookie();
         if (!(cookie instanceof FieldProvider)) {
            if (field instanceof Object) {
               int invalidFieldIndex = this.validateFields((Manager)field, currentFieldProviderIndex);
               if (invalidFieldIndex >= 0) {
                  return invalidFieldIndex;
               }

               currentFieldProviderIndex = -invalidFieldIndex;
            }
         } else {
            FieldProvider fp = (FieldProvider)cookie;
            if (!fp.validate(field, super._context)) {
               return currentFieldProviderIndex;
            }

            currentFieldProviderIndex++;
         }
      }

      return -currentFieldProviderIndex;
   }

   protected int validateFields() {
      int invalidFieldIndex = this.validateFields(this.getMainManager(), 0);
      if (invalidFieldIndex < 0) {
         invalidFieldIndex = -1;
      }

      return invalidFieldIndex;
   }

   public boolean setFocus(Manager manager, Recognizer recognizer) {
      int numFields = manager.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field field = manager.getField(i);
         if (recognizer.recognize(field.getCookie()) && field.isFocusable()) {
            field.setFocus();
            return true;
         }

         if (field instanceof Object && this.setFocus((Manager)field, recognizer)) {
            return true;
         }
      }

      return false;
   }

   public void setFocus(Recognizer recognizer) {
      this.setFocus(this.getMainManager(), recognizer);
   }

   public Field findField(Object cookie) {
      return this.findField(this.getMainManager(), cookie);
   }

   private Field findField(Manager manager, Object cookie) {
      int size = manager.getFieldCount();

      for (int i = 0; i < size; i++) {
         Field field = manager.getField(i);
         if (cookie == field.getCookie()) {
            return field;
         }

         if (field instanceof Object) {
            field = this.findField((Manager)field, cookie);
            if (field != null) {
               return field;
            }
         }
      }

      return null;
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (!attached) {
         this._modelWithSubmembers = null;
         this._insertedModels.removeAllElements();
         this._deletedModels.removeAllElements();
      }

      super.onUiEngineAttached(attached);
   }

   public void finalizeVerbInvocation(Verb verb, Object context) {
      this.invokedVerb(verb, context);
   }
}
