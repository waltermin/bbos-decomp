package net.rim.device.apps.internal.browser.options;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.apps.internal.browser.bookmark.BookmarksFolderList;
import net.rim.device.apps.internal.browser.history.History;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.FontCache;

public final class BrowserOptionsSync extends OTASyncCapableSyncItem implements BrowserOptionsChangeListener {
   private boolean _fireUpdates = true;
   private static String BROWSER_OPTIONS_NAME = "Browser Options";
   private static final int VERSION = 28;
   private static final int EMBEDDED_RICH_CONTENT_FIELD = 1;
   private static final int DEFAULT_FONT_SIZE_FIELD = 2;
   private static final int DEFAULT_FONT_FAMILY_FIELD = 3;
   private static final int SHOW_FULL_SCREEN_FIELD = 4;
   private static final int CONFIRM_LEAVE_MODIFIED_PAGE_FIELD = 5;
   private static final int JAVASCRIPT_ENABLED_OVERRIDE_FIELD = 6;
   private static final int ALLOW_POPUP_ENABLED_OVERRIDE_FIELD = 7;
   private static final int USE_FOREGROUND_BACKGROUND_COLOR_OVERRIDE_FIELD = 8;
   private static final int USE_BACKGROUND_IMAGES_OVERRIDE_FIELD = 9;
   private static final int USE_HTML_TABLE_SUPPORT_OVERRIDE_FIELD = 10;
   private static final int ENABLE_CSS_OVERRIDE_FIELD = 11;
   private static final int CSS_MEDIA_TYPE_OVERRIDE_FIELD = 12;
   private static final int ENABLE_EMBEDDED_RICH_CONTENT_OVERRIDE_FIELD = 13;
   private static final int JAVASCRIPT_LOCATION_ENABLED_FIELD = 14;
   private static final int PAGE_CACHE_SIZE_FIELD = 15;
   private static final int MINIMUM_FONT_SIZE_FIELD = 16;
   private static final int MINIMUM_FONT_STYLE_FIELD = 17;
   private static final int ENABLE_BSM_OVERRIDE_FIELD = 18;
   private static final int CONFIG_VALUES_EDITABLE_OVERRIDE_FIELD = 19;
   private static final int AUDIO_PLAYER_VOLUME_FIELD = 20;
   private static final int STARTUP_PAGE_OVERRIDE_FIELD = 21;
   private static final int IMAGE_QUALITY_FIELD = 22;
   private static final int DEFAULT_VIEW_FIELD = 23;
   private static final int ENABLE_MOBILE_CURSOR_FIELD = 24;

   public BrowserOptionsSync() {
      GeneralProperty.addListener(this);
   }

   @Override
   public final void optionsChanged(BitSet changedOptions) {
      if (this._fireUpdates) {
         BitSet bitSet = (BitSet)(new Object(changedOptions));
         bitSet.and(GeneralProperty.BACKUP_RESTORE_MASK);
         if (bitSet.getNumSet() > 0) {
            this.fireSyncItemUpdated();
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      boolean var13 = false /* VF: Semaphore variable */;
      boolean var16 = false /* VF: Semaphore variable */;

      int size;
      label668: {
         boolean var34;
         label667: {
            label666: {
               label665: {
                  label664: {
                     label663: {
                        label662: {
                           label661: {
                              label660: {
                                 label659: {
                                    label658: {
                                       label657: {
                                          label656: {
                                             label655: {
                                                label654: {
                                                   label653: {
                                                      try {
                                                         label651:
                                                         try {
                                                            var16 = true;
                                                            var13 = true;
                                                            this._fireUpdates = false;
                                                            buffer.readShort();
                                                            buffer.readByte();
                                                            if (version < 10) {
                                                               buffer.readBoolean();
                                                            } else {
                                                               if (version < 17) {
                                                                  buffer.readCompressedInt();
                                                               }

                                                               if (version > 11) {
                                                                  if (version < 17) {
                                                                     buffer.readCompressedInt();
                                                                  }
                                                               } else {
                                                                  buffer.readBoolean();
                                                               }

                                                               if (version < 17) {
                                                                  buffer.readBoolean();
                                                               }
                                                            }

                                                            if (version < 17) {
                                                               buffer.readBoolean();
                                                               buffer.readBoolean();
                                                               buffer.readBoolean();
                                                            }

                                                            if (!GeneralProperty.setCurrentProperty(1, buffer.readBoolean())) {
                                                               size = 0;
                                                               var13 = false;
                                                               var16 = false;
                                                               break label668;
                                                            }

                                                            if (version > 2 && version < 17) {
                                                               buffer.readBoolean();
                                                            }

                                                            buffer.readUTF();
                                                            if (version < 14) {
                                                               buffer.readCompressedInt();
                                                               size = buffer.readCompressedInt();
                                                               if (size == 0 && !GeneralProperty.setCurrentProperty(31, 0)) {
                                                                  var34 = false;
                                                                  var13 = false;
                                                                  var16 = false;
                                                                  break label667;
                                                               }
                                                            } else {
                                                               buffer.readCompressedInt();
                                                               if (!GeneralProperty.setCurrentProperty(31, buffer.readCompressedInt())) {
                                                                  size = 0;
                                                                  var13 = false;
                                                                  var16 = false;
                                                                  break label666;
                                                               }
                                                            }

                                                            buffer.readCompressedInt();
                                                            GeneralProperty.setCurrentProperty(30, buffer.readBoolean());
                                                            buffer.readBoolean();
                                                            if (!History.skipData(buffer, version)) {
                                                               size = 0;
                                                               var13 = false;
                                                               var16 = false;
                                                               break label665;
                                                            }

                                                            buffer.readCompressedLong();
                                                            if (!PushProperty.initialize(buffer, version)) {
                                                               size = 0;
                                                               var13 = false;
                                                               var16 = false;
                                                               break label664;
                                                            }

                                                            if (version > 4) {
                                                               if (version < 15) {
                                                                  buffer.readCompressedInt();
                                                               }

                                                               if (version < 16) {
                                                                  buffer.readCompressedInt();
                                                               }
                                                            }

                                                            if (version > 5 && version < 17) {
                                                               buffer.readBoolean();
                                                            }

                                                            if (version > 6) {
                                                               buffer.readBoolean();
                                                            }

                                                            if (version > 7 && version < 17) {
                                                               buffer.readCompressedInt();
                                                            }

                                                            if (version > 8 && !GeneralProperty.setCurrentProperty(0, buffer.readBoolean())) {
                                                               size = 0;
                                                               var13 = false;
                                                               var16 = false;
                                                               break label663;
                                                            }

                                                            if (version > 10 && !this.readHomePageUrlOverrides(buffer)) {
                                                               size = 0;
                                                               var13 = false;
                                                               var16 = false;
                                                               break label662;
                                                            }

                                                            if (version > 14 && !this.readIntOverrides(18, buffer)) {
                                                               size = 0;
                                                               var13 = false;
                                                               var16 = false;
                                                               break label661;
                                                            }

                                                            if (version > 15 && !this.readIntOverrides(19, buffer)) {
                                                               size = 0;
                                                               var13 = false;
                                                               var16 = false;
                                                               break label660;
                                                            }

                                                            if (version > 16) {
                                                               if (!this.readIntOverrides(16, buffer)) {
                                                                  size = 0;
                                                                  var13 = false;
                                                                  var16 = false;
                                                                  break label659;
                                                               }

                                                               if (!this.readIntOverrides(17, buffer)) {
                                                                  size = 0;
                                                                  var13 = false;
                                                                  var16 = false;
                                                                  break label658;
                                                               }
                                                            }

                                                            if (version > 17) {
                                                               if (!GeneralProperty.setCurrentProperty(14, buffer.readCompressedInt())) {
                                                                  size = 0;
                                                                  var13 = false;
                                                                  var16 = false;
                                                                  break label657;
                                                               }

                                                               String charset = buffer.readUTF();
                                                               if (charset != null && charset.length() == 0) {
                                                                  charset = null;
                                                               }

                                                               if (!GeneralProperty.setDefaultCharsetValue(charset)) {
                                                                  var34 = false;
                                                                  var13 = false;
                                                                  var16 = false;
                                                                  break label656;
                                                               }
                                                            }

                                                            if (version > 18) {
                                                               buffer.readBoolean();
                                                            }

                                                            if (version > 19) {
                                                               buffer.readBoolean();
                                                            }

                                                            if (version > 20) {
                                                               GeneralProperty.setPreferredBrowserConfigServiceUID(buffer.readUTF());
                                                               GeneralProperty.setPreferredMdsBrowserConfigServiceUID(buffer.readUTF());
                                                               GeneralProperty.setPreferredWapBrowserConfigServiceUID(buffer.readUTF());
                                                            }

                                                            if (version > 21 && !GeneralProperty.setCurrentProperty(2, buffer.readCompressedInt())) {
                                                               size = 0;
                                                               var13 = false;
                                                               var16 = false;
                                                               break label655;
                                                            }

                                                            if (version > 22) {
                                                               buffer.readBoolean();
                                                               buffer.readBoolean();
                                                               buffer.readBoolean();
                                                            }

                                                            if (version > 23 && !this.readAuthenticationCredentials(buffer)) {
                                                               size = 0;
                                                               var13 = false;
                                                               var16 = false;
                                                               break label654;
                                                            }

                                                            if (version > 24) {
                                                               buffer.readBoolean();
                                                               buffer.readCompressedInt();
                                                            }

                                                            if (version <= 25) {
                                                               var13 = false;
                                                               var16 = false;
                                                            } else {
                                                               DataBuffer tmpDataBuffer = (DataBuffer)(new Object());

                                                               while (!buffer.eof()) {
                                                                  int type;
                                                                  if ((type = buffer.readUnsignedByte()) == 0) {
                                                                     var13 = false;
                                                                     var16 = false;
                                                                     break label653;
                                                                  }

                                                                  switch (type) {
                                                                     case 1:
                                                                        buffer.skipBytes(buffer.readCompressedInt());
                                                                        break;
                                                                     case 2:
                                                                     default:
                                                                        GeneralProperty.setCurrentProperty(27, TLEUtilities.readIntegerField(buffer));
                                                                        break;
                                                                     case 3:
                                                                        String fontValue = TLEUtilities.readStringField(buffer, true);
                                                                        if (version < 27 && FontCache.isTrueTypeFontAvailable()) {
                                                                           fontValue = FontCache.getInstance().getDefaultFontFamily();
                                                                        }

                                                                        GeneralProperty.setDefaultFontFamily(fontValue);
                                                                        break;
                                                                     case 4:
                                                                        GeneralProperty.setCurrentProperty(29, TLEUtilities.readIntegerField(buffer) == 1);
                                                                        break;
                                                                     case 5:
                                                                        GeneralProperty.setCurrentProperty(24, TLEUtilities.readIntegerField(buffer) == 1);
                                                                        break;
                                                                     case 6:
                                                                        byte[] fieldData = TLEUtilities.readDataField(buffer);
                                                                        tmpDataBuffer.setData(fieldData, 0, fieldData.length);
                                                                        this.readIntOverrides(21, tmpDataBuffer);
                                                                        break;
                                                                     case 7:
                                                                        byte[] var44 = TLEUtilities.readDataField(buffer);
                                                                        tmpDataBuffer.setData(var44, 0, var44.length);
                                                                        this.readIntOverrides(22, tmpDataBuffer);
                                                                        break;
                                                                     case 8:
                                                                        byte[] var43 = TLEUtilities.readDataField(buffer);
                                                                        tmpDataBuffer.setData(var43, 0, var43.length);
                                                                        this.readIntOverrides(8, tmpDataBuffer);
                                                                        break;
                                                                     case 9:
                                                                        byte[] var42 = TLEUtilities.readDataField(buffer);
                                                                        tmpDataBuffer.setData(var42, 0, var42.length);
                                                                        this.readIntOverrides(7, tmpDataBuffer);
                                                                        break;
                                                                     case 10:
                                                                        byte[] var41 = TLEUtilities.readDataField(buffer);
                                                                        tmpDataBuffer.setData(var41, 0, var41.length);
                                                                        this.readIntOverrides(4, tmpDataBuffer);
                                                                        break;
                                                                     case 11:
                                                                        byte[] var40 = TLEUtilities.readDataField(buffer);
                                                                        tmpDataBuffer.setData(var40, 0, var40.length);
                                                                        this.readIntOverrides(6, tmpDataBuffer);
                                                                        break;
                                                                     case 12:
                                                                        byte[] var39 = TLEUtilities.readDataField(buffer);
                                                                        tmpDataBuffer.setData(var39, 0, var39.length);
                                                                        this.readIntOverrides(11, tmpDataBuffer);
                                                                        break;
                                                                     case 13:
                                                                        byte[] var38 = TLEUtilities.readDataField(buffer);
                                                                        tmpDataBuffer.setData(var38, 0, var38.length);
                                                                        this.readIntOverrides(25, tmpDataBuffer);
                                                                        break;
                                                                     case 14:
                                                                        GeneralProperty.setCurrentProperty(23, TLEUtilities.readIntegerField(buffer) == 1);
                                                                        break;
                                                                     case 15:
                                                                        GeneralProperty.setCurrentProperty(
                                                                           34, MathUtilities.clamp(0, TLEUtilities.readIntegerField(buffer), 5)
                                                                        );
                                                                        break;
                                                                     case 16:
                                                                        GeneralProperty.setCurrentProperty(36, TLEUtilities.readIntegerField(buffer));
                                                                        break;
                                                                     case 17:
                                                                        GeneralProperty.setCurrentProperty(37, TLEUtilities.readIntegerField(buffer));
                                                                        break;
                                                                     case 18:
                                                                        byte[] var37 = TLEUtilities.readDataField(buffer);
                                                                        tmpDataBuffer.setData(var37, 0, var37.length);
                                                                        this.readIntOverrides(38, tmpDataBuffer);
                                                                        break;
                                                                     case 19:
                                                                        byte[] var36 = TLEUtilities.readDataField(buffer);
                                                                        tmpDataBuffer.setData(var36, 0, var36.length);
                                                                        this.readIntOverrides(39, tmpDataBuffer);
                                                                        break;
                                                                     case 20:
                                                                        GeneralProperty.setCurrentProperty(40, TLEUtilities.readIntegerField(buffer));
                                                                        break;
                                                                     case 21:
                                                                        byte[] var35 = TLEUtilities.readDataField(buffer);
                                                                        tmpDataBuffer.setData(var35, 0, var35.length);
                                                                        this.readIntOverrides(41, tmpDataBuffer);
                                                                        break;
                                                                     case 22:
                                                                        int imgQuality = MathUtilities.clamp(0, TLEUtilities.readIntegerField(buffer), 2);
                                                                        GeneralProperty.setCurrentProperty(42, imgQuality);
                                                                        break;
                                                                     case 23:
                                                                        int viewValue = TLEUtilities.readIntegerField(buffer);
                                                                        if (version == 27 && viewValue == 2) {
                                                                           viewValue = 0;
                                                                        }

                                                                        GeneralProperty.setCurrentProperty(43, MathUtilities.clamp(0, viewValue, 2));
                                                                        break;
                                                                     case 24:
                                                                        int enabled = TLEUtilities.readIntegerField(buffer);
                                                                        GeneralProperty.setCurrentProperty(48, enabled != 0);
                                                                  }
                                                               }

                                                               var13 = false;
                                                               var16 = false;
                                                            }
                                                            break label653;
                                                         } finally {
                                                            if (var16) {
                                                               var34 = false;
                                                               var13 = false;
                                                               break label651;
                                                            }
                                                         }
                                                      } finally {
                                                         if (var13) {
                                                            this._fireUpdates = true;
                                                         }
                                                      }

                                                      this._fireUpdates = true;
                                                      return var34;
                                                   }

                                                   this._fireUpdates = true;
                                                   this.fireSyncItemUpdated();
                                                   return true;
                                                }

                                                this._fireUpdates = true;
                                                return (boolean)size;
                                             }

                                             this._fireUpdates = true;
                                             return (boolean)size;
                                          }

                                          this._fireUpdates = true;
                                          return var34;
                                       }

                                       this._fireUpdates = true;
                                       return (boolean)size;
                                    }

                                    this._fireUpdates = true;
                                    return (boolean)size;
                                 }

                                 this._fireUpdates = true;
                                 return (boolean)size;
                              }

                              this._fireUpdates = true;
                              return (boolean)size;
                           }

                           this._fireUpdates = true;
                           return (boolean)size;
                        }

                        this._fireUpdates = true;
                        return (boolean)size;
                     }

                     this._fireUpdates = true;
                     return (boolean)size;
                  }

                  this._fireUpdates = true;
                  return (boolean)size;
               }

               this._fireUpdates = true;
               return (boolean)size;
            }

            this._fireUpdates = true;
            return (boolean)size;
         }

         this._fireUpdates = true;
         return var34;
      }

      this._fireUpdates = true;
      return (boolean)size;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      DataBuffer tmpBuffer = (DataBuffer)(new Object(buffer.isBigEndian()));

      try {
         tmpBuffer.writeBoolean(GeneralProperty.getCurrentPropertyAsBoolean(1));
         tmpBuffer.writeUTF("");
         tmpBuffer.writeCompressedInt(0);
         tmpBuffer.writeCompressedInt(GeneralProperty.getCurrentPropertyAsInt(31));
         tmpBuffer.writeCompressedInt(10);
         tmpBuffer.writeBoolean(GeneralProperty.getCurrentPropertyAsBoolean(30));
         tmpBuffer.writeBoolean(false);
         History.serialize(tmpBuffer);
         tmpBuffer.writeCompressedLong(BookmarksFolderList.getDefaultFolderID());
         PushProperty.serialize(tmpBuffer);
         tmpBuffer.writeBoolean(true);
         tmpBuffer.writeBoolean(GeneralProperty.getCurrentPropertyAsBoolean(0));
         this.writeHomePageUrlOverrides(tmpBuffer);
         this.writeIntOverrides(18, tmpBuffer);
         this.writeIntOverrides(19, tmpBuffer);
         this.writeIntOverrides(16, tmpBuffer);
         this.writeIntOverrides(17, tmpBuffer);
         tmpBuffer.writeCompressedInt(GeneralProperty.getDefaultCharsetModeValue());
         String charset = GeneralProperty.getDefaultCharsetValue();
         if (charset == null) {
            charset = "";
         }

         tmpBuffer.writeUTF(charset);
         tmpBuffer.writeBoolean(true);
         tmpBuffer.writeBoolean(false);
         String uid = GeneralProperty.getDefaultBrowserConfigServiceUID();
         if (uid == null) {
            uid = "";
         }

         tmpBuffer.writeUTF(uid);
         uid = GeneralProperty.getDefaultMdsBrowserConfigServiceUID();
         if (uid == null) {
            uid = "";
         }

         tmpBuffer.writeUTF(uid);
         uid = GeneralProperty.getDefaultWapBrowserConfigServiceUID();
         if (uid == null) {
            uid = "";
         }

         tmpBuffer.writeUTF(uid);
         tmpBuffer.writeCompressedInt(GeneralProperty.getCurrentPropertyAsInt(2));
         tmpBuffer.writeBoolean(true);
         tmpBuffer.writeBoolean(true);
         tmpBuffer.writeBoolean(true);
         this.writeAuthenticationCredentials(tmpBuffer);
         tmpBuffer.writeBoolean(true);
         tmpBuffer.writeCompressedInt(0);
         TLEUtilities.writeStringField(tmpBuffer, 3, GeneralProperty.getDefaultFontFamily());
         TLEUtilities.writeIntegerField(tmpBuffer, 2, GeneralProperty.getCurrentPropertyAsInt(27), false);
         TLEUtilities.writeIntegerField(tmpBuffer, 4, GeneralProperty.getCurrentPropertyAsBoolean(29) ? 1 : 0, false);
         TLEUtilities.writeIntegerField(tmpBuffer, 5, GeneralProperty.getCurrentPropertyAsBoolean(24) ? 1 : 0, false);
         TLEUtilities.writeIntegerField(tmpBuffer, 14, GeneralProperty.getCurrentPropertyAsBoolean(23) ? 1 : 0, false);
         TLEUtilities.writeIntegerField(tmpBuffer, 15, GeneralProperty.getCurrentPropertyAsInt(34), false);
         TLEUtilities.writeIntegerField(tmpBuffer, 16, GeneralProperty.getCurrentPropertyAsInt(36), false);
         TLEUtilities.writeIntegerField(tmpBuffer, 17, GeneralProperty.getCurrentPropertyAsInt(37), false);
         TLEUtilities.writeIntegerField(tmpBuffer, 20, GeneralProperty.getCurrentPropertyAsInt(40), false);
         DataBuffer tmpBuffer2 = (DataBuffer)(new Object());
         this.writeIntOverrides(21, tmpBuffer2);
         TLEUtilities.writeDataField(tmpBuffer, 6, tmpBuffer2.getArray(), tmpBuffer2.getArrayStart(), tmpBuffer2.getArrayLength());
         tmpBuffer2.reset();
         this.writeIntOverrides(22, tmpBuffer2);
         TLEUtilities.writeDataField(tmpBuffer, 7, tmpBuffer2.getArray(), tmpBuffer2.getArrayStart(), tmpBuffer2.getArrayLength());
         tmpBuffer2.reset();
         this.writeIntOverrides(8, tmpBuffer2);
         TLEUtilities.writeDataField(tmpBuffer, 8, tmpBuffer2.getArray(), tmpBuffer2.getArrayStart(), tmpBuffer2.getArrayLength());
         tmpBuffer2.reset();
         this.writeIntOverrides(7, tmpBuffer2);
         TLEUtilities.writeDataField(tmpBuffer, 9, tmpBuffer2.getArray(), tmpBuffer2.getArrayStart(), tmpBuffer2.getArrayLength());
         tmpBuffer2.reset();
         this.writeIntOverrides(4, tmpBuffer2);
         TLEUtilities.writeDataField(tmpBuffer, 10, tmpBuffer2.getArray(), tmpBuffer2.getArrayStart(), tmpBuffer2.getArrayLength());
         tmpBuffer2.reset();
         this.writeIntOverrides(6, tmpBuffer2);
         TLEUtilities.writeDataField(tmpBuffer, 11, tmpBuffer2.getArray(), tmpBuffer2.getArrayStart(), tmpBuffer2.getArrayLength());
         tmpBuffer2.reset();
         this.writeIntOverrides(11, tmpBuffer2);
         TLEUtilities.writeDataField(tmpBuffer, 12, tmpBuffer2.getArray(), tmpBuffer2.getArrayStart(), tmpBuffer2.getArrayLength());
         tmpBuffer2.reset();
         this.writeIntOverrides(25, tmpBuffer2);
         TLEUtilities.writeDataField(tmpBuffer, 13, tmpBuffer2.getArray(), tmpBuffer2.getArrayStart(), tmpBuffer2.getArrayLength());
         tmpBuffer2.reset();
         this.writeIntOverrides(38, tmpBuffer2);
         TLEUtilities.writeDataField(tmpBuffer, 18, tmpBuffer2.getArray(), tmpBuffer2.getArrayStart(), tmpBuffer2.getArrayLength());
         tmpBuffer2.reset();
         this.writeIntOverrides(39, tmpBuffer2);
         TLEUtilities.writeDataField(tmpBuffer, 19, tmpBuffer2.getArray(), tmpBuffer2.getArrayStart(), tmpBuffer2.getArrayLength());
         tmpBuffer2.reset();
         this.writeIntOverrides(41, tmpBuffer2);
         TLEUtilities.writeDataField(tmpBuffer, 21, tmpBuffer2.getArray(), tmpBuffer2.getArrayStart(), tmpBuffer2.getArrayLength());
         tmpBuffer2.reset();
         TLEUtilities.writeIntegerField(tmpBuffer, 22, GeneralProperty.getCurrentPropertyAsInt(42), false);
         TLEUtilities.writeIntegerField(tmpBuffer, 23, GeneralProperty.getCurrentPropertyAsInt(43), false);
         TLEUtilities.writeIntegerField(tmpBuffer, 24, GeneralProperty.getCurrentPropertyAsBoolean(48) ? 1 : 0, false);
      } finally {
         ;
      }

      byte[] array = tmpBuffer.toArray();
      buffer.writeShort(array.length);
      buffer.writeByte(0);
      buffer.write(array);
      return true;
   }

   private final boolean readHomePageUrlOverrides(DataBuffer buffer) {
      boolean retVal = true;
      GeneralProperty.getHomePageUrlOverrides().clear();

      try {
         int numOverrides = buffer.readCompressedInt();

         for (int i = 0; i < numOverrides; i++) {
            String uid = buffer.readUTF();
            String urlOverride = buffer.readUTF();
            if (!GeneralProperty.setHomePageUrlOverrideValue(uid, urlOverride)) {
               retVal = false;
            }
         }
      } finally {
         ;
      }

      return retVal;
   }

   private final void writeHomePageUrlOverrides(DataBuffer tmpBuffer) {
      Hashtable table = GeneralProperty.getHomePageUrlOverrides();
      int numOverrides = table.size();
      tmpBuffer.writeCompressedInt(numOverrides);
      Enumeration keys = table.keys();

      try {
         while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            tmpBuffer.writeUTF(key);
            tmpBuffer.writeUTF((String)table.get(key));
         }
      } finally {
         return;
      }
   }

   private final boolean readIntOverrides(int tableKey, DataBuffer buffer) {
      boolean retVal = true;
      GeneralProperty.clearPropertyOverrides(tableKey);

      try {
         int numOverrides = buffer.readCompressedInt();

         for (int i = 0; i < numOverrides; i++) {
            String uid = buffer.readUTF();
            int override = buffer.readCompressedInt();
            if (!GeneralProperty.setOverrideProperty(tableKey, uid, override)) {
               retVal = false;
            }
         }
      } finally {
         ;
      }

      return retVal;
   }

   private final void writeIntOverrides(int tableKey, DataBuffer tmpBuffer) {
      ToIntHashtable table = GeneralProperty.getOverridePropertiesAsInts(tableKey);
      int numOverrides = table.size();
      tmpBuffer.writeCompressedInt(numOverrides);
      Enumeration keys = table.keys();

      try {
         while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            tmpBuffer.writeUTF(key);
            tmpBuffer.writeCompressedInt(table.get(key));
         }
      } finally {
         return;
      }
   }

   private final boolean readAuthenticationCredentials(DataBuffer buffer) {
      boolean retVal = true;
      GeneralProperty.clearAuthenticationCredentials();

      try {
         int numCredentials = buffer.readCompressedInt();

         for (int i = 0; i < numCredentials; i++) {
            String realm = buffer.readUTF();
            int num = buffer.readCompressedInt();
            String[] credentials = new Object[num];

            for (int j = 0; j < num; j++) {
               credentials[j] = buffer.readUTF();
            }

            if (!GeneralProperty.setAuthenticationCredentialsValue(realm, credentials)) {
               retVal = false;
            }
         }
      } finally {
         ;
      }

      return retVal;
   }

   private final void writeAuthenticationCredentials(DataBuffer tmpBuffer) {
      Hashtable table = GeneralProperty.getAuthenticationCredentials();
      int numCredentials = table.size();
      tmpBuffer.writeCompressedInt(numCredentials);
      Enumeration keys = table.keys();

      try {
         while (keys.hasMoreElements()) {
            String realm = (String)keys.nextElement();
            tmpBuffer.writeUTF(realm);
            String[] credentials = (Object[])table.get(realm);
            tmpBuffer.writeCompressedInt(credentials.length);

            for (int i = 0; i < credentials.length; i++) {
               tmpBuffer.writeUTF(credentials[i]);
            }
         }
      } finally {
         return;
      }
   }

   @Override
   public final int getSyncVersion() {
      return 28;
   }

   @Override
   public final String getSyncName() {
      return BROWSER_OPTIONS_NAME;
   }

   @Override
   public final String getSyncName(Locale locale) {
      ResourceBundle bundle = BrowserResources.getResourceBundle().getBundle(locale);
      return bundle != null ? bundle.getString(131) : null;
   }
}
