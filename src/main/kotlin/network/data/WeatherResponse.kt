package network.data

data class WeatherResponse(
    val base: String? = null,
    val clouds: Clouds? = null,
    val cod: Int? = null,
    val coord: Coord? = null,
    val dt: Int? = null,
    val id: Int? = null,
    val main: Main? = null,
    val name: String? = null,
    val snow: Snow? = null,
    val sys: Sys? = null,
    val timezone: Int? = null,
    val visibility: Int? = null,
    val weather: List<Weather?>? = null,
    val wind: Wind? = null
) {
    data class Clouds(
        val all: Int? = null
    )

    data class Coord(
        val lat: Double? = null,
        val lon: Double? = null
    )

    data class Main(
        val feels_like: Double? = null,
        val grnd_level: Int? = null,
        val humidity: Int? = null,
        val pressure: Int? = null,
        val sea_level: Int? = null,
        val temp: Double? = null,
        val temp_max: Double? = null,
        val temp_min: Double? = null
    )

    data class Snow(
        val `1h`: Double? = null
    )

    data class Sys(
        val country: String? = null,
        val id: Int? = null,
        val sunrise: Int? = null,
        val sunset: Int? = null,
        val type: Int? = null
    )

    data class Weather(
        val description: String? = null,
        val icon: String? = null,
        val id: Int? = null,
        val main: String? = null
    )

    data class Wind(
        val deg: Int? = null,
        val speed: Double? = null
    )
}