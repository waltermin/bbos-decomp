package net.rim.wica.runtime.metadata.internal.component.ui;

import java.util.Vector;
import net.rim.device.api.util.Arrays;
import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.runtime.access.data.collections.StdCmpCollection;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.Data;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.UIControl;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.util.ArrayInValueResolver;
import net.rim.wica.runtime.metadata.internal.util.SingleValueHelper;
import net.rim.wica.runtime.util.LongVector;

public class UIControlImpl extends UIComponentImpl implements UIControl {
   private int[] _events;
   protected Object _inValue;
   protected Object _value;
   protected int _valueType;
   protected boolean _valueChanged;
   protected boolean _isMappingApplied;
   private UIControl _controlContainer;
   protected int[] _mapping;
   protected long _mappingType;
   protected Object _mappedValue;
   protected String _mappingKey;
   protected ControlMappingResolver _mappingResolver;
   private static final int IN_ARRAY_FIELDS_OFFSET_ENUM_CASE;
   private static final int IN_ARRAY_FIELDS_OFFSET;
   protected static final int BIT_IS_MANDATORY;
   protected static final int BIT_IS_READONLY;

   protected void onValueChanged(boolean fromUI) {
      if (!fromUI) {
         ((ScreenModelImpl)this.getScreen()).invalidateUI(false);
      }

      this._valueChanged = true;
      ((ScreenModelImpl)this.getScreen()).onModify();
   }

   protected Object resolveInValue(Object inValue, int valueType) {
      Object value = null;
      if ((valueType & 32768) == 0) {
         return SingleValueHelper.resolveInValue(this, inValue, valueType);
      }

      if (!(inValue instanceof int[]) && !(inValue instanceof Object[])) {
         throw new Object("Specified inValue is not supported");
      }

      int[] array;
      if (inValue instanceof Object[]) {
         Object[] objectInValue = (Object[])inValue;
         int i = objectInValue.length - 1;

         while (i >= 0 && !(objectInValue[i] instanceof int[])) {
            i--;
         }

         if (i < 0) {
            throw new Object("Specified inValue is not supported");
         }

         array = (int[])objectInValue[i];
      } else {
         array = (int[])inValue;
      }

      int index = array[0] != 5 && array[0] != 32773 ? 2 : 3;
      if (array.length >= index + 2 && array[index] == -1 && array[index + 1] == -1) {
         LongVector handles = (LongVector)this._controlContainer.getValue();
         if (handles == null) {
            return null;
         }

         switch (valueType) {
            case 32767:
            case 32769:
            case 32770:
            case 32773:
               break;
            case 32768:
            case 32771:
            default:
               if (value == null) {
                  value = new Object();
               } else {
                  ((Vector)value).removeAllElements();
               }
               break;
            case 32772:
            case 32774:
               if (value == null) {
                  value = new LongVector();
               } else {
                  ((LongVector)value).removeAllElements();
               }
         }

         ArrayInValueResolver.resolve(handles.toArray(), inValue, this, value, null, this._controlContainer != null);
         return value;
      } else {
         return value;
      }
   }

   protected void resolveInValue() {
      if (this._inValue != null) {
         Object oldValue = this._value;
         this._value = this.resolveInValue(this._inValue, this._valueType);
         this._valueChanged = this._valueChanged
            | ((this._valueType & 32768) != 0 || oldValue != this._value && (oldValue == null || !oldValue.equals(this._value)));
      }
   }

   protected boolean isMappingValid() {
      return this._mapping == null ? true : !this._valueChanged;
   }

   protected void updateMappedValue() {
      this._mappedValue = SingleValueHelper.convertValue(this.getScreen().getWiclet(), this._mappingType, this._valueType, this._value, 0);
   }

   protected boolean isEmptyKey(Object value) {
      return false;
   }

   protected Object getDataMappedValue() {
      if (this._mapping != null) {
         WicletEx wiclet = (WicletEx)this.getScreen().getWiclet();
         if (this._mapping[0] != -1) {
            return wiclet.getValue((long)this._mapping[0] << 32, this._mapping, 1, this._mapping.length);
         }

         if (this._mapping.length == 2) {
            return new Object(((ScreenModelImpl)this.getScreen()).getVarValue(this._mapping[1]));
         }

         long var = ((ScreenModelImpl)this.getScreen()).getVarValue(this._mapping[1], true);
         if (var != -1) {
            return wiclet.getValue(var, this._mapping, 2, this._mapping.length);
         }
      }

      return null;
   }

   protected void resolveMapping() {
      if (!this.isMappingValid()) {
         this.updateMappedValue();
         this._valueChanged = false;
         this._isMappingApplied = false;
      }

      if (this._mapping != null && !this._isMappingApplied) {
         WicletEx wiclet = (WicletEx)this.getScreen().getWiclet();
         if (this._mapping[0] != -1) {
            long dataHandle = (long)this._mapping[0] << 32;
            int startIndex = 1;
            if (this._mapping.length > 2) {
               startIndex = 2;
               Data data = (Data)wiclet.getData(this._mapping[0]);
               dataHandle = data.getReferenceField(this._mapping[1]);
               if (dataHandle == -1) {
                  int varType = data.getDef().getFieldReferenceType(this._mapping[1]);
                  dataHandle = this.createIfStdCmp(varType, wiclet);
                  if (dataHandle != -1) {
                     data.setReferenceField(this._mapping[1], dataHandle);
                  }
               }
            }

            if (dataHandle != -1) {
               int level = wiclet.setValue(dataHandle, this._mapping, startIndex, this._mapping.length, this._mappedValue);
               if (level == -1) {
                  this._isMappingApplied = true;
                  if (this._mappingKey != null) {
                     this._mappingResolver.notifyMappingChange(this._mappingKey);
                  }
               }
            }
         } else if (this._mapping.length == 2) {
            long handle = !(this._mappedValue instanceof Object) ? -1 : this._mappedValue;
            ((ScreenModelImpl)this.getScreen()).setVarValue(this._mapping[1], handle);
            this._isMappingApplied = true;
            if (handle != -1) {
               this._mappingResolver.notifyMappingChange(this._mappingKey);
               return;
            }
         } else {
            long var = ((ScreenModelImpl)this.getScreen()).getVarValue(this._mapping[1], true);
            if (var == -1) {
               int varType = ((ScreenModelImpl)this.getScreen()).getVarType(this._mapping[1]);
               var = this.createIfStdCmp(varType, wiclet);
               if (var != -1) {
                  ((ScreenModelImpl)this.getScreen()).setVarValue(this._mapping[1], var);
               }
            }

            if (var != -1) {
               int level = wiclet.setValue(var, this._mapping, 2, this._mapping.length, this._mappedValue);
               if (level == -1) {
                  this._isMappingApplied = true;
                  if (this._mappingKey != null) {
                     this._mappingResolver.notifyMappingChange(this._mappingKey);
                     return;
                  }
               }
            }
         }
      }
   }

   void onEvent(int event) {
      int eventId = this._events[event];
      ScreenModel screenModel = this.getScreen();
      ((ScreenModelImpl)screenModel).handleEvent(event, eventId);
   }

   protected void setEvent(int eventType, int eventId) {
      if (eventType >= 0 && eventType < 4) {
         if (this._events == null && eventId != -1) {
            this._events = new int[4];
            Arrays.fill(this._events, -1);
         }

         if (this._events != null) {
            this._events[eventType] = eventId;
         }
      } else {
         throw new Object(((StringBuffer)(new Object("Invalid event type: "))).append(eventType).toString());
      }
   }

   protected void setMapping(int[] mapping) {
      this._mapping = mapping;
      if (this._mapping != null) {
         int dataDefId = -1;
         int field = -1;
         WicletEx wiclet = (WicletEx)this.getScreen().getWiclet();
         int type;
         ComponentDef def;
         if (this._mapping[0] == -1) {
            dataDefId = ((ScreenModelImpl)this.getScreen()).getVarType(this._mapping[1]);
            def = wiclet.getDataCollection(dataDefId).getDef();
            type = 6;
         } else {
            def = ((Component)wiclet.getData(this._mapping[0])).getDef();
            field = this._mapping[1];
            type = def.getFieldType(field);
            if (type == 6) {
               dataDefId = def.getFieldReferenceType(field);
               def = wiclet.getDataCollection(dataDefId).getDef();
            }
         }

         if (dataDefId != -1) {
            int depth = this._mapping.length;

            for (int index = 2; index < depth; index++) {
               field = this._mapping[index];
               type = def.getFieldType(field);
               if (type == 6) {
                  dataDefId = def.getFieldReferenceType(field);
                  def = wiclet.getDataCollection(dataDefId).getDef();
               }
            }
         }

         switch (type) {
            case 5:
            case 32773:
            case 32774:
               this._mappingType = (long)def.getFieldReferenceType(field) << 32 | 4294967295L & type;
               break;
            case 6:
               this._mappingType = (long)dataDefId << 32 | 6;
               break;
            default:
               this._mappingType = type;
         }

         if (this._mapping.length > 2) {
            int[] rootMapping = new int[this._mapping.length - 1];
            System.arraycopy(this._mapping, 0, rootMapping, 0, this._mapping.length - 1);
            this._mappingResolver.register(rootMapping, this);
         }

         if ((int)(this._mappingType & 4294967295L) == 6 || (int)(this._mappingType & 4294967295L) == 32774) {
            this._mappingKey = this._mappingResolver.getMappingKey(mapping);
            this._mappingResolver.registerAsRoot(this);
         }
      }
   }

   protected void setInValue(Object inValue) {
      this._inValue = inValue;
      this._valueType = 3;
      int controlType = this.getType();
      if (controlType != 137 && controlType != 140 && controlType != 141) {
         if (controlType == 130 || this.requireInValueArray()) {
            this._valueType |= 32768;
         }
      } else {
         this._valueType = 32774;
      }
   }

   protected boolean requireInValueArray() {
      boolean ret = false;
      if ((this._inValue instanceof int[] || this._inValue instanceof Object[]) && this._controlContainer != null) {
         int[] inValue;
         if (!(this._inValue instanceof Object[])) {
            inValue = (int[])this._inValue;
         } else {
            Object[] objectInValue = (Object[])this._inValue;
            int i = objectInValue.length - 1;

            while (i >= 0 && !(objectInValue[i] instanceof int[])) {
               i--;
            }

            if (i < 0) {
               return false;
            }

            inValue = (int[])objectInValue[i];
         }

         int index = inValue[0] != 5 && inValue[0] != 32773 ? 2 : 3;
         if (inValue.length >= index + 2 && inValue[index] == -1 && inValue[index + 1] == -1) {
            ret = true;
         }
      }

      return ret;
   }

   @Override
   public void eventOccurred(int event) {
      if (this.hasEvent(event) && this.getScreen().isDisplayed()) {
         this.getScreen().getWiclet().getRuntime().enqueueRunnable(new UiAction(this, event));
      }
   }

   @Override
   public void initializeToEmpty(Object value) {
      if (value != null && (!value.equals(this._value) || (this._valueType & 32768) != 0)) {
         this._value = value;
         if ((int)(this._mappingType & 4294967295L) != 6) {
            this.onValueChanged(true);
         }
      }
   }

   @Override
   public synchronized void setValue(Object value, boolean fromUI) {
      if (!fromUI || this._value != null || !(value instanceof Object) || ((String)value).length() != 0) {
         if (this._value != null || value != null) {
            if (this._value == null || value == null || !this._value.equals(value) || (this._valueType & 32768) != 0 || this.isEmptyKey(value)) {
               this._value = value;
               this.onValueChanged(fromUI);
            }
         }
      }
   }

   @Override
   public synchronized Object getValue() {
      return this._value;
   }

   @Override
   public long getValueType() {
      return this._valueType;
   }

   @Override
   public Object getMappedValue() {
      return this._mappedValue;
   }

   @Override
   public boolean isReadOnly() {
      return (super._bits & 536870912) != 0;
   }

   @Override
   public boolean isMandatory() {
      return (super._bits & 1) != 0 && !this.isReadOnly();
   }

   @Override
   public boolean isMandatorySatisfied() {
      if (this.isMandatory()) {
         return !(this._value instanceof Object) ? false : ((String)this._value).trim().length() > 0;
      } else {
         return true;
      }
   }

   @Override
   public long getMappedValueType() {
      return this._mappingType;
   }

   @Override
   public void updateData() {
      this.resolveMapping();
   }

   protected UIControlImpl(int id, int type, int style, int bits, int x, int y, int initId) {
      super(id, type, style, bits, x, y, initId);
      this._mappingType = -1;
   }

   private long createIfStdCmp(int varType, WicletEx wiclet) {
      DataCollection dc = wiclet.getDataCollection(varType);
      return dc instanceof StdCmpCollection ? dc.create() : -1;
   }

   @Override
   public void updateUI() {
      if (this._inValue == null) {
         this._isMappingApplied = false;
      }

      this.resolveInValue();
      this.resolveMapping();
   }

   private boolean hasEvent(int event) {
      if (this._events != null && event < this._events.length && event >= 0) {
         int eventId = this._events[event];
         if (eventId != -1) {
            return true;
         }
      }

      return false;
   }

   protected UIControlImpl(int id, int type, UIContainer parent, int style, int bits, int x, int y, int initId, Object inValue) {
      super(id, type, parent, style, bits, x, y, initId);

      for (this._mappingType = -1; parent != null; parent = parent.getParent()) {
         if (parent.getType() == 137 || parent.getType() == 140 || parent.getType() == 141) {
            this._controlContainer = (UIControl)parent;
            break;
         }
      }

      this._mappingResolver = ((ScreenModelImpl)this.getScreen()).getMappingResolver();
      this.setInValue(inValue);
   }

   @Override
   protected void reset() {
      super.reset();
      this._value = null;
      this._valueChanged = false;
      this._mappedValue = null;
   }
}
