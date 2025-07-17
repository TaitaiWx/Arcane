package com.arcane.service

import com.arcane.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.github.junrar.Archive
import com.github.junrar.rarfile.FileHeader
import java.io.File
import java.io.FileOutputStream
import java.util.*

class RarArchiveService : ArchiveService {
    
    override fun canHandle(type: ArchiveType): Boolean {
        return type == ArchiveType.RAR
    }
    
    override suspend fun listEntries(file: File): List<ArchiveEntry> {
        val entries = mutableListOf<ArchiveEntry>()
        
        Archive(file).use { archive ->
            archive.fileHeaders.forEach { header ->
                entries.add(mapRarHeader(header))
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
            
            Archive(file).use { archive ->
                val headers = if (selectedEntries != null) {
                    archive.fileHeaders.filter { selectedEntries.contains(it.fileName) }
                } else {
                    archive.fileHeaders
                }
                
                val totalFiles = headers.size
                var processedFiles = 0
                
                for (header in headers) {
                    emit(ExtractionProgress(
                        status = ExtractionStatus.EXTRACTING,
                        progress = processedFiles.toFloat() / totalFiles,
                        currentFile = header.fileName,
                        totalFiles = totalFiles,
                        processedFiles = processedFiles
                    ))
                    
                    val outputFile = File(outputDir, header.fileName)
                    
                    if (header.isDirectory) {
                        outputFile.mkdirs()
                    } else {
                        outputFile.parentFile?.mkdirs()
                        
                        FileOutputStream(outputFile).use { output ->
                            archive.extractFile(header, output)
                        }
                    }
                    
                    processedFiles++
                }
                
                emit(ExtractionProgress(
                    status = ExtractionStatus.COMPLETED,
                    progress = 1f,
                    totalFiles = totalFiles,
                    processedFiles = processedFiles
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
    
    private fun mapRarHeader(header: FileHeader): ArchiveEntry {
        return ArchiveEntry(
            name = header.fileName.split("/").last(),
            path = header.fileName,
            size = header.fullUnpackSize,
            isDirectory = header.isDirectory,
            lastModified = header.mTime,
            parent = if (header.fileName.contains("/")) {
                header.fileName.substring(0, header.fileName.lastIndexOf("/"))
            } else null
        )
    }
}
