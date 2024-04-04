
# Mobile Image Capture and Storage 

This project consists of an Android app frontend and Python Flask backend that work together to allow capturing, labeling and storing images from mobile devices.

## Features

- Android app to capture images and select category label
- Images are uploaded wirelessly to backend Flask server 
- Backend stores images organized by category
- Image files are named based on UTC timestamp

## Architecture

The system is split into two main components:

### Android App (Frontend)
- Built using Android Studio[1]
- Captures images using device camera
- Allows user to select category for image using a Spinner view
- Uploads image to backend server using Retrofit
- Displays loading panel during upload for better UX
- Shows toast messages to indicate upload success or failure

### Flask Server (Backend) 
- Runs on Python using Flask web framework
- Receives images and category from Android app via POST request
- Stores images in folder matching selected category
- Names image files based on UTC timestamp 
- Validates image is JPG, JPEG or PNG format
- Returns success or error response to Android app

## App Workflow

1. User launches app and taps Capture button
2. Default camera app opens to take picture
3. After capturing, user selects image category from dropdown
4. User taps Upload to send image and category to backend
5. App shows loading panel during upload process
6. Backend validates and stores image in matching category folder
7. App displays toast message indicating upload success or failure
8. User can tap Retake to capture image again

## Getting Started

To set up the development environment:

1. Clone this repository
2. Open the `frontend` directory in Android Studio 
3. Edit the server URL in `CloudService.java` to point to your backend
4. Run the Flask server locally:
   ```
   cd backend
   pip install -r requirements.txt
   python app.py
   ```
5. Build and run the Android app on a device or emulator

Ensure the Android device is on the same local network as the backend server. The Android app will capture and upload images to the configured server URL.


