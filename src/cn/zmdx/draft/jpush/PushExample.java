package cn.zmdx.draft.jpush;

import java.util.Map;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

public class PushExample{

	public static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage(
            String alert, String sound, Map<String, String> map,
            String... alias) {
        return PushPayload
            .newBuilder()
            .setPlatform(Platform.ios())
            .setAudience(Audience.alias(alias))
            .setNotification(
	            Notification
                    .newBuilder()
                    .addPlatformNotification(
                        IosNotification.newBuilder()
                            .setAlert(alert)// alert
                            .setSound(sound)// sound
                            .addExtras(map).build())
                    .build())
            .build();
    }
	
	public static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage(
            String alert, Map<String, String> map,
            String... alias) {
        return PushPayload
            .newBuilder()
            .setPlatform(Platform.ios())
            .setAudience(Audience.alias(alias))
            .setNotification(
	            Notification
                    .newBuilder()
                    .addPlatformNotification(
                        IosNotification.newBuilder()
                            .setAlert(alert)// alert
                            .setSound("default")// sound
                            .addExtras(map).build())
                    .build())
            .build();
    }
}
