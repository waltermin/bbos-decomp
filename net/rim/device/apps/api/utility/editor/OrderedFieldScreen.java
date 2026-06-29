package net.rim.device.apps.api.utility.editor;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.model.FieldOrders;
import net.rim.vm.Array;

public class OrderedFieldScreen implements FieldOrders {
   private int[] _orderValues = new int[0];
   private int _sepInterval;
   private EditorUsingRIMModelFactory _owner;
   private IntHashtable _managers = (IntHashtable)(new Object());
   private Hashtable _managerFieldOrders = (Hashtable)(new Object());
   public static final int NO_SEPARATORS;
   private static final int SEP;

   public OrderedFieldScreen(EditorUsingRIMModelFactory owner, int separatorInterval) {
      this._owner = owner;
      this._sepInterval = separatorInterval;
   }

   private void expandOrderValues(int pos, int len) {
      int currLen = this._orderValues.length;
      Array.resize(this._orderValues, currLen + len);
      if (pos < currLen) {
         for (int j = currLen - 1; j >= pos; j--) {
            this._orderValues[j + len] = this._orderValues[j];
         }
      }
   }

   private void contractOrderValues(int pos, int len) {
      int currLen = this._orderValues.length;

      for (int j = pos; j < currLen - len; j++) {
         this._orderValues[j] = this._orderValues[j + len];
      }

      Array.resize(this._orderValues, currLen - len);
   }

   private void insertSeparator(int order, int position) {
      if (order != 5600) {
         if (position < 0) {
            this._owner.add((Field)(new Object()));
            return;
         }

         this._owner.insert((Field)(new Object()), position);
      }
   }

   protected boolean contains(Manager manager) {
      return manager != null ? this._managerFieldOrders.containsKey(manager) : false;
   }

   private int getOrderForManagerForField(Field field, int order) {
      int managerOrder = this._owner.getOrderForManagerForField(field, order);
      return managerOrder < 0 ? -1 : managerOrder;
   }

   private Manager getManagerForField(Field field, int order, int managerOrder) {
      if (managerOrder < 0) {
         return null;
      }

      Manager manager = (Manager)this._managers.get(managerOrder);
      if (manager == null) {
         manager = this._owner.createManagerForField(field, order);
         this._managers.put(managerOrder, manager);
      }

      return manager;
   }

   private boolean insertFieldIntoManager(Manager manager, Field field, int order) {
      int[] fieldOrders = (int[])this._managerFieldOrders.get(manager);
      if (fieldOrders == null) {
         fieldOrders = new int[0];
         this._managerFieldOrders.put(manager, fieldOrders);
      }

      int insertionPoint = Arrays.binarySearch(fieldOrders, order);
      if (insertionPoint < 0) {
         insertionPoint = -insertionPoint - 1;
      } else {
         insertionPoint++;

         while (insertionPoint < fieldOrders.length && fieldOrders[insertionPoint] == order) {
            insertionPoint++;
         }
      }

      int numFields = manager.getFieldCount();
      if (insertionPoint >= numFields) {
         manager.add(field);
      } else {
         manager.insert(field, insertionPoint);
      }

      Arrays.insertAt(fieldOrders, order, insertionPoint);
      return manager.getManager() == null;
   }

   public void insertField(Field field, int order) {
      boolean insertField = true;
      int managerOrder = this.getOrderForManagerForField(field, order);
      Manager manager = this.getManagerForField(field, order, managerOrder);
      if (manager != null) {
         insertField = this.insertFieldIntoManager(manager, field, order);
         field = manager;
         order = managerOrder;
      }

      int len = this._orderValues.length;
      if (this._sepInterval < 0) {
         int newPos = 0;

         int k;
         for (k = len - 1; k >= 0; k--) {
            if (order >= this._orderValues[k]) {
               newPos = k + 1;
               break;
            }
         }

         if (k == -1) {
            newPos = 0;
         }

         if (insertField) {
            this.expandOrderValues(newPos, 1);
            this._orderValues[newPos] = order;
            if (newPos == len) {
               this._owner.add(field);
               return;
            }

            this._owner.insert(field, newPos);
            return;
         }
      } else {
         int state = 0;
         int oDiv = order / this._sepInterval;

         int k;
         for (k = len - 1; k >= 0; k--) {
            int val = this._orderValues[k];
            switch (state) {
               case -1:
                  break;
               case 0:
               default:
                  if (order >= val) {
                     int newPos = k + 1;
                     if (oDiv != val / this._sepInterval) {
                        this.expandOrderValues(newPos, 2);
                        this._orderValues[newPos] = -1;
                        this._orderValues[newPos + 1] = order;
                        this.insertSeparator(order, -1);
                        if (insertField) {
                           this._owner.add(field);
                           return;
                        }
                     } else {
                        this.expandOrderValues(newPos, 1);
                        this._orderValues[newPos] = order;
                        if (insertField) {
                           this._owner.add(field);
                        }
                     }

                     return;
                  }

                  state = 2;
                  break;
               case 1:
                  if (order >= val) {
                     int newPos = k + 1;
                     if (oDiv != val / this._sepInterval) {
                        this.expandOrderValues(newPos, 2);
                        this._orderValues[newPos] = -1;
                        this._orderValues[newPos + 1] = order;
                        if (insertField) {
                           this._owner.insert(field, newPos);
                        }

                        this.insertSeparator(order, newPos);
                        return;
                     }

                     this.expandOrderValues(newPos, 1);
                     this._orderValues[newPos] = order;
                     if (insertField) {
                        this._owner.insert(field, newPos);
                     }

                     return;
                  }

                  state = 2;
                  break;
               case 2:
                  if (val == -1) {
                     state = 1;
                  } else if (order >= val) {
                     int newPos = k + 1;
                     if (oDiv != val / this._sepInterval) {
                        if (oDiv != this._orderValues[k + 1] / this._sepInterval) {
                           this.expandOrderValues(newPos, 3);
                           this._orderValues[newPos] = -1;
                           this._orderValues[newPos + 1] = order;
                           this._orderValues[newPos + 2] = -1;
                           this.insertSeparator(val, newPos);
                           if (insertField) {
                              this._owner.insert(field, newPos);
                           }

                           this.insertSeparator(order, newPos);
                           return;
                        }

                        this.expandOrderValues(newPos, 2);
                        this._orderValues[newPos] = -1;
                        this._orderValues[newPos + 1] = order;
                        if (insertField) {
                           this._owner.insert(field, newPos);
                        }

                        this.insertSeparator(order, newPos);
                        return;
                     }

                     if (oDiv != this._orderValues[k + 1] / this._sepInterval) {
                        this.expandOrderValues(newPos, 2);
                        this._orderValues[newPos] = order;
                        this._orderValues[newPos + 1] = -1;
                        this.insertSeparator(order, newPos);
                        if (insertField) {
                           this._owner.insert(field, newPos);
                           return;
                        }
                     } else {
                        this.expandOrderValues(newPos, 1);
                        this._orderValues[newPos] = order;
                        if (insertField) {
                           this._owner.insert(field, newPos);
                        }
                     }

                     return;
                  }
            }
         }

         if (k == -1) {
            if (len == 0) {
               Array.resize(this._orderValues, 1);
               this._orderValues[0] = order;
               if (insertField) {
                  this._owner.add(field);
                  return;
               }
            } else if (oDiv != this._orderValues[k + 1] / this._sepInterval) {
               this.expandOrderValues(0, 2);
               this._orderValues[0] = order;
               this._orderValues[1] = -1;
               this.insertSeparator(order, 0);
               if (insertField) {
                  this._owner.insert(field, 0);
                  return;
               }
            } else {
               this.expandOrderValues(0, 1);
               this._orderValues[0] = order;
               if (insertField) {
                  this._owner.insert(field, 0);
               }
            }
         }
      }
   }

   public void removeField(Field field) {
      Manager manager = field.getManager();
      if (this.contains(manager)) {
         int fieldIndex = field.getIndex();
         int[] fieldOrders = (int[])this._managerFieldOrders.get(manager);
         Arrays.removeAt(fieldOrders, fieldIndex);
         manager.delete(field);
         if (manager.getFieldCount() != 0) {
            return;
         }

         this._managerFieldOrders.remove(manager);
         this._managers.removeValue(manager);
         field = manager;
      }

      int len = this._orderValues.length;

      for (int i = 0; i < len; i++) {
         Field fi = this._owner.getField(i);
         if (fi == field) {
            if (this._sepInterval > 0) {
               if (i == len - 1) {
                  if (i > 0 && this._orderValues[i - 1] == -1) {
                     this.contractOrderValues(i - 1, 2);
                     this._owner.delete(this._owner.getField(i - 1));
                  } else {
                     this.contractOrderValues(i, 1);
                  }
               } else if (this._orderValues[i + 1] == -1) {
                  if (i != 0 && this._orderValues[i - 1] != -1) {
                     this.contractOrderValues(i, 1);
                  } else {
                     this.contractOrderValues(i, 2);
                     this._owner.delete(this._owner.getField(i + 1));
                  }
               } else {
                  this.contractOrderValues(i, 1);
               }
            } else {
               this.contractOrderValues(i, 1);
            }

            this._owner.delete(field);
            return;
         }
      }
   }

   public void replaceField(Field oldField, Field newField) {
      oldField.getManager().replace(oldField, newField);
   }
}
