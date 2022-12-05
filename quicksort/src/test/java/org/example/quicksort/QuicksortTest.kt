package org.example.quicksort

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.random.Random
import kotlin.test.assertContentEquals

class QuicksortTest {

    @ParameterizedTest
    @EnumSource(Quicksort.QuicksortType::class)
    fun test(type: Quicksort.QuicksortType) {
        type.withSort {
            testEmpty(it)
            testSingleton(it)
            testSmall(it)
            testRandom(it)
        }
    }

    private fun testEmpty(quicksort: Quicksort) {
        val array = intArrayOf()
        assertSorted(quicksort, array)
    }

    private fun testSingleton(quicksort: Quicksort) {
        val array = intArrayOf(42)
        assertSorted(quicksort, array)
    }

    private fun testSmall(quicksort: Quicksort) {
        val arrays = listOf(
            intArrayOf(3, 2, 1),
            intArrayOf(3, 3, 3),
            intArrayOf(3, 2, 1, 2, 3),
            intArrayOf(1, 2, 3, 2, 1),
            intArrayOf(1, -1, 0, 1, -1, 0, 1, -1, 0)
        )
        arrays.forEach {
            assertSorted(quicksort, it)
        }
    }

    private fun testRandom(quicksort: Quicksort) {
        val rand = random
        var base = 1
        repeat(6) {
            base *= 10
            repeat(5) {
                val array = IntArray(base) {
                    rand.nextInt()
                }
                assertSorted(quicksort, array)
            }
        }
    }

    private fun assertSorted(quicksort: Quicksort, array: IntArray) {
        val expected = array.sorted().toIntArray()
        val actual = array.also { quicksort.sort(it) }
        assertContentEquals(expected, actual)
    }

    companion object {
        private const val seed = 127
        val random
            get() = Random(seed)
    }
}
