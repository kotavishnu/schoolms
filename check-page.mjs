import { chromium } from 'playwright';

async function checkStudentsPage() {
  console.log('Launching browser...');
  const browser = await chromium.launch({ headless: false });
  const page = await browser.newPage();

  // Capture console logs
  page.on('console', msg => console.log(`BROWSER ${msg.type().toUpperCase()}: ${msg.text()}`));

  // Capture errors
  page.on('pageerror', err => console.log(`PAGE ERROR: ${err.message}`));

  // Capture network activity
  const apiRequests = [];
  page.on('request', req => {
    if (req.url().includes('/api/')) {
      apiRequests.push({ method: req.method(), url: req.url() });
      console.log(`→ ${req.method()} ${req.url()}`);
    }
  });

  page.on('response', res => {
    if (res.url().includes('/api/')) {
      console.log(`← ${res.status()} ${res.url()}`);
    }
  });

  try {
    console.log('\nNavigating to http://localhost:5173/students...\n');
    await page.goto('http://localhost:5173/students', { waitUntil: 'networkidle', timeout: 10000 });

    await page.waitForTimeout(2000);

    // Check for errors
    const errorDiv = await page.$('.bg-red-50');
    if (errorDiv) {
      const errorText = await errorDiv.textContent();
      console.log('\n❌ ERROR FOUND:', errorText);
    } else {
      console.log('\n✅ No error displayed on page');
    }

    // Check for students table
    const students = await page.$$('tbody tr');
    console.log(`✅ Found ${students.length} student rows`);

    // Take screenshot
    await page.screenshot({ path: 'D:\\wks-sms-specs\\students-screenshot.png', fullPage: true });
    console.log('✅ Screenshot saved to students-screenshot.png');

    console.log('\n📊 API Requests made:', apiRequests.length);

  } catch (error) {
    console.error('\n❌ Navigation failed:', error.message);
  } finally {
    await browser.close();
  }
}

checkStudentsPage().catch(console.error);
