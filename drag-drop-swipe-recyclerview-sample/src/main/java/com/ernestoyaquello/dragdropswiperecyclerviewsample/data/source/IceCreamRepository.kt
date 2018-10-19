package com.ernestoyaquello.dragdropswiperecyclerviewsample.data.source

import com.ernestoyaquello.dragdropswiperecyclerviewsample.data.model.IceCream
import com.ernestoyaquello.dragdropswiperecyclerviewsample.data.source.base.BaseRepository
import java.util.*

/**
 * A dummy repository with ice creams.
 */
class IceCreamRepository : BaseRepository<IceCream>() {

    private val adjectives: List<String> = arrayListOf(
            "Acidic",
            "Bitter",
            "Cool",
            "Creamy",
            "Delicious",
            "Gooey",
            "Hot",
            "Juicy",
            "Mild",
            "Nutty",
            "Peppery",
            "Ripe",
            "Salty",
            "Savory",
            "Sour",
            "Spicy",
            "Sticky",
            "Strong",
            "Sweet",
            "Tangy",
            "Tart",
            "Tasteless",
            "Tasty"
    )

    private val names: List<String> = arrayListOf(
            "Apple",
            "Apricot",
            "Avocado",
            "Banana",
            "Bilberry",
            "Blackberry",
            "Blackcurrant",
            "Blueberry",
            "Boysenberry",
            "Currant",
            "Cherry",
            "Coconut",
            "Cranberry",
            "Cucumber",
            "Custard apple",
            "Damson",
            "Date",
            "Dragon Fruit",
            "Elderberry",
            "Fig",
            "Gooseberry",
            "Grape",
            "Raisin",
            "Grapefruit",
            "Guava",
            "Huckleberry",
            "Jack Fruit",
            "Jujube",
            "Juniper berry",
            "Kiwi",
            "Kumquat",
            "Lemon",
            "Lime",
            "Mango",
            "Melon",
            "Cantaloupe",
            "Honeydew",
            "Watermelon",
            "Miracle fruit",
            "Mulberry",
            "Nectarine",
            "Nance",
            "Olive",
            "Orange",
            "Blood orange",
            "Clementine",
            "Tangerine",
            "Papaya",
            "Peach",
            "Pear",
            "Persimmon",
            "Plantain",
            "Plum",
            "Pineapple",
            "Pomegranate",
            "Quince",
            "Raspberry",
            "Redcurrant",
            "Satsuma",
            "Star fruit",
            "Strawberry",
            "Tamarind",
            "After Eight",
            "Altoids",
            "Aniseed ball",
            "Aniseed twist",
            "Apple drops",
            "Banjo",
            "Barley sugar",
            "Black Jack",
            "Bonfire toffee",
            "Bounty",
            "Butterscotch",
            "Coconut ice",
            "Dolly mixture",
            "Double Decker",
            "Drops",
            "Flake",
            "Flying saucer",
            "Fruit Salad",
            "Fudge",
            "Fudge",
            "Fuse",
            "Galaxy",
            "Galaxy Bubbles",
            "Gobstopper",
            "Halls",
            "Humbug",
            "Jelly Babies",
            "Kit Kat",
            "Lion Bar",
            "Liquorice",
            "Mars",
            "Midget Gems",
            "Mingles",
            "Mini Eggs",
            "Galaxy Minstrels",
            "Pear drop",
            "Polo",
            "Quality Street",
            "Revels",
            "Rock",
            "Sherbet",
            "Smarties",
            "Snickers",
            "Sports Mixture",
            "Toffee Crisp",
            "Topic",
            "Trio",
            "Twirl",
            "Victory V",
            "Wham Bar",
            "Wine gum",
            "Yorkie")

    override fun generateNewItem(): IceCream {
        val iceCreamName = generateIceCreamName()
        val iceCreamPrice = generateIceCreamPrice()
        val red = generateIceCreamBasicColor()
        val green = generateIceCreamBasicColor()
        val blue = generateIceCreamBasicColor()
        val (enhancedRed, enhancedGreen, enhancedBlue) = enhanceColorIntensity(red, green, blue)

        val iceCream = IceCream(iceCreamName, iceCreamPrice, enhancedRed, enhancedGreen, enhancedBlue)

        addItem(iceCream)

        return iceCream
    }

    private fun enhanceColorIntensity(red: Float, green: Float, blue: Float): Triple<Float, Float, Float> {
        var enhancedRed = red
        var enhancedGreen = green
        var enhancedBlue = blue

        // Make the strongest color stronger and the weakest weaker to get more intense colors
        when {
            enhancedRed > enhancedGreen && enhancedRed > enhancedBlue -> enhancedRed += 0.1f
            enhancedGreen > enhancedRed && enhancedGreen > enhancedBlue -> enhancedGreen += 0.1f
            enhancedBlue > enhancedRed && enhancedBlue > enhancedGreen -> enhancedBlue += 0.1f
            enhancedRed < enhancedGreen && enhancedRed < enhancedBlue -> enhancedRed -= 0.1f
            enhancedGreen < enhancedRed && enhancedGreen < enhancedBlue -> enhancedGreen -= 0.1f
            enhancedBlue < enhancedRed && enhancedBlue < enhancedGreen -> enhancedBlue -= 0.1f
        }

        return Triple(enhancedRed, enhancedGreen, enhancedBlue)
    }

    private fun generateIceCreamName() = "${adjectives.shuffled().take(1)[0]} ${names.shuffled().take(1)[0]}"

    private fun generateIceCreamPrice() = (80..500).random().toFloat() / 100f

    private fun generateIceCreamBasicColor() = (80..210).random().toFloat() / 255f

    private fun ClosedRange<Int>.random() =
            Random().nextInt((endInclusive + 1) - start) +  start

    companion object {
        private var instance: IceCreamRepository? = null

        fun getInstance(): IceCreamRepository {
            if (instance == null)
                instance = IceCreamRepository()

            return instance as IceCreamRepository
        }
    }
}