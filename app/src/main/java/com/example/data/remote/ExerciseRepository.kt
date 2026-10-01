package com.example.data.remote

import com.example.data.model.ExerciseCatalogItem

object ExerciseRepository {

    val allExercises: List<ExerciseCatalogItem> = listOf(
        // صدر (Chest)
        ExerciseCatalogItem(
            id = "ex_chest_bench_press",
            name = "Barbell Bench Press",
            nameArabic = "بنش برس مستوي بالبار",
            muscleGroup = "chest",
            muscleArabic = "صدر",
            equipment = "barbell",
            equipmentArabic = "بار",
            instructionsArabic = "استلقِ على المقعد المسطح، امسك البار بقبضة أوسع قليلاً من الكتفين، وانزل به ببطء نحو منتصف الصدر ثم ادفعه بقوة."
        ),
        ExerciseCatalogItem(
            id = "ex_chest_incline_db_press",
            name = "Incline Dumbbell Press",
            nameArabic = "ضغط صدر علوي بالدمبل",
            muscleGroup = "chest",
            muscleArabic = "صدر",
            equipment = "dumbbell",
            equipmentArabic = "دمبل",
            instructionsArabic = "اضبط المقعد بزاوية 30-45 درجة. ادفع الأوزان لأعلى مع التركيز على عصر الألياف العلوية للصدر."
        ),
        ExerciseCatalogItem(
            id = "ex_chest_cable_fly",
            name = "Cable Crossover Fly",
            nameArabic = "تفتيح صدر بالكابل (كروس أوفر)",
            muscleGroup = "chest",
            muscleArabic = "صدر",
            equipment = "cable",
            equipmentArabic = "كابل",
            instructionsArabic = "اسحب الكابلين للأمام بحركة قوسية واضغط عضلات الصدر جيداً في أقصى نقطة."
        ),
        ExerciseCatalogItem(
            id = "ex_chest_dips",
            name = "Chest Dips",
            nameArabic = "متوازي للصدر (Dips)",
            muscleGroup = "chest",
            muscleArabic = "صدر",
            equipment = "bodyweight",
            equipmentArabic = "وزن الجسم",
            instructionsArabic = "مل بجذعك للأمام قليلاً لاستهداف الصدر السفلي، وانزل حتى 90 درجة في الكوع."
        ),
        ExerciseCatalogItem(
            id = "ex_chest_pushups",
            name = "Push-ups",
            nameArabic = "تمرين الضغط (Push-ups)",
            muscleGroup = "chest",
            muscleArabic = "صدر",
            equipment = "bodyweight",
            equipmentArabic = "وزن الجسم",
            instructionsArabic = "حافظ على استقامة الظهر والجذع مشدوداً مع النزول الكامل وصعود قوي."
        ),

        // ظهر (Back)
        ExerciseCatalogItem(
            id = "ex_back_deadlift",
            name = "Conventional Deadlift",
            nameArabic = "ديدليفت تقليدي بالبار",
            muscleGroup = "back",
            muscleArabic = "ظهر",
            equipment = "barbell",
            equipmentArabic = "بار",
            instructionsArabic = "حافظ على استقامة العمود الفقري، ادفع الأرض بقدميك واسحب البار بمحاذاة القصبة."
        ),
        ExerciseCatalogItem(
            id = "ex_back_barbell_row",
            name = "Bent Over Barbell Row",
            nameArabic = "سحب ظهر بالبار منحنياً",
            muscleGroup = "back",
            muscleArabic = "ظهر",
            equipment = "barbell",
            equipmentArabic = "بار",
            instructionsArabic = "انحنِ للأمام بزاوية 45 درجة، اسحب البار نحو السرة مع ضم لوحي الكتف."
        ),
        ExerciseCatalogItem(
            id = "ex_back_lat_pulldown",
            name = "Lat Pulldown",
            nameArabic = "سحب ظهر عالي بالجهاز",
            muscleGroup = "back",
            muscleArabic = "ظهر",
            equipment = "cable",
            equipmentArabic = "كابل/جهاز",
            instructionsArabic = "اسحب المقبض لأسفل نحو عظمة الترقوة دون تأرجح للخلف."
        ),
        ExerciseCatalogItem(
            id = "ex_back_seated_row",
            name = "Seated Cable Row",
            nameArabic = "سحب أرضي ضيق بالكابل",
            muscleGroup = "back",
            muscleArabic = "ظهر",
            equipment = "cable",
            equipmentArabic = "كابل",
            instructionsArabic = "حافظ على صدرك مرتفعاً، واسحب المقبض لأسفل البطن مع ثبات الجذع."
        ),
        ExerciseCatalogItem(
            id = "ex_back_pullups",
            name = "Pull-ups",
            nameArabic = "عقلة قبضة واسعة",
            muscleGroup = "back",
            muscleArabic = "ظهر",
            equipment = "bodyweight",
            equipmentArabic = "وزن الجسم",
            instructionsArabic = "اسحب جسمك لأعلى حتى يتجاوز ذقنك البار، ثم انزل ببطء لتحقيق تمدد كامل."
        ),

        // أرجل (Legs)
        ExerciseCatalogItem(
            id = "ex_legs_squat",
            name = "Barbell Back Squat",
            nameArabic = "سكوات خلفي بالبار",
            muscleGroup = "legs",
            muscleArabic = "أرجل",
            equipment = "barbell",
            equipmentArabic = "بار",
            instructionsArabic = "ضع القدمين بعرض الكتفين، انزل بحوضك لأسفل كأنك تجلس على كرسي حتى يوازي الفخذ الأرض."
        ),
        ExerciseCatalogItem(
            id = "ex_legs_leg_press",
            name = "Leg Press Machine",
            nameArabic = "دفع أرجل بالجهاز (Leg Press)",
            muscleGroup = "legs",
            muscleArabic = "أرجل",
            equipment = "machine",
            equipmentArabic = "أجهزة",
            instructionsArabic = "اضغط المنصة بقدميك دون قفل الركبة تماماً عند القمة لحماية المفاصل."
        ),
        ExerciseCatalogItem(
            id = "ex_legs_rdl",
            name = "Romanian Deadlift",
            nameArabic = "ديدليفت روماني للأفخاذ الخلفية",
            muscleGroup = "legs",
            muscleArabic = "أرجل",
            equipment = "barbell",
            equipmentArabic = "بار",
            instructionsArabic = "انحنِ عند مفصل الورك مع ثني طفيف في الركبة حتى تشعر بتمدد الفخذ الخلفي."
        ),
        ExerciseCatalogItem(
            id = "ex_legs_leg_extension",
            name = "Leg Extension",
            nameArabic = "رفرفة أمامية للأرجل بالجهاز",
            muscleGroup = "legs",
            muscleArabic = "أرجل",
            equipment = "machine",
            equipmentArabic = "أجهزة",
            instructionsArabic = "ارفع الوزن بمد الساقين بالكامل مع الثبات لثانية عند أعلى نقطة لعصر العضلة الرباعية."
        ),
        ExerciseCatalogItem(
            id = "ex_legs_leg_curl",
            name = "Lying Leg Curl",
            nameArabic = "رفرفة خلفية للأرجل بالجهاز",
            muscleGroup = "legs",
            muscleArabic = "أرجل",
            equipment = "machine",
            equipmentArabic = "أجهزة",
            instructionsArabic = "اثنِ الساقين لسحب الوسادة نحو المقعدة مع التحكم التام أثناء النزول."
        ),
        ExerciseCatalogItem(
            id = "ex_legs_calf_raise",
            name = "Standing Calf Raise",
            nameArabic = "رفع السمانة واقفاً",
            muscleGroup = "legs",
            muscleArabic = "أرجل",
            equipment = "machine",
            equipmentArabic = "أجهزة",
            instructionsArabic = "ارتفع على أطراف أصابع القدمين لأقصى مدى حركي مع النزول البطيء للتمدد."
        ),

        // أكتاف (Shoulders)
        ExerciseCatalogItem(
            id = "ex_shoulders_ohp",
            name = "Overhead Barbell Press",
            nameArabic = "ضغط كتف عسكري واقف بالبار",
            muscleGroup = "shoulders",
            muscleArabic = "أكتاف",
            equipment = "barbell",
            equipmentArabic = "بار",
            instructionsArabic = "ادفع البار عمودياً فوق الرأس مع شد عضلات البطن والمؤخرة لثبات الظهر."
        ),
        ExerciseCatalogItem(
            id = "ex_shoulders_db_press",
            name = "Dumbbell Shoulder Press",
            nameArabic = "ضغط كتف جالس بالدمبل",
            muscleGroup = "shoulders",
            muscleArabic = "أكتاف",
            equipment = "dumbbell",
            equipmentArabic = "دمبل",
            instructionsArabic = "ادفع الدمبلين لأعلى بزاوية كوع مريحة ثم انزل بمحاذاة الأذنين."
        ),
        ExerciseCatalogItem(
            id = "ex_shoulders_lateral_raise",
            name = "Lateral Dumbbell Raise",
            nameArabic = "رفرفة كتف جانبي بالدمبل",
            muscleGroup = "shoulders",
            muscleArabic = "أكتاف",
            equipment = "dumbbell",
            equipmentArabic = "دمبل",
            instructionsArabic = "ارفع الذراعين جانباً بمحاذاة الكتف مع ثني خفيف في الكوع وتركيز العصر في الجانب."
        ),
        ExerciseCatalogItem(
            id = "ex_shoulders_face_pull",
            name = "Face Pull with Rope",
            nameArabic = "سحب كابل للحبل نحو الوجه (Face Pull)",
            muscleGroup = "shoulders",
            muscleArabic = "أكتاف",
            equipment = "cable",
            equipmentArabic = "كابل",
            instructionsArabic = "اسحب الحبل نحو مستوى العين مع تدوير الكتف خارجياً لحماية وتقوية الكتف الخلفي."
        ),

        // ذراعين (Arms: Biceps & Triceps)
        ExerciseCatalogItem(
            id = "ex_arms_barbell_curl",
            name = "EZ Bar Bicep Curl",
            nameArabic = "تبادل بايسبس بالبار المتعرج",
            muscleGroup = "biceps",
            muscleArabic = "بايسبس",
            equipment = "barbell",
            equipmentArabic = "بار",
            instructionsArabic = "ثبّت الكوعين بجانب الجذع واثنِ الساعدين للأعلى دون تأرجح الظهر."
        ),
        ExerciseCatalogItem(
            id = "ex_arms_hammer_curl",
            name = "Dumbbell Hammer Curl",
            nameArabic = "هامر كيرل بالدمبل",
            muscleGroup = "biceps",
            muscleArabic = "بايسبس",
            equipment = "dumbbell",
            equipmentArabic = "دمبل",
            instructionsArabic = "حافظ على اتجاه راحتي اليدين للداخل لاستهداف العضلة العضدية والساعد."
        ),
        ExerciseCatalogItem(
            id = "ex_arms_incline_curl",
            name = "Incline Dumbbell Curl",
            nameArabic = "تبادل بايسبس على مقعد مائل",
            muscleGroup = "biceps",
            muscleArabic = "بايسبس",
            equipment = "dumbbell",
            equipmentArabic = "دمبل",
            instructionsArabic = "يوفر تمدداً كبيراً للرأس الطويل لعضلة البايسبس."
        ),
        ExerciseCatalogItem(
            id = "ex_arms_tricep_pushdown",
            name = "Tricep Rope Pushdown",
            nameArabic = "ضغط ترايسبس بالحبل لأسفل",
            muscleGroup = "triceps",
            muscleArabic = "ترايسبس",
            equipment = "cable",
            equipmentArabic = "كابل",
            instructionsArabic = "ادفع الحبل لأسفل وافتح أطرافه عند النهاية لعصر قوي للترايسبس."
        ),
        ExerciseCatalogItem(
            id = "ex_arms_skull_crusher",
            name = "Skull Crushers (Lying Triceps)",
            nameArabic = "ترايسبس مستلقياً بالبار (سكل كراشر)",
            muscleGroup = "triceps",
            muscleArabic = "ترايسبس",
            equipment = "barbell",
            equipmentArabic = "بار",
            instructionsArabic = "انزل بالبار نحو الجبهة مع ثبات الذراع العلوي ثم افرد الذراعين للأعلى."
        ),

        // بطن وكور (Abs & Core)
        ExerciseCatalogItem(
            id = "ex_abs_plank",
            name = "Plank",
            nameArabic = "تمرين البلانك الثابت",
            muscleGroup = "abs",
            muscleArabic = "بطن",
            equipment = "bodyweight",
            equipmentArabic = "وزن الجسم",
            instructionsArabic = "حافظ على استقامة الجسم كلوح خشبي مع شد عضلات البطن والتنفس المنتظم."
        ),
        ExerciseCatalogItem(
            id = "ex_abs_hanging_leg_raise",
            name = "Hanging Leg Raise",
            nameArabic = "رفع الساقين معلقاً على العقلة",
            muscleGroup = "abs",
            muscleArabic = "بطن",
            equipment = "bodyweight",
            equipmentArabic = "وزن الجسم",
            instructionsArabic = "ارفع رجليك بزاوية 90 درجة مع تجنب الأرجحة لعصر الجزء السفلي من البطن."
        )
    )

    fun getExercisesByMuscle(muscleGroup: String): List<ExerciseCatalogItem> {
        return allExercises.filter { it.muscleGroup.equals(muscleGroup, ignoreCase = true) || it.muscleArabic == muscleGroup }
    }

    fun search(query: String): List<ExerciseCatalogItem> {
        val q = query.trim().lowercase()
        return allExercises.filter {
            it.nameArabic.contains(q, ignoreCase = true) ||
                    it.name.contains(q, ignoreCase = true) ||
                    it.muscleArabic.contains(q, ignoreCase = true)
        }
    }
}
