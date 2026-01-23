import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: false });
  const context = await browser.newContext({
    viewport: { width: 1280, height: 720 }
  });
  const page = await context.newPage();

  try {
    console.log('Navigating to configurations page...');
    await page.goto('http://localhost:5173/configurations');
    await page.waitForTimeout(2000);

    // Take screenshot of configurations page
    await page.screenshot({ path: 'qa-screenshots/1-configurations-page.png', fullPage: true });
    console.log('Screenshot saved: 1-configurations-page.png');

    // Check if there are any configuration entries
    const hasConfigs = await page.locator('table tbody tr').count() > 0;

    if (hasConfigs) {
      // Click Edit button on first configuration
      console.log('Clicking Edit button on first configuration...');
      const editButton = page.locator('button:has-text("Edit")').first();
      await editButton.click();
      await page.waitForTimeout(1000);

      // Take screenshot of edit dialog
      await page.screenshot({ path: 'qa-screenshots/2-edit-dialog-opened.png', fullPage: true });
      console.log('Screenshot saved: 2-edit-dialog-opened.png');

      // Capture form field details
      const categoryValue = await page.locator('[role="combobox"]').first().textContent();
      const keyValue = await page.locator('input#key').inputValue();
      const valueValue = await page.locator('input#value').inputValue();
      const descriptionValue = await page.locator('textarea#description').inputValue();

      console.log('\n=== Edit Dialog Form Field Values ===');
      console.log('Category:', categoryValue);
      console.log('Key:', keyValue);
      console.log('Value:', valueValue);
      console.log('Description:', descriptionValue);

      // Check if key field is disabled
      const keyDisabled = await page.locator('input#key').isDisabled();
      console.log('Key field disabled:', keyDisabled);

      // Close dialog
      await page.locator('button:has-text("Cancel")').click();
      await page.waitForTimeout(500);
    } else {
      console.log('No configurations found. Creating a new one...');

      // Click "Add New Configuration" button
      await page.locator('button:has-text("Add New Configuration")').click();
      await page.waitForTimeout(1000);

      // Take screenshot of create dialog
      await page.screenshot({ path: 'qa-screenshots/2-create-dialog-opened.png', fullPage: true });
      console.log('Screenshot saved: 2-create-dialog-opened.png');

      // Fill form
      console.log('Filling form...');
      await page.locator('[role="combobox"]').first().click();
      await page.locator('[role="option"]:has-text("General")').click();
      await page.locator('input#key').fill('TEST_CONFIG_KEY');
      await page.locator('input#value').fill('Test Configuration Value');
      await page.locator('textarea#description').fill('This is a test configuration for QA testing');

      // Take screenshot with filled form
      await page.screenshot({ path: 'qa-screenshots/3-create-dialog-filled.png', fullPage: true });
      console.log('Screenshot saved: 3-create-dialog-filled.png');

      // Click Add Configuration
      await page.locator('button:has-text("Add Configuration")').click();
      await page.waitForTimeout(2000);

      // Take screenshot after creation
      await page.screenshot({ path: 'qa-screenshots/4-after-creation.png', fullPage: true });
      console.log('Screenshot saved: 4-after-creation.png');

      // Now click Edit on the newly created config
      console.log('Clicking Edit on newly created configuration...');
      const editButton = page.locator('button:has-text("Edit")').first();
      await editButton.click();
      await page.waitForTimeout(1000);

      // Take screenshot of edit dialog
      await page.screenshot({ path: 'qa-screenshots/5-edit-dialog-opened.png', fullPage: true });
      console.log('Screenshot saved: 5-edit-dialog-opened.png');

      // Check if key field is disabled in edit mode
      const keyDisabled = await page.locator('input#key').isDisabled();
      console.log('Key field disabled in edit mode:', keyDisabled);
    }

    console.log('\nTest completed successfully!');
  } catch (error) {
    console.error('Error during test:', error);
    await page.screenshot({ path: 'qa-screenshots/error.png', fullPage: true });
  } finally {
    await browser.close();
  }
})();
