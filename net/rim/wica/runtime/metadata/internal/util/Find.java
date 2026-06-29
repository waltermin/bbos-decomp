package net.rim.wica.runtime.metadata.internal.util;

import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.StringTokenizer;
import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.runtime.metadata.MetadataException;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.util.ValueResolver;
import net.rim.wica.runtime.util.GenericComparator;
import net.rim.wica.runtime.util.LongVector;

public final class Find {
   private WicletEx _wiclet;
   public static final int OPERATOR_EQUAL;
   public static final int OPERATOR_LESS_THAN;
   public static final int OPERATOR_GREATER_THAN;
   public static final int OPERATOR_NOT_EQUAL;
   public static final int OPERATOR_GREATER_OR_EQUAL;
   public static final int OPERATOR_LESS_OR_EQUAL;
   private static GenericComparator _comparator = new GenericComparator();

   public Find(WicletEx wiclet) {
      this._wiclet = wiclet;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void findWhere(LongVector results, DataCollection dataCollection, long[] handles, String expression, ValueResolver resolver) {
      MetadataException e;
      try {
         try {
            int index = -1;

            int var10000;
            do {
               var10000 = expression.length();
               index++;
            } while (var10000 > index && expression.charAt(index) == ' ');

            int endIndex = expression.indexOf(32, index);
            if (endIndex != -1 && expression.substring(index, endIndex).equalsIgnoreCase("orderby")) {
               IntVector orderByFields = (IntVector)(new Object());
               int var28 = endIndex + 1;
               endIndex = expression.indexOf(32, var28);
               if (endIndex == -1) {
                  endIndex = expression.length();
               }

               this.parseFields(dataCollection.getDef(), expression.substring(var28, endIndex), orderByFields);
               orderByFields.trimToSize();
               boolean asc = true;
               if (expression.length() > endIndex) {
                  int var29 = endIndex + 1;
                  endIndex = expression.indexOf(32, var29);
                  if (endIndex == -1) {
                     endIndex = expression.length();
                  }

                  asc = expression.substring(var29, endIndex).equalsIgnoreCase("asc");
               }

               this.sort(results, dataCollection, handles, orderByFields.getArray(), asc);
            } else {
               IntVector dataFields = (IntVector)(new Object());
               int var22 = expression.indexOf(60);
               int operator;
               if (var22 != -1) {
                  switch (expression.charAt(var22 + 1)) {
                     case '<':
                        operator = 2;
                        break;
                     case '=':
                     default:
                        operator = 3;
                        break;
                     case '>':
                        operator = 0;
                  }
               } else {
                  var22 = expression.indexOf(62);
                  if (var22 != -1) {
                     operator = expression.charAt(var22 + 1) == '=' ? 5 : 4;
                  } else {
                     var22 = expression.indexOf(61);
                     if (var22 == -1) {
                        throw new MetadataException(((StringBuffer)(new Object("Not recognized operation in "))).append(expression).toString());
                     }

                     if (expression.charAt(var22 - 1) == '!') {
                        var22--;
                        operator = 0;
                     } else {
                        operator = 1;
                     }
                  }
               }

               String dataString = expression.substring(0, var22).trim();
               if (operator == 0 || operator == 3 || operator == 5) {
                  var22++;
               }

               long dataType = this.parseFields(dataCollection.getDef(), dataString, dataFields);
               dataFields.trimToSize();

               do {
                  var10000 = expression.length();
                  var22++;
               } while (var10000 > var22 && expression.charAt(var22) == ' ');

               Object value = null;
               char ch = expression.charAt(var22);
               switch (ch) {
                  case '"':
                  case '\'':
                     endIndex = expression.lastIndexOf(ch);
                     if (endIndex == -1) {
                        endIndex = expression.length();
                     }

                     value = SingleValueHelper.parseString(this._wiclet, dataType, expression.substring(++var22, endIndex));
                     break;
                  case '@':
                     endIndex = expression.indexOf(32, ++var22);
                     if (endIndex == -1) {
                        endIndex = expression.length();
                     }

                     if (resolver != null) {
                        value = resolver.getVariableValue(expression.substring(var22, endIndex));
                     }
                     break;
                  default:
                     throw new MetadataException(
                        ((StringBuffer)(new Object("Error in value to compare: not specified properly in "))).append(expression).toString()
                     );
               }

               int var25 = endIndex;

               do {
                  var10000 = expression.length();
                  var25++;
               } while (var10000 > var25 && expression.charAt(var25) == ' ');

               if (expression.length() <= var25) {
                  this.find(results, dataCollection, handles, dataFields.getArray(), operator, value, null, false);
                  return;
               }

               IntVector orderByFields = (IntVector)(new Object());
               int var26 = expression.indexOf(32, var25) + 1;
               endIndex = expression.indexOf(32, var26);
               if (endIndex == -1) {
                  endIndex = expression.length();
               }

               this.parseFields(dataCollection.getDef(), expression.substring(var26, endIndex), orderByFields);
               orderByFields.trimToSize();
               boolean asc = true;
               if (expression.length() > endIndex) {
                  int var27 = endIndex + 1;
                  endIndex = expression.indexOf(32, var27);
                  if (endIndex == -1) {
                     endIndex = expression.length();
                  }

                  asc = expression.substring(var27, endIndex).equalsIgnoreCase("asc");
               }

               this.find(results, dataCollection, handles, dataFields.getArray(), operator, value, orderByFields.getArray(), asc);
            }

            return;
         } catch (MetadataException var19) {
            e = var19;
         }
      } catch (Throwable var20) {
         throw new MetadataException(((StringBuffer)(new Object("Error during findWhere: "))).append(e.getMessage()).toString());
      }

      throw e;
   }

   private final void find(
      LongVector results, DataCollection dataCollection, long[] all, int[] data, int operator, Object value, int[] orderByField, boolean asc
   ) {
      int i = 0;
      int allLength = all.length;

      for (int index = 0; index < allLength; index++) {
         long handle = all[index];
         if (dataCollection.contains(handle)) {
            DataCollection dc = dataCollection;

            for (int var18 = 0; var18 < data.length; var18++) {
               int type = dc.getDef().getFieldType(data[var18]);
               if (type != 6) {
                  Object fieldValue = dc.getFieldValueAsObject(handle, data[var18]);
                  int result = _comparator.compare(fieldValue, value);
                  if ((operator & 1) != 0 && result == 0
                     || (operator & 2) != 0 && result == -1
                     || (operator & 4) != 0 && result == 1
                     || operator == 0 && result != 0) {
                     if (orderByField != null) {
                        this.addFoundElement(results, dataCollection, all[index], orderByField, asc);
                     } else {
                        results.addElement(all[index]);
                     }
                  }
                  break;
               }

               handle = dc.getReferenceField(handle, data[var18]);
               if (handle == -1) {
                  break;
               }

               dc = this._wiclet.getDataCollection((int)(handle >> 32));
               if (var18 == data.length - 1) {
                  boolean equals = dc.equals(handle, value);
                  if (equals && operator == 1 || !equals && operator == 0) {
                     if (orderByField != null) {
                        this.addFoundElement(results, dataCollection, all[index], orderByField, asc);
                     } else {
                        results.addElement(all[index]);
                     }
                  }
                  break;
               }
            }
         }
      }
   }

   private final void sort(LongVector results, DataCollection dataCollection, long[] handles, int[] orderByField, boolean asc) {
      for (int index = handles.length - 1; index >= 0; index--) {
         this.addFoundElement(results, dataCollection, handles[index], orderByField, asc);
      }
   }

   private final void addFoundElement(LongVector dc, DataCollection dataCollection, long handle, int[] orderByFields, boolean asc) {
      int low = 0;
      int high = dc.size() - 1;
      Object newValue = this.getValue(dataCollection, handle, orderByFields);
      if (newValue == null) {
         if (asc) {
            dc.insertElementAt(handle, 0);
         } else {
            dc.addElement(handle);
         }
      } else {
         while (low <= high) {
            int mid = low + high >> 1;
            Object midValue = this.getValue(dataCollection, dc.elementAt(mid), orderByFields);
            if (midValue == null) {
               if (asc) {
                  low = mid + 1;
               } else {
                  high = mid - 1;
               }
            } else {
               if (newValue.equals(midValue)) {
                  low = mid + 1;
                  break;
               }

               if (_comparator.compare(newValue, midValue) < 0 ^ asc) {
                  low = mid + 1;
               } else {
                  high = mid - 1;
               }
            }
         }

         dc.insertElementAt(handle, low);
      }
   }

   private final Object getValue(DataCollection dc, long handle, int[] fields) {
      if (dc.contains(handle)) {
         for (int i = 0; i < fields.length; i++) {
            int type = dc.getDef().getFieldType(fields[i]);
            if (type != 6) {
               return dc.getFieldValueAsObject(handle, fields[i]);
            }

            handle = dc.getReferenceField(handle, fields[i]);
            if (handle == -1) {
               return null;
            }

            dc = this._wiclet.getDataCollection((int)(handle >> 32));
         }
      }

      return null;
   }

   private final long parseFields(ComponentDef def, String data, IntVector dataFields) {
      StringTokenizer dataTokenizer = (StringTokenizer)(new Object(data, '.'));

      while (true) {
         int field = def.getFieldHandle(dataTokenizer.nextToken());
         dataFields.addElement(field);
         if (!dataTokenizer.hasMoreTokens()) {
            int type = def.getFieldType(field);
            int refType = 0;
            if (type == 6 || type == 5) {
               refType = def.getFieldReferenceType(field);
            }

            return (long)refType << 32 | 4294967295L & type;
         }

         def = this._wiclet.getDataCollection(def.getFieldReferenceType(field)).getDef();
      }
   }
}
