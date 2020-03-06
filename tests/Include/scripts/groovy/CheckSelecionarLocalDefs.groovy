import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.time.LocalDateTime;

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
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When

import org.openqa.selenium.html5.Location;

class CheckSelecionarLocalDefs {
	/**
	 * The step definitions below match with Katalon sample Gherkin steps
	 */
	def timestampDados;
	def timestampRefresh;

	@When("Clico no image button {string}")
	def clicoImgBotao(String nome) {
		Mobile.tap(findTestObject('android.widget.Button0 - '+nome), 0)
	}

	@When("Vejo o text view {string}")
	def vejoTextView(String nome) {
		Mobile.verifyElementExist(findTestObject('android.widget.TextView0 - '+nome), 0)
	}

	@When("Vejo o group view {string}")
	def vejoGroupView(String nome) {
		Mobile.verifyElementExist(findTestObject('android.view.ViewGroup0 - '+nome), 0)
	}


	@When("Nao vejo o text view {string}")
	def naoVejoTextView(String nome) {
		Mobile.verifyElementNotVisible(findTestObject('android.widget.TextView0 - '+nome), 0)
	}

	@When("Vejo o image button {string}")
	def vejoImageButton(String nome) {
		Mobile.verifyElementExist(findTestObject('android.widget.Button0 - '+nome), 0)
	}

	@When("Seleciono o local {string}")
	def selecionoLocal(String nome) {
		Mobile.tap(findTestObject('android.widget.TextView0 - ' + nome), 0)
	}

	@When("Vejo o label {string}")
	def vejoLabel(String nome) {
		def texto = Mobile.getText(findTestObject('android.widget.TextView0 - ESSLei  Edificio A  Sala 1.B'), 0)
		Mobile.verifyEqual(texto, nome)
	}

	@When("Verifico a data dos dados")
	def verifyLastUpdateValue() {
		timestampDados = Mobile.getText(findTestObject('android.widget.TextView0 - Data dos dados'), 0)
	}

	@When("Verifico a data de atualizacao")
	def verifyRefresh() {
		timestampRefresh = Mobile.getText(findTestObject('android.widget.TextView0 - Data Atualizao'), 0)
	}

	@When("Vejo que a data dos dados mudou")
	def verifyTimestampChanged() {
		Mobile.delay(5)
		def newTimestamp = Mobile.getText(findTestObject('android.widget.TextView0 - Data dos dados'), 0)
		println("123****************" + timestampDados + "*******************")
		println("456****************" + newTimestamp + "**************")
		Mobile.verifyNotEqual(timestampDados, newTimestamp)
	}

	@When("Vejo que a data de atualizacao mudou")
	def verifyRefreshTimestampChanged() {
		Mobile.delay(5)
		def newTimestamp = Mobile.getText(findTestObject('android.widget.TextView0 - Data Atualizao'), 0)
		println("123****************" + timestampRefresh + "*******************")
		println("456****************" + newTimestamp + "**************")
		Mobile.verifyNotEqual(timestampRefresh, newTimestamp)
	}

	//sala A.S.1.2B
	@When("Insiro no firebase a temperatura de {string} e humidade {string}")
	def insertValuesIntoFirebase(String temp, String hum) {
		Mobile.delay(5)
		def request = (RequestObject)findTestObject('POST Sensor Data');
		String body = '{ "fields": { "Humidade": { "integerValue": ' + hum + ' }, "Temperatura": { "integerValue": ' + temp + ' }, "Timestamp": { "timestampValue": "' + LocalDateTime.now().toString() + "Z" + '" } } }'
		request.setBodyContent(new HttpTextBodyContent(body, "UTF-8", "application/json"))
		WS.sendRequest(request)
	}

	@When("Clico no botao Dashboard")
	def selecionoLocal() {
		Mobile.tap(findTestObject('android.widget.Button - Dashboard'), 0)
	}

	@When("Clico no botao LOGIN")
	def verifyIfUserIsLoggedinAndclickLogin() {

		try{
			if (Mobile.verifyElementExist(findTestObject('android.widget.Button0 - LOGOUT'), 0)){
				Mobile.tap(findTestObject('android.widget.Button0 - LOGOUT'), 0)
			}
		} catch (Exception e){
			Mobile.tap(findTestObject('android.widget.Button0 - LOGIN'), 0)
		}

		Mobile.tap(findTestObject('android.widget.Button0 - LOGIN'), 0)
	}
}