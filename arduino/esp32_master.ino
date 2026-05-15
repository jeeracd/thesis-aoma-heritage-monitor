#include <WiFi.h>
#include <esp_now.h>

typedef struct SensorPacket {
  char slaveId[16];
  unsigned long tsf;
  float ax;
  float ay;
  float az;
} SensorPacket;

String knownSlaves[8];
bool slaveConnected[8];
bool slaveSynced[8];
unsigned long lastSeenMs[8];
int slaveCount = 0;

unsigned long lastSyncBroadcastMs = 0;
const unsigned long SYNC_INTERVAL_MS = 2000;
const unsigned long DISCONNECT_TIMEOUT_MS = 5000;

bool findSlaveIndex(const String& id, int &indexOut) {
  for (int i = 0; i < slaveCount; i++) {
    if (knownSlaves[i] == id) {
      indexOut = i;
      return true;
    }
  }
  return false;
}

int addSlaveIfMissing(const String& id) {
  int idx;
  if (findSlaveIndex(id, idx)) return idx;

  if (slaveCount < 8) {
    knownSlaves[slaveCount] = id;
    slaveConnected[slaveCount] = false;
    slaveSynced[slaveCount] = false;
    lastSeenMs[slaveCount] = 0;
    slaveCount++;
    return slaveCount - 1;
  }

  return -1;
}

unsigned long getMasterTSF() {
  return micros();
}

void onDataRecv(const esp_now_recv_info_t *recv_info, const uint8_t *incomingData, int len) {
  if (len != sizeof(SensorPacket)) {
    Serial.println("[HUB] Invalid packet size");
    return;
  }

  SensorPacket packet;
  memcpy(&packet, incomingData, sizeof(packet));

  String slaveId = String(packet.slaveId);
  int idx = addSlaveIfMissing(slaveId);
  if (idx < 0) return;

  if (!slaveConnected[idx]) {
    slaveConnected[idx] = true;
    Serial.println("STATUS," + slaveId + ",CONNECTED");
  }

  lastSeenMs[idx] = millis();

  Serial.print("HUB,");
  Serial.print(slaveId);
  Serial.print(",");
  Serial.print(packet.tsf);
  Serial.print(",");
  Serial.print(packet.ax, 6);
  Serial.print(",");
  Serial.print(packet.ay, 6);
  Serial.print(",");
  Serial.println(packet.az, 6);
}

void setupEspNow() {
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();

  if (esp_now_init() != ESP_OK) {
    Serial.println("[HUB] ESP-NOW init failed");
    while (true) delay(1000);
  }

  esp_now_register_recv_cb(onDataRecv);
  Serial.println("[HUB] ESP-NOW ready");
}

void setup() {
  Serial.begin(115200);
  delay(1000);
  Serial.println("[HUB] Booting...");
  setupEspNow();
}

void loop() {
  if (millis() - lastSyncBroadcastMs >= SYNC_INTERVAL_MS) {
    lastSyncBroadcastMs = millis();

    unsigned long masterTsf = getMasterTSF();
    Serial.print("SYNC,");
    Serial.println(masterTsf);

    for (int i = 0; i < slaveCount; i++) {
      if (slaveConnected[i] && !slaveSynced[i]) {
        slaveSynced[i] = true;
        Serial.println("STATUS," + knownSlaves[i] + ",SYNCED");
      }
    }
  }

  for (int i = 0; i < slaveCount; i++) {
    if (slaveConnected[i] && (millis() - lastSeenMs[i] > DISCONNECT_TIMEOUT_MS)) {
      slaveConnected[i] = false;
      slaveSynced[i] = false;
      Serial.println("STATUS," + knownSlaves[i] + ",DISCONNECTED");
    }
  }

  delay(10);
}