# Kotlin Graph
Simple Kotlin DSL for streamlining state machine-like functionality.

# Motivation
I personally have run into a lot of relatively small scale problems that required state machines as solutions. It was very cumbersome to write and required a lot of boilerplate. This is an attempt to solve that problem.

# Features
 - State Machine like functionality.
 - State mutations
 - Custom Behaviour when upon entering and leaving a node.
 - Easy to maintain
 - Multiplatform support (Should work on any platform, but targets might need to be tweaked.)

# Usage
 - Define a State object (preferably mutable)
 - Define a Graph with the `graph()` entry point.
 - Define nodes with `node()`
   - Define behaviour for each step with `step()`
   - Optionally define behaviour for entering and exiting a node with `enter()` and `exit()`
   - Mark terminal nodes
 - provide an input (preferably immutable) and process with `process`

# Use Cases
 - Text Processors
 - Complex Logic that you don't want to dedicate an entire project to.

# Example

# TODO
 - Tests
 - Better utility functions

# Future Plans
## Graph Visualizer
Realistically a tool for visualizing node connections is doable, and relatively simple to create. I'd like to create a tool leveraging static code analysis to achieve this.