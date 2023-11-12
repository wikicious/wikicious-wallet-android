package com.wikicious.app.core.managers

import com.wikicious.app.core.IRandomProvider
import java.util.*

class RandomProvider : IRandomProvider {

    override fun getRandomNumbers(count: Int, maxIndex: Int): List<Int> {
        val numbers = mutableListOf<Int>()

        val random = Random()

        while (numbers.size < count) {
            val number = random.nextInt(maxIndex)

            if (!numbers.contains(number)) {
                numbers.add(number)
            }
        }

        return numbers
    }

}
