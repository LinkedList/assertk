package test.assertk.assertions

import assertk.assert
import assertk.assertions.*
import java.io.File
import java.nio.file.Files
import kotlin.test.*

class FileTest {

    val file = File.createTempFile("exists", "txt")
    val directory = Files.createTempDirectory("isDirectory").toFile()

    //region exists
    @Test fun exists_with_existing_file_passes() {
        assert(file).exists()
    }

    @Test fun exists_with_nonexistent_file_fails() {
        val tempFile = File.createTempFile("exists", "txt")
        tempFile.delete()
        val error = assertFails {
            assert(tempFile).exists()
        }
        assertEquals("expected to exist", error.message)
    }
    //endregion

    //region isDirectory
    @Test fun isDirectory_value_directory_passes() {
        assert(directory).isDirectory()
    }

    @Test fun isDirectory_value_file_fails() {
        val error = assertFails {
            assert(file).isDirectory()
        }
        assertEquals("expected to be a directory", error.message)
    }
    //endregion

    //region isFile
    @Test fun isFile_value_file_passes() {
        assert(file).isFile()
    }

    @Test fun isFile_value_directory_fails() {
        val error = assertFails {
            assert(directory).isFile()
        }
        assertEquals("expected to be a file", error.message)
    }
    //endregion

    //region isNotHidden
    @Test fun isNotHidden_value_regular_file_passes() {
        assert(file).isNotHidden()
    }
    //endregion

    //region isHidden
    @Test fun isHidden_value_regular_file_fails() {
        val error = assertFails {
            assert(file).isHidden()
        }
        assertEquals("expected to be hidden", error.message)
    }
    //endregion

    //region hasName
    val namedFile = File("assertKt/file.txt")
    val namedDirectory = File("assertKt/directory")

    @Test fun hasName_correct_value_file_passes() {
        assert(namedFile).hasName("file.txt")
    }

    @Test fun hasName_correct_value_directory_pases() {
        assert(namedDirectory).hasName("directory")
    }

    @Test fun hasName_wrong_value_file_fails() {
        val error = assertFails {
            assert(namedFile).hasName("file")
        }
        assertEquals("expected [name]:<\"file[]\"> but was:<\"file[.txt]\"> (assertKt/file.txt)", error.message)
    }

    @Test fun hasName_wront_value_directory_fails() {
        val error = assertFails {
            assert(namedDirectory).hasName("assertKt")
        }
        assertEquals(
            "expected [name]:<\"[assertKt]\"> but was:<\"[${namedDirectory.name}]\"> (assertKt/directory)",
            error.message
        )
    }
    //endregion

    //region hasPath
    val fileWithPath = File("assertKt/file.txt")

    @Test fun hasPath_correct_path_passes() {
        assert(fileWithPath).hasPath("assertKt/file.txt")
    }

    @Test fun hasPath_wrong_path_fails() {
        val error = assertFails {
            assert(fileWithPath).hasPath("/directory")
        }
        assertEquals(
            "expected [path]:<\"[/directory]\"> but was:<\"[${fileWithPath.path}]\"> (assertKt/file.txt)",
            error.message
        )
    }
    //endregion

    //region hasParent
    val fileWithParent = File("assertKt/file.txt")

    @Test fun hasParent_correct_parent_passes() {
        assert(fileWithParent).hasParent("assertKt")
    }

    @Test fun hasParent_wrong_parent_passes() {
        val error = assertFails {
            assert(fileWithParent).hasParent("directory")
        }
        assertEquals(
            "expected [parent]:<\"[directory]\"> but was:<\"[${fileWithParent.parent}]\"> (assertKt/file.txt)",
            error.message
        )
    }
    //endregion

    //region hasExtension
    val fileWithExtension = File("file.txt")

    @Test fun hasExtension_correct_extension_passes() {
        assert(fileWithExtension).hasExtension("txt")
    }

    @Test fun hasExtension_wrong_extension_fails() {
        val error = assertFails {
            assert(fileWithExtension).hasExtension("png")
        }
        assertEquals("expected [extension]:<\"[png]\"> but was:<\"[${fileWithExtension.extension}]\"> (file.txt)", error.message)
    }
    //endregion

    //region hasText
    val fileWithText = File.createTempFile("file_contains", ".txt")
    val text =
        "The Answer to the Great Question... Of Life, the Universe and Everything... Is... Forty-two,' said Deep Thought, with infinite majesty and calm."

    @BeforeTest
    fun setupFileWithText() {
        fileWithText.writeText(text)
    }

    @Test fun hasText_correct_value_passes() {
        assert(fileWithText).hasText(text)
    }

    @Test fun hasText_wrong_value_fails() {
        val error = assertFails {
            assert(fileWithText).hasText("Forty-two!")
        }
        assertTrue(error.message!!.startsWith("expected [text]:<\"[Forty-two!]\"> but was:<\"[$text]\">"))
        assertTrue(error.message!!.contains("file_contains"))
    }
    //endregion

    //region contains
    @Test fun contains_correct_substring_passes() {
        assert(fileWithText).text().contains("Forty-two")
    }

    @Test fun contains_wrong_substring_fails() {
        val error = assertFails {
            assert(fileWithText).text().contains("Forty-two!")
        }
        assertTrue(error.message!!.startsWith("expected [text] to contain:<\"Forty-two!\"> but was:<\"$text\">"))
        assertTrue(error.message!!.contains("file_contains"))
    }
    //endregion

    //region matches
    val matchingFile = File.createTempFile("file_contains", ".txt")
    val matchingText = "Matches"

    @BeforeTest
    fun setupMatchingFile() {
        matchingFile.writeText(matchingText)
    }

    @Test fun matches_correct_regex_passes() {
        assert(matchingFile).text().matches(".a...e.".toRegex())
    }

    @Test fun matches_wrong_regex_fails() {
        val incorrectRegexp = ".*d".toRegex()
        val error = assertFails {
            assert(matchingFile).text().matches(incorrectRegexp)
        }
        assertTrue(error.message!!.startsWith("expected [text] to match:</$incorrectRegexp/> but was:<\"$matchingText\">"))
        assertTrue(error.message!!.contains("file_contains"))
    }
    //endregion

    //region hasDirectChild
    val directoryWithChild = Files.createTempDirectory("isDirectory").toFile()
    val childFile = File.createTempFile("file", ".txt", directoryWithChild)

    @Test fun hasDirectChild_value_is_child_passes() {
        assert(directoryWithChild).hasDirectChild(childFile)
    }

    @Test fun hasDirectChild_value_not_child_fails() {
        val newFile = File.createTempFile("file", ".txt")
        val error = assertFails {
            assert(directoryWithChild).hasDirectChild(newFile)
        }
        assertEquals("expected to have direct child <$newFile>", error.message)
    }

    @Test fun hasDirectChild_empty_directory_fails() {
        directoryWithChild.listFiles().forEach { it.delete() }
        val error = assertFails {
            assert(directory).hasDirectChild(file)
        }
        assertEquals("expected to have direct child <$file>", error.message)
    }
    //endregion
}
