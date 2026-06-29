package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.core.SecondaryURLNode;
import net.rim.device.apps.internal.browser.javascript.JavaScriptRegistry;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.FormData;
import net.rim.device.apps.internal.browser.util.BrowserIdEncryptor;
import net.rim.device.apps.internal.browser.util.ImageMap;
import org.w3c.dom.html2.HTMLAnchorElement;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLInputElement;

class HTMLInput extends HTMLGenericElement implements HTMLInputElement, SecondaryURLNode {
   private HTMLForm _form;
   protected Field _uiPeer;
   private String _value;
   protected boolean _dropEvents;
   protected String _uid;
   private int _tag;
   protected int _uiPeerType;
   private long _style;
   protected static final int HIDDEN;
   protected static final int RADIO;
   protected static final int CHECK;
   protected static final int TEXT;
   protected static final int SUBMIT;
   protected static final int RESET;
   protected static final int IMAGE;
   protected static final int BUTTON;
   protected static final int TEXT_AREA;
   protected static final int SELECT;
   protected static final int FILE;

   public void setPeer(Field field) {
      this._uiPeer = field;
      this._uiPeerType = 0;
      if (this._uiPeer instanceof HTMLCheckboxInputField) {
         this._uiPeerType = 2;
      } else if (this._uiPeer instanceof HTMLRadioInputField) {
         this._uiPeerType = 1;
      } else if (this._uiPeer instanceof HTMLSubmitInputField) {
         this._uiPeerType = 4;
      } else if (this._uiPeer instanceof HTMLResetInputField) {
         this._uiPeerType = 5;
      } else if (this._uiPeer instanceof HTMLImageInputField) {
         this._uiPeerType = 6;
      } else if (this._uiPeer instanceof HTMLTextInputField) {
         this._uiPeerType = 3;
      } else if (this._uiPeer instanceof HTMLButtonInputField) {
         this._uiPeerType = 7;
      } else if (this._uiPeer instanceof HTMLTextAreaField) {
         this._uiPeerType = 8;
      } else if (this._uiPeer instanceof HTMLSelectField) {
         this._uiPeerType = 9;
      } else {
         if (this._uiPeer instanceof HTMLFileInputField) {
            this._uiPeerType = 10;
         }
      }
   }

   public Field getPeer() {
      return this._uiPeer;
   }

   public void setUid(int id) {
      String name = this.getName();
      if (name != null) {
         this._uid = ((StringBuffer)(new Object())).append(name).append(id).toString();
      } else {
         this._uid = Integer.toString(id);
      }
   }

   void clickInternal() {
      switch (this._uiPeerType) {
         case 1:
            ((HTMLRadioInputField)this._uiPeer).clickInternal();
            return;
         case 2:
            ((HTMLCheckboxInputField)this._uiPeer).clickInternal();
         case 0:
         case 3:
            return;
         case 4:
         case 5:
         case 7:
         default:
            ((HTMLButtonField)this._uiPeer).clickInternal();
            return;
         case 6:
            ((HTMLImageInputField)this._uiPeer).clickInternal();
      }
   }

   public void setStyle(long style) {
      this._style = style;
   }

   void onFocus() {
      synchronized (this) {
         this._dropEvents = true;
      }

      label46:
      try {
         HTMLDocumentImpl owner = (HTMLDocumentImpl)this.getOwnerDocument();
         if (owner != null) {
            HTMLBrowserContent parent = owner.getUiPeer();
            if (parent != null) {
               parent.executeJavaScriptAction(this, this.getAttributeValue(152), null);
            }
         }
      } finally {
         break label46;
      }

      synchronized (this) {
         this._dropEvents = false;
      }
   }

   void onChange() {
      synchronized (this) {
         this._dropEvents = true;
      }

      label46:
      try {
         HTMLDocumentImpl owner = (HTMLDocumentImpl)this.getOwnerDocument();
         if (owner != null) {
            HTMLBrowserContent parent = owner.getUiPeer();
            if (parent != null) {
               parent.executeJavaScriptAction(this, this.getAttributeValue(149), null);
               parent.executeJavaScriptAction(this, this.getAttributeValue(148), null);
            }
         }
      } finally {
         break label46;
      }

      synchronized (this) {
         this._dropEvents = false;
      }
   }

   void onBlur() {
      synchronized (this) {
         this._dropEvents = true;
      }

      HTMLDocumentImpl owner = (HTMLDocumentImpl)this.getOwnerDocument();
      if (owner != null) {
         HTMLBrowserContent parent = owner.getUiPeer();
         if (parent != null) {
            label44:
            try {
               parent.executeJavaScriptAction(this, this.getAttributeValue(148), null);
            } finally {
               break label44;
            }
         }
      }

      synchronized (this) {
         this._dropEvents = false;
      }
   }

   public void setForm(HTMLForm form) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void submitForm() {
      if (this._form != null) {
         this._form.submitForm(null);
      }
   }

   void onClick() {
      synchronized (this) {
         this._dropEvents = true;
      }

      boolean execute = true;
      HTMLDocumentImpl owner = (HTMLDocumentImpl)this.getOwnerDocument();
      if (owner != null) {
         HTMLBrowserContent parent = owner.getUiPeer();
         if (parent != null) {
            label59:
            try {
               execute = parent.executeJavaScriptAction(this, this.getAttributeValue(150), null);
            } finally {
               break label59;
            }
         }

         if (execute && this._form != null) {
            switch (this._uiPeerType) {
               case 3:
                  break;
               case 4:
               case 6:
                  this._form.submitForm((SubmitButton)this._uiPeer);
                  break;
               case 5:
               default:
                  this._form.resetForm();
            }
         }
      }

      synchronized (this) {
         this._dropEvents = false;
      }
   }

   void eventOccurred(int event, boolean warn) {
      HTMLDocumentImpl owner = (HTMLDocumentImpl)this.getOwnerDocument();
      if (owner != null) {
         HTMLBrowserContent parent = owner.getUiPeer();
         synchronized (this) {
            if (this._dropEvents || parent == null) {
               return;
            }
         }

         RenderingApplication renderingApplication = parent.getRenderingApplication();
         if (!warn) {
            if (renderingApplication != null) {
               renderingApplication.invokeRunnable(new FieldAction(this, event));
            }
         } else {
            if (JavaScriptRegistry.isInstalled()) {
               if (!parent.markupSupportsJavaScript()) {
                  Dialog.alert(BrowserResources.getString(687));
                  return;
               }

               RenderingOptions renderingOptions = parent.getRenderingOptions();
               if (renderingOptions == null || !renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 2, false)) {
                  Dialog.alert(BrowserResources.getString(632));
                  return;
               }

               if (renderingApplication != null) {
                  renderingApplication.invokeRunnable(new FieldAction(this, event));
                  return;
               }
            } else {
               Dialog.alert(BrowserResources.getString(545));
            }
         }
      }
   }

   void eventOccurred(int event) {
      HTMLDocumentImpl owner = (HTMLDocumentImpl)this.getOwnerDocument();
      if (owner != null) {
         HTMLBrowserContent parent = owner.getUiPeer();
         synchronized (this) {
            if (this._dropEvents || parent == null) {
               return;
            }
         }

         RenderingApplication renderingApplication = parent.getRenderingApplication();
         if (renderingApplication != null) {
            renderingApplication.invokeRunnable(new FieldAction(this, event));
         }
      }
   }

   public boolean loadFrom(HTMLContext context) {
      if (context != null) {
         String value = context.get(this._uid);
         switch (this._uiPeerType) {
            case 1:
            case 2:
            default:
               this.setChecked(value != null && value.equals(this.getValue()));
               return true;
            case 3:
               if (StringUtilities.strEqualIgnoreCase(this.getType(), "password", 1701707776)) {
                  return false;
               }
            case 0:
            case 8:
            case 9:
               if (value != null) {
                  this.setValue(value);
               }

               return true;
            case 4:
            case 5:
            case 6:
            case 7:
               return false;
            case 10:
               return false;
         }
      } else {
         return false;
      }
   }

   public void reset() {
      if (this._uiPeerType == 10) {
         ((HTMLFileInputField)this._uiPeer).setFilename(null);
      } else {
         this.setValue(this.getDefaultValue());
      }

      this.setChecked(this.getDefaultChecked());
      if (this._uiPeerType == 9) {
         HTMLSelectField select = (HTMLSelectField)this._uiPeer;
         select.reset();
      }
   }

   public void submit(HTMLContext context) {
      boolean addValue = true;
      switch (this._uiPeerType) {
         case 0:
         case 3:
         case 8:
         case 9:
            break;
         case 1:
            addValue = ((HTMLRadioInputField)this._uiPeer).isSelected();
            break;
         case 2:
         default:
            addValue = ((HTMLCheckboxInputField)this._uiPeer).getChecked();
            break;
         case 4:
         case 5:
         case 6:
         case 7:
         case 10:
            addValue = false;
      }

      if (context != null && addValue) {
         context.put(this._uid, this.getValue());
      }
   }

   public void submit(FormData param1, Object param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokevirtual net/rim/device/apps/internal/browser/html/HTMLInput.getName ()Ljava/lang/String;
      // 004: ldc_w "_charset_"
      // 007: invokestatic net/rim/device/api/util/StringUtilities.strEqual (Ljava/lang/String;Ljava/lang/String;)Z
      // 00a: ifeq 025
      // 00d: ldc_w "hidden"
      // 010: aload 0
      // 011: invokevirtual net/rim/device/apps/internal/browser/html/HTMLInput.getType ()Ljava/lang/String;
      // 014: ldc_w 1701707776
      // 017: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 01a: ifeq 025
      // 01d: aload 0
      // 01e: aload 1
      // 01f: invokevirtual net/rim/device/apps/internal/browser/stack/FormData.getCharset ()Ljava/lang/String;
      // 022: putfield net/rim/device/apps/internal/browser/html/HTMLInput._value Ljava/lang/String;
      // 025: aload 0
      // 026: invokevirtual net/rim/device/apps/internal/browser/html/HTMLInput.getName ()Ljava/lang/String;
      // 029: astore 3
      // 02a: bipush 1
      // 02b: istore 4
      // 02d: bipush 0
      // 02e: istore 5
      // 030: aload 0
      // 031: getfield net/rim/device/apps/internal/browser/html/HTMLInput._uiPeerType I
      // 034: tableswitch 60 0 10 604 79 60 604 98 124 130 124 604 152 278
      // 070: aload 0
      // 071: getfield net/rim/device/apps/internal/browser/html/HTMLInput._uiPeer Lnet/rim/device/api/ui/Field;
      // 074: checkcast net/rim/device/apps/internal/browser/html/HTMLCheckboxInputField
      // 077: astore 6
      // 079: aload 6
      // 07b: invokevirtual net/rim/device/api/ui/component/CheckboxField.getChecked ()Z
      // 07e: istore 4
      // 080: goto 290
      // 083: aload 0
      // 084: getfield net/rim/device/apps/internal/browser/html/HTMLInput._uiPeer Lnet/rim/device/api/ui/Field;
      // 087: checkcast net/rim/device/apps/internal/browser/html/HTMLRadioInputField
      // 08a: astore 7
      // 08c: aload 7
      // 08e: invokevirtual net/rim/device/api/ui/component/RadioButtonField.isSelected ()Z
      // 091: istore 4
      // 093: goto 290
      // 096: aload 0
      // 097: getfield net/rim/device/apps/internal/browser/html/HTMLInput._uiPeer Lnet/rim/device/api/ui/Field;
      // 09a: checkcast net/rim/device/apps/internal/browser/html/HTMLSubmitInputField
      // 09d: astore 8
      // 09f: aload 8
      // 0a1: invokevirtual net/rim/device/apps/internal/browser/html/HTMLSubmitInputField.getSelected ()Z
      // 0a4: istore 4
      // 0a6: aload 8
      // 0a8: invokevirtual net/rim/device/apps/internal/browser/html/HTMLSubmitInputField.isImage ()Z
      // 0ab: istore 5
      // 0ad: goto 290
      // 0b0: bipush 0
      // 0b1: istore 4
      // 0b3: goto 290
      // 0b6: aload 0
      // 0b7: getfield net/rim/device/apps/internal/browser/html/HTMLInput._uiPeer Lnet/rim/device/api/ui/Field;
      // 0ba: checkcast net/rim/device/apps/internal/browser/html/HTMLImageInputField
      // 0bd: astore 9
      // 0bf: bipush 1
      // 0c0: istore 5
      // 0c2: aload 9
      // 0c4: invokevirtual net/rim/device/apps/internal/browser/html/HTMLImageInputField.getSelected ()Z
      // 0c7: istore 4
      // 0c9: goto 290
      // 0cc: aload 0
      // 0cd: getfield net/rim/device/apps/internal/browser/html/HTMLInput._uiPeer Lnet/rim/device/api/ui/Field;
      // 0d0: checkcast net/rim/device/apps/internal/browser/html/HTMLSelectField
      // 0d3: astore 10
      // 0d5: aload 0
      // 0d6: checkcast net/rim/device/apps/internal/browser/html/HTMLSelect
      // 0d9: astore 11
      // 0db: aload 11
      // 0dd: invokevirtual net/rim/device/apps/internal/browser/html/HTMLSelect.getOptions ()Lorg/w3c/dom/html2/HTMLOptionsCollection;
      // 0e0: astore 12
      // 0e2: aload 12
      // 0e4: invokeinterface org/w3c/dom/html2/HTMLOptionsCollection.getLength ()I 1
      // 0e9: istore 13
      // 0eb: aconst_null
      // 0ec: astore 14
      // 0ee: bipush 0
      // 0ef: istore 15
      // 0f1: iload 15
      // 0f3: iload 13
      // 0f5: if_icmpge 144
      // 0f8: aload 10
      // 0fa: iload 15
      // 0fc: invokevirtual net/rim/device/apps/internal/browser/ui/SelectField.isOptionSet (I)Z
      // 0ff: ifeq 13e
      // 102: aload 12
      // 104: iload 15
      // 106: invokeinterface org/w3c/dom/html2/HTMLOptionsCollection.item (I)Lorg/w3c/dom/Node; 2
      // 10b: checkcast net/rim/device/apps/internal/browser/html/HTMLOption
      // 10e: astore 16
      // 110: aload 16
      // 112: ifnull 13e
      // 115: aload 16
      // 117: ldc_w "value"
      // 11a: invokevirtual net/rim/device/apps/internal/browser/html/HTMLGenericElement.hasAttribute (Ljava/lang/String;)Z
      // 11d: ifeq 128
      // 120: aload 16
      // 122: invokevirtual net/rim/device/apps/internal/browser/html/HTMLOption.getValue ()Ljava/lang/String;
      // 125: goto 12f
      // 128: aload 10
      // 12a: iload 15
      // 12c: invokevirtual net/rim/device/apps/internal/browser/ui/SelectField.getOption (I)Ljava/lang/String;
      // 12f: astore 14
      // 131: aload 14
      // 133: ifnull 13e
      // 136: aload 1
      // 137: aload 2
      // 138: aload 3
      // 139: aload 14
      // 13b: invokevirtual net/rim/device/apps/internal/browser/stack/FormData.append (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V
      // 13e: iinc 15 1
      // 141: goto 0f1
      // 144: bipush 0
      // 145: istore 4
      // 147: goto 290
      // 14a: aload 1
      // 14b: dup
      // 14c: instanceof net/rim/device/apps/internal/browser/stack/MultipartFormData
      // 14f: ifne 156
      // 152: pop
      // 153: goto 290
      // 156: checkcast net/rim/device/apps/internal/browser/stack/MultipartFormData
      // 159: astore 15
      // 15b: aload 0
      // 15c: invokevirtual net/rim/device/apps/internal/browser/html/HTMLInput.getValue ()Ljava/lang/String;
      // 15f: astore 16
      // 161: aload 16
      // 163: ifnonnull 16b
      // 166: ldc_w ""
      // 169: astore 16
      // 16b: aconst_null
      // 16c: astore 17
      // 16e: aconst_null
      // 16f: astore 18
      // 171: aload 16
      // 173: invokevirtual java/lang/String.length ()I
      // 176: ifgt 17c
      // 179: goto 276
      // 17c: aconst_null
      // 17d: astore 19
      // 17f: aconst_null
      // 180: astore 20
      // 182: aload 16
      // 184: invokestatic net/rim/device/internal/io/file/FileUtilities.makeFileURL (Ljava/lang/String;)Ljava/lang/String;
      // 187: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 18a: checkcast java/lang/Object
      // 18d: astore 19
      // 18f: aload 19
      // 191: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 196: ifeq 1ca
      // 199: aload 19
      // 19b: dup
      // 19c: instanceof java/lang/Object
      // 19f: ifne 1a6
      // 1a2: pop
      // 1a3: goto 1c1
      // 1a6: checkcast java/lang/Object
      // 1a9: astore 21
      // 1ab: aload 21
      // 1ad: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.isContentDRMForwardLocked ()Z 1
      // 1b2: ifne 1ca
      // 1b5: aload 21
      // 1b7: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.openRawInputStream ()Ljava/io/InputStream; 1
      // 1bc: astore 20
      // 1be: goto 1ca
      // 1c1: aload 19
      // 1c3: invokeinterface javax/microedition/io/file/FileConnection.openInputStream ()Ljava/io/InputStream; 1
      // 1c8: astore 20
      // 1ca: aload 20
      // 1cc: ifnull 1dd
      // 1cf: aload 20
      // 1d1: invokestatic net/rim/device/api/io/IOUtilities.streamToBytes (Ljava/io/InputStream;)[B
      // 1d4: astore 18
      // 1d6: aload 16
      // 1d8: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 1db: astore 17
      // 1dd: aload 20
      // 1df: ifnull 1ec
      // 1e2: aload 20
      // 1e4: invokevirtual java/io/InputStream.close ()V
      // 1e7: goto 1ec
      // 1ea: astore 21
      // 1ec: aload 19
      // 1ee: ifnull 26f
      // 1f1: aload 19
      // 1f3: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1f8: goto 26f
      // 1fb: astore 21
      // 1fd: goto 26f
      // 200: astore 21
      // 202: aload 20
      // 204: ifnull 211
      // 207: aload 20
      // 209: invokevirtual java/io/InputStream.close ()V
      // 20c: goto 211
      // 20f: astore 21
      // 211: aload 19
      // 213: ifnull 26f
      // 216: aload 19
      // 218: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 21d: goto 26f
      // 220: astore 21
      // 222: goto 26f
      // 225: astore 21
      // 227: aload 20
      // 229: ifnull 236
      // 22c: aload 20
      // 22e: invokevirtual java/io/InputStream.close ()V
      // 231: goto 236
      // 234: astore 21
      // 236: aload 19
      // 238: ifnull 26f
      // 23b: aload 19
      // 23d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 242: goto 26f
      // 245: astore 21
      // 247: goto 26f
      // 24a: astore 22
      // 24c: aload 20
      // 24e: ifnull 25b
      // 251: aload 20
      // 253: invokevirtual java/io/InputStream.close ()V
      // 256: goto 25b
      // 259: astore 23
      // 25b: aload 19
      // 25d: ifnull 26c
      // 260: aload 19
      // 262: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 267: goto 26c
      // 26a: astore 23
      // 26c: aload 22
      // 26e: athrow
      // 26f: aload 16
      // 271: invokestatic net/rim/device/internal/io/file/FileUtilities.getName (Ljava/lang/String;)Ljava/lang/String;
      // 274: astore 16
      // 276: aload 17
      // 278: ifnonnull 280
      // 27b: ldc_w "application/octet-stream"
      // 27e: astore 17
      // 280: aload 15
      // 282: aload 2
      // 283: aload 3
      // 284: aload 18
      // 286: aload 17
      // 288: aload 16
      // 28a: invokevirtual net/rim/device/apps/internal/browser/stack/MultipartFormData.append (Ljava/lang/Object;Ljava/lang/String;[BLjava/lang/String;Ljava/lang/String;)V
      // 28d: bipush 0
      // 28e: istore 4
      // 290: iload 4
      // 292: ifeq 2ff
      // 295: iload 5
      // 297: ifeq 2f5
      // 29a: aload 1
      // 29b: aload 2
      // 29c: aload 3
      // 29d: ifnull 2a7
      // 2a0: aload 3
      // 2a1: invokevirtual java/lang/String.length ()I
      // 2a4: ifne 2ad
      // 2a7: ldc_w "x"
      // 2aa: goto 2c1
      // 2ad: new java/lang/Object
      // 2b0: dup
      // 2b1: invokespecial java/lang/StringBuffer.<init> ()V
      // 2b4: aload 3
      // 2b5: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2b8: ldc_w ".x"
      // 2bb: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2be: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2c1: ldc_w "0"
      // 2c4: invokevirtual net/rim/device/apps/internal/browser/stack/FormData.append (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V
      // 2c7: aload 1
      // 2c8: aload 2
      // 2c9: aload 3
      // 2ca: ifnull 2d4
      // 2cd: aload 3
      // 2ce: invokevirtual java/lang/String.length ()I
      // 2d1: ifne 2da
      // 2d4: ldc_w "y"
      // 2d7: goto 2ee
      // 2da: new java/lang/Object
      // 2dd: dup
      // 2de: invokespecial java/lang/StringBuffer.<init> ()V
      // 2e1: aload 3
      // 2e2: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2e5: ldc_w ".y"
      // 2e8: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2eb: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2ee: ldc_w "0"
      // 2f1: invokevirtual net/rim/device/apps/internal/browser/stack/FormData.append (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V
      // 2f4: return
      // 2f5: aload 1
      // 2f6: aload 2
      // 2f7: aload 3
      // 2f8: aload 0
      // 2f9: invokevirtual net/rim/device/apps/internal/browser/html/HTMLInput.getValue ()Ljava/lang/String;
      // 2fc: invokevirtual net/rim/device/apps/internal/browser/stack/FormData.append (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V
      // 2ff: return
      // try (182 -> 184): 185 null
      // try (188 -> 190): 191 null
      // try (146 -> 180): 193 null
      // try (196 -> 198): 199 null
      // try (202 -> 204): 205 null
      // try (146 -> 180): 207 null
      // try (210 -> 212): 213 null
      // try (216 -> 218): 219 null
      // try (146 -> 180): 221 null
      // try (193 -> 194): 221 null
      // try (207 -> 208): 221 null
      // try (224 -> 226): 227 null
      // try (230 -> 232): 233 null
      // try (221 -> 222): 221 null
   }

   @Override
   public void setAlt(String alt) {
      this.setAttributeValue(87, alt);
   }

   @Override
   public boolean getDisabled() {
      return this.getAttributeValueAsBoolean(117, false);
   }

   @Override
   public void setDisabled(boolean disabled) {
      this.setAttributeValue(117, disabled ? 1 : 0);
   }

   @Override
   public int getMaxLength() {
      return this.getAttributeValueAsInt(138);
   }

   @Override
   public void setMaxLength(int maxLength) {
      this.setAttributeValue(138, maxLength);
   }

   @Override
   public String getName() {
      return this.getAttributeValue(142);
   }

   @Override
   public void setName(String name) {
      this.setAttributeValue(142, name);
   }

   @Override
   public boolean getReadOnly() {
      return this.getAttributeValueAsBoolean(168, false);
   }

   @Override
   public void setReadOnly(boolean readOnly) {
      this.setAttributeValue(168, readOnly ? 1 : 0);
   }

   @Override
   public int getSize() {
      return this.getAttributeValueAsInt(179);
   }

   @Override
   public void setSize(int size) {
      this.setAttributeValue(179, size);
   }

   @Override
   public String getSrc() {
      return this.getAttributeValue(181);
   }

   @Override
   public void setSrc(String src) {
      this.setAttributeValue(181, src);
   }

   @Override
   public int getTabIndex() {
      return 0;
   }

   @Override
   public void setTabIndex(int tabIndex) {
   }

   @Override
   public String getType() {
      return this.getAttributeValue(190);
   }

   @Override
   public void setType(String type) {
      this.setAttributeValue(190, type);
   }

   @Override
   public String getUseMap() {
      return this.getAttributeValue(191);
   }

   @Override
   public void setUseMap(String useMap) {
      this.setAttributeValue(191, useMap);
   }

   @Override
   public void blur() {
      if (this._uiPeer != null) {
         Field fieldWithFocus = this._uiPeer.getLeafFieldWithFocus();
         if (fieldWithFocus != null && this._uiPeer.isFocus()) {
            HTMLDocumentImpl owner = (HTMLDocumentImpl)this.getOwnerDocument();
            if (owner != null) {
               HTMLBrowserContent contentUi = owner.getUiPeer();
               if (contentUi != null) {
                  try {
                     contentUi.executeJavaScriptAction(this, this.getAttributeValue(148), null);
                  } finally {
                     return;
                  }
               }
            }
         }
      }
   }

   @Override
   public void focus() {
      if (this._uiPeer != null) {
         Field fieldWithFocus = this._uiPeer.getLeafFieldWithFocus();
         if ((fieldWithFocus != this._uiPeer || !this._uiPeer.isFocus()) && (fieldWithFocus == this._uiPeer || fieldWithFocus == null)) {
            synchronized (this) {
               this._dropEvents = true;
            }

            Application.getApplication().invokeAndWait(new FieldAction(this, 1));
            HTMLDocumentImpl owner = (HTMLDocumentImpl)this.getOwnerDocument();
            if (owner != null) {
               HTMLBrowserContent contentUi = owner.getUiPeer();
               if (contentUi != null) {
                  label64:
                  try {
                     contentUi.executeJavaScriptAction(this, this.getAttributeValue(152), null);
                  } finally {
                     break label64;
                  }
               }
            }

            synchronized (this) {
               this._dropEvents = false;
            }
         }
      }
   }

   @Override
   public void select() {
      if (this._uiPeer != null) {
         synchronized (this) {
            this._dropEvents = true;
         }

         Application.getApplication().invokeAndWait(new FieldAction(this, 2));
         HTMLDocumentImpl owner = (HTMLDocumentImpl)this.getOwnerDocument();
         if (owner != null) {
            HTMLBrowserContent contentUi = owner.getUiPeer();
            if (contentUi != null) {
               label48:
               try {
                  contentUi.executeJavaScriptAction(this, this.getAttributeValue(163), null);
               } finally {
                  break label48;
               }
            }
         }

         synchronized (this) {
            this._dropEvents = false;
         }
      }
   }

   @Override
   public void click() {
      if (this._uiPeer != null) {
         synchronized (this) {
            this._dropEvents = true;
         }

         Application.getApplication().invokeAndWait(new FieldAction(this, 0));
         HTMLDocumentImpl owner = (HTMLDocumentImpl)this.getOwnerDocument();
         if (owner != null) {
            HTMLBrowserContent contentUi = owner.getUiPeer();
            if (contentUi != null) {
               label48:
               try {
                  contentUi.executeJavaScriptAction(this, this.getAttributeValue(150), null);
               } finally {
                  break label48;
               }
            }
         }

         synchronized (this) {
            this._dropEvents = false;
         }
      }
   }

   @Override
   public boolean getChecked() {
      switch (this._uiPeerType) {
         case 0:
            return false;
         case 1:
            return ((HTMLRadioInputField)this._uiPeer).isSelected();
         case 2:
         default:
            return ((HTMLCheckboxInputField)this._uiPeer).getChecked();
      }
   }

   @Override
   public void setChecked(boolean checked) {
      switch (this._uiPeerType) {
         case 1:
            ((HTMLRadioInputField)this._uiPeer).setSelected(checked);
         case 0:
            return;
         case 2:
         default:
            ((HTMLCheckboxInputField)this._uiPeer).setChecked(checked);
      }
   }

   @Override
   public String getAlt() {
      return this.getAttributeValue(87);
   }

   @Override
   public void setAlign(String align) {
      this.setAttributeValue(85, align);
   }

   @Override
   public String getAlign() {
      return this.getAttributeValue(85);
   }

   @Override
   public void setAccessKey(String accessKey) {
      this.setAttributeValue(83, accessKey);
   }

   @Override
   public String getValue() {
      switch (this._uiPeerType) {
         case 3:
            return ((HTMLTextInputField)this._uiPeer).getText();
         case 8:
            return ((HTMLTextAreaField)this._uiPeer).getText();
         case 10:
            return ((HTMLFileInputField)this._uiPeer).getFilename();
         default:
            if (this._value == null) {
               this._value = this.getDefaultValue();
            }

            return this._value;
      }
   }

   @Override
   public void setValue(String value) {
      this._value = value;
      switch (this._uiPeerType) {
         case 3:
            ((HTMLTextInputField)this._uiPeer).setTextWithTruncation(value);
            return;
         case 4:
         case 5:
         case 7:
         default:
            ((HTMLButtonField)this._uiPeer).setLabel(value);
            return;
         case 8:
            ((HTMLTextAreaField)this._uiPeer).setTextWithTruncation(value);
         case 2:
         case 6:
      }
   }

   @Override
   public String getAccessKey() {
      return this.getAttributeValue(83);
   }

   @Override
   public void setAccept(String accept) {
      this.setAttributeValue(81, accept);
   }

   @Override
   public String getAccept() {
      return this.getAttributeValue(81);
   }

   @Override
   public HTMLFormElement getForm() {
      return this._form;
   }

   @Override
   public void setDefaultChecked(boolean defaultChecked) {
      this.setAttributeValue(98, defaultChecked ? 1 : 0);
   }

   @Override
   public boolean getDefaultChecked() {
      return this.getAttributeValueAsBoolean(98, false);
   }

   @Override
   public Field getUIField() {
      return this.getPeer();
   }

   @Override
   public void setUIField(Field f) {
      this.setPeer(f);
   }

   @Override
   public int getVspace() {
      return this.getAttributeValueAsPixels(197);
   }

   @Override
   public int getHspace() {
      return this.getAttributeValueAsPixels(197);
   }

   @Override
   public int getWidth() {
      return this.getAttributeValueAsPixels(198);
   }

   @Override
   public int getHeight() {
      return this.getAttributeValueAsPixels(124);
   }

   @Override
   public void setWidth(int width) {
      this.setAttributeValue(198, width);
   }

   @Override
   public void setHeight(int height) {
      this.setAttributeValue(124, height);
   }

   @Override
   public int getReplaceTag() {
      return -1;
   }

   @Override
   public void setReplaceTag(int value) {
      throw new Object();
   }

   @Override
   public Object getCookie() {
      return null;
   }

   @Override
   public long getStyle() {
      return this._style;
   }

   @Override
   public void setDefaultValue(String defaultValue) {
      this.setAttributeValue(193, defaultValue);
   }

   @Override
   public HTMLAnchorElement getLink() {
      return null;
   }

   @Override
   public ImageMap getImageMap() {
      return null;
   }

   @Override
   public String getDefaultValue() {
      boolean hasValue = this.hasAttribute("value");
      String type = this.getType();
      if (StringUtilities.strEqual("XXX_RIM_ID", this.getName()) && StringUtilities.strEqualIgnoreCase("hidden", type, 1701707776)) {
         return BrowserIdEncryptor.getEncryptedId();
      }

      if (!hasValue) {
         if (StringUtilities.strEqualIgnoreCase("submit", type, 1701707776)) {
            return BrowserResources.getString(537);
         } else if (StringUtilities.strEqualIgnoreCase("reset", type, 1701707776)) {
            return BrowserResources.getString(546);
         } else {
            return StringUtilities.strEqualIgnoreCase("checkbox", type, 1701707776) ? "on" : "";
         }
      } else {
         return this.getAttributeValue(193);
      }
   }

   public HTMLInput(int tag, HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
      this._tag = tag;
   }

   @Override
   public int getTagNameInt() {
      return this._tag;
   }

   public HTMLInput(HTMLDOMInternalRepresentation dom, int nodeId) {
      this(51, dom, nodeId);
   }
}
