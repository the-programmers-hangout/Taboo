package com.github.arkencl.taboo.dataclasses

data class FileMetadata(val name: String,
                        val type: String,
                        val typeAlias: String,
                        val isAllowed: Boolean)

data class FileData(val content: String)

data class FileWrapper(val fileMetadata: FileMetadata,
                       val fileData: FileData)