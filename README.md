# SIT708_Lost_Found_App1

## Overview
This is a Lost and Found mobile application developed using Kotlin in Android Studio. The app allows users to report lost items, upload images, store item details locally using SQLite, and view or manage stored items.

---

# LostFoundApp - SIT708 Task 9.1P

This Android application is a Lost and Found mobile app developed for SIT708 Task 9.1P.

## Features
- Add lost item reports
- Add found item reports
- Store item details using SQLite
- Store location name, latitude, and longitude
- Select location manually using Google Places
- Get current location
- View all lost and found items
- Display items on Google Maps
- Radius-based search to show nearby items
- Different marker colours for lost and found items


## Technologies Used

- Kotlin
- Android Studio
- SQLite Database
- ListView & Spinner
- Android SDK

---

## Project Structure

- `MainActivity.kt` → Home screen navigation  
- `AddItemActivity.kt` → Add item form  
- `ItemListActivity.kt` → View & filter items  
- `DatabaseHelper.kt` → SQLite database operations  
- `Item.kt` → Data model  

---
- SQLite
- Google Maps SDK
- Google Places API
- Fused Location Provider

## How to Run
1. Open the project in Android Studio.
2. Add a valid Google Maps API key in `strings.xml`.
3. Enable Maps SDK for Android and Places API in Google Cloud Console.
4. Sync Gradle.
5. Run the app on an emulator or Android device.

1. Clone the repository
2. Open in Android Studio
3. Connect an emulator or Android device
4. Click **Run**

---

## Future Improvements

- Camera integration for image capture  
- Firebase/cloud database integration  
- User authentication system  
- Search functionality  
- Improved UI using RecyclerView  


## Student Details
Name: Anay Jayakumar  
Student ID: 224726304
