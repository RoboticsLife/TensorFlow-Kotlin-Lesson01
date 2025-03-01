package brain.utils



fun String.filteredHardwareModel(): String {
    return this.filterNot { it == ' ' || it == '-' || it == '_' || it == ',' || it == '.' }.lowercase()
}