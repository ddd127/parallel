## Sequence algorithms

Классические алгоритмы на Sequence<T>:
- `map<R>(mapper: (T) -> R): Sequence<R>`
- `reduce<R>(neutral: R, associative: (R, T) -> R): R`
- `scan<R>(neutral: R, associative: (R, T) -> R): Sequence<R>`
- `filter(indicator: (T) -> Bool): Sequence<T>`

Разумеется, можно написать тупую реализацию без BLOCK, 
но жить с таким будет невозможно.

Задачка подбора размера блока - granularity control problem - 
в общем случае весьма нетривиальна.


```
fun parallelFor(
    block: Int,
    l: Int,
    r: Int,
    action: (index: Int) -> Unit,
) {
    if (r - l <= blockSize) {
        for (i in l until r) {
            action(i)
        }
        return
    }
    join(
        fork { parallelFor(block, l, m, action) },
        fork { parallelFor(block, m, r, action) },
    )
}

Work: O(n)
Span: O(log(n / BLOCK) + BLOCK)
```

```
fun List<T>.parallelMap(
    block: Int,
    l: Int,
    r: Int,
    target: MutableList<R?> = MutableList(size) { null },
    mapper: (T) -> R,
): List<R> {
    if (r - l <= block) {
        for (i in l until r) {
            target[i] = mapper(this[i])
            mapper(i)
        }
        return
    }
    joinAll(
        launch { parallelMap(block, l, m, target, mapper) },
        launch { parallelMap(block, m, r, target, mapper) },
    )
    return target as List<R>
}
```

```
fun List<T>.parallelReduce(
    block: Int,
    l: Int,
    r: Int,
    associative: (T, T) -> T,
): List<T> {
    if (r - l <= block) {
        var result = this[l]
        for (i in l.inc() until r) {
            result = associative(result, this[i])
        }
        return result
    }
    val l = async { parallelReduce(block, l, m, associative) }
    val r = async { parallelReduce(block, m, r, associative) }
    return mapper(l, r)
}
```

```
fun List<T>.parallelScan(
    block: Int,
    l: Int,
    r: Int,
    target: MutableList<T?> = MutableList(size) { null },
    associative: (T, T) -> T,
): List<R> {
    if (r - l <= block) {
        target[l] = this[l]
        for (i in l.inc() until r) {
            target[i] = associative(target[i - 1], this[i])
        }
        return target
    }

    val sums = (0 until (n + block - 1) / block).map { blockIndex ->
        async {
            val l = blockIndex * block
            val r = minOf(size, l + block)
            reduce(block, l, r, associative)
        }
    }.awaitAll()

    (0 until (n + block - 1) / block).map { blockIndex ->
        launch {
            val l = blockIndex * block
            val r = minOf(size, l + block)
            target[l] = associative(sums[blockIndex], this[l])
            for (i in l.inc() until r) {
                target[i] = associative(target[i - 1], this[i])
            }
        }
    }.joinAll()

    return target
}
```

```
fun List<T>.parallelFilter(
    block: Int,
    indicator: (T) -> Bool,
): List<T> {
    val flags = MutableList<Int> { 0 }
    val counts = (0 until (n + block - 1) / block).map { blockIndex ->
        async {
            var count = 0
            val l = blockIndex * block
            val r = minOf(size, l + block)
            
            for (i in l until r) {
                if (indicator(this[i])) {
                    flags = 1
                    ++count
                }
            }
            return@async count 
        }
    }.awaitAll()
    val sums = counts.parallelScan(block, 0, counts.size, Int::plus)

    val target = MutableList<T?>(sums.last() + counts.last()) { null }

    (0 until (n + block - 1) / block).map { blockIndex ->
        launch {
            val l = blockIndex * block
            val r = minOf(size, l + block)
            var position = sums[blockIndex]

            for (i in l until r) {
                if (flags[i]) {
                    target[position++] = this[i]
                }
            }
        }
    }.joinAll()

    return target
}
```
