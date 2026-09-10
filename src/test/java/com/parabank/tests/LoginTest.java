package com.parabank.tests;

import com.parabank.base.BaseTest;
import com.parabank.pages.LoginPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest extends BaseTest {

    private static String validUsername;
    private static final String validPassword = "Test@1234";

    private LoginPage loginPage;

    @BeforeAll
    static void registerTestUser() {
        // generate a unique username and SSN so each run creates a fresh account
        long ts = System.currentTimeMillis();
        validUsername = "user" + ts;
        String uniqueSsn = String.valueOf(ts).substring(5); // 8-digit number

        WebDriverManager.chromedriver().browserVersion("151").setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--incognito");
        opts.setExperimentalOption("prefs", Map.of(
            "credentials_enable_service", false,
            "profile.password_manager_enabled", false
        ));

        WebDriver regDriver = new ChromeDriver(opts);
        try {
            regDriver.get("https://parabank.parasoft.com/parabank/register.htm");
            WebDriverWait w = new WebDriverWait(regDriver, Duration.ofSeconds(15));

            // wait for the form to be ready
            w.until(ExpectedConditions.visibilityOfElementLocated(By.id("customer.firstName")));

            regDriver.findElement(By.id("customer.firstName")).sendKeys("Test");
            regDriver.findElement(By.id("customer.lastName")).sendKeys("User");
            regDriver.findElement(By.id("customer.address.street")).sendKeys("123 Main St");
            regDriver.findElement(By.id("customer.address.city")).sendKeys("Testville");
            regDriver.findElement(By.id("customer.address.state")).sendKeys("CA");
            regDriver.findElement(By.id("customer.address.zipCode")).sendKeys("90001");
            regDriver.findElement(By.id("customer.phoneNumber")).sendKeys("5559876543");
            regDriver.findElement(By.id("customer.ssn")).sendKeys(uniqueSsn);
            regDriver.findElement(By.id("customer.username")).sendKeys(validUsername);
            regDriver.findElement(By.id("customer.password")).sendKeys(validPassword);
            regDriver.findElement(By.id("repeatedPassword")).sendKeys(validPassword);

            regDriver.findElement(By.xpath("//input[@value='Register']")).click();

            // wait for result — success shows "Welcome" heading, error shows p.error
            w.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[contains(text(),'Welcome')]")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("p.error"))
            ));

            boolean hasError = !regDriver.findElements(By.cssSelector("p.error")).isEmpty();
            if (hasError) {
                System.out.println("[SETUP] Registration failed: "
                    + regDriver.findElement(By.cssSelector("p.error")).getText());
            } else {
                System.out.println("[SETUP] Registered successfully — username: " + validUsername);
            }

        } catch (Exception e) {
            System.out.println("[SETUP] Registration exception: " + e.getMessage());
        } finally {
            regDriver.quit();
        }
    }

    @BeforeEach
    public void initPage() {
        loginPage = new LoginPage(driver);
    }

    @Test
    @DisplayName("Verify successful login with valid credentials")
    public void testSuccessfulLogin() {
        loginPage.login(validUsername, validPassword);
        String currentUrl = driver.getCurrentUrl();
        String pageTitle  = driver.getTitle();
        System.out.println("[TEST] URL   : " + currentUrl);
        System.out.println("[TEST] Title : " + pageTitle);
        assertTrue(
            !currentUrl.contains("login.htm") && !pageTitle.contains("Error"),
            "Expected successful login. URL=" + currentUrl + " | Title=" + pageTitle
        );
    }

    @Test
    @DisplayName("Verify login fails with invalid password")
    public void testLoginWithInvalidPassword() {
        loginPage.login(validUsername, "wrongpassword");
        assertTrue(loginPage.isErrorDisplayed(), "Expected error message to be shown");
        assertTrue(driver.getCurrentUrl().contains("login.htm"),
            "Expected to remain on login page");
        String error = loginPage.getErrorMessage();
        assertFalse(error.isEmpty(), "Expected non-empty error message");
        System.out.println("[TEST] Error: " + error);
    }

    @Test
    @DisplayName("Verify login fails with empty username and password")
    public void testLoginWithEmptyCredentials() {
        loginPage.login("", "");
        assertTrue(loginPage.isErrorDisplayed(), "Expected error message for empty fields");
        assertTrue(driver.getCurrentUrl().contains("login.htm"),
            "Expected to remain on login page");
        String error = loginPage.getErrorMessage();
        assertFalse(error.isEmpty(), "Expected non-empty error message");
        System.out.println("[TEST] Error: " + error);
    }
}
