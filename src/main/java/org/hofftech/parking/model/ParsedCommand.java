package org.hofftech.parking.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hofftech.parking.model.enums.CommandType;

/**
 * Класс, представляющий разобранную команду, введённую пользователем.
 *
 * <p>Этот класс хранит все параметры, извлечённые из команды, включая обязательные и
 * необязательные параметры. Используется для передачи данных между компонентами системы
 * после анализа входной команды.</p>
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ParsedCommand {
    /**
     * Имя посылки для создания или обновления.
     */
    private String name;

    /**
     * Старое имя посылки, используемое при обновлении.
     */
    private String oldName;

    /**
     * Форма посылки, описывающая её внешний вид или структуру.
     */
    private String form;

    /**
     * Символ, ассоциируемый с посылкой.
     */
    private String symbol;

    /**
     * Флаг, указывающий, нужно ли сохранять результаты в файл.
     */
    private final boolean saveToFile;

    /**
     * Флаг, указывающий, используется ли простой алгоритм при обработке.
     */
    private final boolean useEasyAlgorithm;

    /**
     * Флаг, указывающий, используется ли чётный алгоритм при обработке.
     */
    private final boolean useEvenAlgorithm;

    /**
     * Текстовое представление посылок для обработки.
     */
    private final String parcelsText;

    /**
     * Путь к файлу с посылками для обработки.
     */
    private final String parcelsFile;

    /**
     * Перечень грузовиков в виде строки, разделённых запятыми.
     */
    private final String trucks;

    /**
     * Путь к входному файлу для других операций.
     */
    private final String inFile;

    /**
     * Флаг, указывающий, нужно ли учитывать количество при обработке.
     */
    private final boolean withCount;

    /**
     * Тип команды, определяющий, какую операцию необходимо выполнить.
     */
    private CommandType commandType;
}
