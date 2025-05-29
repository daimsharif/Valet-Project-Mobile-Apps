# Valet Parking Management App (Java + Firebase + Google Maps)

This Android application enables real-time valet request coordination between drivers and administrators. Built using **Java**, integrated with **Firebase** and **Google Maps API**, the app supports live location tracking, request status management, and role-specific access flows.

---

## 🚗 Project Summary

- 📍 Real-time **driver location tracking** with Google Maps
- 🔐 **Firebase Authentication** for secure login (driver/admin roles)
- 🔄 **Firebase Realtime Database** for instant request updates
- 🧑‍💼 Admin view to assign and monitor valet operations
- 🚘 Driver view to receive jobs and update statuses dynamically

---

## 🛠️ Tech Stack

- **Language**: Java (Android SDK)
- **UI**: XML layouts, Android Material Design
- **Backend**: Firebase Authentication, Firebase Database, Firestore
- **Live Maps**: Google Maps API

## 🔔 Notification System

The app uses **native Android push notifications** that are automatically triggered when Firestore documents (parking request logs) are created or updated.

- Listeners are attached to the Firestore `requests` collection.
- On any change (e.g., new request, status update), the app pushes a system notification to the appropriate user (admin or driver).
- This ensures real-time updates without requiring FCM.

### 🔧 Example Flow

- A driver is assigned a request → Firestore document updates
- Listener detects change → Triggers Android `NotificationManager`
- Notification appears in the system tray, routing to request details

