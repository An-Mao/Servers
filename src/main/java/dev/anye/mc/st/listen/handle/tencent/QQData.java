package dev.anye.mc.st.listen.handle.tencent;

import java.util.HashMap;

public class QQData {
	public record GroupMsg(
			String self_id,
			String user_id,
			String time,
			String message_id,
			String real_id,
			String message_seq,
			String message_type,
			Sender sender,
			String raw_message,
			String font,
			String sub_type,
			//List<Msg> message,
			String message_format,
			String post_type,
			String group_id
	) {

		public record Sender(String user_id, String nickname, String card, String role, String title) {
		}

		public record Msg(String type, MsgData data) {
		}

		public record MsgData(String text) {
		}
	}

	public record Msg(MsgType type, HashMap<String, String> data) {
		public static Msg create(MsgType type, String key, String value) {
			HashMap<String, String> map = new HashMap<>();
			map.put(key, value);
			return new Msg(type, map);
		}

		@Override
		public String toString() {
			StringBuilder s = new StringBuilder("{");
			data.forEach((s1, s2) -> s.append("\"").append(s1).append("\":\"").append(s2).append("\""));
			s.append("}");
			return "{\"type\":\"" + type.toString() + "\",\"data\":" + s + "}";
		}

	}

	public enum MsgType {
		At("at"),
		Reply("reply"),
		Text("text"),
		Image("image");
		private final String s;

		MsgType(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return s;
		}
	}
}
