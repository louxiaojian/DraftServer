package cn.zmdx.draft.jpush;

import java.util.Map;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

public class PushExample {

	/**
	 * 发送iPhone通知，可设置声音
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-11-20 时间：下午2:30:28
	 * @param alert
	 * @param sound
	 * @param map
	 * @param alias
	 * @return
	 */
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
								.build()).build();
	}

	/**
	 * 发送iPhone通知，默认声音
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-11-20 时间：下午2:30:50
	 * @param alert
	 * @param map
	 * @param alias
	 * @return
	 */
	public static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage(
			String alert, Map<String, String> map, String... alias) {
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
								.build()).build();
	}
	/**
	 * 发送Android通知
	 * 
	 * @author louxiaojian
	 * @date： 日期：2015-11-20 时间：下午2:31:05
	 * @param title
	 * @param alert
	 * @param map
	 * @param alias
	 * @return
	 */
	public static PushPayload buildPushObject_android_tagAnd_alertWithExtrasAndMessage(
			String title, String alert, Map<String, String> map,
			String... alias) {
		return PushPayload
				.newBuilder()
				.setPlatform(Platform.android())
				.setAudience(Audience.alias(alias))
				.setNotification(
						Notification
								.newBuilder()
								.addPlatformNotification(
										AndroidNotification.newBuilder()
												.setAlert(alert)
												.setTitle(title).addExtras(map)
												.build()
								).build()
				).build();
	}
}
