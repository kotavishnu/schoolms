# Configuration Dialog UI/UX Issues Report

**Date**: 2026-01-23
**Component**: ConfigurationDialog.tsx
**Page**: ConfigurationsPage.tsx
**Test URL**: http://localhost:5173/configurations

---

## Executive Summary

Conducted comprehensive UI testing of the Configuration Edit Dialog against design specifications. Identified **7 UI/UX issues** ranging from Critical to Low priority that need to be addressed to match the design specifications exactly.

---

## Critical Issues (Must Fix)

### Issue #1: Description Field Incorrectly Marked as Required
**Severity**: CRITICAL
**File**: `src/components/configurations/ConfigurationDialog.tsx` (Line 148)

**Current Behavior**:
- Description field shows red asterisk `*` indicating it's required
- Code: `<Label htmlFor="description">Description <span className="text-red-500">*</span></Label>`

**Expected Behavior** (Per Design Spec):
- Description should be OPTIONAL (no asterisk)
- Design Spec Line 307: `description: { maxLength: 500 }` - No "required" mentioned
- Validation Schema Line 112-115: Correctly marked as `.optional()`

**Impact**:
- Confuses users - they think description is mandatory when it's not
- Inconsistent with validation schema
- Prevents submission when users leave it empty (if client validation is strict)

**Fix Required**:
```tsx
// Line 148 - BEFORE
<Label htmlFor="description">Description <span className="text-red-500">*</span></Label>

// Line 148 - AFTER
<Label htmlFor="description">Description</Label>
```

**Evidence**:
- Screenshot: `qa-screenshots/2-edit-dialog-opened.png`
- Design Reference: `screenshots/configuration-dialog-edit.png`

---

### Issue #2: Category Select Showing Wrong Value in Edit Mode
**Severity**: CRITICAL
**File**: `src/components/configurations/ConfigurationDialog.tsx` (Lines 103-116)

**Current Behavior**:
- When opening Edit dialog, Category dropdown shows "All Categories"
- Test output confirms: `Category: All Categories`
- Actual category is "ACADEMIC" but not displayed

**Expected Behavior**:
- Should display the actual category of the configuration being edited
- Example: If editing ACADEMIC config, dropdown should show "Academic"

**Root Cause**:
- The `watch('category')` is not properly reflecting the form state
- The `reset()` in `useEffect` may not be properly updating the Select component
- Select component may need to be re-mounted or forced to update

**Impact**:
- Users cannot see which category they're editing
- Major data integrity risk - users might accidentally change category
- Violates design specification requirement

**Fix Required**:
```tsx
// Current problematic code (Lines 103-116)
<Select
  value={watch('category')}
  onValueChange={(value: any) => setValue('category', value)}
>

// Suggested fix - Add key to force re-render
<Select
  key={`category-${configuration?.category || 'new'}`}
  value={watch('category') || configuration?.category}
  onValueChange={(value: any) => setValue('category', value, { shouldValidate: true })}
>
```

**Evidence**:
- Screenshot: `qa-screenshots/2-edit-dialog-opened.png`
- Test output log showing "All Categories"

---

### Issue #3: Key Field Placeholder Not Matching Design
**Severity**: MEDIUM
**File**: `src/components/configurations/ConfigurationDialog.tsx` (Line 130)

**Current Behavior**:
- Placeholder text: `placeholder="e.g., school_name"`
- Shows as gray text when key field is disabled in edit mode

**Expected Behavior** (Per Design Spec):
- Should show placeholder in blue/purple color (like in reference screenshot)
- In edit mode, the key value should be clearly visible (not as placeholder)

**Current Code**:
```tsx
// Line 128-133
<Input
  id="key"
  {...register('key')}
  placeholder="e.g., school_name"
  disabled={isEditMode}
  className={isEditMode ? 'bg-gray-100 cursor-not-allowed' : ''}
/>
```

**Fix Required**:
- Update placeholder styling to match design
- Ensure disabled state has proper visual hierarchy

**Evidence**:
- Screenshot comparison: `qa-screenshots/2-edit-dialog-opened.png` vs `screenshots/configuration-dialog-edit.png`

---

## Medium Issues (Should Fix)

### Issue #4: Dialog Background Overlay Styling
**Severity**: MEDIUM
**File**: `src/components/configurations/ConfigurationDialog.tsx` (Line 86-87)

**Current Behavior**:
- Dark gray overlay behind dialog
- Background page content is dimmed but still visible

**Expected Behavior**:
- Reference screenshot shows clean dialog with subtle overlay
- Better visual separation from background content

**Impact**:
- Visual clutter - users can see table rows behind dialog
- Less professional appearance
- Doesn't match design specification

**Fix Required**:
- Review Shadcn Dialog component overlay styling
- May need to add custom overlay class

---

### Issue #5: Dialog Width Inconsistency
**Severity**: LOW
**File**: `src/components/configurations/ConfigurationDialog.tsx` (Line 87)

**Current Behavior**:
- Dialog uses `className="max-w-lg"` (32rem / 512px)
- Appears slightly wider than reference design

**Expected Behavior**:
- Reference design shows more compact dialog
- Should match exact width from Figma specifications

**Current Code**:
```tsx
<DialogContent className="max-w-lg">
```

**Recommendation**:
- Verify with design specs if `max-w-md` (28rem / 448px) is more accurate
- Or use exact pixel value if specified in design

---

### Issue #6: Form Field Vertical Spacing
**Severity**: LOW
**File**: `src/components/configurations/ConfigurationDialog.tsx` (Line 97)

**Current Behavior**:
- Form uses `className="space-y-4"` (1rem / 16px spacing)

**Expected Behavior**:
- Reference design may have slightly tighter spacing
- Should verify exact spacing from design specifications

**Impact**:
- Minor visual difference
- Dialog height slightly taller than reference

---

### Issue #7: Button Text Casing
**Severity**: LOW
**File**: `src/components/configurations/ConfigurationDialog.tsx` (Line 166)

**Current Behavior**:
- Button text: "Update Configuration" / "Add Configuration"

**Expected Behavior** (from reference):
- Verify if button text should match exactly
- Reference shows: "Update Configuration"

**Status**: ✅ MATCHES - No fix needed, just documenting for completeness

---

## Testing Evidence

### Screenshots Captured:
1. `qa-screenshots/1-configurations-page.png` - Full page view
2. `qa-screenshots/2-edit-dialog-opened.png` - Edit dialog with issues visible

### Test Output:
```
=== Edit Dialog Form Field Values ===
Category: All Categories  ← ISSUE #2: Wrong value displayed
Key: CURRENT_ACADEMIC_YEAR
Value: 2025-2026
Description: Current academic year
Key field disabled: true  ← Correct behavior
```

---

## Compliance Check Against Design Specification

### FRONTEND_DESIGN_SPECIFICATION.md Requirements:

#### ConfigurationDialog Specifications (Lines 293-312):

| Requirement | Status | Notes |
|-------------|--------|-------|
| **Fields**: Category (select) | ✅ PASS | Implemented |
| **Fields**: Key (text) | ✅ PASS | Implemented |
| **Fields**: Value (text) | ✅ PASS | Implemented |
| **Fields**: Description (textarea) | ⚠️ PARTIAL | Present but marked as required (should be optional) |
| **Validation**: Category required | ✅ PASS | Implemented |
| **Validation**: Key required, max 100 chars | ✅ PASS | Implemented |
| **Validation**: Key pattern `/^[A-Z0-9_]+$/` | ✅ PASS | Implemented |
| **Validation**: Value required, max 1000 chars | ✅ PASS | Implemented |
| **Validation**: Description max 500 chars, optional | ❌ FAIL | Schema correct, but UI shows as required |
| **Note**: Hide `dataType` field from UI | ✅ PASS | Not displayed |

#### Edit Mode Requirements (Lines 273-275):

| Requirement | Status | Notes |
|-------------|--------|-------|
| Key field disabled in edit mode | ✅ PASS | Correctly disabled with gray background |
| Category select functional | ❌ FAIL | Shows wrong value in edit mode |
| Value field editable | ✅ PASS | Editable |
| Description field editable | ✅ PASS | Editable |

---

## Recommendations

### Immediate Actions (Critical):
1. **Remove asterisk from Description field** (Issue #1) - 2 min fix
2. **Fix Category dropdown value display in edit mode** (Issue #2) - 30 min fix

### Short-term Actions (Medium):
3. **Verify and adjust dialog overlay styling** (Issue #4) - 15 min
4. **Review and adjust dialog width** (Issue #5) - 10 min

### Nice-to-have (Low Priority):
5. **Fine-tune field spacing if needed** (Issue #6) - 10 min

---

## Next Steps

1. Apply fixes for Critical issues (#1, #2)
2. Re-run Playwright test to verify fixes
3. Capture new screenshots for comparison
4. Update validation with stakeholders
5. Mark issues as resolved in tracking system

---

## References

- Design Spec: `specs/FRONTEND_DESIGN_SPECIFICATION.md` (Lines 293-312)
- Reference Screenshot: `screenshots/configuration-dialog-edit.png`
- Reference Screenshot: `screenshots/configuration-dialog-add.png`
- Validation Schema: `src/utils/validation.ts` (Lines 95-116)
- Component: `src/components/configurations/ConfigurationDialog.tsx`

---

**Report Generated**: 2026-01-23
**Tester**: Senior Frontend QA Verification Agent
**Status**: ISSUES IDENTIFIED - FIXES REQUIRED
