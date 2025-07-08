package com.pritechvior.mealeastimatonew.data.model

data class TanzanianDish(
    val nameEnglish: String,
    val nameSwahili: String,
    val ingredientsEnglish: List<String>,
    val ingredientsSwahili: List<String>,
    val emoji: String = "🍽️" // Default emoji if none specified
)

object TanzanianDishes {
    val dishes = listOf(
        TanzanianDish(
            nameEnglish = "Ugali",
            nameSwahili = "Ugali",
            ingredientsEnglish = listOf(
                "Maize flour",
                "Water"
            ),
            ingredientsSwahili = listOf(
                "Unga wa mahindi",
                "Maji"
            ),
            emoji = "🥘"
        ),
        TanzanianDish(
            nameEnglish = "Rice and Beans",
            nameSwahili = "Wali na Maharage",
            ingredientsEnglish = listOf(
                "Rice",
                "Red kidney beans",
                "Onion",
                "Garlic",
                "Ginger",
                "Turmeric",
                "Cumin",
                "Coriander",
                "Chili",
                "Coconut milk"
            ),
            ingredientsSwahili = listOf(
                "Mchele",
                "Maharage",
                "Kitunguu",
                "Kitunguu saumu",
                "Tangawizi",
                "Manjano",
                "Binzari",
                "Korianda",
                "Pilipili",
                "Nazi"
            ),
            emoji = "🍚"
        ),
        TanzanianDish(
            nameEnglish = "Pilau",
            nameSwahili = "Pilau",
            ingredientsEnglish = listOf(
                "Rice",
                "Meat",
                "Onion",
                "Garlic",
                "Cinnamon",
                "Cardamom",
                "Cloves",
                "Cumin",
                "Pepper",
                "Oil"
            ),
            ingredientsSwahili = listOf(
                "Mchele",
                "Nyama",
                "Kitunguu",
                "Kitunguu saumu",
                "Mdalasini",
                "Iliki",
                "Karafuu",
                "Binzari",
                "Pilipili",
                "Mafuta"
            ),
            emoji = "🍛"
        ),
        TanzanianDish(
            nameEnglish = "Chips and Eggs",
            nameSwahili = "Chipsi Mayai",
            ingredientsEnglish = listOf(
                "Potatoes",
                "Eggs",
                "Salt",
                "Oil"
            ),
            ingredientsSwahili = listOf(
                "Viazi",
                "Mayai",
                "Chumvi",
                "Mafuta"
            ),
            emoji = "🍟"
        ),
        TanzanianDish(
            nameEnglish = "Fish Curry",
            nameSwahili = "Mchuzi wa Samaki",
            ingredientsEnglish = listOf(
                "Fish",
                "Coconut milk",
                "Tomatoes",
                "Onions",
                "Green pepper",
                "Carrots",
                "Curry powder",
                "Lemon",
                "Salt"
            ),
            ingredientsSwahili = listOf(
                "Samaki",
                "Nazi",
                "Nyanya",
                "Vitunguu",
                "Pilipili hoho",
                "Karoti",
                "Bizari ya pilau",
                "Limu",
                "Chumvi"
            ),
            emoji = "🐟"
        ),
        TanzanianDish(
            nameEnglish = "Zanzibar Pizza",
            nameSwahili = "Pizza ya Zanzibar",
            ingredientsEnglish = listOf(
                "Flour",
                "Ground meat",
                "Onion",
                "Garlic",
                "Chili",
                "Egg",
                "Cheese",
                "Tomato",
                "Cabbage",
                "Oil"
            ),
            ingredientsSwahili = listOf(
                "Unga",
                "Nyama ya kusaga",
                "Kitunguu",
                "Kitunguu saumu",
                "Pilipili",
                "Yai",
                "Jibini",
                "Nyanya",
                "Kabichi",
                "Mafuta"
            ),
            emoji = "🍕"
        ),
        TanzanianDish(
            nameEnglish = "Mandazi",
            nameSwahili = "Mandazi",
            ingredientsEnglish = listOf(
                "Wheat flour",
                "Sugar",
                "Milk",
                "Eggs",
                "Baking powder",
                "Oil"
            ),
            ingredientsSwahili = listOf(
                "Unga wa ngano",
                "Sukari",
                "Maziwa",
                "Mayai",
                "Baking powder",
                "Mafuta"
            ),
            emoji = "🥯"
        ),
        TanzanianDish(
            nameEnglish = "Octopus Coconut Curry",
            nameSwahili = "Pweza wa Nazi",
            ingredientsEnglish = listOf(
                "Octopus",
                "Coconut milk",
                "Onion",
                "Garlic",
                "Ginger",
                "Tomato",
                "Curry spices",
                "Chili",
                "Lemon"
            ),
            ingredientsSwahili = listOf(
                "Pweza",
                "Nazi",
                "Kitunguu",
                "Kitunguu saumu",
                "Tangawizi",
                "Nyanya",
                "Viungo vya pilau",
                "Pilipili",
                "Limu"
            ),
            emoji = "🐙"
        ),
        TanzanianDish(
            nameEnglish = "Zanzibar Mix (Urojo)",
            nameSwahili = "Urojo",
            ingredientsEnglish = listOf(
                "Potatoes",
                "Coconut chutney",
                "Fried cassava pieces",
                "Boiled eggs",
                "Tamarind",
                "Lemon",
                "Curry spices"
            ),
            ingredientsSwahili = listOf(
                "Viazi",
                "Chutney ya nazi",
                "Vipande vya muhogo vilivyokaangwa",
                "Mayai",
                "Matango",
                "Limu",
                "Viungo vya pilau"
            ),
            emoji = "🥘"
        ),
        TanzanianDish(
            nameEnglish = "Eggplant Curry",
            nameSwahili = "Mchuzi wa Biringani",
            ingredientsEnglish = listOf(
                "Eggplant",
                "Onion",
                "Tomato",
                "Garlic",
                "Ginger",
                "Carrots",
                "Vegetable oil",
                "Potatoes",
                "Coconut milk"
            ),
            ingredientsSwahili = listOf(
                "Biringani",
                "Kitunguu",
                "Nyanya",
                "Kitunguu saumu",
                "Tangawizi",
                "Karoti",
                "Mafuta ya kupikia",
                "Viazi",
                "Nazi"
            ),
            emoji = "🍆"
        ),
        TanzanianDish(
            nameEnglish = "Mshikaki (Spiced Meat Skewers)",
            nameSwahili = "Mshikaki",
            ingredientsEnglish = listOf(
                "Beef",
                "Garlic",
                "Ginger",
                "Lemon",
                "Chili",
                "Salt",
                "Oil"
            ),
            ingredientsSwahili = listOf(
                "Nyama ya ng'ombe",
                "Kitunguu saumu",
                "Tangawizi",
                "Limu",
                "Pilipili",
                "Chumvi",
                "Mafuta"
            ),
            emoji = "🍖"
        ),
        TanzanianDish(
            nameEnglish = "Grilled Meat",
            nameSwahili = "Nyama Choma",
            ingredientsEnglish = listOf(
                "Goat or beef meat",
                "Salt",
                "Black pepper",
                "Garlic",
                "Onions",
                "Lemon",
                "Oil"
            ),
            ingredientsSwahili = listOf(
                "Nyama ya mbuzi au ng'ombe",
                "Chumvi",
                "Pilipili",
                "Kitunguu saumu",
                "Vitunguu",
                "Limu",
                "Mafuta"
            ),
            emoji = "🥩"
        ),
        TanzanianDish(
            nameEnglish = "Chicken Curry",
            nameSwahili = "Mchuzi wa Kuku",
            ingredientsEnglish = listOf(
                "Chicken",
                "Coconut or peanut paste",
                "Onions",
                "Peas",
                "Tomatoes",
                "Garlic",
                "Ginger",
                "Curry spices"
            ),
            ingredientsSwahili = listOf(
                "Kuku",
                "Nazi au korosho",
                "Vitunguu",
                "Kunde",
                "Nyanya",
                "Kitunguu saumu",
                "Tangawizi",
                "Viungo vya pilau"
            ),
            emoji = "🍗"
        ),
        TanzanianDish(
            nameEnglish = "Makande",
            nameSwahili = "Makande",
            ingredientsEnglish = listOf(
                "Maize",
                "Red kidney beans",
                "Coconut milk",
                "Onions",
                "Garlic",
                "Water",
                "Salt"
            ),
            ingredientsSwahili = listOf(
                "Mahindi",
                "Maharage nyekundu",
                "Nazi",
                "Vitunguu",
                "Kitunguu saumu",
                "Maji",
                "Chumvi"
            ),
            emoji = "��"
        )
    )
} 