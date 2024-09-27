# Q&A Forum

## Overview

This project is a Q&A forum application developed using **Spring Boot** for the backend and **React** for the frontend. It allows users to post questions, comment on them, and manage likes on comments.

## Architecture

- **Backend**: 
  - Built with **Spring Boot**
  - Currently uses **H2 Database** for development
  - Plans to migrate to **MySQL** in the future
  - Implements JWT for authentication, along with a blacklist and refresh token mechanism using **Redis**

- **Frontend**: 
  - Developed using **React**
  - Provides a user-friendly interface for interacting with the forum

## Features

- Users can create, read, update, and delete questions.
- Users can comment on questions and manage those comments (edit/delete).
- Each comment can be liked, with restrictions to prevent multiple likes from the same user.
- A notification is displayed before a post or comment is deleted to confirm the action.

## Getting Started

### Prerequisites

- Java 17 or higher
- Node.js and npm
- Redis server

### Backend Setup

1. Clone the repository.
2. Navigate to the backend directory:
   ```bash
   cd /iniforum
   ```
3. Build and run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
4. The backend will be available at `http://localhost:8080`.

### Frontend Setup

1. Navigate to the frontend branch:
   ```bash
   git checkout frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the React application:
   ```bash
   npm start
   ```
4. The frontend will be available at `http://localhost:3000`.

## Future Enhancements

- Migrate the database from H2 to MySQL.
- Improve UI/UX for better user engagement.
- Add more features such as user profiles, tagging, and search functionality.

## License

This project is licensed under the MIT License.
