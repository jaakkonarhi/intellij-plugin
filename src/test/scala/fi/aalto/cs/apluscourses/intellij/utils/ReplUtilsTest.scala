package fi.aalto.cs.apluscourses.intellij.utils

import java.io.File

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.{OrderEntry, OrderEnumerator}
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.Processor
import fi.aalto.cs.apluscourses.intellij.services.PluginSettings._
import fi.aalto.cs.apluscourses.intellij.utils.ReplUtils._
import org.junit.Assert.{assertEquals, assertFalse, assertTrue}
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}

class ReplUtilsTest extends BasePlatformTestCase {

  @Test
  def testGetModuleDirectory(): Unit = {
    val module = mock(classOf[Module])
    when(module.getModuleFilePath).thenReturn("test/path/module.iml")
    assertEquals("getModuleDirectory returns the correct path", "test/path",
      ReplUtils.getModuleDirectory(module))
  }

  val nullDataContext = new DataContext {
    override def getData(dataId: String) = null
  }

  @Test
  def testGetModuleOfEditorFile(): Unit =
    assertFalse(ReplUtils.getModuleOfEditorFile(mock(classOf[Project]), nullDataContext).isDefined)

  @Test
  def testGetModuleOfSelectedFile(): Unit =
    assertFalse(ReplUtils.getModuleOfEditorFile(mock(classOf[Project]), nullDataContext).isDefined)

  @Test
  def testNonEmptyWithEmptyOrderEnumerator(): Unit = {
    val emptyOrderEnumerator = mock(classOf[OrderEnumerator])
    when(
      emptyOrderEnumerator.forEach(
        any(classOf[Processor[OrderEntry]])
      )
    ).thenAnswer(_ => {})
    assertFalse(ReplUtils.nonEmpty(emptyOrderEnumerator))
  }

  @Test
  def testNonEmptyWithNonEmptyOrderEnumerator(): Unit = {
    val orderEntry = mock(classOf[OrderEntry])
    val orderEnumerator = mock(classOf[OrderEnumerator])
    when(
      orderEnumerator.forEach(
        any(classOf[Processor[OrderEntry]])
      )
    ).thenAnswer(mockInvocation => {
      val processor = mockInvocation.getArgument[Processor[OrderEntry]](0)
      processor.process(orderEntry)
    })
    assertTrue(ReplUtils.nonEmpty(orderEnumerator))
  }

  @Test
  def testGetImportsTextForTwoImportReturnsCorrect(): Unit = {
    val commands = Array("o1", "o1.train")

    assertEquals("Auto-imported packages [o1, o1.train] for your convenience.",
      getCommandsText(commands))
  }

  @Test
  def testGetImportsTextForOneImportReturnsCorrect(): Unit = {
    val commands = Array("o1")

    assertEquals("Auto-imported package [o1] for your convenience.",
      getCommandsText(commands))
  }

  @Test
  def testGetImportsTextForNoneImportReturnsCorrect(): Unit = {
    val commands = Array.empty[String]

    assertEquals("The result is empty.", "", getCommandsText(commands))
  }

  @Test
  def testClearImports(): Unit = {
    val commands = Array("import o1._", "import o1.train._")
    val expectedArray = Array("o1", "o1.train")
    val actualArray = clearCommands(commands)

    assertEquals("", expectedArray(0), actualArray(0))
    assertEquals("", expectedArray(1), actualArray(1))
  }

  @Test
  def testNaiveValidateOne(): Unit = assertTrue(naiveValidate("import o1._"))

  @Test
  def testNaiveValidateTwo(): Unit = assertFalse(naiveValidate("o1._"))

  @Test
  def testNaiveValidateThree(): Unit = assertFalse(naiveValidate("o1"))

  @Test
  def testNaiveValidateFour(): Unit = assertFalse(naiveValidate("println(\"blaaah!\")"))

  @Test
  def testNaiveValidateFive(): Unit = assertTrue(naiveValidate("import o1.train._"))

  @Test
  def testNaiveValidateSix(): Unit = assertFalse(naiveValidate("import o1.train.bus._"))

  @Test
  def testNaiveValidateSeven(): Unit = assertTrue(naiveValidate("import o1.train._"))

  @Test
  def testNaiveValidateEight(): Unit = assertFalse(naiveValidate("import o1.train.bus._"))

  @Test
  def testNaiveValidateNine(): Unit = assertFalse(naiveValidate("import o1."))

  @Test
  def testNaiveValidateTen(): Unit = assertFalse(naiveValidate("import o1"))

  @Test
  def testGetUpdatedText() = {
    val commands = Array("import o1._", "import o1.train._")
    val moduleName = "SampleModule"
    val originalText = "Sample original text"

    val expectedText = "Loaded A+ Courses module [SampleModule]. Auto-imported packages [o1, " +
      "o1.train] for your convenience.\nWrite a line (or more) of Scala and press [Ctrl+Enter] " +
      "to run it. Use [Up] and [Down] to scroll through your earlier inputs. \nChanges to the " +
      "module are not loaded automatically. If you edit the files, restart the REPL with [Ctrl+F5] " +
      "or the icon on the left. \nSample original text"

    assertEquals("", expectedText, getUpdatedText(moduleName, commands, originalText))
  }

  @Test
  def testIgnoreFileInProject(): Unit = {
    val fileName = ".sampleFileToIgnore"
    val expected = FileTypeManager.getInstance().getIgnoredFilesList + fileName + ";"

    ignoreFileInProjectView(fileName, getProject)

    val actual = FileTypeManager.getInstance().getIgnoredFilesList
    assertEquals("The file is successfully added to the ignored files list.",
      expected, actual)
  }

  @Test
  def testGetTheModuleRoot(): Unit = {
    val moduleFilePath = "/tmp/unitTest_setConfigurationFieldsWithValidInputWorks1/" +
      "light_idea_test_case.iml"

    val expected = "/tmp/unitTest_setConfigurationFieldsWithValidInputWorks1/"
    val actual = getTheModuleRoot(moduleFilePath)

    assertEquals("The paths are identical.", expected, actual)
  }

  @Test
  def testInitialReplCommandsFileExistIsFalseForNonExistingFile(): Unit = {
    assertFalse("Returns 'false' if the REPL initial commands file does not exist.",
      initialReplCommandsFileExist(MODULE_REPL_INITIAL_COMMANDS_FILE_NAME,
        getModule.getModuleFilePath))
  }

  @Test
  def testInitialReplCommandsFileExistIsTrueForExistingFile(): Unit = {
    val path = getTheModuleRoot(getModule.getModuleFilePath)
    val file = new File(path, MODULE_REPL_INITIAL_COMMANDS_FILE_NAME)
    println(file.getPath)

    assertTrue("Returns 'true' if the REPL initial commands file exists.",
      initialReplCommandsFileExist(MODULE_REPL_INITIAL_COMMANDS_FILE_NAME,
        getModule.getModuleFilePath))
  }
}
