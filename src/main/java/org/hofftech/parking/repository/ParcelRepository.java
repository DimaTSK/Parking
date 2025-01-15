package org.hofftech.parking.repository;

import org.hofftech.parking.model.enums.DefaultPackagesType;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Хранилище посылок, обеспечивающее операции добавления, поиска, редактирования и удаления посылок.
 */
public class ParcelRepository {
    private final Map<String, Parcel> packages = new HashMap<>();

    /**
     * Добавляет новую посылку в хранилище.
     *
     * @param pkg объект посылки для добавления
     * @throws IllegalArgumentException если посылка с таким именем уже существует
     */
    public void addPackage(Parcel pkg) {
        if (packages.containsKey(pkg.getName())) {
            throw new IllegalArgumentException("Посылка с таким именем уже существует: " + pkg.getName());
        }
        packages.put(pkg.getName(), pkg);
    }

    /**
     * Находит и возвращает посылку по её имени, независимо от регистра.
     *
     * @param name имя посылки для поиска
     * @return объект посылки с указанным именем
     * @throws IllegalArgumentException если посылка не найдена
     */
    public Parcel findPackage(String name) {
        for (String key : packages.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return packages.get(key);
            }
        }
        throw new IllegalArgumentException("Посылка не найдена: " + name);
    }

    /**
     * Редактирует существующую посылку с указанным именем.
     *
     * @param name          имя посылки, которую необходимо обновить
     * @param updatedParcel объект посылки с обновленными данными
     * @throws IllegalArgumentException если посылка с указанным именем не найдена
     */
    public void editPackage(String name, Parcel updatedParcel) {
        if (!packages.containsKey(name)) {
            throw new IllegalArgumentException("Посылка не найдена: " + name);
        }
        packages.put(name, updatedParcel);
    }

    /**
     * Удаляет посылку из хранилища по её имени.
     *
     * @param name имя посылки для удаления
     * @throws IllegalArgumentException если посылка с указанным именем не найдена
     */
    public void deletePackage(String name) {
        if (!packages.containsKey(name)) {
            throw new IllegalArgumentException("Посылка не найдена: " + name);
        }
        packages.remove(name);
    }

    /**
     * Возвращает список всех посылок, отсортированных по имени.
     *
     * @return список всех посылок в хранилище
     */
    public List<Parcel> getAllPackages() {
        return packages.values().stream()
                .sorted(Comparator.comparing(Parcel::getName))
                .collect(Collectors.toList());
    }

    /**
     * Загружает набор посылок по умолчанию в хранилище.
     */
    public void loadDefaultPackages() {
        int counter = 1;
        for (DefaultPackagesType type : DefaultPackagesType.values()) {
            String name = "Посылка Тип " + counter++;
            packages.put(name, new Parcel(
                    name,
                    type.getShape(),
                    type.getShape().getFirst().charAt(0),
                    new ParcelStartPosition(0, 0)
            ));
        }
    }
}
