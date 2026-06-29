package net.rim.device.resources;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.ControlledAccess;
import net.rim.vm.Array;
import net.rim.vm.Process;
import net.rim.vm.TraceBack;

public class Resource {
   private Hashtable _resources;
   private Hashtable _properties;
   private byte[] _icons;
   private String _name;

   protected Resource(Hashtable resources, Hashtable properties, byte[] icons) {
      this._resources = resources;
      this._properties = properties;
      this._icons = icons;
   }

   public static Resource getResourceClass() {
      return Resource$Internal.getResourceClass(TraceBack.getCallingModuleName(2), true);
   }

   public Enumeration getResourceKeys() {
      return this._resources != null ? this._resources.keys() : null;
   }

   private byte[] findResource(String name) {
      if (this._resources != null) {
         Object resource = this._resources.get(name);
         if (!(resource instanceof Integer)) {
            if (resource instanceof byte[]) {
               byte[] bytes = (byte[])resource;
               StringBuffer nameBuffer = new StringBuffer();

               while (true) {
                  int offset = bytes.length;
                  nameBuffer.setLength(0);
                  nameBuffer.append('_').append('_').append(name).append('@').append(offset);
                  byte[] more = (byte[])this._resources.get(nameBuffer.toString());
                  if (more == null) {
                     return bytes;
                  }

                  Array.resize(bytes, bytes.length + more.length);
                  System.arraycopy(more, 0, bytes, offset, more.length);
               }
            }
         } else {
            Integer ordinal = (Integer)resource;
            if (this._icons != null) {
               return getIconBytes(this._icons, ordinal);
            }
         }
      }

      return null;
   }

   public Vector listResourcesEndingWith(String text) {
      Vector matches = new Vector();
      if (this._resources != null) {
         Enumeration enu = this._resources.keys();

         while (enu.hasMoreElements()) {
            String key = enu.nextElement().toString();
            if (key.endsWith(text)) {
               matches.addElement(key);
            }
         }
      }

      return matches;
   }

   public byte[] getResource(String name) {
      byte[] result = this.findResource(name);
      if (result == null) {
         int firstRoot = Process.getModuleHandle(this._name);
         int secondRoot = Process.getModuleHandle(Process.currentProcess().getModuleName());
         result = Resource$Internal.FindResourceInDependencyGraph(firstRoot, secondRoot, name);
      }

      return result;
   }

   public byte[] getProperty(String name) {
      return this._properties != null ? (byte[])this._properties.get(name) : null;
   }

   public static int getIconOffset(byte[] icons, int ordinal) {
      int offset = 0;
      int length = icons.length;
      int icon_count = 0;

      try {
         while (offset < length) {
            int entrypoint_length = (icons[offset++] & 255) << 8;
            entrypoint_length += icons[offset++] & 255;

            for (int entrypoint_end = offset + entrypoint_length; offset < entrypoint_end; icon_count++) {
               int icon_length = (icons[offset++] & 255) << 8;
               icon_length += icons[offset++] & 255;
               if (icon_count == ordinal) {
                  return offset;
               }

               offset += icon_length;
            }
         }
      } catch (ArrayIndexOutOfBoundsException var8) {
      }

      throw new IllegalArgumentException();
   }

   public static int getIconLength(byte[] icons, int offset) {
      try {
         int length = ((icons[offset - 2] & 255) << 8) + (icons[offset - 1] & 255);
         if (offset + length <= icons.length) {
            return length;
         }
      } catch (ArrayIndexOutOfBoundsException var3) {
      }

      throw new IllegalArgumentException();
   }

   public static byte[] getIconBytes(byte[] icons, int ordinal) {
      int offset = getIconOffset(icons, ordinal);
      int length = getIconLength(icons, offset);
      byte[] bytes = new byte[length];
      System.arraycopy(icons, offset, bytes, 0, length);
      return bytes;
   }

   public Object instantiateMIDlet(String name) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));

      Class c;
      try {
         c = Class.forName(name);
      } catch (ClassNotFoundException cnfe) {
         throw new IllegalArgumentException();
      }

      try {
         return c.newInstance();
      } catch (Exception e) {
         throw new RuntimeException("Exception thrown in a midlet constructor.");
      }
   }
}
