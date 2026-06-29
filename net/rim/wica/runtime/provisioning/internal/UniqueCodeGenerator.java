package net.rim.wica.runtime.provisioning.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import java.util.Random;
import net.rim.device.api.compress.GZIPInputStream;
import net.rim.device.api.compress.GZIPOutputStream;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.ToIntHashtable;

public class UniqueCodeGenerator {
   IntHashtable _codeToName;
   ToIntHashtable _collisionCodes;
   ToIntHashtable _nameToCode;
   Random _r;
   ToIntHashtable _standardComponents;

   public UniqueCodeGenerator(ToIntHashtable standardComponentCodes) {
      if (standardComponentCodes != null && !standardComponentCodes.isEmpty()) {
         this._standardComponents = standardComponentCodes;
         this._collisionCodes = new ToIntHashtable();
         this._nameToCode = new ToIntHashtable();
         this._codeToName = new IntHashtable();
         this._r = new Random();
         this.storeReservedKeywords();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void clear() {
      this._collisionCodes = new ToIntHashtable();
      this._nameToCode = new ToIntHashtable();
      this._codeToName = new IntHashtable();
      this.storeReservedKeywords();
   }

   public int generateCode(String s) {
      int code = -1;
      if (this.isStandardComponent(s)) {
         return this._standardComponents.get(s);
      }

      code = this._nameToCode.get(s);
      if (code == -1) {
         code = s.hashCode();
         boolean isStored = this._codeToName.containsKey(code);
         if (!this.isStandardComponentCode(code) && !isStored) {
            this.storeCode(code, s);
            return code;
         }

         code = this.generateUniqueCode(code);
         this.storeCode(code, s);
         this._collisionCodes.put(s, code);
      }

      return code;
   }

   public ToIntHashtable getCollisionCodes() {
      return this._collisionCodes;
   }

   public boolean isStandardComponent(String name) {
      return this._standardComponents.containsKey(name);
   }

   public void setCollisionTable(ToIntHashtable collisionCodes) {
      this._collisionCodes = collisionCodes;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void setMappingTable(byte[] compressedTable) {
      this.clear();
      ByteArrayInputStream bs = new ByteArrayInputStream(compressedTable);
      GZIPInputStream gzips = new GZIPInputStream(bs);
      DataInputStream dis = new DataInputStream(gzips);

      try {
         while (dis.available() != -1) {
            byte[] stringBytes = new byte[dis.readInt()];
            dis.readFully(stringBytes);
            this.storeCode(dis.readInt(), new String(stringBytes));
         }
      } catch (Throwable var7) {
         e.printStackTrace();
         return;
      }
   }

   public byte[] getMappingTable() {
      ByteArrayOutputStream bs = new ByteArrayOutputStream();
      GZIPOutputStream gzips = new GZIPOutputStream(bs);
      DataOutputStream ds = new DataOutputStream(gzips);
      Enumeration e = this._nameToCode.keys();

      try {
         while (e.hasMoreElements()) {
            String str = (String)e.nextElement();
            ds.writeInt(str.length());
            ds.write(str.getBytes());
            ds.writeInt(this._nameToCode.get(str));
         }

         ds.flush();
         ds.close();
      } finally {
         return bs.toByteArray();
      }

      return bs.toByteArray();
   }

   @Override
   public String toString() {
      int size = this._nameToCode.size() * 16 + this._collisionCodes.size() * 16;
      StringBuffer buffer = new StringBuffer(size);
      buffer.append("UniqueCodeGenerator[codes:");
      Enumeration e = this._nameToCode.keys();
      String name = null;
      int code = 0;

      while (e.hasMoreElements()) {
         name = (String)e.nextElement();
         code = this._nameToCode.get(name);
         buffer.append(name).append('=').append(code).append('\n');
      }

      buffer.append(",collisions:");
      if (this._collisionCodes.isEmpty()) {
         buffer.append("none");
      } else {
         e = this._collisionCodes.keys();

         while (e.hasMoreElements()) {
            name = (String)e.nextElement();
            code = this._collisionCodes.get(name);
            buffer.append(name).append('=').append(code).append('\n');
         }
      }

      buffer.append(']');
      return buffer.toString();
   }

   private boolean alreadyGeneratedCode(int code) {
      return code == -1 || this._codeToName.containsKey(code);
   }

   private int generateUniqueCode(int code) {
      while (this.isStandardComponentCode(code) || this.alreadyGeneratedCode(code)) {
         code += this._r.nextInt();
      }

      return code;
   }

   private boolean isStandardComponentCode(int code) {
      return this._standardComponents.contains(code);
   }

   private void storeCode(int code, String s) {
      this._nameToCode.put(s, code);
      this._codeToName.put(code, s);
   }

   private void storeReservedKeywords() {
      String reserved = "onError";
      this.storeCode(reserved.hashCode(), reserved);
      reserved = "onExit";
      this.storeCode(reserved.hashCode(), reserved);
   }
}
