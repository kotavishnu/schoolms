## Project Goal
The School Management System (SMS) is a web-based digital platform designed to automate and optimize administrative workflows within schools. It provides a unified interface for managing student registration and school configuration.


## Phases and Steps

### Phase 1: Setup and Core Data Model
1.  **Initialize Project Structure:**
    *   Create necessary directories (e.g., `src`, `tests`).
    *   Set up a basic build system for Java.
2.  **Define Task Data Structure:**
    *   Create a data structure (e.g., a class or dictionary/object structure) to represent a `Task` with properties like `id`, `description`, and `is_completed`.
3.  **Implement Data Persistence (File-based):**
    *   Create functions to load tasks from a JSON file (e.g., `tasks.json`).
    *   Create functions to save tasks to the same JSON file.

### Phase 2: Core Task Operations
1.  **Add Task Functionality:**
    *   Implement a function to add a new task to the in-memory task list.
    *   Assign a unique ID to each new task.
    *   Persist the updated task list.
2.  **List Tasks Functionality:**
    *   Implement a function to retrieve and display all tasks.
    *   Format the output to clearly show task ID, description, and completion status.
3.  **Mark Task as Complete Functionality:**
    *   Implement a function to mark a specific task (by ID) as complete.
    *   Update the `is_completed` status of the task.
    *   Persist the updated task list.
4.  **Delete Task Functionality:**
    *   Implement a function to delete a specific task (by ID).
    *   Remove the task from the in-memory list.
    *   Persist the updated task list.

### Phase 3: Command-Line Interface (CLI)
1.  Build the application after implementation
2.	Run the server and make sure it is running successfully 

### Phase 4: Testing
1.  **Unit Tests:**
    *   Write unit tests for each core task management function (add, list, complete, delete).
    *   Ensure data persistence functions are also tested.
2.  **Integration Tests (Optional but Recommended):**
    *   Write integration tests to verify the end-to-end flow of CLI commands and their interaction with the data persistence layer.