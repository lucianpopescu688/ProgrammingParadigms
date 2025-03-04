# 🧸 Toy Language Interpreter

A Java-based interpreter for a custom toy language with an interactive GUI for visualizing program execution, concurrency, file I/O and memory management. Developed as part of the **Programming Paradigms** course at the Babes-Bolyai University in Cluj-Napoca, this project was built throughout the academic semester.

## 🌟 **GUI Features**

### 🖱️ **Interactive Program Execution**
- Pick over 12 pre-written examples(like arithmetic, heap operations, threading) using a simple dropdown menu
- **Step-by-Step Debugging**:
  - Execute one instruction at a time with the **One Step** button.
  - Track program state changes in real-time across tables and lists.

### 📊 **Visualization Panels**
- **Heap Table**: 
  - View dynamically allocated memory addresses and their values.
  - Track references and nested heap allocations (e.g., `Ref(Ref(int))`).
- **Symbol Table**: 
  - Inspect variable names, types, and current values.
  - Highlight variables modified during execution.
- **Execution Stack**: 
  - LIFO stack showing pending statements (e.g., loops, conditionals).
- **Lock Table**:
  - Monitor lock addresses and their status (free: `-1`, owned by thread ID).
  - Visualize thread contention and synchronization.
- **Output Console**:
  - Display program output.
  - Track file handles opened/closed during execution.
---

### 🛠️ **Tools & Libraries**
- **JavaFX**: Enables cross-platform GUI development and modern GUI.
- **IntelliJ IDEA**: For Java code editing and debugging.
- **Maven**: Dependency management and build automation.
