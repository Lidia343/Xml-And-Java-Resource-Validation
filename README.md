Проект для проверки констант интерфейсов на наличие одинаковых значений в интерфейсах одного типа
(если константы с равными значениями не указаны в файле конфигурации, это принимается за ошибку),
а также параметров атрибутов Id двух типов тегов ("Resource" и "Property") xml-файлов на наличие
значений Id в указанных выше соответствующих интерфейсах.
Пути к проверяемым файлам указываются в файле конфигурации.
Результат проверки (в виде отчёта) выводится  в консоль. В случае отсутствия ошибок программа
возвращает 0, иначе - 1.
