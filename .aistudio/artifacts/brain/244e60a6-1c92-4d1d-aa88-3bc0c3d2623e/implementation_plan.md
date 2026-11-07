# Implementation Plan - Global Media Player Overhaul

## User Requests & Clarifications
1. **Radio Dial Skin**: Bring an authentic radio dial skin for the radio mode of the global player.
2. **Compact Sandwich Menu**: Replace tabs in the global media player with a compact clickable sandwich menu list.
3. **Townsquare Waves Integration**: Deeply integrate the global media player with Townsquare Waves (live radio ticker, automatic song detection, and streaming catalog).
4. **Audiobook Mode**: Add an audiobook mode allowing users to listen, follow along with the digital replica inside the media player, and change the language (English, Spanish, French, German, Japanese).
5. **Modular Source Code**: Break the global media player into modular feature and component files (`GlobalMediaPlayer.kt`, `GlobalPlayerSandwichMenu.kt`, `RadioDialPlayerSkin.kt`, `AudiobookReplicaPlayer.kt`).

## Proposed Changes

### 1. Modular Player Components (`com/example/ui/components/player/`)
- `GlobalPlayerSandwichMenu.kt`: Compact clickable sandwich menu list replacing old tab layout.
- `RadioDialPlayerSkin.kt`: Authentic radio dial skin for radio mode playback inside the global player.
- `AudiobookReplicaPlayer.kt`: Audiobook player mode featuring live audio playback, synchronized digital replica text follow-along, and language selector (English, Spanish, French, German, Japanese).
- `GlobalMediaPlayer.kt`: Main orchestrator integrating Waves, Radio Dial Skin, Audiobook mode, and Sandwich menu.

## Verification Plan
- Run `compile_applet` to ensure zero compilation errors.
