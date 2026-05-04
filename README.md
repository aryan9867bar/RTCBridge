# CAMAVE — WebRTC Video Call App

## Fixes Applied

1. **`MainController.java`** — Removed broken `htmlunit` import (`RTCSessionDescription` from htmlunit does not exist and caused a compile error).
2. **`pom.xml`** — Removed the `htmlunit` dependency entirely (it was unused).
3. **`WebSocketConfig.java`** — Changed `setAllowedOrigins(...)` to `setAllowedOriginPatterns("*")` so the server accepts connections from any host (not just the hardcoded IP).
4. **`index.js`** — Changed the hardcoded `https://192.168.101.7:3000/websocket` URL to use `window.location.host` dynamically, so the frontend always connects to whichever host it's served from.
5. **`index.js`** — Removed broken `testConnection.onclick` handler that referenced a DOM element (`#testConnection`) which doesn't exist in the HTML.

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Steps

```bash
# 1. Navigate to project folder
cd Video-Call-main

# 2. Build the project
mvn clean package -DskipTests

# 3. Run the server
java -jar target/WebRTC-0.0.1-SNAPSHOT.jar
```

### Open in browser

The server runs on **HTTPS port 3000** (required for WebRTC camera access):

```
https://localhost:3000
```

> ⚠️ Your browser will show a security warning for the self-signed certificate.
> Click **"Advanced" → "Proceed to localhost"** to continue.

### Testing two users locally

Open two browser tabs/windows:
- **Tab 1:** Enter ID `alice`, click Connect
- **Tab 2:** Enter ID `bob`, click Connect
- In Tab 1, type `bob` in Remote ID and click Call
- Accept in Tab 2

## SSL Certificate Note

The keystore (`keystore.p12`) uses password `Mandi@2226` and alias `tomcat`.
WebRTC *requires* HTTPS/secure context for camera/microphone access — do not remove SSL.

To generate a fresh self-signed cert:
```bash
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore src/main/resources/keystore.p12 \
  -validity 3650 -storepass Mandi@2226
```
