package net.rim.device.apps.internal.ribbon.skin.svg;

import java.io.InputStream;
import java.util.Stack;
import net.rim.device.api.util.Arrays;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.VisualNodeImpl;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.internal.mediaengine.service.node.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class CustomFocusOrder extends DefaultHandler {
   FocusInteractor _focusInteractor;
   ModelInteractorImpl _modelInteractor;
   FocusGroup _focusGroup;
   FocusVector _vector = new FocusVector();
   int _currentFocus = -1;
   Stack _stack = (Stack)(new Object());

   CustomFocusOrder(ModelInteractorImpl modelInteractor) {
      this._modelInteractor = modelInteractor;
   }

   void setFocusInteractor(FocusInteractor focusInteractor) {
      this._focusInteractor = focusInteractor;
      this._focusGroup.resolveIds(this._modelInteractor);
   }

   public int getTargetFocusOf(int nodeId, int direction) {
      Node node = this._modelInteractor.getNode(nodeId);
      if (node == null) {
         return -1;
      }

      String id = node.getId();
      if (id == null) {
         return -1;
      }

      Object var10000 = this._focusGroup._items[0];
      if (this._focusGroup._items[0] instanceof FocusGroup) {
         FocusGroup group = (FocusGroup)var10000;

         for (int i = 0; i < group._directionalItems.length; i++) {
            if (group._directionalItems[i]._id.equals(id)) {
               String target = null;
               switch (direction) {
                  case -1:
                     break;
                  case 0:
                  default:
                     target = group._directionalItems[i]._left;
                     break;
                  case 1:
                     target = group._directionalItems[i]._right;
                     break;
                  case 2:
                     target = group._directionalItems[i]._up;
                     break;
                  case 3:
                     target = group._directionalItems[i]._down;
               }

               if (target != null) {
                  return this._modelInteractor.getHandle(target);
               }
            }
         }
      }

      return -1;
   }

   public int[] getGroup(int index) {
      int[] result = new int[0];
      Object var10000 = this._focusGroup._items[index];
      if (this._focusGroup._items[index] instanceof FocusGroup) {
         FocusGroup group = (FocusGroup)var10000;

         for (int i = 0; i < group._items.length; i++) {
            var10000 = group._items[i];
            if (group._items[i] instanceof Object) {
               Integer value = (Integer)var10000;
               Arrays.add(result, value.intValue());
            }
         }
      }

      return result;
   }

   void setFocus(int handle) {
      if (VisualNodeImpl.isResolvedDisplayable(handle, this._modelInteractor)) {
         this._currentFocus = handle;
         this._focusGroup.setFocus(handle);
         this._focusInteractor.setFocusToItem(handle);
      }
   }

   public boolean navigationMovement(int dx, int dy) {
      int adx = Math.abs(dx);
      int ady = Math.abs(dy);
      if (adx > ady) {
         dy = 0;
      } else {
         dx = 0;
      }

      return this.moveFocus(dx, dy);
   }

   boolean moveFocus(int dx, int dy) {
      if (this._currentFocus == -1) {
         int focus = this._focusInteractor.getItemInFocus();
         this._focusGroup.setFocus(focus);
         this._currentFocus = focus;
      }

      int oldFocus = this._currentFocus;
      this._vector._dx = dx;
      this._vector._dy = dy;
      this._focusGroup.moveFocus(this._modelInteractor, this._vector);
      int newFocus = this._focusGroup.getCurrentFocus();
      if (newFocus != oldFocus) {
         if (!VisualNodeImpl.isResolvedDisplayable(newFocus, this._modelInteractor)) {
            this._focusGroup.setFocus(oldFocus);
            return false;
         } else {
            this._focusInteractor.setFocusToItem(newFocus);
            this._focusGroup.triggerCustomEvent(this._modelInteractor);
            this._currentFocus = newFocus;
            return true;
         }
      } else {
         return false;
      }
   }

   void parseXml(Object content) {
      if (content instanceof byte[]) {
         byte[] data = (byte[])content;
         this.parseXml((InputStream)(new Object(data)));
      }

      if (content instanceof Object) {
         InputStream stream = (InputStream)content;
         this.parseXml(stream);
      }

      if (content instanceof Object) {
         InputStream stream = (InputStream)(new Object(((String)content).getBytes()));
         this.parseXml(stream);
      }
   }

   public void parseXml(InputStream param1) {
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
   public void characters(char[] ch, int start, int length) {
   }

   @Override
   public void endDocument() {
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (localName.equalsIgnoreCase("group")) {
         this._stack.pop();
      }
   }

   @Override
   public void error(SAXParseException e) {
   }

   @Override
   public void fatalError(SAXParseException e) throws SAXParseException {
      throw e;
   }

   @Override
   public void ignorableWhitespace(char[] ch, int start, int length) {
   }

   @Override
   public void notationDecl(String name, String publicId, String systemId) {
   }

   @Override
   public void processingInstruction(String target, String data) {
   }

   @Override
   public InputSource resolveEntity(String publicId, String systemId) {
      return null;
   }

   @Override
   public void setDocumentLocator(Locator locator) {
   }

   @Override
   public void startDocument() {
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
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
            ((FocusGroup)this._stack.peek()).addLeaf(this._modelInteractor, attributes);
         }
      }
   }
}
