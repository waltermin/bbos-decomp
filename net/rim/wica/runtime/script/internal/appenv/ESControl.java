package net.rim.wica.runtime.script.internal.appenv;

import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESArray;
import net.rim.ecmascript.runtime.ESDate;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIControl;
import net.rim.wica.runtime.metadata.component.ui.control.Selectable;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.script.internal.EcmaUtilities;
import net.rim.wica.runtime.script.internal.WicaAppContext;
import net.rim.wica.runtime.script.internal.handler.DateFieldHandler;
import net.rim.wica.runtime.script.internal.handler.StringFieldHandler;
import net.rim.wica.runtime.util.LongVector;

class ESControl extends RedirectedObject {
   private WicaAppContext _context;
   private UIComponent _model;
   public static final String VisibleProperty;
   private static final String Mapping;
   private static final String Selected;
   private static final String SelectedValue;
   private static final String Contents;
   private static final String Enabled;
   private static final String Mandatory;

   ESControl(UIComponent model, WicaAppContext context) {
      this("MDSControlElement", model, context, context.getControlPrototype());
   }

   ESControl(String clazz, UIComponent model, WicaAppContext context, ESObject prototype) {
      super(clazz, prototype);
      this._model = model;
      this._context = context;
   }

   UIComponent getModel() {
      return this._model;
   }

   @Override
   public long requestFieldValue(String name) {
      long objId = Value.DEFAULT;
      int ctrlType = this._model.getType();
      if (name == "visible") {
         return Value.makeBooleanValue(this._model.isVisible());
      }

      if (ctrlType != 128) {
         UIControl control = (UIControl)this._model;
         if (name == "value") {
            return this._context.getConverter().convertMDSObjectToESValue(control.getValue(), control.getValueType(), control);
         }

         if (name == "mapping") {
            return this._context.getConverter().convertMDSObjectToESValue(control.getMappedValue(), control.getMappedValueType());
         }

         if (name == "enabled") {
            return Value.makeBooleanValue(control.isReadOnly());
         }

         if (name == "mandatory") {
            return Value.makeBooleanValue(control.isMandatory());
         }

         if (name == "selected" && control instanceof Selectable) {
            Selectable selectable = (Selectable)control;
            if (selectable.isMultiSelect()) {
               int[] s = selectable.getSelectedIndices();
               if (s != null) {
                  int sSize = s.length;
                  IntVector iV = (IntVector)(new Object(sSize));
                  System.arraycopy(s, 0, iV.getArray(), 0, sSize);
                  iV.setSize(sSize);
                  return Value.makeObjectValue(new ESMDSArray(32769, iV, null, "int[]", this._context));
               }

               return Value.NULL;
            }

            return Value.makeIntegerValue(selectable.getSelectedIndex());
         }

         if (name == "selectedValue" && control instanceof Selectable) {
            Selectable selectable = (Selectable)control;
            objId = this._context.getConverter().convertMDSObjectToESValue(selectable.getSelectedData(), selectable.getSelectedDataType(), control);
         }
      }

      return objId;
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      int ctrlType = this._model.getType();
      if (name == "visible") {
         this._model.setVisible(Convert.toBoolean(value));
         return false;
      }

      if (ctrlType != 128) {
         UIControl control = (UIControl)this._model;
         if (name == "value") {
            int eType = Value.getType(value);
            Object oValue;
            if (ctrlType == 139) {
               oValue = Convert.toBoolean(value) ? Boolean.TRUE : Boolean.FALSE;
            } else if (ctrlType != 130 && ctrlType != 137 && ctrlType != 140) {
               switch (eType) {
                  case -1:
                  case 1:
                     throw ThrownValue.typeError(RuntimeResources.getString(92));
                  case 0:
                  case 4:
                  case 5:
                  case 7:
                  default:
                     oValue = Convert.toString(value);
                     break;
                  case 2:
                  case 3:
                     oValue = null;
                     break;
                  case 6:
                     oValue = Value.getObjectValue(value);
                     if (oValue instanceof Object) {
                        oValue = new Object((long)((ESDate)oValue).getValue());
                     } else {
                        if (!(oValue instanceof ESEnum)) {
                           throw ThrownValue.typeError(RuntimeResources.getString(92));
                        }

                        oValue = Convert.toString(value);
                     }
               }
            } else if (ctrlType != 130) {
               switch (eType) {
                  case 2:
                  case 3:
                     oValue = null;
                     break;
                  case 6:
                     oValue = Value.getObjectValue(value);
                     if (!(oValue instanceof ESMDSArray)) {
                        if (!(oValue instanceof Object)) {
                           throw ThrownValue.typeError(RuntimeResources.getString(94));
                        }

                        ESArray array = (ESArray)oValue;
                        long size = array.getArrayLength();
                        LongVector lV = new LongVector();

                        for (int i = 0; i < size; i++) {
                           lV.addElement(((ESData)Value.getObjectValue(array.getIndex(i))).getHandle());
                        }

                        oValue = lV;
                     } else {
                        ESMDSArray esArray = (ESMDSArray)oValue;
                        int arrayType = esArray.getType();
                        if (arrayType != 32774) {
                           throw ThrownValue.typeError(RuntimeResources.getString(94));
                        }

                        oValue = esArray.getValue();
                     }
                     break;
                  default:
                     throw ThrownValue.typeError(RuntimeResources.getString(94));
               }
            } else {
               label163:
               switch (eType) {
                  case 2:
                  case 3:
                     oValue = null;
                     break;
                  case 6:
                     oValue = Value.getObjectValue(value);
                     if (!(oValue instanceof ESMDSArray)) {
                        if (!(oValue instanceof Object)) {
                           throw ThrownValue.typeError(RuntimeResources.getString(93));
                        }

                        ESArray esArray = (ESArray)oValue;
                        int size = (int)esArray.getArrayLength();
                        int valueType = (int)(control.getValueType() & -1);
                        if (size == 0) {
                           switch (valueType) {
                              case 32771:
                                 oValue = new Object(0);
                                 break label163;
                              case 32772:
                              default:
                                 oValue = new LongVector(0);
                           }
                        } else {
                           int type = Value.getType(esArray.getIndex(0));
                           switch (type) {
                              case 2:
                                 if (valueType == 32772) {
                                    oValue = new LongVector(size);
                                 } else {
                                    oValue = new Object(size);
                                 }
                                 break label163;
                              case 5:
                                 oValue = new Object();
                                 Vector v = (Vector)oValue;
                                 int i = 0;

                                 while (true) {
                                    if (i >= size) {
                                       break label163;
                                    }

                                    v.addElement(StringFieldHandler.getValue(esArray.getIndex(i)));
                                    i++;
                                 }
                              case 7:
                                 oValue = new LongVector();
                                 LongVector v1 = (LongVector)oValue;
                                 int i = 0;

                                 while (true) {
                                    if (i >= size) {
                                       break label163;
                                    }

                                    v1.addElement(DateFieldHandler.getValue(esArray.getIndex(i)));
                                    i++;
                                 }
                              default:
                                 throw ThrownValue.typeError(RuntimeResources.getString(93));
                           }
                        }
                     } else {
                        ESMDSArray esArray = (ESMDSArray)oValue;
                        int arrayType = esArray.getType();
                        if (arrayType != 32771 && arrayType != 32772) {
                           throw ThrownValue.typeError(RuntimeResources.getString(93));
                        }

                        oValue = esArray.getValue();
                     }
                     break;
                  default:
                     throw ThrownValue.typeError(RuntimeResources.getString(93));
               }
            }

            control.setValue(oValue, false);
            return false;
         }

         if (name == "selected" && control instanceof Selectable) {
            Selectable selectable = (Selectable)this._model;
            if (!selectable.isMultiSelect()) {
               if (Value.getType(value) == 0) {
                  selectable.setSelectedIndex(Value.getIntegerValue(value), false);
                  return false;
               }

               EcmaUtilities.throwESError(RuntimeResources.getString(96));
               return false;
            }

            int[] selectedIndices = new int[0];
            if (Value.getType(value) != 6) {
               EcmaUtilities.throwESError(RuntimeResources.getString(95));
               return false;
            }

            Object objV = Value.getObjectValue(value);
            if (!(objV instanceof Object)) {
               if (!(objV instanceof ESMDSArray)) {
                  EcmaUtilities.throwESError(RuntimeResources.getString(95));
               } else {
                  ESMDSArray esArray = (ESMDSArray)objV;
                  Object theInts = esArray.getValue();
                  if (!(theInts instanceof Object)) {
                     EcmaUtilities.throwESError(RuntimeResources.getString(95));
                  } else {
                     selectedIndices = ((IntVector)theInts).toArray();
                  }
               }
            } else {
               ESArray esOV = (ESArray)objV;
               int length = (int)esOV.getArrayLength();
               selectedIndices = new int[length];

               for (int i = 0; i < length; i++) {
                  long ecmaV = esOV.getIndex(i);
                  if (Value.getType(ecmaV) == 0) {
                     selectedIndices[i] = Value.getIntegerValue(ecmaV);
                  } else {
                     EcmaUtilities.throwESError(RuntimeResources.getString(95));
                  }
               }
            }

            selectable.setSelectedIndices(selectedIndices, false);
            return false;
         }
      }

      return true;
   }
}
