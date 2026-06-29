package net.rim.device.api.enterpriseconfig;

public interface EnterpriseConfigConstants {
   int VERSION = 1;
   String ENTERPRISE_CONFIG_NAME = "Enterprise Configuration";
   byte ENTERPRISE_CONFIG_RECORD_TYPE = 127;
   byte RESERVED_TABLE_ID = 0;
   byte SOFT_TOKEN_TABLE_ID = 1;
   byte SOFT_TOKEN_SEED_TAG = 1;
   byte SOFT_TOKEN_SERIAL_NUM_TAG = 2;
   byte SOFT_TOKEN_SEED_PASSWORD_TAG = 3;
   byte SOFT_TOKEN_PIN_CACHE_TIMEOUT_TAG = 4;
}
