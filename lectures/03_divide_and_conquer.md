## Divide-and-Conquer

Будем рекурсивно делить задачу на подзадачи
и затем собирать итоговый результат из результатов подзадач.

### Пример: mergesort

```
fun List<T>.mergeSorted(
    block: Int,
    l: Int,
    r: Int,
): List<T> {
    if (r - l < block) {
        return this.view(l, r).sorted()
    }
    val m = (l + r) / 2
    val left = async { mergeSorted(block, l, m) }
    val right = async { mergeSorted(block, m, r) }
    return merge(left.await(), right.await())
}
```

Допустим, span merge = O(M(n)). Тогда

```
Work = O(n * log(n)),
Span = O(log(n) * (M(n) + M(n / 2) + M(n / 4) + ...)) =
     = O(log(n) * M(n))    // если M - полилог
```

### Параллельный merge:

Тупой вариант:

За Work = O(n * log(n)) и Span = O(log(n)) найдем каждому элементу
каждого массива его место в результирующем массиве через бин поиск.

Вариант поумнее:

Побьем каждый массив на блоки размера log(n).
Для каждого старта блока из первого массива
найдем его место во втором бин поиском,
аналогично - для слишком больших блоков из 2-го массива.

Тогда merge каждого куска у нас за log(n) work, а total merge-а такой:
```
Span = O(log(n))
Work = O(n / log(n) * log(n)) = O(n)
```
