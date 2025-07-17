package com.arcane.model

import java.io.File
import java.util.*

data class ArchiveEntry(
    val name: String,
    val path: String,
    val size: Long,
    val isDirectory: Boolean,
    val lastModified: Date? = null,
    val parent: String? = null
)

data class ArchiveInfo(
    val file: File,
    val type: ArchiveType,
    val entries: List<ArchiveEntry>,
    val name: String = file.name,
    val size: Long = file.length(),
    val totalFiles: Int = entries.size
)

enum class ArchiveType(val extension: String, val displayName: String) {
    ZIP(".zip", "ZIP Archive"),
    SEVEN_Z(".7z", "7-Zip Archive"),
    RAR(".rar", "RAR Archive"),
    TAR_GZ(".tar.gz", "Tar GZ Archive"),
    TAR(".tar", "Tar Archive"),
    GZIP(".gz", "GZip Archive"),
    UNKNOWN("", "Unknown Archive")
}

enum class ExtractionStatus {
    IDLE, EXTRACTING, COMPLETED, ERROR
}

data class ExtractionProgress(
    val status: ExtractionStatus,
    val progress: Float,
    val currentFile: String = "",
    val totalFiles: Int = 0,
    val processedFiles: Int = 0,
    val errorMessage: String? = null
)
