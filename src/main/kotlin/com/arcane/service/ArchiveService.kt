package com.arcane.service

import com.arcane.model.*
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ArchiveService {
    fun canHandle(type: ArchiveType): Boolean
    suspend fun listEntries(file: File): List<ArchiveEntry>
    fun extractArchive(
        file: File,
        outputDir: File,
        selectedEntries: List<String>? = null
    ): Flow<ExtractionProgress>
}

class ArchiveServiceManager {
    private val services = listOf(
        ZipArchiveService(),
        SevenZipArchiveService(),
        RarArchiveService(),
        TarGzArchiveService()
    )
    
    fun getService(type: ArchiveType): ArchiveService? {
        return services.find { it.canHandle(type) }
    }
    
    suspend fun analyzeArchive(file: File): ArchiveInfo? {
        val type = ArchiveDetector.detectArchiveType(file)
        if (type == ArchiveType.UNKNOWN) return null
        
        val service = getService(type) ?: return null
        val entries = service.listEntries(file)
        
        return ArchiveInfo(file, type, entries)
    }
}
