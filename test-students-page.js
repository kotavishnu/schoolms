const { chromium } = require('playwright');

async function testStudentsPage() {
  const browser = await chromium.launch({ headless: false });
  const context = await browser.newContext();
  const page = await context.newPage();

  // Listen for console messages
  page.on('console', msg => {
    console.log(`BROWSER ${msg.type()}: ${msg.text()}`);
  });

  // Listen for page errors
  page.on('pageerror', error => {
    console.log(`PAGE ERROR: ${error.message}`);
  });

  // Listen for network requests
  page.on('request', request => {
    if (request.url().includes('/api/')) {
      console.log(`REQUEST: ${request.method()} ${request.url()}`);
    }
  });

  // Listen for network responses
  page.on('response', response => {
    if (response.url().includes('/api/')) {
      console.log(`RESPONSE: ${response.status()} ${response.url()}`);
    }
  });

  try {
    console.log('Navigating to http://localhost:5173/students...');
    await page.goto('http://localhost:5173/students', { waitUntil: 'networkidle' });

    // Wait for the page to load
    await page.waitForTimeout(3000);

    // Take a screenshot
    await page.screenshot({ path: 'D:\\wks-sms-specs\\students-page.png', fullPage: true });
    console.log('Screenshot saved to D:\\wks-sms-specs\\students-page.png');

    // Check for error elements
    const errorElement = await page.$('.bg-red-50');
    if (errorElement) {
      const errorText = await errorElement.textContent();
      console.log('ERROR FOUND ON PAGE:', errorText);
    } else {
      console.log('No error element found on page');
    }

    // Check for student data
    const studentRows = await page.$$('tbody tr');
    console.log(`Found ${studentRows.length} student rows`);

    // Get page content
    const title = await page.title();
    console.log(`Page title: ${title}`);

  } catch (error) {
    console.error('Test failed:', error);
  } finally {
    await browser.close();
  }
}

testStudentsPage();
