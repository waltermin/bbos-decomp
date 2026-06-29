package net.rim.device.internal.util;

import java.io.IOException;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.internal.deviceoptions.DeviceOptions;
import net.rim.device.internal.deviceoptions.OptionsProvider;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeListener;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeSource;

public class OptionsRegistry implements OptionsProvider, OptionsProviderChangeSource {
   private final long _guid;
   private PersistentObject _persistent;
   private LongHashtable _persistentData;
   private Object[] _listeners;
   private OptionsProviderChangeListener _syncListener;
   private final LongHashtable _parameterDefinitions = new LongHashtable();
   private static final int TAG = 1;
   private static final int TYPE_STRING = 0;
   private static final int TYPE_INTEGER = 2;
   private static final int TYPE_LONG = 3;
   private static final int TYPE_BYTE_ARRAY = 4;
   private static final int TYPE_DOUBLE = 5;
   private static final int TYPE_BOOLEAN = 6;
   private static final int TYPE_CHAR = 7;

   public void addOptionsRegistryChangeListener(OptionsRegistry$Listener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   protected void define(long key, OptionsRegistry$ParameterDefinition def) {
      if (this._parameterDefinitions.contains(def)) {
         throw new IllegalArgumentException("duplicate definition");
      }

      this._parameterDefinitions.put(key, def);
   }

   public char getChar(long key) {
      return this.getCharParam(key).getValue();
   }

   @Override
   public int getUID() {
      return (int)this._guid;
   }

   public boolean getBoolean(long key) {
      return this.getBooleanParam(key).getValue();
   }

   public OptionsRegistry$BooleanParameter getBooleanParam(long key) {
      OptionsRegistry$BooleanParameter param = (OptionsRegistry$BooleanParameter)this.get(key);
      if (param == null) {
         OptionsRegistry$BooleanParameterDefinition pd = (OptionsRegistry$BooleanParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null) {
            throw new IllegalArgumentException();
         }

         this.setBoolean(key, pd._default);
         param = (OptionsRegistry$BooleanParameter)this.get(key);
      }

      return param;
   }

   public byte[] getByteArray(long key) {
      return this.getByteArrayParam(key).getValue();
   }

   public OptionsRegistry$ByteArrayParameter getByteArrayParam(long key) {
      OptionsRegistry$ByteArrayParameter param = (OptionsRegistry$ByteArrayParameter)this.get(key);
      if (param == null) {
         OptionsRegistry$ByteArrayParameterDefinition pd = (OptionsRegistry$ByteArrayParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null) {
            throw new IllegalArgumentException();
         }

         this.setByteArray(key, pd._default);
         param = (OptionsRegistry$ByteArrayParameter)this.get(key);
      }

      return param;
   }

   public double getDouble(long key) {
      return this.getDoubleParam(key).getValue();
   }

   public OptionsRegistry$DoubleParameter getDoubleParam(long key) {
      OptionsRegistry$DoubleParameter param = (OptionsRegistry$DoubleParameter)this.get(key);
      if (param == null) {
         OptionsRegistry$DoubleParameterDefinition pd = (OptionsRegistry$DoubleParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null) {
            throw new IllegalArgumentException();
         }

         this.setDouble(key, pd._default);
         param = (OptionsRegistry$DoubleParameter)this.get(key);
      }

      return param;
   }

   public int getInt(long key) {
      return this.getIntParam(key).getValue();
   }

   public OptionsRegistry$IntParameter getIntParam(long key) {
      OptionsRegistry$IntParameter param = (OptionsRegistry$IntParameter)this.get(key);
      if (param == null) {
         OptionsRegistry$IntParameterDefinition pd = (OptionsRegistry$IntParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null) {
            throw new IllegalArgumentException();
         }

         this.setInt(key, pd._default);
         param = (OptionsRegistry$IntParameter)this.get(key);
      }

      return param;
   }

   public LongEnumeration getKeys() {
      return this._persistentData.keys();
   }

   public long getLong(long key) {
      return this.getLongParam(key).getValue();
   }

   public OptionsRegistry$LongParameter getLongParam(long key) {
      OptionsRegistry$LongParameter param = (OptionsRegistry$LongParameter)this.get(key);
      if (param == null) {
         OptionsRegistry$LongParameterDefinition pd = (OptionsRegistry$LongParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null) {
            throw new IllegalArgumentException();
         }

         this.setLong(key, pd._default);
         param = (OptionsRegistry$LongParameter)this.get(key);
      }

      return param;
   }

   protected boolean isBackupParam(long key) {
      return true;
   }

   public void setChar(long key, char value) {
      synchronized (this._persistent) {
         OptionsRegistry$CharParameterDefinition pd = (OptionsRegistry$CharParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null || !pd.isValid(value)) {
            throw new IllegalArgumentException();
         }

         OptionsRegistry$CharParameter param = (OptionsRegistry$CharParameter)this.get(key);
         if (param == null) {
            param = new OptionsRegistry$CharParameter();
            this._persistentData.put(key, param);
         }

         param.setValue(value);
         this._persistent.commit();
      }

      this.notifyListeners(key);
   }

   public String getString(long key) {
      return this.getStringParam(key).getValue();
   }

   public OptionsRegistry$StringParameter getStringParam(long key) {
      OptionsRegistry$StringParameter param = (OptionsRegistry$StringParameter)this.get(key);
      if (param == null) {
         OptionsRegistry$StringParameterDefinition pd = (OptionsRegistry$StringParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null) {
            throw new IllegalArgumentException();
         }

         this.setString(key, pd._default);
         param = (OptionsRegistry$StringParameter)this.get(key);
      }

      return param;
   }

   public OptionsRegistry$CharParameter getCharParam(long key) {
      OptionsRegistry$CharParameter param = (OptionsRegistry$CharParameter)this.get(key);
      if (param == null) {
         OptionsRegistry$CharParameterDefinition pd = (OptionsRegistry$CharParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null) {
            throw new IllegalArgumentException();
         }

         this.setChar(key, pd._default);
         param = (OptionsRegistry$CharParameter)this.get(key);
      }

      return param;
   }

   public void removeOptionsRegistryChangeListener(OptionsRegistry$Listener listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   public void setString(long key, String value) {
      synchronized (this._persistent) {
         OptionsRegistry$StringParameterDefinition pd = (OptionsRegistry$StringParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null || !pd.isValid(value)) {
            throw new IllegalArgumentException();
         }

         OptionsRegistry$StringParameter param = (OptionsRegistry$StringParameter)this.get(key);
         if (param == null) {
            param = new OptionsRegistry$StringParameter();
            this._persistentData.put(key, param);
         }

         param.setValue(value);
         this._persistent.commit();
      }

      this.notifyListeners(key);
   }

   public void setBoolean(long key, boolean value) {
      synchronized (this._persistent) {
         OptionsRegistry$BooleanParameterDefinition pd = (OptionsRegistry$BooleanParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null || !pd.isValid(value)) {
            throw new IllegalArgumentException();
         }

         OptionsRegistry$BooleanParameter param = (OptionsRegistry$BooleanParameter)this.get(key);
         if (param == null) {
            param = new OptionsRegistry$BooleanParameter();
            this._persistentData.put(key, param);
         }

         param.setValue(value);
         this._persistent.commit();
      }

      this.notifyListeners(key);
   }

   public void setDouble(long key, double value) {
      synchronized (this._persistent) {
         OptionsRegistry$DoubleParameterDefinition pd = (OptionsRegistry$DoubleParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null || !pd.isValid(value)) {
            throw new IllegalArgumentException();
         }

         OptionsRegistry$DoubleParameter param = (OptionsRegistry$DoubleParameter)this.get(key);
         if (param == null) {
            param = new OptionsRegistry$DoubleParameter();
            this._persistentData.put(key, param);
         }

         param.setValue(value);
         this._persistent.commit();
      }

      this.notifyListeners(key);
   }

   public void setInt(long key, int value) {
      synchronized (this._persistent) {
         OptionsRegistry$IntParameterDefinition pd = (OptionsRegistry$IntParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null || !pd.isValid(value)) {
            throw new IllegalArgumentException();
         }

         OptionsRegistry$IntParameter param = (OptionsRegistry$IntParameter)this.get(key);
         if (param == null) {
            param = new OptionsRegistry$IntParameter();
            this._persistentData.put(key, param);
         }

         param.setValue(value);
         this._persistent.commit();
      }

      this.notifyListeners(key);
   }

   public void setLong(long key, long value) {
      synchronized (this._persistent) {
         OptionsRegistry$LongParameterDefinition pd = (OptionsRegistry$LongParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null || !pd.isValid(value)) {
            throw new IllegalArgumentException();
         }

         OptionsRegistry$LongParameter param = (OptionsRegistry$LongParameter)this.get(key);
         if (param == null) {
            param = new OptionsRegistry$LongParameter();
            this._persistentData.put(key, param);
         }

         param.setValue(value);
         this._persistent.commit();
      }

      this.notifyListeners(key);
   }

   public void setByteArray(long key, byte[] value) {
      synchronized (this._persistent) {
         OptionsRegistry$ByteArrayParameterDefinition pd = (OptionsRegistry$ByteArrayParameterDefinition)this._parameterDefinitions.get(key);
         if (pd == null || !pd.isValid(value)) {
            throw new IllegalArgumentException();
         }

         OptionsRegistry$ByteArrayParameter param = (OptionsRegistry$ByteArrayParameter)this.get(key);
         if (param == null) {
            param = new OptionsRegistry$ByteArrayParameter();
            this._persistentData.put(key, param);
         }

         param.setValue(value);
         this._persistent.commit();
      }

      this.notifyListeners(key);
   }

   @Override
   public void setOptionsProviderChangeListener(OptionsProviderChangeListener listener) {
      this._syncListener = listener;
   }

   @Override
   public void setOptionsData(DataBuffer buffer) {
      this._persistentData.clear();
      if (buffer == null) {
         this._persistent.commit();
      } else {
         for (DataBuffer temp = new DataBuffer(); buffer.available() > 0; temp.reset()) {
            try {
               byte[] bytes = ConverterUtilities.readByteArray(buffer);
               temp.setData(bytes, 0, bytes.length);
               long key = temp.readLong();
               OptionsRegistry$ParameterDefinition pd = (OptionsRegistry$ParameterDefinition)this._parameterDefinitions.get(key);
               int type = temp.readInt();
               switch (type) {
                  case -1:
                  case 1:
                     break;
                  case 0:
                  default:
                     String valuexxxxxx = temp.readUTF();
                     if (pd instanceof OptionsRegistry$StringParameterDefinition) {
                        this.setString(key, valuexxxxxx);
                     }
                     break;
                  case 2:
                     int valuexxxxx = temp.readInt();
                     if (pd instanceof OptionsRegistry$IntParameterDefinition) {
                        this.setInt(key, valuexxxxx);
                     } else if (pd instanceof OptionsRegistry$BooleanParameterDefinition) {
                        this.setBoolean(key, valuexxxxx != 0);
                     }
                     break;
                  case 3:
                     long valuexxxx = temp.readLong();
                     if (pd instanceof OptionsRegistry$LongParameterDefinition) {
                        this.setLong(key, valuexxxx);
                     }
                     break;
                  case 4:
                     byte[] valuexx = temp.readByteArray();
                     if (pd instanceof OptionsRegistry$ByteArrayParameterDefinition) {
                        this.setByteArray(key, valuexx);
                     }
                     break;
                  case 5:
                     double valuexxx = temp.readDouble();
                     if (pd instanceof OptionsRegistry$DoubleParameterDefinition) {
                        this.setDouble(key, valuexxx);
                     }
                     break;
                  case 6:
                     boolean valuex = temp.readBoolean();
                     if (pd instanceof OptionsRegistry$BooleanParameterDefinition) {
                        this.setBoolean(key, valuex);
                     }
                     break;
                  case 7:
                     char value = temp.readChar();
                     if (pd instanceof OptionsRegistry$CharParameterDefinition) {
                        this.setChar(key, value);
                     }
               }
            } catch (IllegalArgumentException var10) {
            } catch (IOException var11) {
            }
         }
      }
   }

   @Override
   public void getOptionsData(DataBuffer buffer) {
      try {
         DataBuffer temp = new DataBuffer();
         LongEnumeration enumeration = this.getKeys();

         while (enumeration.hasMoreElements()) {
            long key = enumeration.nextElement();
            if (this.isBackupParam(key)) {
               temp.writeLong(key);
               Object object = this.get(key);
               if (!(object instanceof OptionsRegistry$StringParameter)) {
                  if (!(object instanceof OptionsRegistry$BooleanParameter)) {
                     if (!(object instanceof OptionsRegistry$IntParameter)) {
                        if (!(object instanceof OptionsRegistry$LongParameter)) {
                           if (!(object instanceof OptionsRegistry$DoubleParameter)) {
                              if (!(object instanceof OptionsRegistry$ByteArrayParameter)) {
                                 if (object instanceof OptionsRegistry$CharParameter) {
                                    char value = ((OptionsRegistry$CharParameter)object).getValue();
                                    temp.writeInt(7);
                                    temp.writeChar(value);
                                 }
                              } else {
                                 byte[] value = ((OptionsRegistry$ByteArrayParameter)object).getValue();
                                 if (value != null) {
                                    temp.writeInt(4);
                                    temp.writeByteArray(value);
                                 }
                              }
                           } else {
                              double value = ((OptionsRegistry$DoubleParameter)object).getValue();
                              temp.writeInt(5);
                              temp.writeDouble(value);
                           }
                        } else {
                           long value = ((OptionsRegistry$LongParameter)object).getValue();
                           temp.writeInt(3);
                           temp.writeLong(value);
                        }
                     } else {
                        int value = ((OptionsRegistry$IntParameter)object).getValue();
                        temp.writeInt(2);
                        temp.writeInt(value);
                     }
                  } else {
                     boolean value = ((OptionsRegistry$BooleanParameter)object).getValue();
                     temp.writeInt(6);
                     temp.writeBoolean(value);
                  }
               } else {
                  String value = ((OptionsRegistry$StringParameter)object).getValue();
                  if (value != null) {
                     temp.writeInt(0);
                     temp.writeUTF(value);
                  }
               }

               byte[] bytes = temp.toArray();
               ConverterUtilities.writeByteArray(buffer, 1, bytes);
               temp.reset();
            }
         }
      } catch (IOException var9) {
      }
   }

   private void notifyListeners(long key) {
      if (this._syncListener != null && this.isBackupParam(key)) {
         this._syncListener.optionsProviderChanged(this);
      }

      Object[] listeners = this._listeners;
      if (listeners != null) {
         for (int index = 0; index < listeners.length; index++) {
            ((OptionsRegistry$Listener)listeners[index]).onOptionsRegistryChange(key);
         }
      }
   }

   private Object get(long key) {
      synchronized (this._persistent) {
         return this._persistentData.get(key);
      }
   }

   public OptionsRegistry(long guid) {
      this._guid = guid;
      this._persistent = RIMPersistentStore.getPersistentObject(guid);
      synchronized (this._persistent) {
         this._persistentData = (LongHashtable)this._persistent.getContents();
         if (this._persistentData == null) {
            this._persistentData = new LongHashtable();
            this._persistent.setContents(this._persistentData, 51, false);
            this._persistent.commit();
         }
      }

      DeviceOptions.addOptionsProvider(this);
   }
}
