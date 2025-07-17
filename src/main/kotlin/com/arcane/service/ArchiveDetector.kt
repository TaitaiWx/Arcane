package com.arcane.service

import com.arcane.model.ArchiveType
import java.io.File

object ArchiveDetector {
    fun detectArchiveType(file: File): ArchiveType {
        val fileName = file.name.lowercase()
        
        return when {
            fileName.endsWith(".zip") -> ArchiveType.ZIP
            fileName.endsWith(".7z") -> ArchiveType.SEVEN_Z
            fileName.endsWith(".rar") -> ArchiveType.RAR
            fileName.endsWith(".tar.gz") || fileName.endsWith(".tgz") -> ArchiveType.TAR_GZ
            fileName.endsWith(".tar") -> ArchiveType.TAR
            fileName.endsWith(".gz") -> ArchiveType.GZIP
            else -> ArchiveType.UNKNOWN
        }
    }
    
    fun isArchiveFile(file: File): Boolean {
        return detectArchiveType(file) != ArchiveType.UNKNOWN
    }
}
