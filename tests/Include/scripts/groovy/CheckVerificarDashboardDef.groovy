
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


class CheckVerificarDashboardDef {
	/**
	 * The step definitions below match with Katalon sample Gherkin steps
	 */
	@Given("Tenho a aplicação fechada e ja a abri uma vez")
	def I_want_to_start_an_app_and_close_it() {
		Mobile.startApplication('C:\\Projeto_TAES\\app\\build\\outputs\\apk\\debug\\app-debug.apk', false)
		Mobile.closeApplication()
	}

	@When("Abro a aplicação")
	def I_want_to_start_the_app() {
		Mobile.startApplication('C:\\Projeto_TAES\\app\\build\\outputs\\apk\\debug\\app-debug.apk', false)
	}

	@And("Verifico que o  campo {string} existe")
	def I_want_to_see_that_field_exists(String campo) {
		Mobile.verifyElementExist(findTestObject('US1/android.widget.TextView0 - '+campo), 30)
	}

	@Then("Vejo que o valor da qualidade do ar esta correto")
	def I_want_to_see_if_the_values_are_correct() {

		def temperatura = Mobile.getText(findTestObject('android.widget.TextView0 - tempEnviada'), 10)
	
		def humidade = Mobile.getText(findTestObject('android.widget.TextView0 - HumidadeEnviada'), 10)

		String[] partsH = humidade.split('%')

		String[] partsT = temperatura.split('º')

		int humInt = Integer.parseInt(partsH[0])

		int tempInt = Integer.parseInt(partsT[0])

		System.out.println('--------------------------->' + humInt)

		System.out.println('------------------------>' + tempInt)

		if (((humInt > 75) || (humInt < 50)) && ((tempInt > 35) || (tempInt < 19))) {
			Mobile.verifyElementExist(findTestObject('android.widget.TextView0 - Mau'), 30)

			Mobile.closeApplication()

			return true
		}

		if (((tempInt >= 19) && (tempInt <= 35)) && ((humInt >= 50) && (humInt <= 75))) {
			Mobile.verifyElementExist(findTestObject('US1/android.widget.TextView0 - Bom'), 30)

			Mobile.closeApplication()

			return true
		}

		Mobile.verifyElementExist(findTestObject('US1/android.widget.TextView0 - Mdio'), 30)
		Mobile.closeApplication()

	}

	@Then("Vejo que tenho acesso ao meu perfil")
	def I_verify_my_acess_to_my_profile() {
		Mobile.verifyElementExist(findTestObject('US1/android.widget.Button0-Perfil'), 30)
	}



	@Then("Verifico a sala {string}")
	def I_verify_the_room_that_I_get_the_values_from(String sala) {

		Mobile.tap(findTestObject('US1/android.view.ViewGroup0'), 0)

		Mobile.verifyElementExist(findTestObject('US1/android.widget.TextView0 - '+sala), 0)
	}

}