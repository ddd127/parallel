## Pipelining

Пусть нам дано 2-3 дерево размера n.
Мы хотим вставить в него m элементов

Если никакие элементы не попадают в одну и ту же дырку, всё ок -
мы сможем обработать такие кейсы leve-by-level
```
Span = O(log(n) * log(m))
```

А вот если попадают, то:
- вставим сначала средний (до конца)
- потом два средних из двух половин
- и тд
```
Span = O(log(1) * log(n) + log(2) * log(n) + ... + log(m) * log(n)) =
  = O(log^2(m) * log(n))
```

А теперь pipelining:
начнем вставлять следующие элементы после того,
как пропихнули следующие на 1 + 1 вверх, чтоб нижний lvl зафиксировался.
```
Span = O(log(m) * (log(n) + log(m)))
```
