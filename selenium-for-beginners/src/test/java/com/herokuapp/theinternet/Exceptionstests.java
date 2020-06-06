package com.herokuapp.theinternet;

import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

//in the maven project class name need to consists of keyword "Test or Tests" to understand to maven

public class Exceptionstests {

	private WebDriver driver; // driver variable

	@Parameters({ "browser" })
	@BeforeMethod(alwaysRun = true) // this will always run without considering any group priority
	private void setUp(@Optional("chrome") String browser) { // if the pass value for browser get default one as chrome

		// Create driver
		switch (browser) {
		case "chrome":

			System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
			driver = new ChromeDriver();
			break;

		case "firefox":

			System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver.exe");
			driver = new FirefoxDriver();
			break;

		default:
			System.out.println("do not knno how to start " + browser + ", starting chrome instead");
			System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
			driver = new ChromeDriver();
			break;
		}

		driver.manage().window().maximize();

	}

	@Test(priority = 1)
	public void notVisibletest() {
		System.out.println("starting Not Visible Test");

		driver.get("http://the-internet.herokuapp.com/dynamic_loading/1");
		System.out.println("page is open");

		WebElement start_btn = driver.findElement(By.xpath("//button[contains(text(),'Start')]"));

		start_btn.click();

		WebElement hide_element = driver.findElement(By.id("finish"));

		// setup the explicit wait for hide_text
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		// before the get text from actual_text it will wait for hide finish text is
		// visible
		wait.until(ExpectedConditions.visibilityOf(hide_element));

		String actual_text = hide_element.getText();

		// compare actual hide text and expected hide text
		Assert.assertTrue(actual_text.contains("Hello World!"), "actual_text :" + actual_text);

	}

	@Test(priority = 2)
	public void timeoutTest() {
		System.out.println("starting Timeout Test");

		// open test page
		driver.get("http://the-internet.herokuapp.com/dynamic_loading/1");
		System.out.println("page is open");

		WebElement start_btn = driver.findElement(By.xpath("//button[contains(text(),'Start')]"));

		start_btn.click();

		WebElement hide_element = driver.findElement(By.id("finish"));

		// setup the explicit wait for hide_text minimum 2 sec
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

		// before the get text from actual_text it will wait for hide finish text is
		// visible
		try {
			wait.until(ExpectedConditions.visibilityOf(hide_element));
		} catch (TimeoutException exception) {
			System.out.println("Exception catched : " + exception.getMessage());
			sleep(3000); // if timeout exception happen this sleep method will be wait 3 sec & execute
			// if total 5 sec will not enough test will fail
		}
		// if other exception happen other than timeout exception, test will fail

		String actual_text = hide_element.getText();

		// compare actual hide text and expected hide text
		Assert.assertTrue(actual_text.contains("Hello World!"), "actual_text :" + actual_text);

	}

	@Test(priority = 3)
	public void noSuchElementTest() {
		System.out.println("starting No Such Element Test");

		driver.get("http://the-internet.herokuapp.com/dynamic_loading/2");
		System.out.println("page is open");

		WebElement start_btn = driver.findElement(By.xpath("//button[contains(text(),'Start')]"));

		start_btn.click();

		// we need to wait until finish element will visible before the getting element
		// because this time finish element also hide by default
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		// this will wait for test inside the hide element of "finish"
		Assert.assertTrue(
				wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("finish"), "Hello World!")),
				"could'tverify expected text 'Hello World!' ");

//		// wait until finish locator is present & assign it to "hide_element" variable
//		WebElement hide_element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("finish")));
//
//		String actual_text = hide_element.getText();
//
//		// compare actual hide text and expected hide text
//		Assert.assertTrue(actual_text.contains("Hello World!"), "actual_text :" + actual_text);

	}

	@Test(priority = 4)
	private void staleElementTest() {
		System.out.println("starting Timeout Test");

		driver.get("http://the-internet.herokuapp.com/dynamic_controls");
		WebElement checkbox = driver.findElement(By.id("checkbox"));
		WebElement remove_btn = driver.findElement(By.xpath("//button[contains(text(),'Remove')]"));

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		remove_btn.click();

//		// wait for checkbox will invisible on page
//		wait.until(ExpectedConditions.invisibilityOf(checkbox));
//		
//		// make sure checkbox is not display then "assertFalse" condition is true
//		Assert.assertFalse(checkbox.isDisplayed());

		// above two code lines are join together
//		Assert.assertTrue(wait.until(ExpectedConditions.invisibilityOf(checkbox)),
//				"Checkbox is still visible, but shouldn't be");

		// In this type method "stalenessOf" is only look at the staleness of the
		// paticular element
		Assert.assertTrue(wait.until(ExpectedConditions.stalenessOf(checkbox)),
				"Checkbox is still visible, but shouldn't be");

		// get return the web element of checkbox by clicking the "Add" button
		WebElement add_btn = driver.findElement(By.xpath("//button[contains(text(),'Add')]"));
		add_btn.click();

		// we need to assign new checkbox due to not find older checkbox after remove
		// and
		// re add the checkbox
		WebElement checkbox2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checkbox")));
		Assert.assertTrue(checkbox2.isDisplayed(), "Checkbox is not visible, but it should be");
	}

	private void sleep(long m) {
		try {
			Thread.sleep(m);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// this method will be execute after all the method without considering any
	// method failure
	@AfterMethod(alwaysRun = true)
	private void tearDown() {
		// close browser
		driver.close();
	}

}

