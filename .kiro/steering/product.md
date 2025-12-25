# Product Overview

**Gyps** is a modern Android mobile development framework built on Google's latest Android Architecture Components (AAC). It provides a comprehensive MVVM architecture foundation for building scalable Android applications.

## Core Purpose

Gyps serves as a reusable framework library that encapsulates common Android development patterns, reducing boilerplate and accelerating development of new Android applications.

## Key Features

- **MVVM Architecture**: Clean separation of concerns with ViewModel, Repository, and View layers
- **Dependency Injection**: Dagger Hilt for compile-time dependency injection
- **Reactive Programming**: Kotlin Coroutines and Flow for asynchronous operations
- **Modern UI**: ViewBinding and optional Jetpack Compose support
- **Network Layer**: Retrofit with OkHttp for REST API communication
- **Image Loading**: Glide v5 with Generated API support
- **Local Storage**: Room database and MMKV for key-value storage
- **Navigation**: AndroidX Navigation component support
- **Permissions**: EasyPermissions for runtime permission handling
- **UI Components**: Immersion bar, Material Design dialogs, and custom widgets

## Module Structure

- **swallow**: Core framework module containing base classes, networking, database, and utilities
- **base**: UI resource module with custom widgets, adapters, and common UI components
- **msc**: Voice input module (iFlytek MSC SDK integration)
- **app**: Sample application demonstrating framework usage
