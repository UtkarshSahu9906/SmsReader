# SMS Forwarder Service (Android)

![Android Component Diagram](https://www.tutorialspoint.com/android/images/services.jpg)

A background service that forwards incoming SMS messages to a specified server endpoint.

## ⚙️ Technical Specifications
- **Component Type**: Android Service (Foreground)
- **Trigger**: SMS received broadcast
- **Data Flow**: SMS → Firebase
- **Persistence**: Runs until manually stopped

## 📋 Core Components
```java
public class SmsForwarderService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, buildNotification());
        // SMS processing logic here
        return START_STICKY;
    }
}
