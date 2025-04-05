# five-letters

Репозиторий с исследованием игры "5-букв" (аналог Wordle)

## Описание исследования

Вся подробная информация об исследовании оформлена в виде [статьи на Хабре](https://habr.com/ru/articles/882914/)

В рамках этой статьи можно найти:
- Как формировались словари для исследования
- Как реализованы разные алгоритмы (оптимальный; "человеческий"; со стартом из нескольких слов с уникальными буквами)
- Примеры работы алгоритмов на реальных играх в приложении Т-Банка
- Подробное описание результатов исследования
- Выводы, применимые человеком в реальных играх

## Устройство репозитория

В репозитории есть несколько папок:
- [dictionaries](/dictionaries) - словари, используемые для исследования и для интерактивных игр
- [research_results](/research_results) - подробные результаты исследования
- [src](/src) - непосредственно, код проекта

Репозиторий рассчитан **только** на то, чтобы запускать код из IDE

Есть несколько запускаемых файлов:
- `org.example.fiveletters.wordsparsing` - пакет с функциональностью для формирования словарей
  - [opencorpora.OpenCorporaApplication](/src/main/java/org/example/fiveletters/wordsparsing/opencorpora/OpenCorporaApplication.java) -
    парсинг слов с ресурса [OpenCorpora.org](https://opencorpora.org/)
  - [poiskslov.PoiskSlovApplication](/src/main/java/org/example/fiveletters/wordsparsing/poiskslov/PoiskSlovApplication.java) -
    парсинг слов с ресурса [поиск-слов.рф](https://xn----dtbqigoecuc.xn--p1ai/suschestvitelnye/5)
  - [textometr.TextometrApplication](/src/main/java/org/example/fiveletters/wordsparsing/textometr/TextometrApplication.java) -
    парсинг информации о частотности слов с ресурса [textometr.ru](https://textometr.ru/frequency-check)
- `org.example.fiveletters.solving` - пакет с функциональностью для взаимодействия с игрой
  - [beginningsearch.BeginningSearchApplication](/src/main/java/org/example/fiveletters/solving/beginningsearch/BeginningSearchApplication.java) -
    поиск оптимальных стартов для игры
  - [cli.FiveLettersCliApplication](/src/main/java/org/example/fiveletters/solving/cli/FiveLettersCliApplication.java) -
    CLI для интерактивного прохождения игры
  - [research.ResearchApplication](/src/main/java/org/example/fiveletters/solving/research/ResearchApplication.java) -
    исследование прохождения игры на разных словарях и разных алгоритмах решения

## Особенности реализации

- Используемые технологии
  - В проекте не используется никаких фреймворков, в том числе нет DI - всё создается руками
  - Добавлено базовое логирование на основе
    [SLF4J Simple Provider](https://www.slf4j.org/apidocs/org/slf4j/simple/SimpleLogger.html)
  - Для HTTP-запросов используется синхронный клиент
    [Apache HttpClient 5.4](https://hc.apache.org/httpcomponents-client-5.4.x/index.html)
  - Для JSON- и XML-сериализации используется [jackson](https://github.com/FasterXML/jackson) (ObjectMapper и XmlMapper)
  - Для HTML-парсинга используется [jsoup](https://jsoup.org/)
- Особенности логики
  - Активно используется работа с битами, потому что 32 уникальные буквы русского алфавита (при условии, что `е` == `ё`)
    отлично помещаются в битовую маску Integer
  - Отдельно учтены кейсы, когда одна и та же буква в слове встречается несколько раз. И, кажется, это сделано
    максимально корректно
- Оптимизации
  - В рамках исследования производится много вычислений, и эти вычисления занимают довольно много времени, поэтому в коде 
    реализованы многопоточные вычисления на базе ForkJoinPool
  - Я постарался максимально оптимизировать вычисления, но при этом оставить код поддерживаемым. За счет этого все
    вычисления возможно выполнить на локальном ПК, но некоторые вычисления все-таки занимают довольно много времени -
    на моем 12-ядерном процессоре самое долгое вычисление заняло у меня около суток

## Nice-to-have фичи

Фичи ниже вряд ли когда-то будут реализованы, они описаны скорее просто для того, чтобы зафиксировать, что 
именно **не** было сделано в этом проекте:

- [ ] Написать тесты. Потому что сейчас тестов почти нет
- [ ] Реализовать ретраи для [textometr.ru](https://textometr.ru/frequency-check), потому что иногда запросы
  отваливаются с `500 Internal Server Error`, и вся программа падает с ошибкой
- [ ] Реализовать поиск частотности слов не через [textometr.ru](https://textometr.ru/frequency-check), а по корпусу
  текстов в [OpenCorpora.org](https://opencorpora.org/), потому что, кажется, это более достоверный источник

## Результаты исследования

Все вычисления выполнены на тэге [habr-article](https://github.com/kadukm/five-letters/tree/habr-article)

**Эвристика по среднему**: минимизация среднего кол-ва оставшихся ответов

<table>
    <tr>
        <td rowspan="2">Алгоритмы \ словари</td>
        <td rowspan="2">Оптимальный</td>
        <td colspan="4">Уникальные символы</td>
        <td rowspan="2">"Человеческий"</td>
    </tr>
    <tr>
        <td>2 слова</td>
        <td>3 слова</td>
        <td>4 слова</td>
        <td>5 слов</td>
    </tr>
    <tr>
        <td>2066 | 6826</td>
        <td>
          <a href="/research_results/average/2066_6826.md#%D0%BE%D0%BF%D1%82%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9">
            3.4458<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-2-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            3.6066<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-3-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.1764<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-4-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            5.0514<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-5-%D1%81%D0%BB%D0%BE%D0%B2">
            6.0102<br/>(35)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_6826.md#%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9">
            3.6003<br/>(15)
          </a>
        </td>
    </tr>
    <tr>
        <td>2066 | 4109</td>
        <td>
          <a href="/research_results/average/2066_4109.md#%D0%BE%D0%BF%D1%82%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9">
            3.4942<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-2-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            3.6110<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-3-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.1813<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-4-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            5.0563<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-5-%D1%81%D0%BB%D0%BE%D0%B2">
            6.0146<br/>(44)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_4109.md#%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9">
            3.6003<br/>(15)
          </a>
        </td>
    </tr>
    <tr>
        <td>2066 | 2066</td>
        <td>
          <a href="/research_results/average/2066_2066.md#%D0%BE%D0%BF%D1%82%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9">
            3.5214<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_2066.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-2-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            3.6183<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_2066.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-3-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.1885<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_2066.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-4-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            5.0674<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_2066.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-5-%D1%81%D0%BB%D0%BE%D0%B2">
            6.0228<br/>(62)
          </a>
        </td>
        <td>
          <a href="/research_results/average/2066_2066.md#%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9">
            3.6003<br/>(15)
          </a>
        </td>
    </tr>
    <tr>
        <td>4109 | 6826</td>
        <td>
          <a href="/research_results/average/4109_6826.md#%D0%BE%D0%BF%D1%82%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9">
            3.7243<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/4109_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-2-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            3.7786<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/4109_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-3-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.2697<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/4109_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-4-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            5.0923<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/4109_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-5-%D1%81%D0%BB%D0%BE%D0%B2">
            6.0370<br/>(166)
          </a>
        </td>
        <td>
          <a href="/research_results/average/4109_6826.md#%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9">
            3.8686<br/>(61)
          </a>
        </td>
    </tr>
    <tr>
        <td>4109 | 4109</td>
        <td>
          <a href="/research_results/average/4109_4109.md#%D0%BE%D0%BF%D1%82%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9">
            3.7362<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/4109_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-2-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            3.7827<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/4109_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-3-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.2799<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/4109_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-4-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            5.1010<br/>(3)
          </a>
        </td>
        <td>
          <a href="/research_results/average/4109_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-5-%D1%81%D0%BB%D0%BE%D0%B2">
            6.0380<br/>(171)
          </a>
        </td>
        <td>
          <a href="/research_results/average/4109_4109.md#%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9">
            3.8686<br/>(61)
          </a>
        </td>
    </tr>
    <tr>
        <td>6826 | 6826</td>
        <td>
          <a href="/research_results/average/6826_6826.md#%D0%BE%D0%BF%D1%82%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9">
            3.9076<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/6826_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-2-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            3.9527<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/6826_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-3-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.3626<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/average/6826_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-4-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            5.1322<br/>(5)
          </a>
        </td>
        <td>
          <a href="/research_results/average/6826_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-5-%D1%81%D0%BB%D0%BE%D0%B2">
            6.0532<br/>(378)
          </a>
        </td>
        <td>
          <a href="/research_results/average/6826_6826.md#%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9">
            4.0951<br/>(166)
          </a>
        </td>
    </tr>
</table>

**Эвристика по максимуму**: минимизация максимального кол-ва оставшихся ответов

<table>
    <tr>
        <td rowspan="2">Алгоритмы \ словари</td>
        <td rowspan="2">Оптимальный</td>
        <td colspan="4">Уникальные символы</td>
        <td rowspan="2">"Человеческий"</td>
    </tr>
    <tr>
        <td>2 слова</td>
        <td>3 слова</td>
        <td>4 слова</td>
        <td>5 слов</td>
    </tr>
    <tr>
        <td>2066 | 6826</td>
        <td>
          <a href="/research_results/max/2066_6826.md#%D0%BE%D0%BF%D1%82%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9">
            3.5194<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-2-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            3.6178<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-3-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.1725<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-4-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            5.0490<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-5-%D1%81%D0%BB%D0%BE%D0%B2">
            6.0102<br/>(35)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_6826.md#%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9">
            3.6202<br/>(16)
          </a>
        </td>
    </tr>
    <tr>
        <td>2066 | 4109</td>
        <td>
          <a href="/research_results/max/2066_4109.md#%D0%BE%D0%BF%D1%82%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9">
            3.5286<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-2-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            3.6207<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-3-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.1813<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-4-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            5.0698<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-5-%D1%81%D0%BB%D0%BE%D0%B2">
            6.0146<br/>(44)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_4109.md#%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9">
            3.6003<br/>(16)
          </a>
        </td>
    </tr>
    <tr>
        <td>2066 | 2066</td>
        <td>
          <a href="/research_results/max/2066_2066.md#%D0%BE%D0%BF%D1%82%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9">
            3.5485<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_2066.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-2-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            3.6188<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_2066.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-3-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.1968<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_2066.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-4-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            5.0674<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_2066.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-5-%D1%81%D0%BB%D0%BE%D0%B2">
            6.0228<br/>(62)
          </a>
        </td>
        <td>
          <a href="/research_results/max/2066_2066.md#%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9">
            3.6003<br/>(15)
          </a>
        </td>
    </tr>
    <tr>
        <td>4109 | 6826</td>
        <td>
          <a href="/research_results/max/4109_6826.md#%D0%BE%D0%BF%D1%82%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9">
            3.7474<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/4109_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-2-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            3.8630<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/4109_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-3-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.2921<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/4109_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-4-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            5.0923<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/4109_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-5-%D1%81%D0%BB%D0%BE%D0%B2">
            6.0370<br/>(166)
          </a>
        </td>
        <td>
          <a href="/research_results/max/4109_6826.md#%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9">
            3.8789<br/>(63)
          </a>
        </td>
    </tr>
    <tr>
        <td>4109 | 4109</td>
        <td>
          <a href="/research_results/max/4109_4109.md#%D0%BE%D0%BF%D1%82%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9">
            3.7633<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/4109_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-2-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            3.8696<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/4109_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-3-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.2977<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/4109_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-4-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            5.1052<br/>(3)
          </a>
        </td>
        <td>
          <a href="/research_results/max/4109_4109.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-5-%D1%81%D0%BB%D0%BE%D0%B2">
            6.0380<br/>(171)
          </a>
        </td>
        <td>
          <a href="/research_results/max/4109_4109.md#%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9">
            3.8789<br/>(63)
          </a>
        </td>
    </tr>
    <tr>
        <td>6826 | 6826</td>
        <td>
          <a href="/research_results/max/6826_6826.md#%D0%BE%D0%BF%D1%82%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9">
            3.9615<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/6826_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-2-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.0210<br/>(3)
          </a>
        </td>
        <td>
          <a href="/research_results/max/6826_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-3-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            4.3859<br/>(0)
          </a>
        </td>
        <td>
          <a href="/research_results/max/6826_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-4-%D1%81%D0%BB%D0%BE%D0%B2%D0%B0">
            5.1407<br/>(4)
          </a>
        </td>
        <td>
          <a href="/research_results/max/6826_6826.md#%D1%83%D0%BD%D0%B8%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%81%D0%B8%D0%BC%D0%B2%D0%BE%D0%BB%D1%8B-5-%D1%81%D0%BB%D0%BE%D0%B2">
            6.0532<br/>(378)
          </a>
        </td>
        <td>
          <a href="/research_results/max/6826_6826.md#%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9">
            4.0975<br/>(136)
          </a>
        </td>
    </tr>
</table>

**Легенда таблиц**:
- `2066 | 4109`
  - `2066` - кол-во возможных ответов
  - `4109` - кол-во слов, допустимых для ввода
- `3.5286 (0)`
  - `3.5286` - Среднее кол-во шагов для прохождения игры
  - `(0)` - Кол-во поражений
