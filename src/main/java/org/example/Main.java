package org.example;

import java.io.IOException;
import java.util.List;

import static org.example.PackageLoader.packPackages;
import static org.example.PackageLoader.readPackages;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Пожалуйста, укажите путь к файлу в качестве аргумента.");
            return;
        }

        String filePath = args[0]; // Получаем путь из параметров командной строки
        List<Package> packages = readPackages(filePath);
        Truck truck = packPackages(packages);
        truck.print();
    }
}