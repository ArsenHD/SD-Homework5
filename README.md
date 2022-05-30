# Software Design HW#5

В этом репозитории находится реализация roguelike-игры.

### Запуск (проверялось только на Windows)
* Windows: `run.bat`
* Linux: `run.sh`

### Требования
* Java 11+

### Функциональность
* Главное меню открывается при запуске игры и позволяет выбрать карту из уже существующих
или сгенерировать новую случайным образом. Сгенерированная карта будет сохранена автоматически.
* Выбранная карта будет загружена с персонажем в правом верхнем углу.
С помощью стрелок можно передвигаться по карте. Нельзя проходить через стены
или выходить за пределы карты.
* Нажатие `ESC` во время игры позволяет перейти в меню паузы.
На данный момент оно содержит всего одну опцию - вернуться в главное меню.
Оттуда можно будет снова загрузить любую карту.

### Структура (MVC)
* [Model](src/ru/itmo/sd/roguelike/model)

  В этом пакете находится реализация карты и ее генератора.
  Также, здесь располагается класс [GameModel](src/ru/itmo/sd/roguelike/model/GameModel.kt),
  который представляет собой текущее состояние игры и позволяет получать доступ к существующим
  картам или сохранять новые. При обновлении состояния происходит оповещение `view`.
* [View](src/ru/itmo/sd/roguelike/view)

  Этот пакет содержит все, что относится к визуальной составляющей игры.
  Класс [GameView](src/ru/itmo/sd/roguelike/view/GameView.kt) создает
  все элементы интерфейса, а также передает сигналы пользовательского ввода контроллеру.
* [Controller](src/ru/itmo/sd/roguelike/controller)

  Этот пакет содержит класс [GameController](src/ru/itmo/sd/roguelike/controller/GameController.kt),
  который принимает сигналы от `view` и обновляет нужным образом `model`.
