package org.hofftech.parking.model;

/**
 * Класс, содержащий константы командных флагов, используемых в приложении.
 * Это финальный класс с приватным конструктором для предотвращения создания экземпляров.
 */
public final class CommandFlags {

    /**
     * Приватный конструктор для предотвращения создания экземпляров класса.
     */
    private CommandFlags() {
        // Предотвращение инстанцирования
    }

    /**
     * Флаг для команды сохранения.
     */
    public static final String SAVE = "-save";

    /**
     * Флаг для установки легкого режима.
     */
    public static final String EASY = "-easy";

    /**
     * Флаг для установки четного режима.
     */
    public static final String EVEN = "-even";

    /**
     * Флаг для включения подсчета.
     */
    public static final String WITH_COUNT = "-withCount";

    /**
     * Флаг для задания списка пакетов через текст.
     */
    public static final String PARCELS_TEXT = "-parcelsText";

    /**
     * Флаг для задания списка пакетов через файл.
     */
    public static final String PARCELS_FILE = "-parcelsFile";

    /**
     * Флаг для задания количества грузовиков.
     */
    public static final String TRUCKS = "-trucks";

    /**
     * Флаг для указания входного файла.
     */
    public static final String IN_FILE = "-inFile";

    /**
     * Флаг для задания имени.
     */
    public static final String NAME = "-name";

    /**
     * Флаг для задания старого имени.
     */
    public static final String OLD_NAME = "-oldName";

    /**
     * Флаг для задания формы.
     */
    public static final String FORM = "-form";

    /**
     * Флаг для задания символа.
     */
    public static final String SYMBOL = "-symbol";
}
