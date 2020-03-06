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


class CheckNotoficacoesPlataformaDefs {


	@When("Carrego no botao Dashboard")
	def selecionoDashboard() {
		Mobile.tap(findTestObject('android.widget.Button - Dash'), 0)
	}

	@When("Clico no botao ToggleNotifications")
	def toggleNotifications() {
		Mobile.tap(findTestObject('US1/android.widget.Button0 - ToggleNotifications'), 0)
	}

	@When("Carrego no image button {string}")
	def selecionoBtn(String btn) {
		Mobile.tap(findTestObject('android.widget.ImageButton0 - ' + btn),0)
		Mobile.delay(10)
	}

	@When("Verifico que o botao {string} existe")
	def verifiBtnExists(String btn) {
		Mobile.verifyElementExist(findTestObject('android.widget.ImageButton0 - ' + btn),0)
	}

	@When("Vejo que recebi uma notificacao")
	def verifyNotification() {
		Mobile.delay(10)
		Mobile.openNotifications()
		Mobile.verifyElementExist(findTestObject('android.widget.TextView0 - Alert'), 0)
		Mobile.closeNotifications()
	}

	@When("Vejo que nao recebi uma notificacao")
	def verifyNoNotification() {
		Mobile.delay(10)
		Mobile.openNotifications()
		Mobile.verifyElementNotExist(findTestObject('android.widget.TextView0 - Alert'), 0)
		Mobile.closeNotifications()
	}

	@When("Verifico que tenho {string} notificacoes por ver")
	def verifiNumNotificacoes(String num) {
		def numero = Mobile.getText(findTestObject('android.widget.TextView0 - NumNotificacoes'), 0)
		Mobile.verifyEqual(numero, num)
	}

	@When("Aply delay")
	def delayExec() {
		Mobile.delay(10)
	}
}
