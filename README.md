# Pinnacle Weather
## Overview

The Weather App is a feature-rich Android application designed to provide users with real-time weather information. It offers the following key features:

- Current temperature
- Weather icon
- Descriptive weather information
- Timestamp for the last data update

The application is built with a strong focus on functionality and architectural flexibility to support future iterations and features. While the primary emphasis is on functionality, the user interface (UI) has been designed to demonstrate the core features of the app.

## Features

The Weather App includes the following features:

1. **Automatic Location-Based Weather**: Upon opening the app, it requests permission to access the user's current location and automatically loads the weather data for that location.

2. **Manual City Entry**: Users can enter a United States city and attempt to load the current weather for that specific city.

3. **Error Handling**: If weather data cannot be retrieved based on user input, a message is displayed to inform the user.

4. **Data Persistence**: Successfully fetched weather data is stored in a local database resident on the Android device. This stored data is accessible to users in case of a failed live weather fetch.

## Architecture and Technologies

The Weather App is built on a robust architectural foundation and uses various technologies:

- **MVVM Architecture**: The application follows the Model-View-ViewModel architecture pattern, promoting separation of concerns and maintainability.

- **Room Database**: Room is used as a local database solution, allowing efficient data storage and retrieval.

- **Retrofit**: Retrofit is employed for making HTTP API calls to retrieve weather data from external sources.

- **Jetpack Compose**: Jetpack Compose is used to create dynamic and interactive UI components.

- **Google LocationServices**: Google LocationServices provides geolocation capabilities, enhancing the user experience with accurate weather data based on the user's location.

- **OpenWeatherAPI**: OpenWeatherAPI is the source of current weather data, offering reliable information to the application.

- **JUnit**: JUnit is used for unit testing to ensure code quality and maintain application reliability.

## Future Enhancements

In the future, we plan to make the following improvements and additions to the Weather App:

- **Expanded Unit Testing**: We aim to achieve 100% test coverage on public functions to enhance the reliability and robustness of the application.

- **Logging**: Implementing comprehensive logging will provide insights into exceptions, debugging, performance, and user interactions, aiding in the maintenance and improvement of the app.

- **Intuitive UI**: Enhancements to the user interface will make it more intuitive, providing a seamless and enjoyable experience for users.

- **More Data Display**: We plan to expand the information displayed to users, offering a comprehensive view of the current weather conditions.

- **User Messages**: Implementing text pop-ups to notify users when weather data cannot be loaded will enhance the user experience and transparency.
