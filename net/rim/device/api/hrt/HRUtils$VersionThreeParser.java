package net.rim.device.api.hrt;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEFieldController;

class HRUtils$VersionThreeParser implements TLEFieldController {
   private int _type;
   private int _art;
   private HostRoutingInfo _gprsHri;
   private HostRoutingInfo _cdmaHri;
   private HostRoutingInfo _idenHri;
   private HostRoutingInfo _wifiHri;
   private HRUtils$ClonedHriInfo _cloneInfo;
   public static final int ROUTING_INFO_FIELD = 64;
   public static final int CLONED_ROUTING_INFO_FIELD = 65;
   private static final int PRIMARY_NPC_FIELD = 1;
   private static final int CLONED_NPC_FIELD = 2;
   private static final int PRIMARY_ART_FIELD = 3;

   public Object getResult(int waf) {
      switch (this._type) {
         case 63:
            return null;
         case 64:
         default:
            HostRoutingInfo hri = null;
            if (waf == 1) {
               return this._gprsHri;
            } else if (waf == 2) {
               return this._cdmaHri;
            } else {
               if (waf == 8) {
                  return this._idenHri;
               }

               if (waf == 4) {
                  hri = this._wifiHri;
               }

               return hri;
            }
         case 65:
            return this._cloneInfo;
      }
   }

   public void reset(int type) {
      this._type = type;
      this._art = 0;
      this._gprsHri = HRUtils.newHriByWAF(1);
      this._cdmaHri = HRUtils.newHriByWAF(2);
      this._idenHri = HRUtils.newHriByWAF(8);
      this._wifiHri = HRUtils.newHriByWAF(4);
      this._cloneInfo = null;
   }

   public int getArt() {
      return this._art;
   }

   @Override
   public boolean processField(int type, int length, DataBuffer db) {
      switch (this._type) {
         case 63:
            return false;
         case 64:
         default:
            if (type != 32) {
               if (type == 22) {
                  if (length <= 16) {
                     byte[] art = new byte[length];
                     if (db.read(art, 0, length) == length) {
                        this._art = art[length - 1];
                        this._gprsHri.setArt(this._art);
                        this._cdmaHri.setArt(this._art);
                        this._idenHri.setArt(this._art);
                        this._wifiHri.setArt(this._art);
                        return true;
                     }
                  }

                  throw new IllegalArgumentException();
               }

               int startPos = db.getPosition();
               boolean result = this._gprsHri.parseField(type, length, db);
               db.setPosition(startPos);
               result |= this._cdmaHri.parseField(type, length, db);
               db.setPosition(startPos);
               result |= this._idenHri.parseField(type, length, db);
               db.setPosition(startPos);
               result |= this._wifiHri.parseField(type, length, db);
               if (result) {
                  db.setPosition(startPos);
                  db.skipBytes(length);
               }

               return result;
            } else {
               int npc = db.readInt();
               this._gprsHri.setNpc(npc);
               this._cdmaHri.setNpc(npc);
               this._idenHri.setNpc(npc);
               this._wifiHri.setNpc(npc);
               if (this._art == 0) {
                  long npcBase = npc & 4294967295L & 252;
                  if (npcBase == 48) {
                     this._art = 7;
                  } else if (npcBase == 64) {
                     this._art = 16;
                  } else if (npcBase == 80) {
                     this._art = 32;
                  } else {
                     if (npcBase != 96) {
                        throw new IllegalArgumentException();
                     }

                     this._art = 8;
                  }

                  this._gprsHri.setArt(this._art);
                  this._cdmaHri.setArt(this._art);
                  this._idenHri.setArt(this._art);
                  this._wifiHri.setArt(this._art);
               }

               return true;
            }
         case 65:
            if (this._cloneInfo == null) {
               this._cloneInfo = new HRUtils$ClonedHriInfo();
            }

            switch (type) {
               case 0:
                  return false;
               case 1:
               default:
                  if (this._cloneInfo.baseNpc != -1) {
                     throw new IllegalArgumentException();
                  } else {
                     this._cloneInfo.baseNpc = db.readLong();
                     if (this._art == 0) {
                        long npcBase = this._cloneInfo.baseNpc & 4294967295L & 252;
                        if (npcBase == 48) {
                           this._art = 7;
                        } else if (npcBase == 64) {
                           this._art = 16;
                        } else if (npcBase == 80) {
                           this._art = 32;
                        } else {
                           if (npcBase != 96) {
                              throw new IllegalArgumentException();
                           }

                           this._art = 8;
                        }

                        this._cloneInfo.baseArt = this._art;
                     }

                     return true;
                  }
               case 2:
                  this._cloneInfo.clonedNpcs.addElement(new Long(db.readLong()));
                  return true;
               case 3:
                  if (length <= 16) {
                     byte[] art = new byte[length];
                     if (db.read(art, 0, length) == length) {
                        this._art = art[length - 1];
                        this._cloneInfo.baseArt = this._art;
                        return true;
                     }
                  }

                  throw new IllegalArgumentException();
            }
      }
   }

   @Override
   public void dumpField(int type, DataBuffer db) {
   }
}
