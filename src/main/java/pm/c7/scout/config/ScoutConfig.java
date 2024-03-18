package pm.c7.scout.config;

import java.io.File;
import java.io.FileWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import joptsimple.internal.Strings;

import net.fabricmc.loader.api.FabricLoader;

import pm.c7.scout.ScoutUtil;

// most of this is taken from EMI
public class ScoutConfig {
	private static final Map<Class<?>, Setter> SETTERS = Maps.newHashMap();
	private static final Map<Class<?>, Writer<?>> WRITERS = Maps.newHashMap();
	private static final Map<String, List<String>> unparsed = Maps.newHashMap();
	public static final String DEFAULT_CONFIG;
	public static String startupConfig;

	// {{{ config values
	@Comment("Allow shulker boxes to be placed in bags. Bags are already blacklisted from shulker boxes with no toggle.")
	@ConfigValue("features.allow-shulkers")
	public static boolean allowShulkers = true;

	@Comment("Allow bags to act as a quiver and pull arrows.")
	@ConfigValue("features.use-arrows")
	public static boolean useArrows = true;
	// }}}

	// {{{ methods
	public static void loadConfig() {
		try {
			File config = getConfigFile();
			if (config.exists() && config.isFile()) {
				QDCSS css = QDCSS.load(config);
				loadConfig(css);
			} else {
				File defaultConfig = new File(FabricLoader.getInstance().getConfigDir().getParent().toFile(), "defaultconfigs/scout.css");
				if (defaultConfig.exists() && defaultConfig.isFile()) {
					QDCSS css = QDCSS.load(defaultConfig);
					loadConfig(css);
				}
			}
			if (startupConfig == null) {
				startupConfig = getSavedConfig();
			}
			writeConfig();
		} catch (Exception e) {
			ScoutUtil.LOGGER.error("[Scout] Error reading config");
			e.printStackTrace();
		}
	}

	public static void loadConfig(QDCSS css) {
		try {
			Set<String> consumed = Sets.newHashSet();
			for (Field field : ScoutConfig.class.getFields()) {
				ConfigValue annot = field.getAnnotation(ConfigValue.class);
				if (annot != null) {
					if (css.containsKey(annot.value())) {
						consumed.add(annot.value());
						assignField(css, annot.value(), field);
					}
				}
			}
			for (String key : css.keySet()) {
				if (!consumed.contains(key)) {
					unparsed.put(key, css.getAll(key));
				}
			}
		} catch (Exception e) {
			ScoutUtil.LOGGER.error("[Scout] Error reading config");
			e.printStackTrace();
		}
	}

	public static void writeConfig() {
		try {
			FileWriter writer = new FileWriter(getConfigFile());
			writer.write(getSavedConfig());
			writer.close();
		} catch (Exception e) {
			ScoutUtil.LOGGER.error("[Scout] Error writing config");
			e.printStackTrace();
		}
	}

	public static String getSavedConfig() {
		Map<String, List<String>> unparsed = Maps.newLinkedHashMap();
		for (Field field : ScoutConfig.class.getFields()) {
			ConfigValue annot = field.getAnnotation(ConfigValue.class);
			if (annot != null) {
				String[] parts = annot.value().split("\\.");
				String group = parts[0];
				String key = parts[1];
				Comment comment = field.getAnnotation(Comment.class);
				String commentText = "";
				if (comment != null) {
					commentText += "\t/**\n";
					var lines = comment.value().split("\\n");
					for (String line : lines) {
						commentText += "\t * ";
						commentText += line;
						commentText += "\n";
					}
					commentText += "\t */\n";
				}
				String text = commentText;
				try {
					text += writeField(key, field);
				} catch (Exception e) {
					ScoutUtil.LOGGER.error("[Scout] Error serializing config");
					e.printStackTrace();
				}
				unparsed.computeIfAbsent(group, g -> Lists.newArrayList()).add(text);
			}
		}
		for (Map.Entry<String, List<String>> entry : ScoutConfig.unparsed.entrySet()) {
			String[] parts = entry.getKey().split("\\.");
			String group = parts[0];
			String key = parts[1];
			for (String value : entry.getValue()) {
				unparsed.computeIfAbsent(group, g -> Lists.newArrayList()).add("\t/** unparsed */\n\t" + key + ": "
					+ value + ";\n");
			}
		}
		String ret = "";
		boolean firstCategory = true;
		for (Map.Entry<String, List<String>> category : unparsed.entrySet()) {
			if (!firstCategory) {
				ret += "\n";
			}
			firstCategory = false;

			ret += "#" + category.getKey() + " {\n";
			ret += Strings.join(category.getValue(), "\n");
			ret += "}\n";
		}
		return ret;
	}

	private static File getConfigFile() {
		return new File(FabricLoader.getInstance().getConfigDir().toFile(), "scout.css");
	}

	private static void assignField(QDCSS css, String annot, Field field) throws IllegalAccessException {
		Class<?> type = field.getType();
		Setter setter = SETTERS.get(type);
		if (setter != null) {
			setter.setValue(css, annot, field);
		} else {
			throw new RuntimeException("[Scout] Unknown parsing type: " + type);
		}
	}

	@SuppressWarnings("unchecked")
	private static String writeField(String key, Field field) throws IllegalAccessException {
		String text = "";
		Class<?> type = field.getType();
		if (WRITERS.containsKey(type)) {
			text += "\t" + key + ": " + ((Writer<Object>) WRITERS.get(type)).writeValue(field.get(null)) + ";\n";
		}
		return text;
	}

	private static void defineType(Class<?> clazz, Setter setter, Writer<?> writer) {
		SETTERS.put(clazz, setter);
		WRITERS.put(clazz, writer);
	}

	private static void defineType(Class<?> clazz, Setter setter) {
		defineType(clazz, setter, field -> field.toString());
	}
	// }}}

	// {{{ static init
	static {
		defineType(boolean.class, (css, annot, field) -> field.setBoolean(null, css.getBoolean(annot).get()));
		defineType(int.class, (css, annot, field) -> field.setInt(null, css.getInt(annot).get()));
		defineType(double.class, (css, annot, field) -> field.setDouble(null, css.getDouble(annot).get()));
		defineType(String.class,
			(css, annot, field) -> {
				String s = css.get(annot).get();
				s = s.substring(1, s.length() - 1);
				field.set(null, s);
			},
			(String field) -> "\"" + field + "\""
		);

		DEFAULT_CONFIG = getSavedConfig();
	}
	// }}}

	// {{{ annotations
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ConfigValue {
		public String value();
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Comment {
		public String value();
	}
	// }}}

	// {{{ interfaces
	private static interface Setter {
		void setValue(QDCSS css, String annot, Field field) throws IllegalAccessException ;
	}

	private static interface Writer<T> {
		String writeValue(T value);
	}
	// }}}
}
