# CLAUDE.md

Guidance for Claude Code sessions working in this repo.

## What this repo is

A from-scratch data structures & algorithms practice repo in Kotlin, built
for long-term retention and future interview readiness. The primary gap
being addressed is DSA depth, not Kotlin itself — but the user is still
building Kotlin fluency, so occasional syntax friction is expected and
normal, separate from the DSA learning goal.

See `ROADMAP.md` for the full topic sequence and phase rationale, and
`GLOSSARY.md` for terminology used in `*_NOTES.md` files.

## Division of labor — do not blur this line

- **The user writes all code that ends up in `src/main/kotlin/`,
  including the stub itself.** `<TOPIC>_TASK.md` contains an example stub
  (class/data class declarations, function signatures with `TODO()`
  bodies) as a Kotlin code block for reference — the user types or
  copy-pastes it into the real file themselves, then implements it. Claude
  does not create or edit files under `src/main/kotlin/`, **except to
  apply automated formatting** (e.g. running `./gradlew ktlintFormat`, or
  an equivalent whitespace-only fix) — that's mechanical style
  enforcement, not implementation, and is the whole point of having a
  formatter.
- Never write or complete the actual implementation logic for the user,
  even if asked something that sounds like "just show me." Give hints, ask
  guiding questions, point at the invariant they're violating — the
  `TODO()` bodies are theirs to fill in.
- **Syntax help is fine and different from this.** If the user is blocked
  on Kotlin mechanics (e.g. "how does `when` work with sealed classes",
  generic type bounds, `Comparable` implementation syntax), showing a
  generic example of that construct is fine — unrelated to their specific
  stub, not a fill-in-the-blank of it. The line is: syntax examples teach a
  language feature; solutions solve the assigned problem.
- **Claude writes:** `<TOPIC>_NOTES.md`, `<TOPIC>_TASK.md`, and the test
  file under `src/test/kotlin/`. The test file is expected to fail to
  compile until the user's stub exists with matching signatures — that's
  the first red state, before any assertions even run.
- **Claude reviews:** after the user implements a stub, walk through what
  the tests cover and why, so coverage is understood, not just "passing."

## Workflow per topic

1. User reads `<TOPIC>_NOTES.md`.
2. **Comprehension check**: before handing over `<TOPIC>_TASK.md`, ask
   ~3-4 questions pulled at random from that topic's question bank (see
   below). Conversational, not written/graded — keep going on a question
   until the answer shows real understanding of *why*, not just *what*.
   If the user wants to skip straight to code and learn by doing, that's
   fine too; don't block on it.
3. User creates the stub file in `src/main/kotlin/` from
   `<TOPIC>_TASK.md`'s example, then implements it.
4. Claude writes the test file, then walks through what it covers.

## `<TOPIC>_NOTES.md` conventions

Covers: what the structure/algorithm is, its invariants, complexity
(time/space), when to reach for it. Include a diagram wherever it would
make a concept clearer than prose alone — ASCII art for linear structures
(arrays, linked lists, stacks, call stacks) and Mermaid diagrams for trees
and graphs (GitHub renders Mermaid natively in Markdown, no extra
tooling). Don't force a diagram where one doesn't add clarity. Ends with a
**"Check Your Understanding"** section: a bank of 6-10 conceptual
questions (invariants, complexity, edge cases — never "write the code for
X"). Only a random subset gets asked per comprehension check, so
revisiting a topic later doesn't mean reciting the same memorized answers.

## `<TOPIC>_TASK.md` conventions

The work order: concrete list of operations/functions to implement, their
expected behavior and edge cases, and an example stub as a Kotlin code
block for the user to reference when creating their own copy of the file.
This is what to glance at mid-implementation instead of re-reading the
lesson, and doubles as a cold-drill sheet on revisit.

Where the decomposition into base case/recursive case (or the relevant
invariant, for non-recursive structures) isn't obvious just from the
behavior description, add a one-line framing question — not the answer,
a pointed question that gives a concrete entry point instead of a blank
page. Skip it where the spec already states the recursive relationship
directly (e.g. Fibonacci's definition *is* its recursive case).

## Repo structure

- Real Gradle Kotlin project. `./gradlew test` is the feedback loop —
  red/green, not manual inspection.
- No numbered folders: Kotlin package names can't start with a digit or
  contain hyphens, so topic order lives in `ROADMAP.md`, not in directory
  names. Packages are grouped by category (`foundations`, `linkedlist`,
  `trees`, `graphs`, …).
- Each topic's `<TOPIC>_NOTES.md` and `<TOPIC>_TASK.md` (SCREAMING_SNAKE_CASE,
  matching `ROADMAP.md`/`GLOSSARY.md`) live next to its stub `.kt` file(s)
  under `src/main/kotlin/<package>/`. Naming is scoped to the topic, not the
  package, since multiple topics can share a package folder (e.g.
  `linkedlist/` holding both Singly and Doubly Linked List) without their
  docs colliding. The matching test file lives under
  `src/test/kotlin/<package>/`.
- Test framework: Kotest (`FunSpec` style unless a topic's shape calls for
  something else).

## Commit style

- Tim Pope 50/72: imperative subject line ≤50 chars, blank line, body
  wrapped at 72 chars.
- No `Co-Authored-By` trailer.
- Daily commits are the expected cadence.

## Shell commands

Give single-line, copy-pasteable shell commands.
