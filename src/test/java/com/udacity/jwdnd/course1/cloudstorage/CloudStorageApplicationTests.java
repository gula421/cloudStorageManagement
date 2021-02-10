package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.page.LoginPage;
import com.udacity.jwdnd.course1.cloudstorage.page.SignupPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

	@LocalServerPort
	private int port;
	private String baseUrl;
	final String firstname = "Android";
	final String lastname = "Ha";
	final String username = "hello";
	final String password = "secret";

	private static WebDriver driver;
	private SignupPage signupPage;


	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		driver = new ChromeDriver();
		baseUrl = "http://localhost:" + this.port;
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	@Test
	public void getLoginPage() {
		driver.get(baseUrl + "/login");
		assertEquals("Login", driver.getTitle());
	}

	@Test
	public void errorLoginWithoutSignup(){
		driver.get(baseUrl+"/login");
		LoginPage loginpage = new LoginPage(driver);
		loginpage.login("userA","passwordA");
		assertTrue(loginpage.hasErrorMessage());
	}

	@Test
	public void signupLoginLogout() throws InterruptedException {
		String newUsername = "new"+username;

		// signup
		driver.get(baseUrl+"/signup");
		SignupPage signupPage = new SignupPage(driver);
		signupPage.signupUser(firstname, lastname, newUsername, password);
		assertTrue(signupPage.isSignupSuccessful());

		// go to login page
		assertEquals(baseUrl+"/login", driver.getCurrentUrl());

		// login
		driver.get(baseUrl+"/login");
		LoginPage loginPage = new LoginPage(driver);
		loginPage.login(newUsername, password);
		assertEquals(baseUrl+"/", driver.getCurrentUrl());

		// logout
		driver.get(baseUrl+"/");
		loginPage = new LoginPage(driver);
		loginPage.logout();
		assertEquals(baseUrl+"/login?logout", driver.getCurrentUrl());
		assertTrue(loginPage.hasLogoutMessage());

		// cannot access home anymore, redirect to login
		driver.get(baseUrl+"/");
		assertEquals(baseUrl+"/login",driver.getCurrentUrl());
	}
}
