package model;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servizio per le notifiche push browser.
 * Simula un sistema di notifiche push usando WebSockets e browser API.
 * In un'implementazione reale si integrerebbe con servizi come Firebase Cloud Messaging (FCM).
 */
public class PushNotificationService {
    
    private static final Logger LOGGER = Logger.getLogger(PushNotificationService.class.getName());
    private ExecutorService executorService;
    private boolean isEnabled = false;
    
    // Simulazione di subscription tokens per gli utenti
    private Map<Integer, String> userSubscriptions;
    private Map<String, PushSubscription> subscriptions;
    
    // Configurazione push
    private String vapidPublicKey;
    @SuppressWarnings("unused")
    private String vapidPrivateKey;
    private String fcmServerKey;
    
    public PushNotificationService() {
        this.executorService = Executors.newFixedThreadPool(3);
        this.userSubscriptions = new ConcurrentHashMap<>();
        this.subscriptions = new ConcurrentHashMap<>();
        
        // Inizializza con chiavi simulate (in produzione sarebbero reali)
        this.vapidPublicKey = "simulated_vapid_public_key_" + System.currentTimeMillis();
        this.vapidPrivateKey = "simulated_vapid_private_key_" + System.currentTimeMillis();
        this.fcmServerKey = "simulated_fcm_server_key";
        
        LOGGER.info("Servizio notifiche push inizializzato (modalità simulazione)");
    }
    
    /**
     * Configura il servizio push con chiavi reali
     */
    public boolean configure(String vapidPublicKey, String vapidPrivateKey, String fcmServerKey) {
        try {
            this.vapidPublicKey = vapidPublicKey;
            this.vapidPrivateKey = vapidPrivateKey;
            this.fcmServerKey = fcmServerKey;
            
            // In un'implementazione reale, qui si verificherebbero le chiavi
            this.isEnabled = true;
            
            LOGGER.info("Servizio notifiche push configurato");
            return true;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nella configurazione push notifications", e);
            return false;
        }
    }
    
    /**
     * Registra una subscription per un utente
     */
    public boolean registerSubscription(int userId, String endpoint, String p256dh, String auth) {
        try {
            String subscriptionId = "sub_" + userId + "_" + System.currentTimeMillis();
            
            PushSubscription subscription = new PushSubscription(subscriptionId, userId, endpoint, p256dh, auth);
            
            userSubscriptions.put(userId, subscriptionId);
            subscriptions.put(subscriptionId, subscription);
            
            LOGGER.info("Subscription registrata per utente " + userId + ": " + subscriptionId);
            return true;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nella registrazione subscription", e);
            return false;
        }
    }
    
    /**
     * Rimuove una subscription
     */
    public boolean unregisterSubscription(int userId) {
        try {
            String subscriptionId = userSubscriptions.remove(userId);
            if (subscriptionId != null) {
                subscriptions.remove(subscriptionId);
                LOGGER.info("Subscription rimossa per utente " + userId);
                return true;
            }
            return false;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nella rimozione subscription", e);
            return false;
        }
    }
    
    /**
     * Invia notifica push a un utente specifico
     */
    public CompletableFuture<Boolean> sendPushNotification(int userId, String title, String body, String icon, String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String subscriptionId = userSubscriptions.get(userId);
                if (subscriptionId == null) {
                    LOGGER.warning("Nessuna subscription trovata per utente " + userId);
                    return false;
                }
                
                PushSubscription subscription = subscriptions.get(subscriptionId);
                if (subscription == null) {
                    LOGGER.warning("Subscription non valida: " + subscriptionId);
                    return false;
                }
                
                PushMessage message = new PushMessage(title, body, icon, url);
                
                // In un'implementazione reale, qui si invierebbe la notifica tramite:
                // - Web Push Protocol per browser
                // - Firebase Cloud Messaging (FCM)
                // - Apple Push Notification service (APNs)
                
                // Simulazione invio push
                simulatePushDelivery(subscription, message);
                
                LOGGER.info("Notifica push inviata a utente " + userId + ": " + title);
                return true;
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errore nell'invio notifica push", e);
                return false;
            }
        }, executorService);
    }
    
    /**
     * Invia notifica push a più utenti
     */
    public CompletableFuture<Integer> sendBulkPushNotification(List<Integer> userIds, String title, String body, String icon, String url) {
        return CompletableFuture.supplyAsync(() -> {
            int successCount = 0;
            
            for (Integer userId : userIds) {
                try {
                    boolean sent = sendPushNotification(userId, title, body, icon, url).get();
                    if (sent) {
                        successCount++;
                    }
                    
                    // Pausa breve per evitare rate limiting
                    Thread.sleep(50);
                    
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Errore invio push a utente " + userId, e);
                }
            }
            
            LOGGER.info("Invio bulk push completato: " + successCount + "/" + userIds.size() + " notifiche inviate");
            return successCount;
            
        }, executorService);
    }
    
    /**
     * Invia notifica broadcast a tutti gli utenti registrati
     */
    public CompletableFuture<Integer> sendBroadcastNotification(String title, String body, String icon, String url) {
        return CompletableFuture.supplyAsync(() -> {
            List<Integer> allUsers = new ArrayList<>(userSubscriptions.keySet());
            
            try {
                return sendBulkPushNotification(allUsers, title, body, icon, url).get();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errore nell'invio broadcast", e);
                return 0;
            }
        }, executorService);
    }
    
    /**
     * Simula la consegna della notifica push
     */
    private void simulatePushDelivery(PushSubscription subscription, PushMessage message) {
        // Simula latenza di rete
        try {
            Thread.sleep(100 + (int)(Math.random() * 200)); // 100-300ms
            
            // Simula possibili errori di consegna (5% di fallimento)
            if (Math.random() < 0.05) {
                throw new RuntimeException("Simulazione errore consegna push");
            }
            
            // Log della "consegna"
            LOGGER.fine("Push simulata consegnata - Endpoint: " + 
                        subscription.getEndpoint().substring(0, Math.min(30, subscription.getEndpoint().length())) + 
                        ", Titolo: " + message.getTitle());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            throw new RuntimeException("Errore simulazione push", e);
        }
    }
    
    /**
     * Ottiene il numero di subscription attive
     */
    public int getActiveSubscriptionsCount() {
        return subscriptions.size();
    }
    
    /**
     * Ottiene statistiche del servizio push
     */
    public String getPushStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== STATISTICHE NOTIFICHE PUSH ===\n");
        stats.append("Servizio abilitato: ").append(isEnabled ? "✅ Sì" : "❌ No").append("\n");
        stats.append("Subscription attive: ").append(subscriptions.size()).append("\n");
        stats.append("Utenti registrati: ").append(userSubscriptions.size()).append("\n");
        
        if (isEnabled) {
            stats.append("VAPID Public Key: ").append(vapidPublicKey.substring(0, Math.min(20, vapidPublicKey.length()))).append("...\n");
            stats.append("FCM configurato: ").append(fcmServerKey != null && !fcmServerKey.isEmpty() ? "✅ Sì" : "❌ No").append("\n");
        }
        
        return stats.toString();
    }
    
    /**
     * Genera il Service Worker JavaScript per le notifiche push
     */
    public String generateServiceWorkerScript() {
        return """
            // Service Worker per notifiche push - Hackathon Manager
            
            self.addEventListener('push', function(event) {
                console.log('Push message ricevuto');
                
                let notificationData = {};
                if (event.data) {
                    try {
                        notificationData = event.data.json();
                    } catch (e) {
                        notificationData = {
                            title: 'Hackathon Manager',
                            body: event.data.text() || 'Nuova notifica',
                            icon: '/icon-192x192.png'
                        };
                    }
                }
                
                const options = {
                    body: notificationData.body || 'Nuova notifica',
                    icon: notificationData.icon || '/icon-192x192.png',
                    badge: '/badge-72x72.png',
                    data: {
                        url: notificationData.url || '/',
                        timestamp: Date.now()
                    },
                    actions: [
                        {
                            action: 'open',
                            title: 'Apri',
                            icon: '/action-open.png'
                        },
                        {
                            action: 'dismiss',
                            title: 'Ignora',
                            icon: '/action-dismiss.png'
                        }
                    ],
                    requireInteraction: true,
                    silent: false
                };
                
                event.waitUntil(
                    self.registration.showNotification(notificationData.title || 'Hackathon Manager', options)
                );
            });
            
            self.addEventListener('notificationclick', function(event) {
                console.log('Notifica cliccata');
                
                event.notification.close();
                
                if (event.action === 'open' || !event.action) {
                    const url = event.notification.data.url || '/';
                    event.waitUntil(
                        clients.openWindow(url)
                    );
                } else if (event.action === 'dismiss') {
                    // Notifica chiusa, nessuna azione
                }
            });
            
            self.addEventListener('notificationclose', function(event) {
                console.log('Notifica chiusa');
                // Tracking analytics se necessario
            });
            """;
    }
    
    /**
     * Genera il JavaScript client per la registrazione push
     */
    public String generateClientScript() {
        return """
            // Client script per notifiche push - Hackathon Manager
            
            const VAPID_PUBLIC_KEY = 'BEl62iUYgUivxIkv69yViEuiBIa40HI8F7j7wAd7nSw8O7Z_3T3Cf9b_0ASp_0jH0JzP3qY0pO4bY8N7vM2oE';
            
            function initializePushNotifications() {
                if ('serviceWorker' in navigator && 'PushManager' in window) {
                    console.log('Push notifications supportate');
                    
                    navigator.serviceWorker.register('/sw.js')
                        .then(function(registration) {
                            console.log('Service Worker registrato');
                            return registration.pushManager.getSubscription();
                        })
                        .then(function(subscription) {
                            if (subscription) {
                                console.log('Subscription esistente trovata');
                                sendSubscriptionToServer(subscription);
                            } else {
                                console.log('Nessuna subscription, richiedo permesso');
                                requestNotificationPermission();
                            }
                        })
                        .catch(function(error) {
                            console.error('Errore Service Worker:', error);
                        });
                } else {
                    console.warn('Push notifications non supportate');
                }
            }
            
            function requestNotificationPermission() {
                return Notification.requestPermission()
                    .then(function(permission) {
                        if (permission === 'granted') {
                            console.log('Permesso notifiche concesso');
                            subscribeUserToPush();
                        } else {
                            console.warn('Permesso notifiche negato');
                        }
                    });
            }
            
            function subscribeUserToPush() {
                return navigator.serviceWorker.getRegistration()
                    .then(function(registration) {
                        const subscribeOptions = {
                            userVisibleOnly: true,
                            applicationServerKey: urlBase64ToUint8Array(VAPID_PUBLIC_KEY)
                        };
                        
                        return registration.pushManager.subscribe(subscribeOptions);
                    })
                    .then(function(subscription) {
                        console.log('Utente sottoscritto alle push notifications');
                        sendSubscriptionToServer(subscription);
                    })
                    .catch(function(error) {
                        console.error('Errore sottoscrizione push:', error);
                    });
            }
            
            function sendSubscriptionToServer(subscription) {
                // Invia subscription al server
                const subscriptionData = {
                    endpoint: subscription.endpoint,
                    keys: {
                        p256dh: arrayBufferToBase64(subscription.getKey('p256dh')),
                        auth: arrayBufferToBase64(subscription.getKey('auth'))
                    }
                };
                
                fetch('/api/push/subscribe', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(subscriptionData)
                })
                .then(response => response.json())
                .then(data => console.log('Subscription inviata al server:', data))
                .catch(error => console.error('Errore invio subscription:', error));
            }
            
            function urlBase64ToUint8Array(base64String) {
                const padding = '='.repeat((4 - base64String.length %% 4) %% 4);
                const base64 = (base64String + padding)
                    .replace(/-/g, '+')
                    .replace(/_/g, '/');
                
                const rawData = window.atob(base64);
                const outputArray = new Uint8Array(rawData.length);
                
                for (let i = 0; i < rawData.length; ++i) {
                    outputArray[i] = rawData.charCodeAt(i);
                }
                return outputArray;
            }
            
            function arrayBufferToBase64(buffer) {
                const bytes = new Uint8Array(buffer);
                let binary = '';
                for (let i = 0; i < bytes.byteLength; i++) {
                    binary += String.fromCharCode(bytes[i]);
                }
                return window.btoa(binary);
            }
            
            // Inizializza quando la pagina è caricata
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', initializePushNotifications);
            } else {
                initializePushNotifications();
            }
            """;
    }
    
    /**
     * Chiude il servizio
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            LOGGER.info("Servizio push notifications terminato");
        }
    }
    
    // Getters
    public boolean isEnabled() { return isEnabled; }
    public String getVapidPublicKey() { return vapidPublicKey; }
    
    /**
     * Classe per rappresentare una subscription push
     */
    public static class PushSubscription {
        private String id;
        private int userId;
        private String endpoint;
        private String p256dh;
        private String auth;
        private long createdAt;
        
        public PushSubscription(String id, int userId, String endpoint, String p256dh, String auth) {
            this.id = id;
            this.userId = userId;
            this.endpoint = endpoint;
            this.p256dh = p256dh;
            this.auth = auth;
            this.createdAt = System.currentTimeMillis();
        }
        
        // Getters
        public String getId() { return id; }
        public int getUserId() { return userId; }
        public String getEndpoint() { return endpoint; }
        public String getP256dh() { return p256dh; }
        public String getAuth() { return auth; }
        public long getCreatedAt() { return createdAt; }
    }
    
    /**
     * Classe per rappresentare un messaggio push
     */
    public static class PushMessage {
        private String title;
        private String body;
        private String icon;
        private String url;
        
        public PushMessage(String title, String body, String icon, String url) {
            this.title = title;
            this.body = body;
            this.icon = icon;
            this.url = url;
        }
        
        // Getters
        public String getTitle() { return title; }
        public String getBody() { return body; }
        public String getIcon() { return icon; }
        public String getUrl() { return url; }
    }
}
