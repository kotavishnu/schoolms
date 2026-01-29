# Frontend Design Specification: Figma Token Implementation

## 1. Objective
Establish a robust, efficient workflow to implement design tokens from Figma into the frontend code with a 1:1 mapping. This ensures design consistency and simplifies updates.

## 2. Architecture Overview

- **Source of Truth**: Figma Variables/Tokens.
- **Implementation Layer**: CSS Variables defined in `frontend/styles/theme.css`.
- **Consumer Layer**: Tailwind CSS utility classes referencing these variables.

## 3. Token Strategy

To maintain a 1:1 match with Figma while preserving the existing component library (shadcn/ui) functionality, we will use a **Tiered Token System**.

### 3.1. Tier 1: Primitive Tokens (Global)
These are the raw values exported from Figma (e.g., color palettes, base spacing). They should be named exactly as they appear in Figma.

**Location**: `frontend/styles/theme.css` inside `:root`

```css
:root {
  /* Figma Primitive Tokens */
  --blue-500: #3b82f6;
  --slate-100: #f1f5f9;
  --radius-sm: 4px;
  /* ... */
}
```

### 3.2. Tier 2: Semantic Tokens (System)
These map existing system concepts (Primary, Secondary, Destructive) to the Primitive Tokens. This bridges the Figma design with the React component library.

**Location**: `frontend/styles/theme.css`

```css
:root {
  /* Mapping Semantic Names to Primitives */
  --primary: var(--blue-500); 
  --muted: var(--slate-100);
  /* ... */
}
```

## 4. Implementation Guidelines

### 4.1. CSS Variable Definitions
- **File**: `frontend/styles/theme.css`
- **Format**: 
  - Use exact naming from Figma for primitives.
  - Map system variables (required by `button.tsx`, `card.tsx` etc.) to these primitives.
  - Use `oklch` or `hex` consistently based on Figma export.

### 4.2. Tailwind Configuration
- The project uses Tailwind v4 (implied by syntax).
- Ensure variables are accessible to Tailwind. The current `theme.css` method exposes them automatically as arbitrary values or extended theme values if configured.
- Existing `tailwind.css` imports `@import 'tailwindcss'`.

### 4.3. Component Usage
- **Do not hardcode values**. Always use Tailwind utility classes that reference the tokens.
- **Example**:
  - ❌ `bg-[#3b82f6]`
  - ✅ `bg-primary` (Semantic) or `bg-[var(--blue-500)]` (Specific Figma Token if needed).

## 5. Workflow for Updates

1.  **Export**: Export variables from Figma (using a plugin or native feature) to CSS format.
2.  **Update Primitives**: Replace the "Primitive Tokens" section in `theme.css` with the new export.
3.  **Verify Mappings**: Ensure semantic tokens (`--primary`, `--background`) point to the correct new primitives.
4.  **No Logic Changes**: Avoid changing component logic (`.tsx`) for style updates; rely solely on `theme.css` changes.

## 6. Token Budget & Efficiency
- **Minimize Duplication**: Do not redefine values. Reuse primitives.
- **Clean Up**: Remove unused variables in `theme.css` that are not linked to any component or semantic token.
- **Scoped Theming**: Use `.dark` class in `theme.css` to re-map semantic tokens for dark mode without duplicating primitive definitions.

```css
/* Example Efficiency Structure */
:root {
  /* 1. Define Primitives once */
  --color-brand-main: #030213;
  --color-neutral-100: #ffffff;
}

/* 2. Map Semantics */
:root {
  --primary: var(--color-brand-main);
  --background: var(--color-neutral-100);
}

.dark {
  /* Re-map Semantics only */
  --primary: var(--color-neutral-100); 
  --background: var(--color-brand-main);
}
```
