#include <WiFi.h>
#include <esp_now.h>
#include <Wire.h>

#define SLAVE_ID "SLAVE_08"
uint8_t HUB_MAC[] = {0x68, 0x25, 0xDD, 0xEE, 0x9E, 0x78};
const uint8_t MPU_ADDR = 0x68;

typedef struct SensorPacket {
  char slaveId[16];
  unsigned long tsf;
  float ax;
  float ay;
  float az;
} SensorPacket;

esp_now_peer_info_t peerInfo;
unsigned long lastSendMs = 0;
const unsigned long SEND_INTERVAL_MS = 10;

void onDataSent(const wifi_tx_info_t *tx_info, esp_now_send_status_t status) {
  if (status != ESP_NOW_SEND_SUCCESS) Serial.println("[SLAVE] Send failed");
}

bool writeMPURegister(uint8_t reg, uint8_t value) {
  Wire.beginTransmission(MPU_ADDR);
  Wire.write(reg);
  Wire.write(value);
  return Wire.endTransmission(true) == 0;
}

bool readMPURawAccel(int16_t &ax, int16_t &ay, int16_t &az) {
  Wire.beginTransmission(MPU_ADDR);
  Wire.write(0x3B);
  if (Wire.endTransmission(false) != 0) return false;
  if (Wire.requestFrom((int)MPU_ADDR, 6, true) != 6) return false;
  ax = (Wire.read() << 8) | Wire.read();
  ay = (Wire.read() << 8) | Wire.read();
  az = (Wire.read() << 8) | Wire.read();
  return true;
}

void setupMPU() {
  Wire.begin(21, 22);
  delay(100);
  if (!writeMPURegister(0x6B, 0x00)) { Serial.println("[SLAVE] Wake MPU failed"); while (true) delay(1000); }
  delay(100);
  if (!writeMPURegister(0x1C, 0x00)) { Serial.println("[SLAVE] Accel range failed"); while (true) delay(1000); }
  int16_t tx, ty, tz;
  if (!readMPURawAccel(tx, ty, tz)) { Serial.println("[SLAVE] MPU read test failed"); while (true) delay(1000); }
  Serial.println("[SLAVE] MPU6050 ready");
}

void setupEspNow() {
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  if (esp_now_init() != ESP_OK) { Serial.println("[SLAVE] ESP-NOW init failed"); while (true) delay(1000); }
  esp_now_register_send_cb(onDataSent);
  memset(&peerInfo, 0, sizeof(peerInfo));
  memcpy(peerInfo.peer_addr, HUB_MAC, 6);
  peerInfo.channel = 0;
  peerInfo.encrypt = false;
  if (esp_now_add_peer(&peerInfo) != ESP_OK) { Serial.println("[SLAVE] Add peer failed"); while (true) delay(1000); }
  Serial.println("[SLAVE] ESP-NOW ready");
}

void setup() {
  Serial.begin(115200);
  delay(1000);
  Serial.println("[SLAVE] Booting " SLAVE_ID);
  setupMPU();
  setupEspNow();
}

void loop() {
  if (millis() - lastSendMs < SEND_INTERVAL_MS) return;
  lastSendMs = millis();

  int16_t rawAx, rawAy, rawAz;
  if (!readMPURawAccel(rawAx, rawAy, rawAz)) { delay(50); return; }

  SensorPacket packet;
  memset(&packet, 0, sizeof(packet));
  strncpy(packet.slaveId, SLAVE_ID, sizeof(packet.slaveId) - 1);
  packet.tsf = micros();
  packet.ax = rawAx / 16384.0f;
  packet.ay = rawAy / 16384.0f;
  packet.az = rawAz / 16384.0f;

  esp_now_send(HUB_MAC, (uint8_t*)&packet, sizeof(packet));
}