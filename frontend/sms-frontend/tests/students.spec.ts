import { test, expect } from '@playwright/test';

test.describe('Student Management Page', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:5174/students');
    await page.waitForLoadState('networkidle');
  });

  test('should load the student list page', async ({ page }) => {
    // Check page title and header
    await expect(page.locator('h1')).toContainText('Students');
    await expect(page.locator('text=Manage student registrations and profiles')).toBeVisible();
  });

  test('should have Add New Student button', async ({ page }) => {
    const addButton = page.locator('text=Add New Student');
    await expect(addButton).toBeVisible();
    await expect(addButton).toBeEnabled();

    // Test navigation
    await addButton.click();
    await expect(page).toHaveURL(/\/students\/new/);
  });

  test('should have search filters', async ({ page }) => {
    // Check search form elements
    await expect(page.locator('input[name="firstName"]')).toBeVisible();
    await expect(page.locator('input[name="lastName"]')).toBeVisible();
    await expect(page.locator('input[name="mobile"]')).toBeVisible();
    await expect(page.locator('select[name="status"]')).toBeVisible();

    // Check search buttons
    await expect(page.locator('button[type="submit"]:has-text("Search")')).toBeVisible();
    await expect(page.locator('button:has-text("Clear Filters")')).toBeVisible();
  });

  test('should perform search', async ({ page }) => {
    // Fill search form
    await page.fill('input[name="firstName"]', 'John');
    await page.fill('input[name="lastName"]', 'Doe');

    // Submit search
    await page.click('button[type="submit"]:has-text("Search")');

    // Wait for results
    await page.waitForTimeout(1000);
  });

  test('should clear filters', async ({ page }) => {
    // Fill search form
    await page.fill('input[name="firstName"]', 'John');
    await page.fill('input[name="lastName"]', 'Doe');

    // Click clear filters
    await page.click('button:has-text("Clear Filters")');

    // Verify fields are cleared
    await expect(page.locator('input[name="firstName"]')).toHaveValue('');
    await expect(page.locator('input[name="lastName"]')).toHaveValue('');
  });

  test('should display student table', async ({ page }) => {
    // Check table headers
    const headers = [
      'Student ID',
      'Name',
      'Age',
      'Mobile',
      'Status',
      'Created',
      'Actions'
    ];

    for (const header of headers) {
      await expect(page.locator(`th:has-text("${header}")`)).toBeVisible();
    }
  });

  test('should have View and Edit buttons for each student', async ({ page }) => {
    // Wait for table to load
    await page.waitForSelector('tbody tr', { timeout: 5000 }).catch(() => {});

    // Check if there are students in the table
    const rows = await page.locator('tbody tr').count();

    if (rows > 0 && !await page.locator('text=No students found').isVisible()) {
      // Check first student row has action buttons
      const firstRow = page.locator('tbody tr').first();
      await expect(firstRow.locator('button:has-text("View")')).toBeVisible();
      await expect(firstRow.locator('button:has-text("Edit")')).toBeVisible();

      // Test View button navigation
      const studentIdLink = firstRow.locator('a[href*="/students/"]').first();
      const studentId = await studentIdLink.textContent();
      await studentIdLink.click();
      await expect(page).toHaveURL(new RegExp(`/students/${studentId?.trim()}`));
    }
  });

  test('should display pagination when multiple pages exist', async ({ page }) => {
    // Wait for page to load
    await page.waitForTimeout(1000);

    // Check if pagination exists
    const paginationExists = await page.locator('text=Showing page').isVisible().catch(() => false);

    if (paginationExists) {
      await expect(page.locator('button:has-text("Previous")')).toBeVisible();
      await expect(page.locator('button:has-text("Next")')).toBeVisible();
    }
  });

  test('should test all clickable elements', async ({ page }) => {
    // Get all buttons and links
    const buttons = await page.locator('button').all();
    const links = await page.locator('a').all();

    console.log(`Found ${buttons.length} buttons and ${links.length} links`);

    // Test that all buttons are visible
    for (let i = 0; i < buttons.length; i++) {
      const button = buttons[i];
      const isVisible = await button.isVisible().catch(() => false);
      if (isVisible) {
        const text = await button.textContent();
        console.log(`Button ${i + 1}: ${text?.trim()}`);
      }
    }

    // Test that all links are visible
    for (let i = 0; i < links.length; i++) {
      const link = links[i];
      const isVisible = await link.isVisible().catch(() => false);
      if (isVisible) {
        const text = await link.textContent();
        const href = await link.getAttribute('href');
        console.log(`Link ${i + 1}: ${text?.trim()} -> ${href}`);
      }
    }
  });

  test('should check for UI/layout issues', async ({ page }) => {
    // Take a screenshot for visual review
    await page.screenshot({ path: 'tests/screenshots/students-page.png', fullPage: true });

    // Check for common UI issues
    const overlappingElements = await page.evaluate(() => {
      const elements = document.querySelectorAll('button, a, input');
      const issues = [];

      for (let i = 0; i < elements.length; i++) {
        const elem = elements[i] as HTMLElement;
        const rect = elem.getBoundingClientRect();

        // Check if element is too small
        if (rect.width < 10 || rect.height < 10) {
          issues.push(`Element too small: ${elem.tagName} - ${elem.textContent?.substring(0, 20)}`);
        }

        // Check if element is off screen
        if (rect.left < 0 || rect.top < 0) {
          issues.push(`Element off screen: ${elem.tagName} - ${elem.textContent?.substring(0, 20)}`);
        }
      }

      return issues;
    });

    console.log('UI Issues found:', overlappingElements);
    expect(overlappingElements.length).toBe(0);
  });

  test('should check responsiveness', async ({ page }) => {
    // Test mobile view
    await page.setViewportSize({ width: 375, height: 667 });
    await page.waitForTimeout(500);
    await expect(page.locator('h1')).toBeVisible();

    // Test tablet view
    await page.setViewportSize({ width: 768, height: 1024 });
    await page.waitForTimeout(500);
    await expect(page.locator('h1')).toBeVisible();

    // Test desktop view
    await page.setViewportSize({ width: 1920, height: 1080 });
    await page.waitForTimeout(500);
    await expect(page.locator('h1')).toBeVisible();
  });
});
