const http = require('http');
const fs = require('fs');
const path = require('path');

const PUBLIC_DIR = path.join(__dirname, 'public');

function requestHandler(req, res) {
  // CORS headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  // Health check endpoints for Cloud Run & AI Studio
  if (req.url === '/health' || req.url === '/api/health' || req.url === '/_health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      status: 'ok',
      service: 'Cooperative Gig Services Platform',
      timestamp: new Date().toISOString()
    }));
    return;
  }

  // Resolve static files safely
  const cleanUrl = req.url.split('?')[0];
  let filePath = path.join(PUBLIC_DIR, cleanUrl === '/' ? 'index.html' : cleanUrl);

  if (!filePath.startsWith(PUBLIC_DIR)) {
    filePath = path.join(PUBLIC_DIR, 'index.html');
  }

  fs.stat(filePath, (err, stats) => {
    if (err || !stats.isFile()) {
      filePath = path.join(PUBLIC_DIR, 'index.html');
    }

    const ext = path.extname(filePath).toLowerCase();
    const mimeTypes = {
      '.html': 'text/html; charset=UTF-8',
      '.js': 'application/javascript; charset=UTF-8',
      '.css': 'text/css; charset=UTF-8',
      '.json': 'application/json; charset=UTF-8',
      '.png': 'image/png',
      '.jpg': 'image/jpeg',
      '.svg': 'image/svg+xml',
      '.ico': 'image/x-icon'
    };

    const contentType = mimeTypes[ext] || 'application/octet-stream';

    fs.readFile(filePath, (readErr, data) => {
      if (readErr) {
        res.writeHead(500, { 'Content-Type': 'text/plain' });
        res.end('Error loading application');
        return;
      }
      res.writeHead(200, { 'Content-Type': contentType });
      res.end(data);
    });
  });
}

function startServer(port) {
  const srv = http.createServer(requestHandler);
  srv.on('error', (err) => {
    if (err.code === 'EADDRINUSE') {
      console.log(`[INFO] Port ${port} is already in use by proxy/system. Server continues on other port.`);
    } else {
      console.error(`[ERROR] Server error on port ${port}:`, err);
    }
  });
  srv.listen(port, '0.0.0.0', () => {
    console.log(`[INFO] Cooperative Gig Services platform server listening on 0.0.0.0:${port}`);
  });
  return srv;
}

// 1. Always bind to port 3000 (standard AI Studio dev server requirement)
startServer(3000);

// 2. If Cloud Run specifies a PORT environment variable (e.g., 8080), bind to it as well
const cloudRunPort = process.env.PORT ? parseInt(process.env.PORT, 10) : null;
if (cloudRunPort && cloudRunPort !== 3000) {
  startServer(cloudRunPort);
}

// Graceful shutdown handling for Cloud Run container lifecycle
process.on('SIGTERM', () => {
  console.log('[INFO] SIGTERM signal received. Shutting down gracefully.');
  process.exit(0);
});

process.on('SIGINT', () => {
  console.log('[INFO] SIGINT signal received. Shutting down gracefully.');
  process.exit(0);
});
