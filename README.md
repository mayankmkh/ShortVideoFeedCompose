# Short Form Video Feed

This is a sample app that uses Exoplayer to play short form video feeds.

## Setup

1. Clone the repo
2. Open the project in Android Studio
3. Run the app

## Features

- Plays short form video feeds
- Configurable number of players using player pool
- Preloads videos
- Supports HLS playback

## Structure

- `ui`: Contains the UI components
- `data`: Contains the data models
- `domain`: Contains the domain logic
- `presentation`: Contains the presentation logic
- `player`: Contains the player logic

## Important Files

- `PlayerInteractor.kt`: Facade for all player related interactions
- `PlayerPool.kt`: The player pool for reusing players
