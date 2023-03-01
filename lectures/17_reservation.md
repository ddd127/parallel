## Deterministic reservation

Есть некоторое количество алгосов, 
которые требуют жадного "прохода слева направо" 
и каких-то последовательных действий.
Например, Spanning forest,
Minimum spanning tree, Maximal Independent Set.

Казалось бы, параллелить тут особо нечего, но нет.

Введем такую конкурентную структуру, как Reservation.

```
interface Reservation {
    /**
     * Reservates item if requestor id less than already reserved
     */
    fun reserve(item, id)

    /**
     * Checks if passed item was reserved by id
     */
    fun check(item, id)
}
```

Тогда во всех вышеперечисленных алгоритмах можно брать некоторые d
следующих по порядку перебора элементов и пытаться проделывать
операции для пачки сразу, через reserve() + check()
