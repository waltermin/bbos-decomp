package net.rim.device.apps.api.utility.viewer;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.vm.Array;

public class ViewReadableListRIMModel extends ModelScreen {
   protected Field _fieldToGetFocus;
   protected Recognizer _initiallyFocussedRecognizer;
   protected int _separatorInterval;
   private int[] _forcedSeparator;
   private Field[] _uiFields;

   public ViewReadableListRIMModel(long style, String title, Object context, int separatorInterval, Recognizer initiallyFocussedRecognizer) {
      super(style, title, ContextObject.clone(context));
      this._initiallyFocussedRecognizer = initiallyFocussedRecognizer;
      this._separatorInterval = separatorInterval;
      ContextObject.clearFlag(super._context, 0);
   }

   public ViewReadableListRIMModel(String title, Object context, int separatorInterval, Recognizer initiallyFocussedRecognizer) {
      this(0, title, context, separatorInterval, initiallyFocussedRecognizer);
   }

   public ViewReadableListRIMModel(String title, Object context, int separatorInterval) {
      this(title, context, separatorInterval, null);
   }

   protected void organizeFields(Field[] fields, int[] orders) {
   }

   private Field lookForFocusableField(Recognizer recognizer, Field field) {
      if (recognizer.recognize(field.getCookie())) {
         return field;
      }

      if (field instanceof Manager) {
         Manager manager = (Manager)field;
         int count = manager.getFieldCount();

         for (int i = 0; i < count; i++) {
            Field focusable = this.lookForFocusableField(recognizer, manager.getField(i));
            if (focusable != null) {
               return focusable;
            }
         }
      }

      return null;
   }

   @Override
   public void setModel(Object model) {
      if (!(model instanceof ReadableList)) {
         throw new IllegalArgumentException();
      }

      super.setModel(model);
      ReadableList readableListModel = (ReadableList)model;
      if (this._uiFields == null
         || !this.supportsIncrementalUpdate(super._context)
         || !this.refreshFieldCookies(readableListModel)
         || !this.performIncrementalUpdate(this._uiFields)) {
         int size = readableListModel.size();
         int[] orders = new int[size];
         Field[] fields = new Field[size];
         int count = 0;

         for (int i = 0; i < size; i++) {
            Object element = readableListModel.getAt(i);
            if (element instanceof FieldProvider) {
               FieldProvider fieldProvider = (FieldProvider)element;
               Field field = fieldProvider.getField(super._context);
               if (field != null) {
                  if (field.getCookie() == null) {
                     field.setCookie(element);
                  }

                  fields[count] = field;
                  orders[count] = fieldProvider.getOrder(super._context);
                  count++;
               }
            }
         }

         Array.resize(fields, count);
         Array.resize(orders, count);
         Arrays.sort(orders, 0, count, fields);
         this._uiFields = new Field[fields.length];
         System.arraycopy(fields, 0, this._uiFields, 0, fields.length);

         for (int index = this.getFieldCount() - 1; index >= 0; index--) {
            Field field = this.getField(index);
            if (field instanceof Manager) {
               ((Manager)field).deleteAll();
            }
         }

         this.deleteAll();
         this.organizeFields(fields, orders);
         count = fields.length;
         int lastGroup = -1;
         boolean drawSeparator = false;
         boolean separatorRequired = false;
         boolean initialFocusFound = false;
         this._fieldToGetFocus = null;

         for (int j = 0; j < count; j++) {
            int value = orders[j];
            int thisGroup = value / (this._separatorInterval != 0 ? this._separatorInterval : 1);
            if (value != 0 && this._separatorInterval > 0) {
               if ((lastGroup < 0 || thisGroup <= lastGroup) && !separatorRequired) {
                  if (this.isSeparatorRequired(value)) {
                     separatorRequired = true;
                     drawSeparator = true;
                  }
               } else {
                  drawSeparator = false;
                  separatorRequired = false;
                  super.add(new SeparatorField());
               }

               lastGroup = thisGroup;
            }

            Field f = fields[j];
            if (this._fieldToGetFocus == null && this._initiallyFocussedRecognizer != null) {
               this._fieldToGetFocus = this.lookForFocusableField(this._initiallyFocussedRecognizer, f);
            }

            if (value == 0) {
               super.setTitle(f);
            } else {
               this.add(f);
               if (!initialFocusFound && f.isFocusable()) {
                  f.setFocus();
                  initialFocusFound = true;
               }
            }
         }

         if (drawSeparator) {
            super.add(new SeparatorField());
         }

         if (this._fieldToGetFocus != null) {
            this._fieldToGetFocus.setFocus();
         }
      }
   }

   protected boolean refreshFieldCookies(ReadableList fieldProvidersList) {
      int size = fieldProvidersList.size();
      int[] orders = new int[size];
      Object[] fieldProviders = new Object[size];
      int count = 0;

      for (int i = 0; i < size; i++) {
         Object element = fieldProvidersList.getAt(i);
         if (element instanceof FieldProvider) {
            FieldProvider fieldProvider = (FieldProvider)element;
            fieldProviders[count] = fieldProvider;
            orders[count] = fieldProvider.getOrder(super._context);
            count++;
         }
      }

      Array.resize(fieldProviders, count);
      Array.resize(orders, count);
      Arrays.sort(orders, 0, count, fieldProviders);
      if (this._uiFields != null && this._uiFields.length == count) {
         for (int i = 0; i < count; i++) {
            Field oldField = this._uiFields[i];
            FieldProvider newFieldProvider = (FieldProvider)fieldProviders[i];
            if (oldField == null || newFieldProvider == null) {
               return false;
            }

            if (!this.validateFieldProvider(oldField, newFieldProvider)) {
               return false;
            }

            oldField.setCookie(newFieldProvider);
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean validateFieldProvider(Field field, FieldProvider fieldProvider) {
      Object cookie = field.getCookie();
      return cookie != null ? cookie.getClass().equals(fieldProvider.getClass()) : false;
   }

   protected boolean supportsIncrementalUpdate(Object context) {
      return false;
   }

   protected boolean performIncrementalUpdate(Field[] fields) {
      return false;
   }

   private Manager getManager(int group) {
      Manager manager = new VerticalFieldManager();
      switch (group) {
         case 2:
            manager.setTag(Tag.create("message-section-header"));
            return manager;
         case 5:
            manager.setTag(Tag.create("message-section-body"));
            return manager;
         case 6:
            manager.setTag(Tag.create("message-section-attachments"));
         default:
            return manager;
      }
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached && this._fieldToGetFocus != null) {
         Field f = this.getLeafFieldWithFocus();
         if (f != null) {
            while (f.getManager() != null && (f.getManager().getStyle() & 281474976710656L) == 0) {
               f = f.getManager();
            }

            f.getManager().setVerticalScroll(this._fieldToGetFocus.getTop());
         }
      }
   }

   protected void addForcedSeparator(int value) {
      if (this._forcedSeparator == null) {
         this._forcedSeparator = new int[0];
      }

      int length = this._forcedSeparator.length;
      Array.resize(this._forcedSeparator, length + 1);
      this._forcedSeparator[length] = value;
   }

   private boolean isSeparatorRequired(int order) {
      if (this._forcedSeparator != null) {
         for (int i = this._forcedSeparator.length - 1; i >= 0; i--) {
            if (order == this._forcedSeparator[i]) {
               return true;
            }
         }
      }

      return false;
   }
}
