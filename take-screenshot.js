const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext({
    viewport: { width: 1920, height: 1080 }
  });
  const page = await context.newPage();

  try {
    console.log('Navigating to configurations page...');
    await page.goto('http://localhost:5173/configurations', {
      waitUntil: 'networkidle',
      timeout: 30000
    });

    console.log('Waiting for page to stabilize...');
    await page.waitForTimeout(2000);

    console.log('Taking screenshot...');
    await page.screenshot({
      path: 'D:\\SCHOOL-GIT-LESSONS_LEARNT\\schoolms\\configurations-actions-screenshot.png',
      fullPage: true
    });

    console.log('Screenshot saved successfully!');

  } catch (error) {
    console.error('Error:', error.message);
  } finally {
    await browser.close();
  }
})();
