# CLAUDE.md

Guidance for Claude Code sessions working in this repo.

## What this repo is

A from-scratch data structures & algorithms practice repo in Kotlin, built
for long-term retention and future interview readiness. The primary gap
being addressed is DSA depth, not Kotlin itself — but the user is still
building Kotlin fluency, so occasional syntax friction is expected and
normal, separate from the DSA learning goal.

See `ROADMAP.md` for the full topic sequence and phase rationale, and
`GLOSSARY.md` for terminology used in `*_NOTES.md` files. Each
`GLOSSARY.md` term must be a `##` heading, not a bold paragraph —
`*_NOTES.md` files link into specific terms via anchors (e.g.
`GLOSSARY.md#amortized-complexity`), and GitHub only auto-generates
anchor IDs for real headings.

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
- **Post-implementation refactors are fair game, concretely.** Once the
  user has a *working, correct* implementation, Claude may show the
  actual rewritten code and apply it on request. The learning happens in
  getting it correct, so hedging into a generic analogue at that point is
  unhelpful, not principled. The rules above still hold for anything that
  doesn't work yet — "refactoring" a broken function is just supplying
  the solution with extra steps.
- **Claude writes:** `<TOPIC>_NOTES.md`, `<TOPIC>_TASK.md`, and the test
  file under `src/test/kotlin/`. The test file is expected to fail to
  compile until the user's stub exists with matching signatures — that's
  the first red state, before any assertions even run.

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
4. Claude writes the test file, then walks through what it covers and
   why — so coverage is understood, not just "passing."

## `<TOPIC>_NOTES.md` conventions

Covers: what the structure/algorithm is, its invariants, complexity
(time/space), when to reach for it. Include a diagram wherever it would
make a concept clearer than prose alone — ASCII art for linear structures
(arrays, linked lists, stacks, call stacks) and Mermaid diagrams for trees
and graphs (GitHub renders Mermaid natively in Markdown, no extra
tooling). Don't force a diagram where one doesn't add clarity. Ends with a
**"Check Your Understanding"** section: a bank of 6-10 conceptual
questions (invariants, complexity, edge cases — never "write the code for
X"). The bank is deliberately larger than any one check uses, so
revisiting a topic later isn't reciting memorized answers.

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
- **Don't restate in a commit what the notes or tests already cover.**
  Language gotchas, complexity explanations, and how a structure works
  belong in `<TOPIC>_NOTES.md`/`<TOPIC>_TASK.md`; edge cases belong in
  the test file. A commit body is for what changed and *why the code
  looks the way it does* — decisions and rejected alternatives that
  aren't visible in the diff. If a paragraph could sit unchanged in the
  notes, it belongs there instead.

## PR description

One commit per PR — squash locally before pushing rather than stacking
follow-up commits. Three sections, in this order, and nothing else:

1. `## <Structure or algorithm name>` — a short paragraph on what it is
   and what problem it solves. Enough that someone who hasn't read the
   notes knows what they're looking at.
2. `## Changes` — a table of the files touched and what each one is.
3. `## Design notes` — the decisions worth explaining: alternatives
   rejected and why, trade-offs, and any language traps that failed
   quietly rather than crashing.

## PR review

The user reviews via GitHub PR comments, then names the PR to address.
`gh` is installed and authenticated with `repo` scope, so Claude can
both read comments and reply to them.

Comments live at **three** separate endpoints — check all three, since
it's easy to address the inline ones and miss a summary:

```
gh api repos/{owner}/{repo}/pulls/{n}/comments    # inline, on specific lines
gh api repos/{owner}/{repo}/pulls/{n}/reviews     # overall review summaries
gh api repos/{owner}/{repo}/issues/{n}/comments   # the Conversation tab
```

Reply in-thread rather than only summarizing in chat, so the PR keeps a
self-contained record:

```
gh api repos/{owner}/{repo}/pulls/{n}/comments/{comment-id}/replies -f body='...'
```

Answer the question even when no code change results — several review
comments are "what does this do?" rather than "change this." Unsubmitted
review drafts are invisible to the API, so if the user expects comments
and none appear, they likely haven't submitted the review yet.

## Shell commands

Give single-line, copy-pasteable shell commands.
