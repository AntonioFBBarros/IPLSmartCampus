import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords

import internal.GlobalVariable

import MobileBuiltInKeywords as Mobile
import WSBuiltInKeywords as WS
import WebUiBuiltInKeywords as WebUI

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When


class CheckAtualizarDashboardDefs {
	def message;

	@When("Verifico que o header tem o texto {string}")
	def verificarHeader(String texto) {
		Mobile.verifyElementExist(findTestObject('android.widget.TextView0 - ' + texto), 0)
	}

	@When("Vejo o botao atualizar")
	def verificarBotaoAtualizar() {
		Mobile.verifyElementExist(findTestObject('android.widget.ImageButton0'), 0)
	}

	@When("Verifico que vejo o timestamp dos dados")
	def verificarTimestampDados() {
		Mobile.verifyElementExist(findTestObject('android.widget.TextView0 - Data dos Dados'), 0)
	}

	@When("Vejo o timestamp da ultima atualizacao")
	def verifyLastUpdate() {
		message = Mobile.getText(findTestObject('android.widget.TextView0 - Data Atualizao'), 0)
	}

	@When("Clico no botao atualizarDados")
	def clickUpdateData() {
		Mobile.delay(5);
		Mobile.tap(findTestObject('android.widget.ImageButton0'), 0)
		Mobile.delay(5);
	}

	@When("Vejo que o timestamp mudou")
	def verifyTimestampChanged() {
		def newTimestamp = Mobile.getText(findTestObject('android.widget.TextView0 - Data Atualizao'), 0)
		Mobile.verifyNotEqual(message, newTimestamp)
	}
}
