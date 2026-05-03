import ComposeApp
import FirebaseCore
import FirebaseMessaging
import UIKit
import UserNotifications

private enum ShelfFcmBridge {

    static func string(forKey key: String, in userInfo: [AnyHashable: Any]) -> String? {
        for (k, v) in userInfo {
            guard let ks = k as? String, ks == key else { continue }
            if let s = v as? String { return s }
            if let s = v as? NSString { return s as String }
            if let n = v as? NSNumber { return n.stringValue }
            return nil
        }
        return nil
    }

    /// Flat string map for Kotlin JSON (excludes `aps` and nested blobs).
    static func dataJson(from userInfo: [AnyHashable: Any]) -> String {
        var map: [String: String] = [:]
        for (anyKey, value) in userInfo {
            guard let key = anyKey as? String, key != "aps" else { continue }
            if let s = value as? String {
                map[key] = s
            } else if let s = value as? NSString {
                map[key] = s as String
            } else if let n = value as? NSNumber {
                map[key] = n.stringValue
            }
        }
        guard let data = try? JSONSerialization.data(withJSONObject: map),
              let str = String(data: data, encoding: .utf8) else {
            return "{}"
        }
        return str
    }

    static func alertTitleBody(from userInfo: [AnyHashable: Any]) -> (String?, String?) {
        guard let aps = userInfo["aps"] as? [AnyHashable: Any] else { return (nil, nil) }
        let alert = aps["alert"]
        if let dict = alert as? [String: Any] {
            let t = dict["title"] as? String
            let b = dict["body"] as? String ?? dict["subtitle"] as? String
            return (t, b)
        }
        if let s = alert as? String {
            return (nil, s)
        }
        return (nil, nil)
    }

    static func forwardToKmp(
        userInfo: [AnyHashable: Any],
        notificationTitle: String?,
        notificationBody: String?
    ) {
        let messageId = string(forKey: "gcm.message_id", in: userInfo)
        let collapseKey = string(forKey: "collapse_key", in: userInfo)
        let (apsTitle, apsBody) = alertTitleBody(from: userInfo)
        let dataTitle = string(forKey: "title", in: userInfo)
        let dataBody = string(forKey: "body", in: userInfo)
        let title = notificationTitle?.isEmpty == false ? notificationTitle : (apsTitle ?? dataTitle)
        let body = notificationBody?.isEmpty == false ? notificationBody : (apsBody ?? dataBody)
        let json = dataJson(from: userInfo)
        IosFcmKt.notifyIosFcmMessage(
            messageId: messageId,
            collapseKey: collapseKey,
            notificationTitle: title,
            notificationBody: body,
            dataJson: json
        )
    }
}

final class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { _, _ in }
        application.registerForRemoteNotifications()
        Messaging.messaging().token { token, error in
            if let error = error {
                print("[FcmCurrentToken] getToken error: \(error)")
                return
            }
            if let token = token {
                print("[FcmCurrentToken] Current FCM token: \(token)")
            }
        }
        return true
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }

    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable: Any],
        fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
        ShelfFcmBridge.forwardToKmp(
            userInfo: userInfo,
            notificationTitle: nil,
            notificationBody: nil
        )
        completionHandler(.newData)
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate {

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        let content = notification.request.content
        ShelfFcmBridge.forwardToKmp(
            userInfo: content.userInfo,
            notificationTitle: content.title,
            notificationBody: content.body
        )
        completionHandler([.banner, .badge, .sound])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let content = response.notification.request.content
        ShelfFcmBridge.forwardToKmp(
            userInfo: content.userInfo,
            notificationTitle: content.title,
            notificationBody: content.body
        )
        completionHandler()
    }
}

extension AppDelegate: MessagingDelegate {

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let token = fcmToken else { return }
        IosFcmKt.notifyIosFcmToken(token: token)
    }
}
