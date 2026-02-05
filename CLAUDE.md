Before executing complex tasks, review the "Global Directives" section in the LESSONS_LEARNED.md file first to avoid repeating past errors. When a new error is encountered and resolved, append a new entry to the Execution_Log under the Backend section if you are the backend agent, or the Frontend section if you are the frontend agent. At the end of each entry record a statement on Lesson Learned, alongwith a Resulting Directive key to the Global Directives if it is a recurring pattern; else record that "None added to Global Directives yet, but noting as a preferred pattern". Use format "[D-001]" for Global Directives key, and "Entry ID:[2026-02-03_01]" for Execution_Log entry key. Additionally, update "Global Directives" if a new rule or lesson learned is established.

# Example of LESSONS_LEARNED.md
## Global Directives
[D-001]: Spring OpenAPI: Always check Spring Boot and SpringDoc compatibility matrix when upgrading Spring Boot versions. Add version compatibility testing to CI/CD pipeline. SpringDoc OpenAPI compatibility reference:

Spring Boot 3.0.x - 3.2.x: SpringDoc 2.0.x - 2.3.x SpringBoot 3.3.x - 3.5.x: SpringDoc 2.5.x - 2.7.x+

## Execution Log (Chronological)
**Entry_ID:** 2026-02-03_1 **Task:** Backend QA Verification - Iteration 3 Code-Level Verification Agent: Backend QA Orchestrator Date: 2026-02-03

**Observation/Issue

**Analysis**

**Corrective Actions Taken
- Service startup validation
** Resulting Directive: [D-001]

#TOKEN BUDGET
Ensure strict adherence to token budget.
