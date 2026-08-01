# Data Structures & Algorithms - Kotlin

Data structures and algorithms, implemented from scratch in Kotlin, for
long-term retention and interview readiness — not a Kotlin learning
exercise. See `CLAUDE.md` for the full workflow this repo follows.

## Structure

- `ROADMAP.md` — the topic sequence and why it's ordered this way.
- `GLOSSARY.md` — terminology used across `*_NOTES.md` files.
- Per topic, under `src/main/kotlin/<package>/`, named by topic (e.g.
  `SINGLY_LINKED_LIST_NOTES.md`) so multiple topics can share a package
  folder without colliding:
  - `<TOPIC>_NOTES.md` — the lesson: concept, invariants, complexity, when
    to use it, and a "Check Your Understanding" quiz bank.
  - `<TOPIC>_TASK.md` — the work order: what to implement, expected
    behavior/edge cases, and an example stub to copy in.
  - The implementation itself, written by hand.
- Matching tests under `src/test/kotlin/<package>/`.

## Running tests

    ./gradlew test

Tests are the feedback loop here — red until an implementation is correct,
green once it is.
