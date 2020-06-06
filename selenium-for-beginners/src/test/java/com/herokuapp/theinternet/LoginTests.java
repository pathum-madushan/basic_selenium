package com.herokuapp.theinternet;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

//in the maven project class name need to consists of keyword "Test or Tests" to understand to maven

public class LoginTests {

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

		// implicit wait will wait for load some WebElement loaded
		// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	}

	@Test(priority = 1, groups = { "positiveTests", "smokeTests" })
	public void positiveLoginTest() {

		System.out.println("starting  successful loginTest");

		String url = "http://the-internet.herokuapp.com/login";
		driver.get(url);
		System.out.println("page is open");

		WebElement username = driver.findElement(By.id("username"));
		username.sendKeys("tomsmith");

		WebElement password = driver.findElement(By.id("password"));
		password.sendKeys("SuperSecretPassword!");

		// setup the explicit wait for loginbtn
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		WebElement loginbtn = driver.findElement(By.tagName("button"));
		// the explicit wait using for loginbtn
		wait.until(ExpectedConditions.elementToBeClickable(loginbtn));// system will wait until loginbtn to be clickable

		loginbtn.click();

		// Verifications

		// check the url is correct
		String expectedUrl = "http://the-internet.herokuapp.com/secure";
		String actualUrl = driver.getCurrentUrl(); // get the current url

		Assert.assertEquals(actualUrl, expectedUrl, "Actual url is not match with the expected url");

		// check log out btn is visible in the page
		WebElement logout = driver.findElement(By.xpath("//a[@class='button secondary radius']"));
		Assert.assertTrue(logout.isDisplayed(), "log out btn is not visible");

		WebElement successmsg = driver.findElement(By.xpath("//div[@id='flash']"));

		// check the success msg is there
		String expectedmessage = "You logged into a secure area!";
		String actualmessage = successmsg.getText(); // get text on the message box
		// Assert.assertEquals(actualmessage, expectedmessage, "Actual msg is not match
		// with the expected msg");
		Assert.assertTrue(actualmessage.contains(expectedmessage),
				"actual is not same as the expected. \n Actual message:+ actualmessage + \n Expected message:"
						+ expectedmessage);

		logout.click();

	}

	// this method required Parameters from LoginTests_param suits therefore need to
	// run that xml file
	// to run correctly this test

	@Parameters({ "username", "password", "expectedErrormessage" })
	@Test(priority = 2, groups = { "negativeTests", "smokeTests" })
	// group anotation type is used to add some group for the paticular test method

	public void negativeLogintest(String username, String password, String expectedErrormessage) {

		// System.out.println("starting Incorrect Username Test ");
		System.out.println("starting NegativeTests with " + username + " and " + password);

		String url = "http://the-internet.herokuapp.com/login";
		driver.get(url);
		System.out.println("page is open");

		WebElement usernameElement = driver.findElement(By.id("username"));
		WebElement passwordElement = driver.findElement(By.id("password"));
		WebElement loginbtn = driver.findElement(By.tagName("button"));

		// set incorrect username & correct password through parameter from
		// NegativeTests_param xml file
		usernameElement.sendKeys(username);
		passwordElement.sendKeys(password);
		loginbtn.click();

		// Verifications

		WebElement errorMessage = driver.findElement(By.id("flash"));

		// check the error message is there
		// String expectedErrormessage = "Your username is invalid!";
		String actualErrormessage = errorMessage.getText(); // get the display text on the message box

		Assert.assertTrue(actualErrormessage.contains(expectedErrormessage),
				"actual Error message not contain expected one. \n Actual: " + actualErrormessage + "\n expected: "
						+ expectedErrormessage);

	}

	@AfterMethod(alwaysRun = true) // this method will be execute after all the method without consoidering any
									// method failure
	private void tearDown() {

		driver.close();
	}

}

