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


class CheckLugaresFavoritosDefs {
	/**
	 * The step definitions below match with Katalon sample Gherkin steps
	 */
	@When("Faço login {string} {string}")
	def EuFacoLogin(String email, String password) {
		sleep(1000)
		Mobile.tap(findTestObject('android.widget.Button0 - PROFILE'), 0)
		try{
			Mobile.tap(findTestObject('android.widget.Button0 - LOGOUT'), 5)
		}
		catch (Exception) {
		}
		Mobile.tap(findTestObject('android.widget.Button0 - LOGIN'), 0)
		Mobile.setText(findTestObject('android.widget.EditText0 - Email'),email, 0)
		Mobile.setText(findTestObject('android.widget.EditText0 - Password'),password, 0)
		Mobile.tap(findTestObject('android.widget.Button0 - LOGIN'), 0)
	}

	@When("Faço logout")
	def EuFacoLogout() {
		Mobile.tap(findTestObject('android.widget.Button0 - PROFILE'), 0)
		Mobile.tap(findTestObject('android.widget.Button0 - LOGOUT'), 0)
		Mobile.verifyElementVisible(findTestObject('android.widget.Button0 - LOGIN'),0)
	}
}