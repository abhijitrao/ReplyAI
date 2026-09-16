# ReplyAI

ReplyAI is an Android-first AI communication assistant focused on turning natural Hinglish or rough text into clear, context-appropriate replies.

## Product goals

- Generate professional replies without requiring prompt engineering.
- Show a small, configurable translation directly below the generated reply.
- Support WhatsApp, Teams, Email and general communication workflows.
- Support configurable online and offline AI providers.
- Keep UI, domain/business logic and data/infrastructure concerns separated.
- Keep the codebase Kotlin-first, readable, testable and easy to extend for a future web client.

## Android architecture

The project follows a layered Clean Architecture approach:

```text
UI (Compose)
   ↓
Presentation (ViewModel / UI state)
   ↓
Domain (use cases / models / repository contracts)
   ↓
Data (repository implementations / local / remote)
```

AI providers are hidden behind an `AiProvider` abstraction so the UI and business logic do not depend on a specific model vendor.

## Initial MVP

1. Hinglish / natural-language input
2. Professional reply generation
3. Configurable tone and response length
4. Configurable reply language
5. Configurable translation language
6. Translation shown below the reply
7. Copy and share actions
8. Local history
9. Online/offline provider abstraction
10. Model/provider settings foundation

## Development principles

- Kotlin only for application source.
- Jetpack Compose for UI.
- Business logic must not live in Composables.
- No provider-specific code in the domain layer.
- Small, focused classes and functions.
- Consistent formatting and naming.
- Unit tests for domain/business logic.
- Secrets/API keys must never be committed to source control.

See [`docs/SKILLS.md`](docs/SKILLS.md) for the project development rules.