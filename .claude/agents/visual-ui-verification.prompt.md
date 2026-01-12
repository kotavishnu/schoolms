# Task: Visual UI Verification and Fix with Playwright

**Agent:** `@frontend-qa-orchestrator`

**Objective:** 
Iteratively verify and fix UI styling issues by comparing the live application against reference screenshots. Ensure design elements (colors, spacing, typography, layout) match perfectly.

**Input Data:**
(User: Paste your JSON map of URLs and Reference Screenshot paths below)
```json
{
  screenshots_map: {
   
    "UI_Navigate_Steps":"Navigate to the URL http://localhost:5173/ and take a screenshot of the current UI state for comparison.",
    "original_screenshot": "screenshots/Home Page.PNG"
  }  ,
  {
    "UI_Navigate_Steps":"Navigate to the URL http://localhost:5173/students and click on 'Register New Student' and take a screenshot of the current UI state for comparison.",
    "original_screenshot": "screenshots/Student Register.PNG"
  },
  {
    "UI_Navigate_Steps":"Navigate to the URL http://localhost:5173/configurations and click on button 'Register New Student' and take a screenshot of the current UI state for comparison.",
    "original_screenshot": "screenshots/Configuration Home.PNG"
  },
  {
    "UI_Navigate_Steps":"Navigate to the URL http://localhost:5173/students and click on button 'Edit' and take a screenshot of the current UI state for comparison.",
    "original_screenshot": "screenshots/Student Edit-1.PNG"
  },
  {
    "UI_Navigate_Steps":"Navigate to the URL http://localhost:5173/students and click on button 'View Details' and take a screenshot of the current UI state for comparison.",
    "original_screenshot": "screenshots/Student Details.PNG"
  }


  "http://localhost:5173/students": "screenshots/Student Details.png",
}
```

**Instructions:**

1.  **Preparation**
    -   Ensure the frontend development server is running (e.g., `npm run dev` @ `frontend/app`).
    -   Verify that all reference screenshots exist at the specified paths.

2.  **Iterative Verification Loop**
    For each `(url, reference_screenshot)` in the Input Data:

    a.  **Capture Live State**
        -   Use `Playwright_Navigate` to visit the `url`.
        -   Use `Playwright_Screenshot` to capture the current state of the UI (save as `current_verification.png`).

    b.  **Visual Comparison & Analysis**
        -   Critically compare `current_verification.png` (Live) vs `reference_screenshot` (Design).
        -   Check for:
            -   **Colors:** Hex codes match branding? Backgrounds, buttons, text.
            -   **Typography:** Font families, weights, sizes, line heights.
            -   **Spacing:** Margins, padding, gaps between elements.
            -   **Layout:** Alignment, grid structure, responsive behavior.
            -   **Components:** Rounded corners, shadows, border styles.
        -   *Note: If the visual difference is significant, lists the specific CSS/Tailwind discrepancies.*

    c.  **Fixing Issues (Iterative)**
        -   If mismatches are found:
            1.  **Locate Code:** Identify the React component responsible for the mismatched UI (use `grep` or `file_search` if needed).
            2.  **Read Code:** Read the component file to understand current styling.
            3.  **Apply Fix:** Use `replace_string_in_file` to update Tailwind classes or CSS to match the design.
            4.  **Verify Fix:** Refresh the page (or navigate again) and take a new screenshot.
            5.  **Re-evaluate:** Compare again. Repeat until the UI matches the reference.

    d.  **Reporting**
        -   Log the status for each URL: `[MATCH]` or `[FIXED]`.
        -   Summarize any changes made to the codebase.

3.  **Final Output**
    -   Provide a summary of all styling fixes applied.
    -   Confirm that all pages in the map now visually match the designs.

**Constraints:**
-   Do not modify logic or functional behavior (e.g., validation rules), only styles.
-   Maintain existing responsive behavior unless it violates the design.
-   Use Tailwind utility classes over custom CSS whenever possible.
