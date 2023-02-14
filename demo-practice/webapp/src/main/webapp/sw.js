importScripts('/VAADIN/static/server/workbox/workbox-sw.js');

workbox.setConfig({
    modulePathPrefix: '/VAADIN/static/server/workbox/'
});
workbox.precaching.precacheAndRoute([
    { url: 'icons/icon-144x144.png', revision: '-1456135562' },
    { url: 'icons/icon-192x192.png', revision: '-1333786034' },
    { url: 'icons/icon-512x512.png', revision: '1931390955' },
    { url: 'icons/icon-16x16.png', revision: '-1417519116' },
    { url: 'offline.html', revision: '-141932327' },
    { url: 'manifest1.json', revision: '4238356' }
]);

self.addEventListener('install', function(event) {
    event.waitUntil(self.skipWaiting()); // Activate worker immediately
});

self.addEventListener('activate', function(event) {
    event.waitUntil(self.clients.claim()); // Become available to all pages
});

self.addEventListener('fetch', function(event) {
    var request = event.request;
    if (request.mode === 'navigate') {
        event.respondWith(
            fetch(request)
                .catch(function() {
                    return caches.match('offline.html');
                })
        );
    }
});

self.addEventListener('message', function(event) {
    console.log('Handling message event:', event);
    if (event.data.command == "save") {
        saveUser(event.data.username, event.data.password);
    }

});

function saveUser(username, password) {
    let request = self.indexedDB.open('PLANGLOBAL_DB', 1);

    request.onsuccess = function (event) {
        console.log('[onsuccess]', request.result);
        db = event.target.result;

        var user = {id: 1, name: username, password: password};

        var transaction = db.transaction('users', 'readwrite');

        transaction.onsuccess = function (event) {
            console.log('[Transaction] ALL DONE!');
        };

        var usersStore = transaction.objectStore('users');

        var db_op_req = usersStore.add(user);

        db_op_req.onsuccess = function (event) {
            console.log(event.target.result == user.id); // true
        }
    };

    request.onerror = function (event) {
        console.log('[onerror]', request.error);
    };

    request.onupgradeneeded = function (event) {
        var db = event.target.result;
        var usertStore = db.createObjectStore('users', {keyPath: 'id'});
    };

}




