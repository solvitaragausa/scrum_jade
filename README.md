# JADE SCRUM Daudzaģentu Sistēma

Daudzaģentu sistēma SCRUM darba plūsmas simulācijai, izmantojot FIPA Contract Net un Request protokolus.

## Prasības

- Java 17+
- Maven 3.6+
- JADE 4.6

## Sistēmas Arhitektūra

### Aģenti

| Aģents | Ātrums | Zināšanas | Loma |
|--------|--------|-----------|------|
| ProduktuIpasnieks | - | - | Uzdevumu piešķiršana, testēšanas koordinācija |
| ProgA | 2.0 | backend | Izstrāde |
| ProgB | 1.8 | backend | Izstrāde |
| ProgC | 1.6 | frontend | Izstrāde |
| ProgD | 2.2 | frontend | Izstrāde |
| ProgE | 1.5 | fullstack | Izstrāde |
| Testetajs | - | - | Kvalitātes nodrošināšana |

**Konkurence:** Katram uzdevumam konkurē visi 5 izstrādātāji. Izvēle balstīta uz izmaksu aprēķinu.

### Protokoli

**FIPA Contract Net** - Uzdevumu piešķiršana
```
CFP → PROPOSE → ACCEPT/REJECT → INFORM
```

**FIPA Request** - Testēšanas koordinācija
```
REQUEST → AGREE → INFORM
```

### Izmaksu Modelis

```
izmaksas = (sarežģītība / ātrums) + sods - bonuss
```

- sods: HIGH=2.0, MEDIUM=1.0, LOW=0.3
- bonuss: 0.8 (ja zināšanas atbilst) vai 0.0

### Uzdevumu Tipi

- FEATURE: Jauna funkcionalitāte
- BUGFIX: Kļūdu labošana
- TEST: Testu izveide
- DOC: Dokumentācijas rakstīšana

## Projekta Struktūra

```
src/main/java/lv/rtu/
├── MainBoot.java
├── agents/
│   ├── ProductOwnerAgent.java
│   ├── DeveloperAgent.java
│   └── TesterAgent.java
├── domain/
│   └── Task.java
└── util/
    ├── CostModel.java
    └── SnifferReadySignal.java
```

## Palaišana

```bash
mvn clean compile
mvn exec:java
```

## Testēšana

Projekts satur vienību testus, kas pārbauda aģentu uzvedību un sistēmas funkcionalitāti, integrācijas testus, kas nodrošina pareizu aģentu mijiedarbību, un JADE testus, kas palaiž aģentus īstā JADE konteinera vidē.

```bash
mvn test
```

