<div align="center">

# 🧮 TDS AI Calculator

### AI-Powered Tax Deduction at Source Calculator

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2023.10.01-green.svg)](https://developer.android.com/jetpack/compose)
[![Material Design 3](https://img.shields.io/badge/Material%20Design-3-blue.svg)](https://m3.material.io)
[![License](https://img.shields.io/badge/License-Educational-orange.svg)](LICENSE)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-brightgreen.svg)](https://developer.android.com)

**A premium, AI-powered TDS (Tax Deducted at Source) calculator Android application built with Kotlin and Jetpack Compose.**

[Features](#-features) • [Screenshots](#-screenshots) • [Tech Stack](#-tech-stack) • [Installation](#-installation) • [Contact](#-contact)

</div>

---

## Features

### 🧮 TDS Calculator
- Real-time tax calculation based on income and deductions
- Support for Section 80C deductions (up to ₹1.5 lakh)
- Other deductions including NPS, health insurance, etc.
- Detailed tax slab breakdown
- Animated progress indicators and visualizations

### 📚 Learning Center
- Comprehensive TDS information
- Tax slabs and rates
- Deduction options explained
- Collapsible cards for easy navigation
- Tax-saving strategies

### 🤖 AI Chat Assistant
- Interactive AI chatbot for TDS queries
- Real-time responses
- Typing indicators
- Chat history
- Smart suggestions and clarifications

### 📊 AI Analysis
- Tax efficiency score
- Detailed breakdown of income distribution
- AI-powered recommendations for tax optimization
- Visual charts and progress bars
- Personalized tax-saving tips

### ⚙️ Settings
- Light/Dark/System theme modes
- Smooth theme transitions
- Notification preferences
- App information

## Design Highlights

- **Edge-to-edge layout** with proper window insets handling
- **Glassmorphic floating navigation bar** with 5 tabs
- **Premium gradient designs** with Indigo → Purple → Pink color scheme
- **Smooth animations** throughout the app
- **Material Design 3** principles
- **Responsive** for all screen sizes

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Clean Architecture
- **Dependency Injection**: Hilt
- **State Management**: StateFlow & Compose State
- **Data Storage**: DataStore Preferences
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## Package Structure

```
com.kreggscode.tdscalculator
├── data
│   ├── models          # Data models
│   ├── preferences     # DataStore preferences
│   └── repository      # Business logic
├── di                  # Dependency injection modules
├── ui
│   ├── components      # Reusable UI components
│   ├── navigation      # Navigation logic
│   ├── screens         # App screens
│   ├── theme           # Theme and styling
│   └── viewmodels      # ViewModels
├── MainActivity.kt
└── TDSApplication.kt
```

## Key Components

### Premium UI Components
- `PremiumCard` - Gradient-bordered elevated cards
- `GlassmorphicCard` - Frosted glass effect cards
- `AnimatedGradientButton` - Spring-animated gradient buttons
- `StatCard` - Metric display cards with icons
- `AnimatedProgressBar` - Smooth progress indicators
- `PulsingIcon` - Breathing animations for AI features

### Screens
1. **SplashScreen** - Animated app launch
2. **CalculatorScreen** - TDS calculation interface
3. **LearningScreen** - Educational content
4. **ChatScreen** - AI assistant chat
5. **AnalysisScreen** - Tax analysis and insights
6. **SettingsScreen** - App preferences

## Building the App

1. Clone the repository
2. Open in Android Studio (Hedgehog or later)
3. Sync Gradle files
4. Run on emulator or device

## Requirements

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Gradle 8.2

## Color Palette

- **Primary**: Indigo (#6366F1) → Purple (#8B5CF6) → Pink (#EC4899)
- **AI Gradient**: Teal (#14B8A6) → Indigo → Purple
- **Success**: Emerald (#10B981) → Teal
- **Warning**: Amber (#F59E0B) → Red (#EF4444)

## 📸 Screenshots

<div align="center">

| Splash Screen | Calculator | AI Chat |
|:---:|:---:|:---:|
| ![Splash](docs/screenshots/splash.png) | ![Calculator](docs/screenshots/calculator.png) | ![Chat](docs/screenshots/chat.png) |

| Learning | Analysis | Settings |
|:---:|:---:|:---:|
| ![Learning](docs/screenshots/learning.png) | ![Analysis](docs/screenshots/analysis.png) | ![Settings](docs/screenshots/settings.png) |

</div>

## 🚀 Installation

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Gradle 8.2

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/kreggscode/TDS-Ai.git
   cd TDS-Ai
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Click `File → Open`
   - Select the project directory

3. **Sync Gradle**
   - Wait for Gradle sync to complete
   - All dependencies will be downloaded automatically

4. **Run the app**
   - Click the Run button (▶️) or press `Shift + F10`
   - Select an emulator or connected device

## 📱 Download

Coming soon to Google Play Store!

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is for educational and demonstration purposes.

## 📧 Contact

**Developer**: KreggsCode

- 📧 Email: [kreg9da@gmail.com](mailto:kreg9da@gmail.com)
- 📦 Package: `com.kreggscode.tdscalculator`
- 🔖 Version: 1.0.0

## 🌐 Links

- [Privacy Policy](https://kreggscode.github.io/TDS-Ai/privacy.html)
- [Terms & Conditions](https://kreggscode.github.io/TDS-Ai/terms.html)

---

<div align="center">

**Built with ❤️ using Jetpack Compose and Material Design 3**

⭐ Star this repo if you find it helpful!

</div>
