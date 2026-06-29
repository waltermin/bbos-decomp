package net.rim.wica.runtime.metadata.internal.component.ui.control;

import java.util.Date;
import java.util.Vector;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntVector;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.control.ChoiceControl;
import net.rim.wica.runtime.metadata.internal.component.ui.UIControlImpl;
import net.rim.wica.runtime.metadata.internal.util.ArrayInValueResolver;
import net.rim.wica.runtime.metadata.internal.util.SingleValueHelper;
import net.rim.wica.runtime.util.DoubleVector;
import net.rim.wica.runtime.util.LongVector;
import net.rim.wica.runtime.util.Util;

public final class ChoiceControlImpl extends UIControlImpl implements ChoiceControl {
   private int _selectedIndex = -1;
   private IntVector _selectedIndices;
   private int _choiceType;
   private int _visibleRows;
   private String _format;
   private SimpleDateFormat _dateFormatter;
   private Object _mappingTypeVector;
   private long[] _dataHandles;
   private int _oldValueSize = -1;

   protected final synchronized void setSelectedIndex(int index, boolean modify, boolean fromUI) {
      if (this._selectedIndices != null) {
         int size = this._selectedIndices.size();
         if ((size != 0 || index != -1) && (size != 1 || !this._selectedIndices.contains(index))) {
            this._selectedIndices.removeAllElements();
            if (index != -1) {
               this._selectedIndices.addElement(index);
            }

            if (modify) {
               this.onValueChanged(fromUI);
               return;
            }
         }
      } else if (this._selectedIndex != index) {
         this._selectedIndex = index;
         if (modify) {
            this.onValueChanged(fromUI);
         }
      }
   }

   @Override
   public final synchronized void setSelectedIndex(int index, boolean fromUI) {
      if (fromUI || index > -1 && index < this.getSize() && this._selectedIndex != -1) {
         this.setSelectedIndex(index, true, fromUI);
      }
   }

   @Override
   public final boolean getBorder() {
      return true;
   }

   @Override
   public final boolean isMultiSelect() {
      return this._selectedIndices != null;
   }

   @Override
   public final synchronized int getSelectedIndex() {
      return this._selectedIndex;
   }

   @Override
   public final synchronized boolean isSelected(int index) {
      return this._selectedIndices != null ? this._selectedIndices.contains(index) : this._selectedIndex == index;
   }

   @Override
   public final synchronized int[] getSelectedIndices() {
      return this._selectedIndices != null ? this._selectedIndices.toArray() : null;
   }

   @Override
   public final synchronized void addSelectedIndex(int index, boolean fromUI) {
      if (this._selectedIndices != null) {
         int low = 0;
         int high = this._selectedIndices.size() - 1;

         while (low <= high) {
            int mid = low + high >> 1;
            int midValue = this._selectedIndices.elementAt(mid);
            if (index == midValue) {
               return;
            }

            if (index > midValue) {
               low = mid + 1;
            } else {
               high = mid - 1;
            }
         }

         this._selectedIndices.insertElementAt(index, low);
         this.onValueChanged(fromUI);
      }
   }

   @Override
   public final synchronized void removeSelectedIndex(int index, boolean fromUI) {
      if (this._selectedIndices != null) {
         this._selectedIndices.removeElement(index);
         this.onValueChanged(fromUI);
      }
   }

   @Override
   public final synchronized void setSelectedIndices(int[] selected, boolean fromUI) {
      if (this._selectedIndices != null) {
         this._selectedIndices.setSize(selected.length);
         System.arraycopy(selected, 0, this._selectedIndices.getArray(), 0, selected.length);
         Arrays.sort(this._selectedIndices.getArray(), 0, selected.length);
         this.onValueChanged(fromUI);
      }
   }

   @Override
   public final String getFormat() {
      return this._format;
   }

   @Override
   public final int getChoiceType() {
      return this._choiceType;
   }

   @Override
   public final int getVisibleRows() {
      return this._visibleRows;
   }

   @Override
   public final String getFormattedDate(long longValue) {
      return this._dateFormatter != null ? this._dateFormatter.format(new Date(longValue)) : null;
   }

   @Override
   public final Object getSelectedData() {
      if (this._dataHandles != null) {
         if (this._selectedIndices != null) {
            int size = this._selectedIndices.size();
            LongVector v = new LongVector(size);

            for (int i = 0; i < size; i++) {
               v.addElement(this._dataHandles[this._selectedIndices.elementAt(i)]);
            }

            return v;
         }

         if (this._selectedIndex != -1) {
            return new Long(this._dataHandles[this._selectedIndex]);
         }
      } else if (super._value != null) {
         if (this._selectedIndices != null) {
            int size = this._selectedIndices.size();
            if (super._value instanceof Vector) {
               Vector v = new Vector(size);

               for (int i = 0; i < size; i++) {
                  v.addElement(((Vector)super._value).elementAt(this._selectedIndices.elementAt(i)));
               }

               return v;
            }

            if (super._value instanceof LongVector) {
               LongVector v = new LongVector(size);

               for (int i = 0; i < size; i++) {
                  v.addElement(((LongVector)super._value).elementAt(this._selectedIndices.elementAt(i)));
               }

               return v;
            }
         } else if (this._selectedIndex != -1) {
            if (super._value instanceof Vector) {
               return ((Vector)super._value).elementAt(this._selectedIndex);
            }

            if (super._value instanceof LongVector) {
               return new Long(((LongVector)super._value).elementAt(this._selectedIndex));
            }
         }
      }

      return null;
   }

   @Override
   public final long getSelectedDataType() {
      if (this._dataHandles != null) {
         return this.isMultiSelect() ? 32774 : 6;
      } else {
         return this.isMultiSelect() ? this.getValueType() : this.getValueType() & -32769;
      }
   }

   private final int getSize() {
      if (super._value == null) {
         return 0;
      } else {
         return !(super._value instanceof Vector) ? ((LongVector)super._value).size() : ((Vector)super._value).size();
      }
   }

   @Override
   protected final void resolveInValue() {
      if (super._inValue != null || this._mappingTypeVector != null) {
         if (super._inValue != null) {
            this._oldValueSize = this.getSize();
         }

         if (super._inValue != null) {
            this.clearInValueVectors();
         }

         this.clearMappingVector();
         if (super._inValue != null) {
            this._dataHandles = ArrayInValueResolver.resolve(super._inValue, this, super._value, this._mappingTypeVector);
         } else {
            this._dataHandles = null;
            ArrayInValueResolver.resolveFromScript(this, super._value, this._mappingTypeVector);
         }

         if (this.isMultiSelect() && this._oldValueSize != -1 && this._oldValueSize != this.getSize()) {
            this._oldValueSize = -1;
            this._selectedIndices.removeAllElements();
         }

         super._valueChanged = true;
      }
   }

   @Override
   protected final void onValueChanged(boolean fromUI) {
      if (!fromUI) {
         Object inValue = super._inValue;
         super._inValue = null;
         this.resolveInValue();
         super._inValue = inValue;
      }

      super.onValueChanged(fromUI);
   }

   private final void validateSelection() {
      int size = this.getSize();
      if (size > 0) {
         if (!this.isMultiSelect()) {
            if (this._selectedIndex <= -1) {
               this.validateSingleSelect(size);
               return;
            }

            this._selectedIndex = this._selectedIndex >= size ? (this._selectedIndex = this.getDefaultSelectionIndex()) : this._selectedIndex;
            return;
         }

         if (!this.hasSelection()) {
            this.validateMultiSelect(size);
            return;
         }
      } else if (this.hasSelection()) {
         this.setSelectedIndex(-1, false, false);
      }
   }

   private final void validateMultiSelect(int valueCount) {
      Object dataMappedValue = this.getDataMappedValue();
      if (dataMappedValue != null && this._mappingTypeVector != null) {
         this.setSelectedIndex(-1, false, false);
         if (!(dataMappedValue instanceof Vector)) {
            if (!(dataMappedValue instanceof IntVector)) {
               if (!(dataMappedValue instanceof DoubleVector)) {
                  if (dataMappedValue instanceof LongVector) {
                     boolean doSimpleLookup = true;
                     if ((int)(super._mappingType & 4294967295L) == 32774) {
                        DataCollection dc = this.getScreen().getWiclet().getDataCollection((int)(super._mappingType >> 32));
                        if (!dc.getDef().hasKey()) {
                           doSimpleLookup = false;
                           int selSize = ((LongVector)dataMappedValue).size();

                           for (int i = 0; i < selSize; i++) {
                              long mappedHandle = ((LongVector)dataMappedValue).elementAt(i);
                              if (dc.contains(mappedHandle)) {
                                 for (int j = ((LongVector)this._mappingTypeVector).size() - 1; j >= 0; j--) {
                                    long handle = ((LongVector)this._mappingTypeVector).elementAt(j);
                                    if (dc.contains(handle) && dc.equals(mappedHandle, handle)) {
                                       if (i >= 0 && i < valueCount) {
                                          this.addSelectedIndex(i, true);
                                       }
                                       break;
                                    }
                                 }
                              }
                           }
                        }
                     }

                     if (doSimpleLookup) {
                        int selSize = ((LongVector)dataMappedValue).size();

                        for (int i = 0; i < selSize; i++) {
                           int index = ((LongVector)this._mappingTypeVector).indexOf(((LongVector)dataMappedValue).elementAt(i));
                           if (index >= 0 && index < valueCount) {
                              this.addSelectedIndex(index, true);
                           }
                        }
                     }
                  }
               } else {
                  int selSize = ((DoubleVector)dataMappedValue).size();

                  for (int i = 0; i < selSize; i++) {
                     int index = ((DoubleVector)this._mappingTypeVector).indexOf(((DoubleVector)dataMappedValue).elementAt(i));
                     if (index >= 0 && index < valueCount) {
                        this.addSelectedIndex(index, true);
                     }
                  }
               }
            } else {
               int selSize = ((IntVector)dataMappedValue).size();

               for (int i = 0; i < selSize; i++) {
                  int index = ((IntVector)this._mappingTypeVector).indexOf(((IntVector)dataMappedValue).elementAt(i));
                  if (index >= 0 && index < valueCount) {
                     this.addSelectedIndex(index, true);
                  }
               }
            }
         } else {
            int selSize = ((Vector)dataMappedValue).size();

            for (int i = 0; i < selSize; i++) {
               int index = ((Vector)this._mappingTypeVector).indexOf(((Vector)dataMappedValue).elementAt(i));
               if (index >= 0 && index < valueCount) {
                  this.addSelectedIndex(index, true);
               }
            }
         }
      } else if (this._selectedIndices.size() > 0 && this._selectedIndices.lastElement() >= valueCount) {
         this.setSelectedIndex(-1, false, false);
      }
   }

   private final void validateSingleSelect(int valueCount) {
      Object dataMappedValue = this.getDataMappedValue();
      if (dataMappedValue != null) {
         int index = -1;
         if (this._mappingTypeVector == null) {
            if ((int)(super._mappingType & 4294967295L) == 6 && dataMappedValue instanceof Long) {
               long handle = (Long)dataMappedValue;
               if (handle != -1) {
                  DataCollection dc = this.getScreen().getWiclet().getDataCollection((int)(super._mappingType >> 32));
                  if (dc instanceof KeyDataCollection) {
                     Object value = ((KeyDataCollection)dc).getPKey(handle);
                     if (value != null) {
                        if (!(super._value instanceof Vector)) {
                           if (super._value instanceof LongVector) {
                              index = ((LongVector)super._value).indexOf((Long)value);
                           }
                        } else {
                           index = ((Vector)super._value).indexOf(value.toString());
                        }
                     }
                  }
               }
            }
         } else if (!(this._mappingTypeVector instanceof Vector)) {
            if (this._mappingTypeVector instanceof IntVector) {
               if (dataMappedValue instanceof Boolean) {
                  boolean value = (Boolean)dataMappedValue;
                  dataMappedValue = new Integer(value ? 1 : 0);
               }

               index = ((IntVector)this._mappingTypeVector).indexOf((Integer)dataMappedValue);
            } else if (!(this._mappingTypeVector instanceof DoubleVector)) {
               if (this._mappingTypeVector instanceof LongVector) {
                  boolean doSimpleLookup = true;
                  if ((int)(super._mappingType & 4294967295L) == 6) {
                     DataCollection dc = this.getScreen().getWiclet().getDataCollection((int)(super._mappingType >> 32));
                     if (!dc.getDef().hasKey()) {
                        doSimpleLookup = false;
                        long mappedHandle = (Long)dataMappedValue;
                        if (dc.contains(mappedHandle)) {
                           LongVector dataHandles = (LongVector)this._mappingTypeVector;
                           int dataHandlesCount = dataHandles.size();

                           for (int i = 0; i < dataHandlesCount; i++) {
                              long handle = dataHandles.elementAt(i);
                              if (handle != -1 && dc.contains(handle) && dc.equals(handle, mappedHandle)) {
                                 index = i;
                                 break;
                              }
                           }
                        }
                     }
                  }

                  if (doSimpleLookup) {
                     index = ((LongVector)this._mappingTypeVector).indexOf((Long)dataMappedValue);
                  }
               }
            } else {
               index = ((DoubleVector)this._mappingTypeVector).indexOf((Double)dataMappedValue);
            }
         } else {
            index = ((Vector)this._mappingTypeVector).indexOf(dataMappedValue);
         }

         if (index != -1) {
            this._selectedIndex = index;
         }
      }

      if (this._selectedIndex == -1 || this._selectedIndex >= valueCount) {
         this._selectedIndex = this.getDefaultSelectionIndex();
      }
   }

   private final void clearInValueVectors() {
      if (super._value == null) {
         this.createInValueVectors();
      }

      switch ((int)this.getValueType()) {
         case 32771:
         default:
            ((Vector)super._value).removeAllElements();
            return;
         case 32774:
            ((LongVector)super._value).removeAllElements();
         case 32770:
         case 32772:
         case 32773:
      }
   }

   private final void clearMappingVector() {
      if (this._mappingTypeVector != null) {
         int mappingType = (int)super._mappingType & 32767;
         switch (mappingType) {
            case -1:
            case 7:
               break;
            case 0:
            case 1:
            case 5:
            default:
               ((IntVector)this._mappingTypeVector).removeAllElements();
               return;
            case 2:
               ((DoubleVector)this._mappingTypeVector).removeAllElements();
               return;
            case 3:
               ((Vector)this._mappingTypeVector).removeAllElements();
               break;
            case 4:
            case 6:
            case 8:
               ((LongVector)this._mappingTypeVector).removeAllElements();
               return;
         }
      }
   }

   private final void createInValueVectors() {
      switch ((int)this.getValueType()) {
         case 32770:
         case 32772:
         case 32773:
            break;
         case 32771:
         default:
            super._value = new Vector();
            break;
         case 32774:
            super._value = new LongVector();
      }

      if (super._mapping != null && ArrayInValueResolver.canAutoResolveMapping(this, super._inValue, super._mappingType & -32769)) {
         int mappingType = (int)super._mappingType & 32767;
         switch (mappingType) {
            case -1:
            case 7:
               break;
            case 0:
            case 1:
            case 5:
            default:
               this._mappingTypeVector = new IntVector();
               return;
            case 2:
               this._mappingTypeVector = new DoubleVector();
               return;
            case 3:
               this._mappingTypeVector = new Vector();
               break;
            case 4:
            case 6:
            case 8:
               this._mappingTypeVector = new LongVector();
               return;
         }
      }
   }

   @Override
   protected final void updateMappedValue() {
      if (this._selectedIndices != null) {
         this.updateMappedValueVector();
      } else if (this._selectedIndex != -1) {
         this.updateSingleMappedValue();
      } else {
         super._mappedValue = null;
      }
   }

   private final void updateSingleMappedValue() {
      if (this._mappingTypeVector == null) {
         if (6 == (int)(super._mappingType & 4294967295L)) {
            Object key = !(super._value instanceof Vector)
               ? new Long(((LongVector)super._value).elementAt(this._selectedIndex))
               : ((Vector)super._value).elementAt(this._selectedIndex);
            Wiclet wiclet = this.getScreen().getWiclet();
            super._mappedValue = SingleValueHelper.convertValue(wiclet, super._mappingType, super._value instanceof Vector ? 3 : 8, key, 0);
         } else {
            super._mappedValue = null;
         }
      } else {
         switch ((int)(super._mappingType & 4294967295L)) {
            case -1:
            case 3:
            case 7:
               super._mappedValue = ((Vector)this._mappingTypeVector).size() > 0 ? ((Vector)this._mappingTypeVector).elementAt(this._selectedIndex) : null;
               return;
            case 0:
               super._mappedValue = ((IntVector)this._mappingTypeVector).size() > 0
                  ? new Boolean(((IntVector)this._mappingTypeVector).elementAt(this._selectedIndex) == 1)
                  : null;
               return;
            case 1:
            case 5:
            default:
               super._mappedValue = ((IntVector)this._mappingTypeVector).size() > 0
                  ? new Integer(((IntVector)this._mappingTypeVector).elementAt(this._selectedIndex))
                  : null;
               return;
            case 2:
               super._mappedValue = ((DoubleVector)this._mappingTypeVector).size() > 0
                  ? new Double(((DoubleVector)this._mappingTypeVector).elementAt(this._selectedIndex))
                  : null;
               return;
            case 4:
            case 6:
            case 8:
               super._mappedValue = ((LongVector)this._mappingTypeVector).size() > 0
                  ? new Long(((LongVector)this._mappingTypeVector).elementAt(this._selectedIndex))
                  : null;
         }
      }
   }

   private final void updateMappedValueVector() {
      int size = this._selectedIndices.size();
      switch ((int)(super._mappingType & 4294967295L)) {
         case 32767:
         case 32775:
            break;
         case 32768:
         case 32769:
         case 32773:
         default:
            if (super._mappedValue == null) {
               super._mappedValue = new IntVector(size);
            } else {
               ((IntVector)super._mappedValue).removeAllElements();
            }
            break;
         case 32770:
            if (super._mappedValue == null) {
               super._mappedValue = new DoubleVector(size);
            } else {
               ((DoubleVector)super._mappedValue).removeAllElements();
            }
            break;
         case 32771:
            if (super._mappedValue == null) {
               super._mappedValue = new Vector(size);
            } else {
               ((Vector)super._mappedValue).removeAllElements();
            }
            break;
         case 32772:
         case 32774:
         case 32776:
            if (super._mappedValue == null) {
               super._mappedValue = new LongVector(size);
            } else {
               ((LongVector)super._mappedValue).removeAllElements();
            }
      }

      if (this._mappingTypeVector == null) {
         if (32774 == (int)(super._mappingType & 4294967295L)) {
            for (int i = 0; i < size; i++) {
               int index = this._selectedIndices.elementAt(i);
               Object key = !(super._value instanceof Vector) ? new Long(((LongVector)super._value).elementAt(index)) : ((Vector)super._value).elementAt(index);
               Wiclet wiclet = this.getScreen().getWiclet();
               Object returnedValue = SingleValueHelper.convertValue(wiclet, super._mappingType & -32769, super._value instanceof Vector ? 3 : 8, key, 0);
               if (returnedValue instanceof Long) {
                  ((LongVector)super._mappedValue).addElement((Long)returnedValue);
               }
            }
         }
      } else {
         for (int i = 0; i < size; i++) {
            int index = this._selectedIndices.elementAt(i);
            switch ((int)(super._mappingType & 4294967295L)) {
               case 32767:
               case 32775:
                  break;
               case 32768:
               case 32769:
               case 32773:
               default:
                  ((IntVector)super._mappedValue).addElement(((IntVector)this._mappingTypeVector).elementAt(index));
                  break;
               case 32770:
                  ((DoubleVector)super._mappedValue).addElement(((DoubleVector)this._mappingTypeVector).elementAt(index));
                  break;
               case 32771:
                  ((Vector)super._mappedValue).addElement(((Vector)this._mappingTypeVector).elementAt(index));
                  break;
               case 32772:
               case 32774:
               case 32776:
                  ((LongVector)super._mappedValue).addElement(((LongVector)this._mappingTypeVector).elementAt(index));
            }
         }
      }
   }

   @Override
   protected final void reset() {
      super.reset();
      this._selectedIndex = this.getDefaultSelectionIndex();
      if (this._selectedIndices != null) {
         this._selectedIndices.removeAllElements();
      }

      this.clearMappingVector();
   }

   public ChoiceControlImpl(
      int id,
      UIContainer parent,
      int style,
      int bits,
      int x,
      int y,
      int initId,
      Object inValue,
      int choiceType,
      int changeId,
      String format,
      int[] mapping,
      int visibleRows
   ) {
      super(id, 130, parent, style, bits, x, y, initId, inValue);
      this._choiceType = choiceType;
      this._visibleRows = visibleRows;
      this._format = format;
      if (this._format != null) {
         this._dateFormatter = new SimpleDateFormat(this._format);
      } else {
         this._dateFormatter = Util.DEFAULT_DATE_FORMATTER;
      }

      this.setEvent(1, changeId);
      this.setMapping(mapping);
      if (choiceType == 1 || choiceType == 4) {
         this._selectedIndices = new IntVector();
      }

      this.createInValueVectors();
   }

   private final int getDefaultSelectionIndex() {
      return this.getSize() <= 0 || this._choiceType != 3 && this._choiceType != 2 ? -1 : 0;
   }

   @Override
   public final boolean isMandatorySatisfied() {
      return this.isMandatory() && this.getSize() > 0 ? this.hasSelection() : true;
   }

   @Override
   public final void updateUI() {
      if (super._inValue == null) {
         super._isMappingApplied = false;
      }

      this.resolveInValue();
      this.validateSelection();
      this.resolveMapping();
   }

   private final boolean hasSelection() {
      return this._selectedIndices != null ? this._selectedIndices.size() > 0 : this._selectedIndex != -1;
   }

   @Override
   public final synchronized void setValue(Object value, boolean fromUI) {
      this._oldValueSize = this.getSize();
      super.setValue(value, fromUI);
   }
}
