# ReplyAI Development Skills / Rules

This document defines the engineering rules for the ReplyAI Android application.

## 1. Language

- Kotlin only for application source code.
- Prefer immutable data (`val`) and immutable UI state.
- Use coroutines and `Flow`/`StateFlow` for asynchronous work and state streams.

## 2. Architecture

Use layered Clean Architecture:

```text
presentation → domain ← data
```

- **Presentation**: Compose UI, ViewModels, UI state and UI events.
- **Domain**: business rules, domain models, use cases and repository/provider contracts.
- **Data**: repository implementations, local persistence, network clients and AI provider implementations.

The domain layer must not depend on Android framework classes, Compose, Retrofit, Room or a specific AI vendor.

## 3. UI and business logic

- Composables render state and emit user events.
- Business decisions belong in use cases/domain classes or ViewModels.
- Do not perform network/database/model calls directly from Composables.
- Keep Composables small and reusable.
- Prefer stateless Composables with state hoisted to the caller.

## 4. AI provider abstraction

All AI models/providers must implement a common abstraction. The rest of the application must not know whether a response came from OpenAI, another online provider, or an offline model.

```text
AiProvider
 ├── Online provider implementations
 └── Offline provider implementations
```

Provider credentials and model identifiers must remain outside the domain layer.

## 5. Communication generation contract

The generation request should preserve:

- Input text
- Input language
- Communication type
- Reply language
- Translation language
- Tone
- Response length
- Meaning-preservation requirement

The generated result contains the reply and its translation.

## 6. Privacy and secrets

- Never commit API keys, tokens, passwords or signing credentials.
- Use Android secure storage for locally stored secrets.
- History should be local-first unless a future sync feature explicitly opts in.
- Clearly distinguish online processing from offline processing in the UI.

## 7. Code quality

- Use descriptive names; avoid unnecessary abbreviations.
- One responsibility per class/function.
- Prefer small functions over large procedural methods.
- Avoid magic strings and numbers; use constants or typed configuration.
- Avoid nullable values unless null has a meaningful domain interpretation.
- Keep public APIs minimal.
- Remove dead code and unused dependencies.

## 8. Formatting

- Code must be Kotlin-formatted and easy to scan.
- Follow standard Kotlin naming conventions.
- Keep imports clean and ordered by IDE formatting.
- Do not use compressed one-line code when it hurts readability.
- Run formatting before committing.

## 9. Testing

- Unit-test domain use cases and business rules.
- Test ViewModel state transitions where practical.
- Provider implementations should be mockable through interfaces.
- Do not require a real AI API key for unit tests.

## 10. Dependencies

Add a dependency only when it provides clear value.

Before adding a library, consider whether the requirement can be solved with the Android/Kotlin platform already in use.

## 11. Git

Use focused commits with conventional prefixes:

- `feat:` new functionality
- `fix:` bug fix
- `refactor:` structural/code-quality change
- `test:` tests
- `docs:` documentation
- `build:` build/dependency changes
- `chore:` maintenance

Keep commits small enough to review independently.

## 12. Future web client

The Android application must keep communication-generation contracts and business concepts platform-neutral so the same API/domain concepts can later support a web client.
