# SIT708_Lost_Found_App1

## Overview
This is a Lost and Found mobile application developed using Kotlin in Android Studio for SIT708 Task 9.1P. The app allows users to report lost or found items, upload images, store item details locally using SQLite, select locations using Google Places, and display items on Google Maps with radius-based search functionality.

---

## Features

### Lost and Found Item Reporting
Users can add lost or found item reports with:
- Title
- Description
- Category
- Image upload from gallery
- Timestamp
- Location selection
- Current location support

### Google Maps Integration
- Display all saved lost and found items on Google Maps
- Different coloured markers:
  - Red markers for lost items
  - Green markers for found items
- User current location marker

### Radius-Based Search
- Search nearby items within a selected radius
- Distance calculation using Android location services

### Item Management
- View all saved items
- Filter items by category using Spinner
- Delete items using long press

### Validation
- Input validation using Toast messages
- Location and image validation before saving

---

## Technologies Used
- Kotlin
- Android Studio
- SQLite Database
- Google Maps SDK
- Google Places API
- Fused Location Provider
- Android SDK
- ListView & Spinner

---

## Project Structure

### Activities
- `MainActivity.kt` → Home screen navigation
- `AddItemActivity.kt` → Add item form and location handling
- `ItemListActivity.kt` → View and filter items
- `MapActivity.kt` → Display map and radius search

### Database
- `DatabaseHelper.kt` → SQLite database operations

### Model
- `Item.kt` → Data model for lost/found items

### Layouts
- `activity_main.xml`
- `activity_add_item.xml`
- `activity_item_list.xml`
- `activity_map.xml`

---

## How to Run
1. Clone the repository
2. Open the project in Android Studio
3. Add a valid Google Maps API key in `strings.xml`
4. Enable:
   - Maps SDK for Android
   - Places API
5. Sync Gradle files
6. Connect an Android device or emulator
7. Click Run

---

## Future Improvements
- Firebase or cloud database integration
- User authentication system
- Push notifications for nearby matched items
- In-app messaging between users
- Camera integration for image capture
- RecyclerView-based UI improvements
- Admin moderation system

---

## Student Details
- Name: Anay Jayakumar
- Student ID: 224726304
- Unit: SIT708 Mobile Application Development
