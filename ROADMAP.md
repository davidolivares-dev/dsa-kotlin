# Roadmap

The authoritative learning order for this repo. Package names under
`src/main/kotlin/` are grouped by category, not numbered (Kotlin packages
can't start with a digit), so **this document — not folder names — is what
defines the sequence.**

Check items off as you complete them. Each topic gets a `<TOPIC>_NOTES.md`
next to its stub code, and a test file under the mirrored path in
`src/test/kotlin/`.

## Core

### Phase 0 — Foundations
- [x] Complexity analysis (time/space, amortized cost)
- [x] Recursion refresher

### Phase 1 — Linear structures
- [x] Arrays & Strings (in-place techniques)
- [x] Singly Linked List
- [x] Doubly Linked List
- [x] Stack
- [x] Queue (array-based + linked, circular buffer)

### Phase 2 — Hashing
- [ ] Hash Table (collision handling, resizing)
- [ ] Hash Set

### Phase 3 — Trees
- [ ] Binary Tree + traversals (DFS pre/in/post, BFS level-order)
- [ ] Binary Search Tree (insert/delete/search)
- [ ] Trie

### Phase 4 — Heaps
- [ ] Binary Heap (min/max)
- [ ] Priority Queue

### Phase 5 — Graphs
- [ ] Adjacency list/matrix representations
- [ ] BFS / DFS
- [ ] Topological sort
- [ ] Union-Find (Disjoint Set)
- [ ] Dijkstra's shortest path

### Phase 6 — Sorting & Searching
- [ ] Merge sort, quicksort, heap sort
- [ ] Binary search + variants (rotated array, search-on-answer)

### Phase 7 — Cross-cutting patterns
- [ ] Two pointers
- [ ] Sliding window
- [ ] Fast/slow pointers
- [ ] Backtracking (subsets/permutations/combinations)
- [ ] Greedy
- [ ] Intervals

### Phase 8 — Dynamic Programming (capstone)
- [ ] 1D DP
- [ ] 2D DP
- [ ] Knapsack family
- [ ] Common patterns (LIS, edit distance, coin change)

## Stretch (only if it earns its keep)
- [ ] AVL / Red-Black trees
- [ ] Segment tree / Fenwick tree
- [ ] Bellman-Ford, Floyd-Warshall, A*
- [ ] KMP / Rabin-Karp string matching
- [ ] Bit manipulation tricks

## Why this order
Linear structures come first as the lowest-friction refresher. Hashing
comes before trees because hash sets/maps get reused as a tool inside later
topics (e.g. graph visited-sets). Cross-cutting patterns (Phase 7) come
after the structures they lean on, so two-pointers/sliding-window land
right after arrays are solid, and backtracking/DP land after trees and
recursion are solid. Dijkstra rides along with Phase 5 rather than the
stretch tier because it's BFS plus the priority queue already built in
Phase 4 — the marginal cost is low and it shows up often enough in
"hard"-tier interview questions to be worth it.
