package net.rim.device.apps.internal.phone.model;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveFieldCookie;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardElement;
import net.rim.device.apps.api.addressbook.CustomContactImageProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.SyncFieldIDProvider;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.WrapperVerb;
import net.rim.device.apps.api.messaging.implus.IMPlusServiceModel;
import net.rim.device.apps.api.phone.PhoneNumberTypes;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.quickcontact.QuickContactUtil;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.ui.ToggleableField;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.phone.pattern.PhoneNumberStringPattern;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.PhoneNumberEditField;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class PhoneNumberModel
   extends AbstractPhoneNumberModel
   implements PersistableRIMModel,
   ActiveFieldCookie,
   FieldProvider,
   KeyProvider,
   VerbProvider,
   VerbDescriptionProvider,
   PaintProvider,
   MatchProvider,
   ConversionProvider,
   Copyable,
   EncryptableProvider,
   SyncFieldIDProvider {
   int _type;
   private Object _phoneNumberEncoding;
   private int _hashCode;
   private static final boolean ALLOW_PHONE_TYPE_CHANGES;
   private static String CTI_PREFIX = "cti:";
   private static final int MESSAGE_ADDRESS_DATA_SIZE;
   private static WeakReference _convertBufferWR = (WeakReference)(new Object(null));

   protected void getTransmittablePhoneNumber(StringBuffer buffer, Object context) {
      PhoneNumberConverter.convertForTransmission(buffer, this.toString().toCharArray(), context);
   }

   protected Verb getDefaultComposeVerb(Object context, Object addressCard) {
      Verb[] verbs = this.getPhoneNumberComposeVerbs();
      if (verbs != null) {
         int count = verbs.length;
         if (count > 0) {
            int maxComposeVerbPriority = -1;
            Verb defaultVerb = null;

            for (int idx = 0; idx < count; idx++) {
               Verb composeVerb = verbs[idx];
               if (this.validateVerb(composeVerb, context)) {
                  int priority = composeVerb.getOrdering();
                  if (priority > maxComposeVerbPriority) {
                     maxComposeVerbPriority = priority;
                     defaultVerb = composeVerb;
                  }
               }
            }

            return this.copyVerbAndSetParameters(defaultVerb, context);
         }
      }

      return null;
   }

   public void addVerbsForEnabledPhone(boolean editable, boolean hasFocus, boolean hyperlink, Object context, Verb[] verbs) {
      if (PackageManager.isPhoneEnabled()) {
         if (editable && hasFocus && !hyperlink && !ContextObject.getPrivateFlag(context, 4936088360624690805L, 63)) {
            Object field = ContextObject.get(context, 9045827404276417370L);
            if (field instanceof Object) {
               EditField editField = (EditField)field;
               if (editField != null) {
                  String currentValue = editField.getText();
                  if (currentValue != null && currentValue.length() > 0) {
                     Array.resize(verbs, verbs.length + 2);
                     verbs[verbs.length - 2] = new AddDelayVerb(editField, 0);
                     verbs[verbs.length - 1] = new AddDelayVerb(editField, 1);
                  }
               }
            }
         }
      }
   }

   @Override
   public int getOrder(Object context) {
      if (ContextObject.getFlag(context, 11)) {
         return ContextObject.getFlag(context, 0) ? 2800 + this._type : 3500 + this._type;
      } else {
         return ContextObject.getFlag(context, 24) ? 15400 : 0;
      }
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      if (!(field instanceof PhoneNumberModelEditField)) {
         if (!(field instanceof Object)) {
            return false;
         }

         PhoneNumberEditField editField = (PhoneNumberEditField)field;
         this.setValue(editField.getText());
         return this.getValue().length() > 0;
      } else {
         PhoneNumberModelEditField editField = (PhoneNumberModelEditField)field;
         this.setValue(editField.getText());
         this._type = editField.getPhoneNumberType();
         return this.getValue().length() > 0;
      }
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      if (keyRequested == -4145532165335996154L) {
         if (keyArray.length <= index) {
            Array.resize(keyArray, index + 1);
         }

         keyArray[index] = this.getReverseLookupCodeString();
         return 1;
      } else {
         return 0;
      }
   }

   @Override
   public int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      if (keyRequested == -4145532165335996154L) {
         if (keyArray.length <= index) {
            Array.resize(keyArray, index + 1);
         }

         int code = this.hashCode();
         if (code == 0) {
            return 0;
         }

         keyArray[index] = code;
         return 1;
      } else {
         return 0;
      }
   }

   @Override
   public int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public Field getField(Object context) {
      String typeString = AbstractPhoneNumberModel.getTypeString(this._type);
      boolean editable = ContextObject.getFlag(context, 0);
      boolean noLabel = ContextObject.getFlag(context, 1);
      if (!noLabel && PackageManager.isPhoneEnabled()) {
         noLabel = ContextObject.getFlag(context, 58) && this._type == 0;
      }

      if (ContextObject.getFlag(context, 84)) {
         return this.checkIfIconRequired(this.getQualifiedAddressField(context));
      }

      String label;
      if (noLabel) {
         label = "";
      } else {
         Object contextFieldLabel = ContextObject.get(context, 3986845832244503196L);
         if (!(contextFieldLabel instanceof Object)) {
            label = ((StringBuffer)(new Object())).append(typeString).append(": ").toString();
         } else {
            label = (String)contextFieldLabel;
         }
      }

      if (editable) {
         Field editField = null;
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         Factory f = (Factory)ar.get(-5309169568015258230L);
         if (f != null) {
            ContextObject contextCopy = ContextObject.clone(context);
            contextCopy.put(3986845832244503196L, label);
            contextCopy.put(247, this.getValue());
            editField = (Field)f.createInstance(contextCopy);
         }

         if (editField == null) {
            editField = this.getPhoneNumberEditField(label, this.getValue(), context);
         }

         editField.setCookie(this);
         return editField;
      } else {
         if (ContextObject.getFlag(context, 24)) {
            StringBuffer strBuffer = (StringBuffer)(new Object());
            this.getDisplayablePhoneNumber(strBuffer, false);
            label = ((StringBuffer)(new Object())).append(label).append(strBuffer.toString()).toString();
            long flags = 18014398509481984L;
            return this.checkIfIconRequired((Field)(new Object(label, flags)));
         }

         if (!ContextObject.getFlag(context, 58)) {
            if (ContextObject.getFlag(context, 128)) {
               Field leftField = (Field)(new Object(label));
               Field rightField = this.checkIfIconRequired(this.getQualifiedAddressField(context));
               Field manager = (Field)(new Object(leftField, rightField));
               rightField.setCookie(this);
               manager.setCookie(this);
               return manager;
            }

            String value = this.getValue();
            if (value != null && value.length() != 0) {
               context = ContextObject.castOrCreate(context);
               if (ContextObject.get(context, 252) == null) {
                  Object addresscard = this.getAddressBookEntry();
                  if (addresscard == null) {
                     ContextObject.setFlag(context, 82);
                  } else {
                     ContextObject.put(context, 252, addresscard);
                  }
               }

               Field fieldToReturn = this.checkIfIconRequired(new ReadOnlyPhoneNumberField(this, context));
               IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
               if (implusService != null && implusService.isIMPlusCompose(context) && ContextObject.get(context, 252) == null) {
                  StringBuffer postfixBuffer = (StringBuffer)(new Object());
                  postfixBuffer.append(' ');
                  postfixBuffer.append('(');
                  if (this._type == 7) {
                     postfixBuffer.append((String)((Object[])PhoneResources.getObject(601))[7]);
                  } else {
                     postfixBuffer.append((String)((Object[])PhoneResources.getObject(601))[0]);
                  }

                  postfixBuffer.append(')');
                  FlowFieldManager ffm = (FlowFieldManager)(new Object(fieldToReturn.getStyle()));
                  LabelField postfixLabel = (LabelField)(new Object(postfixBuffer.toString(), 36028797018963968L));
                  ffm.add(fieldToReturn);
                  ffm.setCookie(fieldToReturn.getCookie());
                  ffm.add(postfixLabel);
                  fieldToReturn = ffm;
               }

               return fieldToReturn;
            } else {
               return null;
            }
         } else {
            String num = this.getValue();
            if (num == null) {
               String displayNumber = (String)ContextObject.get(context, -6346895525857192403L);
               if (displayNumber != null && displayNumber != null && displayNumber.length() > 0) {
                  num = displayNumber;
               }
            }

            if (num == null) {
               return null;
            }

            int index = PhoneNumberConverter.findExtensionOffset(num.toString().toCharArray());
            if (index == -1) {
               label = ((StringBuffer)(new Object())).append(label).append(num).toString();
            } else {
               String labelPhoneNumber = num.substring(0, index);
               labelPhoneNumber = labelPhoneNumber.trim();
               if (labelPhoneNumber.length() > 0) {
                  label = ((StringBuffer)(new Object())).append(label).append(labelPhoneNumber).toString();
               } else {
                  StringBuffer strBuffer = (StringBuffer)(new Object());
                  this.getDisplayablePhoneNumber(strBuffer, context);
                  label = ((StringBuffer)(new Object())).append(label).append(strBuffer.toString()).toString();
               }
            }

            LabelField labelField = null;
            Object var28;
            if (ContextObject.getFlag(context, 34) && this._type != 0) {
               StringBuffer buf = (StringBuffer)(new Object());
               buf.append(typeString);
               buf.append(' ');
               buf.append(label);
               var28 = new Object(buf, 64);
            } else {
               var28 = new Object(label, 64);
            }

            return this.checkIfIconRequired((Field)var28);
         }
      }
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      if (!ContextObject.getFlag(context, 119) || !ContextObject.getFlag(context, 2)) {
         boolean editable = ContextObject.getFlag(context, 0);
         String value = this.getValue();
         if (editable || value != null && value.length() != 0) {
            Object addressCard = ContextObject.get(context, 252);
            if (ContextObject.getFlag(context, 36)) {
               ContextObject.put(context, 247, this);
               Verb composeVerb = this.getDefaultComposeVerb(context, addressCard);
               ContextObject.remove(context, 247);
               if (composeVerb != null) {
                  if (verbs != null) {
                     Array.resize(verbs, 1);
                     verbs[0] = composeVerb;
                  }

                  return composeVerb;
               } else {
                  return null;
               }
            } else {
               Verb defaultVerb = null;
               if (addressCard == null && !editable) {
                  addressCard = this.getAddressBookEntry(context);
               }

               boolean composeOnlyContext = ContextObject.getFlag(context, 44);
               boolean picking = ContextObject.getFlag(context, 5);
               boolean hasFocus = ContextObject.getFlag(context, 2);
               boolean hyperlink = ContextObject.getFlag(context, 83);
               ContextObject contextObject = ContextObject.clone(context);
               contextObject.put(247, this);
               Array.resize(verbs, 0);
               boolean isInAddressBook = !(addressCard instanceof Object) ? false : ((AddressCardElement)addressCard).getUID() != -1;
               if (!isInAddressBook && hasFocus && !editable) {
                  if (!ContextObject.getPrivateFlag(contextObject, 4936088360624690805L, 55)) {
                     Object friendlyName = ContextObject.get(context, -4886909117188079897L);
                     Verb defaultAddToABVerb = this.addAddToAddressBookVerbs(verbs, contextObject, friendlyName);
                     if (contextObject.getFlag(112)) {
                        return defaultAddToABVerb;
                     }
                  }
               } else if (contextObject.getFlag(112)) {
                  return null;
               }

               if (hyperlink || !editable && !picking || picking && contextObject.getFlag(20)) {
                  if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 18) && ContextObject.getFlag(context, 37)) {
                     Object model = ContextObject.get(context, 250);
                     if (model == this) {
                        VerbFactory[] vfs = VerbFactoryRepository.getVerbFactories(-5922809018445304484L);
                        if (vfs != null && vfs.length > 0) {
                           Object oldPhoneNumber = ContextObject.get(context, 247);
                           ContextObject.put(context, 247, this);
                           VerbFactory vf = vfs[0];
                           Verb[] speedDialVerbs = vf.getVerbs(context);
                           if (speedDialVerbs != null && speedDialVerbs.length > 0) {
                              Array.resize(verbs, verbs.length + speedDialVerbs.length);
                              System.arraycopy(speedDialVerbs, 0, verbs, verbs.length - speedDialVerbs.length, speedDialVerbs.length);
                           }

                           if (oldPhoneNumber != null) {
                              ContextObject.put(context, 247, oldPhoneNumber);
                           } else {
                              ContextObject.remove(context, 247);
                           }
                        }
                     }
                  }

                  if (ContextObject.getFlag(contextObject, 83)) {
                     Object hyperlinkContext = ContextObject.clone(contextObject);
                     if (addressCard != null && ContextObject.get(hyperlinkContext, 252) != null) {
                        ContextObject.remove(hyperlinkContext, 252);
                     }

                     defaultVerb = this.addHyperlinkVerbs(verbs, hyperlinkContext);
                  } else if (addressCard == null || composeOnlyContext || ContextObject.getFlag(contextObject, 11)) {
                     int maxPriorityComposeVerbIndex = this.addComposeVerbs(verbs, contextObject);
                     if (maxPriorityComposeVerbIndex >= 0) {
                        if (contextObject.getFlag(20)) {
                           defaultVerb = verbs[maxPriorityComposeVerbIndex];
                        } else {
                           defaultVerb = hasFocus ? verbs[maxPriorityComposeVerbIndex] : null;
                        }
                     }
                  } else if (addressCard instanceof Object) {
                     VerbProvider verbProvider = (VerbProvider)addressCard;
                     ContextObject tmpContext = ContextObject.clone(context);
                     ContextObject.setFlag(tmpContext, 44);
                     if (isInAddressBook) {
                        AbstractPhoneNumberModel.addViewContactVerb(verbs, addressCard);
                     } else {
                        ContextObject.setPrivateFlag(tmpContext, 4936088360624690805L, 55);
                     }

                     defaultVerb = verbProvider.getVerbs(tmpContext, verbs);
                  }
               }

               if (!ContextObject.getFlag(context, 44)) {
                  Field uiField = (Field)ContextObject.get(context, 9045827404276417370L);
                  if (uiField != null) {
                     Manager mgr = uiField.getManager();
                     ToggleableField toggleableField = null;

                     while (mgr != null) {
                        if (mgr instanceof Object) {
                           toggleableField = (ToggleableField)mgr;
                           break;
                        }

                        mgr = mgr.getManager();
                     }

                     if (toggleableField != null) {
                        Array.resize(verbs, verbs.length + 1);
                        int resId = toggleableField.isFriendlyVisible() ? 1650 : 1700;
                        verbs[verbs.length - 1] = (Verb)(new Object(toggleableField, CommonResources.getResourceBundle(), resId));
                     }
                  }
               }

               this.addVerbsForEnabledPhone(editable, hasFocus, hyperlink, context, verbs);
               if (contextObject.getFlag(20)) {
                  return defaultVerb;
               } else {
                  return hasFocus ? defaultVerb : null;
               }
            }
         } else {
            return null;
         }
      } else if (!ContextObject.getFlag(context, 125)) {
         Array.resize(verbs, 1);
         verbs[0] = this.getSendKeyVerb(context);
         return verbs[0];
      } else if (ContextObject.getPrivateFlag(context, 4936088360624690805L, 83)) {
         Array.resize(verbs, 1);
         verbs[0] = this.getSendKeyVerb(context);
         return verbs[0];
      } else {
         return null;
      }
   }

   @Override
   public String getVerbDescription(Object context) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      String description = null;
      StringBuffer _strBuffer = WeakReferenceUtilities.getStringBuffer(AbstractPhoneNumberModel._strBufferWR);
      Object address = this.getAddressBookEntry(contextObject);
      if (address == null) {
         String friendlyName = (String)contextObject.get(-4886909117188079897L);
         if (friendlyName != null) {
            return friendlyName;
         }

         synchronized (_strBuffer) {
            _strBuffer.setLength(0);
            this.getDisplayablePhoneNumber(_strBuffer, context);
            return _strBuffer.toString();
         }
      } else {
         boolean hyperlink = ContextObject.getFlag(context, 83);
         boolean type = ContextObject.getFlag(context, 34);
         boolean hotlist = ContextObject.getPrivateFlag(context, 4936088360624690805L, 9);
         if (type && !hyperlink && !hotlist) {
            String typeString = null;
            if (this._type == 0) {
               PhoneNumberModel matchedNumberModel = (PhoneNumberModel)this.matchPhoneNumber(address, null);
               if (matchedNumberModel != null) {
                  typeString = AbstractPhoneNumberModel.getTypeString(matchedNumberModel._type);
               }
            }

            if (typeString == null) {
               typeString = AbstractPhoneNumberModel.getTypeString(this._type);
            }

            if (ContextObject.getFlag(context, 42)) {
               synchronized (_strBuffer) {
                  _strBuffer.setLength(0);
                  AbstractPhoneNumberModel.formatTypeString(_strBuffer, typeString, false, false);
                  _strBuffer.append(": ");
                  this.getDisplayablePhoneNumber(_strBuffer, context);
                  return _strBuffer.toString();
               }
            } else {
               return typeString;
            }
         } else {
            String friendlyNameString = (String)contextObject.get(7065077197339612497L);
            if (friendlyNameString != null && friendlyNameString.length() > 0) {
               return friendlyNameString;
            }

            this.getAddressAndFriendlyNameGivenAddress(address, AbstractPhoneNumberModel._nameStrings);
            if (AbstractPhoneNumberModel._nameStrings[1] != null) {
               description = AbstractPhoneNumberModel._nameStrings[1];
            } else {
               description = "";
            }

            AbstractPhoneNumberModel._nameStrings[0] = null;
            AbstractPhoneNumberModel._nameStrings[1] = null;
            return description;
         }
      }
   }

   @Override
   public int getSyncFieldId(Object context) {
      int fieldId = 0;
      switch (this._type) {
         case 0:
         default:
            return 2;
         case 1:
            return 6;
         case 2:
            return 16;
         case 3:
            return 7;
         case 4:
            return 17;
         case 5:
            return 8;
         case 6:
            return 9;
         case 7:
            return 3;
         case 8:
            fieldId = 18;
         case -1:
            return fieldId;
      }
   }

   @Override
   public boolean convert(Object context, Object target) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      if (contextObject.getFlag(11) && contextObject.getFlag(19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         int fieldId = this.getSyncFieldId(null);
         syncBuffer.addField(fieldId, this.getValue());
         return true;
      }

      if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 54)) {
         if (target instanceof Object) {
            StringBuffer stringBuffer = (StringBuffer)target;
            String label;
            switch (this._type) {
               case 0:
               default:
                  label = "\rPhone:";
                  break;
               case 1:
                  label = "\rWork #:";
                  break;
               case 2:
                  label = "\rWork2 #:";
                  break;
               case 3:
                  label = "\rHome #:";
                  break;
               case 4:
                  label = "\rHome2 #:";
                  break;
               case 5:
                  label = "\rMobile #:";
                  break;
               case 6:
                  label = "\rPager:";
                  break;
               case 7:
                  label = "\rFax #:";
                  break;
               case 8:
                  label = "\rOther #:";
            }

            stringBuffer.append(label);
            stringBuffer.append(this.getValue());
            return true;
         }
      } else {
         if (contextObject.getFlag(24)) {
            StringBuffer buffer = (StringBuffer)target;
            buffer.setLength(0);
            if (ContextObject.getFlag(context, 34)) {
               String typeString = AbstractPhoneNumberModel.getTypeString(this._type);
               AbstractPhoneNumberModel.formatTypeString(buffer, typeString, contextObject.getFlag(41), true);
               return true;
            }

            if (ContextObject.getFlag(context, 42)) {
               buffer.append(this.getValue());
            }

            return true;
         }

         if (contextObject.getFlag(55) && contextObject.getFlag(19)) {
            DataBuffer _convertBuffer = WeakReferenceUtilities.getDataBuffer(_convertBufferWR, false);
            _convertBuffer.setLength(0);
            String value = this.getValue();
            int len = value.length();
            _convertBuffer.ensureCapacity(len + 5);
            _convertBuffer.writeInt(PhoneNumberTypes.mapType(this._type, true));

            for (int i = 0; i < len; i++) {
               char c = value.charAt(i);
               if (c == '\n') {
                  _convertBuffer.writeByte(13);
               } else {
                  _convertBuffer.writeByte(c);
               }
            }

            _convertBuffer.writeByte(0);
            _convertBuffer.trim();
            int fieldId = contextObject.getIntegerData(0);
            ((SyncBuffer)target).addBytes(fieldId, _convertBuffer.getArray());
            return true;
         }

         if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19)) {
            int bufferSize = 10;
            this.getAddressAndFriendlyName(AbstractPhoneNumberModel._nameStrings);
            byte[] friendlyNameBytes = null;
            String fn = AbstractPhoneNumberModel._nameStrings[1];
            if (fn != null && fn.length() > 0) {
               friendlyNameBytes = fn.getBytes();
               bufferSize += friendlyNameBytes.length;
            }

            byte[] addressBytes = null;
            String address = AbstractPhoneNumberModel._nameStrings[0];
            if (address != null) {
               addressBytes = address.getBytes();
               bufferSize += addressBytes.length;
            }

            int addressFieldId;
            if (this._type == 7) {
               addressFieldId = 3;
            } else {
               addressFieldId = 2;
            }

            byte[] buf = new byte[bufferSize];
            if (ContextObject.getFlag(context, 38)) {
               buf[0] = -1;
               buf[1] = 7;
               buf[2] = 0;
               buf[3] = 0;
            } else if (ContextObject.getFlag(context, 104)) {
               buf[0] = -1;
               buf[1] = -1;
               buf[2] = 31;
               buf[3] = 0;
            } else if (ContextObject.getFlag(context, 75)) {
               buf[0] = -1;
               buf[1] = -1;
               buf[2] = 63;
               buf[3] = 0;
            } else {
               buf[0] = -1;
               buf[1] = -1;
               buf[2] = -1;
               buf[3] = 1;
            }

            buf[4] = -1;
            buf[5] = 0;
            buf[6] = (byte)addressFieldId;
            buf[7] = 3;
            int currentOffset = 8;
            if (friendlyNameBytes != null) {
               System.arraycopy(friendlyNameBytes, 0, buf, currentOffset, friendlyNameBytes.length);
               currentOffset += friendlyNameBytes.length;
            }

            currentOffset++;
            if (addressBytes != null) {
               System.arraycopy(addressBytes, 0, buf, currentOffset, addressBytes.length);
            }

            ((Object[])target)[0] = buf;
            return true;
         }

         if (ContextObject.getFlag(context, 21)) {
            if (target instanceof Object) {
               StringBuffer buf = (StringBuffer)target;
               this.getTransmittablePhoneNumber(buf, context);
               return true;
            }
         } else if (ContextObject.getFlag(context, 10) && target instanceof Object[]) {
            String[] names = (Object[])target;
            if (names.length == 2) {
               this.getAddressAndFriendlyName(names);
               return true;
            }
         } else if (contextObject.getFlag(20) && contextObject.getFlag(19)) {
            SyncBuffer syncBuffer = (SyncBuffer)target;
            syncBuffer.addInt(11, PhoneNumberTypes.mapType(this._type, true), 4);
            syncBuffer.addField(12, this.getValue());
            return true;
         }
      }

      return false;
   }

   @Override
   public Object copy() {
      return new PhoneNumberModel(this);
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._phoneNumberEncoding, false, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._phoneNumberEncoding = PersistentContent.reEncode(this._phoneNumberEncoding, false, encrypt);
      return null;
   }

   @Override
   public boolean invokeApplicationKeyVerb() {
      return ControllerUtilities.invokeApplicationKeyVerb(this);
   }

   @Override
   public MenuItem getFocusVerbs(CookieProvider provider, Object context, Vector items) {
      if (context == null) {
         ContextObject contextObject = (ContextObject)(new Object());
         contextObject.setFlag(2);
         contextObject.setFlag(83);
         if (provider == null || provider.getCookieWithFocus() != null) {
            contextObject.setFlag(117);
         }

         context = contextObject;
      }

      return CookieProviderUtilities.getFocusVerbsForActiveField(this, context, items);
   }

   @Override
   protected int paintRawPhoneNumber(Graphics g, int x, int y, int width, int height, int flags, Object context) {
      StringBuffer _strBuffer = WeakReferenceUtilities.getStringBuffer(AbstractPhoneNumberModel._strBufferWR);
      synchronized (_strBuffer) {
         _strBuffer.setLength(0);
         this.getDisplayablePhoneNumber(_strBuffer, context);
         return g.drawText(_strBuffer, 0, _strBuffer.length(), x, y, flags, width);
      }
   }

   @Override
   public boolean equals(Object other, boolean typeIndependent) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof PhoneNumberModel)) {
         return false;
      }

      PhoneNumberModel otherNumber = (PhoneNumberModel)other;
      if (!typeIndependent && this.getType() != otherNumber.getType()) {
         return false;
      }

      if (this._hashCode != otherNumber._hashCode) {
         return false;
      }

      PhoneNumberComparator comparator = PhoneNumberComparator.getScratchComparator();
      synchronized (comparator) {
         comparator.compare(this, otherNumber);
         return comparator.isSubsetMatch();
      }
   }

   @Override
   public int hashCode() {
      return this._hashCode;
   }

   private void regenerateHashCode() {
      this._hashCode = PhoneNumberHashCodeGenerator.hashCode(this);
   }

   private String getReverseLookupCodeString() {
      char[] number = this.toString().toCharArray();
      if (number != null && number.length != 0) {
         if (number.length <= 7) {
            return this.getValue();
         }

         int index = getFirstIndexForReverseLookupCode(number, 7);

         try {
            return (String)(new Object(number, index, number.length - index));
         } finally {
            ;
         }
      } else {
         return "";
      }
   }

   private static int getFirstIndexForReverseLookupCode(char[] number, int hashLength) {
      int endIndex = PhoneNumberConverter.findExtensionOffset(number);
      if (endIndex < 0) {
         endIndex = number.length;
      }

      int digitCount = 0;

      int index;
      for (index = endIndex - 1; index >= 0; index--) {
         char c = number[index];
         if (Character.isDigit(c)) {
            digitCount++;
         }

         if (digitCount == hashLength) {
            break;
         }
      }

      if (index == -1) {
         index = 0;
      }

      return index;
   }

   private static char byteToChar(byte b) {
      return (char)(b & 0xFF);
   }

   @Override
   public byte[] getBytes() {
      String value = this.getValue();
      return value != null ? value.getBytes() : null;
   }

   @Override
   public String getValue() {
      try {
         return PersistentContent.decodeString(this._phoneNumberEncoding);
      } finally {
         ;
      }
   }

   @Override
   public void setValue(String phoneNumber) {
      if (phoneNumber == null) {
         phoneNumber = "";
      } else {
         phoneNumber = phoneNumber.trim();
      }

      this._phoneNumberEncoding = PersistentContent.encode(phoneNumber, false, false);
      this.regenerateHashCode();
   }

   private Verb[] getPhoneNumberComposeVerbs() {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(-6761056765378641298L);
      return verbRepository.getVerbs(null);
   }

   private int addComposeVerbs(Verb[] verbs, Object context) {
      int offsetToStartAddingAt = verbs.length;
      Verb[] phoneNumberComposeVerbs = this.getPhoneNumberComposeVerbs();
      int minComposeVerbPriority = Integer.MAX_VALUE;
      int minPriorityComposeVerbIndex = -1;
      int numberOfPhoneNumberComposeVerbs = phoneNumberComposeVerbs != null ? phoneNumberComposeVerbs.length : 0;
      if (numberOfPhoneNumberComposeVerbs > 0) {
         Array.resize(verbs, verbs.length + numberOfPhoneNumberComposeVerbs);

         for (int i = 0; i < numberOfPhoneNumberComposeVerbs; i++) {
            Verb composeVerb = phoneNumberComposeVerbs[i];
            Verb copyOfVerb = null;
            if (this.validateVerb(composeVerb, context)) {
               copyOfVerb = this.copyVerbAndSetParameters(composeVerb, context);
               if (copyOfVerb != null) {
                  verbs[offsetToStartAddingAt++] = copyOfVerb;
                  int priority = copyOfVerb.getOrdering();
                  if (priority < minComposeVerbPriority) {
                     minComposeVerbPriority = priority;
                     minPriorityComposeVerbIndex = offsetToStartAddingAt - 1;
                  }
               }
            }
         }

         Array.resize(verbs, offsetToStartAddingAt);
      }

      return minPriorityComposeVerbIndex;
   }

   private Verb copyVerbAndSetParameters(Verb verbToCopy, Object context) {
      if (!(verbToCopy instanceof Object)) {
         return null;
      }

      Verb copy = (Verb)((Copyable)verbToCopy).copy();
      if (copy instanceof Object) {
         ((SetParameter)copy).setParameter(context);
      }

      return copy;
   }

   private Verb addHyperlinkVerbs(Verb[] verbs, Object context) {
      Verb[] composeVerbs = this.getPhoneNumberComposeVerbs();
      int minOrdering = Integer.MAX_VALUE;
      int defaultIndex = -1;
      int indexAtWhichToAdd = verbs.length;
      boolean oldBackgroundFlag = ContextObject.getFlag(context, 96);
      ContextObject.setFlag(context, 96);
      if (composeVerbs != null && composeVerbs.length > 0) {
         Array.resize(verbs, verbs.length + composeVerbs.length);

         for (int i = 0; i < composeVerbs.length; i++) {
            Verb composeVerb = composeVerbs[i];
            if (this.validateVerb(composeVerb, context)) {
               Verb copy = this.copyVerbAndSetParameters(composeVerb, context);
               if (copy != null) {
                  verbs[indexAtWhichToAdd++] = copy;
                  int ordering = copy.getOrdering();
                  if (ordering < minOrdering) {
                     defaultIndex = indexAtWhichToAdd - 1;
                     minOrdering = ordering;
                  }
               }
            }
         }

         Array.resize(verbs, indexAtWhichToAdd);
      }

      if (!oldBackgroundFlag) {
         ContextObject.clearFlag(context, 96);
      }

      return defaultIndex < 0 ? null : verbs[defaultIndex];
   }

   private String removePrefix(String number) {
      number = number.trim();
      AbstractString absStr = AbstractStringWrapper.createInstance(number);
      int prefixLen = PhoneNumberStringPattern.getPrefixLength(absStr);
      return prefixLen > 0 ? number.substring(prefixLen).trim() : number;
   }

   private Verb getSendKeyVerb(Object context) {
      String value = this.getValue();
      if (value != null && value.length() != 0) {
         Verb[] composeVerbs = this.getPhoneNumberComposeVerbs();

         for (int i = 0; i < composeVerbs.length; i++) {
            if (composeVerbs[i] instanceof Object && composeVerbs[i] instanceof Object && composeVerbs[i] instanceof Object) {
               ContextObject co = ContextObject.clone(context);
               co.put(247, this);
               Object addressCard = ContextObject.get(context, 252);
               if (addressCard == null) {
                  addressCard = this.getAddressBookEntry(context);
                  if (addressCard != null) {
                     co.put(252, addressCard);
                  }
               }

               return this.copyVerbAndSetParameters(composeVerbs[i], co);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean validateVerb(Verb verb, Object context) {
      if (this._type == 13) {
         return false;
      } else {
         return context instanceof Object && ((ContextObject)context).getPrivateFlag(4936088360624690805L, 15) && !(verb instanceof DialPhoneNumberVerb)
            ? false
            : !(verb instanceof Object) || ((ConditionalVerb)verb).canInvoke(context);
      }
   }

   private Verb addAddToAddressBookVerbs(Verb[] verbs, Object context, Object friendlyName) {
      Verb addToABVerb = AddressBookServices.getAddToAddressBookVerb();
      int verbCount = 0;
      int verbInsertionIndex = verbs.length;
      boolean copyContext = ContextObject.getFlag(context, 49);
      Verb defaultVerb = null;
      int numberType = 1;
      if (addToABVerb != null) {
         verbCount++;
      }

      if (copyContext) {
         verbCount++;
      }

      Array.resize(verbs, verbs.length + verbCount);
      if (addToABVerb != null) {
         Object selectedItem = ContextObject.get(context, 250);
         Object uiField = ContextObject.get(context, 9045827404276417370L);
         if (isSMS(selectedItem) && uiField instanceof SmartPhoneNumberLabelField) {
            numberType = 5;
         }

         ContextObject ctxt = (ContextObject)(new Object(34));
         ctxt.putIntegerData(numberType);
         ctxt.put(254, this);
         PersistableRIMModel phoneNumberModel = (PersistableRIMModel)FactoryUtil.createInstance(3797587162219887872L, ctxt);
         int menuOrdering;
         if (ContextObject.getPrivateFlag(context, 4936088360624690805L, 9)) {
            menuOrdering = 16908608;
         } else {
            menuOrdering = 16867328;
         }

         ContextObject ctx = (ContextObject)(new Object());
         if (phoneNumberModel != null) {
            ctx.put(254, phoneNumberModel);
         }

         if (friendlyName != null) {
            ctx.put(-4886909117188079897L, friendlyName);
         }

         WrapperVerb wrapper = (WrapperVerb)(new Object(addToABVerb, ctx, menuOrdering));
         verbs[verbInsertionIndex++] = wrapper;
         defaultVerb = wrapper;
      }

      if (copyContext) {
         StringBuffer strBuffer = (StringBuffer)(new Object());
         this.getDisplayablePhoneNumber(strBuffer, false);
         CopyPhoneNumberVerb copyNumberVerb = new CopyPhoneNumberVerb();
         copyNumberVerb.setNumber(strBuffer.toString());
         verbs[verbInsertionIndex++] = copyNumberVerb;
         if (addToABVerb == null) {
            defaultVerb = copyNumberVerb;
         }
      }

      return defaultVerb;
   }

   private void initialize(String stringData, boolean preservePrefix) {
      if (stringData != null && stringData.length() > 0) {
         if (StringUtilities.regionMatches(stringData, true, 0, CTI_PREFIX, 0, CTI_PREFIX.length(), 1701707776)) {
            stringData = stringData.substring(CTI_PREFIX.length());
            this._type = 12;
         }

         if (preservePrefix) {
            this.setValue(stringData);
            return;
         }

         this.setValue(this.removePrefix(stringData));
      }
   }

   private EditField getPhoneNumberEditField(String label, String initialValue, Object context) {
      return new SmartPhoneNumberEditField(label, initialValue, context);
   }

   private Field getQualifiedAddressField(Object context) {
      boolean fullContents = ContextObject.getFlag(context, 107);
      long style = 0;
      if (!fullContents) {
         style |= 64;
      }

      if (!ContextObject.getFlag(context, 26)) {
         style |= 18014398509481984L;
      }

      if (PackageManager.isPhoneEnabled()) {
         String convertedString = PhoneNumberServices.convertInternalToDisplay(this.getValue());
         if (fullContents) {
            Field returnField = (Field)(new Object(convertedString, style));
            returnField.setCookie(this);
            char speedDialKey = QuickContactList.getInstance().getQuickContactKey(this);
            if (speedDialKey != 0) {
               VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
               String label = QuickContactUtil.getSpeedDialKeyLabel(speedDialKey);
               vfm.add(returnField);
               vfm.add((Field)(new Object(label)));
               vfm.setCookie(this);
               return vfm;
            } else {
               return returnField;
            }
         } else {
            return new SmartPhoneNumberLabelField(convertedString, style);
         }
      } else {
         return (Field)(fullContents ? new Object(this.getValue(), style) : new Object(this.getValue(), style));
      }
   }

   private void getDisplayablePhoneNumber(StringBuffer buffer, Object context) {
      boolean trimPausesAndWaits = !ContextObject.getFlag(context, 3);
      this.getDisplayablePhoneNumber(buffer, trimPausesAndWaits);
   }

   private void getDisplayablePhoneNumber(StringBuffer buffer, boolean trimPausesAndWaits) {
      String convertedString = PhoneNumberServices.convertForDisplayWithExtension(this.getValue(), trimPausesAndWaits);
      buffer.append(convertedString);
   }

   @Override
   public String getDisplayablePhoneNumber() {
      return PhoneNumberServices.convertForDisplayWithExtension(this.getValue(), false);
   }

   public PhoneNumberModel(Object initialData) {
      this._type = 0;
      if (initialData instanceof Object) {
         this.initialize((String)initialData, false);
      } else if (initialData != null) {
         ContextObject contextObject = ContextObject.verifyNonNull(initialData);
         boolean haveTypeInfo = false;
         if (contextObject.getFlag(34)) {
            Integer intType = (Integer)contextObject.get(-4054673099568009991L);
            if (intType != null) {
               haveTypeInfo = true;
               this._type = intType;
            }
         }

         Object model = contextObject.get(254);
         if (model != null) {
            if (!(model instanceof PhoneNumberModel)) {
               throw new Object(((StringBuffer)(new Object("IllegalClassName: "))).append(model.getClass().getName()).toString());
            }

            PhoneNumberModel pnm = (PhoneNumberModel)model;
            this.setValue(pnm.getValue());
            if (!haveTypeInfo) {
               this._type = pnm._type;
            }
         } else {
            String stringData = (String)contextObject.get(253);
            this.initialize(stringData, contextObject.getFlag(32));
         }
      }

      if (this._phoneNumberEncoding == null) {
         this.setValue("");
      }
   }

   private Field checkIfIconRequired(Field in) {
      Bitmap bmp = CustomContactImageProvider.getDisplayIcon(this);
      if (bmp != null) {
         BitmapField bitmapField = (BitmapField)(new Object(bmp));
         HorizontalFieldManager out = (HorizontalFieldManager)(new Object());
         out.add(bitmapField);
         out.add(in);
         return out;
      } else {
         return in;
      }
   }

   public PhoneNumberModel(PhoneNumberModel model) {
      if (model != null) {
         this._type = model._type;
         this._phoneNumberEncoding = PersistentContent.copyEncoding(model._phoneNumberEncoding);
         this._hashCode = model._hashCode;
      }
   }

   @Override
   public void setType(int type) {
      this._type = type;
   }

   @Override
   public int getType() {
      return this._type;
   }

   private static boolean isSMS(Object item) {
      if (!(item instanceof Object)) {
         return false;
      }

      MatchProvider matchProvider = (MatchProvider)item;
      SearchCriterion crit = new PhoneNumberModel$1();
      return matchProvider.match(crit) == 1;
   }
}
