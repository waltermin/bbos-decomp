package net.rim.wica.runtime.metadata.internal.util;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.util.IntVector;
import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.common.metadata.component.EnumCollection;
import net.rim.wica.runtime.metadata.MetadataException;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.component.ui.DateFormatter;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.UIControl;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.component.ui.ScreenModelImpl;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.util.DoubleVector;
import net.rim.wica.runtime.util.LongVector;
import net.rim.wica.runtime.util.Util;

public final class ArrayInValueResolver {
   private static int[] _inArray;
   private static int _numFields;
   private static int _currentIndex = 1;
   private static int _lastFieldIndex;
   private static int _arrayLength;
   private static long[] _handles;
   private static DataCollection _dc;
   private static int _defID;
   private static int _type;
   private static int _field;
   private static WicletEx _wiclet;
   private static UIControl _uiControl;
   private static boolean _isContainerControl;
   private static Vector _preResultObjects;
   private static StringBuffer _preString;
   private static Vector _postResultObjects;
   private static StringBuffer _postString;
   private static boolean _mappingNeeded;
   private static int _mappingType;
   private static int _mappingDef;
   private static int _mappingEnumDef;
   private static LongVector _mappingHandles;
   private static boolean _childOfContainer;
   private static boolean _hasDataArray;

   private static final void resetReferences() {
      _inArray = null;
      _handles = null;
      _dc = null;
      _wiclet = null;
      _uiControl = null;
      _preResultObjects = null;
      _preString = null;
      _postResultObjects = null;
      _postString = null;
      _mappingHandles = null;
      _hasDataArray = false;
   }

   private static final void init() {
      _numFields = 0;
      _currentIndex = 0;
      _lastFieldIndex = 0;
      _arrayLength = 0;
      _defID = 0;
      _type = -1;
      _field = -1;
      _preResultObjects = (Vector)(new Object(5));
      _postResultObjects = (Vector)(new Object(5));
      _mappingNeeded = false;
      _mappingType = 0;
      _mappingDef = 0;
      _mappingEnumDef = 0;
      _hasDataArray = false;
   }

   private static final void init(Object inValue, UIControl uiControl, Object mappingVector, boolean childOfContainer) {
      init();
      _isContainerControl = uiControl instanceof UIContainer;
      _childOfContainer = childOfContainer;
      parseInValue(inValue);
      _mappingNeeded = mappingVector != null;
      _uiControl = uiControl;
      _wiclet = (WicletEx)uiControl.getScreen().getWiclet();
      if (_wiclet != null) {
         if (_mappingNeeded) {
            _mappingType = (int)uiControl.getMappedValueType() & 32767;
            if (_mappingType == 6) {
               _mappingDef = (int)(uiControl.getMappedValueType() >> 32);
               _mappingHandles = (LongVector)mappingVector;
            }

            if (_mappingType == 5) {
               _mappingEnumDef = (int)(uiControl.getMappedValueType() >> 32);
            }
         }
      }
   }

   private static final void parseInValue(Object inValue) {
      if (inValue instanceof int[]) {
         _inArray = (int[])inValue;
      } else {
         if (inValue instanceof Object[]) {
            Object[] inObjects = (Object[])inValue;
            boolean intArrayFound = false;

            for (int i = 0; i < inObjects.length; i++) {
               if (inObjects[i] instanceof int[]) {
                  if (intArrayFound) {
                     _inArray = null;
                     return;
                  }

                  _inArray = (int[])inObjects[i];
                  intArrayFound = true;
               } else if (intArrayFound) {
                  _postResultObjects.addElement(inObjects[i]);
               } else {
                  _preResultObjects.addElement(inObjects[i]);
               }
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final boolean canAutoResolveMapping(UIControl uiControl, Object inValue, long mappingType) {
      boolean var16 = false /* VF: Semaphore variable */;

      int offset;
      label170: {
         label169: {
            boolean i;
            label168: {
               int type;
               label167: {
                  boolean var30;
                  label166: {
                     label165: {
                        label164: {
                           int defId;
                           try {
                              var16 = true;
                              if ((int)(mappingType & 4294967295L) == 6) {
                                 init();
                                 defId = (int)(mappingType >> 32);
                                 parseInValue(inValue);
                                 if (_inArray == null) {
                                    offset = 1;
                                    var16 = false;
                                    break label170;
                                 }

                                 if (_inArray.length == 3) {
                                    offset = 0;
                                    var16 = false;
                                    break label169;
                                 }

                                 offset = 1;
                                 if (_inArray[0] == 5 || _inArray[0] == 32773) {
                                    offset++;
                                 }

                                 int lastOffset = offset + _inArray[offset];
                                 offset++;
                                 WicletEx wiclet = (WicletEx)uiControl.getScreen().getWiclet();
                                 int inDef = _inArray[offset] == -1
                                    ? ((ScreenModelImpl)uiControl.getScreen()).getVarType(_inArray[++offset])
                                    : _inArray[offset];
                                 offset++;

                                 while (offset <= lastOffset) {
                                    ComponentDef dataDef = wiclet.getComponentDef(inDef);
                                    type = dataDef.getFieldType(_inArray[offset]);
                                    if (type != 6 && type != 32774) {
                                       i = false;
                                       var16 = false;
                                       break label168;
                                    }

                                    inDef = dataDef.getFieldReferenceType(_inArray[offset]);
                                    offset++;
                                 }

                                 if (inDef == defId) {
                                    type = 1;
                                    var16 = false;
                                    break label167;
                                 }

                                 if (++offset < _inArray.length) {
                                    type = _inArray[offset++];

                                    for (int ix = 0; ix < type; offset++) {
                                       ComponentDef dataDef = wiclet.getComponentDef(inDef);
                                       int typex = dataDef.getFieldType(_inArray[offset]);
                                       if (typex != 6 && typex != 32774) {
                                          var30 = false;
                                          var16 = false;
                                          break label166;
                                       }

                                       inDef = dataDef.getFieldReferenceType(_inArray[offset]);
                                       if (inDef == defId) {
                                          var30 = true;
                                          var16 = false;
                                          break label165;
                                       }

                                       ix++;
                                    }
                                 }

                                 type = 0;
                                 var16 = false;
                                 break label164;
                              }

                              defId = 1;
                              var16 = false;
                           } finally {
                              if (var16) {
                                 resetReferences();
                              }
                           }

                           resetReferences();
                           return (boolean)defId;
                        }

                        resetReferences();
                        return (boolean)type;
                     }

                     resetReferences();
                     return var30;
                  }

                  resetReferences();
                  return var30;
               }

               resetReferences();
               return (boolean)type;
            }

            resetReferences();
            return i;
         }

         resetReferences();
         return (boolean)offset;
      }

      resetReferences();
      return (boolean)offset;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void resolve(long[] handles, Object inValue, UIControl uiControl, Object resultVector, Object mappingVector, boolean childOfContainer) {
      boolean var8 = false /* VF: Semaphore variable */;

      label52: {
         try {
            var8 = true;
            if (inValue == null) {
               var8 = false;
               break label52;
            }

            if (uiControl == null) {
               var8 = false;
               break label52;
            }

            if (uiControl.getScreen() == null) {
               var8 = false;
               break label52;
            }

            if (uiControl.getScreen().getWiclet() == null) {
               var8 = false;
               break label52;
            }

            if (resultVector == null) {
               var8 = false;
               break label52;
            }

            _handles = handles;
            resolveInternal(inValue, uiControl, resultVector, mappingVector, childOfContainer);
            var8 = false;
         } finally {
            if (var8) {
               resetReferences();
            }
         }

         resetReferences();
         return;
      }

      resetReferences();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final long[] resolve(Object inValue, UIControl uiControl, Object resultVector, Object mappingVector) {
      boolean var7 = false /* VF: Semaphore variable */;

      J var9;
      label49: {
         try {
            var7 = true;
            if (inValue == null || uiControl == null || uiControl.getScreen() == null || uiControl.getScreen().getWiclet() == null || resultVector == null) {
               var9 = null;
               var7 = false;
               break label49;
            }

            _handles = new long[1];
            resolveInternal(inValue, uiControl, resultVector, mappingVector, false);
            var9 = _hasDataArray ? _handles : null;
            var7 = false;
         } finally {
            if (var7) {
               resetReferences();
            }
         }

         resetReferences();
         return (long[])var9;
      }

      resetReferences();
      return (long[])var9;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void resolveFromScript(UIControl uiControl, Object resultVector, Object mappingVector) {
      boolean var10 = false /* VF: Semaphore variable */;

      label181: {
         try {
            var10 = true;
            if (uiControl == null) {
               var10 = false;
               break label181;
            }

            if (uiControl.getScreen() == null) {
               var10 = false;
               break label181;
            }

            if (uiControl.getScreen().getWiclet() == null) {
               var10 = false;
               break label181;
            }

            if (resultVector == null) {
               var10 = false;
               break label181;
            }

            if (mappingVector == null) {
               var10 = false;
               break label181;
            }

            _handles = new long[1];
            init(null, uiControl, mappingVector, false);
            if (!_mappingNeeded) {
               var10 = false;
            } else if (_mappingType == 6) {
               var10 = false;
            } else {
               switch (_mappingType) {
                  case 0:
                  case 32768:
                     Vector result = (Vector)resultVector;
                     int size = result.size();
                     IntVector mapping = (IntVector)mappingVector;

                     for (int i = 0; i < size; i++) {
                        String v = (String)result.elementAt(i);
                        if (v.equalsIgnoreCase("True")) {
                           mapping.addElement(1);
                        } else {
                           if (!v.equalsIgnoreCase("False")) {
                              throw new MetadataException(RuntimeResources.getString(150));
                           }

                           mapping.addElement(0);
                        }
                     }

                     var10 = false;
                     break;
                  case 1:
                  case 32769:
                     Vector result = (Vector)resultVector;
                     int size = result.size();
                     IntVector mapping = (IntVector)mappingVector;

                     for (int i = 0; i < size; i++) {
                        mapping.addElement(Integer.parseInt((String)result.elementAt(i)));
                     }

                     var10 = false;
                     break;
                  case 2:
                  case 32770:
                     Vector result = (Vector)resultVector;
                     int size = result.size();
                     DoubleVector mapping = (DoubleVector)mappingVector;

                     for (int i = 0; i < size; i++) {
                        mapping.addElement(Double.parseDouble((String)result.elementAt(i)));
                     }

                     var10 = false;
                     break;
                  case 3:
                  case 32771:
                     Vector result = (Vector)resultVector;
                     int size = result.size();
                     Vector mapping = (Vector)mappingVector;

                     for (int i = 0; i < size; i++) {
                        mapping.addElement(result.elementAt(i));
                     }

                     var10 = false;
                     break;
                  case 4:
                  case 32772:
                     Vector result = (Vector)resultVector;
                     int size = result.size();
                     LongVector mapping = (LongVector)mappingVector;

                     for (int i = 0; i < size; i++) {
                        mapping.addElement(HttpDateParser.parse((String)result.elementAt(i)));
                     }

                     var10 = false;
                     break;
                  case 5:
                  case 32773:
                     Vector result = (Vector)resultVector;
                     int size = result.size();
                     IntVector mapping = (IntVector)mappingVector;
                     EnumCollection e = _wiclet.getEnums();

                     for (int i = 0; i < size; i++) {
                        mapping.addElement(e.getEnumValueAsInt(_mappingEnumDef, (String)result.elementAt(i)));
                     }

                     var10 = false;
                     break;
                  case 8:
                  case 32776:
                     Vector result = (Vector)resultVector;
                     int size = result.size();
                     LongVector mapping = (LongVector)mappingVector;

                     for (int i = 0; i < size; i++) {
                        mapping.addElement(Long.parseLong((String)result.elementAt(i)));
                     }

                     var10 = false;
                     break;
                  default:
                     throw new MetadataException(RuntimeResources.getString(137));
               }
            }
         } finally {
            if (var10) {
               resetReferences();
            }
         }

         resetReferences();
         return;
      }

      resetReferences();
   }

   private static final void resolveInternal(Object inValue, UIControl uiControl, Object resultVector, Object mappingVector, boolean childOfContainer) {
      init(inValue, uiControl, mappingVector, childOfContainer);
      if (_inArray != null && _inArray.length >= 3) {
         if ((int)_uiControl.getValueType() == 32771) {
            processPrePostStrings();
         }

         boolean enumType = _inArray[0] == 5 || _inArray[0] == 32773;
         if (_inArray.length == 3) {
            if (enumType) {
               handleEnumerationInValue(resultVector, mappingVector);
            }
         } else {
            _currentIndex = enumType ? 2 : 1;
            _numFields = _inArray[_currentIndex];
            _lastFieldIndex = _numFields + _currentIndex;
            _arrayLength = _inArray.length;
            _defID = _inArray[++_currentIndex];
            boolean globalVar = false;
            boolean varOrParam = false;
            if (_defID == -1) {
               int varValue = _inArray[++_currentIndex];
               if (varValue != -1) {
                  _handles[0] = uiControl.getScreen().getVarValue(varValue);
                  varOrParam = true;
               }
            } else {
               Object data = _wiclet.getData(_defID);
               if (data instanceof Component) {
                  if (_currentIndex == _lastFieldIndex) {
                     return;
                  }

                  if (handleGlobalVar(data, resultVector, mappingVector)) {
                     return;
                  }

                  globalVar = true;
               } else {
                  _handles[0] = (long)_defID << 32;
                  _dc = (DataCollection)data;
                  _hasDataArray = true;
               }
            }

            if (_currentIndex == _lastFieldIndex) {
               if (varOrParam) {
                  return;
               }

               if (!globalVar) {
                  if (!(_dc instanceof KeyDataCollection)) {
                     return;
                  }

                  _handles = ((KeyDataCollection)_dc).retrieveAll(true);
               }

               checkMappingHandles();
            } else {
               if (!resolveFields()) {
                  return;
               }

               if (_type != 32774 || _currentIndex == _arrayLength - 1) {
                  constructReturnVectors(resultVector, mappingVector);
                  return;
               }
            }

            if (_handles != null && _handles.length != 0) {
               if (_currentIndex < _arrayLength - 1) {
                  int findStringIndex = _inArray[++_currentIndex];
                  if (findStringIndex != -1) {
                     if (_type == 32774) {
                        retrieveHandlesFromDataArrayField();
                        if (_handles == null || _handles.length == 0) {
                           return;
                        }
                     }

                     Object obj = ((ScreenModelImpl)uiControl.getScreen()).getObjectData(findStringIndex);
                     if (obj instanceof Object) {
                        LongVector results = new LongVector();
                        DataCollection dc = _wiclet.getDataCollection((int)(_handles[0] >> 32));
                        _wiclet.findWhere(results, dc, _handles, (String)obj, new ValueResolverImpl(_wiclet, uiControl.getScreen()));
                        if (results.size() == 0) {
                           return;
                        }

                        _handles = results.toArray();
                        checkMappingHandles();
                     }
                  }

                  if (_currentIndex < _arrayLength - 1) {
                     if (findStringIndex == -1 && _type == 32774) {
                        retrieveHandlesFromDataArrayField();
                        if (_handles == null || _handles.length == 0) {
                           return;
                        }

                        checkMappingHandles();
                     }

                     _numFields = _inArray[++_currentIndex];
                     _lastFieldIndex = _currentIndex + _numFields;
                     if (_lastFieldIndex >= _arrayLength) {
                        return;
                     }

                     if (!resolveFields()) {
                        return;
                     }
                  }
               }

               if (_handles != null && _handles.length > 0) {
                  checkMappingHandles();
                  constructReturnVectors(resultVector, mappingVector);
               }
            }
         }
      }
   }

   private static final void retrieveHandlesFromDataArrayField() {
      _handles = ((LongVector)_dc.getObjectFieldValue(_handles[0], _field)).toArray();
      _defID = _dc.getDef().getFieldReferenceType(_field);
      _type = -1;
      _hasDataArray = true;
   }

   private static final void handleEnumerationInValue(Object resultVector, Object mappingVector) {
      if ((int)_uiControl.getValueType() == 32771 && resultVector instanceof Object) {
         EnumCollection enumCol = _wiclet.getEnums();
         if (enumCol != null) {
            String[] enumStrings = enumCol.getEnum(_inArray[2]);
            if (enumStrings != null) {
               int arraySize = enumStrings.length;
               Vector vec = (Vector)resultVector;

               for (int j = 0; j < arraySize; j++) {
                  appendResultString(vec, enumStrings[j]);
               }
            }
         }

         if (_mappingNeeded) {
            if (_mappingType == 3 && mappingVector instanceof Object) {
               Vector results = (Vector)resultVector;
               Vector mappings = (Vector)mappingVector;
               int size = results.size();

               for (int i = 0; i < size; i++) {
                  mappings.addElement(results.elementAt(i));
               }
            } else if ((_mappingType == 1 || _mappingType == 5) && mappingVector instanceof Object) {
               Vector results = (Vector)resultVector;
               IntVector mappings = (IntVector)mappingVector;
               int size = results.size();
               int enumDefId = _inArray[2];

               for (int i = 0; i < size; i++) {
                  mappings.addElement(enumCol.getEnumValueAsInt(enumDefId, (String)results.elementAt(i)));
               }
            }
         }
      }
   }

   private static final boolean handleGlobalVar(Object data, Object resultVector, Object mappingVector) {
      boolean returnResults = false;
      Component cmp = (Component)data;
      int field = _inArray[++_currentIndex];
      int type = cmp.getDef().getFieldType(field);
      if (!_isContainerControl) {
         _type = type;
         _field = field;
      }

      switch (type) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 8:
         case 32768:
         case 32769:
         case 32770:
         case 32771:
         case 32772:
         case 32773:
         case 32776:
            handleResultsFromGlobal(cmp, resultVector, mappingVector);
            return true;
         case 6:
            _handles[0] = cmp.getReferenceField(field);
            if (_handles[0] == -1) {
               return true;
            }

            _defID = (int)(_handles[0] >> 32);
            _dc = _wiclet.getDataCollection(_defID);
            return returnResults;
         case 32774:
            LongVector dataElements = (LongVector)cmp.getObjectFieldValue(field);
            if (dataElements == null || dataElements.size() == 0) {
               return true;
            } else {
               int numHandles = dataElements.size();

               for (int i = numHandles - 1; i >= 0; i--) {
                  if (dataElements.elementAt(i) != -1) {
                     _defID = (int)(dataElements.elementAt(i) >> 32);
                     _dc = _wiclet.getDataCollection(_defID);
                     break;
                  }
               }

               numHandles = dataElements.size();
               if (numHandles <= 0) {
                  return true;
               } else {
                  _handles = new long[numHandles];
                  dataElements.copyInto(_handles);
                  _hasDataArray = true;
                  if (!_isContainerControl) {
                     _type = 6;
                  }
               }
            }
         default:
            return returnResults;
      }
   }

   private static final void handleResultsFromGlobal(Component cmp, Object resultVector, Object mappingVector) {
      constructResultVectorFromGlobal(cmp, resultVector);
      constructMappingVectorFromGlobal(cmp, mappingVector);
   }

   private static final void constructMappingVectorFromGlobal(Component var0, Object var1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Could not find destination nodes for stat id {Block}:63 from source 94_condition
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.FlattenStatementsHelper.setEdges(FlattenStatementsHelper.java:563)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.FlattenStatementsHelper.buildDirectGraph(FlattenStatementsHelper.java:50)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.<init>(VarDefinitionHelper.java:151)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.<init>(VarDefinitionHelper.java:52)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarDefinitions(VarProcessor.java:52)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:458)
      //
      // Bytecode:
      // 000: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._mappingNeeded Z
      // 003: ifne 009
      // 006: goto 2f6
      // 009: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._mappingType I
      // 00c: bipush 3
      // 00e: if_icmpeq 01f
      // 011: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._mappingType I
      // 014: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._type I
      // 017: sipush 32767
      // 01a: iand
      // 01b: if_icmpeq 01f
      // 01e: return
      // 01f: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._mappingType I
      // 022: bipush 3
      // 024: if_icmpne 02b
      // 027: bipush 1
      // 028: goto 02c
      // 02b: bipush 0
      // 02c: istore 2
      // 02d: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._type I
      // 030: lookupswitch 710 14 0 206 1 124 2 165 3 288 4 247 5 124 8 247 32768 392 32769 305 32770 626 32771 479 32772 542 32773 305 32776 542
      // 0ac: iload 2
      // 0ad: ifeq 0c4
      // 0b0: aload 1
      // 0b1: checkcast java/lang/Object
      // 0b4: aload 0
      // 0b5: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 0b8: invokeinterface net/rim/wica/runtime/metadata/component/Component.getIntFieldValue (I)I 2
      // 0bd: invokestatic java/lang/String.valueOf (I)Ljava/lang/String;
      // 0c0: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 0c3: return
      // 0c4: aload 1
      // 0c5: checkcast java/lang/Object
      // 0c8: aload 0
      // 0c9: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 0cc: invokeinterface net/rim/wica/runtime/metadata/component/Component.getIntFieldValue (I)I 2
      // 0d1: invokevirtual net/rim/device/api/util/IntVector.addElement (I)V
      // 0d4: return
      // 0d5: iload 2
      // 0d6: ifeq 0ed
      // 0d9: aload 1
      // 0da: checkcast java/lang/Object
      // 0dd: aload 0
      // 0de: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 0e1: invokeinterface net/rim/wica/runtime/metadata/component/Component.getDoubleFieldValue (I)D 2
      // 0e6: invokestatic java/lang/String.valueOf (D)Ljava/lang/String;
      // 0e9: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 0ec: return
      // 0ed: aload 1
      // 0ee: checkcast net/rim/wica/runtime/util/DoubleVector
      // 0f1: aload 0
      // 0f2: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 0f5: invokeinterface net/rim/wica/runtime/metadata/component/Component.getDoubleFieldValue (I)D 2
      // 0fa: invokevirtual net/rim/wica/runtime/util/DoubleVector.addElement (D)V
      // 0fd: return
      // 0fe: iload 2
      // 0ff: ifeq 116
      // 102: aload 1
      // 103: checkcast java/lang/Object
      // 106: aload 0
      // 107: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 10a: invokeinterface net/rim/wica/runtime/metadata/component/Component.getIntFieldValue (I)I 2
      // 10f: invokestatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver.booleanToString (I)Ljava/lang/String;
      // 112: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 115: return
      // 116: aload 1
      // 117: checkcast java/lang/Object
      // 11a: aload 0
      // 11b: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 11e: invokeinterface net/rim/wica/runtime/metadata/component/Component.getIntFieldValue (I)I 2
      // 123: invokevirtual net/rim/device/api/util/IntVector.addElement (I)V
      // 126: return
      // 127: iload 2
      // 128: ifeq 13f
      // 12b: aload 1
      // 12c: checkcast java/lang/Object
      // 12f: aload 0
      // 130: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 133: invokeinterface net/rim/wica/runtime/metadata/component/Component.getLongFieldValue (I)J 2
      // 138: invokestatic java/lang/String.valueOf (J)Ljava/lang/String;
      // 13b: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 13e: return
      // 13f: aload 1
      // 140: checkcast net/rim/wica/runtime/util/LongVector
      // 143: aload 0
      // 144: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 147: invokeinterface net/rim/wica/runtime/metadata/component/Component.getLongFieldValue (I)J 2
      // 14c: invokevirtual net/rim/wica/runtime/util/LongVector.addElement (J)V
      // 14f: return
      // 150: aload 1
      // 151: checkcast java/lang/Object
      // 154: aload 0
      // 155: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 158: invokeinterface net/rim/wica/runtime/metadata/component/Component.getObjectFieldValue (I)Ljava/lang/Object; 2
      // 15d: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 160: return
      // 161: aload 0
      // 162: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 165: invokeinterface net/rim/wica/runtime/metadata/component/Component.getObjectFieldValue (I)Ljava/lang/Object; 2
      // 16a: astore 3
      // 16b: aload 3
      // 16c: dup
      // 16d: instanceof java/lang/Object
      // 170: ifne 177
      // 173: pop
      // 174: goto 2f6
      // 177: checkcast java/lang/Object
      // 17a: astore 4
      // 17c: bipush 0
      // 17d: istore 5
      // 17f: iload 5
      // 181: aload 4
      // 183: invokevirtual net/rim/device/api/util/IntVector.size ()I
      // 186: if_icmplt 18c
      // 189: goto 2f6
      // 18c: iload 2
      // 18d: ifeq 1a4
      // 190: aload 1
      // 191: checkcast java/lang/Object
      // 194: aload 4
      // 196: iload 5
      // 198: invokevirtual net/rim/device/api/util/IntVector.elementAt (I)I
      // 19b: invokestatic java/lang/String.valueOf (I)Ljava/lang/String;
      // 19e: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 1a1: goto 1b2
      // 1a4: aload 1
      // 1a5: checkcast java/lang/Object
      // 1a8: aload 4
      // 1aa: iload 5
      // 1ac: invokevirtual net/rim/device/api/util/IntVector.elementAt (I)I
      // 1af: invokevirtual net/rim/device/api/util/IntVector.addElement (I)V
      // 1b2: iinc 5 1
      // 1b5: goto 17f
      // 1b8: aload 0
      // 1b9: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 1bc: invokeinterface net/rim/wica/runtime/metadata/component/Component.getObjectFieldValue (I)Ljava/lang/Object; 2
      // 1c1: astore 3
      // 1c2: aload 3
      // 1c3: dup
      // 1c4: instanceof java/lang/Object
      // 1c7: ifne 1ce
      // 1ca: pop
      // 1cb: goto 2f6
      // 1ce: checkcast java/lang/Object
      // 1d1: astore 4
      // 1d3: bipush 0
      // 1d4: istore 5
      // 1d6: iload 5
      // 1d8: aload 4
      // 1da: invokevirtual net/rim/device/api/util/IntVector.size ()I
      // 1dd: if_icmplt 1e3
      // 1e0: goto 2f6
      // 1e3: iload 2
      // 1e4: ifeq 1fb
      // 1e7: aload 1
      // 1e8: checkcast java/lang/Object
      // 1eb: aload 4
      // 1ed: iload 5
      // 1ef: invokevirtual net/rim/device/api/util/IntVector.elementAt (I)I
      // 1f2: invokestatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver.booleanToString (I)Ljava/lang/String;
      // 1f5: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 1f8: goto 209
      // 1fb: aload 1
      // 1fc: checkcast java/lang/Object
      // 1ff: aload 4
      // 201: iload 5
      // 203: invokevirtual net/rim/device/api/util/IntVector.elementAt (I)I
      // 206: invokevirtual net/rim/device/api/util/IntVector.addElement (I)V
      // 209: iinc 5 1
      // 20c: goto 1d6
      // 20f: aload 0
      // 210: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 213: invokeinterface net/rim/wica/runtime/metadata/component/Component.getObjectFieldValue (I)Ljava/lang/Object; 2
      // 218: astore 3
      // 219: aload 3
      // 21a: dup
      // 21b: instanceof java/lang/Object
      // 21e: ifne 225
      // 221: pop
      // 222: goto 2f6
      // 225: checkcast java/lang/Object
      // 228: astore 4
      // 22a: bipush 0
      // 22b: istore 5
      // 22d: iload 5
      // 22f: aload 4
      // 231: invokevirtual java/util/Vector.size ()I
      // 234: if_icmplt 23a
      // 237: goto 2f6
      // 23a: aload 1
      // 23b: checkcast java/lang/Object
      // 23e: aload 4
      // 240: iload 5
      // 242: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 245: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 248: iinc 5 1
      // 24b: goto 22d
      // 24e: aload 0
      // 24f: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 252: invokeinterface net/rim/wica/runtime/metadata/component/Component.getObjectFieldValue (I)Ljava/lang/Object; 2
      // 257: astore 3
      // 258: aload 3
      // 259: dup
      // 25a: instanceof net/rim/wica/runtime/util/LongVector
      // 25d: ifne 264
      // 260: pop
      // 261: goto 2f6
      // 264: checkcast net/rim/wica/runtime/util/LongVector
      // 267: astore 4
      // 269: bipush 0
      // 26a: istore 5
      // 26c: iload 5
      // 26e: aload 4
      // 270: invokevirtual net/rim/wica/runtime/util/LongVector.size ()I
      // 273: if_icmpge 2f6
      // 276: iload 2
      // 277: ifeq 28e
      // 27a: aload 1
      // 27b: checkcast java/lang/Object
      // 27e: aload 4
      // 280: iload 5
      // 282: invokevirtual net/rim/wica/runtime/util/LongVector.elementAt (I)J
      // 285: invokestatic java/lang/String.valueOf (J)Ljava/lang/String;
      // 288: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 28b: goto 29c
      // 28e: aload 1
      // 28f: checkcast net/rim/wica/runtime/util/LongVector
      // 292: aload 4
      // 294: iload 5
      // 296: invokevirtual net/rim/wica/runtime/util/LongVector.elementAt (I)J
      // 299: invokevirtual net/rim/wica/runtime/util/LongVector.addElement (J)V
      // 29c: iinc 5 1
      // 29f: goto 26c
      // 2a2: aload 0
      // 2a3: getstatic net/rim/wica/runtime/metadata/internal/util/ArrayInValueResolver._field I
      // 2a6: invokeinterface net/rim/wica/runtime/metadata/component/Component.getObjectFieldValue (I)Ljava/lang/Object; 2
      // 2ab: astore 3
      // 2ac: aload 3
      // 2ad: dup
      // 2ae: instanceof net/rim/wica/runtime/util/DoubleVector
      // 2b1: ifne 2b8
      // 2b4: pop
      // 2b5: goto 2f6
      // 2b8: checkcast net/rim/wica/runtime/util/DoubleVector
      // 2bb: astore 4
      // 2bd: bipush 0
      // 2be: istore 5
      // 2c0: iload 5
      // 2c2: aload 4
      // 2c4: invokevirtual net/rim/wica/runtime/util/DoubleVector.size ()I
      // 2c7: if_icmpge 2f6
      // 2ca: iload 2
      // 2cb: ifeq 2e2
      // 2ce: aload 1
      // 2cf: checkcast java/lang/Object
      // 2d2: aload 4
      // 2d4: iload 5
      // 2d6: invokevirtual net/rim/wica/runtime/util/DoubleVector.elementAt (I)D
      // 2d9: invokestatic java/lang/String.valueOf (D)Ljava/lang/String;
      // 2dc: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 2df: goto 2f0
      // 2e2: aload 1
      // 2e3: checkcast net/rim/wica/runtime/util/DoubleVector
      // 2e6: aload 4
      // 2e8: iload 5
      // 2ea: invokevirtual net/rim/wica/runtime/util/DoubleVector.elementAt (I)D
      // 2ed: invokevirtual net/rim/wica/runtime/util/DoubleVector.addElement (D)V
      // 2f0: iinc 5 1
      // 2f3: goto 2c0
      // 2f6: return
   }

   private static final void constructResultVectorFromGlobal(Component cmp, Object resultVector) {
      switch ((int)_uiControl.getValueType()) {
         case 32773:
            constructStringVectorFromGlobal(cmp, resultVector);
            break;
         case 32774:
         default:
            LongVector result = (LongVector)resultVector;
            switch (_type) {
               case 4:
               case 6:
                  if (_type == 6) {
                     result.addElement(cmp.getReferenceField(_field));
                     return;
                  }

                  result.addElement(cmp.getLongFieldValue(_field));
                  return;
               case 32772:
               case 32774:
                  Object o = cmp.getObjectFieldValue(_field);
                  if (o instanceof LongVector) {
                     int size = ((LongVector)o).size();
                     result.setSize(size);
                     System.arraycopy(((LongVector)o).getArray(), 0, result.getArray(), 0, size);
                     return;
                  }
            }
      }
   }

   private static final void constructStringVectorFromGlobal(Component cmp, Object resultVector) {
      if (resultVector instanceof Object) {
         Vector result = (Vector)resultVector;
         switch (_type) {
            case 0:
               appendResultString(result, booleanToString(cmp.getIntFieldValue(_field)));
               return;
            case 1:
               appendResultString(result, String.valueOf(cmp.getIntFieldValue(_field)));
               return;
            case 2:
               appendResultString(result, String.valueOf(cmp.getDoubleFieldValue(_field)));
               return;
            case 3:
               appendResultString(result, (String)cmp.getObjectFieldValue(_field));
               return;
            case 4:
               appendResultString(result, getDateFormattedString(cmp.getLongFieldValue(_field)));
               return;
            case 5:
               int enumValue = cmp.getIntFieldValue(_field);
               if (enumValue >= 0) {
                  EnumCollection enumCol = _wiclet.getEnums();
                  String[] enumStrings = enumCol.getEnum(_inArray[1]);
                  if (enumStrings != null && enumValue < enumStrings.length) {
                     appendResultString(result, enumStrings[enumValue]);
                     return;
                  }
               }
               break;
            case 6:
            case 8:
               appendResultString(result, String.valueOf(cmp.getLongFieldValue(_field)));
               return;
            case 32768:
               Object oxx = cmp.getObjectFieldValue(_field);
               if (oxx instanceof Object) {
                  IntVector vec = (IntVector)oxx;

                  for (int j = 0; j < vec.size(); j++) {
                     appendResultString(result, booleanToString(vec.elementAt(j)));
                  }
               }
               break;
            case 32769:
               Object oxxxx = cmp.getObjectFieldValue(_field);
               if (oxxxx instanceof Object) {
                  IntVector vec = (IntVector)oxxxx;

                  for (int j = 0; j < vec.size(); j++) {
                     appendResultString(result, String.valueOf(vec.elementAt(j)));
                  }
               }
               break;
            case 32770:
               Object o = cmp.getObjectFieldValue(_field);
               if (o instanceof DoubleVector) {
                  DoubleVector vec = (DoubleVector)o;

                  for (int j = 0; j < vec.size(); j++) {
                     appendResultString(result, String.valueOf(vec.elementAt(j)));
                  }
               }
               break;
            case 32771:
               Vector results = (Vector)cmp.getObjectFieldValue(_field);
               if (results != null) {
                  int size = results.size();
                  if (size > 0) {
                     for (int j = 0; j < size; j++) {
                        appendResultString(result, (String)results.elementAt(j));
                     }
                  }
               }
               break;
            case 32772:
            case 32774:
            case 32776:
               Object ox = cmp.getObjectFieldValue(_field);
               if (ox instanceof LongVector) {
                  LongVector vec = (LongVector)ox;

                  for (int j = 0; j < vec.size(); j++) {
                     if (_type == 32772) {
                        appendResultString(result, getDateFormattedString(vec.elementAt(j)));
                     } else {
                        appendResultString(result, String.valueOf(vec.elementAt(j)));
                     }
                  }
               }
               break;
            case 32773:
               Object oxxx = cmp.getObjectFieldValue(_field);
               if (oxxx instanceof Object) {
                  IntVector vec = (IntVector)oxxx;
                  EnumCollection enumCol = _wiclet.getEnums();
                  String[] enumStrings = enumCol.getEnum(_inArray[1]);
                  if (enumStrings != null) {
                     for (int j = 0; j < vec.size(); j++) {
                        int enumValuex = vec.elementAt(j);
                        if (enumValuex >= 0 && enumValuex < enumStrings.length) {
                           appendResultString(result, enumStrings[enumValuex]);
                        }
                     }
                  }
               }
         }
      }
   }

   private static final String getDateFormattedString(long l) {
      String result = null;
      if (_uiControl instanceof DateFormatter) {
         result = ((DateFormatter)_uiControl).getFormattedDate(l);
      }

      if (result == null) {
         result = Util.DEFAULT_DATE_FORMATTER.format(new Object(l));
      }

      return result;
   }

   private static final void processPrePostStrings() {
      if (_preResultObjects != null) {
         _preString = (StringBuffer)(new Object());
         processPrePostHelper(_preResultObjects, _preString);
      }

      if (_postResultObjects != null) {
         _postString = (StringBuffer)(new Object());
         processPrePostHelper(_postResultObjects, _postString);
      }
   }

   private static final void processPrePostHelper(Vector resultObjects, StringBuffer result) {
      Object obj = null;
      Enumeration e = resultObjects.elements();

      while (e.hasMoreElements()) {
         obj = e.nextElement();
         if (obj instanceof Object) {
            result.append((String)obj);
         }
      }
   }

   private static final void appendResultString(Vector result, String resultString) {
      StringBuffer finalString = (StringBuffer)(new Object());
      if (_preString != null && _preString.length() > 0) {
         finalString.append(_preString);
      }

      if (resultString != null && resultString.length() > 0) {
         finalString.append(resultString);
      }

      if (_postString != null && _postString.length() > 0) {
         finalString.append(_postString);
      }

      result.addElement(finalString.toString());
   }

   private static final void checkMappingHandles() {
      if (_mappingNeeded && _mappingDef != 0 && _mappingDef == _defID) {
         int size = _handles.length;
         _mappingHandles.setSize(size);
         System.arraycopy(_handles, 0, _mappingHandles.getArray(), 0, size);
      }
   }

   private static final boolean resolveFields() {
      boolean foundValidHandle = false;
      int numHandles = _handles.length;
      _currentIndex++;

      while (_currentIndex < _lastFieldIndex) {
         foundValidHandle = false;

         for (int i = numHandles - 1; i >= 0; i--) {
            if (_handles[i] != -1) {
               if (!foundValidHandle) {
                  _defID = (int)(_handles[i] >> 32);
                  _dc = _wiclet.getDataCollection(_defID);
                  _field = _inArray[_currentIndex];
                  _type = _dc.getDef().getFieldType(_field);
                  if (_type != 6) {
                     return false;
                  }

                  _defID = _dc.getDef().getFieldReferenceType(_field);
                  foundValidHandle = true;
               }

               _handles[i] = _dc.getReferenceField(_handles[i], _field);
            }
         }

         checkMappingHandles();
         _currentIndex++;
      }

      for (int i = numHandles - 1; i >= 0; i--) {
         if (_handles[i] != -1) {
            _defID = (int)(_handles[i] >> 32);
            _dc = _wiclet.getDataCollection(_defID);
            _field = _inArray[_currentIndex];
            _type = _dc.getDef().getFieldType(_field);
            return true;
         }
      }

      return true;
   }

   private static final void constructReturnVectors(Object resultVector, Object mappingVector) {
      constructResultVector(resultVector);
      constructMappingVector(mappingVector);
   }

   private static final void constructMappingVector(Object mappingVector) {
      if (_mappingNeeded && _mappingDef == 0) {
         if (_mappingType == 3) {
            mappingVector = constructStringVectorFromHandles(mappingVector);
            return;
         }

         if (_mappingType == (_type & 32767)) {
            constructVectorFromHandles(mappingVector);
         }
      }
   }

   private static final void constructVectorFromHandles(Object vector) {
      int numHandles = _handles.length;

      for (int i = 0; i < numHandles; i++) {
         if (_handles[i] != -1 && _dc.contains(_handles[i])) {
            switch (_type) {
               case 0:
               case 1:
               case 5:
                  ((IntVector)vector).addElement(_dc.getIntFieldValue(_handles[i], _field));
                  break;
               case 2:
                  ((DoubleVector)vector).addElement(_dc.getDoubleFieldValue(_handles[i], _field));
                  break;
               case 4:
               case 8:
                  ((LongVector)vector).addElement(_dc.getLongFieldValue(_handles[i], _field));
                  break;
               case 32768:
               case 32769:
               case 32773:
                  Object oxx = _dc.getObjectFieldValue(_handles[i], _field);
                  if (oxx instanceof Object) {
                     int size = ((IntVector)oxx).size();
                     ((IntVector)vector).setSize(size);
                     System.arraycopy(((IntVector)oxx).getArray(), 0, ((IntVector)vector).getArray(), 0, size);
                  }
                  break;
               case 32770:
                  Object ox = _dc.getObjectFieldValue(_handles[i], _field);
                  if (ox instanceof DoubleVector) {
                     int size = ((DoubleVector)ox).size();
                     ((DoubleVector)vector).setSize(size);
                     System.arraycopy(((DoubleVector)ox).getArray(), 0, ((DoubleVector)vector).getArray(), 0, size);
                  }
                  break;
               case 32772:
               case 32776:
                  Object o = _dc.getObjectFieldValue(_handles[i], _field);
                  if (o instanceof LongVector) {
                     int size = ((LongVector)o).size();
                     ((LongVector)vector).setSize(size);
                     System.arraycopy(((LongVector)o).getArray(), 0, ((LongVector)vector).getArray(), 0, size);
                  }
            }
         }
      }
   }

   private static final Object constructStringVectorFromHandles(Object vector) {
      if (!(vector instanceof Object)) {
         return vector;
      }

      Vector result = (Vector)vector;
      int numHandles = _handles.length;

      for (int i = 0; i < numHandles; i++) {
         if (_handles[i] != -1 && _dc.contains(_handles[i])) {
            switch (_type) {
               case 0:
                  appendResultString(result, booleanToString(_dc.getBooleanFieldValue(_handles[i], _field) ? 1 : 0));
                  break;
               case 1:
                  appendResultString(result, String.valueOf(_dc.getIntFieldValue(_handles[i], _field)));
                  break;
               case 2:
                  appendResultString(result, String.valueOf(_dc.getDoubleFieldValue(_handles[i], _field)));
                  break;
               case 3:
                  appendResultString(result, (String)_dc.getObjectFieldValue(_handles[i], _field));
                  break;
               case 4:
                  appendResultString(result, getDateFormattedString(_dc.getLongFieldValue(_handles[i], _field)));
                  break;
               case 5:
                  int enumValue = _dc.getIntFieldValue(_handles[i], _field);
                  if (enumValue >= 0) {
                     EnumCollection enumCol = _wiclet.getEnums();
                     String[] enumStrings = enumCol.getEnum(_inArray[1]);
                     if (enumStrings != null && enumValue < enumStrings.length) {
                        appendResultString(result, enumStrings[enumValue]);
                     }
                  }
                  break;
               case 6:
               case 8:
                  appendResultString(result, String.valueOf(_dc.getLongFieldValue(_handles[i], _field)));
                  break;
               case 32768:
                  Object oxxxx = _dc.getObjectFieldValue(_handles[i], _field);
                  if (oxxxx instanceof Object) {
                     IntVector vec = (IntVector)oxxxx;

                     for (int j = 0; j < vec.size(); j++) {
                        appendResultString(result, booleanToString(vec.elementAt(j)));
                     }
                  }
                  break;
               case 32769:
                  Object oxxx = _dc.getObjectFieldValue(_handles[i], _field);
                  if (oxxx instanceof Object) {
                     IntVector vec = (IntVector)oxxx;

                     for (int j = 0; j < vec.size(); j++) {
                        appendResultString(result, String.valueOf(vec.elementAt(j)));
                     }
                  }
                  break;
               case 32770:
                  Object oxx = _dc.getObjectFieldValue(_handles[i], _field);
                  if (oxx instanceof DoubleVector) {
                     DoubleVector vec = (DoubleVector)oxx;

                     for (int j = 0; j < vec.size(); j++) {
                        appendResultString(result, String.valueOf(vec.elementAt(j)));
                     }
                  }
                  break;
               case 32771:
                  Vector vec = (Vector)_dc.getObjectFieldValue(_handles[i], _field);
                  if (vec == null) {
                     break;
                  }

                  int size = vec.size();
                  if (size > 0) {
                     for (int j = 0; j < size; j++) {
                        appendResultString(result, (String)vec.elementAt(j));
                     }
                  }
                  break;
               case 32772:
               case 32774:
               case 32776:
                  Object ox = _dc.getObjectFieldValue(_handles[i], _field);
                  if (ox instanceof LongVector) {
                     LongVector vec = (LongVector)ox;

                     for (int j = 0; j < vec.size(); j++) {
                        if (_type == 32772) {
                           appendResultString(result, getDateFormattedString(vec.elementAt(j)));
                        } else {
                           appendResultString(result, String.valueOf(vec.elementAt(j)));
                        }
                     }
                  }
                  break;
               case 32773:
                  Object o = _dc.getObjectFieldValue(_handles[i], _field);
                  if (o instanceof Object) {
                     IntVector vec = (IntVector)o;
                     EnumCollection enumCol = _wiclet.getEnums();
                     String[] enumStrings = enumCol.getEnum(_inArray[1]);
                     if (enumStrings != null) {
                        for (int j = 0; j < vec.size(); j++) {
                           int enumValuex = vec.elementAt(j);
                           if (enumValuex >= 0 && enumValuex < enumStrings.length) {
                              appendResultString(result, enumStrings[enumValuex]);
                           }
                        }
                     }
                  }
            }
         } else {
            appendResultString(result, "");
         }
      }

      return result;
   }

   private static final void constructResultVector(Object resultVector) {
      int numHandles = _handles.length;
      if (_isContainerControl || _type == -1 || (_type & 32768) != 32768 || numHandles <= 1) {
         switch ((int)_uiControl.getValueType()) {
            case 32767:
            case 32769:
            case 32770:
            case 32771:
            case 32773:
               resultVector = constructStringVectorFromHandles(resultVector);
               break;
            case 32768:
            default:
               Vector result = (Vector)resultVector;
               if (_type != 0) {
                  return;
               }

               for (int i = 0; i < numHandles; i++) {
                  if (_handles[i] == -1) {
                     if (_childOfContainer) {
                        result.addElement(new Object(false));
                     }
                  } else {
                     result.addElement(new Object(_dc.getBooleanFieldValue(_handles[i], _field)));
                  }
               }
               break;
            case 32772:
            case 32774:
               LongVector result = (LongVector)resultVector;

               for (int i = 0; i < numHandles; i++) {
                  if (_handles[i] == -1) {
                     result.addElement(-1);
                  } else {
                     switch (_type) {
                        case -1:
                           if (_handles[i] != -1) {
                              result.addElement(_handles[i]);
                           }
                           break;
                        case 4:
                        case 6:
                           if (_type == 6) {
                              result.addElement(_dc.getReferenceField(_handles[i], _field));
                           } else {
                              result.addElement(_dc.getLongFieldValue(_handles[i], _field));
                           }
                           break;
                        case 32772:
                           Object o = _dc.getObjectFieldValue(_handles[i], _field);
                           if (o instanceof LongVector) {
                              int size = ((LongVector)o).size();
                              result.setSize(size);
                              System.arraycopy(((LongVector)o).getArray(), 0, result.getArray(), 0, size);
                           }
                           break;
                        case 32774:
                           Object ox = _dc.getObjectFieldValue(_handles[i], _field);
                           if (ox instanceof LongVector) {
                              LongVector dataElements = (LongVector)ox;
                              int size = dataElements.size();
                              int j = size - 1;

                              for (; j >= 0; j--) {
                                 if (dataElements.elementAt(j) == -1) {
                                    dataElements.removeElementAt(j);
                                 }
                              }

                              size = dataElements.size();
                              if (size <= 0) {
                                 return;
                              }

                              result.setSize(size);
                              System.arraycopy(dataElements.getArray(), 0, result.getArray(), 0, size);
                           }
                     }
                  }
               }
         }
      }
   }

   private static final String booleanToString(int value) {
      return value == 1 ? "True" : "False";
   }
}
