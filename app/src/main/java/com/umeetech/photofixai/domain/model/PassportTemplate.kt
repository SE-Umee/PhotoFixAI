package com.umeetech.photofixai.domain.model

/** Physical unit for a passport/ID template. */
enum class SizeUnit(val label: String) { MM("mm"), INCH("inch"), PIXEL("px") }

/**
 * A passport / ID photo specification. Dimensions are stored in [unit]; the editor
 * converts to pixels at print DPI when exporting.
 */
data class PassportTemplate(
    val id: String,
    val name: String,
    val width: Float,
    val height: Float,
    val unit: SizeUnit,
    val requiredBackground: BackgroundOption,
    val description: String
) {
    val aspectRatio: Float get() = width / height

    fun pixelSize(dpi: Int = 300): Pair<Int, Int> = when (unit) {
        SizeUnit.MM -> (width / 25.4f * dpi).toInt() to (height / 25.4f * dpi).toInt()
        SizeUnit.INCH -> (width * dpi).toInt() to (height * dpi).toInt()
        SizeUnit.PIXEL -> width.toInt() to height.toInt()
    }
}

/** Built-in country/document templates. */
object PassportTemplates {
    val Pakistan = PassportTemplate(
        id = "pk_passport", name = "Pakistan Passport",
        width = 35f, height = 45f, unit = SizeUnit.MM,
        requiredBackground = BackgroundOption.White,
        description = "35 × 45 mm, white background."
    )
    val PakistanCnic = PassportTemplate(
        id = "pk_cnic", name = "Pakistan CNIC",
        width = 35f, height = 45f, unit = SizeUnit.MM,
        requiredBackground = BackgroundOption.Blue,
        description = "35 × 45 mm, blue background."
    )
    val UsPassport = PassportTemplate(
        id = "us_passport", name = "US Passport",
        width = 2f, height = 2f, unit = SizeUnit.INCH,
        requiredBackground = BackgroundOption.White,
        description = "2 × 2 inch, white background."
    )
    val UkPassport = PassportTemplate(
        id = "uk_passport", name = "UK Passport",
        width = 35f, height = 45f, unit = SizeUnit.MM,
        requiredBackground = BackgroundOption.White,
        description = "35 × 45 mm, light grey/white background."
    )
    val CanadaVisa = PassportTemplate(
        id = "ca_visa", name = "Canada Visa",
        width = 35f, height = 45f, unit = SizeUnit.MM,
        requiredBackground = BackgroundOption.White,
        description = "35 × 45 mm, white background."
    )
    val UaeVisa = PassportTemplate(
        id = "ae_visa", name = "UAE Visa",
        width = 43f, height = 55f, unit = SizeUnit.MM,
        requiredBackground = BackgroundOption.White,
        description = "43 × 55 mm, white background."
    )
    val Custom = PassportTemplate(
        id = "custom", name = "Custom Size",
        width = 35f, height = 45f, unit = SizeUnit.MM,
        requiredBackground = BackgroundOption.White,
        description = "Set your own dimensions."
    )

    val all = listOf(Pakistan, PakistanCnic, UsPassport, UkPassport, CanadaVisa, UaeVisa, Custom)

    fun byId(id: String): PassportTemplate = all.firstOrNull { it.id == id } ?: Pakistan
}
