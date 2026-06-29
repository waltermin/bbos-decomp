package net.rim.device.apps.internal.browser.plugin.media.field;

import java.io.InputStream;
import java.util.Stack;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.VisualNodeImpl;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

final class CustomFocusOrder extends DefaultHandler {
   ModelInteractorImpl _model;
   int _defaultFocus = -1;
   FocusInteractor _focusInteractor;
   FocusGroup _focusGroup;
   FocusVector _vector = new FocusVector();
   Stack _stack = new Stack();

   public final void setModel(ModelInteractorImpl model) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void parseXml(InputStream param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/xml/parsers/SAXParserFactory.newInstance ()Lnet/rim/device/api/xml/parsers/SAXParserFactory;
      // 03: astore 2
      // 04: aload 2
      // 05: invokevirtual net/rim/device/api/xml/parsers/SAXParserFactory.newSAXParser ()Lnet/rim/device/api/xml/parsers/SAXParser;
      // 08: astore 3
      // 09: aload 3
      // 0a: aload 1
      // 0b: aload 0
      // 0c: invokevirtual net/rim/device/api/xml/parsers/SAXParser.parse (Ljava/io/InputStream;Lorg/xml/sax/helpers/DefaultHandler;)V
      // 0f: return
      // 10: astore 2
      // 11: return
      // 12: astore 2
      // 13: return
      // 14: astore 2
      // 15: return
      // try (0 -> 9): 10 null
      // try (0 -> 9): 12 null
      // try (0 -> 9): 14 null
   }

   @Override
   public final void endElement(String uri, String localName, String qName) {
      if (localName.equalsIgnoreCase("group")) {
         this._stack.pop();
      }
   }

   @Override
   public final void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (localName.equalsIgnoreCase("group")) {
         FocusGroup newFocusGroup = new FocusGroup(attributes);
         if (this._stack.size() == 0) {
            this._focusGroup = newFocusGroup;
         } else {
            ((FocusGroup)this._stack.peek()).addGroup(newFocusGroup);
         }

         this._stack.push(newFocusGroup);
      } else {
         if (localName.equalsIgnoreCase("item")) {
            ((FocusGroup)this._stack.peek()).addLeaf(this._model, attributes);
         }
      }
   }

   final void setFocusInteractor(FocusInteractor focusInteractor) {
      this._focusInteractor = focusInteractor;
      this._defaultFocus = focusInteractor.getItemInFocus();
      if (this._focusGroup != null) {
         this._focusGroup.resolveIds(this._model);
         this._focusGroup.setFocus(this._defaultFocus);
      }
   }

   final void resetFocus() {
      if (this._defaultFocus != -1) {
         this._focusInteractor.setDefaultItem(this._defaultFocus);
         this._focusInteractor.setFocusToItem(this._defaultFocus);
         if (this._focusGroup != null) {
            this._focusGroup.resetFocus();
            this._focusGroup.setFocus(this._defaultFocus);
         }
      }
   }

   final boolean moveFocus(int dx, int dy) {
      if (this._focusGroup != null) {
         int oldFocus = this._focusInteractor.getItemInFocus();
         this._vector._dx = dx;
         this._vector._dy = dy;
         this._focusGroup.moveFocus(this._vector);
         int newFocus = this._focusGroup.getCurrentFocus();
         if (newFocus != oldFocus) {
            if (!VisualNodeImpl.isResolvedDisplayable(newFocus, this._model)) {
               this._focusGroup.setFocus(oldFocus);
               return false;
            } else {
               this._focusInteractor.setFocusToItem(newFocus);
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
