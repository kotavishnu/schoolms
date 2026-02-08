const fs = require('fs');
const { PNG } = require('pngjs');
const pixelmatch = require('pixelmatch').default;

function compareImages(refPath, currPath, diffPath, threshold = 0.05) {
  try {
    const refImg = PNG.sync.read(fs.readFileSync(refPath));
    let currImg = PNG.sync.read(fs.readFileSync(currPath));

    const { width, height } = refImg;

    // Resize current image if dimensions don't match using simple nearest-neighbor
    if (currImg.width !== width || currImg.height !== height) {
      console.log(`RESIZE_INFO:ref=${width}x${height},curr=${currImg.width}x${currImg.height}`);

      const resized = new PNG({ width, height });
      const xRatio = currImg.width / width;
      const yRatio = currImg.height / height;

      for (let y = 0; y < height; y++) {
        for (let x = 0; x < width; x++) {
          const srcX = Math.floor(x * xRatio);
          const srcY = Math.floor(y * yRatio);
          const srcIdx = (srcY * currImg.width + srcX) << 2;
          const dstIdx = (y * width + x) << 2;

          resized.data[dstIdx] = currImg.data[srcIdx];
          resized.data[dstIdx + 1] = currImg.data[srcIdx + 1];
          resized.data[dstIdx + 2] = currImg.data[srcIdx + 2];
          resized.data[dstIdx + 3] = currImg.data[srcIdx + 3];
        }
      }

      currImg = resized;
    }

    const diff = new PNG({ width, height });

    const numDiffPixels = pixelmatch(
      refImg.data,
      currImg.data,
      diff.data,
      width,
      height,
      { threshold: 0.1 }
    );

    const totalPixels = width * height;
    const diffPercentage = (numDiffPixels / totalPixels) * 100;

    fs.writeFileSync(diffPath, PNG.sync.write(diff));

    console.log(`DIFF_PERCENTAGE:${diffPercentage.toFixed(2)}`);
    console.log(`DIFF_PIXELS:${numDiffPixels}`);
    console.log(`TOTAL_PIXELS:${totalPixels}`);
    console.log(`DIMENSIONS:${width}x${height}`);

  } catch (error) {
    console.error(`ERROR:${error.message}`);
    process.exit(1);
  }
}

const args = process.argv.slice(2);
if (args.length < 3) {
  console.error('Usage: node compare-images.js <refPath> <currPath> <diffPath> [threshold]');
  process.exit(1);
}

compareImages(args[0], args[1], args[2], parseFloat(args[3]) || 0.05);
