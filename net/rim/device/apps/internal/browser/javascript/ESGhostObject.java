package net.rim.device.apps.internal.browser.javascript;

import java.util.Hashtable;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.internal.browser.util.Frame;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;

public class ESGhostObject extends RedirectedObject {
   private Hashtable _fieldsByName = new Hashtable();
   private IntHashtable _fieldsByIndex = new IntHashtable();
   private int _type;
   private Frame _frame;
   static final int WINDOW = 0;
   static final int DOCUMENT = 1;
   static final int LOCATION = 2;
   static final int FRAMES = 3;
   static final Long UNDEFINED = new Long(Value.UNDEFINED);

   public ESGhostObject(Frame f, int type) {
      this._frame = f;
      this._type = type;
   }

   public int getType() {
      return this._type;
   }

   @Override
   public long requestFieldValue(String name) {
      long val = Value.UNDEFINED;
      if (this._fieldsByName.containsKey(name)) {
         Object o = this._fieldsByName.get(name);
         return o == UNDEFINED ? Value.UNDEFINED : Value.makeObjectValue((ESObject)o);
      }

      switch (this._type) {
         case -1:
         case 2:
            val = Value.UNDEFINED;
            break;
         case 0:
         default:
            if (name.equalsIgnoreCase(Names.parent)) {
               Frame pFrame = this._frame.getParent();
               if (pFrame == null) {
                  val = Value.UNDEFINED;
                  this._fieldsByName.put(name, UNDEFINED);
                  return Value.UNDEFINED;
               }

               ESGhostObject ghostObject = new ESGhostObject(pFrame, 0);
               this._fieldsByName.put(name, ghostObject);
               this._fieldsByIndex.put(this.getFieldIndex(name), ghostObject);
               return Value.makeObjectValue(ghostObject);
            }

            if (name.equalsIgnoreCase(Names.frames)) {
               ESGhostObject ghostObject = new ESGhostObject(this._frame, 3);
               val = Value.makeObjectValue(ghostObject);
               this._fieldsByName.put(name, ghostObject);
               this._fieldsByIndex.put(this.getFieldIndex(name), ghostObject);
               return val;
            }

            if (name.equalsIgnoreCase(Names.document)) {
               ESGhostObject ghostObject = new ESGhostObject(this._frame, 1);
               val = Value.makeObjectValue(ghostObject);
               this._fieldsByName.put(name, ghostObject);
               this._fieldsByIndex.put(this.getFieldIndex(name), ghostObject);
               return val;
            }

            if (name.equalsIgnoreCase(Names.location)) {
               ESGhostObject ghostObject = new ESGhostObject(this._frame, 2);
               val = Value.makeObjectValue(ghostObject);
               this._fieldsByName.put(name, ghostObject);
               this._fieldsByIndex.put(this.getFieldIndex(name), ghostObject);
               return val;
            }

            if (name.equalsIgnoreCase(Names.top)) {
               ESGhostObject ghostObject = new ESGhostObject(this._frame.getTop(), 0);
               this._fieldsByName.put(name, ghostObject);
               this._fieldsByIndex.put(this.getFieldIndex(name), ghostObject);
               return Value.makeObjectValue(ghostObject);
            }

            long value = this._frame.getESFrames().getWindow().requestFieldValue(name);
            if (value == Value.UNDEFINED || value == Value.DEFAULT) {
               return value;
            }

            ESObject esobject = Convert.toObject(value);
            if (!(esobject instanceof ESWindow) && !(esobject instanceof ESFrames) && !(esobject instanceof ESHTMLDocument)) {
               return Value.UNDEFINED;
            }

            if (esobject instanceof ESWindow) {
               ESWindow window = (ESWindow)esobject;
               ESGhostObject ghostObject = new ESGhostObject(window.getFrame(), 0);
               this._fieldsByName.put(name, ghostObject);
               this._fieldsByIndex.put(this.getFieldIndex(name), ghostObject);
               return Value.makeObjectValue(ghostObject);
            }
            break;
         case 1:
            if (!name.equalsIgnoreCase(Names.location)) {
               return Value.UNDEFINED;
            }
            break;
         case 3:
            val = this._frame.getESFrames().getWindow().requestFieldValue(name);
            if (val == Value.UNDEFINED) {
               return val;
            }

            ESGhostObject ghostObject = new ESGhostObject(this._frame, 3);
            this._fieldsByName.put(name, ghostObject);
            this._fieldsByIndex.put(this.getFieldIndex(name), ghostObject);
            return val;
      }

      return val;
   }

   @Override
   public long requestElementValue(long element) {
      long val = Value.UNDEFINED;

      try {
         int type = Value.getType(element);
         if (type != 0) {
            return Value.UNDEFINED;
         }

         int index = (int)element;
         if (this._fieldsByIndex.containsKey(index)) {
            Object o = this._fieldsByIndex.get(index);
            return o == UNDEFINED ? Value.UNDEFINED : Value.makeObjectValue((ESObject)o);
         }

         long esobjectVal = this._frame.getESFrames().getWindow().requestElementValue(element);
         if (esobjectVal != Value.UNDEFINED && esobjectVal != Value.DEFAULT) {
            ESObject esobject = Convert.toObject(esobjectVal);
            ESGhostObject ghostObject = null;
            String name = null;
            switch (this._type) {
               case -1:
               case 2:
                  break;
               case 0:
               default:
                  if (!(esobject instanceof ESWindow) && !(esobject instanceof ESFrames) && !(esobject instanceof ESHTMLDocument)) {
                     return Value.UNDEFINED;
                  }

                  name = this.getFieldName(index);
                  if (name.equalsIgnoreCase(Names.parent)) {
                     ghostObject = new ESGhostObject(this._frame, 0);
                  } else if (name.equalsIgnoreCase(Names.frames)) {
                     ghostObject = new ESGhostObject(this._frame, 3);
                  } else if (name.equalsIgnoreCase(Names.document)) {
                     ghostObject = new ESGhostObject(this._frame, 1);
                  }

                  this._fieldsByName.put(name, ghostObject);
                  this._fieldsByIndex.put(index, ghostObject);
                  val = esobjectVal;
                  break;
               case 1:
                  val = Value.UNDEFINED;
                  break;
               case 3:
                  if (!(esobject instanceof ESWindow)) {
                     return Value.UNDEFINED;
                  }

                  ghostObject = new ESGhostObject(this._frame, 0);
                  name = this.getFieldName(index);
                  this._fieldsByName.put(name, ghostObject);
                  this._fieldsByIndex.put(index, ghostObject);
                  val = esobjectVal;
            }

            return val;
         } else {
            return esobjectVal;
         }
      } finally {
         ;
      }
   }

   @Override
   public boolean putField(String name, long value) {
      boolean ret = false;
      if (name.equalsIgnoreCase(Names.href) && this._type == 2) {
         ESLocation location = this._frame.getESFrames().getWindow().getLocation();
         location.putField(name, value);
         ret = true;
      }

      return ret;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean putElement(long element, long value) {
      switch (this._type) {
         case 0:
         case 2:
            try {
               long val = this._frame.getESFrames().getWindow().requestElementValue(element);
               ESObject esobject = Convert.toObject(val);
               if (!(esobject instanceof ESLocation)) {
                  return false;
               }

               ((ESLocation)esobject).putElement(element, value);
               return true;
            } catch (Throwable var9) {
               ex.printStackTrace();
               return false;
            }
         default:
            return false;
      }
   }
}
