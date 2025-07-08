# 🕹️ Igame - Multiplayer Betting Game (Spring Boot + WebSocket)

**Igame** is a real-time multiplayer betting game built with **Spring Boot**, **WebSocket**, and **Gradle**. It allows players to place bets, join sessions, and compete in live rounds. The backend manages betting logic, rounds, player sessions, and nickname uniqueness.

---

<img width="697" alt="Screenshot 2025-07-04 at 13 39 02" src="https://github.com/user-attachments/assets/9541351c-7517-4560-b27f-a28f60a2c6c7" />

---


<img width="697" alt="Screenshot 2025-07-04 at 13 42 14" src="https://github.com/user-attachments/assets/a89286ab-783c-427b-a4e8-7d56b12b066f" />

---


<img width="697" alt="Screenshot 2025-07-04 at 13 43 54" src="https://github.com/user-attachments/assets/108726cb-6ad4-48f3-8660-33174c88ed9d" />

---
<img width="697" alt="Screenshot 2025-07-04 at 13 46 44" src="https://github.com/user-attachments/assets/6ec1cddf-30b7-479d-8deb-8cb9a0c048c0" />

---



## 🚀 Features

- Real-time WebSocket-based communication
- Place numeric bets with nickname and amount
- Automatic round management with scheduled results
- Duplicate nickname detection per round
- RTP-tested logic (Return-To-Player)
- Dockerized deployment

---

## 🛠️ Technologies Used

- Java 21
- Spring Boot
- WebSocket (Spring Messaging)
- Gradle
- Docker
- JUnit & Mockito (for testing)

---

## 📦 Getting Started

### Prerequisites

- Java 21
- Gradle
- Docker

### Clone the repository

```bash
git clone https://github.com/your-username/Igame.git
cd Igame
🧪 Run Tests
./gradlew test
🔧 Build the Project
./gradlew clean build
The JAR file will be located in build/libs/Igame-0.0.1-SNAPSHOT.jar

🐳 Docker
Build Docker Image
Make sure the JAR is built first:
./gradlew build
docker build -t igame:latest .
Run Container
docker run -p 8080:8585 igame:latest
The application will be accessible on http://localhost:8585

🎮 WebSocket Usage
💡 Place a Bet
Send a TextMessage via WebSocket with the following JSON:

json
Copy
Edit
{
  "nickname": "Player1",
  "number": 3,
  "amount": 50
}
nickname: Unique per player per round

number: Integer between 1–10

amount: Positive numeric value (BigDecimal)

✅ Server Responses
"WIN: 495.0" — You won 9.9× your bet

"LOSS" — Better luck next time!

"Round Result: [...]" — Summary of all winners

🧪 Testing RTP
Run a simulation of 1,000,000 rounds:
./gradlew test --tests GameRTPTest
Expected RTP: ~99%



📁 Project Structure
swift
Copy
Edit
Igame/
├── src/
│   ├── main/
│   │   ├── java/com/amoloye/yolo/igame/
│   │   └── resources/
│   └── test/
├── build.gradle
├── Dockerfile
├── .gitignore
└── README.md
✨ Sample WebSocket Usage
You can connect to:
ws://localhost:8585/igame
Send a JSON message to place a bet:

json
Copy
Edit
{
  "nickname": "Player1",
  "number": 5,
  "amount": 10.0
}






