// Firebase Cloud Messaging Service Worker for background push notifications
/* eslint-disable no-undef */
importScripts('https://www.gstatic.com/firebasejs/9.23.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.23.0/firebase-messaging-compat.js');

// Standard Firebase default configuration
const firebaseConfig = {
  apiKey: "AIzaSyDummyFCMKeyForDemoPushes",
  authDomain: "cooperative-gig-services.firebaseapp.com",
  projectId: "cooperative-gig-services",
  storageBucket: "cooperative-gig-services.appspot.com",
  messagingSenderId: "959642062579",
  appId: "1:959642062579:web:3fde683c537745f6acac1b"
};

try {
  if (firebase.apps.length === 0) {
    firebase.initializeApp(firebaseConfig);
  }
  const messaging = firebase.messaging();

  // Background message handler
  messaging.onBackgroundMessage((payload) => {
    console.log('[firebase-messaging-sw.js] Received background push message:', payload);
    const notificationTitle = payload.notification?.title || 'Cooperative Gig Platform Update';
    const notificationOptions = {
      body: payload.notification?.body || 'You have a new update regarding your cooperative service.',
      icon: '/icons/icon-192.png',
      badge: '/icons/badge-72.png',
      data: payload.data || {},
      tag: payload.data?.bookingId || 'coop-gig-notification',
      vibrate: [200, 100, 200]
    };

    self.registration.showNotification(notificationTitle, notificationOptions);
  });
} catch (e) {
  console.log('[FCM-SW] Firebase compatibility mode:', e.message);
}

// Push event fallback for generic web push payloads
self.addEventListener('push', (event) => {
  let data = {};
  if (event.data) {
    try {
      data = event.data.json();
    } catch (err) {
      data = { title: 'Service Notification', body: event.data.text() };
    }
  }
  const title = data.title || data.notification?.title || 'Cooperative Gig Platform';
  const options = {
    body: data.body || data.notification?.body || 'New message or status update from your professional.',
    icon: '/portfolio/plumbing_installation_portfolio_1790690637076.jpg',
    data: data.data || {},
    actions: [
      { action: 'open_chat', title: 'Open Chat' },
      { action: 'track_order', title: 'Track Status' }
    ]
  };
  event.waitUntil(self.registration.showNotification(title, options));
});

// Notification click handling
self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  const urlToOpen = '/';
  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true }).then((windowClients) => {
      for (let client of windowClients) {
        if (client.url.includes(self.location.origin) && 'focus' in client) {
          return client.focus();
        }
      }
      if (clients.openWindow) {
        return clients.openWindow(urlToOpen);
      }
    })
  );
});
