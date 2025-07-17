package com.arcane.service

import com.arcane.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.*

class ZipArchiveService : ArchiveService {
    
    override fun canHandle(type: ArchiveType): Boolean {
        return type == ArchiveType.ZIP
    }
    
    override suspend fun listEntries(file: File): List<ArchiveEntry> {
        val entries = mutableListOf<ArchiveEntry>()
        
        ZipFile(file).use { zipFile ->
            val zipEntries = zipFile.entries()
            
            while (zipEntries.hasMoreElements()) {
                val entry = zipEntries.nextElement()
                entries.add(mapZipEntry(entry))
            }
        }
        
        return entries
    }
    
    override fun extractArchive(
        file: File,
        outputDir: File,
        selectedEntries: List<String>?
    ): Flow<ExtractionProgress> = flow {
        emit(ExtractionProgress(ExtractionStatus.EXTRACTING, 0f))
        
        try {
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            
            ZipFile(file).use { zipFile ->
                val entries = if (selectedEntries != null) {
                    Collections.list(zipFile.entries()).filter { selectedEntries.contains(it.name) }
                } else {
                    Collections.list(zipFile.entries())
                }
                
                val totalEntries = entries.size
                var processedEntries = 0
                
                for (entry in entries) {
                    emit(ExtractionProgress(
                        status = ExtractionStatus.EXTRACTING,
                        progress = processedEntries.toFloat() / totalEntries,
                        currentFile = entry.name,
                        totalFiles = totalEntries,
                        processedFiles = processedEntries
                    ))
                    
                    val outputFile = File(outputDir, entry.name)
                    
                    if (entry.isDirectory) {
                        outputFile.mkdirs()
                    } else {
                        outputFile.parentFile?.mkdirs()
                        
                        zipFile.getInputStream(entry).use { input ->
                            outputFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                    
                    processedEntries++
                }
                
                emit(ExtractionProgress(
                    status = ExtractionStatus.COMPLETED,
                    progress = 1f,
                    totalFiles = totalEntries,
                    processedFiles = processedEntries
                ))
            }
        } catch (e: Exception) {
            emit(ExtractionProgress(
                status = ExtractionStatus.ERROR,
                progress = 0f,
                errorMessage = e.message
            ))
        }
    }
    
    private fun mapZipEntry(entry: ZipEntry): ArchiveEntry {
        return ArchiveEntry(
            name = entry.name.split("/").last(),
            path = entry.name,
            size = entry.size,
            isDirectory = entry.isDirectory,
            lastModified = if (entry.lastModifiedTime != null) Date(entry.lastModifiedTime.toMillis()) else null,
            parent = if (entry.name.contains("/")) {
                entry.name.substring(0, entry.name.lastIndexOf("/"))
            } else null
        )
    }
}
