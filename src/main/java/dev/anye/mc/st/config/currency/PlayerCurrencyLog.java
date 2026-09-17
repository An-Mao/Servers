package dev.anye.mc.st.config.currency;

import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.exception._IOException;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.ST;
import dev.anye.mc.st.config.ConfigDir;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * 虽然实现了json，但并没有使用，因为直接文件写入可能性能会更好一些。
 */
public final class PlayerCurrencyLog extends _JsonConfigS<List<PlayerCurrencyLogData>> {
	private static final Logger LOGGER = LogUtils.getLogger();

	public PlayerCurrencyLog(ServerPlayer serverPlayer) {
		this(serverPlayer.getStringUUID());
	}
	public PlayerCurrencyLog(String uuid) {
		super(path(uuid), List.of(), new TypeToken<>(){}, false);
	}
	public boolean writeLog(String source,double number){
		return writeLog(new PlayerCurrencyLogData(source, number));
	}
	public boolean writeLog(PlayerCurrencyLogData data){
		update(data1 -> data1.add(data));
		save();
		return true;
	}
	public boolean writeLogs(PlayerCurrencyLogData... data){
		return writeLogs(List.of(data));
	}
	public boolean writeLogs(List<PlayerCurrencyLogData> data){
		update(data1 -> data1.addAll(data));
		return true;
	}


	/**
	 * 写入文本（UTF-8格式）到log
	 * @param uuid 玩家uuid
	 * @param log 要写入的内容
	 * @return 写入失败时返回false
	 */
	public static boolean writeLog(String uuid,String log){
		return writeLog(Path.of(path(uuid)),(log + "\n").getBytes(StandardCharsets.UTF_8));
	}
	public static boolean writeLog(ServerPlayer serverPlayer,String log){
		return writeLog(serverPlayer.getStringUUID(),log);
	}

	/**
	 * 写入byte到log
	 * @param path 文件路径
	 * @param bytes 要写入的字节集
	 * @return 写入失败时返回false
	 */
	public static boolean writeLog(Path path,byte[] bytes){
		try (FileChannel channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			channel.write(buffer);
			return true;
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			return false;
		}
	}

	public static String path(String uuid){
		String dir = _File.getFilePath(ConfigDir.getPlayerCurrencyDir(uuid),"Log");
		if (_File.checkAndCreateDir(dir)) return _File.getFilePath(dir, ST.FAST_DATE_TIME.update().toDateString("-")+ _SuffixCDT.LOG_SUFFIX);
		throw new _IOException(dir);
	}
}
